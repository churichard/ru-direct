package org.rudirect.android.util;

import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.rudirect.android.activity.BusStopsActivity;
import org.rudirect.android.adapter.BusStopAdapter;
import org.rudirect.android.api.NextBusAPI;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.fragment.BusTimesFragment;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, Void> {

    private Fragment fragment;
    private BusStop[] busStops;

    @Override
    protected Void doInBackground(Object... objects) {
        String tag = (String) objects[0];
        fragment = (Fragment) objects[1];

        // Save bus stop times and get the bus stops
        NextBusAPI.saveBusStopTimes(tag);
        busStops = NextBusAPI.getBusStops(tag);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (fragment instanceof BusTimesFragment) {
            // Update items in RecyclerView
            BusTimesFragment busTimesFragment = ((BusTimesFragment) fragment);
            RecyclerView busTimesRecyclerView = busTimesFragment.getBusTimesRecyclerView();

            // Update bus stops
            BusStopAdapter busStopAdapter = (BusStopAdapter) busTimesRecyclerView.getAdapter();
            busStopAdapter.setBusStops(busStops);
            busStopAdapter.notifyDataSetChanged();
            if (busTimesFragment.isAdded()) {
                ((BusStopsActivity) busTimesFragment.getActivity()).setBusStops(busStops);
            }

            // Update progress bar and swipe refresh layout
            busTimesFragment.getProgressBar().setVisibility(View.GONE);
            busTimesFragment.getSwipeRefreshLayout().setRefreshing(false);
        }
    }
}