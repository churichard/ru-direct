package me.rutgersdirect.rudirect.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;

public class BusStopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title;
    public TextView times;
    public BusStopViewHolderClick mListener;

    public BusStopViewHolder(View v, BusStopViewHolderClick listener) {
        super(v);

        title = (TextView) v.findViewById(R.id.bus_stop_name);
        times = (TextView) v.findViewById(R.id.bus_stop_times);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }

    public interface BusStopViewHolderClick {
        void onClick(View v, int position);
    }
}