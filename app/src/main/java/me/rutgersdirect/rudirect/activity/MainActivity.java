package me.rutgersdirect.rudirect.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.adapter.MainPagerAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.fragment.AllRoutesFragment;

public class MainActivity extends AppCompatActivity {

    // Sets up the bus routes
    private class SetupBusRoutesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            NextBusAPI.saveBusStops(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            AllRoutesFragment.allBusesRecyclerView.setAdapter(new BusRouteAdapter(getBusRoutes(), MainActivity.this, null));
        }
    }

    public String[] getBusRoutes() {
        Map<String, ?> busesToTagsMap = getSharedPreferences(
                getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE).getAll();
        Object[] busNamesObj = busesToTagsMap.keySet().toArray();
        String[] busNames = Arrays.copyOf(busNamesObj, busNamesObj.length, String[].class);
        Arrays.sort(busNames);
        return busNames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize shared preferences
        SharedPreferences tagsToBusesPref = getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
        // Save bus routes
        if (tagsToBusesPref.getAll().size() == 0) {
            new SetupBusRoutesTask().execute();
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