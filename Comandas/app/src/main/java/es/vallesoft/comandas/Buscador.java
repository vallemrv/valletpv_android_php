package es.vallesoft.comandas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.Util.HTTPRequest;


public class Buscador extends Activity implements TextWatcher{

    Context cx;
    String server = "";
    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if (op.equals("art")) {
                RellenaBotonera(res);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        server = getIntent().getExtras().getString("url");

        this.cx = this;
        TextView t = (TextView)findViewById(R.id.txtBuscador);
        t.addTextChangedListener(this);
    }


    private void RellenaBotonera(String res) {

        try {

            JSONArray lsart = new JSONArray(res);

            if (lsart.length() > 0) {

                TableLayout ll = (TableLayout) findViewById(R.id.pneBuscador);
                ll.removeAllViews();
                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);


                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,140);

                rowparams.setMargins(9, 3, 9, 3);

                TableRow row = new TableRow(cx);
                ll.addView(row, params);


                for (int i = 0; i < lsart.length(); i++) {

                    final JSONObject m = lsart.getJSONObject(i);

                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.btn_art, null);


                    Button btn = (Button)v.findViewById(R.id.boton_art);

                    btn.setId(i);
                    btn.setTag(m);
                    btn.setSingleLine(false);
                    btn.setText(m.getString("Nombre").trim().replace(" ","\n"));
                    String[] rgb = m.getString("RGB").split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject art = (JSONObject) view.getTag();
                            Intent it = getIntent();
                            it.putExtra("art", art.toString());
                            setResult(RESULT_OK, it);
                            finish();
                        }
                    });
                    row.addView(v, rowparams);

                    if (((i + 1) % 3) == 0) {
                        row = new TableRow(cx);
                        ll.addView(row, params);
                    }
                }
            }

        } catch (Exception e) {
            Log.e("cuenta-rellenarart", e.getMessage());
        }


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
           List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("str", charSequence.toString()));
            new HTTPRequest(server+"/articulos/listado",p,"art",Http);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
