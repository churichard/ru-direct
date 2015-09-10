package org.rudirect.android.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.rudirect.android.fragment.DirectionsFragment;
import org.rudirect.android.fragment.RoutesFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = {"Routes", "Directions"};
    private static final int NUM_OF_ITEMS = TITLES.length;
    private static SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public MainPagerAdapter(FragmentManager fm) {
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
            return new RoutesFragment();
        } else {
            return new DirectionsFragment();
        }
    }

    @Override
    public String getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public static Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}