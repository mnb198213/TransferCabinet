package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2017/11/2.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawHolder> {
    private Context mContext;
    private List<Drawer> drawerList;
    private boolean isOpened = false;
    private List<Boolean> isSelecteds;
    private OnItemDrawerClickListener onItemDrawerClickListener;

    public DrawerAdapter(Context mContext, List<Drawer> drawers) {
        this.mContext = mContext;
        this.drawerList = drawers;
        isSelecteds = new ArrayList<>();
        for (int i = 0; i < drawers.size(); i++) {
            isSelecteds.add(false);
        }
    }

    @Override
    public DrawHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawHolder(View.inflate(mContext, R.layout.item_draw_viewer, null));
    }

    @Override
    public void onBindViewHolder(DrawHolder holder, final int position) {
        Drawer drawer = drawerList.get(position);
        String statue = drawer.getState();
        if (statue.equals("0") && !isSelecteds.get(position) && !isOpened) {//空柜子,未选中，未打开
            holder.tv_name.setVisibility(View.INVISIBLE);
        } else if (statue.equals("0") && !isSelecteds.get(position) && !isOpened) {//空柜子,已选中，未打开
            holder.tv_name.setVisibility(View.INVISIBLE);
            holder.iv_drawer.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else if (statue.equals("0") && !isSelecteds.get(position) && !isOpened) {//空柜子,未选中，已打开
            holder.tv_name.setVisibility(View.INVISIBLE);
            holder.iv_drawer.setBackgroundColor(mContext.getResources().getColor(R.color.opened_color));
        } else if (statue.equals("1") && !isSelecteds.get(position) && !isOpened) {//已使用的柜子,未选中，未打开
            holder.tv_name.setVisibility(View.INVISIBLE);
            holder.iv_drawer.setBackgroundColor(mContext.getResources().getColor(R.color.saved_color));
        } else if (statue.equals("1") && isSelecteds.get(position) && !isOpened) {//已使用的柜子,已选中，未打开
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.tv_name.setText(drawer.getName());
            holder.iv_drawer.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else if (statue.equals("1") && !isSelecteds.get(position) && isOpened) {//已使用的柜子,未选中，已打开
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.tv_name.setText(drawer.getName());
            holder.iv_drawer.setBackgroundColor(mContext.getResources().getColor(R.color.opened_color));
        }
        holder.fl_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelecteds.clear();
                for (int i = 0; i < drawerList.size(); i++) {
                    isSelecteds.add(false);
                }
                isSelecteds.set(position, true);
                if (onItemDrawerClickListener != null) {
                    onItemDrawerClickListener.onItemDrawerClick(position);
                }
            }
        });
    }

    public interface OnItemDrawerClickListener {
        void onItemDrawerClick(int position);
    }

    public void setOnItemDrawerClickListener(OnItemDrawerClickListener onItemDrawerClickListener) {
        this.onItemDrawerClickListener = onItemDrawerClickListener;
    }

    @Override
    public int getItemCount() {
        return drawerList.size();
    }

    class DrawHolder extends RecyclerView.ViewHolder {
        private ImageView iv_drawer;
        private TextView tv_name;
        private FrameLayout fl_drawer;

        public DrawHolder(View itemView) {
            super(itemView);
            iv_drawer = (ImageView) itemView.findViewById(R.id.iv_drawer);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            fl_drawer = (FrameLayout) itemView.findViewById(R.id.fl_drawer);
        }
    }
}
