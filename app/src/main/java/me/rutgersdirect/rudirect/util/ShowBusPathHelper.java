package me.rutgersdirect.rudirect.util;

import android.os.AsyncTask;

import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.fragment.BusMapFragment;

public class ShowBusPathHelper extends AsyncTask<Object, Void, Void> {

    private BusMapFragment busMapFragment;
    private String[][] busPathLats;
    private String[][] busPathLons;

    @Override
    protected Void doInBackground(Object... objects) {
        String tag = (String) objects[0];
        busMapFragment = (BusMapFragment) objects[1];
        busPathLats = NextBusAPI.getBusPathLats(tag);
        busPathLons = NextBusAPI.getBusPathLons(tag);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        busMapFragment.pathLats = busPathLats;
        busMapFragment.pathLons = busPathLons;
        busMapFragment.getMapAsync(busMapFragment);
    }
}