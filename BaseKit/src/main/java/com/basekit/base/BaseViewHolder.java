package com.basekit.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Spirit on 2016/12/13 10:32.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(int position);
}
