package es.vallesoft.valletpv.Interfaces;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by valle on 19/10/14.
 */
public interface IControlador {
    void salir();
    void mostarCobrar(final JSONArray lsart, Double totalCobro);
    void cobrar(JSONArray lsart, Double totalCobro, Double entrega );
    void pedirArt(JSONObject art, String s);
    void clickMostrarBorrar(final JSONObject art);
    void borrarArticulo(JSONObject art) throws JSONException;
}
