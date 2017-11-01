package com.jintoufs.zj.transfercabinet.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.ExampleImgAdapetr;
import com.jintoufs.zj.transfercabinet.adapter.PaperworkAdapter;
import com.jintoufs.zj.transfercabinet.model.bean.Paperwork;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 还证
 * Created by zj on 2017/9/6.
 */

public class ReturnActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_hand_do)
    Button btnHandDo;
    @BindView(R.id.rv_examples)
    RecyclerView rvExamples;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.rv_paperwork)
    RecyclerView rvPaperwork;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private PaperworkAdapter paperworkAdapter;
    private List<Paperwork> paperworkList;

    private ExampleImgAdapetr exampleImgAdapetr;
    private int[] imgs = {R.mipmap.empty_img, R.mipmap.empty_img, R.mipmap.empty_img, R.mipmap.empty_img};

    @Override
    public void initData() {
        super.initData();
        paperworkList = new ArrayList<>();
        Paperwork paperwork = new Paperwork();
        paperwork.setUsername("尚小涵");
        paperwork.setSex("女");
        paperwork.setBirthDate("1995.12.12");
        paperwork.setNation("汗族");
        paperwork.setIDNumber("510236511220233654");
        paperwork.setPhone("18363636548");
        paperwork.setAgency("外联部");
        paperwork.setType("身份证");
        paperwork.setNumber("510236511220233654");
        paperwork.setPhyNumber("101412554784");
        paperworkList.add(paperwork);
        paperworkAdapter = new PaperworkAdapter(this, paperworkList);

        exampleImgAdapetr = new ExampleImgAdapetr(this, imgs);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_return);
        ButterKnife.bind(this);
        rvPaperwork.setLayoutManager(new LinearLayoutManager(this));
        rvPaperwork.setAdapter(paperworkAdapter);

        rvExamples.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExamples.setAdapter(exampleImgAdapetr);

        tvTime.setText(getDateStr());
    }

    @OnClick({R.id.btn_back, R.id.btn_hand_do, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                break;
            case R.id.btn_hand_do:
                break;
            case R.id.btn_finish:
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
