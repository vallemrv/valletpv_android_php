package es.vallesoft.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.vallesoft.DB.DbPendientes;
import es.vallesoft.recepcion.R;
import es.vallesoft.recepcion.Recepcion;


public class ServicioCom extends Service {

    final IBinder myBinder = new MyBinder();

    int IDLinea = 0;
    int Nuevas = 0;
    int IDPedido = 0;

    String server = null;
    Timer timerUpdate = new Timer();
    DbPendientes dbPendientes;

    private final Handler Http = new Handler() {
        public void handleMessage(Message msg) {
            String res = msg.getData().getString("RESPONSE").toString();
            String op = msg.getData().getString("op").toString();
            if(op.equals("P")){
                synchronized (dbPendientes) {
                   Notificar(res);
                }
            }else{
               Log.v("cagada", res);
            }
        }
    };

    public ServicioCom(){
        super();
    }

    private void Notificar(String res){
       if(!res.equals("")  && !res.equals("[]")){
            try {
                JSONArray l = new JSONArray(res);
                dbPendientes.RellenarTabla(l);
                Nuevas += l.length();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("ValleRecp")
                                .setContentText("Hay " + Integer.toString(Nuevas) + " bebidas nuevas");

                Intent intent = new Intent(getApplicationContext(), Recepcion.class);
                PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);
                // Add as notification
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification n = mBuilder.build();
                n.defaults |= Notification.DEFAULT_SOUND;
                manager.notify(12345, n);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbPendientes = new DbPendientes(getApplicationContext());
        server = intent.getStringExtra("server");
        return START_STICKY;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        timerUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                if (server != null) {
                    IDLinea = dbPendientes.getUltimo();
                    List<NameValuePair> p = new ArrayList<NameValuePair>();
                    if(IDLinea>0)   p.add(new BasicNameValuePair("reg", Integer.toString(IDLinea)));
                    new HTTPRequest(server+"/pedidos/rcpendientes",p,"P",Http);
                }
            }
        }, 0, 8000);

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

    public JSONArray getZonas() {
        return dbPendientes.getZonas();
    }

    public String getNumBebidas(String z) {
        return Integer.toString(dbPendientes.numBebidas(IDPedido,z));
    }

    public class MyBinder extends Binder {
        public ServicioCom getService() {
            return ServicioCom.this;
       }
     }


    public void sendServidos(String zona){
        JSONArray pedidos = dbPendientes.getAll(IDPedido, zona);
        dbPendientes.Vaciar(IDPedido, zona);

        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new BasicNameValuePair("lineas", pedidos.toString()));
        new HTTPRequest(server + "/pedidos/mservido", p, "S", Http);
    }

    public JSONArray getPendientes(String z){
        synchronized (dbPendientes) {
            Nuevas = 0;
            IDPedido = dbPendientes.getUltimo();
            return dbPendientes.getPendientes(IDPedido, z);
        }
    }
}
