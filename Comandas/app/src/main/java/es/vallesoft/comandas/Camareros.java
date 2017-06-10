package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.Util.ServicioCom;


public class Camareros extends Activity {


    private String server = "";
    JSONArray lscam = null;
    final Context cx = this;


    private final Handler handle = new Handler(){
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            try {

                lscam = new JSONArray(res);

                if(lscam.length()>0){

                    TableLayout ll = (TableLayout)findViewById(R.id.pneCamareros);
                    ll.removeAllViews();
                    TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );

                    DisplayMetrics metrics = getResources().getDisplayMetrics();

                    TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Math.round(metrics.density * 100));

                    rowparams.setMargins(5,5,5,5);
                    TableRow row = new TableRow(cx);
                     ll.addView(row, params);

                    for (int i = 0; i < lscam.length(); i++) {
                        JSONObject  cam =  lscam.getJSONObject(i);
                        Button btn = new Button(cx);
                        btn.setId(i);
                        btn.setSingleLine(false);
                        btn.setTextSize(15);
                        btn.setText(cam.getString("Nombre").trim().replace(" ", "\n") + "\n" + cam.getString("Apellidos").trim().replace(" ", "\n"));

                        btn.setBackgroundColor(Color.rgb(70, 80, 90));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent intent = new Intent(cx, Mesas.class);
                                    intent.putExtra("cam", lscam.getJSONObject(view.getId()).toString());
                                    intent.putExtra("url", server);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                   e.printStackTrace();
                                }
                            }
                        });
                        row.addView(btn, rowparams);

                        if (((i+1) % 3) == 0) {
                            row = new TableRow(cx);
                            ll.addView(row, params);
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camareros);
    }

    private void cargarPreferencias() {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(pref==null){
                Intent intent = new Intent(this, Preferencias.class);
                startActivity(intent);
            }else{
                server = pref.getString("URL");
                new HTTPRequest(server+"/camareros/listado",new ArrayList<NameValuePair>(),"",handle);
                Intent datos = new Intent(getApplicationContext(),ServicioCom.class);
                datos.putExtra("server", server);
                startService(datos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        cargarPreferencias();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Intent datos = new Intent(getApplicationContext(),ServicioCom.class);
        stopService(datos);
        super.onDestroy();
    }
}
