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
import com.example.hampo.fragments.MiHampoFragment;
import com.example.hampo.fragments.NFCsyncFragment;
import com.example.hampo.fragments.RawSyncroFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterHampoList extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_syncro_NFC, R.string.tab_syncro_QR, R.string.tab_syncro_RAW};
    private final Context mContext;

    public SectionsPagerAdapterHampoList(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new NFCsyncFragment();
                break;
            case 1:
                fragment = new RawSyncroFragment();
                break;
            case 2:
                fragment = new RawSyncroFragment();
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