package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.ExampleImgAdapetr;
import com.jintoufs.zj.transfercabinet.adapter.PaperworkAdapter;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;
import com.jintoufs.zj.transfercabinet.widget.SpaceItemLeftDecoration;
import com.jintoufs.zj.transfercabinet.widget.SpaceItemTopDecoration;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 申领人存证
 * Created by zj on 2017/9/6.
 */

public class UserReturnActivity extends BaseActivity {

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
    private Unbinder unbinder;
    private PaperworkAdapter paperworkAdapter;
    private List<CertificateVo> paperworkList;
    private Context mContext;

    private ExampleImgAdapetr exampleImgAdapetr;
    private int[] imgs = {R.mipmap.empty_img, R.mipmap.empty_img, R.mipmap.empty_img, R.mipmap.empty_img};

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String time = TimeUtil.DateToString(new Date());
                if (tvTime != null)
                    tvTime.setText("当前时间：" + time);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void initData() {
        super.initData();
        mContext = this;
        paperworkList = new ArrayList<>();

        paperworkAdapter = new PaperworkAdapter(this, paperworkList);

        exampleImgAdapetr = new ExampleImgAdapetr(this, imgs);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_return);
        unbinder = ButterKnife.bind(this);
        rvPaperwork.setLayoutManager(new LinearLayoutManager(this));
        rvPaperwork.addItemDecoration(new SpaceItemTopDecoration(DensityUtil.dip2px(this, 10)));
        rvPaperwork.setAdapter(paperworkAdapter);

        rvExamples.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExamples.addItemDecoration(new SpaceItemLeftDecoration(DensityUtil.dip2px(this, 10)));
        rvExamples.setAdapter(exampleImgAdapetr);
        btnFinish.setVisibility(View.INVISIBLE);
        new TimeThread().start();
    }

    @OnClick({R.id.btn_back, R.id.btn_hand_do, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_hand_do:
                showInputPaperworkId("证件号：");
                break;
            case R.id.btn_finish:
                ToastUtils.showShortToast(mContext, "存证完成");
                break;
        }
    }

    private void showInputPaperworkId(String info) {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(mContext, R.layout.dialog_input_view, null);
        final EditText et_input = (EditText) view.findViewById(R.id.et_input);
        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
        tv_info.setText(info);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paperworkId = et_input.getText().toString().trim();
                if (TextUtils.isEmpty(paperworkId)) {
                    ToastUtils.showShortToast(mContext, "证件号不能为空");
                    return;
                }
                Call<ResponseInfo<CertificateVo>> call = NetService.getApiService().getCertificateByNumber(paperworkId);
                call.enqueue(new Callback<ResponseInfo<CertificateVo>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<CertificateVo>> call, Response<ResponseInfo<CertificateVo>> response) {
                        if (response.body() != null) {
                            ResponseInfo<CertificateVo> responseInfo = response.body();
                            if ("200".equals(responseInfo.getCode())) {
                                CertificateVo certificateVo = response.body().getData();
                                if (certificateVo != null) {
                                    Logger.i("certificateVo != null");
                                    paperworkList.add(certificateVo);
                                    paperworkAdapter.notifyDataSetChanged();
                                } else {
                                    Logger.i("certificateVo == null");
                                }
                            } else {
                                Logger.i("请求码：" + responseInfo.getCode() + responseInfo.getMsg());
                            }
                        } else {
                            Logger.i("response.body() == null");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<CertificateVo>> call, Throwable t) {
                        Logger.i("url:" + call.request().url() + "  error:" + t.getMessage());
                    }
                });
            }
        });
        window.setContentView(view);
        dialog.show();
    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message message = Message.obtain();
                    message.what = 1;
                    mHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
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
