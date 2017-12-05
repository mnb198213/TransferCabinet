package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.dialog.WaitDialog;
import com.jintoufs.zj.transfercabinet.model.CabinetInfoBeanModel;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.model.bean.Drawer;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.widget.SpaceDrawerItemDecoration;
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
    @BindView(R.id.btn_set)
    Button btnSet;

    private Unbinder unbinder;
    private DrawerAdapter drawerAdapter;
    private int raw;
    private int column;
    private Context mContext;
    private List<Drawer> drawerList;
    private Drawer drawer;//当前操作的柜子

    private boolean isAllOpen = false;//判断是否全部打开
    private DBManager dbManager;
    private CabinetInfoBeanModel cabinetInfoBeanModel;
    private User user;//库管员
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String serialNo;

    private String IPAddress;//交接柜IP
    private CabinetModel cabinetModel;
    private ExecutorService threadPool;//线程池
    private Socket socket;
    private String row;//柜子的行
    private String col;//柜子的列
    private boolean isSingleOpen = false;//单个柜子是否打开
    private WaitDialog waitDialog;
    private int tryCount;//尝试发送指令次数

    @Override
    public void initData() {
        mContext = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        IPAddress = (String) sharedPreferencesHelper.get("IpAddress", null);
        user = getIntent().getParcelableExtra("User");
        dbManager = DBManager.getInstance(mContext);
        cabinetInfoBeanModel = new CabinetInfoBeanModel(mContext);
        List<CabinetInfo> cabinetInfoList = dbManager.queryAllCabinetInfos();
        CabinetInfoBean cabinetInfoBean = cabinetInfoBeanModel.getCabinetInfoBean();
        raw = Integer.valueOf(cabinetInfoBean.getRow());
        column = Integer.valueOf(cabinetInfoBean.getCol());
        waitDialog = new WaitDialog(this);

        drawerList = new ArrayList<>();
        for (int i = 0; i < cabinetInfoList.size(); i++) {
            Drawer drawer = new Drawer();
            drawer.setRaw(i / column + 1);
            drawer.setColumn(i % column + 1);
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
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_cabinet_monitor);
        unbinder = ButterKnife.bind(this);
        tvPaperworkUser.setText("姓名：" + "无" +
                "\n\n所属机构：" + "无" +
                "\n\n" + "身份证号：" + "无");
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
                String name;
                if ("0".equals(drawer.getName())) {
                    name = "无";
                } else {
                    name = drawer.getName();
                }

                String department;
                if ("0".equals(drawer.getDepartment())) {
                    department = "无";
                } else {
                    department = drawer.getName();
                }

                String userId;
                if ("0".equals(drawer.getUserId())) {
                    userId = "无";
                } else {
                    userId = drawer.getUserId();
                }

                tvPaperworkUser.setText("姓名：" + name +
                        "\n\n所属机构：" + department +
                        "\n\n" + "身份证号：" + userId);

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
        btnOpenAll.setText("打开所有柜子");
        btnOpenSingle.setText("打开单个柜子");
    }

    @OnClick({R.id.tv_statue, R.id.btn_back, R.id.btn_open_single, R.id.btn_open_all, R.id.btn_set})
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
                if (serialNo == null) {
                    ToastUtils.showShortToast(mContext, "柜子编号获取失败！");
                    return;
                }

                isSingleOpen = drawer.isOpen();

                row = String.valueOf(drawer.getRaw());
                if (row.length() == 1) {
                    row = "0" + row;
                }
                col = String.valueOf(drawer.getColumn());
                if (col.length() == 1) {
                    col = "0" + col;
                }
                Logger.i("柜子行列数：" + row + "  " + col);
                if (drawer.isOpen()) {//已打开
                    Logger.i("柜子关闭操作");
                    waitDialog.show(mContext, "柜子正在关闭...");
                    //关闭柜子操作
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (socket == null)
                                    socket = new Socket(IPAddress, AppConstant.PORT);
                                Logger.i("发送关柜子指令");

                                tryCount = 0;
                                while (isSingleOpen && tryCount != 5) {
                                    CabinetModel.closeDrawer(socket, row, col);
                                    Thread.sleep(1000);
                                    isSingleOpen = CabinetModel.isOpen(socket, row, col);
                                    Logger.i("关柜子反馈：" + isSingleOpen);
                                    tryCount++;
                                }
                                sendMessageToHandler(mHandler, isSingleOpen, 1);
                            } catch (Exception e) {
                                Logger.i("异常：" + e.getClass().getName());
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Logger.i("柜子打开操作");
                    waitDialog.show(mContext, "柜子正在打开...");
                    //打开柜子操作
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (socket == null)
                                    socket = new Socket(IPAddress, AppConstant.PORT);
                                Logger.i("发送开柜子指令");

                                tryCount = 0;
                                while (!isSingleOpen && tryCount != 5) {
                                    CabinetModel.openDrawer(socket, row, col);
                                    Thread.sleep(1000);
                                    isSingleOpen = CabinetModel.isOpen(socket, row, col);
                                    tryCount++;
                                }
                                sendMessageToHandler(mHandler, isSingleOpen, 1);
                            } catch (Exception e) {
                                Logger.i("异常：" + e.getClass().getName());
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;
            case R.id.btn_open_all:
                if (serialNo == null) {
                    ToastUtils.showShortToast(mContext, "柜子编号获取失败！");
                    return;
                }
                drawerAdapter.clearSelected();//清除柜子的选中状态

                if (!isAllOpen) {
                    showNoticeDialog("确定打开所有柜子");
                } else {
                    showNoticeDialog("确定关闭所有柜子");
                }
                break;
            case R.id.btn_set://设置交接柜
                Intent intent = new Intent(CabinetMonitorActivity.this, CabinetSetActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean isAll = true;
    private boolean isFinish = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://单个柜子操作
                    Bundle bundle = msg.getData();
                    boolean isOpen = bundle.getBoolean("IsOpen");
                    waitDialog.dismiss();
                    if (isOpen) {
                        Logger.i("打开柜子成功");
                        btnOpenSingle.setText("关闭当前柜子");
                        //柜子打开上传操作记录
                        Call<ResponseInfo<String>> call = NetService.getApiService().tccMaintainSubmit(user.getUserId(),
                                "1", serialNo, row + col);
                        call.enqueue(new retrofit2.Callback<ResponseInfo<String>>() {
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
                                Logger.i("请求失败的异常：" + t.getMessage());
                            }
                        });

                    } else {
                        Logger.i("关闭柜子成功");
                        btnOpenSingle.setText("打开当前柜子");
                    }
                    drawer.setOpen(isOpen);
                    drawerAdapter.clearSelected();
                    drawerAdapter.notifyDataSetChanged();
                    break;
                case 2://对全部柜子开操作过程中...
                    boolean isOpen2 = (boolean) msg.obj;
                    Logger.i("handler： 行：" + drawer.getRaw() + "  isOpen:" + isOpen2);
                    if (!isOpen2) {
                        isAll = false;
                    }
                    drawer.setOpen(isOpen2);
                    drawerAdapter.notifyDataSetChanged();
                    break;
                case 3://对全部柜子开操作结束
                    waitDialog.dismiss();
                    isAllOpen = true;
                    btnOpenAll.setText("关闭所有柜子");
                    if (!isAll) {
                        ToastUtils.showLongToast(mContext, "提示：有未正常打开的柜子，请检查故障！");
                    } else {
                        ToastUtils.showLongToast(mContext, "全部柜子已打开！");
                    }
                    break;
                case 4://对全部柜子关操作过程中...
                    boolean isOpen4 = (boolean) msg.obj;
                    if (!isOpen4) {
                        isFinish = false;
                    }
                    drawer.setOpen(isOpen4);
                    drawerAdapter.notifyDataSetChanged();
                    break;
                case 5://对全部柜子关操作结束

                    waitDialog.dismiss();

                    isAllOpen = false;
                    btnOpenAll.setText("打开所有柜子");
                    if (isFinish) {
                        ToastUtils.showLongToast(mContext, "全部柜子已关闭！");
                    } else {
                        ToastUtils.showLongToast(mContext, "提示：有未正常关闭的柜子，请检查故障！");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    //单开发消息
    private void sendMessageToHandler(Handler handler, boolean isOpen, int what) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsOpen", isOpen);
        message.setData(bundle);
        message.what = what;
        handler.sendMessage(message);
    }

    private Dialog getWaitDialog(Context context, String info) {
        Dialog dialog = new Dialog(context, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_search_view, null);
        TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
        tv_info.setText(info);
        window.setContentView(view);
        return dialog;
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
                dialog.dismiss();
                if (!isAllOpen) {//执行全开操作
                    waitDialog.show(mContext, "柜子正在依次打开...");

                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            int count = drawerList.size();
                            for (int i = 0; i < count; i++) {
                                drawer = drawerList.get(i);
                                isSingleOpen = drawer.isOpen();

                                row = String.valueOf(drawer.getRaw());
                                if (row.length() == 1) {
                                    row = "0" + row;
                                }
                                col = String.valueOf(drawer.getColumn());
                                if (col.length() == 1) {
                                    col = "0" + col;
                                }
                                try {
                                    if (socket == null)
                                        socket = new Socket(IPAddress, AppConstant.PORT);

                                    tryCount = 0;
                                    while (!isSingleOpen && tryCount != 5) {
                                        CabinetModel.openDrawer(socket, row, col);
                                        Thread.sleep(1000);
                                        isSingleOpen = CabinetModel.isOpen(socket, row, col);
                                        tryCount++;
                                    }
                                    Logger.i("行：" + drawer.getRaw() + "  isOpen:" + isSingleOpen);
                                    Message message = Message.obtain();
                                    message.obj = isSingleOpen;
                                    message.what = 2;
                                    mHandler.sendMessage(message);

                                    Thread.sleep(1000);//休眠1秒，让线程有足够时间处理逻辑
                                } catch (Exception e) {
                                    Logger.i("异常：" + e.getClass().getName());
                                    e.printStackTrace();
                                }
                            }
                            //完成
                            Message message = Message.obtain();
                            message.what = 3;
                            mHandler.sendMessage(message);
                        }
                    });
                } else {//执行全关操作
                    waitDialog.show(mContext, "柜子正在依次关闭...");
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            int count = drawerList.size();
                            for (int i = 0; i < count; i++) {
                                drawer = drawerList.get(i);
                                isSingleOpen = drawer.isOpen();

                                row = String.valueOf(drawer.getRaw());
                                if (row.length() == 1) {
                                    row = "0" + row;
                                }
                                col = String.valueOf(drawer.getColumn());
                                if (col.length() == 1) {
                                    col = "0" + col;
                                }

                                if (isSingleOpen) {//当为打开的时候才调用关闭柜子命令
                                    try {
                                        if (socket == null)
                                            socket = new Socket(IPAddress, AppConstant.PORT);
                                        Logger.i("发送关柜子指令");

                                        tryCount = 0;
                                        while (isSingleOpen && tryCount != 5) {
                                            CabinetModel.closeDrawer(socket, row, col);
                                            Thread.sleep(1000);
                                            isSingleOpen = CabinetModel.isOpen(socket, row, col);
                                            tryCount++;
                                        }
                                        //关柜子过程中
                                        Message message = Message.obtain();
                                        message.obj = isSingleOpen;
                                        message.what = 4;
                                        mHandler.sendMessage(message);

                                        Thread.sleep(1000);//休眠1秒，让线程有足够时间处理逻辑
                                    } catch (Exception e) {
                                        Logger.i("异常：" + e.getClass().getName());
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //关柜子结束
                            Message message = Message.obtain();
                            message.what = 5;
                            mHandler.sendMessage(message);
                        }
                    });
                }
//                     //上传操作所有柜子的记录
                    Call<ResponseInfo<String>> call = NetService.getApiService().tccMaintainSubmit(user.getUserId(), "2", serialNo, null);
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
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    @Override
    protected void onStop() {
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

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
