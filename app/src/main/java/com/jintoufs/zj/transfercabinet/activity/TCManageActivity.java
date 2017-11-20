package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 交接柜管理
 * Created by zj on 2017/11/1.
 */

public class TCManageActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_statue)
    TextView tvStatue;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.tv_take)
    TextView tvTake;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_monitor)
    TextView tvMonitor;
    @BindView(R.id.tv_time)
    TextView tvTime;
    private Unbinder unbinder;
    private boolean isLogin = true;
    private Intent mIntent;
    private Context mContext;
    private String username;
    private String password;

    @Override
    public void initData() {
        super.initData();
        mContext = this;
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_tcmanage);
        unbinder = ButterKnife.bind(this);
        if (!isLogin) {
            tvStatue.setText("未登录");
        } else {
            tvStatue.setText("张三  已登录");
        }

        tvTime.setText("当前时间："+TimeUtil.DateToString(new Date()));
    }

    @OnClick({R.id.tv_statue, R.id.btn_back, R.id.tv_take, R.id.tv_save, R.id.tv_monitor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_statue:
                if (!isLogin) {
                    showLoginDialog(AppConstant.ACTION_NOTHING);
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_take:
                if (!isLogin) {
                    showLoginDialog(AppConstant.ACTION_TAKE);
                }else {
                    mIntent = new Intent(mContext, ManagerReceiveActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_save:
                if (!isLogin) {
                    showLoginDialog(AppConstant.ACTION_SAVE);
                }else {
                    mIntent = new Intent(mContext, ManagerCastActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_monitor:
                if (!isLogin) {
                    showLoginDialog(AppConstant.ACTION_MONITOR);
                }else {
                    mIntent = new Intent(mContext, CabinetMonitorActivity.class);
                    startActivity(mIntent);
                }

                break;
        }
    }

    private void showLoginDialog(final int action) {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(this, R.layout.dialog_login_view, null);
        final EditText et_username = (EditText) view.findViewById(R.id.et_username);
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户名和密码
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                //库管员登录...
                if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                    Call<ResponseInfo<User>> call = NetService.getApiService().login(username,password);
                    call.enqueue(new Callback<ResponseInfo<User>>() {
                        @Override
                        public void onResponse(Call<ResponseInfo<User>> call, Response<ResponseInfo<User>> response) {
                            isLogin = true;
                            User user = response.body().getData();
                            dialog.dismiss();
                            tvStatue.setText(user.getUserName()+"  已登录");
                            if (action == AppConstant.ACTION_NOTHING){
                                ToastUtils.showShortToast(mContext,"登录成功");
                            }else if (action == AppConstant.ACTION_SAVE){
                                mIntent = new Intent(mContext, ManagerCastActivity.class);
                                startActivity(mIntent);
                            }else if (action == AppConstant.ACTION_TAKE){
                                mIntent = new Intent(mContext, ManagerReceiveActivity.class);
                                startActivity(mIntent);
                            }else if (action == AppConstant.ACTION_MONITOR){
                                mIntent = new Intent(mContext, CabinetMonitorActivity.class);
                                startActivity(mIntent);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseInfo<User>> call, Throwable t) {
//                            Log.i(TAG,);
                            Logger.i("url:"+call.request().url()+"  error:"+t.getMessage());
                        }
                    });
                }else {
                    ToastUtils.showShortToast(mContext,"用户名或密码不能为空");
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消登录...
                dialog.dismiss();
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
