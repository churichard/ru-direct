package org.rudirect.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jgrapht.GraphPath;
import org.rudirect.android.R;
import org.rudirect.android.activity.RouteActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRouteEdge;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.data.model.DirectionsItem;
import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.ui.holder.DirectionsRouteViewHolder;
import org.rudirect.android.ui.holder.DirectionsStopViewHolder;
import org.rudirect.android.util.DirectionsUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int INNER_BUS_STOP = 0, OUTER_BUS_STOP = 1, BUS_ROUTE = 2;
    private Activity activity;
    private ArrayList<DirectionsItem> items;

    public DirectionsAdapter(Activity activity, GraphPath<BusStop, BusRouteEdge> path) {
        this.activity = activity;
        List<BusRouteEdge> busStopEdges = path.getEdgeList();
        items = new ArrayList<>();
        long time = new Date().getTime();

        // Set initial wait time
        time += calcWaitTime((int) DirectionsUtil.getInitialWait());
        // Add origin bus stop and initial route
        items.add(new DirectionsItem(OUTER_BUS_STOP, busStopEdges.get(0).getSourceBusStop().getTitle(),
                busStopEdges.get(0).getSourceBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
        items.add(new DirectionsItem(BUS_ROUTE, busStopEdges.get(0).getRouteName()
                + " (Bus #" + busStopEdges.get(0).getVehicleId() + ")", busStopEdges.get(0).getRouteTag(), null, 0));

        // Iterate through all the edges
        for (int i = 0; i < busStopEdges.size() - 1; i++) {
            // Set travel time
            time += calcWaitTime((int) busStopEdges.get(i).getTravelTime());

            // Handle vehicle transfers
            if (items.get(items.size() - 1).getTitle().equals(busStopEdges.get(i).getTargetBusStop().getTitle())) {
                if (!busStopEdges.get(i).getRouteName().equals(busStopEdges.get(i + 1).getRouteName())
                        || !busStopEdges.get(i).getVehicleId().equals(busStopEdges.get(i + 1).getVehicleId())) {
                    items.remove(items.get(items.size() - 1));
                    items.add(new DirectionsItem(OUTER_BUS_STOP, busStopEdges.get(i).getTargetBusStop().getTitle(),
                            busStopEdges.get(i).getTargetBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
                    items.add(new DirectionsItem(BUS_ROUTE, busStopEdges.get(i + 1).getRouteName()
                            + " (Bus #" + busStopEdges.get(i + 1).getVehicleId() + ")", busStopEdges.get(i + 1).getRouteTag(), null, 0));
                }
                continue;
            }

            // Add inner bus stop
            items.add(new DirectionsItem(INNER_BUS_STOP, busStopEdges.get(i).getTargetBusStop().getTitle(),
                    busStopEdges.get(i).getTargetBusStop().getTag(), getTimeInHHMM(time), android.R.color.transparent));
        }

        // Add destination bus stop
        time += calcWaitTime((int) busStopEdges.get(busStopEdges.size() - 1).getTravelTime());
        items.add(new DirectionsItem(OUTER_BUS_STOP, busStopEdges.get(busStopEdges.size() - 1).getTargetBusStop().getTitle(),
                busStopEdges.get(busStopEdges.size() - 1).getTargetBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
    }

    public DirectionsAdapter() {
        this.items = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v;

        // Set view and viewholder
        if (viewType == BUS_ROUTE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions_route, parent, false);
            return new DirectionsRouteViewHolder(v, new ViewHolderClickListener() {
                public void onClick(View v, int position) { /* Do nothing */ }
            });
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions_stop, parent, false);
            DirectionsStopViewHolder viewHolder = new DirectionsStopViewHolder(v, new ViewHolderClickListener() {
                public void onClick(View v, int position) { /* Do nothing */ }
            });

            // Stylize outer bus stop
            if (viewType == OUTER_BUS_STOP) {
                Context context = RUDirectApplication.getContext();
                viewHolder.title.setTypeface(null, Typeface.BOLD);
                viewHolder.title.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                viewHolder.title.setTextSize(20);
                viewHolder.time.setTypeface(null, Typeface.BOLD);
                viewHolder.time.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                viewHolder.time.setTextSize(20);
            }

            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        DirectionsItem item = items.get(position);
        if (viewHolder instanceof DirectionsStopViewHolder) {
            DirectionsStopViewHolder busStopViewHolder = (DirectionsStopViewHolder) viewHolder;
            busStopViewHolder.title.setText(item.getTitle());
            busStopViewHolder.icon.setImageResource(item.getIconId());
            busStopViewHolder.time.setText(item.getTime());
        } else if (viewHolder instanceof DirectionsRouteViewHolder) {
            DirectionsRouteViewHolder routeViewHolder = (DirectionsRouteViewHolder) viewHolder;
            routeViewHolder.route.setText(item.getTitle());
            routeViewHolder.route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Setup intent
                    Context context = RUDirectApplication.getContext();
                    Intent intent = new Intent(activity, RouteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(context.getString(R.string.bus_tag_message), items.get(viewHolder.getLayoutPosition()).getTag());
                    intent.putExtra(context.getString(R.string.page_clicked_from_message), "Directions Selector");

                    // Start new activity to show bus stops
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
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

    // Calculate the initial wait time / travel time
    private int calcWaitTime(int time) {
        if (time == 0) { // Handle cases where the initial wait time is 0
            return 500;
        } else {
            return time * (int) DateUtils.MINUTE_IN_MILLIS;
        }
    }
}