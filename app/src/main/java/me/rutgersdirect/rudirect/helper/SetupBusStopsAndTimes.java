package me.rutgersdirect.rudirect.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.ui.BusTimesActivity;

public class SetupBusStopsAndTimes extends AsyncTask<Object, Void, String> {
    private String busName;
    private Activity activity;

    protected String doInBackground(Object... objects) {
        busName = (String) objects[0];
        activity = (Activity) objects[1];
        return NextBusAPI.getJSON("http://runextbus.herokuapp.com/route/" + busName);
    }

    protected void onPostExecute(String result) {
        // Get bus stop titles and times
        String[][] titlesAndTimes = NextBusAPI.getBusStopTitlesAndTimes(result);

        // Start new activity to display bus titles and times
        Intent intent = new Intent(activity, BusTimesActivity.class);
        intent.putExtra(BusConstants.BUS_NAME_MESSAGE, busName);
        intent.putExtra(BusConstants.BUS_STOP_TITLES_MESSAGE, titlesAndTimes[0]);
        intent.putExtra(BusConstants.BUS_STOP_TIMES_MESSAGE, titlesAndTimes[1]);
        activity.startActivity(intent);
    }
}