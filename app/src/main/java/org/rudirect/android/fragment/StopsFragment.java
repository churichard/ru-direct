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
import org.rudirect.android.interfaces.NetworkCallFinishListener;
import org.rudirect.android.ui.view.DividerItemDecoration;
import org.rudirect.android.util.RUDirectUtil;

import java.util.ArrayList;

public class StopsFragment extends BaseMainFragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noInternetBanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noInternetBanner = (TextView) mainActivity.findViewById(R.id.no_internet_banner);
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.routes_progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        setupRecyclerView();
        setupSwipeRefreshLayout();

        // TODO Move this into Stops when it's completed
//        DirectionsFragment directionsFragment = (DirectionsFragment) MainPagerAdapter.getRegisteredFragment(1);
//        new UpdateBusRoutesTask().execute(directionsFragment);
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
        recyclerView = (RecyclerView) mainActivity.findViewById(R.id.routes_recyclerview);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.routes_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateActiveRoutes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Update active routes
    public void updateActiveRoutes() {
        new UpdateActiveRoutesTask().execute();
    }

    private class UpdateBusRoutesTask extends AsyncTask<NetworkCallFinishListener, Void, Void> {
        private NetworkCallFinishListener listener;

        protected Void doInBackground(NetworkCallFinishListener... listeners) {
            listener = listeners[0];
            NextBusAPI.saveBusRoutes();
            return null;
        }

        protected void onPostExecute(Void v) {
            listener.onBusStopsUpdated();
        }
    }

    private class UpdateActiveRoutesTask extends AsyncTask<Void, Void, ArrayList<BusRoute>> {

        protected ArrayList<BusRoute> doInBackground(Void... voids) {
            if (RUDirectApplication.getBusData().getBusRoutes() == null) {
                NextBusAPI.saveBusRoutes();
            }
            return NextBusAPI.getActiveRoutes();
        }

        protected void onPostExecute(ArrayList<BusRoute> activeRoutes) {
            BusRouteAdapter adapter = (BusRouteAdapter) recyclerView.getAdapter();
            if (RUDirectUtil.isNetworkAvailable()) {
                noInternetBanner.setVisibility(View.GONE);
                if (activeRoutes == null || activeRoutes.size() == 0) {
                    activeRoutes = new ArrayList<>();
                    activeRoutes.add(new BusRoute("No active routes."));
                } else {
                    // Set active and inactive routes
                    adapter.setActiveRoutes(activeRoutes);
                    ArrayList<BusRoute> inactiveRoutes = RUDirectApplication.getBusData().getBusRoutes();
                    inactiveRoutes.removeAll(activeRoutes);
                    if (inactiveRoutes.size() == 0) {
                        inactiveRoutes.add(new BusRoute("No inactive routes."));
                    }
                    adapter.setInactiveRoutes(inactiveRoutes);
                    adapter.notifyDataSetChanged();
                }
            } else {
                // Show error
                noInternetBanner.setVisibility(View.VISIBLE);
                if (recyclerView.getAdapter().getItemCount() == 0) {
                    ArrayList<BusRoute> allRoutes = RUDirectApplication.getBusData().getBusRoutes();
                    if (allRoutes != null) {
                        adapter.setActiveRoutes(allRoutes);
                        adapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(mainActivity.findViewById(R.id.routes_layout),
                                "No Internet connection. Please try again later.", Snackbar.LENGTH_SHORT).show();
                    }
                }
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