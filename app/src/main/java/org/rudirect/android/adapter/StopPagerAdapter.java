package org.rudirect.android.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import org.rudirect.android.fragment.BusTimesFragment;

public class StopPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Routes"};
    public static final int NUM_OF_ITEMS = TITLES.length;

    public StopPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_OF_ITEMS;
    }

    // Returns the fragment for the each position in the ViewPager
    @Override
    public Fragment getItem(int position) {
        return new BusTimesFragment();
    }

    @Override
    public String getPageTitle(int position) {
        return TITLES[position];
    }
}