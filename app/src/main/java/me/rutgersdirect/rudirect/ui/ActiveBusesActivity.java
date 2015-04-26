package me.rutgersdirect.rudirect.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.HashMap;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.api.NextbusAPI;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;

public class ActiveBusesActivity extends ActionBarActivity {
    private ListView listView;

    private class SetupListViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextbusAPI.getActiveBusTags();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = {"No active buses"};
            if (activeBusTags.length != 0) {
                activeBuses = new String[activeBusTags.length];
                for (int i = 0; i < activeBusTags.length; i++) {
                    activeBuses[i] = BusConstants.TAGS_TO_BUSES.get(activeBusTags[i]);
                }
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
                        String bus = (String) (listView.getItemAtPosition(myItemInt));
                        String busTag = BusConstants.BUSES_TO_TAGS.get(bus);
                        new ShowBusStopsHelper().execute(busTag, ActiveBusesActivity.this);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_buses);
        setTitle("RU Direct");

        // Setup the list view
        new SetupListViewTask().execute();

        // Initialize hash maps
        BusConstants.TAGS_TO_BUSES = new HashMap<>();
        BusConstants.BUSES_TO_TAGS = new HashMap<>();
        BusConstants.TAGS_TO_STOPS = new HashMap<>();
        BusConstants.TITLES_TO_STOPS = new HashMap<>();
        for (int i = 0; i < BusConstants.allBusNames.length; i++) {
            BusConstants.TAGS_TO_BUSES.put(BusConstants.allBusTags[i], BusConstants.allBusNames[i]);
            BusConstants.BUSES_TO_TAGS.put(BusConstants.allBusNames[i], BusConstants.allBusTags[i]);
        }

        // Setup refresh button
        final Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SetupListViewTask().execute();
            }
        });

        // Setup all buses button
        Button allBusesButton = (Button) findViewById(R.id.allBuses);
        allBusesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start new activity to display all buses
                Intent intent = new Intent(ActiveBusesActivity.this, AllBusesActivity.class);
                ActiveBusesActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }
}
