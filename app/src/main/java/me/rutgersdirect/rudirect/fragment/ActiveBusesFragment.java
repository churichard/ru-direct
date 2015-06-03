package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.util.ShowBusStopsHelper;

public class ActiveBusesFragment extends Fragment {
    private MainActivity mainActivity;
    private RelativeLayout rlLayout;
    private ListView listView;
    private TextView errorView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private class SetupListViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextBusAPI.getActiveBusTags();
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            SharedPreferences tagsToBusesPref = mainActivity.getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
            for (int i = 0; i < activeBusTags.length; i++) {
                activeBuses[i] = tagsToBusesPref.getString(activeBusTags[i], "Offline");
            }
            if (errorView != null) {
                rlLayout.removeView(errorView);
            }
            if (activeBusTags.length == 1 && activeBuses[0].equals("Offline")) {
                // Setup error message
                errorView = new TextView(mainActivity);
                errorView.setTextSize(24);

                RelativeLayout.LayoutParams params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (10 * scale + 0.5f);
                errorView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
                errorView.setLayoutParams(params);
                errorView.setGravity(Gravity.CENTER);
                if (isNetworkAvailable()) {
                    errorView.setText("No active buses.");
                } else {
                    errorView.setText("Unable to get active buses - check your Internet connection and try again.");
                }
                rlLayout.addView(errorView);

                // Clear listView
                listView.setAdapter(null);
            } else {
                // Set listView adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(),
                        R.layout.list_black_text, R.id.list_content, activeBuses);
                listView.setAdapter(adapter);

                // Setup item click listener
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
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // Sets up the list view
    public void setupListView() {
        new SetupListViewTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) super.getActivity();
        rlLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_active_buses, container, false);

        setHasOptionsMenu(true);

        return rlLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize listView
        listView = (ListView) mainActivity.findViewById(R.id.busList);
        // Set up swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.active_buses_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupListView();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_active_buses, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
