package me.rutgersdirect.rudirect.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.ui.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.ui.activity.MainActivity;
import me.rutgersdirect.rudirect.ui.helper.ShowBusStopsHelper;

public class ActiveBusesFragment extends Fragment {
    private MainActivity mainActivity;
    private ListView listView;

    private class SetupListViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextBusAPI.getActiveBusTags();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            SharedPreferences tagsToBusesPref = mainActivity.getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
            for (int i = 0; i < activeBusTags.length; i++) {
                activeBuses[i] = tagsToBusesPref.getString(activeBusTags[i], "No active buses");
                /* TODO: Return a message if Internet connection isn't active */
            }

            // Setup list view
            listView = (ListView) mainActivity.findViewById(R.id.busList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, activeBuses);
            listView.setAdapter(adapter);

            // Setup item click listener
            if (activeBusTags.length != 0) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        if (!BusStopsActivity.active) {
                            String bus = (String) (listView.getItemAtPosition(myItemInt));
                            SharedPreferences busesToTagsPref = mainActivity.getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
                            String busTag = busesToTagsPref.getString(bus, null);
                            if (busTag != null) {
                                BusStopsActivity.active = true;
                                new ShowBusStopsHelper().execute(busTag, mainActivity, mainActivity.getApplicationContext());
                            }
                        }
                    }
                });
            }
        }
    }

    // Sets up the list view
    public void setupListView() {
        new SetupListViewTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) super.getActivity();
        RelativeLayout rlLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_active_buses, container, false);

        setHasOptionsMenu(true);

        return rlLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_active_buses, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.refresh) {
            setupListView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
