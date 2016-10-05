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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.vallesoft.valletpv.Interfaces.IControlador;
import es.vallesoft.valletpv.Util.JSON;
import es.vallesoft.valletpv.Util.ServicioCom;
import es.vallesoft.valletpv.Util.Ticket;
import es.vallesoft.valletpv.db.DbTeclas;
import es.vallesoft.valletpv.db.DbCuenta;
import es.vallesoft.valletpv.db.DbMesas;
import es.vallesoft.valletpv.db.DbSecciones;

import es.vallesoft.valletpv.dlg.DlgCobrar;
import es.vallesoft.valletpv.dlg.DlgSepararTicket;
import es.vallesoft.valletpv.dlg.DlgVarios;


public class Cuenta extends Activity implements TextWatcher, IControlador {


    private String server = "";
    DbSecciones dbSecciones = new DbSecciones(this);
    DbTeclas dbTeclas = new DbTeclas(this);
    DbCuenta dbCuenta = new DbCuenta(this);
    DbMesas dbMesas = new DbMesas(this);

    JSONObject cam = null;
    JSONObject mesa = null;

    JSONArray lineas = null;
    JSONArray lsartresul = null;

    ArrayList<JSONObject> lineasTicket = new ArrayList<JSONObject>();
    Double totalMesa = 0.00;

    String tipo = "";
    String sec = "";
    int cantidad = 1;
    Boolean estoy = true;
    Boolean stop = false;
    Timer timer = new Timer();

    ServicioCom myServicio;

    final Context cx = this;

    private ServiceConnection mConexion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myServicio = ((ServicioCom.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myServicio = null;
        }
    };

    private Handler mostrarBusqueda= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RellenarArticulos(lsartresul);
        }
    };



    private void RellenarTicket() {
        try {

                TextView l = (TextView) findViewById(R.id.lblPrecio);
                ListView lst = (ListView) findViewById(R.id.lstCamareros);
                lineasTicket.clear();

                lineas = dbCuenta.getAll(mesa.getString("ID"));

                totalMesa = dbCuenta.getTotal(mesa.getString("ID"));
                l.setText(String.format("%01.2f €", totalMesa));


                for(int i=0; i < lineas.length(); i++){
                    lineasTicket.add(lineas.getJSONObject(i));
                }

                lst.setAdapter(new Ticket(cx, lineasTicket, this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void RellenarSecciones() {

        try{

            JSONArray lssec = dbSecciones.getAll();


            if(lssec.length()>0){

                LinearLayout ll = (LinearLayout)findViewById(R.id.pneSecciones);
                ll.removeAllViews();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,100);

                params.setMargins(5,5,5,5);


                for (int i = 0; i < lssec.length(); i++) {
                    JSONObject  z =  lssec.getJSONObject(i);

                    if(sec == "" && i==0) sec =  z.getString("ID");

                    Button btn = new Button(cx);
                    btn.setId(z.getInt("ID"));
                    btn.setSingleLine(false);
                    btn.setText(z.getString("Nombre"));
                    btn.setTag(z.getString("ID"));
                    btn.setTextSize(16);
                    String[] rgb = z.getString("RGB").trim().split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            estoy = true;
                            sec =  view.getTag().toString();
                            try {
                                JSONArray  lsart = dbTeclas.getAll(sec,mesa.getInt("Tarifa"));
                                RellenarArticulos(lsart);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btn.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AsociarBotonera(view);
                            return false;
                        }
                    });
                    ll.addView(btn, params);
                }

                JSONArray lsart = dbTeclas.getAll(sec,mesa.getInt("Tarifa"));
                RellenarArticulos(lsart);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void RellenarArticulos(JSONArray lsart) {
        try {


            if(lsart.length()>0){

                TableLayout ll = (TableLayout)findViewById(R.id.pneArt);
                ll.removeAllViews();
                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,130);


                LinearLayout.LayoutParams rowparams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                rowparams.setMargins(5,5,5,5);
                rowparams.weight = 1;

                LinearLayout row = new LinearLayout(cx);
                row.setOrientation(LinearLayout.HORIZONTAL);


                ll.addView(row, params);


                for (int i = 0; i < lsart.length(); i++) {

                    final JSONObject  m =  lsart.getJSONObject(i);


                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.boton_art, null);
                    Button btn = (Button)v.findViewById(R.id.btnArt);

                    btn.setId(i);
                    btn.setTag(m);

                    btn.setText(m.getString("Nombre")+"\n"+String.format("%01.2f €",m.getDouble("Precio")));

                   String[] rgb = m.getString("RGB").split(",");
                   btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           JSONObject art = (JSONObject)view.getTag();
                           PedirArt(art);
                        }
                    });
                    row.addView(v, rowparams);

                    if (((i+1) % 5) == 0) {
                        row = new LinearLayout(cx);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setMinimumHeight(130);

                        ll.addView(row, params);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void PedirArt(JSONObject art) {

        try {
            estoy=true;
            dbCuenta.addArt(cantidad,mesa.getInt("ID"),art);
            cantidad = 1;
            TextView lbl = (TextView)findViewById(R.id.lblCantida);
            lbl.setText("Cantidad "+cantidad);
            RellenarTicket();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    private void cargarPreferencias() {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(!pref.isNull("sec")) {
               sec = pref.getString("sec");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void aparcar(String idm, JSONArray nuevos) throws JSONException {
        if(nuevos.length()>0) {
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("idm", idm));
            p.add(new BasicNameValuePair("idc", cam.getString("ID")));
            p.add(new BasicNameValuePair("pedido", nuevos.toString()));
            if (myServicio != null) {
                myServicio.nuevoPedido(p);
                dbCuenta.aparcar(idm);
                dbMesas.abrirMesa(idm);
            }
        }
    }


    public void AsociarBotonera(View view) {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            pref.put("sec",Integer.toString(view.getId()));
            json.serializar("preferencias.dat", pref, cx);
            Toast toast= Toast.makeText(getApplicationContext(),
                    "Asocicion realizada", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void MostrarVarios(View v) {
        stop = true;
        DlgVarios dlg= new DlgVarios(this, this);
        dlg.show();
    }



    public void MostrarSeparados(View v) {
        // TODO Auto-generated method stub
        if( totalMesa > 0) {
            try {
                stop = true;
                this.aparcar(mesa.getString("ID"), dbCuenta.getNuevos(mesa.getString("ID")));
                lineas = dbCuenta.getAll(mesa.getString("ID"));
                DlgSepararTicket dlg = new DlgSepararTicket(this,this);
                dlg.setTitle("Separar ticket " + mesa.getString("Nombre"));
                dlg.setLineasTicket(lineas);
                dlg.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void PreImprimir(View v){
        try{
            aparcar(mesa.getString("ID"), dbCuenta.getNuevos(mesa.getString("ID")));
            lineas = dbCuenta.getAll(mesa.getString("ID"));

            if(totalMesa>0) {
                    List<NameValuePair> p = new ArrayList<NameValuePair>();
                    p.add(new BasicNameValuePair("idm", mesa.getString("ID")));
                    if(myServicio!=null) myServicio.PreImprimir(p);
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AbrirCajon(View v){
        if(myServicio!=null) myServicio.AbrirCajon();
    }

    public void CobrarMesa(View v){
        try {
            aparcar(mesa.getString("ID"), dbCuenta.getNuevos(mesa.getString("ID")));
            lineas = dbCuenta.getAll(mesa.getString("ID"));
            mostarCobrar(lineas, totalMesa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clickCantidad(View v){
        estoy = true;
        cantidad = Integer.parseInt(((Button) v).getText().toString());
        TextView lbl = (TextView)findViewById(R.id.lblCantida);
        lbl.setText("Cantidad "+cantidad);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        cargarPreferencias();

        EditText bus = (EditText)findViewById(R.id.txtBuscar);
        bus.addTextChangedListener(this);

        try {
           server = getIntent().getExtras().getString("url");
           cam = new JSONObject(getIntent().getExtras().getString("cam"));
           mesa = new JSONObject(getIntent().getExtras().getString("mesa"));
           tipo = getIntent().getExtras().getString("op");
           TextView title = (TextView)findViewById(R.id.txtTitulo);
           title.setText(cam.getString("Nombre") +  " -- "+mesa.getString("Nombre"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {  }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        estoy = true;
        if(charSequence.length()>0) {
            try {
                final String str = charSequence.toString();
                final String t =  mesa.getString("Tarifa");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lsartresul = dbTeclas.getCoincidencia(str, t);
                        mostrarBusqueda.sendEmptyMessage(1);
                     }
                }).start();
             } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    protected void onPause() {
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        try{
            String idm = mesa.getString("ID");
            JSONArray nuevos = dbCuenta.getNuevos(idm);
            if(nuevos.length()>0) {
               aparcar(idm, nuevos);
            }

            ServicioCom.pasa = false;
            DbCuenta.IDMesa = -1;

         }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(timer!=null) {
            timer.cancel();
            timer=null;
        }
        if(mConexion!=null&& myServicio!=null) unbindService(mConexion);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
             ServicioCom.pasa = true;
             if(timer==null) timer = new Timer();
             timer.schedule(new TimerTask()
            {
                @Override
                public void run() {
                    if(!stop) {
                        if (!estoy) finish();
                        else estoy = false;
                    }
                }
            },10000,10000);

            Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
            intent.putExtra("url", server);
            bindService(intent, mConexion, Context.BIND_AUTO_CREATE);

            DbCuenta.IDMesa = mesa.getInt("ID");

            RellenarSecciones();
            RellenarTicket();

            if(tipo.equals("c"))
                mostarCobrar(dbCuenta.getAll(Integer.toString(DbCuenta.IDMesa)), totalMesa);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        super.onResume();
    }

    @Override
    public void salir() {
       estoy=true;stop=false;
    }

    @Override
    public void mostarCobrar(JSONArray lsart, Double totalCobro) {
        if(totalCobro>0) {
            try {
                stop = true;
                DlgCobrar dlg = new DlgCobrar(this, this);
                dlg.setTitle("Cobrar " + mesa.getString("Nombre"));
                dlg.setDatos(lsart, totalCobro);
                dlg.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cobrar(JSONArray lsart, Double totalCobro, Double entrega) {
        try {
            estoy=true; stop=false;
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("idm", mesa.getString("ID")));
            p.add(new BasicNameValuePair("idc", cam.getString("ID")));
            p.add(new BasicNameValuePair("entrega", Double.toString(entrega)));
            p.add(new BasicNameValuePair("art", lsart.toString()));
            dbCuenta.eliminar(mesa.getString("ID"), lsart);
            if(myServicio!=null) {
                myServicio.cobrarCuenta(p);
                if (totalCobro == totalMesa) {
                    dbMesas.cerrarMesa(mesa.getString("ID"));
                    finish();
                } else {
                    RellenarTicket();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pedirArt(JSONObject art, String s) {
        cantidad = Integer.parseInt(s);
        PedirArt(art);
    }

    @Override
    public void clickMostrarBorrar(final JSONObject art) {

            final Dialog dlg = new Dialog(cx);
            dlg.setContentView(R.layout.borrar_art);
            dlg.setTitle("Borrar articulos");
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
                            art.put("Can",1);
                            List<NameValuePair> p = new ArrayList<NameValuePair>();
                            String idm = mesa.getString("ID");
                            p.add(new BasicNameValuePair("idm", idm));
                            p.add(new BasicNameValuePair("Precio", art.getString("Precio")));
                            p.add(new BasicNameValuePair("idArt", art.getString("IDArt")));
                            p.add(new BasicNameValuePair("can", "1"));
                            p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                            p.add(new BasicNameValuePair("motivo", motivo.getText().toString()));
                            p.add(new BasicNameValuePair("Estado", art.getString("Estado")));
                            p.add(new BasicNameValuePair("Nombre", art.getString("Nombre")));
                            if (myServicio != null){
                                myServicio.rmLinea(p);
                                dbCuenta.eliminar(idm, new JSONArray().put(art));
                                if(dbCuenta.getCount(idm)<=0)   dbMesas.cerrarMesa(idm);
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
                        art.put("Can",1);
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        String idm = mesa.getString("ID");
                        p.add(new BasicNameValuePair("idm", idm));
                        p.add(new BasicNameValuePair("Precio", art.getString("Precio")));
                        p.add(new BasicNameValuePair("idArt", art.getString("IDArt")));
                        p.add(new BasicNameValuePair("can", "1"));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        p.add(new BasicNameValuePair("motivo", error.getText().toString()));
                        p.add(new BasicNameValuePair("Estado", art.getString("Estado")));
                        p.add(new BasicNameValuePair("Nombre", art.getString("Nombre")));
                        if (myServicio != null) {
                            myServicio.rmLinea(p);
                            dbCuenta.eliminar(idm, new JSONArray().put(art));
                            if (dbCuenta.getCount(idm) <= 0) dbMesas.cerrarMesa(idm);
                            RellenarTicket();
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
                        art.put("Can",1);
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        String idm = mesa.getString("ID");
                        p.add(new BasicNameValuePair("idm", idm));
                        p.add(new BasicNameValuePair("Precio", art.getString("Precio")));
                        p.add(new BasicNameValuePair("idArt", art.getString("IDArt")));
                        p.add(new BasicNameValuePair("can", "1"));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        p.add(new BasicNameValuePair("motivo", simpa.getText().toString()));
                        p.add(new BasicNameValuePair("Estado", art.getString("Estado")));
                        p.add(new BasicNameValuePair("Nombre", art.getString("Nombre")));
                        if (myServicio != null){
                            myServicio.rmLinea(p);
                            dbCuenta.eliminar(idm, new JSONArray().put(art));
                            if(dbCuenta.getCount(idm)<=0)   dbMesas.cerrarMesa(idm);
                            RellenarTicket();
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
                        art.put("Can",1);
                        List<NameValuePair> p = new ArrayList<NameValuePair>();
                        String idm = mesa.getString("ID");
                        p.add(new BasicNameValuePair("idm", idm));
                        p.add(new BasicNameValuePair("Precio", art.getString("Precio")));
                        p.add(new BasicNameValuePair("idArt", art.getString("IDArt")));
                        p.add(new BasicNameValuePair("can", "1"));
                        p.add(new BasicNameValuePair("idc", cam.getString("ID")));
                        p.add(new BasicNameValuePair("motivo", inv.getText().toString()));
                        p.add(new BasicNameValuePair("Estado", art.getString("Estado")));
                        p.add(new BasicNameValuePair("Nombre", art.getString("Nombre")));
                        if (myServicio != null) {
                            myServicio.rmLinea(p);
                            dbCuenta.eliminar(idm, new JSONArray().put(art));
                            if (dbCuenta.getCount(idm) <= 0) dbMesas.cerrarMesa(idm);
                            RellenarTicket();
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
    public void borrarArticulo(JSONObject art) throws JSONException {
         art.put("Can",1);
         dbCuenta.eliminar(mesa.getString("ID"),new JSONArray().put(art));
         RellenarTicket();
    }
}
