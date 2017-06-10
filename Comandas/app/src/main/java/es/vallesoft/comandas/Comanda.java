package es.vallesoft.comandas;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.IUtil.IComanda;
import es.vallesoft.comandas.IUtil.INota;
import es.vallesoft.comandas.IUtil.ITeclados;
import es.vallesoft.comandas.Util.AdaptadorComanda;
import es.vallesoft.comandas.Util.AdaptadorPedidos;
import es.vallesoft.comandas.Util.LaComanda;
import es.vallesoft.comandas.Util.Nota;
import es.vallesoft.comandas.Util.ServicioCom;
import es.vallesoft.comandas.Util.Teclados;
import es.vallesoft.comandas.db.DbMesas;
import es.vallesoft.comandas.db.DbSubTeclas;
import es.vallesoft.comandas.db.DbTeclas;


public class Comanda extends FragmentActivity implements  INota, IComanda, ITeclados {

    private AdaptadorComanda aComanda;
    private LaComanda comanda = null;
    private Teclados teclados = null;
    private String server = "";

    DbMesas  dbMesas = new DbMesas(this);
    DbTeclas dbTeclas = new DbTeclas(this);
    DbSubTeclas dbSubTeclas = new DbSubTeclas(this);

    Context cx = null;
    JSONObject cam = null;
    JSONObject mesa = null;

    int can =1;
    int tarifa = 1;
    boolean tapas = true;

    String sec = "Bar";

    JSONObject artSel ;
    Nota nota;

    TextView cantidad;
    TextView infPedio;
    ServicioCom myServicio = null;


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



    public  void CargarNota(){
        nota = new Nota(mesa, this,this);
        RellenarComanda();
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


    public void RellenarComanda() {

            List<JSONObject> lPedidos = new ArrayList<JSONObject>();
            comanda.setCantidad("0");

              if(nota.getNum() > 0) {
                String num = Integer.toString(nota.getNum());
                lPedidos = nota.getLineas();
                comanda.setCantidad(num);
                infPedio.setText(num + " articulos");

            } else  infPedio.setText("Ningun articulo");


            comanda.getLista().setAdapter(new AdaptadorPedidos(cx, lPedidos));
            can = 1;
            cantidad.setText(Integer.toString(can));

    }



    public void RellenarBotonera() {

        try {

            JSONArray lsart = dbTeclas.getAll(sec, tarifa);

            if (lsart.length() > 0) {

                LinearLayout ll = (LinearLayout) teclados.getPanel();
                ll.removeAllViews();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                params.weight = 1;


                LinearLayout.LayoutParams rowparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                rowparams.weight= 1;
                rowparams.setMargins(5,5,5,5);


                LinearLayout row = new LinearLayout(cx);
                row.setOrientation(LinearLayout.HORIZONTAL);

                ll.addView(row, params);


                for (int i = 0; i < lsart.length(); i++) {

                    final JSONObject m = lsart.getJSONObject(i);

                    LayoutInflater inflater = (LayoutInflater) cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.btn_art, null);

                    Button btn = (Button) v.findViewById(R.id.boton_art);
                    String[] rgb = m.getString("RGB").split(",");

                    btn.setBackgroundColor(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
                    btn.setId(i);
                    btn.setSingleLine(false);

                    String Nombre = "";

                    if(sec.equals("Tapas")){
                         if(!tapas) btn.setBackgroundColor(Color.RED);
                         else  m.put("Precio","0.00");
                         Nombre = m.getString("Nombre").replace("Tapa", "");
                    }else Nombre = m.getString("Nombre");

                    btn.setText(Nombre.trim());
                    btn.setTag(new JSONObject(m.toString()));


                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject art = (JSONObject) view.getTag();
                            PedirArt(art);
                        }
                    });

                    row.addView(v, rowparams);

                    if ((i<lsart.length()-1) && ((i + 1) % 3) == 0) {
                        row = new LinearLayout(cx);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        ll.addView(row, params);
                    }
                }
            }


        } catch (Exception e) {
           e.printStackTrace();
        }


    }

    private void RellenarSub() {

        try {

            JSONArray lsart = dbSubTeclas.getAll(artSel.getString("ID"));

            if (lsart.length() > 0) {

                LinearLayout ll = (LinearLayout) teclados.getPanel();

                ll.removeAllViews();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                params.weight = 1;

                LinearLayout.LayoutParams rowparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                rowparams.weight = 1;
                rowparams.setMargins(5,5,5,5);

                LinearLayout row = new LinearLayout(cx);
                row.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(row, params);

                for (int i = 0; i < lsart.length(); i++) {

                    JSONObject m = lsart.getJSONObject(i);

                    LayoutInflater inflater = (LayoutInflater)cx.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.btn_art, null);


                    Button btn = (Button)v.findViewById(R.id.boton_art);

                    btn.setId(i);
                    btn.setTag(m);
                    btn.setSingleLine(false);
                    btn.setText(m.getString("Nombre"));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddSug((JSONObject)view.getTag());
                        }
                    });

                    row.addView(v, rowparams);

                    if ((i<lsart.length()-1) && ((i + 1) % 3) == 0) {
                        row = new LinearLayout(cx);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        ll.addView(row, params);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void AddSug(JSONObject sub){

        try {
            Toast toast= Toast.makeText(getApplicationContext(),
                    sub.getString("Nombre"), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();

            String nom = this.artSel.getString("Nombre");
            String sug = sub.getString("Nombre");
            Double precio = this.artSel.getDouble("Precio")+ sub.getDouble("Incremento");
            this.artSel.put("Nombre",nom+" "+sug);
            this.artSel.put("Precio",precio);
            nota.addArt(this.artSel,can);
            RellenarBotonera();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void PedirArt(JSONObject art) {
        try {
            Toast toast= Toast.makeText(getApplicationContext(),
                    art.getString("Nombre"), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();

            if(dbSubTeclas.getCount(art.getString("ID")) == 0){
                nota.addArt(art,can);tapas = true;
            }else{
                this.artSel = art;
                RellenarSub();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void clickMenu(View v) {
        // does something very interesting
        sec = v.getTag().toString();
        RellenarBotonera();
    }

    public void clickCan(View v){
        can = Integer.parseInt(v.getTag().toString());
        cantidad.setText(Integer.toString(can));
    }

    public void clickEnviarComanda(View v){
        try{
          List<NameValuePair> p = new ArrayList<NameValuePair>();
          p.add(new BasicNameValuePair("idm",mesa.getString("ID")));
          p.add(new BasicNameValuePair("pedido",nota.getLineas().toString()));
          p.add(new BasicNameValuePair("idc",cam.getString("ID")));
          if(myServicio!=null){
              myServicio.encolar(p);
              nota.EliminarComanda();
              dbMesas.abrirMesa(mesa.getString("ID"));
              finish();
          }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void OnBorrarLinea(View v){
        nota.rmArt((JSONObject)v.getTag());
    }

    public void clickSugerencia(View v){
       JSONObject art = (JSONObject)v.getTag();
        Intent intent = new Intent(cx, Sugerencias.class);
        intent.putExtra("url", server);
        intent.putExtra("art",nota.getArt(art));
        startActivityForResult(intent, 200);
    }


    public void clickBuscador(View v){
        Intent intent = new Intent(cx, Buscador.class);
        intent.putExtra("url", server);
        startActivityForResult(intent, 100);
    }

    public void clickVarios(View v){
        Intent intent = new Intent(cx, Buscador.class);
        startActivityForResult(intent, 300);
    }

    public void AsociarBotonera(View view) {
        JSON json = new JSON();
        try {
            JSONObject pref = json.deserializar("preferencias.dat", this);
            pref.put("sec",view.getTag().toString());
            json.serializar("preferencias.dat", pref, cx);
            Toast toast= Toast.makeText(getApplicationContext(),
                    "Asocicion realizada", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        cantidad = (TextView) findViewById(R.id.listaPedidos);
        infPedio = (TextView) findViewById(R.id.lblPedido);

        comanda = new LaComanda(this);
        teclados = new Teclados(this);
        aComanda = new AdaptadorComanda(getSupportFragmentManager(), comanda, teclados);

        cx = this;


        ViewPager vpPager = (ViewPager) findViewById(R.id.pager);
        TextView title = (TextView) findViewById(R.id.lblTitulo);

        vpPager.setAdapter(aComanda);

        cargarPreferencias();


        try {

            server = getIntent().getExtras().getString("url");
            cam = new JSONObject(getIntent().getExtras().getString("cam"));
            mesa = new JSONObject(getIntent().getExtras().getString("mesa"));
            title.setText(cam.getString("Nombre") + " " + cam.getString("Apellidos") + " -- " + mesa.getString("Nombre"));
            tarifa = mesa.getInt("Tarifa");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getBaseContext(), ServicioCom.class);
        intent.putExtra("server",server);
        bindService(intent, mConexion, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        nota.EliminarComanda();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if(resultCode == RESULT_OK){
               try{
                JSONObject art = new JSONObject(data.getStringExtra("art"));
                    art.put("Precio", tarifa==1 ? art.getString("P1") : art.getString("P2"));
                    nota.addArt(art, can);
                   } catch (JSONException e) {
                   e.printStackTrace();
               }
            }

        }else if (requestCode == 200) {
            if(resultCode == RESULT_OK){
                try{
                    JSONObject art = new JSONObject(data.getStringExtra("art"));
                    String sug = data.getStringExtra("sug");
                    nota.addSug(sug);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        if(mConexion!=null && myServicio!=null) unbindService(mConexion);
        super.onDestroy();
    }

    @Override
    public void TapaExtra(View view) {
        if(tapas) {
            tapas = false;
        }else tapas = true;
        sec= "Tapas";
        RellenarBotonera();
    }
}