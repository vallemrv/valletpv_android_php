package es.vallesoft.valletpv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.valletpv.Util.HTTPRequest;
import es.vallesoft.valletpv.Util.JSON;


public class Arqueo extends Activity {

    String server;
    LinearLayout pneGastos;
    LinearLayout pneEfectivo;
    TextView txtCambio;
    TextView txtGastos;
    TextView txtEfectivo;
    Double cambio =0.0, gastos=0.0, efectivo = 0.0;
    List<JSONObject> objGastos = new ArrayList<JSONObject>();
    List<JSONObject> objEfectivo = new ArrayList<JSONObject>();
    Context cx;


    private final Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if (op.equals("cambio")) {
                txtCambio.setText(String.format("%s €", res));
                cambio = Double.parseDouble(res);
            } else if(op.equals("arqueo")){
                if(res.equals("success")) finish();
            }
        }

    };

    private void RellenarGastos() {
        gastos=0.0;
        pneGastos.removeAllViews();

        for (JSONObject gasto : objGastos) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            LayoutInflater inflater = (LayoutInflater) cx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.linea_gastos, null);
            TextView can = (TextView) v.findViewById(R.id.cantidad);
            TextView des = (TextView) v.findViewById(R.id.Descripcion);
            Button rm = (Button)v.findViewById(R.id.btnBorrar);
            rm.setTag(gasto);

            try {
            Double cantidad = gasto.getDouble("Importe");
            String descrip = gasto.getString("Des");

            if (cantidad > 0 && descrip.length() > 0) {
                can.setText(String.format("%.2f €", cantidad));
                des.setText(descrip);
                gastos += cantidad;
                pneGastos.addView(v, params);
              }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        txtGastos.setText(String.format("%.2f €", gastos));

    }

    private void RellenarEfectivo(){

        efectivo = 0.0;
        pneEfectivo.removeAllViews();

        for(JSONObject e : objEfectivo) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            LayoutInflater inflater = (LayoutInflater) cx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.linea_efectivo, null);
            TextView mon = (TextView) v.findViewById(R.id.Moneda);
            TextView can = (TextView) v.findViewById(R.id.Cantidad);
            TextView tot = (TextView) v.findViewById(R.id.Total);
            Button rm = (Button)v.findViewById(R.id.btnBorrar);
            rm.setTag(e);

            try {
                Double moneda = e.getDouble("Moneda");
                int cantidad = e.getInt("Can");
                mon.setText(String.format("%01.2f €", moneda));
                can.setText(String.format("%s", cantidad));
                tot.setText(String.format("%01.2f €", (cantidad * moneda)));
                efectivo += cantidad * moneda;
                pneEfectivo.addView(v, params);
            } catch (JSONException x) {
                x.printStackTrace();
            }
        }
        txtEfectivo.setText(String.format("%.2f €", efectivo));
    }

    public void clickAbrirCaja(View v){
        new HTTPRequest(server+"/impresion/abrircajon",new ArrayList<NameValuePair>(),"open",handle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arqueo);
        CargarPreferencias();
        pneGastos = (LinearLayout)findViewById(R.id.pneGastos);
        pneEfectivo = (LinearLayout)findViewById(R.id.pneEfectivo);
        txtCambio = (TextView)findViewById(R.id.lblCambio);
        txtEfectivo = (TextView)findViewById(R.id.lblEfectivo);
        txtGastos = (TextView)findViewById(R.id.lblGastos);
        this.cx = this;
    }

    public void AddEfectivo(View v){
       final Dialog dlg = new Dialog(this);
       dlg.setContentView(R.layout.add_efectivo);
       dlg.setTitle("Agregar efectivo");
       Button s = (Button) dlg.findViewById(R.id.btnSalir);
       Button ok = (Button) dlg.findViewById(R.id.btnAceptar);
       final TextView m = (TextView) dlg.findViewById(R.id.txtMoneda);
       final TextView c = (TextView) dlg.findViewById(R.id.txtCantidad);
          s.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  dlg.cancel();
              }
          });
          ok.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                try {
                    Double moneda = Double.parseDouble(m.getText().toString().replace(",", "."));
                    int cantidad = Integer.parseInt(c.getText().toString());
                    if ((moneda * cantidad) > 0) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("Can", cantidad);
                            obj.put("Moneda", moneda);
                            objEfectivo.add(obj);
                            RellenarEfectivo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dlg.cancel();
                }catch(Exception exp){
                  exp.printStackTrace();
                }
              }
          });
          dlg.show();
      }

      public void AddGastos(View v){
          final Dialog dlg = new Dialog(this);
          dlg.setContentView(R.layout.add_gastos);
          dlg.setTitle("Agregar gasto");
          Button s = (Button) dlg.findViewById(R.id.Salir);
          Button ok = (Button) dlg.findViewById(R.id.Aceptar);
          final TextView txtDes = (TextView) dlg.findViewById(R.id.txtDescripcion);
          final TextView imp = (TextView) dlg.findViewById(R.id.txtImporte);
          s.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  dlg.cancel();
              }
          });
          ok.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  try {
                      Double Importe = Double.parseDouble(imp.getText().toString().replace(",", "."));
                      String des = txtDes.getText().toString();
                      if(Importe>0 &&  des.length()>0){
                              JSONObject obj = new JSONObject();
                              obj.put("Des",des);
                              obj.put("Importe", Importe);
                              objGastos.add(obj);
                              RellenarGastos();

                      }
                      dlg.cancel();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });
          dlg.show();
      }

      public void EditCambio(View v){
          final Dialog dlg = new Dialog(this);
          dlg.setContentView(R.layout.edit_cambio);
          dlg.setTitle("Editar Cambio");
          Button s = (Button) dlg.findViewById(R.id.salirCambio);
          Button ok = (Button) dlg.findViewById(R.id.aceptarCam);
          final TextView txtDes = (TextView) dlg.findViewById(R.id.cambio);
          s.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  dlg.cancel();
              }
          });
          ok.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                try{
                  cambio = Double.parseDouble(txtDes.getText().toString());
                  txtCambio.setText(String.format("%s €", cambio));
                  dlg.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
              }

          });
         dlg.show();
      }

      public void ArquearCaja(View v){
              List<NameValuePair> p = new ArrayList<NameValuePair>();
              p.add(new BasicNameValuePair("cambio", Double.toString(cambio)));
              p.add(new BasicNameValuePair("efectivo", Double.toString(efectivo )));
              p.add(new BasicNameValuePair("gastos",Double.toString(gastos)));
              p.add(new BasicNameValuePair("des_efectivo",objEfectivo.toString()));
              p.add(new BasicNameValuePair("des_gastos",objGastos.toString()));
              new HTTPRequest(server+"/arqueos/arquear",p,"arqueo",handle);

      }

      public void CargarPreferencias(){
          JSON json = new JSON();
          try {
              JSONObject pref = json.deserializar("preferencias.dat", this);
              if(pref==null){
                  Intent intent = new Intent(this,PreferenciasTPV.class);
                  startActivity(intent);
              }else{
                  server = pref.getString("URL");
                  new HTTPRequest(server+"/arqueos/getcambio",new ArrayList<NameValuePair>(),"cambio",handle);
              }

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

    public void clickBorrarEfc(View v){
        JSONObject obj = (JSONObject)v.getTag();
        objEfectivo.remove(obj);
        RellenarEfectivo();
    }

    public void clickBorrarGasto(View v){
        JSONObject obj = (JSONObject)v.getTag();
        objGastos.remove(obj);
        RellenarGastos();
    }

}
