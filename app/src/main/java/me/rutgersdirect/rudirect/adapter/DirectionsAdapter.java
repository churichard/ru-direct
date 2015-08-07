package me.rutgersdirect.rudirect.adapter;

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
import me.rutgersdirect.rudirect.data.model.BusRouteEdge;
import me.rutgersdirect.rudirect.data.model.BusStop;
import me.rutgersdirect.rudirect.ui.holder.DirectionsViewHolder;
import me.rutgersdirect.rudirect.util.DirectionsUtil;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsViewHolder> {

    private static final int MILLIS_IN_ONE_MINUTE = 60000;
    private String[] titles;
    private String[] times;

    public DirectionsAdapter(GraphPath<BusStop, BusRouteEdge> path) {
        List<BusRouteEdge> busStopEdges = path.getEdgeList();
        titles = new String[busStopEdges.size() * 2 + 1];
        times = new String[busStopEdges.size() * 2 + 1];
        long time = new Date().getTime();

        titles[0] = busStopEdges.get(0).getSourceBusStop().getTitle();
        time += ((int) DirectionsUtil.getInitialWait(path) * MILLIS_IN_ONE_MINUTE);
        times[0] = getTimeInHHMM(time);
        int j = 1;
        for (int i = 0; i < busStopEdges.size(); i++) {
            titles[j] = busStopEdges.get(i).getRouteName();
            titles[j + 1] = busStopEdges.get(i).getTargetBusStop().getTitle();

            time += ((int) busStopEdges.get(i).getTravelTime() * MILLIS_IN_ONE_MINUTE);
            times[j] = (int) busStopEdges.get(i).getTravelTime() + " min";
            times[j + 1] = getTimeInHHMM(time);
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
        return new DirectionsViewHolder(v, new DirectionsViewHolder.DirectionsViewHolderClick() {
            public void onClick(View v, int position) {
                Log.d("DirectionsAdapter", "Title: " + titles[position] + " was clicked");
            }
        });
    }

    @Override
    public void onBindViewHolder(DirectionsViewHolder viewHolder, int position) {
        viewHolder.title.setText(titles[position]);
        if (times != null) {
            viewHolder.time.setText(times[position]);
        }
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