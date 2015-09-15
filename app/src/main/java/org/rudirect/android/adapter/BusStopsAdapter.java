package org.rudirect.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rudirect.android.R;
import org.rudirect.android.activity.StopActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusStop;
import org.rudirect.android.fragment.StopsFragment;
import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.ui.holder.BusItemViewHolder;

import java.sql.SQLException;
import java.util.ArrayList;

public class BusStopsAdapter extends RecyclerView.Adapter<BusItemViewHolder> {

    private static final String TAG = BusStopsAdapter.class.getSimpleName();
    private ArrayList<BusStop> busStops;
    private Activity activity;
    private StopsFragment fragment;

    public BusStopsAdapter(Activity activity, StopsFragment fragment) {
        this.busStops = null;
        this.activity = activity;
        this.fragment = fragment;
    }

    // Create new views
    @Override
    public BusItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_items, parent, false);
        return new BusItemViewHolder(v, new ViewHolderClickListener() {
            public void onClick(View v, int position) {
                BusStop stop = busStops.get(position);

                // Setup intent
                Intent intent = new Intent(activity, StopActivity.class);
                Context context = RUDirectApplication.getContext();
                intent.putExtra(context.getString(R.string.stop_tag_message), stop.getTag());
                intent.putExtra(context.getString(R.string.page_clicked_from_message), "Stops");

                // Start new activity to show bus stops
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
            }
        });
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(final BusItemViewHolder viewHolder, final int position) {
        BusStop stop = busStops.get(position);
        viewHolder.title.setText(stop.getTitle());
        viewHolder.starImage.setActivated(stop.isStarred());
        viewHolder.starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the star image
                boolean starred = !viewHolder.starImage.isActivated();
                viewHolder.starImage.setActivated(starred);

                // Handle star click
                handleStarClick(position, starred);

                // Update bus data
                try {
                    RUDirectApplication.getDatabaseHelper().getDao()
                            .createOrUpdate(RUDirectApplication.getBusData());
                } catch (SQLException e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        });
    }

    // Return the item count
    @Override
    public int getItemCount() {
        if (busStops != null) {
            return busStops.size();
        }
        return 0;
    }

    // Handles what happens when a star is clicked
    private void handleStarClick(int position, boolean starred) {
        BusStop stop = busStops.get(position);
        stop.setStarred(starred);
        busStops.remove(position);

        // Move bus route to the proper position
        int size = busStops.size();
        for (int i = 0; i <= size; i++) {
            if (i == size
                    || (starred && (!busStops.get(i).isStarred() ||
                    stop.getTitle().compareToIgnoreCase(busStops.get(i).getTitle()) < 0))
                    || (!starred && !busStops.get(i).isStarred() &&
                    stop.getTitle().compareToIgnoreCase(busStops.get(i).getTitle()) < 0)) {
                busStops.add(i, stop);
                notifyItemMoved(position, i);
                notifyItemRangeChanged(Math.min(position, i), Math.abs(position - i) + 1);
                break;
            }
        }

        // Scroll to top
        if (position == 0) {
            scrollToTop();
        }
    }

    // Helper method that scrolls the RecyclerView to the first item
    private void scrollToTop() {
        fragment.getRecyclerView().scrollToPosition(0);
    }

    // Sets the active bus routes in the adapter
    public void setBusStops(ArrayList<BusStop> busStops) {
        this.busStops = busStops;
    }
}