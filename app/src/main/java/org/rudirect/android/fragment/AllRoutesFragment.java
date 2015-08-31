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

import org.rudirect.android.activity.SettingsActivity;
import org.rudirect.android.adapter.MainPagerAdapter;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.interfaces.NetworkCallFinishListener;
import org.rudirect.android.ui.view.DividerItemDecoration;
import org.rudirect.android.R;
import org.rudirect.android.adapter.BusRouteAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.util.RUDirectUtil;

public class AllRoutesFragment extends BaseRouteFragment {

    private RecyclerView allBusesRecyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) mainActivity.findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        setupRecyclerView();
        setupSwipeRefreshLayout();
        errorView = (TextView) mainActivity.findViewById(R.id.all_buses_error);
        updateAllRoutes();
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
        allBusesRecyclerView = (RecyclerView) mainActivity.findViewById(R.id.all_buses_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        allBusesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        allBusesRecyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        allBusesRecyclerView.setAdapter(new BusRouteAdapter(RUDirectApplication.getBusData().getBusRoutes(), mainActivity, this));
    }

    // Set up SwipeRefreshLayout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.all_buses_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAllRoutes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Sets up the RecyclerView
    private void updateAllRoutes() {
        DirectionsFragment directionsFragment = (DirectionsFragment) MainPagerAdapter.getRegisteredFragment(1);
        new UpdateBusStopsAndPaths().execute(directionsFragment);
    }

    // Sets up the bus routes
    private class UpdateBusStopsAndPaths extends AsyncTask<NetworkCallFinishListener, Void, Void> {
        private NetworkCallFinishListener listener;

        protected Void doInBackground(NetworkCallFinishListener... listeners) {
            if (listeners.length != 0) {
                this.listener = listeners[0];
            }
            NextBusAPI.saveBusRoutes();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (RUDirectUtil.isNetworkAvailable()) {
                errorView.setVisibility(View.GONE);
                BusRouteAdapter adapter = ((BusRouteAdapter) allBusesRecyclerView.getAdapter());
                adapter.setBusRoutes(RUDirectApplication.getBusData().getBusRoutes());
                adapter.notifyDataSetChanged();
            } else {
                if (allBusesRecyclerView.getAdapter().getItemCount() == 0) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText("Unable to get routes. Check your Internet connection and try again.");
                }
                Snackbar.make(mainActivity.findViewById(R.id.all_routes_layout),
                        "No Internet connection. Please try again later.", Snackbar.LENGTH_SHORT).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            if (listener != null) {
                listener.onBusStopsUpdated();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            RUDirectApplication.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.all_routes_category))
                    .setAction(getString(R.string.view_action))
                    .build());
        }
    }
}