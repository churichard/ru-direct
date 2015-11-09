package org.rudirect.android.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rudirect.android.R;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusItem;
import org.rudirect.android.data.model.BusTime;
import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.ui.holder.BusTimesViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BusTimesAdapter extends RecyclerView.Adapter<BusTimesViewHolder> {

    private static boolean expToggleRequest; // Whether or not the bus stop should be expanded/retracted
    private ArrayList<BusItem> busItems;

    public BusTimesAdapter() {
        this.busItems = null;
    }

    // Create new views
    @Override
    public BusTimesViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_times, parent, false);
        return new BusTimesViewHolder(v, new ViewHolderClickListener() {
            public void onClick(View v, int position) {
                expToggleRequest = true;

                BusItem stop = busItems.get(position);
                TextView titleTextView = (TextView) v.findViewById(R.id.bus_stop_name);
                TextView timesTextView = (TextView) v.findViewById(R.id.bus_stop_times);
                timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
            }
        });
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(BusTimesViewHolder viewHolder, int position) {
        BusItem stop = busItems.get(position);
        TextView titleTextView = viewHolder.title;
        TextView timesTextView = viewHolder.times;

        titleTextView.setText(stop.getTitle());
        timesTextView.setText(getBusStopTimes(stop, titleTextView, timesTextView));
    }

    // Return the number of posts
    @Override
    public int getItemCount() {
        if (busItems != null) {
            return busItems.size();
        }
        return 0;
    }

    // Return the bus stop times
    private String getBusStopTimes(BusItem busItem, TextView titleTextView, TextView timesTextView) {
        ArrayList<BusTime> times = busItem.getTimes();
        setTextColor(titleTextView, timesTextView, times);

        if (expToggleRequest && busItem.isActive() && !busItem.isExpanded()
                || !expToggleRequest && busItem.isExpanded()) {
            expToggleRequest = false;
            busItem.setIsExpanded(true);
            return expandedTimes(times);
        } else {
            expToggleRequest = false;
            busItem.setIsExpanded(false);
            return normalTimes(times);
        }
    }

    // Sets up the expanded view of the bus stop times
    private String expandedTimes(ArrayList<BusTime> times) {
        long currentTime = new Date().getTime();

        // Build string of times
        StringBuilder builder = new StringBuilder();
        builder.append("Arriving at ");
        for (int i = 0; i < times.size(); i++) {
            Date stopTime = new Date(currentTime + (times.get(i).getMinutes() * DateUtils.MINUTE_IN_MILLIS));
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
    private String normalTimes(ArrayList<BusTime> times) {
        // Bus stop is offline
        if (times == null) {
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
    private void setTextColor(TextView busStopName, TextView busStopTimes, ArrayList<BusTime> times) {
        Resources resources = RUDirectApplication.getContext().getResources();
        busStopName.setTextColor(resources.getColor(android.R.color.black));
        if (times == null) {
            busStopName.setTextColor(resources.getColor(R.color.medium_gray));
            busStopTimes.setTextColor(resources.getColor(R.color.medium_gray));
        } else {
            int minutes = times.get(0).getMinutes();
            if (minutes == 0 || minutes == 1) {
                busStopTimes.setTextColor(resources.getColor(R.color.primary_color));
            } else if (minutes > 1 && minutes <= 5) {
                busStopTimes.setTextColor(resources.getColor(R.color.orange));
            } else {
                busStopTimes.setTextColor(resources.getColor(R.color.blue));
            }
        }
    }

    // Sets expanded toggle request boolean
    public static void setExpToggleRequest(boolean expToggleRequest) {
        BusTimesAdapter.expToggleRequest = expToggleRequest;
    }

    // Sets the bus stops
    public void setBusItems(ArrayList<BusItem> busItems) {
        this.busItems = busItems;
    }
}