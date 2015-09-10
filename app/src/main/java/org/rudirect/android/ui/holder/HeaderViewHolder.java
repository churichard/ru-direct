package org.rudirect.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rudirect.android.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView title;

    public HeaderViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.recyclerview_header);
    }
}