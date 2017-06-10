package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import es.vallesoft.comandas.Util.AdaptadorSugerencias;


public class Sugerencias extends Activity implements TextWatcher {

    String server;
    JSONObject art;
    Context cx;
    TextView txtSug;

    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            Log.e("cagada",res);
            if(op.equals("add")){
                if(res.trim().equals("success")){
                   Intent it = getIntent();
                   it.putExtra("art", art.toString());
                   it.putExtra("sug", txtSug.getText().toString().replace("\n",""));
                   setResult(RESULT_OK, it);
                   finish();
               }
            }else if(op.equals("sug")){
                RellenarSug(res);
            }
        }
    };

    private void RellenarSug(String res) {
        try {

            List<JSONObject> lPedidos = new ArrayList<JSONObject>();


            if(!res.equals("")) {

                JSONArray p = new JSONArray(res);


                for(int i=0; i < p.length(); i++){
                    lPedidos.add(p.getJSONObject(i));
                }


            }

            ListView lst = (ListView)findViewById(R.id.lstSugerencias);
            lst.setAdapter(new AdaptadorSugerencias(cx, lPedidos));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);
        txtSug = (TextView)findViewById(R.id.editText);
        txtSug.addTextChangedListener(this);

        cx = this;
        server = getIntent().getExtras().getString("url");

        try {

            art = new JSONObject(getIntent().getExtras().getString("art"));
            TextView l = (TextView)findViewById(R.id.lblTitulo);
            String titulo = "Sugerencia para " + art.getString("Nombre") ;
            l.setText(titulo);
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("id", art.getString("ID")));
            new HTTPRequest(server+"/sugerencias/ls",p,"sug", Http);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void clickSugerencia(View v){
        Intent it = getIntent();
        it.putExtra("art", art.toString());
        it.putExtra("sug", v.getTag().toString());
        setResult(RESULT_OK, it);
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


      if(charSequence.length()>0) {
          try {

              if (charSequence.toString().contains("\n")) {
                   List<NameValuePair> p = new ArrayList<NameValuePair>();
                  p.add(new BasicNameValuePair("sug", charSequence.toString().replace("\n", "")));
                  p.add(new BasicNameValuePair("idArt", art.getString("ID")));
                  new HTTPRequest(server + "/sugerencias/add", p, "add", Http);
                  txtSug.setVisibility(View.GONE);
              } else if (!charSequence.toString().contains("\n")) {
                  List<NameValuePair> p = new ArrayList<NameValuePair>();
                  p.add(new BasicNameValuePair("id", art.getString("ID")));
                  p.add(new BasicNameValuePair("str", charSequence.toString()));
                  new HTTPRequest(server + "/sugerencias/ls", p, "sug", Http);
              }

          } catch (JSONException e) {
              e.printStackTrace();
          }

      }



    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}
