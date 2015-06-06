package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.adapter.BusStopAdapter;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;
import me.rutgersdirect.rudirect.util.ShowBusStopsHelper;


public class BusTimesFragment extends Fragment {

    private BusStopsActivity busStopsActivity;
    private Handler refreshHandler;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    // Bus stop times expansion
    public static boolean expansionRequest; // Whether or not the bus stop should be expanded
    public static int expBusStopIndex; // Index of the bus stop to be expanded
    public static int lastExpBusStopIndex; // Index of the last bus stop expanded
    public static boolean isExpBusStopIndexExpanded; // Is the bus stop expanded already?

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        busStopsActivity = (BusStopsActivity) getActivity();
        return inflater.inflate(R.layout.fragment_bus_times, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefreshLayout();
        updateBusTimes();
    }

    @Override
    public void onResume() {
        // Auto refreshes times every 60 seconds
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                expansionRequest = false;
                updateBusTimes();
                refreshHandler.postDelayed(this, 60000);
            }
        }, 60000);
        super.onResume();
    }

    // Updates the bus times
    private void updateBusTimes() {
        mSwipeRefreshLayout.setRefreshing(true);
        new ShowBusStopsHelper().execute(busStopsActivity.getBusTag(), busStopsActivity, BusTimesFragment.this);
    }

    // Set up swipe refresh layout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) busStopsActivity.findViewById(R.id.bus_stops_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                expansionRequest = false;
                updateBusTimes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor);
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        expBusStopIndex = -1;
        isExpBusStopIndexExpanded = false;

        // Initialize recycler view
        RecyclerView busTimesRecyclerView = (RecyclerView) busStopsActivity.findViewById(R.id.bus_times_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(busStopsActivity);
        busTimesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        busTimesRecyclerView.addItemDecoration(new DividerItemDecoration(busStopsActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        busTimesRecyclerView.setAdapter(new BusStopAdapter());
    }
}