package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.util.Base64Util;

import java.util.List;

/**
 * Created by zj on 2017/10/31.
 */

public class PaperworkAdapter extends RecyclerView.Adapter<PaperworkAdapter.PWHolder> {
    private Context mContext;
    private List<CertificateVo> paperworkList;
    private ReEnterClickListener reEnterClickListener;
    private OpenCabinetClickListener openCabinetClickListener;
    private CloseCabinetClickListener closeCabinetClickListener;
    private PWHolder tempHolder;


    public PaperworkAdapter(Context mContext, List<CertificateVo> paperworkList) {
        this.mContext = mContext;
        this.paperworkList = paperworkList;
    }

    @Override
    public PWHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PWHolder(View.inflate(mContext, R.layout.item_paperwork_view, null));
    }

    @Override
    public void onBindViewHolder(final PWHolder holder, final int position) {
        CertificateVo paperwork = paperworkList.get(position);
        holder.tv_context.setText("姓名：" + paperwork.getUserName() + "      性别：" + paperwork.getSex() + "      出生日期：" + paperwork.getBornDate() +
                "      民族：" + paperwork.getNation() + "\n身份证号：" + paperwork.getIdCard() + "      联系电话：" + paperwork.getPhone() +
                "\n证件类型：" + paperwork.getType() + "      证件号：" + paperwork.getNumber() +
                "\n所属机构：" + paperwork.getOrgName());
        Bitmap bitmap = Base64Util.stringtoBitmap(paperwork.getImage());
        //压缩图片
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            temp.setConfig(Bitmap.Config.ARGB_8888);
        }
        temp.setHasAlpha(true);
        if (bitmap != null) {
            holder.image.setImageBitmap(temp);
        } else {
            holder.image.setImageResource(R.mipmap.empty_img);
        }
//        Glide.with(mContext).load(bitmap).centerCrop().placeholder(R.mipmap.empty_img).into(holder.image);

        holder.btn_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reEnterClickListener != null) {
                    reEnterClickListener.reEnter(position);
                }
            }
        });
        holder.btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempHolder = holder;
                if (openCabinetClickListener != null) {
                    openCabinetClickListener.openCabinet(position);
                }
            }
        });
        holder.btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeCabinetClickListener != null) {
                    closeCabinetClickListener.closeCabinet(position);
                }
            }
        });
    }

    public void surePaperWorkToSave() {
        tempHolder.btn_save.setVisibility(View.GONE);
        tempHolder.btn_close.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return paperworkList.size();
    }

    public interface ReEnterClickListener {
        void reEnter(int position);
    }

    public interface OpenCabinetClickListener {
        void openCabinet(int position);
    }

    public interface CloseCabinetClickListener {
        void closeCabinet(int position);
    }

    public void setReEnterClickListener(ReEnterClickListener reEnterClickListener) {
        this.reEnterClickListener = reEnterClickListener;
    }

    public void setOpenCabinetClickListener(OpenCabinetClickListener openCabinetClickListener) {
        this.openCabinetClickListener = openCabinetClickListener;
    }

    public void setCloseCabinetClickListener(CloseCabinetClickListener closeCabinetClickListener) {
        this.closeCabinetClickListener = closeCabinetClickListener;
    }

    class PWHolder extends RecyclerView.ViewHolder {
        TextView tv_context;
        ImageView image;
        Button btn_save;
        Button btn_re;
        Button btn_close;

        public PWHolder(View itemView) {
            super(itemView);
            tv_context = (TextView) itemView.findViewById(R.id.tv_context);
            image = (ImageView) itemView.findViewById(R.id.image);
            btn_save = (Button) itemView.findViewById(R.id.btn_save);
            btn_re = (Button) itemView.findViewById(R.id.btn_re);
            btn_close = (Button) itemView.findViewById(R.id.btn_close);
        }
    }
}
