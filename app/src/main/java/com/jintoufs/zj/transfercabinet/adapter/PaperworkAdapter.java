package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.model.bean.Paperwork;

import java.util.List;

/**
 * Created by zj on 2017/10/31.
 */

public class PaperworkAdapter extends RecyclerView.Adapter<PaperworkAdapter.PWHolder> {
    private Context mContext;
    private List<Paperwork> paperworkList;

    public PaperworkAdapter(Context mContext, List<Paperwork> paperworkList) {
        this.mContext = mContext;
        this.paperworkList = paperworkList;
    }

    @Override
    public PWHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PWHolder(View.inflate(mContext, R.layout.item_paperwork_view, null));
    }

    @Override
    public void onBindViewHolder(PWHolder holder, int position) {
        Paperwork paperwork = paperworkList.get(position);
        holder.tv_context.setText("姓名：" + paperwork.getUsername() + "      性别：" + paperwork.getSex() + "      出生日期：" + paperwork.getBirthDate() +
                "      民族：" + paperwork.getNation() + "\n身份证号：" + paperwork.getIDNumber() + "      联系电话：" + paperwork.getPhone() +
                "\n证件类型：" + paperwork.getType() + "      证件号：" + paperwork.getNumber() +
                "\n所属机构：" + paperwork.getAgency() +"      物理序号：" + paperwork.getPhyNumber());
//        holder.image.setImageResource(paperwork.getImage());
    }

    @Override
    public int getItemCount() {
        return paperworkList.size();
    }

    class PWHolder extends RecyclerView.ViewHolder {
        TextView tv_context;
        ImageView image;

        public PWHolder(View itemView) {
            super(itemView);
            tv_context = (TextView) itemView.findViewById(R.id.tv_context);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
