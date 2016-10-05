package es.vallesoft.valletpv.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.vallesoft.valletpv.R;
import es.vallesoft.valletpv.db.DbAccesos;
import es.vallesoft.valletpv.db.DbCamareros;
import es.vallesoft.valletpv.db.DbCuenta;
import es.vallesoft.valletpv.db.DbMesas;
import es.vallesoft.valletpv.db.DbSecciones;
import es.vallesoft.valletpv.db.DbTeclas;
import es.vallesoft.valletpv.db.DbZonas;

public class ServicioCom extends Service {

    public static boolean pasa = false;

    final IBinder myBinder = new MyBinder();
    DbCamareros dbCamareros ;
    DbZonas dbZonas;
    DbMesas dbMesas;
    DbSecciones dbSecciones;
    DbTeclas dbTeclas;
    DbAccesos dbAccesos;
    DbCuenta dbCuenta;

    String server = null;

    private Handler EventMesas;

    Timer timerUpdate = new Timer();


    NotificationManager notificationManager;

    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            try {
                if(op.length()>0){
                    if      (op.equals("cam")){
                        synchronized (dbCamareros) {
                            dbCamareros.RellenarTabla(new JSONArray(res));
                        }
                    }
                    else if (op.equals("zonas")){
                        synchronized (dbCamareros) {
                            dbZonas.RellenarTabla(new JSONArray(res));
                        }
                    }
                    else if (op.equals("mesas")){
                        synchronized (dbMesas){
                            dbMesas.RellenarTabla(new JSONArray(res));
                        }
                    }
                    else if (op.equals("sec")){
                        synchronized (dbSecciones){
                            dbSecciones.RellenarTabla(new JSONArray(res));
                        }
                    }
                    else if (op.equals("art")){
                        synchronized (dbTeclas){
                            dbTeclas.RellenarTabla(new JSONArray(res));
                        }
                    }
                    else if (op.equals("cuenta")) {
                        synchronized (dbCuenta){
                            dbCuenta.RellenarTabla(new JSONArray(res));
                        }
                    }

                    else if (op.equals("sync") && !ServicioCom.pasa){
                        JSONObject aux = new JSONObject(res);
                        synchronized (dbAccesos){
                            dbAccesos.addAcceso(aux.getString("hora"));
                        }
                        JSONArray tb = aux.getJSONArray("Tablas");
                        if(tb.length()>0){
                            for(int i=0;i<tb.length();i++){
                               String tabla = tb.getJSONObject(i).getString("Tabla");
                                if(tabla.equals("Camareros")){
                                   new HTTPRequest(server + "/camareros/listado", new ArrayList<NameValuePair>() , "cam", Http);
                                }

                               if(tabla.equals("Zonas")){
                                   new HTTPRequest(server + "/mesas/lszonas", new ArrayList<NameValuePair>() , "zonas", Http);
                                   new HTTPRequest(server + "/mesas/lstodaslasmesas", new ArrayList<NameValuePair>() , "mesas", Http);
                               }

                               if(tabla.equals("Secciones")){
                                   new HTTPRequest(server+"/secciones/listado",new ArrayList<NameValuePair>(),"sec", Http);
                                   new HTTPRequest(server+"/articulos/lstodos",new ArrayList<NameValuePair>(),"art", Http);
                               }

                               if(tabla.equals("MesasAbiertas")){
                                   new HTTPRequest(server + "/mesas/lsmesasabiertas", new ArrayList<NameValuePair>() , "m", Http);
                                   new HTTPRequest(server + "/cuenta/lsaparcadas", new ArrayList<NameValuePair>() , "cuenta", Http);
                               }
                            }
                        }


                    } else if (op.equals("m")) {
                       synchronized (dbMesas) {
                           JSONArray datos = new JSONArray(res);
                           dbMesas.update(datos);
                           if (EventMesas != null) EventMesas.sendEmptyMessage(0);
                       }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private void IniciarDB() {
      if(dbSecciones==null)  dbSecciones = new DbSecciones(getApplicationContext());
      if(dbTeclas==null)  dbTeclas = new DbTeclas(getApplicationContext());
      if(dbZonas==null)  dbZonas = new DbZonas(getApplicationContext());
      if(dbAccesos==null)  dbAccesos = new DbAccesos(getApplicationContext());
      if(dbCamareros==null)  dbCamareros = new DbCamareros(getApplicationContext());
      if(dbMesas==null)  dbMesas = new DbMesas(getApplicationContext());
      if(dbCuenta==null)  dbCuenta = new DbCuenta(getApplicationContext());
    }





    public ServicioCom() {  }


    public void AbrirCajon() {
        if(server!=null) new HTTPRequest(server + "/impresion/abrircajon", new ArrayList<NameValuePair>(), "", Http);
    }



    public void getTicket(Handler mostrarLsTicket, String IDTicket) {
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new BasicNameValuePair("id", IDTicket));
        new HTTPRequest(server + "/cuenta/lslineas", p, "ticket", mostrarLsTicket);
    }

    public void getLsTicket(Handler hLsTicket) {
        new HTTPRequest(server+"/cuenta/lsticket",new ArrayList<NameValuePair>(),"lsticket", hLsTicket);
    }

    public void imprimirTicket(String idTicket) {
          Log.e("cagada", "imprimirTicket");
          List<NameValuePair> p = new ArrayList<NameValuePair>();
          p.add(new BasicNameValuePair("id", idTicket));
          new HTTPRequest(server + "/impresion/ticket", p, "", Http);
    }

    public void rmMesa(List<NameValuePair> p, String IDZona) {
        new HTTPRequest(server+"/cuenta/rm", p ,"", Http);
    }

    public void nuevoPedido(List<NameValuePair> obj)
    {
        new HTTPRequest(server+"/cuenta/add", obj ,"", Http);
    }

    public void cobrarCuenta(List<NameValuePair> obj)
    {
        new HTTPRequest(server+"/cuenta/cobrar", obj ,"", Http);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        server = intent.getStringExtra("url");
        IniciarDB();

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Valletpv ejecutandose!!");
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);

        Notification notification = builder.build();

        startForeground(1, notification);
        return START_STICKY;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        IniciarDB();
        timerUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                if (server != null && !ServicioCom.pasa) {
                    List<NameValuePair> p = new ArrayList<NameValuePair>();
                    p.add(new BasicNameValuePair("hora", dbAccesos.getUltimoAcceso()));
                    new HTTPRequest(server + "/sync/getupdate", p, "sync", Http);
                }
            }
        }, 0, 10000);
    }

    @Override
    public void onDestroy() {
        if(timerUpdate!=null) timerUpdate.cancel();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;

    }

    public void setHandleMesas(Handler handler) {
        this.EventMesas = handler;
        this.EventMesas.sendEmptyMessage(0);
    }

    public void PreImprimir(final List<NameValuePair> p) {
        Thread t = new Thread(){
           public void run(){
               try {
                 sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               new HTTPRequest(server + "/impresion/preimprimir", p, "", Http);
           }
        };
        t.start();
    }

    public void opMesas(List<NameValuePair> p, String url) {
        new HTTPRequest(url, p, "", Http);
    }

    public void rmLinea(List<NameValuePair> p) {
        new HTTPRequest(server+"/cuenta/rmlinea", p ,"", Http);
    }


    public class MyBinder extends Binder{
       public ServicioCom getService() {
            return ServicioCom.this;
       }
    }

}
