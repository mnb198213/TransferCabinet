package com.jintoufs.zj.transfercabinet.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jintoufs.zj.transfercabinet.R;

/**
 * Created by zj on 2017/11/3.
 */

public class SpaceDrawerItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;

    public SpaceDrawerItemDecoration(Context context, int space) {
        this.context = context;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.top = space;
        outRect.right = space;
        outRect.bottom = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.drawColor(context.getResources().getColor(R.color.white));
    }
}
