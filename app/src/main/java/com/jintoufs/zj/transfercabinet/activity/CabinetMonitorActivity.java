package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.DrawerAdapter;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 柜门监控
 * Created by zj on 2017/11/1.
 */

public class CabinetMonitorActivity extends BaseActivity {
    @BindView(R.id.tv_statue)
    TextView tvStatue;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.tv_cabinet_name)
    TextView tvCabinetName;
    @BindView(R.id.rv_cabinets)
    RecyclerView rvCabinets;
    @BindView(R.id.tv_paperwork_user)
    TextView tvPaperworkUser;
    @BindView(R.id.btn_open_single)
    Button btnOpenSingle;
    @BindView(R.id.btn_open_all)
    Button btnOpenAll;

    private Unbinder unbinder;
    private DrawerAdapter drawerAdapter;
    private int raw = 8;
    private int column = 4;
    private Context mContext;
    private List<Drawer> drawerList;

    @Override
    public void initData() {
        mContext = this;
        drawerList = new ArrayList<>();
        for (int i = 0; i < raw * column; i++) {
            Drawer drawer = new Drawer();
            drawer.setRaw(i / column + 1);
            drawer.setColumn(i % column);
            if (i % 3 == 0) {
                drawer.setState("1");
                drawer.setName("用户" + i);
                drawer.setDepartment("成都第" + i + "支行");
                drawer.setUserId(i + "2" + i + "5" + i + "125" + i + "2");
            } else {
                drawer.setState("0");
            }
            drawerList.add(drawer);
        }
        drawerAdapter = new DrawerAdapter(mContext, drawerList);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_cabinet_monitor);
        unbinder = ButterKnife.bind(this);
        tvPaperworkUser.setText("姓名：" + "null" +
                "\n\n所属机构：" + "null" +
                "\n\n" + "身份证号：" + "null");

        drawerAdapter.setOnItemDrawerClickListener(new DrawerAdapter.OnItemDrawerClickListener() {
            @Override
            public void onItemDrawerClick(int position) {
                drawerAdapter.notifyDataSetChanged();
                Drawer drawer = drawerList.get(position);
                tvPaperworkUser.setText("姓名：" + drawer.getName() +
                        "\n\n所属机构：" + drawer.getDepartment() +
                        "\n\n" + "身份证号：" + drawer.getUserId());
            }
        });
        rvCabinets.setLayoutManager(new GridLayoutManager(mContext, column));
        rvCabinets.setAdapter(drawerAdapter);

    }

    @OnClick({R.id.tv_statue, R.id.btn_back, R.id.btn_open_single, R.id.btn_open_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_statue:
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_open_single:
                break;
            case R.id.btn_open_all:
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
