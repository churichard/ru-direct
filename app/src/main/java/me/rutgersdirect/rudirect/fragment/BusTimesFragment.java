package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
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

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.adapter.BusStopAdapter;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;
import me.rutgersdirect.rudirect.util.RUDirectUtil;
import me.rutgersdirect.rudirect.util.ShowBusStopsHelper;

public class BusTimesFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private static final int REFRESH_INTERVAL = 60000;

    private BusStopsActivity busStopsActivity;
    private Handler refreshHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView busTimesRecyclerView;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStopsActivity = (BusStopsActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_times, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appBarLayout = (AppBarLayout) busStopsActivity.findViewById(R.id.appbar);
        progressBar = (ProgressBar) busStopsActivity.findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update bus times
        BusStopAdapter.setExpToggleRequest(false);
        updateBusTimes();

        // Auto refreshes times every REFRESH_INTERVAL seconds
        refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BusStopAdapter.setExpToggleRequest(false);
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
        TextView noInternetTextView = (TextView) busStopsActivity.findViewById(R.id.no_internet_textview);
        if (RUDirectUtil.isNetworkAvailable()) {
            if (noInternetTextView != null) {
                noInternetTextView.setVisibility(View.GONE);
            }
            new ShowBusStopsHelper().execute(busStopsActivity.getBusTag(), BusTimesFragment.this);
        } else {
            if (noInternetTextView != null) {
                noInternetTextView.setVisibility(View.VISIBLE);
            }
            if (busTimesRecyclerView.getAdapter().getItemCount() == 0) {
                new ShowBusStopsHelper().execute(busStopsActivity.getBusTag(), BusTimesFragment.this);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    // Set up swipe refresh layout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) busStopsActivity.findViewById(R.id.bus_stops_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BusStopAdapter.setExpToggleRequest(false);
                updateBusTimes();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        busTimesRecyclerView = (RecyclerView) busStopsActivity.findViewById(R.id.bus_times_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(busStopsActivity);
        busTimesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        busTimesRecyclerView.addItemDecoration(new DividerItemDecoration(busStopsActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        busTimesRecyclerView.setAdapter(new BusStopAdapter());
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
            BusStopAdapter.setExpToggleRequest(false);
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

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public RecyclerView getBusTimesRecyclerView() {
        return busTimesRecyclerView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}