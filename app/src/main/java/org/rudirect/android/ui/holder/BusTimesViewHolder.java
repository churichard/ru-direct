package org.rudirect.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rudirect.android.interfaces.ViewHolderClickListener;
import org.rudirect.android.R;

public class BusTimesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title;
    public TextView times;
    public ViewHolderClickListener mListener;

    public BusTimesViewHolder(View v, ViewHolderClickListener listener) {
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
}