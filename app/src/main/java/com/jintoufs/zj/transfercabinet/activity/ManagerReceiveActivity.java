package com.jintoufs.zj.transfercabinet.activity;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.ToastUtils;
import com.baselib.http.util.GsonHelper;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.adapter.CabinetInfoAdapter;
import com.jintoufs.zj.transfercabinet.adapter.TitleAdapter;
import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;
import com.jintoufs.zj.transfercabinet.model.bean.PoVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.widget.SpacePwItemDecoration;
import com.jintoufs.zj.transfercabinet.widget.SpaceTitleItemDecoration;
import com.orhanobut.logger.Logger;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 库管员回收
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
    @BindView(R.id.tv_no_info)
    TextView tv_no_info;
    @BindView(R.id.rl_info)
    RelativeLayout rl_info;

    private Unbinder unbinder;
    private Context mContext;

    private TitleAdapter titleAdapter;
    private CabinetInfoAdapter cabinetInfoAdapter;
    private List<CabinetInfo> paperworkInfoList;
    private DBManager dbManager;
    private User user;
    private List<PoVo> poVoList;
    private boolean isAction = false;//判断是否有取证操作
    private ExecutorService threadPool;
    private Socket socket;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String IPAddress;


    @Override
    public void initData() {
        super.initData();
        mContext = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        IPAddress = (String) sharedPreferencesHelper.get("IpAddress", null);
        titleAdapter = new TitleAdapter(mContext, new String[]{"证件类型", "人员", "机构/部门",
                "身份证号", "交接柜", "柜门号", "操作"});
        user = getIntent().getParcelableExtra("User");
        dbManager = DBManager.getInstance(this);
        paperworkInfoList = new ArrayList<>();
        paperworkInfoList.addAll(dbManager.queryUseredCabinetList());
        cabinetInfoAdapter = new CabinetInfoAdapter(mContext, paperworkInfoList);
        poVoList = new ArrayList<>();
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_riceive);
        unbinder = ButterKnife.bind(this);
        tvStatue.setText(user.getUserName() + " 已登录");
        if (paperworkInfoList.size() == 0) {
            tv_no_info.setVisibility(View.VISIBLE);
            rl_info.setVisibility(View.GONE);
        } else {
            tv_no_info.setVisibility(View.GONE);
            rl_info.setVisibility(View.VISIBLE);
        }
        rvTitleList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvTitleList.addItemDecoration(new SpaceTitleItemDecoration(mContext, DensityUtil.dip2px(mContext, 1)));
        rvTitleList.setAdapter(titleAdapter);

        rvPaperworkList.setLayoutManager(new LinearLayoutManager(mContext));
        rvPaperworkList.addItemDecoration(new SpacePwItemDecoration(mContext, DensityUtil.dip2px(mContext, 1)));
        rvPaperworkList.setAdapter(cabinetInfoAdapter);
        cabinetInfoAdapter.setOnOpenDrawerClickListener(new CabinetInfoAdapter.OnOpenDrawerClickListener() {
            @Override
            public void openDraw(int position) {
                CabinetInfo cabinetInfo = paperworkInfoList.get(position);
                isAction = true;
                openSingleCabinet(cabinetInfo);
            }
        });
    }

    private void openSingleCabinet(CabinetInfo cabinetInfo) {
        //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
        String cabinetNumber = cabinetInfo.getCabinetNumber();
        String[] strs = cabinetNumber.split(",");
        if (strs.length != 3) {
            ToastUtils.showLongToast(mContext, "柜子数据不合理，无法读取");
            return;
        }
        final String row = strs[1];
        final String col = strs[2];
        //showNoticeDialog("柜门已打开，请取走证件");
        //打开单个柜门，取证，关闭柜门，完成取证操作
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null) socket = new Socket(IPAddress, AppConstant.PORT);
                    int count = 0;
                    boolean isOpen = false;
                    while (isOpen && count != 5) {
                        CabinetModel.openDrawer(socket,row,col);
                        Thread.sleep(500);
                        isOpen = CabinetModel.isOpen(socket,row,col);
                    }
                    Message message = Message.obtain();
                    message.what=1;
                    message.obj = isOpen;
                    mHandler.sendMessage(message);
                    ////////////////////////////////////////////////////////////////待续...
                } catch (Exception e) {
                    ToastUtils.showLongToast(mContext, "开柜异常：" + e.getMessage());
                }

            }
        });

        if (strs[1].length() == 1) {
            strs[1] = "0" + strs[1];
        }
        if (strs[2].length() == 1) {
            strs[2] = "0" + strs[2];
        }

        PoVo poVo = new PoVo();
        poVo.setLocationCode(strs[1] + strs[2]);
        poVo.setNumber(cabinetInfo.getPaperworkId());
        poVo.setCabinetSerialNo(strs[0]);
        poVoList.add(poVo);

        CabinetInfo cabinetInfo1 = dbManager.querySingleCabinet(cabinetNumber);
        cabinetInfo1.setPaperworkId("0");
        cabinetInfo1.setUserIdCard("0");
        dbManager.updateCabinetInfo(cabinetInfo1);

        paperworkInfoList.remove(cabinetInfo);
        cabinetInfoAdapter.notifyDataSetChanged();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
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
                int count = paperworkInfoList.size();
                for (int i = 0; i < count; i++) {
                    CabinetInfo cabinetInfo = paperworkInfoList.get(i);

                    //依次打开柜子，拿出证件，关闭柜门，完成取证操作
                    //、、、、、、、、、、、、、、
                    openSingleCabinet(cabinetInfo);
                }
                isAction = true;
                dialog.dismiss();
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    @OnClick({R.id.btn_back, R.id.btn_open_all_cabinet, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                if (isAction) {
                    ToastUtils.showLongToast(mContext, "请点击完成，确定取证结束并提交数据完成！");
                } else
                    finish();
                break;
            case R.id.btn_open_all_cabinet:
                int count = paperworkInfoList.size();
                if (count == 0) {
                    ToastUtils.showShortToast(mContext, "当前没有证件需要");
                    return;
                }
                showNoticeDialog("当前操作为：打开所有柜门");
                break;
            case R.id.btn_finish:
                String strOpvoList = GsonHelper.objectToJSONString(poVoList);
                Call<ResponseInfo<String>> call = NetService.getApiService().tccOutSubmit(strOpvoList, user.getUserId());
                call.enqueue(new Callback<ResponseInfo<String>>() {
                    @Override
                    public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                        Logger.i("正常回调");
                        ResponseInfo<String> responseInfo = response.body();
                        isAction = false;
//                        if ("200".equals(responseInfo.getCode())) {    }
                        ToastUtils.showLongToast(mContext, responseInfo.getMsg());
                    }

                    @Override
                    public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                        Logger.i("失败回调：" + t.getMessage());
                    }
                });
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
