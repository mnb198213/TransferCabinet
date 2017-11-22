package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Boolean> isSelected;
    private OnItemDrawerClickListener onItemDrawerClickListener;

    public DrawerAdapter(Context mContext, List<Drawer> drawers) {
        this.mContext = mContext;
        this.drawerList = drawers;
        isSelected = new ArrayList<>();
        clearSelected();
    }

    public void clearSelected() {
        isSelected.clear();
        for (int i = 0; i < drawerList.size(); i++) {
            isSelected.add(false);
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
        if (statue.equals("0") && !isSelected.get(position) && !drawer.isOpen()) {//空柜子,未选中，未打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.null_color));
        } else if (statue.equals("0") && isSelected.get(position) && !drawer.isOpen()) {//空柜子,已选中，未打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else if (statue.equals("0") && !isSelected.get(position) && drawer.isOpen()) {//空柜子,未选中，已打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.opened_color));
        } else if (statue.equals("0") && isSelected.get(position) && drawer.isOpen()) {//空箱子，已选中，已打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else if (statue.equals("1") && !isSelected.get(position) && !drawer.isOpen()) {//已使用的柜子,未选中，未打开
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.saved_color));
            holder.tv_name.setText(drawer.getName());
        } else if (statue.equals("1") && isSelected.get(position) && !drawer.isOpen()) {//已使用的柜子,已选中，未打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else if (statue.equals("1") && !isSelected.get(position) && drawer.isOpen()) {//已使用的柜子,未选中，已打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.opened_color));
        } else if (statue.equals("1") && isSelected.get(position) && drawer.isOpen()) {//已使用的柜子,已选中，已打开
            holder.tv_name.setText(drawer.getName());
            holder.tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        }
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelected();
                isSelected.set(position, true);
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
        private TextView tv_name;

        public DrawHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
