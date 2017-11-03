package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.DrawerAdapter;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.widget.SpaceDrawerItemDecoration;
import com.orhanobut.logger.Logger;

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
    private int raw = 12;
    private int column = 5;
    private Context mContext;
    private List<Drawer> drawerList;
    private Drawer drawer;

    private boolean isAllOpen = false;//判断是否全部打开

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
                drawer = drawerList.get(position);
                drawerAdapter.notifyDataSetChanged();
                tvPaperworkUser.setText("姓名：" + drawer.getName() +
                        "\n\n所属机构：" + drawer.getDepartment() +
                        "\n\n" + "身份证号：" + drawer.getUserId());
                if (drawer.isOpen()) {
                    btnOpenSingle.setText("关闭当前柜子");
                } else {
                    btnOpenSingle.setText("打开当前柜子");
                }
            }
        });
        rvCabinets.setLayoutManager(new GridLayoutManager(mContext, column));
        rvCabinets.addItemDecoration(new SpaceDrawerItemDecoration(mContext, DensityUtil.dip2px(mContext, 1)));
        rvCabinets.setAdapter(drawerAdapter);

        if (isAllOpen) {
            btnOpenAll.setText("打开所有柜子");
        } else {
            btnOpenAll.setText("关闭所有柜子");
        }
        btnOpenSingle.setText("打开单个柜子");
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
                if (drawer == null) {
                    ToastUtils.showShortToast(mContext, "未选中抽屉");
                    return;
                }
                if (drawer.isOpen()) {
                    btnOpenSingle.setText("打开当前柜子");
                    drawer.setOpen(false);
                } else {
                    btnOpenSingle.setText("关闭当前柜子");
                    drawer.setOpen(true);
                }
                drawerAdapter.clearSelected();
                drawerAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_open_all:
                if (!isAllOpen) {
                    showNoticeDialog("确定打开所有柜子");
                } else {
                    showNoticeDialog("确定关闭所有柜子");
                }
                break;
        }
    }

    private void showNoticeDialog(String info) {
        final Dialog dialog = new Dialog(mContext, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        View view = View.inflate(mContext, R.layout.dialog_open_all, null);
        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        TextView tv_try_again = (TextView) view.findViewById(R.id.tv_try_again);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_message.setText(info);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllOpen) {
                    //打开所有柜门
                    for (Drawer drawer : drawerList) {
                        drawer.setOpen(true);
                    }
                    isAllOpen = true;
                    btnOpenAll.setText("关闭所有柜子");
                } else {
                    //关闭所有柜子
                    for (Drawer drawer : drawerList) {
                        drawer.setOpen(false);
                    }
                    isAllOpen = false;
                    btnOpenAll.setText("打开所有柜子");
                }
                drawerAdapter.clearSelected();
                drawerAdapter.notifyDataSetChanged();
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
