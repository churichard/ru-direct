package me.rutgersdirect.rudirect.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.adapter.MainPagerAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.interfaces.UpdateBusStopsListener;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;
import me.rutgersdirect.rudirect.util.RUDirectUtil;

public class AllRoutesFragment extends BaseRouteFragment {

    private RecyclerView allBusesRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSwipeRefreshLayout();
        errorView = (TextView) mainActivity.findViewById(R.id.all_buses_error);
        updateAllRoutes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            updateAllRoutes();
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
        allBusesRecyclerView.setAdapter(new BusRouteAdapter(RUDirectUtil.mapKeySetToSortedArray(
                RUDirectApplication.getBusData().getBusTitlesToBusTags()), mainActivity, this));
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
        MainPagerAdapter adapter = (MainPagerAdapter) mainActivity.getViewPager().getAdapter();
        DirectionsFragment directionsFragment = (DirectionsFragment) adapter.getRegisteredFragment(1);
        new UpdateBusStopsAndPaths().execute(directionsFragment);
    }

    // Sets up the bus routes
    private class UpdateBusStopsAndPaths extends AsyncTask<UpdateBusStopsListener, Void, Void> {
        private UpdateBusStopsListener listener;

        protected Void doInBackground(UpdateBusStopsListener... listeners) {
            if (listeners.length != 0) {
                this.listener = listeners[0];
            }
            NextBusAPI.saveBusStops();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (!RUDirectUtil.isNetworkAvailable() && allBusesRecyclerView.getAdapter().getItemCount() == 0) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setText("Unable to get routes - check your Internet connection and try again.");
            } else {
                errorView.setVisibility(View.GONE);
                allBusesRecyclerView.setAdapter(new BusRouteAdapter(RUDirectUtil.mapKeySetToSortedArray(
                                RUDirectApplication.getBusData().getBusTitlesToBusTags()), mainActivity, AllRoutesFragment.this));
            }
            mSwipeRefreshLayout.setRefreshing(false);

            if (listener != null) {
                listener.onBusStopsUpdate();
            }
        }
    }
}