package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.jintoufs.zj.transfercabinet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
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
    private boolean isLogin = false;
    private Intent mIntent;
    private Context mContext;

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
    }

    @OnClick({R.id.tv_statue, R.id.btn_back, R.id.tv_take, R.id.tv_save, R.id.tv_monitor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_statue:
                if (!isLogin) {
                    showLoginDialog();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_take:
                if (!isLogin) {
                    showLoginDialog();
                }else {
                    mIntent = new Intent(mContext, ManagerReceiveActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_save:
                if (!isLogin) {
                    showLoginDialog();
                }else {
                    mIntent = new Intent(mContext, ManagerCastActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_monitor:
                if (!isLogin) {
                    showLoginDialog();
                }else {
                    mIntent = new Intent(mContext, CabinetMonitorActivity.class);
                    startActivity(mIntent);
                }

                break;
        }
    }

    private void showLoginDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(this, R.layout.dialog_login_view, null);
        EditText et_username = (EditText) view.findViewById(R.id.et_username);
        EditText et_password = (EditText) view.findViewById(R.id.et_password);
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        //用户名和密码
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //库管员登录...
                isLogin = true;
                tvStatue.setText("张三  已登录");
                dialog.dismiss();
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
