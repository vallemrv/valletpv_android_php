package es.vallesoft.comandas.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import es.vallesoft.comandas.R;

/**
 * Created by valle on 17/09/14.
 */
public class AdaptadorSugerencias extends ArrayAdapter<JSONObject> {

    Context cx;
    List<JSONObject> values;

    public AdaptadorSugerencias(Context context, List<JSONObject> obj) {
        super(context, R.layout.linea_simple, obj);
        this.cx = context;this.values= obj;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) cx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linea_simple, parent, false);

        try {
            TextView can = (TextView) rowView.findViewById(R.id.lblSugerencia);
            String sug = values.get(position).getString("Sugerencia");
            can.setText(String.format("%s", sug));
            RelativeLayout btnInfo = (RelativeLayout)rowView.findViewById(R.id.btnArt);
            btnInfo.setTag(sug);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rowView;
    }

}
