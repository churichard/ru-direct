package me.rutgersdirect.rudirect.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.data.model.BusStopTime;
import me.rutgersdirect.rudirect.interfaces.ViewHolderClickListener;
import me.rutgersdirect.rudirect.ui.holder.BusStopViewHolder;

public class BusStopAdapter extends RecyclerView.Adapter<BusStopViewHolder> {

    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private static boolean expToggleRequest; // Whether or not the bus stop should be expanded/retracted
    private BusStop[] busStops;

    public BusStopAdapter(BusStop[] busStops) {
        this.busStops = busStops;
    }

    public BusStopAdapter() {
        this.busStops = null;
    }

    // Create new views
    @Override
    public BusStopViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_stops, parent, false);
        return new BusStopViewHolder(v, new ViewHolderClickListener() {
            public void onClick(View v, int position) {
                expToggleRequest = true;

                BusStop stop = busStops[position];
                TextView titleTextView = (TextView) v.findViewById(R.id.bus_stop_name);
                TextView timesTextView = (TextView) v.findViewById(R.id.bus_stop_times);
                timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
            }
        });
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(BusStopViewHolder viewHolder, int position) {
        BusStop stop = busStops[position];
        TextView titleTextView = viewHolder.title;
        TextView timesTextView = viewHolder.times;

        titleTextView.setText(stop.getTitle());
        timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
    }

    // Return the number of posts
    @Override
    public int getItemCount() {
        if (busStops != null) {
            return busStops.length;
        }
        return 0;
    }

    // Return the bus stop times
    private String getBusStopTimes(BusStop busStop, TextView titleTextView, TextView timesTextView) {
        ArrayList<BusStopTime> times = busStop.getTimes();
        setTextColor(titleTextView, timesTextView, times);

        if (expToggleRequest && busStop.isActive() && !busStop.isExpanded() || !expToggleRequest && busStop.isExpanded()) {
            expToggleRequest = false;
            busStop.setIsExpanded(true);
            return expandedTimes(times);
        } else {
            expToggleRequest = false;
            busStop.setIsExpanded(false);
            return normalTimes(times);
        }
    }

    // Sets up the expanded view of the bus stop times
    private String expandedTimes(ArrayList<BusStopTime> times) {
        long currentTime = new Date().getTime();

        // Build string of times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving at ");
        for (int i = 0; i < times.size(); i++) {
            Date stopTime = new Date(currentTime + (times.get(i).getMinutes() * MILLIS_IN_ONE_MINUTE));
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(stopTime);
            builder.append(time);
            if (i != times.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(".");

        return builder.toString();
    }

    // Sets up the normal view of the bus stop times
    private String normalTimes(ArrayList<BusStopTime> times) {
        // Bus stop is offline
        if (times.size() == 1 && times.get(0).getMinutes() == -1) {
            return "Offline";
        }
        // Format bus stop times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving in ");
        for (int i = 0; i < times.size(); i++) {
            // Append time
            int minutes = times.get(i).getMinutes();
            if (minutes == 0) {
                builder.append("<1");
            } else {
                builder.append(Integer.toString(minutes));
            }
            // Append comma
            if (i != times.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(" minutes.");
        return builder.toString();
    }

    // Change text color of the ListView items
    private void setTextColor(TextView busStopName, TextView busStopTimes, ArrayList<BusStopTime> times) {
        Resources resources = RUDirectApplication.getContext().getResources();
        busStopName.setTextColor(resources.getColor(android.R.color.black));
        int minutes = times.get(0).getMinutes();
        if (minutes == -1) {
            busStopName.setTextColor(resources.getColor(R.color.medium_gray));
            busStopTimes.setTextColor(resources.getColor(R.color.medium_gray));
        } else if (minutes == 0 || minutes == 1) {
            busStopTimes.setTextColor(resources.getColor(R.color.primary_color));
        } else if (minutes > 1 && minutes <= 5) {
            busStopTimes.setTextColor(resources.getColor(R.color.orange));
        } else {
            busStopTimes.setTextColor(resources.getColor(R.color.blue));
        }
    }

    // Sets expanded toggle request boolean
    public static void setExpToggleRequest(boolean expToggleRequest) {
        BusStopAdapter.expToggleRequest = expToggleRequest;
    }

    // Sets the bus stops
    public void setBusStops(BusStop[] busStops) {
        this.busStops = busStops;
    }
}