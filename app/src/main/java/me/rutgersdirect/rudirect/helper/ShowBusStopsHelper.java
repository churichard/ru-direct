package me.rutgersdirect.rudirect.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.api.NextbusAPI;
import me.rutgersdirect.rudirect.ui.BusStopsActivity;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, String[][]> {
    private String tag;
    private Activity activity;

    protected String[][] doInBackground(Object... objects) {
        tag = (String) objects[0];
        activity = (Activity) objects[1];
        String[][] busStopTitlesAndTimes = {NextbusAPI.getBusStopTitles(tag), NextbusAPI.getBusStopTimes(tag)};
        return busStopTitlesAndTimes;
    }

    protected void onPostExecute(String[][] titlesAndTimes) {
        if (activity instanceof BusStopsActivity) {
            // Update bus stop titles and times
            ((BusStopsActivity) activity).setListView(titlesAndTimes[0], titlesAndTimes[1]);
        }
        else {
            // Start new activity to display bus stop titles and times
            Intent intent = new Intent(activity, BusStopsActivity.class);
            intent.putExtra(BusConstants.BUS_TAG_MESSAGE, tag);
            intent.putExtra(BusConstants.BUS_STOP_TITLES_MESSAGE, titlesAndTimes[0]);
            intent.putExtra(BusConstants.BUS_STOP_TIMES_MESSAGE, titlesAndTimes[1]);
            activity.startActivity(intent);
        }
    }
}