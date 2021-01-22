package com.example.hampo;


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
import com.example.hampo.fragments.GalleryFragmentB;
import com.example.hampo.fragments.MiHampoFragment;
import com.example.hampo.fragments.StadisticsFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterGallery extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_GalleryA, R.string.tab_text_GalleryB};
    private final Context mContext;

    public SectionsPagerAdapterGallery(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new GalleryFragmentA();
                break;
            case 1:
                fragment = new GalleryFragmentB();
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
        return 2;
    }
}
