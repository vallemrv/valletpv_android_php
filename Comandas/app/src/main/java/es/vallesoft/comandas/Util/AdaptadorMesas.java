package es.vallesoft.comandas.Util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by valle on 28/10/14.
 */
public class AdaptadorMesas extends FragmentPagerAdapter {

    Fragment mesas = null;
    Fragment pedidos = null;

    public AdaptadorMesas(FragmentManager fm, Fragment mesas, Fragment pedidos) {
        super(fm); this.mesas = mesas; this.pedidos= pedidos;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 1:
                return (Fragment) pedidos;
            case 0:
                return  (Fragment) mesas;
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
                return  "Mesas";
            case 1:
                return "Pedidos";
            default:
                return null;

        }

    }
}
