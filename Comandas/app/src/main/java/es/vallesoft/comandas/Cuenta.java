package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.comandas.Util.Ticket;


public class Cuenta extends Activity {

    String totalMesa;
    String server = "";
    JSONObject mesa;
    ArrayList<JSONObject> lineasTicket = new ArrayList<JSONObject>();
    Context cx;


    private final Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if(op.equals("ticket")){
                RellenarTicket(res);
            }else if(op.equals("salir")){
                finish();
            }
         }
    };

    private void RellenarTicket(String res) {
        try {

            if(!res.equals("")) {

                TextView l = (TextView) findViewById(R.id.txtTotal);
                ListView lst = (ListView) findViewById(R.id.lstCuenta);
                lineasTicket.clear();

                JSONObject ticket = new JSONObject(res);
                JSONArray lineas = ticket.getJSONArray("lineas");

                totalMesa = String.format("%.2f", ticket.getDouble("total"));
                l.setText(String.format("%s €", totalMesa));


                for(int i=0; i < lineas.length(); i++){
                    lineasTicket.add(lineas.getJSONObject(i));
                }
                lst.setAdapter(new Ticket(cx, lineasTicket));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        TextView lbl = (TextView)findViewById(R.id.lblMesa);
        this.cx = this;
        try {
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            server = getIntent().getExtras().getString("url");
            mesa = new JSONObject(getIntent().getExtras().getString("mesa"));
            lbl.setText("Mesa "+mesa.getString("Nombre"));
            p.add(new BasicNameValuePair("idm",mesa.getString("ID")));
            new HTTPRequest(server+"/cuenta/ticket",p,"ticket",handle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clickImprimir(View v){
        if(Double.parseDouble(totalMesa.replace(",","."))>0) {
            try {
                List<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("idm", mesa.getString("ID")));
                new HTTPRequest(server + "/impresion/preimprimir", p, "salir", handle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickSalir(View v){
        finish();
    }
}
