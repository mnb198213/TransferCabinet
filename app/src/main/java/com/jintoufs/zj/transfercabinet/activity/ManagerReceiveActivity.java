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
import com.jintoufs.zj.transfercabinet.dialog.WaitDialog;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.model.bean.Cabinet;
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

import java.io.IOException;
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
    private List<Cabinet> paperworkInfoList;
    private DBManager dbManager;
    private User user;
    private List<PoVo> poVoList;
    private boolean isAction = false;//判断是否有取证操作
    private ExecutorService threadPool;
    private Socket socket;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String IPAddress, serialNo;
    private WaitDialog waitDialog;
    private Cabinet cabinet;//当前操作的柜子
    private String row, col;//当前操作柜子的行列号
    private boolean isAllOpen = true;//是否全开


    @Override
    public void initData() {
        super.initData();
        mContext = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        serialNo = (String) sharedPreferencesHelper.get("SerialNo", null);
        IPAddress = (String) sharedPreferencesHelper.get("IpAddress", null);
        titleAdapter = new TitleAdapter(mContext, new String[]{"证件类型", "人员", "机构/部门",
                "身份证号", "交接柜", "柜门号", "操作"});
        user = getIntent().getParcelableExtra("User");
        dbManager = DBManager.getInstance(this);
        paperworkInfoList = new ArrayList<>();
        List<CabinetInfo> cabinetInfoList = dbManager.queryReceiveCabinetList();

        int size = cabinetInfoList.size();
        for (int i = 0; i < size; i++) {
            Cabinet cabinet = new Cabinet();
            cabinet.setOpen(false);
            cabinet.setCabinetInfo(cabinetInfoList.get(i));
            paperworkInfoList.add(cabinet);
        }
        cabinetInfoAdapter = new CabinetInfoAdapter(mContext, paperworkInfoList);
        poVoList = new ArrayList<>();
        threadPool = Executors.newCachedThreadPool();
        waitDialog = new WaitDialog(this);
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
                cabinet = paperworkInfoList.get(position);
                if (cabinet.isOpen()) {
                    closeSingleCabinet();
                } else {
                    openSingleCabinet();
                }
            }
        });
    }

    private void openSingleCabinet() {
        //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
        String cabinetNumber = cabinet.getCabinetInfo().getCabinetNumber();
        String[] strs = cabinetNumber.split(",");
        if (strs.length != 3) {
            ToastUtils.showLongToast(mContext, "柜子数据不合理，无法读取");
            return;
        }
        waitDialog.show(mContext, "柜子正在打开,请稍等...");
        row = strs[1];
        col = strs[2];
        Logger.i("行：" + row + "列：" + col);
        //打开单个柜门
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null) socket = new Socket(IPAddress, AppConstant.PORT);
                    int count = 0;
                    boolean isOpen = false;
                    while (!isOpen && count != 5) {
                        CabinetModel.openDrawer(socket, row, col);
                        Thread.sleep(500);
                        isOpen = CabinetModel.isOpen(socket, row, col);
                        count++;
                    }
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = isOpen;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    ToastUtils.showLongToast(mContext, "开柜异常：" + e.getMessage());
                    return;
                }
            }
        });
    }

    private void closeSingleCabinet() {
        //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
        String cabinetNumber = cabinet.getCabinetInfo().getCabinetNumber();
        String[] strs = cabinetNumber.split(",");
        if (strs.length != 3) {
            ToastUtils.showLongToast(mContext, "柜子数据不合理，无法读取");
            return;
        }
        waitDialog.show(mContext, "柜子正在关闭,请稍等...");
        row = strs[1];
        col = strs[2];
        //打开单个柜门
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null) socket = new Socket(IPAddress, AppConstant.PORT);
                    int count = 0;
                    boolean isOpen = true;
                    while (isOpen && count != 5) {
                        CabinetModel.closeDrawer(socket, row, col);
                        Thread.sleep(500);
                        isOpen = CabinetModel.isOpen(socket, row, col);
                        count++;
                    }
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = isOpen;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    ToastUtils.showLongToast(mContext, "开柜异常：" + e.getMessage());
                }
            }
        });
    }

    private boolean isAll = false;//记录是否全部打开
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://开柜
                    boolean isOpen1 = (boolean) msg.obj;
                    if (isOpen1) {
                        isAction = true;
                        waitDialog.show(mContext, "柜子打开成功，请取出证件！");
                        mHandler.sendEmptyMessageDelayed(3, 1500);
                        cabinet.setOpen(true);
                        cabinetInfoAdapter.notifyDataSetChanged();
                    } else {
                        waitDialog.show(mContext, "柜子打开失败，请重试或联系管理员！");
                        mHandler.sendEmptyMessageDelayed(3, 1500);
                    }
                    break;
                case 2://关柜
                    boolean isOpen2 = (boolean) msg.obj;
                    if (isOpen2) {
                        waitDialog.show(mContext, "柜子关闭失败，请重试或联系管理员！");
                        mHandler.sendEmptyMessageDelayed(3, 1500);
                    } else {
                        waitDialog.show(mContext, "柜子已关闭！");
                        mHandler.sendEmptyMessageDelayed(3, 1500);
                        if (row.length() == 1) {
                            row = "0" + row;
                        }
                        if (col.length() == 1) {
                            col = "0" + col;
                        }

                        PoVo poVo = new PoVo();
                        poVo.setLocationCode(row + col);
                        poVo.setNumber(cabinet.getCabinetInfo().getPaperworkId());
                        poVo.setCabinetSerialNo(serialNo);
                        poVoList.add(poVo);
                        String cabinetNumber = cabinet.getCabinetInfo().getCabinetNumber();
                        CabinetInfo cabinetInfo1 = dbManager.querySingleCabinet(cabinetNumber);
                        cabinetInfo1.setPaperworkId("0");
                        cabinetInfo1.setUserIdCard("0");
                        cabinetInfo1.setState("0");
                        dbManager.updateCabinetInfo(cabinetInfo1);

                        paperworkInfoList.remove(cabinet);
                        cabinetInfoAdapter.notifyDataSetChanged();
                    }
                    break;
                case 3://延迟对话框消失
                    waitDialog.dismiss();
                    break;
                case 4://全部开柜过程中...
                    boolean isOpen3 = (boolean) msg.obj;
                    if (!isOpen3) {
                        isAll = false;
                    }
                    cabinet.setOpen(isOpen3);
                    cabinetInfoAdapter.notifyDataSetChanged();
                    break;
                case 5://全部开柜结束
                    isAction = true;
                    waitDialog.dismiss();
                    if (!isAll) {
                        ToastUtils.showLongToast(mContext, "提示：有未正常打开的柜子，请检查故障！");
                    } else
                        ToastUtils.showLongToast(mContext, "柜子已全部打开！");
                    btnOpenAllCabinet.setText("关闭所有已开柜门");
                    isAllOpen = false;
                    break;
                case 6://全部关柜过程中...
                    boolean isOpen4 = (boolean) msg.obj;
                    if (!isOpen4) {
                        isAll = false;
                    }
                    if (row.length() == 1) {
                        row = "0" + row;
                    }
                    if (col.length() == 1) {
                        col = "0" + col;
                    }

                    PoVo poVo = new PoVo();
                    poVo.setLocationCode(row + col);
                    poVo.setNumber(cabinet.getCabinetInfo().getPaperworkId());
                    poVo.setCabinetSerialNo(serialNo);
                    poVoList.add(poVo);
                    String cabinetNumber = cabinet.getCabinetInfo().getCabinetNumber();
                    CabinetInfo cabinetInfo1 = dbManager.querySingleCabinet(cabinetNumber);
                    cabinetInfo1.setPaperworkId("0");
                    cabinetInfo1.setUserIdCard("0");
                    dbManager.updateCabinetInfo(cabinetInfo1);

                    paperworkInfoList.remove(cabinet);
                    cabinetInfoAdapter.notifyDataSetChanged();
                    break;
                case 7://全部关柜结束
                    waitDialog.dismiss();
                    if (!isAll) {
                        ToastUtils.showLongToast(mContext, "提示：有未正常关闭的柜子，请检查故障！");
                    } else
                        ToastUtils.showLongToast(mContext, "柜子已全部关闭！");
                    btnOpenAllCabinet.setText("打开所有已开柜门");
                    isAllOpen = true;
                    break;
                default:
                    super.handleMessage(msg);

            }
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
                isAll = true;
                if (isAllOpen) {
                    waitDialog.show(mContext, "柜子正在依次打开...");
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            int count = paperworkInfoList.size();
                            for (int i = 0; i < count; i++) {
                                CabinetInfo cabinetInfo = paperworkInfoList.get(i).getCabinetInfo();
                                //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
                                String cabinetNumber = cabinetInfo.getCabinetNumber();
                                String[] strs = cabinetNumber.split(",");
                                if (strs.length != 3) {
                                    ToastUtils.showLongToast(mContext, "柜子数据不合理，无法读取");
                                    return;
                                }
                                row = strs[1];
                                col = strs[2];
                                if (row.length() == 1) {
                                    row = "0" + row;
                                }
                                if (col.length() == 1) {
                                    col = "0" + col;
                                }
                                try {
                                    if (socket == null)
                                        socket = new Socket(IPAddress, AppConstant.PORT);

                                    int tryCount = 0;
                                    boolean isOpen = false;
                                    while (!isOpen && tryCount != 5) {
                                        CabinetModel.openDrawer(socket, row, col);
                                        Thread.sleep(1000);
                                        isOpen = CabinetModel.isOpen(socket, row, col);
                                        tryCount++;
                                    }
                                    Message message = Message.obtain();
                                    message.obj = isOpen;
                                    message.what = 4;
                                    mHandler.sendMessage(message);

                                    Thread.sleep(1000);//休眠1秒，让线程有足够时间处理逻辑
                                } catch (Exception e) {
                                    Logger.i("异常：" + e.getClass().getName());
                                    e.printStackTrace();
                                }
                            }
                            //完成
                            Message message = Message.obtain();
                            message.what = 5;
                            mHandler.sendMessage(message);
                        }
                    });
                } else {
                    waitDialog.show(mContext, "柜子正在依次关闭...");
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            int count = paperworkInfoList.size();
                            for (int i = 0; i < count; i++) {
                                CabinetInfo cabinetInfo = paperworkInfoList.get(i).getCabinetInfo();
                                //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
                                String cabinetNumber = cabinetInfo.getCabinetNumber();
                                String[] strs = cabinetNumber.split(",");
                                if (strs.length != 3) {
                                    ToastUtils.showLongToast(mContext, "柜子数据不合理，无法读取");
                                    return;
                                }
                                row = strs[1];
                                col = strs[2];
                                if (row.length() == 1) {
                                    row = "0" + row;
                                }
                                if (col.length() == 1) {
                                    col = "0" + col;
                                }
                                try {
                                    if (socket == null)
                                        socket = new Socket(IPAddress, AppConstant.PORT);

                                    int tryCount = 0;
                                    boolean isOpen = true;
                                    while (isOpen && tryCount != 5) {
                                        CabinetModel.closeDrawer(socket, row, col);
                                        Thread.sleep(1000);
                                        isOpen = CabinetModel.isOpen(socket, row, col);
                                        tryCount++;
                                    }
                                    Message message = Message.obtain();
                                    message.obj = isOpen;
                                    message.what = 6;
                                    mHandler.sendMessage(message);

                                    Thread.sleep(1000);//休眠1秒，让线程有足够时间处理逻辑
                                } catch (Exception e) {
                                    Logger.i("异常：" + e.getClass().getName());
                                    e.printStackTrace();
                                }
                            }
                            //完成
                            Message message = Message.obtain();
                            message.what = 7;
                            mHandler.sendMessage(message);
                        }
                    });
                }
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
                if (isAllOpen) {
                    showNoticeDialog("当前操作为：打开所有装待回收证件的柜门");
                } else {
                    showNoticeDialog("当前操作为：关闭所有已打开的柜门");
                }
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
    protected void onStop() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (threadPool != null)
            threadPool.shutdown();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }
}
