package me.rutgersdirect.rudirect.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.ui.holder.BusRouteViewHolder;
import me.rutgersdirect.rudirect.util.ShowBusStopsHelper;

public class BusRouteAdapter extends RecyclerView.Adapter<BusRouteViewHolder> {

    private String[] busRoutes;
    private Activity activity;
    private Fragment fragment;

    public BusRouteAdapter(String[] busRoutes, Activity activity, Fragment fragment) {
        this.busRoutes = busRoutes;
        this.activity = activity;
        this.fragment = fragment;
    }

    public BusRouteAdapter() {
        this.busRoutes = null;
    }

    // Create new views
    @Override
    public BusRouteViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_routes, parent, false);
        return new BusRouteViewHolder(v, new BusRouteViewHolder.BusRouteViewHolderClick() {
            public void onClick(View v, int position) {
                if (!BusStopsActivity.active) {
                    String bus = busRoutes[position];

                    SharedPreferences busesToTagsPref
                            = activity.getSharedPreferences(activity.getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
                    String busTag = busesToTagsPref.getString(bus, null);

                    if (busTag != null) {
                        BusStopsActivity.active = true;
                        new ShowBusStopsHelper().execute(busTag, activity, fragment);
                    }
                }
            }
        });
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(BusRouteViewHolder viewHolder, int position) {
        viewHolder.title.setText(busRoutes[position]);
    }

    // Return the number of posts
    @Override
    public int getItemCount() {
        if (busRoutes != null) {
            return busRoutes.length;
        }
        return 0;
    }
}