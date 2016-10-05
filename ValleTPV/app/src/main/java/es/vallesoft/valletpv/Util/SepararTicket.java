package es.vallesoft.valletpv.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import es.vallesoft.valletpv.R;

/**
 * Created by xbmc on 10/09/14.
 */
public class SepararTicket extends ArrayAdapter<JSONObject>{

    private final Context context;
    private java.util.List<JSONObject> values = null;
    boolean separados;


    public SepararTicket(Context context, ArrayList<JSONObject> values, boolean separados) {
        super(context, R.layout.item_art, (java.util.List<JSONObject>) values);
        this.context = context;   this.values = values; this.separados = separados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_separado, parent, false);
        try {
            JSONObject art = values.get(position);
            int can = art.getInt("Can");
            int canCobro = art.getInt("CanCobro");
            TextView lblCan = (TextView) rowView.findViewById(R.id.lblCan);
            TextView nombre = (TextView) rowView.findViewById(R.id.lblNombre);
            if(separados) can = canCobro;
            else{
                can = can-canCobro;
            }
            lblCan.setText(String.format("%s", can));
            nombre.setText(String.format("%s", art.getString("Nombre")));
            rowView.setTag(art);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}

