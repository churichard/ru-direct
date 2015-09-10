package org.rudirect.android.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import org.rudirect.android.R;
import org.rudirect.android.activity.RouteActivity;
import org.rudirect.android.adapter.BusTimesAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.ui.view.DividerItemDecoration;
import org.rudirect.android.util.RUDirectUtil;

public class BusTimesFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private static final int REFRESH_INTERVAL = 60000;

    private RouteActivity routeActivity;
    private Handler refreshHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView busTimesRecyclerView;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private TextView noInternetBanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeActivity = (RouteActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_times, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appBarLayout = (AppBarLayout) routeActivity.findViewById(R.id.appbar);
        progressBar = (ProgressBar) routeActivity.findViewById(R.id.routes_progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        noInternetBanner = (TextView) routeActivity.findViewById(R.id.no_internet_banner);
        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update bus times
        BusTimesAdapter.setExpToggleRequest(false);
        updateBusTimes();

        // Auto refreshes times every REFRESH_INTERVAL seconds
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BusTimesAdapter.setExpToggleRequest(false);
                updateBusTimes();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        }, REFRESH_INTERVAL);

        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    // Updates the bus times
    private void updateBusTimes() {
        new UpdateBusTimes().execute();
    }

    // Set up swipe refresh layout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) routeActivity.findViewById(R.id.bus_stops_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BusTimesAdapter.setExpToggleRequest(false);
                updateBusTimes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        busTimesRecyclerView = (RecyclerView) routeActivity.findViewById(R.id.bus_times_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(routeActivity);
        busTimesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        busTimesRecyclerView.addItemDecoration(new DividerItemDecoration(routeActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        busTimesRecyclerView.setAdapter(new BusTimesAdapter());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            BusTimesAdapter.setExpToggleRequest(false);
            updateBusTimes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private class UpdateBusTimes extends AsyncTask<Void, Void, BusRoute> {

        @Override
        protected BusRoute doInBackground(Void... voids) {
            BusRoute route = routeActivity.getRoute();
            NextBusAPI.saveBusStopTimes(route);
            return route;
        }

        @Override
        protected void onPostExecute(BusRoute route) {
            BusTimesAdapter busTimesAdapter = (BusTimesAdapter) busTimesRecyclerView.getAdapter();

            if (RUDirectUtil.isNetworkAvailable()) { // If there's Internet, update bus stops
                noInternetBanner.setVisibility(View.GONE);
                busTimesAdapter.setBusStops(route.getBusStops());
                busTimesAdapter.notifyDataSetChanged();
            } else {
                noInternetBanner.setText(RUDirectApplication.getContext().getString(R.string.no_internet_text));
                if (route.getLastUpdatedTime() != 0) {
                    noInternetBanner.append(" - last updated "
                            + RUDirectUtil.getTimeDiff(route.getLastUpdatedTime()));
                }
                noInternetBanner.setVisibility(View.VISIBLE);
                // If there's no bus stops shown, show them and set them as offline
                if (busTimesRecyclerView.getAdapter().getItemCount() == 0) {
                    busTimesAdapter.setBusStops(route.getBusStops());
                    busTimesAdapter.notifyDataSetChanged();
                }
            }

            // Update progress bar and swipe refresh layout
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            RUDirectApplication.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.route_times_category))
                    .setAction(getString(R.string.view_action))
                    .setLabel(routeActivity.getTitle().toString())
                    .build());
        }
    }
}