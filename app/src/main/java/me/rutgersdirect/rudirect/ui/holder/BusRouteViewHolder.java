package me.rutgersdirect.rudirect.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.interfaces.ViewHolderClickListener;

public class BusRouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title;
    public ViewHolderClickListener mListener;

    public BusRouteViewHolder(View v, ViewHolderClickListener listener) {
        super(v);

        title = (TextView) v.findViewById(R.id.bus_route_name);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }
}