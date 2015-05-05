package me.rutgersdirect.rudirect.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import me.rutgersdirect.rudirect.ui.fragment.ActiveBusesFragment;
import me.rutgersdirect.rudirect.ui.fragment.AllBusesFragment;

public class SlidingPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    private String[] titles;

    public SlidingPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment for the each position in the ViewPager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ActiveBusesFragment();
        }
        else {
            return new AllBusesFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}