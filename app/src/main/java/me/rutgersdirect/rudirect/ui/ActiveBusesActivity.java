package me.rutgersdirect.rudirect.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;

public class ActiveBusesActivity extends AppCompatActivity {
    private ListView listView;

    private class SetupListViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextBusAPI.getActiveBusTags();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
            for (int i = 0; i < activeBusTags.length; i++) {
                activeBuses[i] = tagsToBusesPref.getString(activeBusTags[i], "No active buses\nCheck your Internet connection");
            }

            // Setup list view
            listView = (ListView) findViewById(R.id.busList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, activeBuses);
            listView.setAdapter(adapter);

            // Setup item click listener
            if (activeBusTags.length != 0) {
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        if (!BusStopsActivity.active) {
                            String bus = (String) (listView.getItemAtPosition(myItemInt));
                            SharedPreferences busesToTagsPref = getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
                            String busTag = busesToTagsPref.getString(bus, null);
                            if (busTag != null) {
                                BusStopsActivity.active = true;
                                new ShowBusStopsHelper().execute(busTag, ActiveBusesActivity.this, getApplicationContext());
                            }
                        }
                    }
                });
            }
        }
    }

    // Sets up the bus routes
    private class SetupBusRoutes extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            NextBusAPI.saveBusStops(getApplicationContext());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_buses);
        setTitle("Active Buses");

        // Setup the list view
        new SetupListViewTask().execute();

        // Initialize shared preferences
        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
        SharedPreferences busesToTagsPref = getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
        if (!tagsToBusesPref.contains(BusConstants.allBusTags[0])) {
            // Save bus routes
            new SetupBusRoutes().execute();
            // Save tags to buses and buses to tags hash maps
            SharedPreferences.Editor tagsToBusesEdit = tagsToBusesPref.edit();
            SharedPreferences.Editor busesToTagsEdit = busesToTagsPref.edit();
            for (int i = 0; i < BusConstants.allBusNames.length; i++) {
                tagsToBusesEdit.putString(BusConstants.allBusTags[i], BusConstants.allBusNames[i]);
                busesToTagsEdit.putString(BusConstants.allBusNames[i], BusConstants.allBusTags[i]);
            }
            tagsToBusesEdit.apply();
            busesToTagsEdit.apply();
        }

        // Initialize bus tags to stop times hash map
        BusConstants.BUS_TAGS_TO_STOP_TIMES = new HashMap<>();

        // ActionBar setup
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.refresh) {
            new SetupListViewTask().execute();
            return true;
        }

        if (id == R.id.allBus) {
            Intent intent = new Intent(ActiveBusesActivity.this, AllBusesActivity.class);
            ActiveBusesActivity.this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
