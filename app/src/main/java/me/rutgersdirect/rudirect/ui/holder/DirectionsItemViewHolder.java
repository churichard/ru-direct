package me.rutgersdirect.rudirect.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.interfaces.DirectionsViewHolderClick;

public class DirectionsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView icon;
    public TextView title;
    public TextView time;
    public DirectionsViewHolderClick mListener;

    public DirectionsItemViewHolder(View v, DirectionsViewHolderClick listener) {
        super(v);

        icon = (ImageView) v.findViewById(R.id.directions_icon);
        title = (TextView) v.findViewById(R.id.directions_title);
        time = (TextView) v.findViewById(R.id.directions_time);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }
}