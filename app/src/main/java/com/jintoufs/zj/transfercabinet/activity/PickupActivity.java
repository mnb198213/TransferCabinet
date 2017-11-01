package com.jintoufs.zj.transfercabinet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.jintoufs.zj.transfercabinet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 取件
 * Created by zj on 2017/9/6.
 */

public class PickupActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_number01)
    EditText etNumber01;
    @BindView(R.id.et_number02)
    EditText etNumber02;
    @BindView(R.id.et_number03)
    EditText etNumber03;
    @BindView(R.id.et_number04)
    EditText etNumber04;
    @BindView(R.id.et_number05)
    EditText etNumber05;
    @BindView(R.id.et_number06)
    EditText etNumber06;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @Override
    public void initView() {
        setContentView(R.layout.activity_pickup);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_sure, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                break;
            case R.id.btn_back:
                break;
        }
    }
}
