package com.example.hampo.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.hampo.R;
import com.example.hampo.chart.graficaFragment;
import com.example.hampo.fragments.GalleryFragment;
import com.example.hampo.fragments.GalleryFragmentA;
import com.example.hampo.fragments.MiHampoFragment;
import com.example.hampo.fragments.StadisticsFragment;

import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterMiHampo extends FragmentPagerAdapter {
    public static ArrayList<Float> datosTemperatura;
    public static ArrayList<Float> datosHumedad;
    public static ArrayList<Float> datosLuminosidad;
    public static ArrayList<String> fechasTemperatura;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapterMiHampo(Context context, FragmentManager fm, ArrayList<String> fechas, ArrayList<Float> datosTemperatura, ArrayList<Float> datosHumedad, ArrayList<Float> datosLuminosidad) {
        super(fm);
        mContext = context;
        this.datosTemperatura = datosTemperatura;
        this.datosHumedad = datosHumedad;
        this.datosLuminosidad = datosLuminosidad;
        this.fechasTemperatura = fechas;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new MiHampoFragment();
                break;
            case 1:
                fragment = new graficaFragment();
                break;
            case 2:
                fragment = new GalleryFragment();
                break;
        }
        // getItem is called to instantiate the fragment for the given page.
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}