package es.vallesoft.comandas.Util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.Util.JSON;
import es.vallesoft.comandas.IUtil.INota;

/**
 * Created by valle on 18/09/14.
 */
public class Nota {

    private List<JSONObject> comanda = new ArrayList<JSONObject>();
    private int num= 0;

    String nombre="";
    Context cx;
    INota controlador;
    JSONObject artSel = null;

    public Nota(JSONObject mesa, Context cx, INota ctr){
        try {
          this.nombre = mesa.getString("Nombre");
         } catch (Exception e) {
            e.printStackTrace();
        }
        this.cx = cx;this.controlador = ctr;
        this.CargarComanda();
    }


    public int getNum() {
        return num;
    }

    public String getArt(JSONObject art){
        this.artSel = art;
        return this.artSel.toString();
    }

    public List<JSONObject> getLineas() {
        return this.comanda;
    }

    public void rmArt(JSONObject art){
        try {
           this.num--;
           comanda.remove(art);
           this.GuardarComanda();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addArt(JSONObject art, int can){
        try {
            this.num+=can;
            art.put("Can",1);
            for(int i = 0;i<can;i++){
                comanda.add(new JSONObject(art.toString()));
            }
            this.GuardarComanda();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CargarComanda(){
        JSON json = new JSON();
        try {
            JSONObject cm = json.deserializar(nombre+".dat", cx);
            comanda = new ArrayList<JSONObject>();
            if(cm==null)  num = 0;
            else{

                JSONArray l = new JSONArray(cm.get("lineas").toString());

                this.num = cm.getInt("num");
                for(int i=0;i<l.length();i++){
                    JSONObject art = l.getJSONObject(i);
                    comanda.add(art);
                  }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GuardarComanda(){
        JSON json = new JSON();
        try {
            JSONObject cm = new JSONObject();
            cm.put("num", this.num);
            cm.put("lineas", this.getLineas().toString());
            json.serializar(nombre + ".dat",cm, cx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        controlador.RellenarComanda();

    }


    public void EliminarComanda(){
            this.comanda = new ArrayList<JSONObject>();
            this.num = 0;
            cx.deleteFile(nombre + ".dat");
            controlador.RellenarComanda();
     }

    public void addSug( String sug) {
         try{
            String nombre = this.artSel.getString("Nombre")+" "+sug;
            this.artSel.put("Nombre", nombre);
            this.GuardarComanda();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
