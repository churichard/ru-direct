package me.rutgersdirect.rudirect.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;
import me.rutgersdirect.rudirect.model.BusStop;

public class BusStopsActivity extends Activity {
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

        // Setup refresh button
        Button refresh = (Button) findViewById(R.id.refreshTimes);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ShowBusStopsHelper().execute(busTag, BusStopsActivity.this);
            }
        });
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
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_times, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh) {
            new ShowBusStopsHelper().execute(busTag, BusStopsActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}