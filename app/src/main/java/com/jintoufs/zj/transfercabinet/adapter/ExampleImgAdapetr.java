package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jintoufs.zj.transfercabinet.R;

import java.util.List;

/**
 * Created by zj on 2017/10/31.
 */

public class ExampleImgAdapetr extends RecyclerView.Adapter<ExampleImgAdapetr.ImgHolder> {
    private Context mContext;
    private int[] imgs;

    public ExampleImgAdapetr(Context mContext, int[] imgs) {
        this.mContext = mContext;
        this.imgs = imgs;
    }

    @Override
    public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImgHolder(View.inflate(mContext, R.layout.item_img_view, null));
    }

    @Override
    public void onBindViewHolder(ImgHolder holder, int position) {
        holder.img.setImageResource(imgs[position]);
    }

    @Override
    public int getItemCount() {
        return imgs.length;
    }

    class ImgHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ImgHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}
