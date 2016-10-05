package es.vallesoft.valletpv.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import es.vallesoft.valletpv.Interfaces.IControlador;
import es.vallesoft.valletpv.R;

/**
 * Created by valle on 19/10/14.
 */
public class DlgCobrar extends Dialog{

    JSONArray lineas;
    IControlador controlador;
    String strEntrega = "";
    Double totalCobro = 0.00;
    Double entrega = 0.00;
    TextView lblEntrega;
    TextView lblCambio;
    TextView lbltotal;

    public DlgCobrar(Context context, IControlador controlador) {
        super(context);
        this.controlador = controlador;
        setContentView(R.layout.cobros);
        lbltotal = (TextView) findViewById(R.id.lblPrecio);
        lblEntrega = (TextView) findViewById(R.id.lblEntrega);
        lblCambio = (TextView) findViewById(R.id.lblCambio);
        Button tj = (Button)findViewById(R.id.btnTarjeta);
        ImageButton ef = (ImageButton)findViewById(R.id.btnEfectivo);
        ImageButton s = (ImageButton)findViewById(R.id.btnSalir);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSalir(view);
            }
        });
        ef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEfectivo(view);
            }
        });

        tj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTarjeta(view);
            }
        });
    }

    public void setDatos(JSONArray lineas, Double totalCobro){
        this.lineas = lineas;
        lbltotal.setText(String.format("%01.2f €", totalCobro));
        lblEntrega.setText(String.format("%01.2f €", totalCobro));
        this.totalCobro = totalCobro;
        this.entrega = totalCobro;
        this.strEntrega = "";
        LinearLayout pne = (LinearLayout)findViewById(R.id.pneBotonera);
        ArrayList<View> touchables = pne.getTouchables();

        for (View v : touchables){
            if (v instanceof Button){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickEntrega(view);
                    }
                });
            }
        }
    }


    public void clickEfectivo(View v){
        if(entrega>=totalCobro) {

            controlador.cobrar(lineas,totalCobro,entrega);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View vtoast = inflater.inflate(R.layout.ticket_cobrado, null);
            vtoast.setMinimumWidth(700);

            TextView text = (TextView) vtoast.findViewById(R.id.text);
            text.setText(String.format("Total: %01.2f €, Entrega: %01.2f € \n Cambio: %01.2f € ", totalCobro, entrega, entrega - totalCobro));

            final Toast toast = new Toast(getContext());
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);

            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(vtoast);
            toast.show();
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    toast.show();
                }
                public void onFinish() {
                    toast.cancel();
                }
            }.start();

           this.cancel();
        }
    }

    public void clickTarjeta(View v){
        controlador.cobrar(lineas,totalCobro,0.00);
        this.cancel();
    }

    public void clickSalir(View v){
        controlador.salir();cancel();
    }

    public void clickEntrega(View v){
        String caracter = v.getTag().toString();
        if(caracter.equals("C")){
            entrega = totalCobro; strEntrega="";
            lblEntrega.setText(String.format("%01.2f €", totalCobro));
            lblCambio.setText("0.00 €");
        }else{
            try {
                strEntrega+=caracter;
                entrega = Double.parseDouble(strEntrega);
                lblEntrega.setText(String.format("%01.2f €", entrega));
                if(entrega>totalCobro)  lblCambio.setText(String.format("%01.2f €", entrega-totalCobro));
            }catch (Exception e){
                entrega = totalCobro; strEntrega= "";
                lblEntrega.setText(String.format("%01.2f €", totalCobro));
                lblCambio.setText("0.00 €");
            }
        }

    }

}
