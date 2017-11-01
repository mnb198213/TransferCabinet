package com.jintoufs.zj.transfercabinet.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jintoufs.zj.transfercabinet.R;

/**
 * Created by zj on 2017/11/1.
 */

public class SpacePwItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private Context mContext;

    public SpacePwItemDecoration(Context context, int space) {
        this.space = space;
        this.mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = space;
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDraw(c, parent, state);
        c.drawColor(mContext.getResources().getColor(R.color.smart_text_color));
    }
}
