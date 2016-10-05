package es.vallesoft.valletpv;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.vallesoft.valletpv.Util.JSON;
import es.vallesoft.valletpv.Util.ServicioCom;
import es.vallesoft.valletpv.db.DbCamareros;


public class Camareros extends Activity {

    private String server = "";
    JSONArray lscam = null;
    final Context cx = this;
    DbCamareros dbCamareros = new DbCamareros(cx);


     private void RellenarCamareros() {


               try {

                  if(lscam.length()>0){

                      TableLayout ll = (TableLayout)findViewById(R.id.pneCamareros);
                      ll.removeAllViews();

                      TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                              TableLayout.LayoutParams.MATCH_PARENT,
                              160);


                      TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                              TableLayout.LayoutParams.MATCH_PARENT,
                              TableLayout.LayoutParams.MATCH_PARENT);

                      rowparams.setMargins(5,5,5,5);
                      ll.setStretchAllColumns(true);

                      TableRow row = new TableRow(cx);

                      ll.addView(row, params);


                      for (int i = 0; i < lscam.length(); i++) {
                          JSONObject  cam =  lscam.getJSONObject(i);
                          Button btn = new Button(cx);
                          btn.setId(i);
                          btn.setSingleLine(false);
                          String[] nom = cam.getString("Nombre").split(" ");

                          btn.setText(nom.length > 1 ? nom[0]+"\n"+nom[1] : cam.getString("Nombre"));
                          btn.setBackgroundResource(R.drawable.blancoxml);
                          btn.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View view) {
                                  try {
                                      Intent  intent = new Intent(cx, Mesas.class);
                                      intent.putExtra("url", server);
                                      intent.putExtra("cam", lscam.getJSONObject(view.getId()).toString());
                                      startActivity(intent);
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }

                              }
                          });
                          row.addView(btn, rowparams);

                          if (((i+1) % 6) == 0) {
                              row = new TableRow(cx);
                              ll.addView(row, params);
                          }
                    }
                  }

              }catch (Exception e){
                  e.printStackTrace();
              }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valle_tpv);
    }

    private void cargarPreferencias() {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(pref==null){
                Intent intent = new Intent(this,PreferenciasTPV.class);
                startActivity(intent);
            }else{
              server = pref.getString("URL");
              lscam = dbCamareros.getAll();
              RellenarCamareros();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(cx, ServicioCom.class);
        stopService(intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPreferencias();
        Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
        intent.putExtra("url", server);
        startService(intent);

    }


}
