package es.vallesoft.comandas.Util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import es.vallesoft.comandas.IUtil.ITeclados;
import es.vallesoft.comandas.R;

/**
 * Created by valle on 12/09/14.
 */
public class Teclados extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private View panel;
    ITeclados click;

    public Teclados(){ }

    @SuppressLint("ValidFragment")
    public Teclados(ITeclados click){
        this.click = click;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        click.RellenarBotonera();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teclados, container, false);
        panel = view.findViewById(R.id.pneArt);
        Button b = (Button)view.findViewById(R.id.bar);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                click.AsociarBotonera(view);
                return false;
            }
        });
        b = (Button)view.findViewById(R.id.cafeteria);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                click.AsociarBotonera(view);
                return false;
            }
        });
        b = (Button)view.findViewById(R.id.tapas);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                click.TapaExtra(view);
                return false;
            }
        });
        return view;
    }

    public View getPanel() {
        return panel;
    }
}
