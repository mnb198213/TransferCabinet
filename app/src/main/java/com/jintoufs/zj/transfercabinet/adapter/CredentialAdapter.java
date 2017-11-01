package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

import java.util.List;

/**
 * Created by zj on 2017/9/19.
 */

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.MyViewHolder> {

    private Context context;
    private List<String> infos;

    public CredentialAdapter(Context context, List<String> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(context, R.layout.credential_item_view, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText(infos.get(position));
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
