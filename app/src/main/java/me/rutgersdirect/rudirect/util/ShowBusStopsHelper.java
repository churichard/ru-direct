package me.rutgersdirect.rudirect.util;

import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.adapter.BusStopAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.fragment.BusTimesFragment;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, Void> {

    private Fragment fragment;
    private BusStop[] busStops;

    @Override
    protected Void doInBackground(Object... objects) {
        String tag = (String) objects[0];
        fragment = (Fragment) objects[1];

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

            busTimesRecyclerView.setAdapter(new BusStopAdapter(busStops));
            if (busTimesFragment.isAdded()) {
                ((BusStopsActivity) busTimesFragment.getActivity()).setBusStops(busStops);
            }
            busTimesFragment.getSwipeRefreshLayout().setRefreshing(false);
        }
    }
}