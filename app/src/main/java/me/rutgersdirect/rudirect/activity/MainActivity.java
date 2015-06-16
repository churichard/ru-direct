package me.rutgersdirect.rudirect.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.MainPagerAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.AppData;


public class MainActivity extends AppCompatActivity {

    // Sets up the bus routes
    private class SetupBusRoutesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            NextBusAPI.saveBusStops(getApplicationContext());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize shared preferences
        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
        SharedPreferences busesToTagsPref = getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
        if (!tagsToBusesPref.contains(AppData.allBusTags[0])) {
            // Save bus routes
            new SetupBusRoutesTask().execute();
            // Save tags to buses and buses to tags hash maps
            SharedPreferences.Editor tagsToBusesEdit = tagsToBusesPref.edit();
            SharedPreferences.Editor busesToTagsEdit = busesToTagsPref.edit();
            for (int i = 0; i < AppData.allBusNames.length; i++) {
                tagsToBusesEdit.putString(AppData.allBusTags[i], AppData.allBusNames[i]);
                busesToTagsEdit.putString(AppData.allBusNames[i], AppData.allBusTags[i]);
            }
            tagsToBusesEdit.apply();
            busesToTagsEdit.apply();
        }
        // Initialize bus tags to stop times hash map
        AppData.BUS_TAGS_TO_STOP_TIMES = new HashMap<>();

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new MainPagerAdapter(getFragmentManager()));

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}