package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * 库管员存证
 * Created by zj on 2017/9/6.
 */

public class ManagerCastActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

    }
}
