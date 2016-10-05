package es.vallesoft.recepcion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import es.vallesoft.recepcion.R;
import es.vallesoft.util.JSON;
import es.vallesoft.util.ServicioCom;


public class Preferencias extends Activity {

    String server ;
    Context cx;
    Switch run ;
    EditText txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        cx = this;

        txt = (EditText) findViewById(R.id.txtUrl);
        Button btn = (Button) findViewById(R.id.btnAceptar);
        run = (Switch) findViewById(R.id.switch1);
        run.setVisibility(View.GONE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = txt.getText().toString();
                JSONObject obj = new JSONObject();
                try {
                    server = url;
                    obj.put("URL", url);
                    obj.put("run", true);

                    JSON json = new JSON();
                    json.serializar("preferencias.dat", obj, cx);
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Cambios guardados con exito", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();

                    Intent intent = new Intent(getBaseContext(), ServicioCom.class);
                    intent.putExtra("server",url);
                    startService(intent);
                    run.setChecked(true);
                    run.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
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


    @Override
    protected void onResume() {
        JSONObject obj = cargarPreferencias();

        if (obj != null){
            try {
                txt.setText(obj.getString("URL"));
                server = obj.getString("URL");
                Boolean isRuning = obj.getBoolean("run");
                run.setVisibility(View.VISIBLE);
                run.setChecked(isRuning);if(isRuning) RunService();
                run.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                           RunService();
                        }else{
                            Intent intent = new Intent(cx, ServicioCom.class);
                            stopService(intent);
                        }

                        String url = server;
                        JSONObject obj = new JSONObject();
                        try {

                            obj.put("URL", url);
                            obj.put("run", isChecked);
                            JSON json = new JSON();
                            json.serializar("preferencias.dat", obj, cx);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        super.onResume();
    }

    private void RunService() {
        Intent intent = new Intent(getBaseContext(), ServicioCom.class);
        intent.putExtra("server",server);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Recepcion.class);
        startActivity(intent);
    }
}
