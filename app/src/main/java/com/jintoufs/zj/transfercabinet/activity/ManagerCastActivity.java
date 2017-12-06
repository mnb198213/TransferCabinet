package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.dialog.WaitDialog;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.model.bean.PoVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.DensityUtil;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;
import com.jintoufs.zj.transfercabinet.widget.SpaceItemTopDecoration;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import honeywell.hedc_usb_com.HEDCUsbCom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 库管员存证
 * Created by zj on 2017/9/6.
 */

public class ManagerCastActivity extends AppCompatActivity implements HEDCUsbCom.OnConnectionStateListener,
        HEDCUsbCom.OnBarcodeListener, HEDCUsbCom.OnImageListener,
        HEDCUsbCom.OnDownloadListener {


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
    private CabinetInfo cabinetInfo;//当前操作的柜子
    private String IPAddress;//交接柜IP
    private String col, row;
    private ExecutorService threadPool;//线程池
    private Socket socket;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private WaitDialog waitDialog;

    private HEDCUsbCom m_engine;

    private List<CabinetInfo> emptyCabinetInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        dbManager = DBManager.getInstance(this);
        threadPool = Executors.newCachedThreadPool();
        m_engine = new HEDCUsbCom(this, this, this, this, this);

        waitDialog = new WaitDialog(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        IPAddress = (String) sharedPreferencesHelper.get("IpAddress", null);
        if (IPAddress == null) {
            ToastUtils.showShortToast(mContext, "交接柜信息异常！");
            return;
        }
        user = getIntent().getParcelableExtra("User");
        if (user == null) {
            ToastUtils.showShortToast(mContext, "user == null");
            return;
        }
        tvStatue.setText(user.getUserName() + " 已登录");
        paperworkList = new ArrayList<>();
        paperworkAdapter = new PaperworkAdapter(this, paperworkList);
        paperworkAdapter.setOpenCabinetClickListener(new PaperworkAdapter.OpenCabinetClickListener() {
            @Override
            public void openCabinet(int position) {
                if (cabinetInfo != null) {
                    cabinetInfo = null;
                }
                waitDialog.show(mContext, "柜子正在打开...");
                emptyCabinetInfoList = dbManager.queryAllEmptyCabinet();
                if (emptyCabinetInfoList != null && emptyCabinetInfoList.size() > 0) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            int count = emptyCabinetInfoList.size();
                            boolean isOpen = false;
                            for (int i = 0; i < count; i++) {
                                CabinetInfo tempCabinetInfo = emptyCabinetInfoList.get(i);
                                //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
                                String str = tempCabinetInfo.getCabinetNumber();
                                String strs[] = str.split(",");
                                if (strs.length != 3) return;
                                String row = strs[1];
                                String col = strs[2];
                                try {
                                    if (socket == null)
                                        socket = new Socket(IPAddress, AppConstant.PORT);
                                    int number = 0;
                                    while (!isOpen && number != 5) {
                                        CabinetModel.openDrawer(socket, row, col);
                                        Thread.sleep(500);
                                        isOpen = CabinetModel.isOpen(socket, row, col);
                                        number++;
                                    }
                                    //打开了一个柜子
                                    if (isOpen) {
                                        cabinetInfo = emptyCabinetInfoList.get(i);
                                        break;
                                    }
                                    Thread.sleep(1000);

                                } catch (Exception e) {
                                    Logger.i("开空柜子 " + row + "|" + col + " 异常：" + e.getMessage());
                                }
                            }
                            Message message = Message.obtain();
                            message.obj = isOpen;
                            message.what = 3;
                            mHandler.sendMessage(message);
                        }
                    });
                } else {
                    ToastUtils.showLongToast(mContext, "抱歉，当前没有可用的空柜！");
                }
            }
        });
        paperworkAdapter.setCloseCabinetClickListener(new PaperworkAdapter.CloseCabinetClickListener() {
            @Override
            public void closeCabinet(final int position) {
                //关闭已经打开的柜子
                String cabinetNumber = cabinetInfo.getCabinetNumber();
                String[] strs = cabinetNumber.split(",");
                if (strs.length != 3) {
                    return;
                }
                row = strs[1];
                col = strs[2];

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
                            message.arg1 = position;
                            mHandler.sendMessage(message);

                        } catch (Exception e) {
                            Logger.i("关柜异常：" + e.getMessage());
                        }
                    }
                });
            }
        });
        paperworkAdapter.setReEnterClickListener(new PaperworkAdapter.ReEnterClickListener() {
            @Override
            public void reEnter(int position) {
                cabinetInfo = null;
                paperworkList.remove(position);
                paperworkAdapter.notifyDataSetChanged();
            }
        });
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
                showInputTypeDialog();
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                boolean isOpen = (boolean) msg.obj;
                if (!isOpen) {//正常关闭

                    int position = msg.arg1;
                    CertificateVo certificateVo = paperworkList.get(position);
                    //更新本地柜子信息
                    cabinetInfo.setUserIdCard(certificateVo.getIdCard());
                    cabinetInfo.setPaperworkId(certificateVo.getNumber());
                    cabinetInfo.setDepartment(certificateVo.getOrgName());
                    cabinetInfo.setType(certificateVo.getType());
                    cabinetInfo.setUsername(certificateVo.getUserName());
                    cabinetInfo.setState("1");
                    dbManager.updateCabinetInfo(cabinetInfo);

                    String cabinetNumber = cabinetInfo.getCabinetNumber();
                    String[] strs = cabinetNumber.split(",");
                    //生成一条开柜子的记录
                    PoVo poVo = new PoVo();
                    poVo.setCabinetSerialNo(strs[0]);
                    if (row.length() == 1)
                        row = "0" + strs[1];
                    if (col.length() == 1)
                        col = "0" + strs[2];
                    poVo.setLocationCode(row + col);
                    poVo.setNumber(cabinetInfo.getPaperworkId());
                    poVoList.add(poVo);
                    //完成发证
                    paperworkAdapter.finishSave("已发证");
                    //标识有开箱记录，需要提交记录
                    isAction = true;
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.showLongToast(mContext, "柜子不能正常关闭，请换其他柜子存证");
                }
            } else if (msg.what == 1) {
                loadPaperworkInfo(paperworkId);
            } else if (msg.what == 3) {
                waitDialog.dismiss();
                boolean isOpen = (boolean) msg.obj;
                if (!isOpen) {
                    ToastUtils.showLongToast(mContext, "抱歉，当前没有可用的空柜！");
                    return;
                }
                paperworkAdapter.surePaperWorkToSave();
            } else if (msg.what == 4) {
                ToastUtils.showLongToast(mContext, "获取的证件号不正确，请重新扫描");
            }
        }
    };

    private void showInputTypeDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(mContext, R.layout.dialog_input_type_view, null);
        Button btn_sao = (Button) view.findViewById(R.id.btn_sao);
        Button btn_hand_do = (Button) view.findViewById(R.id.btn_hand_do);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_sao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isConnnection) {
                    m_engine.StartReadingSession();
                } else {
                    ToastUtils.showLongToast(mContext, "扫描仪连接异常");
                }
            }
        });
        btn_hand_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showInputPaperworkId("证件号：");
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        window.setContentView(view);
        dialog.show();
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
                dialog.dismiss();
                paperworkId = null;
                paperworkId = et_input.getText().toString().trim();
                if (TextUtils.isEmpty(paperworkId)) {
                    ToastUtils.showShortToast(mContext, "证件号不能为空");
                    return;
                }
                loadPaperworkInfo(paperworkId);
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    private void loadPaperworkInfo(String paperworkId) {
        waitDialog.show(mContext, "正在查询证件，请等待...");
        Call<ResponseInfo<CertificateVo>> call = NetService.getApiService().getCertificateByNumber(paperworkId);
        call.enqueue(new Callback<ResponseInfo<CertificateVo>>() {
            @Override
            public void onResponse(Call<ResponseInfo<CertificateVo>> call, Response<ResponseInfo<CertificateVo>> response) {
                waitDialog.dismiss();
                if (response.body() != null) {
                    ResponseInfo<CertificateVo> responseInfo = response.body();
                    if ("200".equals(responseInfo.getCode())) {
                        CertificateVo certificateVo = response.body().getData();
                        if (certificateVo == null) {
                            ToastUtils.showLongToast(mContext, "证件信息加载出错");
                            return;
                        }
                        paperworkList.add(certificateVo);
                        paperworkAdapter.notifyDataSetChanged();
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
                waitDialog.dismiss();
                Logger.i("url:" + call.request().url() + "  error:" + t.getMessage());
                ToastUtils.showShortToast(mContext, "error:" + t.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (threadPool != null)
            threadPool.shutdown();

        if (isConnnection) {
            m_engine.StopReadingSession();
        }
        super.onStop();
    }

    private String[] AsciiTab = {
            "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT", "LF", "VT",
            "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB",
            "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US", "SP", "DEL",
    };
    private boolean isConnnection = false;

    public String ConvertToString(byte[] data, int length) {
        String s = "";
        String s_final = "";
        for (int i = 0; i < length; i++) {
            if ((data[i] >= 0) && (data[i] < 0x20)) {
                s = String.format("<%s>", AsciiTab[data[i]]);
            } else if (data[i] >= 0x7F) {
                s = String.format("<0x%02X>", data[i]);
            } else {
                s = String.format("%c", data[i] & 0xFF);
            }
            s_final += s;
        }

        return s_final;
    }

    @Override
    public void OnBarcodeData(byte[] bytes, int i) {
        String result = ConvertToString(bytes, i);
        if (result != null && result.length() > 9) {
            paperworkId = null;
            paperworkId = result.substring(0, 9);
            mHandler.sendEmptyMessage(1);
        } else {
            mHandler.sendEmptyMessage(4);
        }
    }

    @Override
    public void OnConnectionStateEvent(HEDCUsbCom.ConnectionState connectionState) {
        if (connectionState == HEDCUsbCom.ConnectionState.Connected) {
            isConnnection = true;
        } else {
            isConnnection = false;
        }
    }

    @Override
    public void OnDownloadData(int i, short i1) {

    }

    @Override
    public void OnImageData(byte[] bytes, int i) {

    }
}
