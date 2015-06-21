package me.rutgersdirect.rudirect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
    private String[] latitudes;
    private String[] longitudes;
    private String[][] pathLats;
    private String[][] pathLons;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops);

        // Gets the bus tag, stop titles, and stop times
        Intent intent = getIntent();
        busTag = intent.getStringExtra(AppData.BUS_TAG_MESSAGE);
        busStops = (ArrayList) intent.getParcelableArrayListExtra(AppData.BUS_STOPS_MESSAGE);
        latitudes = intent.getStringArrayExtra(AppData.BUS_STOP_LATS_MESSAGE);
        longitudes = intent.getStringArrayExtra(AppData.BUS_STOP_LONS_MESSAGE);

        Bundle bundle = intent.getExtras();
        // Get path latitudes
        Object[] objectPathLats = (Object[]) bundle.getSerializable(AppData.BUS_PATH_LATS_MESSAGE);
        pathLats = new String[objectPathLats.length][];
        for (int i = 0; i < objectPathLats.length; i++) {
            pathLats[i] = (String[]) objectPathLats[i];
        }
        // Get path longitudes
        Object[] objectPathLons = (Object[]) bundle.getSerializable(AppData.BUS_PATH_LONS_MESSAGE);
        pathLons = new String[objectPathLons.length][];
        for (int i = 0; i < objectPathLons.length; i++) {
            pathLons[i] = (String[]) objectPathLons[i];
        }

        // Sets the title to the name of the bus
        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
        setTitle(tagsToBusesPref.getString(busTag, "Bus Stops"));

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    // Get array of bus stop latitudes
    public String[] getLatitudes() {
        return latitudes;
    }

    // Get array of bus stop longitudes
    public String[] getLongitudes() {
        return longitudes;
    }

    // Get array of bus path latitudes
    public String[][] getPathLats() {
        return pathLats;
    }

    // Get array of bus path longitudes
    public String[][] getPathLons() {
        return pathLons;
    }
}