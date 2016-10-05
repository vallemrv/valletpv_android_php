package es.vallesoft.comandas.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.vallesoft.comandas.R;

/**
 * Created by valle on 15/09/14.
 */
public class Ticket extends ArrayAdapter<JSONObject> {

    private final Context context;
    private java.util.List<JSONObject> values = new ArrayList<JSONObject>();

    public Ticket(Context context, ArrayList<JSONObject> values) {
        super(context, R.layout.linea_art, (java.util.List<JSONObject>) values);
        this.context = context; this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linea_art, parent, false);
        try {
            TextView can = (TextView) rowView.findViewById(R.id.lblCan);
            TextView nombre = (TextView) rowView.findViewById(R.id.lblDescripcion);
            TextView p = (TextView) rowView.findViewById(R.id.lblPrecio);
            TextView t = (TextView) rowView.findViewById(R.id.lblTotal);
            can.setText(String.format("%s", values.get(position).getString("Can")));
            nombre.setText(String.format("%s",values.get(position).getString("Nombre")));
            p.setText(String.format("%.2f €", values.get(position).getDouble("Precio")));
            t.setText(String.format("%.2f €", values.get(position).getDouble("Total")));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rowView;
    }
}
