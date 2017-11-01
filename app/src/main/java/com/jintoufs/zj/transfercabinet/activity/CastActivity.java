package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.CredentialListAdapter;
import com.jintoufs.zj.transfercabinet.widget.CustomToolBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2017/9/6.
 */

public class CastActivity extends SerialPortActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

    }


    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String info = new String(buffer).toString().trim();

            }
        });
    }
}
