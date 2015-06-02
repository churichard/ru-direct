package me.rutgersdirect.rudirect.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.AppData;
import me.rutgersdirect.rudirect.model.BusStop;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, Void> {
    private String tag;
    private Activity activity;
    private String[] busStopTitles;
    private int[][] busStopTimes;

    @Override
    protected Void doInBackground(Object... objects) {
        tag = (String) objects[0];
        activity = (Activity) objects[1];
        Context context = (Context) objects[2];
        busStopTitles = NextBusAPI.getBusStopTitles(tag, context);
        busStopTimes = NextBusAPI.getBusStopTimes(tag, context);
        return null;
    }

    // Return an ArrayList of BusStops given the titles and the times
    private ArrayList<BusStop> getBusStops(String[] titles, int[][] times) {
        ArrayList<BusStop> buses = new ArrayList<>(busStopTitles.length);
        for (int i = 0; i < busStopTitles.length; i++) {
            buses.add(new BusStop(tag, titles[i], times[i]));
        }
        return buses;
    }

    @Override
    protected void onPostExecute(Void v) {
        ArrayList<BusStop> buses = getBusStops(busStopTitles, busStopTimes);
        if (activity instanceof BusStopsActivity) {
            // Update bus stop titles and times
            ((BusStopsActivity) activity).setListView(buses);
        } else {
            // Start new activity to display bus stop titles and times
            // Create intent
            Intent intent = new Intent(activity, BusStopsActivity.class);
            intent.putExtra(AppData.BUS_TAG_MESSAGE, tag);
            intent.putParcelableArrayListExtra(AppData.BUS_STOPS_MESSAGE, buses);
            // Start activity
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
        }
    }
}