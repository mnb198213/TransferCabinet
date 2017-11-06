package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        tvTime.setText(TimeUtil.DateToString(new Date()));
    }

    @OnClick({R.id.tv_take, R.id.tv_save, R.id.tv_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_take:
                mIntent = new Intent(mContext, UserPickupActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_save:
                mIntent = new Intent(mContext, UserReturnActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_manager:
                mIntent = new Intent(mContext, TCManageActivity.class);
                startActivity(mIntent);
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
