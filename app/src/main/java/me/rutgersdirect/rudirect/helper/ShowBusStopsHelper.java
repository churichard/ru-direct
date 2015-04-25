package me.rutgersdirect.rudirect.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.ui.BusTimesActivity;

public class ShowBusStopsHelper extends AsyncTask<Object, Void, String> {
    private String busTag;
    private Activity activity;

    protected String doInBackground(Object... objects) {
        busTag = (String) objects[0];
        activity = (Activity) objects[1];
        return NextBusAPI.getJSON("http://runextbus.herokuapp.com/route/" + busTag);
    }

    protected void onPostExecute(String result) {
        // Get bus stop titles and times
        String[][] titlesAndTimes = NextBusAPI.getBusStopTitlesAndTimes(result);

        // Start new activity to display bus titles and times
        Intent intent = new Intent(activity, BusTimesActivity.class);
        intent.putExtra(BusConstants.BUS_TAG_MESSAGE, busTag);
        intent.putExtra(BusConstants.BUS_STOP_TITLES_MESSAGE, titlesAndTimes[0]);
        intent.putExtra(BusConstants.BUS_STOP_TIMES_MESSAGE, titlesAndTimes[1]);
        activity.startActivity(intent);
    }
}