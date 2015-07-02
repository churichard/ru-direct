package me.rutgersdirect.rudirect.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.MainPagerAdapter;
import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.fragment.DirectionsFragment;
import me.rutgersdirect.rudirect.interfaces.UpdateBusStopsListener;

public class MainActivity extends AppCompatActivity implements UpdateBusStopsListener {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize bus tags to stop times hash map
        AppData.BUS_TAGS_TO_STOP_TIMES = new HashMap<>();

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up viewpager
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new MainPagerAdapter(getFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBusStopsUpdate() {
        MainPagerAdapter adapter = (MainPagerAdapter) viewPager.getAdapter();
        DirectionsFragment directionsFragment = (DirectionsFragment) adapter.getRegisteredFragment(1);
        directionsFragment.setupAutoCompleteTextViews();
    }
}