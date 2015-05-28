package me.rutgersdirect.rudirect.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.model.BusStop;
import me.rutgersdirect.rudirect.ui.activity.BusStopsActivity;

public class BusStopAdapter extends ArrayAdapter<BusStop> {
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

        if (BusStopsActivity.expBusStopIndex != -1 && BusStopsActivity.expBusStopIndex == position) {
            setupExpandedItem(busStopName, busStopTimes, values.get(position));
        } else {
            setupNormalItem(busStopName, busStopTimes, values.get(position));
        }

        return convertView;
    }

    // Sets up the expanded view of the bus stop times
    private void setupExpandedItem(TextView busStopName, TextView busStopTimes, BusStop stop) {
        if (BusStopsActivity.expBusStopIndex != BusStopsActivity.lastExpBusStopIndex || !BusStopsActivity.isExpBusStopIndexExpanded) {
            BusStopsActivity.isExpBusStopIndexExpanded = true;
            busStopTimes.setText("Expanded!");
        } else {
            BusStopsActivity.isExpBusStopIndexExpanded = false;
            setupNormalItem(busStopName, busStopTimes, stop);
        }
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

    // Change text color of the listview items
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

    // Refills the listview with refreshed values
    public void refill(ArrayList<BusStop> newValues) {
        values.clear();
        values.addAll(newValues);
        notifyDataSetChanged();
    }
}