package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.adapter.SlidingPagerAdapter;
import me.rutgersdirect.rudirect.ui.SlidingTabLayout;

public class SlidingTabsFragment extends Fragment {
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_sliding_tabs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the sliding pager adapter for the view pager
        SlidingPagerAdapter slidingPagerAdapter = new SlidingPagerAdapter(getFragmentManager());
        ViewPager pager = (ViewPager) mainActivity.findViewById(R.id.sliding_pager);
        pager.setAdapter(slidingPagerAdapter);

        // Configuring the sliding tab layout view
        SlidingTabLayout tabs = (SlidingTabLayout) mainActivity.findViewById(R.id.sliding_tabs);
        tabs.setDistributeEvenly(true);

        // Setting color for the scroll bar indicator of the tab view
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the view pager for the sliding tab layout
        tabs.setViewPager(pager);
    }
}
