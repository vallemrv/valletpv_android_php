package es.vallesoft.comandas.Util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import es.vallesoft.comandas.IUtil.IPedidos;
import es.vallesoft.comandas.R;

/**
 * Created by valle on 28/10/14.
 */
public class Pedidos extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private LinearLayout contenedor;

    public Pedidos(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pedidos, container, false);
        contenedor = (LinearLayout)v.findViewById(R.id.listaPedidos);
        return v;
    }

    public void vaciarPanel(){
        if(contenedor!=null) contenedor.removeAllViews();
    }

    public void addLinea(JSONObject art, ViewGroup.LayoutParams params, Context cx, final IPedidos pedidos){
        try {
            LayoutInflater inflater = (LayoutInflater) cx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.linea_pedido_externo, null);
            TextView t = (TextView) v.findViewById(R.id.lblCantidad);
            TextView s = (TextView) v.findViewById(R.id.lblNombre);
            t.setText(String.format("%s", art.getString("Can")));
            s.setText(String.format("%s - %s", art.getString("Nombre"), art.getString("nomMesa")));
            RelativeLayout btn = (RelativeLayout) v.findViewById(R.id.btnPedir);
            ImageButton btnCamb = (ImageButton) v.findViewById(R.id.btnBorrarPedido);
            btnCamb.setTag(art);
            btn.setTag(art);
            btn.setLongClickable(true);
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    pedidos.pedir(view);
                    return false;
                }
            });

            contenedor.addView(v, params);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

}
