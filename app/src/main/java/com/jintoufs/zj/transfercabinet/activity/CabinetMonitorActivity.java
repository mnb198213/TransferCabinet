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
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.model.CabinetInfoBeanModel;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.widget.SpaceDrawerItemDecoration;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private int raw;
    private int column;
    private Context mContext;
    private List<Drawer> drawerList;
    private Drawer drawer;

    private boolean isAllOpen = false;//判断是否全部打开
    private DBManager dbManager;
    private CabinetInfoBeanModel cabinetInfoBeanModel;
    private User user;//库管员
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String serialNo;

    @Override
    public void initData() {
        mContext = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        user = getIntent().getParcelableExtra("User");
        dbManager = DBManager.getInstance(mContext);
        cabinetInfoBeanModel = new CabinetInfoBeanModel(mContext);
        List<CabinetInfo> cabinetInfoList = dbManager.queryAllCabinetInfos();
        CabinetInfoBean cabinetInfoBean = cabinetInfoBeanModel.getCabinetInfoBean();
        raw = Integer.valueOf(cabinetInfoBean.getRow());
        column = Integer.valueOf(cabinetInfoBean.getCol());

        drawerList = new ArrayList<>();
        for (int i = 0; i < cabinetInfoList.size(); i++) {
            Drawer drawer = new Drawer();
            drawer.setRaw(i / column + 1);
            drawer.setColumn(i % column+1);
            CabinetInfo cabinetInfo = cabinetInfoList.get(i);
            if (cabinetInfo.getPaperworkId().equals("0") && cabinetInfo.getUserIdCard().equals("0")) {
                drawer.setState("0");
            } else {
                drawer.setState("1");
            }
            drawer.setName(cabinetInfo.getUsername());
            drawer.setUserId(cabinetInfo.getUserIdCard());
            drawer.setDepartment(cabinetInfo.getDepartment());
            drawer.setOpen(false);
            drawerList.add(drawer);
        }
        drawerAdapter = new DrawerAdapter(mContext, drawerList);
        serialNo = (String) sharedPreferencesHelper.get("SerialNo", null);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_cabinet_monitor);
        unbinder = ButterKnife.bind(this);
        tvPaperworkUser.setText("姓名：" + "null" +
                "\n\n所属机构：" + "null" +
                "\n\n" + "身份证号：" + "null");
        if (user != null) {
            tvStatue.setText(user.getUserName() + " 已登录");
        } else {
            tvStatue.setText("管理员信息加载失败");
        }

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
                for (int i = 0; i < drawerList.size(); i++) {
                    if (drawerList.get(i).isOpen()) {
                        ToastUtils.showShortToast(mContext, "请确保所有箱子都已关闭");
                        return;
                    }
                }
                finish();
                break;
            case R.id.btn_open_single:
                if (drawer == null) {
                    ToastUtils.showShortToast(mContext, "未选中抽屉");
                    return;
                }
                if (drawer.isOpen()) {//已打开

                    //关闭柜子操作
                    //、、、、、、、、、、、、、

                    btnOpenSingle.setText("打开当前柜子");
                    drawer.setOpen(false);
                } else {

                    //打开柜子操作
                    //、、、、、、、、、、、
                    if (serialNo == null) {
                        ToastUtils.showShortToast(mContext, "柜子编号获取失败！");
                        return;
                    }
                    String row = String.valueOf(drawer.getRaw());
                    if (row.length() == 1) {
                        row = "0" + row;
                    }
                    String col = String.valueOf(drawer.getColumn());
                    if (col.length() == 1) {
                        col = "0" + col;
                    }
                    Call<ResponseInfo<String>> call = NetService.getApiService().tccMaintainSubmit(user.getUserId(), "1", serialNo, row + col);
                    call.enqueue(new Callback<ResponseInfo<String>>() {
                        @Override
                        public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                            if ("200".equals(response.body().getCode())) {
                                Logger.i("提交成功");
                            } else {
                                Logger.i("提交失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                            Logger.i("异常：" + t.getMessage());
                        }
                    });
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
                    //打开所有柜门操作
                    //、、、、、、、、、、、、、
                    Call<ResponseInfo<String>> call = NetService.getApiService().tccMaintainSubmit(user.getUserId(), "2", serialNo,null);
                    call.enqueue(new Callback<ResponseInfo<String>>() {
                        @Override
                        public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                            if ("200".equals(response.body().getCode())) {
                                Logger.i("提交成功");
                            } else {
                                Logger.i("提交失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                            Logger.i("异常：" + t.getMessage());
                        }
                    });
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
