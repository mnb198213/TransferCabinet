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
import com.baselib.http.util.GsonHelper;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.ExampleImgAdapetr;
import com.jintoufs.zj.transfercabinet.adapter.PaperworkAdapter;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.model.bean.PoVo;
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
    private boolean isAction = false;
    private List<PoVo> poVoList;
    private DBManager dbManager;

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
        dbManager = DBManager.getInstance(this);
        paperworkList = new ArrayList<>();

        paperworkAdapter = new PaperworkAdapter(this, paperworkList);

        exampleImgAdapetr = new ExampleImgAdapetr(this, imgs);

        poVoList = new ArrayList<>();
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
                if (isAction) {
                    ToastUtils.showShortToast(mContext, "请点击完成，确认存证完成");
                } else {
                    finish();
                }
                finish();
                break;
            case R.id.btn_hand_do:
                showInputPaperworkId("证件号：");
                break;
            case R.id.btn_finish:
                String strPovoList = GsonHelper.objectToJSONString(poVoList);
                Call<ResponseInfo<String>> call = NetService.getApiService().tccInSubmit(strPovoList, null);
                call.enqueue(new Callback<ResponseInfo<String>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                        if ("200".equals(response.body().getCode())) {
                            ToastUtils.showShortToast(mContext, "存证记录提交成功！");
                            btnFinish.setVisibility(View.GONE);
                            isAction = false;
                        } else {
                            ToastUtils.showShortToast(mContext, "存证记录提交失败！");
                            btnFinish.setVisibility(View.VISIBLE);
                            isAction = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                        Logger.i("异常：" + t.getMessage());
                    }
                });
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
                        Logger.i("正常返回");
                        if (response.body() != null) {
                            ResponseInfo<CertificateVo> responseInfo = response.body();
                            if ("200".equals(responseInfo.getCode())) {
                                CertificateVo certificateVo = response.body().getData();
                                if (certificateVo == null) {
                                    ToastUtils.showLongToast(mContext, "证件信息加载出错");
                                    return;
                                }
                                CabinetInfo cabinetInfo = dbManager.queryEmptyCabinet();
                                if (cabinetInfo == null) {
                                    ToastUtils.showLongToast(mContext, "抱歉，当前没有空柜可用！");
                                    return;
                                }
                                String cabinetNumber = cabinetInfo.getCabinetNumber();
                                String[] strs = cabinetNumber.split(",");
                                String row = strs[1];
                                String col = strs[2];
                                //打开柜子，放入证件，关上柜子
                                //、、、、、、、、、、、、、、、、、、、、
                                //、、、、、、、、、、、、、、、、、、

                                cabinetInfo.setUserIdCard(certificateVo.getIdCard());
                                cabinetInfo.setPaperworkId(certificateVo.getNumber());
                                cabinetInfo.setDepartment(certificateVo.getOrgName());
                                cabinetInfo.setType(certificateVo.getType());
                                cabinetInfo.setUsername(certificateVo.getUserName());
                                dbManager.updateCabinetInfo(cabinetInfo);

                                PoVo poVo = new PoVo();
                                poVo.setCabinetSerialNo(strs[0]);
                                if (row.length() == 1)
                                    row = "0" + strs[1];
                                if (col.length() == 1)
                                    col = "0" + strs[2];
                                poVo.setLocationCode(row + col);
                                poVo.setNumber(cabinetInfo.getPaperworkId());
                                poVoList.add(poVo);

                                isAction = true;
                                btnFinish.setVisibility(View.VISIBLE);
                                paperworkList.add(certificateVo);
                                paperworkAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else {
                                Logger.i("请求码：" + responseInfo.getCode() + responseInfo.getMsg());
                                ToastUtils.showLongToast(mContext, responseInfo.getMsg());
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
