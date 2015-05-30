package me.rutgersdirect.rudirect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.model.BusStop;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;

public class BusStopAdapter extends ArrayAdapter<BusStop> {
    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private Context context;
    private int layout;
    private ArrayList<BusStop> values;

    public BusStopAdapter(Context context, int layout, ArrayList<BusStop> values) {
        super(context, layout, values);
        this.context = context;
        this.layout = layout;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }

        TextView busStopName = (TextView) convertView.findViewById(R.id.bus_stop_name);
        TextView busStopTimes = (TextView) convertView.findViewById(R.id.bus_stop_times);

        if (BusStopsActivity.expansionRequest && BusStopsActivity.expBusStopIndex == position
                && !busStopTimes.getText().equals("Offline")) {
            if (!BusStopsActivity.isExpBusStopIndexExpanded
                    || BusStopsActivity.expBusStopIndex != BusStopsActivity.lastExpBusStopIndex) {
                setupExpandedItem(busStopTimes, values.get(position));
            } else if (BusStopsActivity.isExpBusStopIndexExpanded) {
                BusStopsActivity.isExpBusStopIndexExpanded = false;
                setupNormalItem(busStopName, busStopTimes, values.get(position));
            }
        } else {
            setupNormalItem(busStopName, busStopTimes, values.get(position));
        }

        return convertView;
    }

    // Sets up the expanded view of the bus stop times
    private void setupExpandedItem(TextView busStopTimes, BusStop stop) {
        BusStopsActivity.isExpBusStopIndexExpanded = true;
        int[] minutes = stop.times;
        long currentTime = new Date().getTime();

        // Build string of times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving at ");
        for (int i = 0; i < minutes.length; i++) {
            Date stopTime = new Date(currentTime + (minutes[i] * MILLIS_IN_ONE_MINUTE));
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(stopTime);
            builder.append(time);
            if (i != minutes.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(".");

        busStopTimes.setText(builder.toString());
    }

    // Sets up the normal view of the bus stop times
    private void setupNormalItem(TextView busStopName, TextView busStopTimes, BusStop stop) {
        int[] times = stop.times;
        String timesString = convertTimesToString(times);
        busStopTimes.setText(timesString);
        busStopName.setText(stop.title);
        setTextColor(busStopName, busStopTimes, times);
    }

    // Converts bus times to a string
    private String convertTimesToString(int[] times) {
        // Bus stop is offline
        if (times.length == 1 && times[0] == -1) {
            return "Offline";
        }
        // Format bus stop times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving in ");
        for (int i = 0; i < times.length; i++) {
            // Append time
            if (times[i] == 0) {
                builder.append("<1");
            } else {
                builder.append(Integer.toString(times[i]));
            }
            // Append comma
            if (i != times.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(" minutes.");
        return builder.toString();
    }

    // Change text color of the ListView items
    private void setTextColor(TextView busStopName, TextView busStopTimes, int[] times) {
        busStopName.setTextColor(Color.parseColor("#000000")); // Black
        if (times[0] == -1) {
            busStopName.setTextColor(Color.parseColor("#9E9E9E")); // Grey
            busStopTimes.setTextColor(Color.parseColor("#9E9E9E"));
        } else if (times[0] == 0 || times[0] == 1) {
            busStopTimes.setTextColor(Color.parseColor("#C62828")); // Red
        } else if (times[0] > 1 && times[0] <= 5) {
            busStopTimes.setTextColor(Color.parseColor("#EF6C00")); // Orange
        } else {
            busStopTimes.setTextColor(Color.parseColor("#1565C0")); // Blue
        }
    }

    // Refills the ListView with refreshed values
    public void refill(ArrayList<BusStop> newValues) {
        values.clear();
        values.addAll(newValues);
        notifyDataSetChanged();
    }
}