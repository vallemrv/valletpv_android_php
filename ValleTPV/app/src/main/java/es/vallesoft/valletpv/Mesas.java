package es.vallesoft.valletpv;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.vallesoft.valletpv.Util.ServicioCom;
import es.vallesoft.valletpv.db.DbCuenta;
import es.vallesoft.valletpv.db.DbMesas;
import es.vallesoft.valletpv.db.DbZonas;


public class Mesas extends Activity {

    final Context cx = this;
    String server = "";
    int presBack = 0;
    DbZonas dbZonas = new DbZonas(cx);
    DbMesas dbMesas = new DbMesas(cx);
    DbCuenta dbCuenta = new DbCuenta(cx);
    JSONObject cam = null;
    JSONObject zn = null;
    JSONArray lsTicket = null;

    Dialog dlgListadoTicket;
    String IDTicket = "";

    ServicioCom myServicio;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            RellenarMesas();
            super.handleMessage(msg);
        }
    };

    private Handler handlerLsTicket = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if(op.equals("lsticket")){
                try {
                    lsTicket = new JSONArray(res);
                    MostrarListado(lsTicket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(op.equals("ticket")){
                MostrarTicket(res);
            }

        }

    };

    private ServiceConnection mConexion = new ServiceConnection() {
       @Override
       public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           myServicio = ((ServicioCom.MyBinder)iBinder).getService();
           if(myServicio!=null) myServicio.setHandleMesas(handler);
       }

       @Override
       public void onServiceDisconnected(ComponentName componentName) {
          myServicio = null;
       }
    };



    private void MostrarListado(JSONArray ls) {
      try{


        if(ls.length()>0){

            LinearLayout ll = (LinearLayout) dlgListadoTicket.findViewById(R.id.pneListados);

            ll.removeAllViews();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(5,5,5,5);

            for (int i = 0; i < ls.length(); i++) {
                JSONObject  z =  ls.getJSONObject(i);

                LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.linea_cabecera_ticket, null);

                TextView  n = (TextView)v.findViewById(R.id.lblNumTicket);
                TextView  f = (TextView)v.findViewById(R.id.lblHoraFecha);
                TextView  m = (TextView)v.findViewById(R.id.lblNombre);
                TextView  e = (TextView)v.findViewById(R.id.lblEntrega);
                TextView  t = (TextView)v.findViewById(R.id.lblTotal);

                m.setText(z.getString("Mesa"));
                f.setText(z.getString("Fecha")+" - "+z.getString("Hora"));
                e.setText(z.getString("Entrega") + " €");
                t.setText(z.getString("Total")+ " €");
                n.setText(z.getString("ID"));

                ((ImageButton)v.findViewById(R.id.btnVerTicket)).setTag(z.getString("ID"));

                ll.addView(v, params);

            }
        }

      }catch (Exception e){
        e.printStackTrace();
      }

    }

    private  void MostrarTicket(String res){
        try{

            JSONObject ticket = new JSONObject(res);
            JSONArray ls = ticket.getJSONArray("lineas");
            Double Total = ticket.getDouble("total");


            if(ls.length()>0){

                LinearLayout ll = (LinearLayout) dlgListadoTicket.findViewById(R.id.pneListados);

                ll.removeAllViews();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5,5,5,5);

                for (int i = 0; i < ls.length(); i++) {
                    JSONObject  z =  ls.getJSONObject(i);

                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.linea_ticket, null);

                    TextView  c = (TextView)v.findViewById(R.id.lblCan);
                    TextView  p = (TextView)v.findViewById(R.id.lblPrecio);
                    TextView  n = (TextView)v.findViewById(R.id.lblNombre);
                    TextView  t = (TextView)v.findViewById(R.id.lblTotal);

                    c.setText(z.getString("Can"));
                    p.setText(String.format("%01.2f €",z.getDouble("Precio")));
                    n.setText(z.getString("Nombre"));
                    t.setText(String.format("%01.2f €",z.getDouble("Total")));

                    ll.addView(v, params);

                }

                LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.linea_total, null);
                TextView  t = (TextView)v.findViewById(R.id.lblTotalTicket);
                t.setText(String.format("%01.2f €",Total));

                ll.addView(v, params);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void RellenarZonas() {
        try {

             JSONArray lszonas = dbZonas.getAll();

             if(lszonas.length()>0){

                LinearLayout ll = (LinearLayout)findViewById(R.id.pneZonas);
                ll.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,100);

                params.setMargins(5,5,5,5);

                for (int i = 0; i < lszonas.length(); i++) {
                    JSONObject  z =  lszonas.getJSONObject(i);

                    if(zn==null && i==0) zn=z;

                    Button btn = new Button(cx);
                    btn.setId(i);
                    btn.setSingleLine(false);
                    btn.setText(z.getString("Nombre"));
                    btn.setTag(z);
                    String[] rgb = z.getString("RGB").trim().split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          zn = (JSONObject)view.getTag();
                          RellenarMesas();
                        }
                    });
                    ll.addView(btn, params);
                }

                RellenarMesas();

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void RellenarMesas() {
        try {

            JSONArray lsmesas = dbMesas.getAll(zn.getString("ID"));
            if(lsmesas.length()>0){

                TableLayout ll = (TableLayout)findViewById(R.id.pneMesas);
                ll.removeAllViews();

                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);


                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                        160,160);

                rowparams.setMargins(5,5,5,5);
                //rowparams.weight = 1;

                TableRow row = new TableRow(cx);
                ll.addView(row, params);

                for (int i = 0; i < lsmesas.length(); i++) {

                    JSONObject  m =  lsmesas.getJSONObject(i);
                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    View v = inflater.inflate(R.layout.boton_mesa, null);

                    ImageButton btnCm = (ImageButton)v.findViewById(R.id.btnCambiarMesa);
                    ImageButton btnC = (ImageButton)v.findViewById(R.id.btnCobrar);
                    ImageButton btnRm = (ImageButton)v.findViewById(R.id.btnBorrarMesa);
                    LinearLayout panel = (LinearLayout)v.findViewById(R.id.pneBtnMesa);

                    if(!m.getBoolean("abierta")) panel.setVisibility(View.GONE);
                    else inicializarBtnAux(btnC, btnCm, btnRm, m);
                    m.put("Tarifa",zn.getString("Tarifa"));

                    Button btn = (Button)v.findViewById(R.id.btnMesa);

                    btn.setId(i);
                    btn.setText(m.getString("Nombre"));

                    btn.setTag(m);

                    String[] rgb = m.getString("RGB").trim().split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(cx, Cuenta.class);
                                JSONObject obj = (JSONObject)view.getTag();
                                intent.putExtra("op", "m");
                                intent.putExtra("url", server);
                                intent.putExtra("cam", cam.toString());
                                intent.putExtra("mesa", obj.toString());
                                startActivity(intent);
                        }
                    });
                    row.addView(v, rowparams);

                    if (((i+1) % 5) == 0) {
                        row = new TableRow(cx);
                        ll.addView(row, params);
                    }
                }
            }

        }catch (Exception e){
           e.printStackTrace();
        }

    }

    private void inicializarBtnAux(ImageButton btnC, ImageButton btnCm, ImageButton btnRm,   final JSONObject m) {
          btnCm.setTag(m);btnC.setTag(m);btnRm.setTag(m);
          btnCm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickJuntarMesa(view);
                return false;
            }
          });
    }

    public void clickCobrarMesa(View v){
        JSONObject m = (JSONObject)v.getTag();
        Intent intent = new Intent(cx, Cuenta.class);
        intent.putExtra("op", "c");
        intent.putExtra("url", server);
        intent.putExtra("cam", cam.toString());
        intent.putExtra("mesa", m.toString());
        startActivity(intent);
    }

    public void clickAbrirCaja(View v){
       if(myServicio!=null) myServicio.AbrirCajon();
    }

    public  void clickVerTicket(View v){
        IDTicket =  v.getTag().toString();
        ((Button) dlgListadoTicket.findViewById(R.id.btnImprimir)).setVisibility(View.VISIBLE);
        ((Button) dlgListadoTicket.findViewById(R.id.btnListado)).setVisibility(View.VISIBLE);
        if(myServicio!=null){
            myServicio.getTicket(handlerLsTicket, IDTicket);
        }
    }

    public void clickListaTicket(View v){
        dlgListadoTicket = new Dialog(this);
        dlgListadoTicket.setContentView(R.layout.listado_ticket);
        dlgListadoTicket.setTitle("Lista de ticket");
        final Button imp = (Button) dlgListadoTicket.findViewById(R.id.btnImprimir);
        final Button salir = (Button) dlgListadoTicket.findViewById(R.id.btnSalir);
        final Button ls = (Button) dlgListadoTicket.findViewById(R.id.btnListado);
        imp.setVisibility(View.GONE);
        ls.setVisibility(View.GONE);

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlgListadoTicket.cancel();
            }
        });

        ls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imp.setVisibility(View.GONE);
                ls.setVisibility(View.GONE);
                MostrarListado(lsTicket);
            }
        });
        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myServicio!=null) myServicio.imprimirTicket(IDTicket);
                dlgListadoTicket.cancel();
            }
        });
        if(myServicio!=null) myServicio.getLsTicket(handlerLsTicket);
        dlgListadoTicket.show();
    }


    public void clickCambiarMesa(View v){
        Intent intent = new Intent(cx, OpMesas.class);
        intent.putExtra("url", server);
        intent.putExtra("mesa", ((JSONObject)v.getTag()).toString());
        intent.putExtra("op", "cambiar");
        startActivity(intent);
    }

    public void clickJuntarMesa(View v){
        Intent intent = new Intent(cx, OpMesas.class);
        intent.putExtra("url", server);
        intent.putExtra("mesa", ((JSONObject)v.getTag()).toString());
        intent.putExtra("op", "juntar");
        startActivity(intent);
    }


    public void clickBorrarMesa(View v){
        final JSONObject m = (JSONObject)v.getTag();
        final Dialog dlg = new Dialog(cx);
        dlg.setContentView(R.layout.borrar_art);
        dlg.setTitle("Borrar Mesa ");
        final EditText motivo = (EditText)dlg.findViewById(R.id.txtMotivo);
        final Button error = (Button) dlg.findViewById(R.id.btnError);
        final Button simpa = (Button) dlg.findViewById(R.id.btnSimpa);
        final Button inv = (Button) dlg.findViewById(R.id.btnInv);
        final Button ok = (Button) dlg.findViewById(R.id.btnOk);
        final ImageButton edit = (ImageButton) dlg.findViewById(R.id.btnEdit);
        final ImageButton exit = (ImageButton) dlg.findViewById(R.id.btnSalir);

        final LinearLayout pneEdit = (LinearLayout) dlg.findViewById(R.id.pneEditarMotivo);
        pneEdit.setVisibility(View.GONE);

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dlg.cancel();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pneEdit.getVisibility() == View.VISIBLE)  pneEdit.setVisibility(View.GONE);
                    else pneEdit.setVisibility(View.VISIBLE);
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (motivo.getText().length() > 0) {
                        try {
                            String idm = m.getString("ID");
                            List<NameValuePair> p = new ArrayList<NameValuePair>();
                            p.add(new BasicNameValuePair("motivo", motivo.getText().toString()));
                            p.add(new BasicNameValuePair("idm", idm));
                            p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                            if (myServicio != null){
                                dbMesas.cerrarMesa(idm); dbCuenta.eliminar(idm); RellenarMesas();
                                myServicio.rmMesa(p,zn.getString("ID"));
                            }
                            dlg.cancel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String idm = m.getString("ID");
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        p.add(new BasicNameValuePair("motivo", error.getText().toString()));
                        p.add(new BasicNameValuePair("idm",idm));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        if (myServicio != null){
                             dbMesas.cerrarMesa(idm); dbCuenta.eliminar(idm); RellenarMesas();
                             myServicio.rmMesa(p,zn.getString("ID"));
                        }
                        dlg.cancel();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                 }
            });

            simpa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String idm = m.getString("ID");
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        p.add(new BasicNameValuePair("motivo", simpa.getText().toString()));
                        p.add(new BasicNameValuePair("idm", idm));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        if (myServicio != null){
                            dbMesas.cerrarMesa(idm); dbCuenta.eliminar(idm); RellenarMesas();
                            myServicio.rmMesa(p,zn.getString("ID"));
                        }
                        dlg.cancel();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            inv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String idm = m.getString("ID");
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        p.add(new BasicNameValuePair("motivo", inv.getText().toString()));
                        p.add(new BasicNameValuePair("idm", idm));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        if (myServicio != null){
                            dbMesas.cerrarMesa(idm); dbCuenta.eliminar(idm); RellenarMesas();
                            myServicio.rmMesa(p,zn.getString("ID"));
                        }
                        dlg.cancel();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

         dlg.show();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);

        try {

            server = getIntent().getStringExtra("url");
            cam = new JSONObject(getIntent().getStringExtra("cam"));
            TextView title = (TextView)findViewById(R.id.lblTitulo);
            title.setText(cam.getString("Nombre"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(mConexion!=null && myServicio!=null) unbindService(mConexion);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        presBack = 0;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(presBack>=1) {
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(),"Pulsa otra vez para salir", Toast.LENGTH_SHORT).show();
            presBack++;
        }
    }

    @Override
    protected void onResume() {
       Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
       intent.putExtra("url", server);
       bindService(intent, mConexion, Context.BIND_AUTO_CREATE);
       RellenarZonas();
       super.onResume();
    }



}
