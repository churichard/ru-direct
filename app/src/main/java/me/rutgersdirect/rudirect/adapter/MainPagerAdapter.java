package me.rutgersdirect.rudirect.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import me.rutgersdirect.rudirect.fragment.ActiveRoutesFragment;
import me.rutgersdirect.rudirect.fragment.AllRoutesFragment;
import me.rutgersdirect.rudirect.fragment.DirectionsFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final String[] titles = {"Active Routes", "Directions", "All Routes"};
    private static final int NUM_ITEMS = titles.length;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment for the each position in the ViewPager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ActiveRoutesFragment();
        } else if (position == 1) {
            return new DirectionsFragment();
        } else {
            return new AllRoutesFragment();
        }
    }

    @Override
    public String getPageTitle(int position) {
        return titles[position];
    }
}