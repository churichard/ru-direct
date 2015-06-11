package me.rutgersdirect.rudirect.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.model.BusStop;
import me.rutgersdirect.rudirect.ui.holder.BusStopViewHolder;


public class BusStopAdapter extends RecyclerView.Adapter<BusStopViewHolder> {

    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private List<BusStop> busStops;
    private static boolean expToggleRequest; // Whether or not the bus stop should be expanded/retracted

    public BusStopAdapter(List<BusStop> busStops) {
        this.busStops = busStops;
    }

    public BusStopAdapter() {
        this.busStops = null;
    }

    // Create new views
    @Override
    public BusStopViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_stops, parent, false);
        return new BusStopViewHolder(v, new BusStopViewHolder.BusStopViewHolderClick() {
            public void onClick(View v, int position) {
                expToggleRequest = true;

                BusStop stop = busStops.get(position);
                TextView titleTextView = (TextView) v.findViewById(R.id.bus_stop_name);
                TextView timesTextView = (TextView) v.findViewById(R.id.bus_stop_times);
                timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
            }
        });
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(BusStopViewHolder viewHolder, int position) {
        BusStop stop = busStops.get(position);
        TextView titleTextView = viewHolder.title;
        TextView timesTextView = viewHolder.times;

        titleTextView.setText(stop.getTitle());
        timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
    }

    // Return the number of posts
    @Override
    public int getItemCount() {
        if (busStops != null) {
            return busStops.size();
        }
        return 0;
    }

    private String getBusStopTimes(BusStop busStop, TextView titleTextView, TextView timesTextView) {
        int[] times = busStop.getTimes();
        if (expToggleRequest && times[0] != -1) {
            if (!busStop.isExpanded()) {
                busStop.setIsExpanded(true);
                setTextColor(titleTextView, timesTextView, times);
                return expandedTimes(times);
            } else {
                busStop.setIsExpanded(false);
            }
        }
        setTextColor(titleTextView, timesTextView, times);
        return normalTimes(times);
    }

    // Sets up the expanded view of the bus stop times
    private String expandedTimes(int[] times) {
        long currentTime = new Date().getTime();

        // Build string of times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving at ");
        for (int i = 0; i < times.length; i++) {
            Date stopTime = new Date(currentTime + (times[i] * MILLIS_IN_ONE_MINUTE));
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(stopTime);
            builder.append(time);
            if (i != times.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(".");

        return builder.toString();
    }

    // Sets up the normal view of the bus stop times
    private String normalTimes(int[] times) {
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

    public static void setExpToggleRequest(boolean expToggleRequest) {
        BusStopAdapter.expToggleRequest = expToggleRequest;
    }
}