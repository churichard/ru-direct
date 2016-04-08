package org.rudirect.android.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rudirect.android.R;
import org.rudirect.android.activity.RouteActivity;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusData;
import org.rudirect.android.data.model.BusRoute;
import org.rudirect.android.fragment.RoutesFragment;
import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.ui.holder.BusRouteViewHolder;
import org.rudirect.android.ui.holder.HeaderViewHolder;

import java.sql.SQLException;
import java.util.ArrayList;

public class BusRouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BusRouteAdapter.class.getSimpleName();
    private static final int BUS_ROUTE = 0, HEADER = 1;
    private ArrayList<BusRoute> activeRoutes;
    private ArrayList<BusRoute> inactiveRoutes;
    private Activity activity;
    private Fragment fragment;

    public BusRouteAdapter(Activity activity, Fragment fragment) {
        this.activeRoutes = null;
        this.inactiveRoutes = null;
        this.activity = activity;
        this.fragment = fragment;
    }

    // Create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == BUS_ROUTE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bus_routes, parent, false);
            return new BusRouteViewHolder(v, new ViewHolderClickListener() {
                public void onClick(View v, int position) {
                    BusRoute route = getRouteByPosition(position);

                    if (route != null) {
                        // Setup intent
                        Intent intent = new Intent(activity, RouteActivity.class);
                        Context context = RUDirectApplication.getContext();
                        intent.putExtra(context.getString(R.string.bus_tag_message), route.getTag());
                        if (fragment instanceof RoutesFragment) {
                            intent.putExtra(context.getString(R.string.page_clicked_from_message), "Routes");
                        }

                        // Start new activity to show bus stops
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, 0);
                    }
                }
            });
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recyclerview_header, parent, false);
            return new HeaderViewHolder(v);
        }
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof BusRouteViewHolder) {
            BusRoute route = getRouteByPosition(position);
            if (route != null) {
                final BusRouteViewHolder busRouteViewHolder = (BusRouteViewHolder) viewHolder;
                busRouteViewHolder.title.setText(route.getTitle());
                busRouteViewHolder.starImage.setActivated(route.isStarred());
                busRouteViewHolder.starImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update the star image
                        boolean starred = !busRouteViewHolder.starImage.isActivated();
                        busRouteViewHolder.starImage.setActivated(starred);

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
        } else {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            if (position == 0) {
                if (inactiveRoutes == null) {
                    headerViewHolder.title.setText("All");
                } else {
                    headerViewHolder.title.setText("Active");
                }
            } else {
                headerViewHolder.title.setText("Inactive");
            }
        }
    }

    // Return the item count
    @Override
    public int getItemCount() {
        if (activeRoutes != null && inactiveRoutes != null) {
            return activeRoutes.size() + inactiveRoutes.size() + 2;
        } else if (activeRoutes != null) {
            return activeRoutes.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == activeRoutes.size() + 1) {
            return HEADER;
        } else {
            return BUS_ROUTE;
        }
    }

    // Handles what happens when a star is clicked

    private void handleStarClick(int position, boolean starred) {
        BusRoute route = getRouteByPosition(position);
        if (route != null) {
            route.setStarred(starred);

            // Set the correct route list
            ArrayList<BusRoute> routeList;
            if (BusData.getActiveRoutes().contains(route)) {
                routeList = activeRoutes;
                routeList.remove(position - 1);
            } else {
                routeList = inactiveRoutes;
                routeList.remove(position - activeRoutes.size() - 2);
            }

            // Move bus route to the proper position
            int size = routeList.size();
            for (int i = 0; i <= size; i++) {
                if (i == size
                        || (starred && (!routeList.get(i).isStarred() ||
                        route.getTitle().compareToIgnoreCase(routeList.get(i).getTitle()) < 0))
                        || (!starred && !routeList.get(i).isStarred() &&
                        route.getTitle().compareToIgnoreCase(routeList.get(i).getTitle()) < 0)) {
                    int newPosition;
                    if (routeList == activeRoutes) {
                        newPosition = i + 1;
                    } else {
                        newPosition = i + activeRoutes.size() + 2;
                    }
                    routeList.add(i, route);
                    notifyItemMoved(position, newPosition);
                    notifyItemRangeChanged(Math.min(position, newPosition), Math.abs(position - newPosition) + 1);
                    break;
                }
            }
        }
    }

    // Helper method that returns the bus route given the adapter position that was clicked
    private BusRoute getRouteByPosition(int position) {
        if (position < activeRoutes.size() + 1 && position > 0) {
            return activeRoutes.get(position - 1);
        } else if (position > activeRoutes.size() + 1
                && position < activeRoutes.size() + inactiveRoutes.size() + 2) {
            return inactiveRoutes.get(position - activeRoutes.size() - 2);
        } else {
            return null;
        }
    }

    // Sets the active bus routes in the adapter
    public void setActiveRoutes(ArrayList<BusRoute> activeRoutes) {
        this.activeRoutes = activeRoutes;
    }

    // Sets the other bus routes in the adapter
    public void setInactiveRoutes(ArrayList<BusRoute> inactiveRoutes) {
        this.inactiveRoutes = inactiveRoutes;
    }
}