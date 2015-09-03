package org.rudirect.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jgrapht.GraphPath;
import org.rudirect.android.R;
import org.rudirect.android.activity.BusStopsActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRouteEdge;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.data.model.DirectionsBusRoute;
import org.rudirect.android.data.model.DirectionsInnerBusStop;
import org.rudirect.android.data.model.DirectionsItem;
import org.rudirect.android.data.model.DirectionsOuterBusStop;
import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.ui.holder.DirectionsItemViewHolder;
import org.rudirect.android.util.DirectionsUtil;
import org.rudirect.android.util.RUDirectUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsItemViewHolder> {

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
        items.add(new DirectionsOuterBusStop(busStopEdges.get(0).getSourceBusStop().getTitle(),
                busStopEdges.get(0).getSourceBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
        items.add(new DirectionsBusRoute(busStopEdges.get(0).getRouteName()
                + " (Bus #" + busStopEdges.get(0).getVehicleId() + ")", busStopEdges.get(0).getRouteTag(), R.drawable.ic_directions_bus));

        // Iterate through all the edges
        for (int i = 0; i < busStopEdges.size() - 1; i++) {
            // Set travel time
            time += calcWaitTime((int) busStopEdges.get(i).getTravelTime());

            // Handle vehicle transfers
            if (items.get(items.size() - 1).getTitle().equals(busStopEdges.get(i).getTargetBusStop().getTitle())) {
                if (!busStopEdges.get(i).getRouteName().equals(busStopEdges.get(i + 1).getRouteName())
                        || !busStopEdges.get(i).getVehicleId().equals(busStopEdges.get(i + 1).getVehicleId())) {
                    items.remove(items.get(items.size() - 1));
                    items.add(new DirectionsOuterBusStop(busStopEdges.get(i).getTargetBusStop().getTitle(),
                            busStopEdges.get(i).getTargetBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
                    items.add(new DirectionsBusRoute(busStopEdges.get(i + 1).getRouteName()
                            + " (Bus #" + busStopEdges.get(i + 1).getVehicleId() + ")", busStopEdges.get(i + 1).getRouteTag(), R.drawable.ic_directions_bus));
                }
                continue;
            }

            // Add inner bus stop
            items.add(new DirectionsInnerBusStop(busStopEdges.get(i).getTargetBusStop().getTitle(),
                    busStopEdges.get(i).getTargetBusStop().getTag(), getTimeInHHMM(time), android.R.color.transparent));
        }

        // Add destination bus stop
        time += calcWaitTime((int) busStopEdges.get(busStopEdges.size() - 1).getTravelTime());
        items.add(new DirectionsOuterBusStop(busStopEdges.get(busStopEdges.size() - 1).getTargetBusStop().getTitle(),
                busStopEdges.get(busStopEdges.size() - 1).getTargetBusStop().getTag(), getTimeInHHMM(time), R.drawable.ic_bus_stop_circle));
    }

    public DirectionsAdapter() {
        this.items = null;
    }

    @Override
    public DirectionsItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v;
        DirectionsItemViewHolder viewHolder;

        // Set view and viewholder
        if (viewType == BUS_ROUTE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions_route, parent, false);
            viewHolder = new DirectionsItemViewHolder(v, new ViewHolderClickListener() {
                public void onClick(View v, int position) {
                    // Setup intent
                    Context context = RUDirectApplication.getContext();
                    Intent intent = new Intent(activity, BusStopsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(context.getString(R.string.bus_tag_message), items.get(position).getTag());
                    intent.putExtra(context.getString(R.string.page_clicked_from_message), "Directions Selector");

                    // Start new activity to show bus stops
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
                }
            });
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directions_bus_stop, parent, false);
            viewHolder = new DirectionsItemViewHolder(v, new ViewHolderClickListener() {
                public void onClick(View v, int position) { /* Do nothing */ }
            });
        }

        // Stylize outer bus stop
        if (viewType == OUTER_BUS_STOP) {
            Resources resources = RUDirectApplication.getContext().getResources();
            viewHolder.title.setTypeface(null, Typeface.BOLD);
            viewHolder.title.setTextColor(resources.getColor(android.R.color.black));
            viewHolder.title.setTextSize(20);
            viewHolder.time.setTypeface(null, Typeface.BOLD);
            viewHolder.time.setTextColor(resources.getColor(android.R.color.black));
            viewHolder.time.setTextSize(20);
        } else if (viewType == INNER_BUS_STOP) {
            RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.directions_bus_stop_layout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, RUDirectUtil.dpToPx(12), 0, 0);
            layout.setLayoutParams(params);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DirectionsItemViewHolder viewHolder, int position) {
        DirectionsItem item = items.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.icon.setImageResource(item.getIconId());
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

    // Calculate the initial wait time / travel time
    private int calcWaitTime(int time) {
        if (time == 0) { // Handle cases where the initial wait time is 0
            return 500;
        } else {
            return time * (int) DateUtils.MINUTE_IN_MILLIS;
        }
    }
}