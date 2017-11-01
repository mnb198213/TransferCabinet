package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2017/9/5.
 */

public class TestPortActivity extends SerialPortActivity {


    @BindView(R.id.tv_input)
    TextView tvInput;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_test_port);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("zoujiang", "收到的信息：" + new String(buffer).toString() + "    size:" + size);
                tvInput.setText(new String(buffer));
            }
        });
    }
}
