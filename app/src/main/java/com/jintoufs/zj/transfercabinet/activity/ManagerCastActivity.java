package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 库管员存证
 * Created by zj on 2017/9/6.
 */

public class ManagerCastActivity extends AppCompatActivity {


    @BindView(R.id.tv_statue)
    TextView tvStatue;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_hand_do)
    Button btnHandDo;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.tv_context)
    TextView tvContext;
    @BindView(R.id.tv_cab_location)
    TextView tvCabLocation;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    private Unbinder unbinder;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        tvStatue.setText("张三 已登录");
        tvContext.setText("姓名：" + "张三" + "      性别：" + "男" + "      出生日期：" + "1968.12.12" + "      民族：" + "汗族" +
                "\n身份证号：" + "510323654552322562" + "      联系电话：" + "12365652545" + "      所属机构：" + "外联部" +
                "\n证件类型：" + "港澳通行证" + "      证件号：" + "21135453453453");
        tvCabLocation.setText("交接柜：" + "1225" + "      柜门号：" + "1023");
    }

    @OnClick({R.id.btn_back, R.id.btn_hand_do, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_hand_do:
                break;
            case R.id.btn_finish:
                ToastUtils.showShortToast(mContext, "完成本次存证");
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
