package me.rutgersdirect.rudirect.util;

import android.app.Activity;
import android.os.AsyncTask;

import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.fragment.BusMapFragment;

public class ShowBusPathHelper extends AsyncTask<Object, Void, Void> {

    private BusMapFragment busMapFragment;
    private String[] busStopLats;
    private String[] busStopLons;
    private String[][] busPathLats;
    private String[][] busPathLons;

    @Override
    protected Void doInBackground(Object... objects) {
        String tag = (String) objects[0];
        Activity activity = (Activity) objects[1];
        busMapFragment = (BusMapFragment) objects[2];
        busStopLats = NextBusAPI.getBusStopLats(tag, activity);
        busStopLons = NextBusAPI.getBusStopLons(tag, activity);
        busPathLats = NextBusAPI.getBusPathLats(tag, activity);
        busPathLons = NextBusAPI.getBusPathLons(tag, activity);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        busMapFragment.latitudes = busStopLats;
        busMapFragment.longitudes = busStopLons;
        busMapFragment.pathLats = busPathLats;
        busMapFragment.pathLons = busPathLons;
        busMapFragment.getMapAsync(busMapFragment);
    }
}