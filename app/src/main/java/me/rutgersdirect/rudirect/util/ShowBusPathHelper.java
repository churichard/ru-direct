package me.rutgersdirect.rudirect.util;

import android.os.AsyncTask;

import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.data.model.BusPathSegment;
import me.rutgersdirect.rudirect.fragment.BusMapFragment;

public class ShowBusPathHelper extends AsyncTask<Object, Void, Void> {

    private BusMapFragment busMapFragment;
    private BusPathSegment[] segments;

    @Override
    protected Void doInBackground(Object... objects) {
        String tag = (String) objects[0];
        busMapFragment = (BusMapFragment) objects[1];
        segments = NextBusAPI.getBusPathSegments(tag);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        busMapFragment.setPathSegments(segments);
        busMapFragment.getMapAsync(busMapFragment);
    }
}