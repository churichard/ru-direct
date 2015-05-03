package me.rutgersdirect.rudirect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;
import me.rutgersdirect.rudirect.model.BusStop;

public class BusStopsActivity extends AppCompatActivity {
    public static boolean active;
    private String busTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_stops);

        Intent intent = getIntent();
        busTag = intent.getStringExtra(BusConstants.BUS_TAG_MESSAGE);
        String[] busStopTitles = intent.getStringArrayExtra(BusConstants.BUS_STOP_TITLES_MESSAGE);
        String[] busStopTimes = intent.getStringArrayExtra(BusConstants.BUS_STOP_TIMES_MESSAGE);

        setListView(busStopTitles, busStopTimes);

        // ActionBar setup
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Updates the list view with bus stop titles and times
    public void setListView(String[] titles, String[] times) {
        ArrayList<BusStop> buses = new ArrayList<>(titles.length);
        for (int i = 0; i < titles.length; i++) {
            buses.add(new BusStop(null, titles[i], times[i]));
        }

        ListView busTimesList = (ListView) findViewById(android.R.id.list);
        BusStopAdapter adapter = new BusStopAdapter(getApplicationContext(), R.layout.list_bus_stops, buses);
        busTimesList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timer autoUpdate = new Timer(); //Auto Update Setup
        autoUpdate.schedule(new TimerTask() {
            public void run() {
                updateBusTimes();
            }
        }, 0, 60000); //Updates every minute nonetheless
        active = true;
    }

    // Updates the bus times
    private void updateBusTimes() {
        new ShowBusStopsHelper().execute(busTag, BusStopsActivity.this, getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bus_times, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.refresh) {
            updateBusTimes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
