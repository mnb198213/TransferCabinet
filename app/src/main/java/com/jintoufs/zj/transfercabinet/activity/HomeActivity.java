package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.dialog.WaitDialog;
import com.jintoufs.zj.transfercabinet.model.CabinetInfoBeanModel;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_take)
    TextView tvTake;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_manager)
    TextView tvManager;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private Unbinder unbinder;

    private Intent mIntent;
    private Context mContext;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private CabinetInfoBeanModel cabinetInfoBeanModel;
    private boolean isFirst;
    private DBManager dbManager;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        waitDialog = new WaitDialog(this);
        dbManager = DBManager.getInstance(this);
        tvTime.setText("当前时间：" + TimeUtil.DateToString(new Date()));
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        isFirst = (boolean) sharedPreferencesHelper.get("isFirst", true);
        if (isFirst) {
            showInputCabinetIPDialog("交接柜IP：");
        }
        cabinetInfoBeanModel = new CabinetInfoBeanModel(this);
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
                waitDialog.show(mContext,"正在初始化，请稍等...");
                String CIP = et_input.getText().toString();
                Call<ResponseInfo<CabinetInfoBean>> call = NetService.getApiService().getTransferCabinetByIp(CIP);
                call.enqueue(new Callback<ResponseInfo<CabinetInfoBean>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<CabinetInfoBean>> call, Response<ResponseInfo<CabinetInfoBean>> response) {
                        waitDialog.dismiss();
                        ResponseInfo<CabinetInfoBean> responseInfo = response.body();
                        Logger.i("正常反馈");
                        if (responseInfo != null && responseInfo.getData() != null) {
                            CabinetInfoBean cabinetInfoBean = responseInfo.getData();
                            cabinetInfoBeanModel.saveCabinetInfoBeanBySP(cabinetInfoBean);
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
                                    CabinetInfo cabinetInfo = new CabinetInfo(null,"0", "0", "0", cabinetNumber, "0","0","0");
                                    cabinetInfoList.add(cabinetInfo);
                                }
                            }
                            //将每个柜子的状态信息加入到数据库
                            dbManager.insertCabinetInfoList(cabinetInfoList);
                            sharedPreferencesHelper.put("isFirst", false);
                            ToastUtils.showLongToast(mContext, "初始化柜子成功！");
                            dialog.dismiss();
                        } else
                            ToastUtils.showLongToast(mContext, "未获取到数据");
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<CabinetInfoBean>> call, Throwable t) {
                        waitDialog.dismiss();
                        Logger.i("失败："+t.getMessage());
                        ToastUtils.showLongToast(mContext,t.getMessage());
                    }
                });
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    @OnClick({R.id.tv_take, R.id.tv_save, R.id.tv_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_take:
                isFirst = (boolean) sharedPreferencesHelper.get("isFirst", true);
                if (isFirst) showInputCabinetIPDialog("交接柜IP：");
                else {
                    mIntent = new Intent(mContext, UserPickupActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_save:
                isFirst = (boolean) sharedPreferencesHelper.get("isFirst", true);
                if (isFirst) showInputCabinetIPDialog("交接柜IP：");
                else {
                    mIntent = new Intent(mContext, UserReturnActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_manager:
                isFirst = (boolean) sharedPreferencesHelper.get("isFirst", true);
                if (isFirst) showInputCabinetIPDialog("交接柜IP：");
                else {
                    mIntent = new Intent(mContext, TCManageActivity.class);
                    startActivity(mIntent);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
