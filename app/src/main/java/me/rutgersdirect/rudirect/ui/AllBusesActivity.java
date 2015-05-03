package me.rutgersdirect.rudirect.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;

public class AllBusesActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_buses);

        // Setup list view of all buses
        listView = (ListView) findViewById(R.id.allBusesList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_black_text, R.id.list_content, BusConstants.allBusNames);
        listView.setAdapter(adapter);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                if (!BusStopsActivity.active) {
                    BusStopsActivity.active = true;
                    String bus = (String) (listView.getItemAtPosition(myItemInt));
                    SharedPreferences busesToTagsPref = getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
                    String busTag = busesToTagsPref.getString(bus, null);
                    new ShowBusStopsHelper().execute(busTag, AllBusesActivity.this, getApplicationContext());
                }
            }
        });

        // ActionBar setup
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_all_buses, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
