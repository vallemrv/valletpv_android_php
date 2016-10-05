package es.vallesoft.valletpv.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import es.vallesoft.valletpv.Interfaces.IControlador;
import es.vallesoft.valletpv.R;

/**
 * Created by valle on 19/10/14.
 */
public class DlgVarios extends Dialog {

    IControlador controlador;

    public DlgVarios(Context context, final IControlador controlador) {
        super(context);
        this.controlador = controlador;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.varios);
        this.setTitle("Varios ");
        final TextView can = (TextView) this.findViewById(R.id.txtCan);
        final TextView p = (TextView) this.findViewById(R.id.txtPrecio);
        final TextView nom = (TextView) this.findViewById(R.id.txtNombre);
        Button ok = (Button) this.findViewById(R.id.btnAceptar);
        ImageButton s = (ImageButton) this.findViewById(R.id.salirVarios);

        can.setText("1");p.setText("");nom.setText("");

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                controlador.salir();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p.getText().length()>0) {
                    try {
                        JSONObject art = new JSONObject();
                        String nombre = nom.getText().toString().length()>0 ? nom.getText().toString() : "Varios";
                        art.put("ID", "0");
                        art.put("Precio", p.getText().toString().replace(",", "."));
                        art.put("Can", can.getText().toString());
                        art.put("Nombre", nombre);
                        controlador.pedirArt(art, can.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cancel();
                }
            }
        });

    }
}
