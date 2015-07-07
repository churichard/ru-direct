package me.rutgersdirect.rudirect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.BusStopsPagerAdapter;
import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.model.BusStop;

public class BusStopsActivity extends AppCompatActivity {

    public static boolean active; // Whether or not the activity is active
    private String busTag; // Bus tag
    private ArrayList<BusStop> busStops;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops);

        // Gets the bus tag, stop titles, and stop times
        Intent intent = getIntent();
        busTag = intent.getStringExtra(AppData.BUS_TAG_MESSAGE);
        busStops = (ArrayList) intent.getParcelableArrayListExtra(AppData.BUS_STOPS_MESSAGE);

        // Sets the title to the name of the bus
        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
        setTitle(tagsToBusesPref.getString(busTag, "Bus Stops"));

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_toolbar_back, getTheme()));
        }

        // Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.bus_stop_viewpager);
        viewPager.setAdapter(new BusStopsPagerAdapter(getFragmentManager()));

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.abc_shrink_fade_out_from_bottom);
    }

    @Override
    protected void onPause() {
        active = false; // Activity is not active
        super.onPause();
    }

    @Override
    public void onResume() {
        active = true; // Activity is active
        super.onResume();
    }

    // Returns the bus tag
    public String getBusTag() {
        return busTag;
    }

    // Returns the ArrayList of bus stops
    public ArrayList<BusStop> getBusStops() {
        return busStops;
    }
}