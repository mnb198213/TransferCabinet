package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;

import java.util.List;

/**
 * Created by zj on 2017/11/1.
 */

public class CabinetInfoAdapter extends RecyclerView.Adapter<CabinetInfoAdapter.CabinetHolder> {
    private Context mContext;
    private List<CabinetInfo> cabinetInfoBeanList;
    private OnOpenDrawerClickListener onOpenDrawerClickListener;

    public CabinetInfoAdapter(Context mContext, List<CabinetInfo> cabinetInfoBeanList) {
        this.mContext = mContext;
        this.cabinetInfoBeanList = cabinetInfoBeanList;
    }

    @Override
    public CabinetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CabinetHolder(View.inflate(mContext, R.layout.item_cabinetinfo_view, null));
    }

    @Override
    public void onBindViewHolder(CabinetHolder holder, final int position) {
        CabinetInfo cabinetInfoBean = cabinetInfoBeanList.get(position);
        //识别证件类型
//        if ("0".equals(cabinetInfoBean.getType())) {
//            holder.tv_type.setText();
//        } else if ("".equals(cabinetInfoBean.getType())) {
//            holder.tv_type.setText();
//        } else if ("".equals(cabinetInfoBean.getType())) {
//            holder.tv_type.setText();
//        }

        holder.tv_type.setText("港澳通行证");
        holder.tv_username.setText(cabinetInfoBean.getUsername());
        holder.tv_agency.setText(cabinetInfoBean.getDepartment());
        holder.tv_IDNumber.setText(cabinetInfoBean.getUserIdCard());
        String cabinetNumber = cabinetInfoBean.getCabinetNumber();
//        交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
        String[] strs = cabinetNumber.split(",");
        if (strs.length == 3) {
            holder.tv_cabinetId.setText(strs[0]);
            if (strs[1].length() == 1) {
                strs[1] = "0" + strs[1];
            }
            if (strs[2].length() == 1) {
                strs[2] = "0" + strs[2];
            }
            holder.tv_drawerId.setText(strs[1] + strs[2]);
            holder.btn_open_drawer.setEnabled(true);
        } else {
            holder.btn_open_drawer.setEnabled(false);
            holder.tv_cabinetId.setText("数据出错");
            holder.tv_drawerId.setText("数据出错");
        }
        holder.btn_open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOpenDrawerClickListener != null) {
                    onOpenDrawerClickListener.openDraw(position);
                }
            }
        });
    }

    public interface OnOpenDrawerClickListener {
        void openDraw(int position);
    }

    public void setOnOpenDrawerClickListener(OnOpenDrawerClickListener onOpenDrawerClickListener) {
        this.onOpenDrawerClickListener = onOpenDrawerClickListener;
    }

    @Override
    public int getItemCount() {
        return cabinetInfoBeanList.size();
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
