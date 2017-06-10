package es.vallesoft.comandas;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.IUtil.IPedidos;
import es.vallesoft.comandas.Util.AdaptadorMesas;
import es.vallesoft.comandas.Util.ListaMesas;
import es.vallesoft.comandas.Util.Pedidos;
import es.vallesoft.comandas.Util.ServicioCom;
import es.vallesoft.comandas.db.DbMesas;
import es.vallesoft.comandas.db.DbZonas;


public class Mesas extends FragmentActivity implements View.OnLongClickListener, IPedidos {

    private AdaptadorMesas adaptadorMesas;
    private ListaMesas listaMesas = null;
    private Pedidos pedidos = null;

    private String server = "";
    int presBack = 0;
    DbMesas dbMesas = new DbMesas(this);
    DbZonas dbZonas = new DbZonas(this);
    JSONObject cam = null;
    JSONObject zn = null;

    final Context cx = this;
    ServicioCom myServicio;


    private final Handler rellenarZonas = new Handler() {
        public void handleMessage(Message msg) {
           RellenarZonas();
        }
    };

    private final Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            if(op.equals("pedido")){
                RellenarPedido(res);
            }else{
                Toast toast= Toast.makeText(getApplicationContext(),
                        "Peticion enviadaaaa", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
                toast.show();
            }
        }
    };


    private ServiceConnection mConexion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myServicio = ((ServicioCom.MyBinder)iBinder).getService();
            if(myServicio!=null) myServicio.setHandleMesas(rellenarZonas);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myServicio = null;
        }
    };

    private void RellenarPedido(String res) {

        try{
            JSONArray lineas = new JSONArray(res);
            pedidos.vaciarPanel();

            if(lineas.length()>0){


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5,0,5,0);

                 for (int i = 0; i < lineas.length(); i++) {
                   JSONObject  art =  lineas.getJSONObject(i);
                    pedidos.addLinea(art,params,this,this);
                 }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private void RellenarZonas() {
        try {

            JSONArray lszonas = dbZonas.getAll();
            LinearLayout ll = (LinearLayout)findViewById(R.id.pneZonas);
            ll.removeAllViews();


            if(lszonas.length()>0){


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

                params.setMargins(5,0,5,0);
                for (int i = 0; i < lszonas.length(); i++) {
                    JSONObject  z =  lszonas.getJSONObject(i);

                    if(zn==null && i==0) zn=z;

                    Button btn = new Button(cx);
                    btn.setId(i);
                    btn.setSingleLine(false);
                    btn.setTextSize(11);
                    btn.setTag(z);
                    btn.setText(z.getString("Nombre").trim().replace(" ", "\n"));
                    String[] rgb = z.getString("RGB").split(",");
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));
                    btn.setOnLongClickListener(this);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                zn = (JSONObject)view.getTag();
                                RellenarMesas();getPendientes();
                        }
                    });
                    ll.addView(btn, params);
                }

                RellenarMesas();getPendientes();

            }

        }catch (Exception e){
          e.printStackTrace();
        }

    }

    private void RellenarMesas() {
        try {

            JSONArray lsmesas = dbMesas.getAll(zn.getString("ID")) ;
            listaMesas.clearTable();

            if(lsmesas.length()>0){

                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                TableRow.LayoutParams rowparams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, Math.round(metrics.density * 120));

                rowparams.setMargins(5,5,5,5);

                TableRow row = new TableRow(cx);
                listaMesas.addView(row, params);

                for (int i = 0; i < lsmesas.length(); i++) {
                    JSONObject  m =  lsmesas.getJSONObject(i);
                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.boton_mesa, null);

                    ImageButton btnCm = (ImageButton)v.findViewById(R.id.btnCambiarMesa);
                    ImageButton btnC = (ImageButton)v.findViewById(R.id.btnCobrar);
                    ImageButton btnLs = (ImageButton)v.findViewById(R.id.btnLsPedidos);

                    String[] rgb = m.getString("RGB").split(",");
                    LinearLayout pne = (LinearLayout)v.findViewById(R.id.pneBotones);

                    if(!m.getBoolean("abierta")){
                        btnC.setVisibility(View.INVISIBLE); btnCm.setVisibility(View.INVISIBLE);
                        btnLs.setVisibility(View.INVISIBLE);
                        pne.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));
                    }else{
                        inicializarBtnAux(btnC, btnCm, btnLs, m);
                    }
                    Button btn = (Button)v.findViewById(R.id.btnMesa);

                    btn.setId(i);
                    btn.setSingleLine(false);
                    btn.setText(m.getString("Nombre"));
                    btn.setTextSize(15);
                    btn.setTag(m);
                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                JSONObject m = (JSONObject)view.getTag();
                                Intent intent = new Intent(cx, Comanda.class);
                                intent.putExtra("op", "m");
                                intent.putExtra("url", server);
                                intent.putExtra("cam", cam.toString());
                                intent.putExtra("mesa", m.toString());
                                startActivity(intent);
                        }
                    });
                    row.addView(v, rowparams);

                    if (((i+1) % 3) == 0) {
                        row = new TableRow(cx);
                        listaMesas.addView(row, params);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void inicializarBtnAux(ImageButton btnC, ImageButton btnCm, ImageButton btnLs, final JSONObject m) {
         btnC.setTag(m);btnCm.setTag(m);btnLs.setTag(m);
         btnCm.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 ((Mesas)cx).clickJuntarMesa(view);
                 return false;
             }
         });

    }

    private void getPendientes(){
        try {
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("idz",zn.getString("ID")));
            new HTTPRequest(server+"/pedidos/getpendientes",p,"pedido",handle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clickServido(View v){
        try {
            JSONObject obj = (JSONObject) v.getTag();
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("art", obj.toString()));
            p.add(new BasicNameValuePair("idz", zn.getString("ID")));
            new HTTPRequest(server + "/pedidos/servido", p, "pedido", handle);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void MostrarListaTicket(View v) {
       Intent intent = new Intent(cx, Cuenta.class);
        intent.putExtra("url", server);
        intent.putExtra("mesa", ((JSONObject)v.getTag()).toString());
        startActivity(intent);
     }

    public void clickMoverArt(View v){}

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

    public void clickMostrarPedidos(View v){
        Intent intent = new Intent(cx, MostrarPedidos.class);
        intent.putExtra("url", server);
        intent.putExtra("mesa", ((JSONObject)v.getTag()).toString());
        startActivity(intent);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);

        pedidos = new Pedidos();
        listaMesas = new ListaMesas();
        adaptadorMesas = new AdaptadorMesas(getSupportFragmentManager(), listaMesas, pedidos);

        ViewPager vpPager = (ViewPager) findViewById(R.id.pager);

        vpPager.setAdapter(adaptadorMesas);

        try {
            server = getIntent().getExtras().getString("url");
            cam = new JSONObject(getIntent().getExtras().getString("cam"));
            TextView title = (TextView)findViewById(R.id.lblTitulo);
            title.setText(cam.getString("Nombre")+" "+ cam.getString("Apellidos"));
        } catch (Exception e) {
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
    public boolean onLongClick(View view) {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            if(pref.isNull("zn")) {
                pref.put("zn",((JSONObject)view.getTag()).toString());
            }else{
              if(pref.getString("zn").equals(((JSONObject)view.getTag()).toString())){
                  pref.remove("zn");
              }else pref.put("zn",((JSONObject)view.getTag()).toString());

            }
            json.serializar("preferencias.dat", pref, cx);

            Toast toast= Toast.makeText(getApplicationContext(),
                    "Asocicion realizada", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(getApplicationContext(), ServicioCom.class);
        intent.putExtra("server", server);
        bindService(intent, mConexion, Context.BIND_AUTO_CREATE);
        cargarPreferencias();
        RellenarZonas();
        super.onResume();
    }

    @Override
    public void pedir(View v) {
        try{
            JSONObject obj = (JSONObject)v.getTag();
            List<NameValuePair> p = new ArrayList<NameValuePair>();
            p.add(new BasicNameValuePair("idp",obj.getString("IDPedido")));
            p.add(new BasicNameValuePair("id",obj.getString("IDArt")));
            p.add(new BasicNameValuePair("Nombre",obj.getString("Nombre")));
            new HTTPRequest(server+"/impresion/reenviarlinea",p,"",handle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
