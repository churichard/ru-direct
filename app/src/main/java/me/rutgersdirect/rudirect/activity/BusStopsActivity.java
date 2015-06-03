package me.rutgersdirect.rudirect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.BusStopAdapter;
import me.rutgersdirect.rudirect.data.AppData;
import me.rutgersdirect.rudirect.model.BusStop;
import me.rutgersdirect.rudirect.util.ShowBusStopsHelper;

public class BusStopsActivity extends AppCompatActivity {
    public static boolean active; // Whether or not the activity is active
    private String busTag; // Bus tag
    private Handler refreshHandler; // Handles auto refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Bus stop times expansion
    public static boolean expansionRequest; // Whether or not the bus stop should be expanded
    public static int expBusStopIndex; // Index of the bus stop to be expanded
    public static int lastExpBusStopIndex; // Index of the last bus stop expanded
    public static boolean isExpBusStopIndexExpanded; // Is the bus stop expanded already?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops);

        // Gets the bus tag, stop titles, and stop times
        Intent intent = getIntent();
        busTag = intent.getStringExtra(AppData.BUS_TAG_MESSAGE);
        @SuppressWarnings("unchecked")
        ArrayList<BusStop> busStops = (ArrayList) intent.getParcelableArrayListExtra(AppData.BUS_STOPS_MESSAGE);

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

        // Set up swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.bus_stops_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                expansionRequest = false;
                updateBusTimes();
            }
        });

        // Sets up the list view
        expBusStopIndex = -1;
        isExpBusStopIndexExpanded = false;
        setListView(busStops);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long myLong) {
                expansionRequest = true;
                lastExpBusStopIndex = expBusStopIndex;
                expBusStopIndex = myItemInt;
                updateBusTimes();
            }
        });
    }

    // Updates the list view with bus stop titles and times
    public void setListView(ArrayList<BusStop> busStops) {
        ListView busTimesList = (ListView) findViewById(android.R.id.list);
        if (busTimesList.getAdapter() == null) {
            BusStopAdapter adapter = new BusStopAdapter(getApplicationContext(), R.layout.list_bus_stops, busStops);
            busTimesList.setAdapter(adapter);
        } else {
            ((BusStopAdapter) busTimesList.getAdapter()).refill(busStops);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    // Updates the bus times
    private void updateBusTimes() {
        new ShowBusStopsHelper().execute(busTag, this, getApplicationContext());
    }

    @Override
    public void onResume() {
        // Auto refreshes times every 60 seconds
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                expansionRequest = false;
                updateBusTimes();
                refreshHandler.postDelayed(this, 60000);
            }
        }, 60000);
        active = true; // Activity is active

        super.onResume();
    }

    @Override
    protected void onPause() {
        active = false; // Activity is not active
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.abc_shrink_fade_out_from_bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bus_stops, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
