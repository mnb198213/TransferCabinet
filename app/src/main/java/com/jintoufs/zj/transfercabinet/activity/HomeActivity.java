package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private Intent mIntent;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mContext = this;
        tvTime.setText(getDateStr());
    }

    @OnClick({R.id.tv_take, R.id.tv_save, R.id.tv_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_take:
                mIntent = new Intent(mContext, PickupActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_save:
                mIntent = new Intent(mContext, ReturnActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_manager:
                break;
        }
    }

    private String getDateStr() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String strDate = simpleDateFormat.format(date);
        if (strDate != null) {
            return strDate;
        } else {
            return "当前时间未获取";
        }
    }
}
