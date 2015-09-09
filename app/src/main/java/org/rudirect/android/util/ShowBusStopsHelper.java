package org.rudirect.android.util;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rudirect.android.R;
import org.rudirect.android.adapter.BusStopAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.fragment.BusTimesFragment;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, BusRoute> {

    private BusTimesFragment busTimesFragment;

    @Override
    protected BusRoute doInBackground(Object... objects) {
        BusRoute route = (BusRoute) objects[0];
        busTimesFragment = (BusTimesFragment) objects[1];

        // Save bus stop times and get the bus stops
        NextBusAPI.saveBusStopTimes(route);

        return route;
    }

    @Override
    protected void onPostExecute(BusRoute route) {
        RecyclerView busTimesRecyclerView = busTimesFragment.getBusTimesRecyclerView();
        BusStopAdapter busStopAdapter = (BusStopAdapter) busTimesRecyclerView.getAdapter();
        TextView noInternetTextView = busTimesFragment.getNoInternetTextView();

        if (RUDirectUtil.isNetworkAvailable()) { // If there's Internet, update bus stops
            noInternetTextView.setVisibility(View.GONE);
            busStopAdapter.setBusStops(route.getBusStops());
            busStopAdapter.notifyDataSetChanged();
        } else {
            noInternetTextView.setText(RUDirectApplication.getContext().getString(R.string.no_internet_text));
            if (route.getLastUpdatedTime() != 0) {
                noInternetTextView.append(" - last updated " + RUDirectUtil.getTimeDiff(route.getLastUpdatedTime()));
            }
            noInternetTextView.setVisibility(View.VISIBLE);
            // If there's no bus stops shown, show them and set them as offline
            if (busTimesRecyclerView.getAdapter().getItemCount() == 0) {
                busStopAdapter.setBusStops(route.getBusStops());
                busStopAdapter.notifyDataSetChanged();
            }
        }

        // Update progress bar and swipe refresh layout
        busTimesFragment.getProgressBar().setVisibility(View.GONE);
        busTimesFragment.getSwipeRefreshLayout().setRefreshing(false);
    }
}