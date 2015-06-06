package me.rutgersdirect.rudirect.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;


public class BusRouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title;
    public BusRouteViewHolderClick mListener;

    public BusRouteViewHolder(View v, BusRouteViewHolderClick listener) {
        super(v);

        title = (TextView) v.findViewById(R.id.bus_route_name);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }

    public interface BusRouteViewHolderClick {
        void onClick(View v, int position);
    }
}