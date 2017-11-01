package com.jintoufs.zj.transfercabinet.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zj on 2017/11/1.
 */

public class SpaceItemLeftDecoration extends RecyclerView.ItemDecoration {
    int space;

    public SpaceItemLeftDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) !=0)
            outRect.left = space;
    }
}
