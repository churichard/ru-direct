package me.rutgersdirect.rudirect.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import me.rutgersdirect.rudirect.fragment.BusMapFragment;
import me.rutgersdirect.rudirect.fragment.BusTimesFragment;

public class BusStopsPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Route", "Map"};
    public static final int NUM_OF_ITEMS = TITLES.length;

    public BusStopsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_OF_ITEMS;
    }

    // Returns the fragment for the each position in the ViewPager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new BusTimesFragment();
        } else {
            return new BusMapFragment();
        }
    }

    @Override
    public String getPageTitle(int position) {
        return TITLES[position];
    }
}