package es.vallesoft.DB;

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
public class DbPendientes extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "valletpv";

    public DbPendientes(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS pendientes (ID INTEGER PRIMARY KEY, nomZona TEXT, IDPedido INTEGER, Nombre TEXT, Precio TEXT,  IDArt INTEGER )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE  IF EXISTS pendientes");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



    public void RellenarTabla(JSONArray datos){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
       // Insert the new row, returning the primary key value of the new row
        for (int i= 0 ; i<datos.length();i++){
            // Create a new map of values, where column names are the keys
            try {
                   ContentValues values = new ContentValues();
                   values.put("ID", datos.getJSONObject(i).getInt("ID"));
                   values.put("IDArt", datos.getJSONObject(i).getInt("IDArt"));
                   values.put("Nombre", datos.getJSONObject(i).getString("Nombre"));
                   values.put("Precio", datos.getJSONObject(i).getDouble("Precio"));
                   values.put("IDPedido", datos.getJSONObject(i).getString("IDPedido"));
                   values.put("nomZona", datos.getJSONObject(i).getString("nomZona"));
                   db.insert("pendientes", null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        db.close();
     }

    public JSONArray getPendientes(int ID, String nomZona)
    {
           JSONArray lista = new JSONArray();
           SQLiteDatabase db = this.getReadableDatabase();

           try {
               Cursor res = db.rawQuery("SELECT *, COUNT(IDArt) AS Can FROM pendientes WHERE nomZona='"+nomZona+"' AND ID<= "+Integer.toString(ID) +
                       " GROUP BY  IDArt, Nombre ORDER BY ID DESC", null);
               res.moveToFirst();
               while (res.isAfterLast() == false) {
                   try {
                       JSONObject obj = new JSONObject();
                       obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                       obj.put("Can", res.getString(res.getColumnIndex("Can")));
                       lista.put(obj);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   res.moveToNext();

               }
               res.close();
           } catch (SQLiteException e) {
               db.close();
               db = this.getWritableDatabase();
               this.onCreate(db);
           }
           db.close();
           return lista;

    }

    public JSONArray getAll(int ID, String nomZona)
    {
        JSONArray lista = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor res = db.rawQuery("SELECT * FROM pendientes WHERE nomZona='"+nomZona+"' AND ID<="+Integer.toString(ID)+
                    "  ORDER BY ID DESC", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("ID", res.getString(res.getColumnIndex("ID")));
                    lista.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                res.moveToNext();

            }
            res.close();
        }catch (SQLiteException e){
            db.close(); db = this.getWritableDatabase();
            this.onCreate(db);
        }
        db.close();
        return lista;
    }

    public int numBebidas(int ID, String nomZona){
        SQLiteDatabase db = this.getReadableDatabase();
        int s = 0;
        Cursor cursor = db.rawQuery("SELECT count(ID) FROM pendientes WHERE nomZona='"+nomZona+"' AND ID<="+Integer.toString(ID), null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            s= cursor.getInt(0);
        }
        cursor.close();db.close();
        return s;
    }

    public int getUltimo() {
        SQLiteDatabase db = this.getReadableDatabase();
        int s = 0;
        Cursor cursor = db.rawQuery("SELECT ID FROM pendientes ORDER BY ID DESC LIMIT 1", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            s= cursor.getInt(0);
          }
        cursor.close();db.close();
        return s;
    }

    public void Vaciar(int ID, String nomZona){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
           db.execSQL("DELETE FROM pendientes WHERE nomZona='"+nomZona+"' AND ID<="+Integer.toString(ID));
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        db.close();
    }

    public void Vaciar(){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL("DELETE FROM pendientes");
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        db.close();
    }


    public JSONArray getZonas(){
        JSONArray lista = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor res = db.rawQuery("SELECT nomZona FROM pendientes "+
                    " GROUP BY nomZona ", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                try {
                    JSONObject obj = new JSONObject();
                      obj.put("nomZona", res.getString(res.getColumnIndex("nomZona")));
                    lista.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                res.moveToNext();

            }
            res.close();
        }catch (SQLiteException e){
            db.close(); db = this.getWritableDatabase();
            this.onCreate(db);
        }
        db.close();
        return lista;
    }


}
