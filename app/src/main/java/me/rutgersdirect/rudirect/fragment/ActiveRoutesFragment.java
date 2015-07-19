package me.rutgersdirect.rudirect.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.SettingsActivity;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class ActiveRoutesFragment extends BaseRouteFragment {

    private RecyclerView activeBusesRecyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) mainActivity.findViewById(R.id.active_buses_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        setupRecyclerView();
        setupSwipeRefreshLayout();

        errorView = (TextView) mainActivity.findViewById(R.id.active_buses_error);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActiveRoutes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(mainActivity, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        activeBusesRecyclerView = (RecyclerView) mainActivity.findViewById(R.id.active_buses_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        activeBusesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        activeBusesRecyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        activeBusesRecyclerView.setAdapter(new BusRouteAdapter());
    }

    // Set up SwipeRefreshLayout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.active_buses_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateActiveRoutes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Sets up the RecyclerView
    public void updateActiveRoutes() {
        new UpdateActiveRoutesTask().execute();
    }

    private class UpdateActiveRoutesTask extends AsyncTask<Void, Void, String[]> {

        protected String[] doInBackground(Void... voids) {
            if (RUDirectApplication.getBusData().getBusTagToBusTitle() == null) {
                NextBusAPI.saveBusStops();
            }
            return NextBusAPI.getActiveBusTags();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            HashMap<String, String> busTagsToBusTitles = RUDirectApplication.getBusData().getBusTagToBusTitle();
            if (busTagsToBusTitles != null) {
                for (int i = 0; i < activeBusTags.length; i++) {
                    activeBuses[i] = busTagsToBusTitles.get(activeBusTags[i]);
                }
            }
            if (activeBusTags.length == 1 && activeBuses[0] == null) {
                // Setup error message
                errorView.setVisibility(View.VISIBLE);
                activeBusesRecyclerView.setAdapter(new BusRouteAdapter());
                if (RUDirectUtil.isNetworkAvailable()) {
                    errorView.setText("No active buses.");
                } else {
                    errorView.setText("Unable to get active routes - check your Internet connection and try again.");
                }
            } else {
                // Show active buses
                errorView.setVisibility(View.GONE);
                activeBusesRecyclerView.setAdapter(new BusRouteAdapter(activeBuses, mainActivity, ActiveRoutesFragment.this));
            }
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}