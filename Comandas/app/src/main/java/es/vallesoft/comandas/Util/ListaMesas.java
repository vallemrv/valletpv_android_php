package es.vallesoft.comandas.Util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import es.vallesoft.comandas.R;

/**
 * Created by valle on 28/10/14.
 */
public class ListaMesas extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    TableLayout lsMesas;

    public ListaMesas(){}

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
        View view = inflater.inflate(R.layout.mesas, container, false);
        lsMesas = (TableLayout) view.findViewById(R.id.pneMesas);
        return view;
    }


    public void addView(View b, ViewGroup.LayoutParams layout){
       if(lsMesas!=null) lsMesas.addView(b, layout);
    }

    public void clearTable(){
       if(lsMesas!=null) lsMesas.removeAllViews();
    }

}
