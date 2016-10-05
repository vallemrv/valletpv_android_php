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
public class DbCuenta extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "valletpv";
    public static int IDMesa = -1;

    public DbCuenta(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cuenta (ID INTEGER PRIMARY KEY, Estado TEXT, Nombre TEXT, Precio DOUBLE, IDMesa INTEGER, IDArt INTEGER )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE  IF EXISTS cuenta");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



    public void RellenarTabla(JSONArray datos){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        try{
           db.execSQL("DELETE FROM cuenta WHERE IDMesa <> "+Integer.toString(DbCuenta.IDMesa));
        }catch (SQLiteException e){
            e.printStackTrace();
            this.onCreate(db);
        }
        // Insert the new row, returning the primary key value of the new row
        for (int i= 0 ; i<datos.length();i++){
            // Create a new map of values, where column names are the keys
            try {
               int idMesa = datos.getJSONObject(i).getInt("IDMesa");
               if(DbCuenta.IDMesa!= idMesa) {
                   ContentValues values = new ContentValues();
                   values.put("ID", datos.getJSONObject(i).getInt("ID"));
                   values.put("IDArt", datos.getJSONObject(i).getInt("IDArt"));
                   values.put("Nombre", datos.getJSONObject(i).getString("Nombre"));
                   values.put("Precio", datos.getJSONObject(i).getDouble("Precio"));
                   values.put("IDMesa", idMesa);
                   values.put("Estado", datos.getJSONObject(i).getString("Estado"));
                   db.insert("cuenta", null, values);
               }
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

        try {
            Cursor res = db.rawQuery("SELECT *, COUNT(ID) AS Can, SUM(PRECIO) AS Total FROM cuenta WHERE IDMesa=" + id +
                    " GROUP BY  IDArt, Nombre, Precio, Estado ORDER BY ID DESC", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                    obj.put("Can", res.getString(res.getColumnIndex("Can")));
                    obj.put("Precio", res.getString(res.getColumnIndex("Precio")));
                    obj.put("Total", res.getString(res.getColumnIndex("Total")));
                    obj.put("IDArt", res.getString(res.getColumnIndex("IDArt")));
                    obj.put("Estado", res.getString(res.getColumnIndex("Estado")));
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

    public Double getTotal(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Double s = 0.0;

        try {
            Cursor cursor = db.rawQuery("SELECT SUM(Precio) AS TotalTicket FROM cuenta WHERE IDMesa=" + id, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
                s = cursor.getDouble(0);
            }
            cursor.close();

        }catch (SQLiteException e){
            db.close();db = this.getWritableDatabase();
            this.onCreate(db);
        }

        db.close();
        return s;
    }


    public int getCount(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int s = 0;
        Cursor cursor = db.rawQuery("SELECT count(*) FROM cuenta WHERE IDMesa="+id, null);
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
           db.execSQL("DELETE FROM cuenta");
        }catch (SQLiteException e){
            this.onCreate(db);
        }
        db.close();
    }

    public void addArt(int can, int IDMesa,  JSONObject art){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
         // Insert the new row, returning the primary key value of the new row
        for(int i=0;i<can;i++) {
            try {
                ContentValues values = new ContentValues();
                values.put("IDArt", art.getInt("ID"));
                values.put("Nombre", art.getString("Nombre"));
                values.put("Precio", art.getDouble("Precio"));
                values.put("IDMesa", IDMesa);
                values.put("Estado", "N");
                db.insert("cuenta", null, values);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        db.close();
    }

    public void eliminar(String IDMesa, JSONArray lsart){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < lsart.length(); i++){
                JSONObject art = lsart.getJSONObject(i);
                String sql = "DELETE FROM cuenta WHERE ID IN (SELECT ID FROM cuenta WHERE IDMesa="
                               + IDMesa + " AND IDArt=" + art.getString("IDArt")
                                        + " AND Nombre='" + art.getString("Nombre")+"'"
                                        + " AND Precio=" + art.getString("Precio")
                                        + " AND Estado='" + art.getString("Estado") + "' LIMIT "+art.getString("Can")+")";
                db.execSQL(sql);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
            this.onCreate(db);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void eliminar(String IDMesa){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
           db.execSQL("DELETE FROM cuenta WHERE IDMesa=" + IDMesa);
        }catch (SQLiteException e) {
            e.printStackTrace();
            this.onCreate(db);
        }
        db.close();
    }

    public void aparcar(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL("UPDATE cuenta SET Estado='P' WHERE IDMesa="+id);
        }catch (SQLiteException e){
            e.printStackTrace();
            this.onCreate(db);
        }
        db.close();
    }

    public JSONArray getNuevos(String id) {

        JSONArray lista = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor res = db.rawQuery("SELECT *, COUNT(ID) AS Can FROM cuenta WHERE IDMesa=" + id +" AND Estado = 'N'"+
                    " Group by IDArt, Nombre, Precio, Estado", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("Nombre", res.getString(res.getColumnIndex("Nombre")));
                    obj.put("Can", res.getString(res.getColumnIndex("Can")));
                    obj.put("Precio", res.getString(res.getColumnIndex("Precio")));
                    obj.put("IDArt", res.getString(res.getColumnIndex("IDArt")));
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

    public void cambiarCuenta(String id, String id1) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("IDMesa", id1);
            db.update("cuenta", values, "IDMesa="+id, null);
         }catch (SQLiteException e){
            e.printStackTrace();
         }
        db.close();
    }
}
