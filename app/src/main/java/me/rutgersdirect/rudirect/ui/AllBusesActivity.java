package me.rutgersdirect.rudirect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.SetupBusStopsAndTimes;

public class AllBusesActivity extends ActionBarActivity {
    private ListView listView;
    private HashMap<String, String> activeBusTitlesAndTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_buses);

        // Get active bus tags and titles
        Intent intent = getIntent();
        activeBusTitlesAndTags = (HashMap<String, String>) intent.getSerializableExtra(BusConstants.ACTIVE_BUSES);

        // Setup list view of all buses
        listView = (ListView) findViewById(R.id.allBusesList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_black_text, R.id.list_content, BusConstants.allBuses);
        listView.setAdapter(adapter);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String bus = (String) (listView.getItemAtPosition(myItemInt));
                String busTag = BusConstants.allBusTags[java.util.Arrays.asList(BusConstants.allBuses).indexOf(bus)];
                new SetupBusStopsAndTimes().execute(busTag, AllBusesActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_buses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
