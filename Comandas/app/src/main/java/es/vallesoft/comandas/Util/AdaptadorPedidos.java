package es.vallesoft.comandas.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import es.vallesoft.comandas.R;

/**
 * Created by valle on 14/09/14.
 */
public class AdaptadorPedidos extends ArrayAdapter<JSONObject> {

    Context cx;
    List<JSONObject> values;

    public AdaptadorPedidos(Context context, List<JSONObject> obj) {
        super(context, R.layout.item_pedido, obj);
        this.cx = context;this.values= obj;
      }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         LayoutInflater inflater = (LayoutInflater) cx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_pedido, parent, false);

        try {
            TextView nombre = (TextView) rowView.findViewById(R.id.lblDescripcion);
            JSONObject art = values.get(position);
            nombre.setText(String.format("%s",art.getString("Nombre")));

            ImageButton btnRm = (ImageButton)rowView.findViewById(R.id.btnBorrar);
            btnRm.setTag(art);

            RelativeLayout btnInfo = (RelativeLayout)rowView.findViewById(R.id.linea_art);
            btnInfo.setTag(art);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rowView;
    }
}
