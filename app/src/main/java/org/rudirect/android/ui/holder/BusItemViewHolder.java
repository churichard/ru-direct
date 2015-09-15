package org.rudirect.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.rudirect.android.R;
import org.rudirect.android.interfaces.ViewHolderClickListener;

public class BusItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title;
    public ImageView starImage;
    public ViewHolderClickListener mListener;

    public BusItemViewHolder(View v, ViewHolderClickListener listener) {
        super(v);

        title = (TextView) v.findViewById(R.id.bus_item_name);
        starImage = (ImageView) v.findViewById(R.id.star_image);
        mListener = listener;

        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getLayoutPosition());
    }
}