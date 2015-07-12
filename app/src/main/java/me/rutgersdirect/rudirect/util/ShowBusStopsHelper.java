package me.rutgersdirect.rudirect.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.adapter.BusStopAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.constants.AppData;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.fragment.BusTimesFragment;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, Void> {

    private String tag;
    private Activity activity;
    private Fragment fragment;
    private BusStop[] busStops;

    @Override
    protected Void doInBackground(Object... objects) {
        tag = (String) objects[0];
        activity = (Activity) objects[1];
        fragment = (Fragment) objects[2];
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
            busTimesFragment.getSwipeRefreshLayout().setRefreshing(false);
        } else {
            // Start new activity to display bus stop titles and times
            Intent intent = new Intent(activity, BusStopsActivity.class);

            intent.putExtra(AppData.BUS_TAG_MESSAGE, tag);
            intent.putExtra(AppData.BUS_STOPS_MESSAGE, busStops);

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
        }
    }
}