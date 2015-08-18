package me.rutgersdirect.rudirect.adapter;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jgrapht.GraphPath;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.ui.holder.DirectionsViewHolder;
import me.rutgersdirect.rudirect.util.DirectionsUtil;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsViewHolder> {

    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private static final int INNER_BUS_STOP = 0;
    private static final int BUS_ROUTE = 1;
    private static final int OUTER_BUS_STOP = 2;
    private int size;
    private String[] titles;
    private String[] times;
    private String[] vehicleIds;

    public DirectionsAdapter(GraphPath<BusStop, BusRouteEdge> path) {
        List<BusRouteEdge> busStopEdges = path.getEdgeList();
        size = busStopEdges.size() * 2 + 1;
        titles = new String[size];
        times = new String[size];
        vehicleIds = new String[size];
        long time = new Date().getTime();

        // Set title
        titles[0] = busStopEdges.get(0).getSourceBusStop().getTitle();
        // Set initial wait time
        int tempInitialWait = (int) DirectionsUtil.getInitialWait();
        if (tempInitialWait == 0) { // Handle cases where the initial wait time is 0
            time += 500;
        } else {
            time += tempInitialWait * MILLIS_IN_ONE_MINUTE;
        }
        times[0] = getTimeInHHMM(time);
        // Set vehicle id
        vehicleIds[0] = "";

        int j = 1;
        for (int i = 0; i < busStopEdges.size(); i++) {
            // Set title
            titles[j] = busStopEdges.get(i).getRouteName();
            titles[j + 1] = busStopEdges.get(i).getTargetBusStop().getTitle();
            // Set travel time
            int tempTravelTime = (int) busStopEdges.get(i).getTravelTime();
            if (tempTravelTime == 0) { // Handle cases where the travel time is 0
                time += 500;
                times[j] = "<1 min";
            } else {
                time += tempTravelTime * MILLIS_IN_ONE_MINUTE;
                times[j] = tempTravelTime + " min";
            }
            times[j + 1] = getTimeInHHMM(time);
            // Set vehicle id
            vehicleIds[j] = "(Bus ID: " + busStopEdges.get(i).getVehicleId() + ")";
            vehicleIds[j + 1] = "";

            j += 2;
        }
    }

    public DirectionsAdapter(String[] titles, String[] times) {
        this.titles = titles;
        this.times = times;
    }

    public DirectionsAdapter() {
        this.titles = null;
        this.times = null;
    }

    @Override
    public DirectionsViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions, parent, false);
        DirectionsViewHolder viewHolder = new DirectionsViewHolder(v, new DirectionsViewHolder.DirectionsViewHolderClick() {
            public void onClick(View v, int position) {
                Log.d("DirectionsAdapter", "Title: " + titles[position] + " was clicked");
            }
        });
        Resources resources = RUDirectApplication.getContext().getResources();
        if (viewType == BUS_ROUTE) {
            viewHolder.title.setTextColor(resources.getColor(android.R.color.white));
            viewHolder.title.setBackgroundColor(resources.getColor(R.color.primary_color));
            viewHolder.time.setTextColor(resources.getColor(android.R.color.white));
            viewHolder.time.setBackgroundColor(resources.getColor(R.color.primary_color));
        } else if (viewType == OUTER_BUS_STOP) {
            viewHolder.title.setTypeface(null, Typeface.BOLD);
            viewHolder.title.setTextColor(resources.getColor(android.R.color.black));
            viewHolder.title.setTextSize(20);
            viewHolder.time.setTypeface(null, Typeface.BOLD);
            viewHolder.time.setTextColor(resources.getColor(android.R.color.black));
            viewHolder.time.setTextSize(20);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DirectionsViewHolder viewHolder, int position) {
        // Set title
        viewHolder.title.setText(titles[position]);
        // Set icon
        if (viewHolder.getItemViewType() == OUTER_BUS_STOP) {
            viewHolder.icon.setImageResource(R.drawable.bus_stop_circle);
        } else if (viewHolder.getItemViewType() == BUS_ROUTE) {
            viewHolder.icon.setImageResource(R.drawable.bus_route_circle);
        } else if (viewHolder.getItemViewType() == INNER_BUS_STOP) {
            viewHolder.icon.setImageResource(R.drawable.bus_route_circle_no_fill);
        }
        // Set time
        if (times != null) {
            viewHolder.time.setText(times[position] + " " + vehicleIds[position]);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == size - 1) return OUTER_BUS_STOP;
        else if (position % 2 == 0 && (titles[position].equals(titles[position - 2])
                || titles[position].equals(titles[position + 2]))) return OUTER_BUS_STOP;
        else return position % 2;
    }

    @Override
    public int getItemCount() {
        if (titles != null) {
            return titles.length;
        }
        return 0;
    }

    // Takes a time in millis and returns the time in HH:MM format
    private String getTimeInHHMM(long time) {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(time));
    }
}