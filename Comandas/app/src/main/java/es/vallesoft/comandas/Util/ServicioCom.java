package es.vallesoft.comandas.Util;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import es.vallesoft.Util.HTTPRequest;
import es.vallesoft.comandas.R;
import es.vallesoft.comandas.db.DbAccesos;
import es.vallesoft.comandas.db.DbMesas;
import es.vallesoft.comandas.db.DbSubTeclas;
import es.vallesoft.comandas.db.DbTeclas;
import es.vallesoft.comandas.db.DbZonas;

public class ServicioCom extends Service {

    final IBinder myBinder = new MyBinder();
    DbTeclas dbTeclas;
    DbMesas dbMesas;
    DbAccesos dbAccesos;
    DbSubTeclas dbSubTeclas;
    DbZonas dbZonas;

    String server = null;
    Queue<List<NameValuePair>> cola = new LinkedList<List<NameValuePair>>();
    boolean enviado = true;
    boolean error = false;



    private Handler EventMesas;

    Timer timerUpdate = new Timer();
    Timer timer = new Timer();


    NotificationManager notificationManager;

    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String op = msg.getData().getString("op");
            String res = msg.getData().getString("RESPONSE").toString();
            Log.d("cagada",res);
            try {
                if(!res.contains("hora") && res.length()>0) Log.e("cagada", res);
                if(op.length()>0){
                    if (op.equals("zonas"))      dbZonas.RellenarTabla(new JSONArray(res));
                    else if (op.equals("mesas"))     dbMesas.RellenarTabla(new JSONArray(res));
                    else if (op.equals("sub"))        dbSubTeclas.RellenarTabla(new JSONArray(res));
                    else if (op.equals("art"))        dbTeclas.RellenarTabla(new JSONArray(res));

                    else if (op.equals("sync")){
                        JSONObject aux = new JSONObject(res);
                        dbAccesos.addAcceso(aux.getString("hora"));
                        JSONArray tb = aux.getJSONArray("Tablas");
                        if(tb.length()>0){
                            for(int i=0;i<tb.length();i++){
                               String tabla = tb.getJSONObject(i).getString("Tabla");

                               if(tabla.equals("Zonas")){
                                   new HTTPRequest(server + "/mesas/lszonas", new ArrayList<NameValuePair>() , "zonas", Http);
                                   new HTTPRequest(server + "/mesas/lstodaslasmesas", new ArrayList<NameValuePair>() , "mesas", Http);
                               }

                               if(tabla.equals("SubTeclas")){
                                   new HTTPRequest(server+"/comandas/lssubteclas",new ArrayList<NameValuePair>(),"sub", Http);
                               }

                                if(tabla.equals("TeclasCom")){
                                    new HTTPRequest(server+"/comandas/lsAll",new ArrayList<NameValuePair>(),"art", Http);
                                }

                               if(tabla.equals("MesasAbiertas")){
                                   new HTTPRequest(server + "/mesas/lsmesasabiertas", new ArrayList<NameValuePair>() , "m", Http);
                               }
                            }
                        }


                    } else if (op.equals("m")) {
                       JSONArray datos = new JSONArray(res);
                       dbMesas.update(datos);
                       if(EventMesas!=null) EventMesas.sendEmptyMessage(0);
                    }else if (op.equals("pedir")){
                        if(res.trim().equals("success")) {
                            enviado = true;error=false;
                        }else if(res.equals("error")){
                            error=true;
                        }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private void IniciarDB() {
      if(dbTeclas==null)  dbTeclas = new DbTeclas(getApplicationContext());
      if(dbZonas==null)  dbZonas = new DbZonas(getApplicationContext());
      if(dbAccesos==null)  dbAccesos = new DbAccesos(getApplicationContext());
      if(dbMesas==null)  dbMesas = new DbMesas(getApplicationContext());
      if(dbSubTeclas==null)  dbSubTeclas = new DbSubTeclas(getApplicationContext());
    }





    public ServicioCom() {  }



    public void setHandleMesas(Handler handler) {
        this.EventMesas = handler;
        this.EventMesas.sendEmptyMessage(0);
    }


    public void encolar(List<NameValuePair> p) {
        cola.add(p);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        server = intent.getStringExtra("server");
        IniciarDB();

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("ValleCom ejecutandose!!");
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
                if (server != null) {
                    List<NameValuePair> p = new ArrayList<NameValuePair>();
                    p.add(new BasicNameValuePair("hora", dbAccesos.getUltimoAcceso()));
                    new HTTPRequest(server + "/sync/getupdate", p, "sync", Http);
                }
            }
        }, 0, 5000);

        timer.schedule(new TimerTask() {
            List<NameValuePair> p = null;
            @Override
            public void run() {
                  if((enviado && cola.size()>0) || error) {
                     if(!error) p = cola.remove();
                     error=false; enviado = false;
                     new HTTPRequest(server + "/comandas/pedir", p, "pedir", Http);
                }
            }
        },0,1000);
    }

    @Override
    public void onDestroy() {
        if(timerUpdate!=null) timerUpdate.cancel();
        if(timer!=null) timer.cancel();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;

    }



    public class MyBinder extends Binder{
       public ServicioCom getService() {
            return ServicioCom.this;
       }
    }

}
