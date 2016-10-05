package es.vallesoft.recepcion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import es.vallesoft.util.HTTPRequest;
import es.vallesoft.util.JSON;
import es.vallesoft.util.ServicioCom;


public class Recepcion extends ActionBarActivity {

    String server;
    ServicioCom myServicio;
    JSONArray lineas ;
    TextToSpeech ttobj;
    String zona = "";

    private ServiceConnection mConexion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myServicio = ((ServicioCom.MyBinder)iBinder).getService();
            if(myServicio!=null) RellenarZonas();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myServicio = null;
        }
    };

    private void RellenarZonas(){
        try {
            LinearLayout ll = (LinearLayout) findViewById(R.id.pneZonas);
            ll.removeAllViews();
            ((LinearLayout) findViewById(R.id.pneComanda)).removeAllViews();
            JSONArray nombres = myServicio.getZonas();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

                params.setMargins(5, 0, 5, 0);
                for (int i = 0; i < nombres.length(); i++) {
                   String nombre = nombres.getJSONObject(i).getString("nomZona");
                    if(i==0) RellenarPedido(nombre);
                    Button btn = new Button(this);
                    btn.setId(i);
                    btn.setTag(nombre);
                    btn.setText(nombre);
                    ll.addView(btn, params);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RellenarPedido(v.getTag().toString());
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    private void RellenarPedido(String z) {

        try{
                this.zona = z;
                lineas = myServicio.getPendientes(z);
                LinearLayout ll = (LinearLayout) findViewById(R.id.pneComanda);
                TextView n = (TextView) findViewById(R.id.txtNombre);
                n.setText(myServicio.getNumBebidas(z) + " bebidas para servir");

                ll.removeAllViews();

                if (lineas.length() > 0) {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(5, 0, 5, 0);

                    for (int i = 0; i < lineas.length(); i++) {

                        JSONObject linea = lineas.getJSONObject(i);

                        LayoutInflater inflater = (LayoutInflater) getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);

                        View v = inflater.inflate(R.layout.linea_comanda, null);
                        TextView c = (TextView) v.findViewById(R.id.txtCandidad);
                        TextView d = (TextView) v.findViewById(R.id.txtDescripcion);
                        c.setText(linea.getString("Can"));
                        d.setText(linea.getString("Nombre"));
                        ll.addView(v, params);
                    }
                }



        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion);
        Button btn = (Button) findViewById(R.id.btnServido);
        Button btnr = (Button) findViewById(R.id.btnRecargar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              myServicio.sendServidos(zona);
              RellenarZonas();
            }
        });
        btnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              speakText();
            }
        });
    }

    public void speakText(){
        try {
            for (int i = 0; i < lineas.length(); i++) {
                JSONObject linea = lineas.getJSONObject(i);
                String text = " " + linea.getString("Can");
                text += " " + linea.getString("Nombre");
                ttobj.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttobj.setLanguage(Locale.getDefault());
                }
            }

        });
        cargarPreferencias();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unbindService(mConexion);
        if(ttobj!=null) {
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

    private void cargarPreferencias() {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(pref==null){
                Intent intent = new Intent(this, Preferencias.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
                intent.putExtra("server", server);
                bindService(intent, mConexion, Context.BIND_AUTO_CREATE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
