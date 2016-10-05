package es.vallesoft.valletpv.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.vallesoft.valletpv.Interfaces.IControlador;
import es.vallesoft.valletpv.R;
import es.vallesoft.valletpv.Util.SepararTicket;

/**
 * Created by valle on 19/10/14.
 */
public class DlgSepararTicket extends Dialog{

    IControlador controlador;
    Double totalCobro = 0.00;
    ListView lstArt;
    ArrayList<JSONObject> lineasTicket = new ArrayList<JSONObject>();
    ArrayList<JSONObject> separados = new ArrayList<JSONObject>();




    public DlgSepararTicket(Context context, IControlador controlador ) {
        super(context);
        this.controlador = controlador;
        setContentView(R.layout.separarticket);

            final TextView tot = (TextView) findViewById(R.id.lblTotalCobro);
            final ListView lstCobros = (ListView)findViewById(R.id.lstCobros);
            Button ok = (Button)findViewById(R.id.btnAceptar);
            ImageButton s = (ImageButton)findViewById(R.id.btnSalir);

            lstArt = (ListView)findViewById(R.id.lstArticulos);

             ok.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     clickCobrarSeparados(view);
                 }
             });

             s.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     clickSalirSeparados(view);
                 }
             });

            tot.setText(String.format("Total cobro %01.2f €", totalCobro));

            lstArt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        JSONObject art = (JSONObject)view.getTag();
                        int can = art.getInt("Can");
                        int canCobro = art.getInt("CanCobro")+1;
                        if(canCobro<=can) {
                            totalCobro += art.getDouble("Precio");
                            tot.setText(String.format("Total cobro %01.2f €", totalCobro));
                            art.put("CanCobro", canCobro);
                            if (can==canCobro) lineasTicket.remove(art);
                            if (canCobro == 1) separados.add(art);
                            lstCobros.setAdapter(new SepararTicket(getContext(), separados, true));
                            lstArt.setAdapter(new SepararTicket(getContext(), lineasTicket, false));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });



            lstCobros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        JSONObject art = (JSONObject)view.getTag();
                        int can = art.getInt("Can");
                        int canCobro = art.getInt("CanCobro")-1;
                        totalCobro -= art.getDouble("Precio");
                        tot.setText(String.format("Total cobro %01.2f €", totalCobro));
                        art.put("CanCobro", canCobro);
                        if (can > canCobro) lineasTicket.add(art);
                        if (canCobro == 0) separados.remove(art);
                        lstCobros.setAdapter(new SepararTicket(getContext(), separados, true));
                        lstArt.setAdapter(new SepararTicket(getContext(), lineasTicket, false));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



    }

    public void setLineasTicket(JSONArray lsart) throws JSONException {
        for(int i= 0;i<lsart.length();i++){
            JSONObject art = new JSONObject(lsart.get(i).toString());
            art.put("CanCobro",0);
            lineasTicket.add(art);
        }
        lstArt.setAdapter(new SepararTicket(getContext(), lineasTicket, false));
     }


    public void clickCobrarSeparados(View v){
        JSONArray arts = new JSONArray();
        for(int i=0;i<separados.size();i++){
            try {
                JSONObject art = separados.get(i);
                art.put("Can", art.getString("CanCobro"));
                arts.put(separados.get(i));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        cancel();
        controlador.mostarCobrar(arts, totalCobro);
    }

    public void clickSalirSeparados(View view){
        cancel(); this.controlador.salir();
    }

}
