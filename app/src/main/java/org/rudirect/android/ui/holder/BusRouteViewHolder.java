package org.rudirect.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rudirect.android.R;
import org.rudirect.android.interfaces.ViewHolderClickListener;

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