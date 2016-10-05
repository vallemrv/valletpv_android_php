package es.vallesoft.valletpv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import es.vallesoft.valletpv.Util.JSON;
import es.vallesoft.valletpv.Util.ServicioCom;
import es.vallesoft.valletpv.db.DbAccesos;


public class PreferenciasTPV extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias_tpv);
        final Context cx = this;
        final EditText txt = (EditText)findViewById(R.id.txtUrl);
        Button btn = (Button)findViewById(R.id.btnAceptar);
        JSONObject obj = cargarPreferencias();

        if(obj!=null) try {
            txt.setText(obj.getString("URL"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String url = txt.getText().toString();
                 JSONObject obj = new JSONObject();
                 try {

                     obj.put("URL", url);
                     JSON json = new JSON();
                     json.serializar("preferencias.dat",obj,cx);
                     Toast.makeText(getApplicationContext(),"Datos guardados correctamente",Toast.LENGTH_SHORT).show();

                     VaciarBaseDatos();

                     Intent intent = new Intent(getBaseContext(), ServicioCom.class);
                     intent.putExtra("url",url);
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
