package me.rutgersdirect.rudirect.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.fragment.ActiveRoutesFragment;
import me.rutgersdirect.rudirect.fragment.AllRoutesFragment;
import me.rutgersdirect.rudirect.fragment.DirectionsFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Active Routes", "Directions", "All Routes"};
    private static final int NUM_ITEMS = TITLES.length;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

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

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}