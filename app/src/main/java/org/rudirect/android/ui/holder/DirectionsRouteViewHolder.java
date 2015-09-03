package org.rudirect.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.rudirect.android.R;
import org.rudirect.android.interfaces.ViewHolderClickListener;

public class DirectionsRouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Button route;
    public ViewHolderClickListener mListener;

    public DirectionsRouteViewHolder(View v, ViewHolderClickListener listener) {
        super(v);

        route = (Button) v.findViewById(R.id.directions_route_button);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }
}