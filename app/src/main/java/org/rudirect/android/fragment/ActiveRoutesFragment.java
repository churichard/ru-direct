package org.rudirect.android.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import org.rudirect.android.R;
import org.rudirect.android.activity.SettingsActivity;
import org.rudirect.android.adapter.BusRouteAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.ui.view.DividerItemDecoration;
import org.rudirect.android.util.RUDirectUtil;

import java.util.ArrayList;

public class ActiveRoutesFragment extends BaseRouteFragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) mainActivity.findViewById(R.id.active_routes_progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        setupRecyclerView();
        setupSwipeRefreshLayout();

        errorView = (TextView) mainActivity.findViewById(R.id.active_routes_error);
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
        recyclerView = (RecyclerView) mainActivity.findViewById(R.id.active_routes_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        // Setup layout
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        recyclerView.setAdapter(new BusRouteAdapter(mainActivity, this));
    }

    // Set up SwipeRefreshLayout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.active_routes_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateActiveRoutes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Returns the RecyclerView
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    // Update active routes
    public void updateActiveRoutes() {
        new UpdateActiveRoutesTask().execute();
    }

    private class UpdateActiveRoutesTask extends AsyncTask<Void, Void, ArrayList<BusRoute>> {

        protected ArrayList<BusRoute> doInBackground(Void... voids) {
            if (RUDirectApplication.getBusData().getBusTagsToBusRoutes() == null) {
                NextBusAPI.saveBusRoutes();
            }
            return NextBusAPI.getActiveRoutes();
        }

        protected void onPostExecute(ArrayList<BusRoute> activeRoutes) {
            BusRouteAdapter adapter = (BusRouteAdapter) recyclerView.getAdapter();
            if (activeRoutes == null) {
                // Setup error message
                errorView.setVisibility(View.VISIBLE);
                adapter.setBusRoutes(null);
                adapter.notifyDataSetChanged();
                if (RUDirectUtil.isNetworkAvailable()) {
                    errorView.setText("No active buses.");
                } else {
                    errorView.setText("Unable to get active routes. Check your Internet connection and try again.");
                    Snackbar.make(mainActivity.findViewById(R.id.active_routes_layout),
                            "No Internet connection. Please try again later.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                // Show active buses
                errorView.setVisibility(View.GONE);
                adapter.setBusRoutes(activeRoutes);
                adapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            RUDirectApplication.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.active_routes_category))
                    .setAction(getString(R.string.view_action))
                    .build());
        }
    }
}