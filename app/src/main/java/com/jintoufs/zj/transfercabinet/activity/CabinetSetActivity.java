package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.model.CabinetInfoBeanModel;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zj on 2017/11/27.
 */

public class CabinetSetActivity extends BaseActivity {

    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.btn_change)
    Button btnChange;
    private Unbinder unbinder;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private CabinetInfoBean cabinetInfoBean;
    private CabinetInfoBeanModel cabinetInfoBeanModel;
    private DBManager dbManager;
    private Context mContext;

    @Override
    public void initData() {
        mContext = this;
        cabinetInfoBeanModel = new CabinetInfoBeanModel(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        dbManager = DBManager.getInstance(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_cabinet_set);
        unbinder = ButterKnife.bind(this);
        loadCabinetInfo();
    }

    private void loadCabinetInfo() {
        cabinetInfoBean = cabinetInfoBeanModel.getCabinetInfoBean();
        tvInfo.setText("编号：" + cabinetInfoBean.getSerialNo() +
                "\n名称：" + cabinetInfoBean.getName() +
                "\nIP:" + cabinetInfoBean.getIpAddress() +
                "\n位置：" + cabinetInfoBean.getLocation() +
                "\n行列数：" + cabinetInfoBean.getRow() + "," + cabinetInfoBean.getCol() +
                "\n机构名称：" + cabinetInfoBean.getOrgName());
    }

    @OnClick(R.id.btn_change)
    public void onViewClicked() {
        showInputCabinetIPDialog("交接柜IP:");
    }

    private void showInputCabinetIPDialog(String info) {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(mContext, R.layout.dialog_input_view, null);
        final EditText et_input = (EditText) view.findViewById(R.id.et_input);
        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
        tv_info.setText(info);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CIP = et_input.getText().toString();
                Call<ResponseInfo<CabinetInfoBean>> call = NetService.getApiService().getTransferCabinetByIp(CIP);
                call.enqueue(new Callback<ResponseInfo<CabinetInfoBean>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<CabinetInfoBean>> call, Response<ResponseInfo<CabinetInfoBean>> response) {
                        ResponseInfo<CabinetInfoBean> responseInfo = response.body();
                        Logger.i("正常反馈");
                        if (responseInfo != null && responseInfo.getData() != null) {
                            CabinetInfoBean cabinetInfoBean = responseInfo.getData();
                            cabinetInfoBeanModel.saveCabinetInfoBeanBySP(cabinetInfoBean);
                            loadCabinetInfo();//加载柜子新数据

                            List<CabinetInfo> cabinetInfoList = new ArrayList<CabinetInfo>();
                            int col = Integer.valueOf(cabinetInfoBean.getCol());
                            int row = Integer.valueOf(cabinetInfoBean.getRow());
                            String id = cabinetInfoBean.getSerialNo();
                            for (int i = 1; i < (row + 1); i++) {
                                for (int j = 1; j < (col + 1); j++) {
                                    //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
                                    String cabinetNumber = id + "," + i + "," + j;
//                                    CabinetInfo(Long id, String userIdCard, String username,
//                                            String department, String cabinetNumber, String paperworkId, String type)
                                    CabinetInfo cabinetInfo = new CabinetInfo(null, "0", "0", "0", cabinetNumber, "0", "0");
                                    cabinetInfoList.add(cabinetInfo);
                                }
                            }
                            //将每个柜子的状态信息加入到数据库
                            dbManager.insertCabinetInfoList(cabinetInfoList);
                            sharedPreferencesHelper.put("isFirst", false);
                            ToastUtils.showLongToast(mContext, "新柜子数据加载成功！");
                            dialog.dismiss();
                        } else
                            ToastUtils.showLongToast(mContext, "未获取到数据");
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<CabinetInfoBean>> call, Throwable t) {
                        Logger.i("失败：" + t.getMessage());
                        ToastUtils.showLongToast(mContext, t.getMessage());
                    }
                });
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
