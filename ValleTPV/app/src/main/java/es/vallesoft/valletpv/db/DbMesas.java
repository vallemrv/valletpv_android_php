package es.vallesoft.valletpv.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by valle on 13/10/14.
 */
public class DbMesas extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "valletpv";


    public DbMesas(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS mesas (ID INTEGER PRIMARY KEY, Nombre TEXT, RGB TEXT, abierta TEXT,  IDZona INTEGER, num INTEGER, Orden INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS mesas");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public void RellenarTabla(JSONArray datos){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        try{
          db.execSQL("DELETE FROM mesas");
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        // Insert the new row, returning the primary key value of the new row
        for (int i= 0 ; i<datos.length();i++){
            // Create a new map of values, where column names are the keys
            try {
                ContentValues values = new ContentValues();
                values.put("ID", datos.getJSONObject(i).getInt("ID"));
                values.put("Nombre", datos.getJSONObject(i).getString("Nombre"));
                values.put("IDZona", datos.getJSONObject(i).getInt("IDZona"));
                values.put("RGB", datos.getJSONObject(i).getString("RGB"));
                values.put("abierta", datos.getJSONObject(i).getString("abierta"));
                values.put("num", datos.getJSONObject(i).getInt("num"));
                values.put("Orden", datos.getJSONObject(i).getInt("Orden"));
                db.insert("mesas", null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        db.close();
    }

    public JSONArray getAll(String id)
    {
        JSONArray lista = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM mesas WHERE IDZona="+id+" ORDER BY Orden DESC", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            try{
                JSONObject obj = new JSONObject();
                int num = res.getInt(res.getColumnIndex("num"));
                String RGB = res.getString(res.getColumnIndex("RGB"));
                obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                obj.put("IDZoma", res.getString(res.getColumnIndex("IDZona")));
                obj.put("RGB", num<=0 ? RGB : "255,0,0");
                obj.put("abierta", res.getString(res.getColumnIndex("abierta")));
                obj.put("ID", res.getString(res.getColumnIndex("ID")));
                lista.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            res.moveToNext();

        }
        res.close();db.close();
        return lista;
    }


    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int s = 0;
        Cursor cursor = db.rawQuery("select count(*) from mesas", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            s= cursor.getInt(0);
          }
        cursor.close();db.close();
        return s;
    }

    public void Vaciar(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
           db.execSQL("DELETE FROM mesas");
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        db.close();
    }

    public void update(JSONArray datos) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE mesas SET abierta='false', num=0");
            for (int i = 0; i < datos.length(); i++) {
                db.execSQL("UPDATE mesas SET abierta='true', num="
                        + datos.getJSONObject(i).getString("num") + " WHERE ID=" + datos.getJSONObject(i).getString("IDMesa"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        db.close();
    }


    public void abrirMesa(String idm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE mesas SET abierta='true', num=0 WHERE ID="+idm);
        db.close();
    }

    public void cerrarMesa(String idm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE mesas SET abierta='false', num=0 WHERE ID="+idm);
        db.close();
    }

    public JSONArray getAllMenosUna(String id, String idm) {
        JSONArray lista = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM mesas WHERE IDZona="+id+" ORDER BY Orden DESC", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            try{
                String ID = res.getString(res.getColumnIndex("ID"));
                if(!ID.equals(idm)) {
                    JSONObject obj = new JSONObject();
                    int num = res.getInt(res.getColumnIndex("num"));
                    String RGB = res.getString(res.getColumnIndex("RGB"));
                    obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                    obj.put("IDZoma", res.getString(res.getColumnIndex("IDZona")));
                    obj.put("RGB", num <= 0 ? RGB : "255,0,0");
                    obj.put("abierta", res.getString(res.getColumnIndex("abierta")));
                    obj.put("ID", ID);
                    lista.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            res.moveToNext();

        }
        res.close();db.close();
        return lista;
    }
}
