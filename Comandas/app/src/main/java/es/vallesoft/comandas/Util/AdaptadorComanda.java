package es.vallesoft.comandas.Util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.vallesoft.comandas.Comanda;

/**
 * Created by valle on 12/09/14.
 */
public class AdaptadorComanda extends FragmentPagerAdapter {

    Fragment comanda = null;
    Fragment teclados = null;

    public AdaptadorComanda(FragmentManager fm, Fragment comanda, Fragment teclados) {
        super(fm); this.comanda = comanda; this.teclados= teclados;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 1:
                 return (Fragment) comanda;
              case 0:
                 return  (Fragment) teclados;
             default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return  "Teclados";
            case 1:
                return "La comanda";
            default:
                return null;

        }

    }
}
