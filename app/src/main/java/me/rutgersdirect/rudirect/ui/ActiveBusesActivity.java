package me.rutgersdirect.rudirect.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

public class ActiveBusesActivity extends ActionBarActivity {
    private ListView listView;

    private class SetupListViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextBusAPI.getActiveBusTags();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            for (int i = 0; i < activeBusTags.length; i++) {
                activeBuses[i] = BusConstants.TAGS_TO_BUSES.get(activeBusTags[i]);
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
                            BusStopsActivity.active = true;
                            String bus = (String) (listView.getItemAtPosition(myItemInt));
                            String busTag = BusConstants.BUSES_TO_TAGS.get(bus);
                            new ShowBusStopsHelper().execute(busTag, ActiveBusesActivity.this);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_buses);

        // Setup the list view
        new SetupListViewTask().execute();

//        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
//        SharedPreferences busesToTagsPref = getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
//        SharedPreferences.Editor tagsToBusesEdit = tagsToBusesPref.edit();
//        SharedPreferences.Editor busesToTagsEdit = busesToTagsPref.edit();
//        for (int i = 0; i < BusConstants.allBusNames.length; i++) {
//            tagsToBusesEdit.putString(BusConstants.allBusTags[i], BusConstants.allBusNames[i]);
//            busesToTagsEdit.putString(BusConstants.allBusTags[i], BusConstants.allBusNames[i]);
//        }
//        tagsToBusesEdit.apply();
//        busesToTagsEdit.apply();

        // Initialize hash maps
        BusConstants.TAGS_TO_BUSES = new HashMap<>();
        BusConstants.BUSES_TO_TAGS = new HashMap<>();
        BusConstants.BUS_TAGS_TO_STOP_TAGS = new HashMap<>();
        BusConstants.BUS_TAGS_TO_STOP_TITLES = new HashMap<>();
        for (int i = 0; i < BusConstants.allBusNames.length; i++) {
            BusConstants.TAGS_TO_BUSES.put(BusConstants.allBusTags[i], BusConstants.allBusNames[i]);
            BusConstants.BUSES_TO_TAGS.put(BusConstants.allBusNames[i], BusConstants.allBusTags[i]);
        }

        // ActionBar setup
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
