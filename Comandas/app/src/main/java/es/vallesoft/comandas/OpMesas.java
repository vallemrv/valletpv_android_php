package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.db.DbMesas;
import es.vallesoft.comandas.db.DbZonas;


public class OpMesas extends Activity {

    DbZonas dbZonas = new DbZonas(this);
    DbMesas dbMesas = new DbMesas(this);
    String server="";
    String url = "";
    JSONObject mesa ;
    String op ;
    JSONArray lsmesas = null;
    JSONArray lszonas = null;
    JSONObject zn = null;
    JSONObject art = null;
    Context cx;


    private final Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            if(op.equals("salir")){
                finish();
            }

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
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));

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


            lsmesas = dbMesas.getAll(zn.getString("ID"));

            if(lsmesas.length()>0){

                TableLayout ll = (TableLayout)findViewById(R.id.pneMesas);
                ll.removeAllViews();
                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, Math.round(metrics.density * 120));

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
                       btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));

                       btn.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               try {
                                   JSONObject m = (JSONObject)view.getTag();
                                   List<NameValuePair> p = new ArrayList<NameValuePair>();

                                   if(art==null){
                                       p.add(new BasicNameValuePair("idp", mesa.getString("ID")));
                                       p.add(new BasicNameValuePair("ids", m.getString("ID")));
                                       finalizar(m);
                                   } else{
                                       p.add(new BasicNameValuePair("idm", m.getString("ID")));
                                       p.add(new BasicNameValuePair("idLinea", art.getString("ID")));
                                   }
                                   Log.e("cagada", url);
                                   new HTTPRequest(url, p, "salir", handle);

                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }

                           }
                       });
                       row.addView(v, rowparams);

                       if (((i + 1) % 3) == 0) {
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
            }
        }else {
            if(m.getBoolean("abierta")){
                dbMesas.cerrarMesa(m.getString("ID"));
            }else{
                dbMesas.abrirMesa(m.getString("ID"));
            }
        }

        Toast.makeText(getApplicationContext(), "Realizando un cambio en las mesas.....", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_mesas);
        cargarPreferencias();
        cx = this;
        server = getIntent().getExtras().getString("url");
        op = getIntent().getExtras().getString("op");
        try {
           TextView l = (TextView)findViewById(R.id.lblTitulo);
            String titulo = "";
            mesa = new JSONObject(getIntent().getExtras().getString("mesa"));
            url = server + (op.equals("cambiar") ? "/cuenta/cambiarmesas" :"/cuenta/juntarmesas");
           if(op.equals("art")){
               titulo = "Cambiar articulo "+mesa.getString("Nombre");
               art = new JSONObject(getIntent().getExtras().getString("art"));
               url = server+"/cuenta/mvlinea";
           }else{
                titulo =  op.equals("cambiar") ? "Cambiar mesa " + mesa.getString("Nombre") : "Juntar mesa " + mesa.getString("Nombre");
           }
           l.setText(titulo);
           RellenarZonas();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void cargarPreferencias() {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(!pref.isNull("zn")) {
                zn = new JSONObject(pref.getString("zn"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
