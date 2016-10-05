package es.vallesoft.valletpv;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.valletpv.Util.HTTPRequest;
import es.vallesoft.valletpv.Util.ServicioCom;
import es.vallesoft.valletpv.db.DbCuenta;
import es.vallesoft.valletpv.db.DbMesas;
import es.vallesoft.valletpv.db.DbZonas;


public class OpMesas extends Activity {

    DbMesas dbMesas=new DbMesas(this);
    DbZonas dbZonas= new DbZonas(this);
    DbCuenta dbCuenta = new DbCuenta(this);

    ServicioCom servicioCom;

    String server="";
    JSONObject mesa ;
    String op ;
    JSONArray lsmesas = null;
    JSONArray lszonas = null;
    JSONObject zn = null;
    Context cx;

    private ServiceConnection mConexion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            servicioCom = ((ServicioCom.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            servicioCom = null;
        }
    };


    private void RellenarZonas() {
        try {

            lszonas = dbZonas.getAll();


            if(lszonas.length()>0){

                LinearLayout ll = (LinearLayout)findViewById(R.id.pneZonas);
                ll.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

                params.setMargins(5,0,5,0);
                for (int i = 0; i < lszonas.length(); i++) {
                    JSONObject  z =  lszonas.getJSONObject(i);

                    if(zn==null && i==0) zn=z;

                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.btn_art, null);


                    Button btn = (Button)v.findViewById(R.id.boton_art);
                    btn.setId(i);
                    btn.setSingleLine(false);
                    btn.setTextSize(11);
                    btn.setText(z.getString("Nombre").trim().replace(" ", "\n"));
                    String[] rgb = z.getString("RGB").trim().split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                zn = lszonas.getJSONObject((Integer)view.getId());
                                RellenarMesas();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    ll.addView(v, params);


                }

               RellenarMesas();

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void RellenarMesas() {
        try {

            lsmesas = dbMesas.getAllMenosUna(zn.getString("ID"), mesa.getString("ID"));

            if(lsmesas.length()>0){

                TableLayout ll = (TableLayout)findViewById(R.id.pneMesas);
                ll.removeAllViews();
                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);


                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,100);

                rowparams.setMargins(5,5,5,5);

                TableRow row = new TableRow(cx);
                ll.addView(row, params);

                for (int i = 0; i < lsmesas.length(); i++) {
                    JSONObject  m =  lsmesas.getJSONObject(i);

                        LayoutInflater inflater = (LayoutInflater) cx.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);

                        View v = inflater.inflate(R.layout.btn_art, null);


                        Button btn = (Button) v.findViewById(R.id.boton_art);

                        btn.setId(i);
                        btn.setSingleLine(false);
                        btn.setText(m.getString("Nombre"));
                        btn.setTag(m);
                        btn.setTextSize(15);
                        String[] rgb = m.getString("RGB").trim().split(",");
                        btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    JSONObject m = (JSONObject)view.getTag();
                                    List<NameValuePair> p = new ArrayList<NameValuePair>();
                                    p.add(new BasicNameValuePair("idp", mesa.getString("ID")));
                                    p.add(new BasicNameValuePair("ids", m.getString("ID")));
                                    String url = server + (op.equals("cambiar") ? "/cuenta/cambiarmesas" :"/cuenta/juntarmesas");
                                    if(servicioCom!=null){
                                        servicioCom.opMesas(p, url);
                                        finalizar(m);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        row.addView(v, rowparams);

                        if (((i + 1) % 5) == 0) {
                            row = new TableRow(cx);
                            ll.addView(row, params);
                        }
                    }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void finalizar(JSONObject m) throws JSONException {
        if(op.equals("cambiar")){
            if(!m.getBoolean("abierta")){
                dbMesas.abrirMesa(m.getString("ID"));
                dbMesas.cerrarMesa(mesa.getString("ID"));
                dbCuenta.cambiarCuenta(mesa.getString("ID"), m.getString("ID"));
            }else{
                dbCuenta.cambiarCuenta(mesa.getString("ID"), "-100");
                dbCuenta.cambiarCuenta(m.getString("ID"), mesa.getString("ID"));
                dbCuenta.cambiarCuenta("-100", m.getString("ID"));
            }
        }else {
            if(m.getBoolean("abierta")){
                dbMesas.cerrarMesa(m.getString("ID"));
                dbCuenta.cambiarCuenta(m.getString("ID"), mesa.getString("ID"));
            }else{
                dbMesas.abrirMesa(m.getString("ID"));
            }
        }

        Toast.makeText(getApplicationContext(), "Realizando un cambio en las mesas.....", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_mesas);
        cx = this;
        server = getIntent().getExtras().getString("url");
        op = getIntent().getExtras().getString("op");
        try {
            mesa = new JSONObject(getIntent().getExtras().getString("mesa"));
            TextView l = (TextView)findViewById(R.id.lblTitulo);
            String titulo = op.equals("cambiar") ? "Cambiar mesa "+ mesa.getString("Nombre"):"Juntar mesa "+ mesa.getString("Nombre") ;
            l.setText(titulo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RellenarZonas();
    }

    @Override
    protected void onResume() {
        ServicioCom.pasa = false;
        Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
        intent.putExtra("url", server);
        bindService(intent, mConexion, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ServicioCom.pasa = true;
        if(mConexion!=null && servicioCom!=null ) unbindService(mConexion);
        super.onDestroy();
    }
}
