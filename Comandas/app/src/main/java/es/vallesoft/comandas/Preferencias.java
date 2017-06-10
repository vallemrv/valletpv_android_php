package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.Util.ServicioCom;
import es.vallesoft.comandas.db.DbAccesos;


public class Preferencias extends Activity {

    String server ;
    Button btnDescargar;
    Context cx;

    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if (op.equals("teclados")) {
                try {
                    JSONObject tcl = new JSONObject(res);
                    JSON json = new JSON();
                    json.serializar("teclados.dat",tcl,cx);
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Teclados guardados", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        cx = this;

        final EditText txt = (EditText) findViewById(R.id.txtUrl);
        Button btn = (Button) findViewById(R.id.btnAceptar);
        btnDescargar = (Button) findViewById(R.id.btnCargarTeclados);
        btnDescargar.setVisibility(View.GONE);


        JSONObject obj = cargarPreferencias();

        if (obj != null){
            try {
                txt.setText(obj.getString("URL"));
                server = obj.getString("URL");
            } catch (JSONException e) {
                e.printStackTrace();
            }
         }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = txt.getText().toString();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("URL", url);
                    JSON json = new JSON();
                    json.serializar("preferencias.dat", obj, cx);
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Cambios guardados con exito", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();

                    VaciarBaseDatos();

                    Intent intent = new Intent(getBaseContext(), ServicioCom.class);
                    intent.putExtra("server",url);
                    startService(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private void VaciarBaseDatos(){
        DbAccesos accesos = new DbAccesos(this);
        accesos.Vaciar();
    }

    public  void clickCargarTeclados(View e){
        new HTTPRequest(server+"/comandas/DescargarTeclados",new ArrayList<NameValuePair>(),"teclados",Http);
    }

    private JSONObject cargarPreferencias() {
        JSON json = new JSON();

        try {
            return json.deserializar("preferencias.dat", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }





}
