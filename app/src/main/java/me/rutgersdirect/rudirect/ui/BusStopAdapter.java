package me.rutgersdirect.rudirect.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.model.BusStop;

public class BusStopAdapter extends ArrayAdapter {
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

        // Change text color if lowest time is below 1 or 5 minutes
        String[] timeArray = busStopTimes.getText().toString().split(" ");
        String lowestTime = timeArray[2].substring(0, timeArray[2].length() - 1);
        int lowestTimeInt = 6; // Random number that is above the threshold for blue
        if (!lowestTime.equals("<1")) {
            lowestTimeInt = Integer.parseInt(lowestTime);
        }
        if (lowestTime.equals("<1") || lowestTimeInt == 1) {
            Log.d("Hello", "red: " + lowestTime);
            busStopTimes.setTextColor(Color.parseColor("#C62828"));
        } else if (lowestTimeInt > 1 && lowestTimeInt <= 5) {
            Log.d("Hello", "orange: " + lowestTime);
            busStopTimes.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            Log.d("Hello", "blue: " + lowestTime);
            busStopTimes.setTextColor(Color.parseColor("#1565C0"));
        }

        return convertView;
    }
}