package es.vallesoft.valletpv.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by valle on 13/10/14.
 */
public class DbTeclas extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "valletpv";


    public DbTeclas(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS  teclas (ID INTEGER PRIMARY KEY, Nombre TEXT, P1 DOUBLE, P2 DOUBLE, Precio DOUBLE," +
                         " RGB TEXT, IDSeccion INTEGER, Tag TEXT, Orden INTEGER, IDSec2 INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS teclas");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public void RellenarTabla(JSONArray datos){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        try{
          db.execSQL("DELETE FROM teclas");
        }catch (SQLiteException e){
            this.onCreate(db);
        }
       // Insert the new row, returning the primary key value of the new row
        for (int i= 0 ; i<datos.length();i++){
            // Create a new map of values, where column names are the keys
            try {
                 ContentValues values = new ContentValues();
                 values.put("ID", datos.getJSONObject(i).getInt("ID"));
                 values.put("IDSeccion", datos.getJSONObject(i).getInt("IDSeccion"));
                 values.put("Nombre", datos.getJSONObject(i).getString("Nombre"));
                 values.put("P1", datos.getJSONObject(i).getDouble("P1"));
                 values.put("P2", datos.getJSONObject(i).getDouble("P2"));
                 values.put("Precio", datos.getJSONObject(i).getDouble("Precio"));
                 values.put("RGB", datos.getJSONObject(i).getString("RGB"));
                 values.put("Tag", datos.getJSONObject(i).getString("Tag"));
                 values.put("IDSec2", datos.getJSONObject(i).getString("IDSec2"));
                 values.put("Orden", datos.getJSONObject(i).getString("Orden"));

                db.insert("teclas", null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        db.close();
    }

    public JSONArray getAll(String id, int tarifa)
    {
        JSONArray ls = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM teclas WHERE IDSeccion="+id +" OR IDSec2="+id+" ORDER BY Orden DESC", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            try{
                JSONObject obj = new JSONObject();
                obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                obj.put("ID", res.getString(res.getColumnIndex("ID")));
                obj.put("RGB", res.getString(res.getColumnIndex("RGB")));
                if (tarifa == 2)   obj.put("Precio", res.getString(res.getColumnIndex("P2")));
                else  obj.put("Precio", res.getString(res.getColumnIndex("P1")));
                ls.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            res.moveToNext();

        }
        res.close();db.close();
        return ls;
    }


    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int s = 0;
        Cursor cursor = db.rawQuery("select count(*) from teclas", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
           s = cursor.getInt(0);
        }
        cursor.close();db.close();
        return  s;
    }

    public void Vaciar(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
           db.execSQL("DELETE FROM teclas");
        }catch (SQLiteException e){
           this.onCreate(db);
        }
        db.close();
     }

    public JSONArray getCoincidencia(String str, String t) {
        JSONArray ls = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM teclas WHERE Tag LIKE '%"+str+"%' ORDER BY Orden DESC LIMIT 15 ", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            try{
                JSONObject obj = new JSONObject();
                obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                obj.put("ID", res.getString(res.getColumnIndex("ID")));
                obj.put("RGB", res.getString(res.getColumnIndex("RGB")));
                if (t.equals("2"))   obj.put("Precio", res.getString(res.getColumnIndex("P2")));
                else  obj.put("Precio", res.getString(res.getColumnIndex("P1")));
                ls.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            res.moveToNext();

        }
        res.close();db.close();
        return ls;
    }
}
