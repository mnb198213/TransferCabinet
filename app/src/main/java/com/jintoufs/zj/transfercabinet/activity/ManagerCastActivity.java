package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basekit.util.ToastUtils;
import com.baselib.http.util.GsonHelper;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.PaperworkAdapter;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.model.bean.PoVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.widget.SpaceItemTopDecoration;
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
 * 库管员存证
 * Created by zj on 2017/9/6.
 */

public class ManagerCastActivity extends AppCompatActivity {


    @BindView(R.id.tv_statue)
    TextView tvStatue;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_hand_do)
    Button btnHandDo;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.rv_paperwork)
    RecyclerView rvPaperwork;

    private Unbinder unbinder;
    private Context mContext;
    private PaperworkAdapter paperworkAdapter;
    private List<CertificateVo> paperworkList;
    private User user;
    private DBManager dbManager;
    private String paperworkId;
    private List<PoVo> poVoList = new ArrayList<>();
    private boolean isAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        dbManager = DBManager.getInstance(this);
        user = getIntent().getParcelableExtra("User");
        if (user == null) {
            ToastUtils.showShortToast(mContext, "user == null");
            return;
        }
        tvStatue.setText(user.getUserName() + " 已登录");
        paperworkList = new ArrayList<>();
        paperworkAdapter = new PaperworkAdapter(this, paperworkList);

        rvPaperwork.setLayoutManager(new LinearLayoutManager(this));
        rvPaperwork.addItemDecoration(new SpaceItemTopDecoration(DensityUtil.dip2px(this, 10)));
        rvPaperwork.setAdapter(paperworkAdapter);
//        tvCabLocation.setText("交接柜：" + "1225" + "      柜门号：" + "1023");
        btnFinish.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.btn_back, R.id.btn_hand_do, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                if (isAction) {
                    ToastUtils.showShortToast(mContext, "请先点击完成，确认操作结束");
                } else {
                    finish();
                }
                break;
            case R.id.btn_hand_do:
                showInputPaperworkId("证件号：");
                break;
            case R.id.btn_finish:
                btnFinish.setVisibility(View.INVISIBLE);
                String certificateOpVoList = GsonHelper.objectToJSONString(poVoList);
                Call<ResponseInfo<String>> call = NetService.getApiService().tccInSubmit(certificateOpVoList, user.getUserId());
                call.enqueue(new Callback<ResponseInfo<String>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                        if ("200".equals(response.body().getCode())) {
                            isAction = false;
                            ToastUtils.showLongToast(mContext, "提交记录成功,完成本次发证");
                        } else if ("500".equals(response.body().getCode())) {
                            ToastUtils.showLongToast(mContext, "提交记录失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                        ToastUtils.showLongToast(mContext, "异常：" + t.getMessage());
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
                paperworkId = et_input.getText().toString().trim();
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
                                    CabinetInfo cabinetInfo = dbManager.queryEmptyCabinet();
                                    if (cabinetInfo != null) {//获取一个空柜子
                                        String number = cabinetInfo.getCabinetNumber();
                                        String[] strs = number.split(",");

                                        String row = null;
                                        if (strs[1].length() == 1) row = "0" + strs[1];
                                        else row = strs[1];
                                        String col = null;
                                        if (strs[2].length() == 1) col = "0" + strs[2];
                                        else col = strs[2];
                                        //打开柜门，放证件，关闭柜门，完成投件操作
                                        //、、、、、、、、、、、、、、、、、、、、、、、
                                        //、、、、、、、、、、、、、、、、、、、、、、、
                                        //、、、、、、、、、、、、、、、、、、、、、、、
                                        cabinetInfo.setDepartment(certificateVo.getOrgName());
                                        cabinetInfo.setUserIdCard(certificateVo.getIdCard());
                                        cabinetInfo.setPaperworkId(certificateVo.getNumber());
                                        cabinetInfo.setUsername(certificateVo.getUserName());
                                        dbManager.updateCabinetInfo(cabinetInfo);

                                        isAction = true;
                                        PoVo poVo = new PoVo();
                                        poVo.setLocationCode(strs[1] + strs[2]);
                                        poVo.setNumber(cabinetInfo.getPaperworkId());
                                        poVo.setCabinetSerialNo(strs[0]);
                                        poVoList.add(poVo);

                                        paperworkList.add(certificateVo);
                                        paperworkAdapter.notifyDataSetChanged();
                                        btnFinish.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    } else {
                                        ToastUtils.showShortToast(mContext, "当前没有空的柜子");
                                    }
                                } else {
                                    Logger.i("certificateVo == null");
                                }
                            } else if ("500".equals(responseInfo.getCode())) {
                                ToastUtils.showShortToast(mContext, "查询不到该证件号");
                                Logger.i("请求码：" + responseInfo.getCode() + responseInfo.getMsg());
                            }
                        } else {
                            Logger.i("response.body() == null");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<CertificateVo>> call, Throwable t) {
                        Logger.i("url:" + call.request().url() + "  error:" + t.getMessage());
                        ToastUtils.showShortToast(mContext, "error:" + t.getMessage());
                    }
                });
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
