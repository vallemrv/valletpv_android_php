package es.vallesoft.valletpv.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.vallesoft.valletpv.Interfaces.IControlador;
import es.vallesoft.valletpv.R;

/**
 * Created by xbmc on 8/09/14.
 */
public class Ticket extends ArrayAdapter<JSONObject> implements View.OnClickListener {

    private final Context context;
    private java.util.List<JSONObject> values = new ArrayList<JSONObject>();
    IControlador controlador;

    public Ticket(Context context, ArrayList<JSONObject> values, IControlador controlador) {
        super(context, R.layout.item_art, (java.util.List<JSONObject>) values);
        this.context = context;this.controlador = controlador;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_art, parent, false);

        try {
            TextView can = (TextView) rowView.findViewById(R.id.lblCan);
            TextView nombre = (TextView) rowView.findViewById(R.id.lblNombre);
            TextView p = (TextView) rowView.findViewById(R.id.lblPrecio);
            TextView t = (TextView) rowView.findViewById(R.id.lblTotal);
            can.setText(String.format("%s", values.get(position).getString("Can")));
            nombre.setText(String.format("%s",values.get(position).getString("Nombre")));
            p.setText(String.format("%.2f €", values.get(position).getDouble("Precio")));
            t.setText(String.format("%.2f €", values.get(position).getDouble("Total")));
            ImageButton rm = (ImageButton)rowView.findViewById(R.id.btnBorrar);
            rm.setTag(values.get(position));
            rm.setOnClickListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rowView;
    }

    @Override
    public void onClick(View view) {
        try {
            JSONObject art = (JSONObject)view.getTag();
            String estado = art.getString("Estado");
            art.put("Can", 1);
            if(estado.equals("N")) controlador.borrarArticulo(art);
            else controlador.clickMostrarBorrar(art);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
