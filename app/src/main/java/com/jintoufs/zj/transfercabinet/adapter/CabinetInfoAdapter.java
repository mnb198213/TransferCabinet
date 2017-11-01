package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfo;

import java.util.List;

/**
 * Created by zj on 2017/11/1.
 */

public class CabinetInfoAdapter extends RecyclerView.Adapter<CabinetInfoAdapter.CabinetHolder> {
    private Context mContext;
    private List<CabinetInfo> cabinetInfoList;
    private OnOpenDrawerClickListener onOpenDrawerClickListener;

    public CabinetInfoAdapter(Context mContext, List<CabinetInfo> cabinetInfoList) {
        this.mContext = mContext;
        this.cabinetInfoList = cabinetInfoList;
    }

    @Override
    public CabinetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CabinetHolder(View.inflate(mContext, R.layout.item_cabinetinfo_view, null));
    }

    @Override
    public void onBindViewHolder(CabinetHolder holder, final int position) {
        CabinetInfo cabinetInfo = cabinetInfoList.get(position);
        holder.tv_type.setText(cabinetInfo.getType());
        holder.tv_username.setText(cabinetInfo.getUsername());
        holder.tv_agency.setText(cabinetInfo.getAgency());
        holder.tv_IDNumber.setText(cabinetInfo.getIDNumber());
        holder.tv_cabinetId.setText(cabinetInfo.getCabinetId());
        holder.tv_drawerId.setText(cabinetInfo.getDrawerId());
        holder.btn_open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOpenDrawerClickListener != null) {
                    onOpenDrawerClickListener.openDraw(position);
                }
            }
        });
    }

    interface OnOpenDrawerClickListener {
        void openDraw(int position);
    }

    public void setOnOpenDrawerClickListener(OnOpenDrawerClickListener onOpenDrawerClickListener) {
        this.onOpenDrawerClickListener = onOpenDrawerClickListener;
    }

    @Override
    public int getItemCount() {
        return cabinetInfoList.size();
    }

    class CabinetHolder extends RecyclerView.ViewHolder {
        TextView tv_type, tv_username, tv_agency, tv_IDNumber, tv_cabinetId, tv_drawerId;
        Button btn_open_drawer;

        public CabinetHolder(View itemView) {
            super(itemView);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_agency = (TextView) itemView.findViewById(R.id.tv_agency);
            tv_IDNumber = (TextView) itemView.findViewById(R.id.tv_IDNumber);
            tv_cabinetId = (TextView) itemView.findViewById(R.id.tv_cabinetId);
            tv_drawerId = (TextView) itemView.findViewById(R.id.tv_drawerId);
            btn_open_drawer = (Button) itemView.findViewById(R.id.btn_open_drawer);
        }
    }
}
