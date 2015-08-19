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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.data.constants.RUDirectApplication;
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.data.model.DirectionsBusRoute;
import me.rutgersdirect.rudirect.data.model.DirectionsInnerBusStop;
import me.rutgersdirect.rudirect.data.model.DirectionsItem;
import me.rutgersdirect.rudirect.data.model.DirectionsOuterBusStop;
import me.rutgersdirect.rudirect.interfaces.DirectionsViewHolderClick;
import me.rutgersdirect.rudirect.ui.holder.DirectionsItemViewHolder;
import me.rutgersdirect.rudirect.util.DirectionsUtil;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsItemViewHolder> {

    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private static final int INNER_BUS_STOP = 0;
    private static final int OUTER_BUS_STOP = 1;
    private static final int BUS_ROUTE = 2;
    private ArrayList<DirectionsItem> items;

    public DirectionsAdapter(GraphPath<BusStop, BusRouteEdge> path) {
        List<BusRouteEdge> busStopEdges = path.getEdgeList();
        items = new ArrayList<>();
        long time = new Date().getTime();

        // Set initial wait time
        int tempInitialWait = (int) DirectionsUtil.getInitialWait();
        if (tempInitialWait == 0) { // Handle cases where the initial wait time is 0
            time += 500;
        } else {
            time += tempInitialWait * MILLIS_IN_ONE_MINUTE;
        }

        // Set title
        items.add(new DirectionsOuterBusStop(R.drawable.bus_stop_circle,
                busStopEdges.get(0).getSourceBusStop().getTitle(), getTimeInHHMM(time)));
        items.add(new DirectionsBusRoute(android.R.color.transparent, busStopEdges.get(0).getRouteName(), null));

        for (int i = 0; i < busStopEdges.size() - 1; i++) {
            // Set travel time
            int tempTravelTime = (int) busStopEdges.get(i).getTravelTime();
            if (tempTravelTime == 0) { // Handle cases where the travel time is 0
                time += 500;
            } else {
                time += tempTravelTime * MILLIS_IN_ONE_MINUTE;
            }

            // Handle vehicle transfers
            if (items.get(items.size() - 1).getTitle().equals(busStopEdges.get(i).getTargetBusStop().getTitle())) {
                items.remove(items.get(items.size() - 1));
                items.add(new DirectionsOuterBusStop(R.drawable.bus_stop_circle,
                        busStopEdges.get(i).getTargetBusStop().getTitle(), getTimeInHHMM(time)));
                items.add(new DirectionsBusRoute(android.R.color.transparent,
                        busStopEdges.get(i + 1).getRouteName(), null));
                continue;
            }

            items.add(new DirectionsInnerBusStop(android.R.color.transparent,
                    busStopEdges.get(i).getTargetBusStop().getTitle(), getTimeInHHMM(time)));
        }

        // Add destination item
        int tempTravelTime = (int) busStopEdges.get(busStopEdges.size() - 1).getTravelTime();
        if (tempTravelTime == 0) { // Handle cases where the travel time is 0
            time += 500;
        } else {
            time += tempTravelTime * MILLIS_IN_ONE_MINUTE;
        }

        items.add(new DirectionsOuterBusStop(R.drawable.bus_stop_circle,
                busStopEdges.get(busStopEdges.size() - 1).getTargetBusStop().getTitle(), getTimeInHHMM(time)));
    }

    public DirectionsAdapter() {
        this.items = null;
    }

    @Override
    public DirectionsItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions, parent, false);
        DirectionsItemViewHolder viewHolder = new DirectionsItemViewHolder(v, new DirectionsViewHolderClick() {
            public void onClick(View v, int position) {
                Log.d("DirectionsAdapter", "Title: " + items.get(position).getTitle() + " was clicked");
            }
        });

        // Stylize item
        Resources resources = RUDirectApplication.getContext().getResources();
        if (viewType == BUS_ROUTE) {
            viewHolder.title.setTextColor(resources.getColor(R.color.primary_color));
            viewHolder.title.setTypeface(null, Typeface.BOLD);
            viewHolder.title.setCompoundDrawablePadding(20);
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_directions_bus, 0, 0, 0);
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
    public void onBindViewHolder(DirectionsItemViewHolder viewHolder, int position) {
        DirectionsItem item = items.get(position);
        // Set title
        viewHolder.title.setText(item.getTitle());
        // Set icon
        viewHolder.icon.setImageResource(item.getIconId());
        // Set time
        viewHolder.time.setText(item.getTime());
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof DirectionsInnerBusStop) return INNER_BUS_STOP;
        else if (items.get(position) instanceof DirectionsOuterBusStop) return OUTER_BUS_STOP;
        else return BUS_ROUTE;
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    // Takes a time in millis and returns the time in HH:MM format
    private String getTimeInHHMM(long time) {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(time));
    }
}