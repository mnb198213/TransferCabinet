package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

/**
 * Created by zj on 2017/11/1.
 */

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.TitleHolder> {

    private Context mContetx;
    private String[] titles;

    public TitleAdapter(Context mContetx, String[] titles) {
        this.mContetx = mContetx;
        this.titles = titles;
    }

    @Override
    public TitleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TitleHolder(View.inflate(mContetx, R.layout.item_title_view, null));
    }

    @Override
    public void onBindViewHolder(TitleHolder holder, int position) {
        holder.tv_title_item.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        TextView tv_title_item;

        public TitleHolder(View itemView) {
            super(itemView);
            tv_title_item = (TextView) itemView.findViewById(R.id.tv_title_item);
        }
    }
}
