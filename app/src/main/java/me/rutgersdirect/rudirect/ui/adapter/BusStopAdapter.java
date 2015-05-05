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

        busStopName.setText(values.get(position).title);
        busStopTimes.setText(values.get(position).times);

        // Change text color depending on what the lowest time is
        String[] timeArray = busStopTimes.getText().toString().split(" ");
        if (timeArray.length >= 4) { // Arriving in ___ minutes
            int lowestTimeInt = 6; // Random number that is above the threshold for blue
            String lowestTime;
            if (timeArray.length != 4) {
                lowestTime = timeArray[2].substring(0, timeArray[2].length() - 1);
            }
            else {
                lowestTime = timeArray[2];
            }

            if (!lowestTime.equals("<1")) {
                lowestTimeInt = Integer.parseInt(lowestTime);
            }
            if (lowestTime.equals("<1") || lowestTimeInt <= 1) {
                busStopTimes.setTextColor(Color.parseColor("#C62828")); // Red
            } else if (lowestTimeInt > 1 && lowestTimeInt <= 5) {
                busStopTimes.setTextColor(Color.parseColor("#EF6C00")); // Orange
            } else {
                busStopTimes.setTextColor(Color.parseColor("#1565C0")); // Blue
            }
        }
        else { // If the bus is offline or it is empty
            busStopName.setTextColor(Color.parseColor("#9E9E9E")); // Grey
            busStopTimes.setTextColor(Color.parseColor("#9E9E9E"));
        }

        return convertView;
    }
}