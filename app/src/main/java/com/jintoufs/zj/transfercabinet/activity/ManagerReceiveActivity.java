package com.jintoufs.zj.transfercabinet.activity;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.CabinetInfoAdapter;
import com.jintoufs.zj.transfercabinet.adapter.TitleAdapter;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfo;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.widget.SpacePwItemDecoration;
import com.jintoufs.zj.transfercabinet.widget.SpaceTitleItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 库管员取证
 * Created by zj on 2017/9/6.
 */

public class ManagerReceiveActivity extends BaseActivity {
    @BindView(R.id.tv_statue)
    TextView tvStatue;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_open_all_cabinet)
    Button btnOpenAllCabinet;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.rv_title_list)
    RecyclerView rvTitleList;
    @BindView(R.id.rv_paperwork_list)
    RecyclerView rvPaperworkList;

    private Unbinder unbinder;
    private Context mContext;

    private TitleAdapter titleAdapter;
    private CabinetInfoAdapter cabinetInfoAdapter;
    private List<CabinetInfo> cabinetInfoList;

    @Override
    public void initData() {
        super.initData();
        mContext = this;
        titleAdapter = new TitleAdapter(mContext, new String[]{"证件类型", "人员", "机构/部门", "身份证号", "交接柜", "柜门号", "操作"});

        cabinetInfoList = new ArrayList<>();
        CabinetInfo cabinetInfo = new CabinetInfo();
        cabinetInfo.setAgency("外联部");
        cabinetInfo.setType("港澳通行证");
        cabinetInfo.setCabinetId("1215");
        cabinetInfo.setIDNumber("510235662365412568");
        cabinetInfo.setUsername("李敖");
        cabinetInfo.setDrawerId("2106");
        cabinetInfoList.add(cabinetInfo);
        cabinetInfoList.add(cabinetInfo);
        cabinetInfoAdapter = new CabinetInfoAdapter(mContext, cabinetInfoList);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_riceive);
        unbinder = ButterKnife.bind(this);
        tvStatue.setText("张三 已登录");
        rvTitleList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvTitleList.addItemDecoration(new SpaceTitleItemDecoration(mContext, DensityUtil.dip2px(mContext, 1)));
        rvTitleList.setAdapter(titleAdapter);

        rvPaperworkList.setLayoutManager(new LinearLayoutManager(mContext));
        rvPaperworkList.addItemDecoration(new SpacePwItemDecoration(mContext, DensityUtil.dip2px(mContext, 1)));
        rvPaperworkList.setAdapter(cabinetInfoAdapter);
    }

    @OnClick({R.id.btn_back, R.id.btn_open_all_cabinet, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_open_all_cabinet:
                break;
            case R.id.btn_finish:
                ToastUtils.showShortToast(mContext, "库管员取件完成");
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
