package me.rutgersdirect.rudirect.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import me.rutgersdirect.rudirect.data.AppData;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;

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

    @Override
    protected void onPostExecute(Void v) {
        if (activity instanceof BusStopsActivity) {
            // Update bus stop titles and times
            ((BusStopsActivity) activity).setListView(busStopTitles, busStopTimes);
        } else {
            // Start new activity to display bus stop titles and times
            Intent intent = new Intent(activity, BusStopsActivity.class);
            intent.putExtra(AppData.BUS_TAG_MESSAGE, tag);
            intent.putExtra(AppData.BUS_STOP_TITLES_MESSAGE, busStopTitles);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(AppData.BUS_STOP_TIMES_MESSAGE, busStopTimes);
            intent.putExtras(mBundle);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
        }
    }
}