package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basekit.base.BaseActivity;
import com.basekit.util.StringUtils;
import com.basekit.util.ToastUtils;
import com.baselib.http.util.GsonHelper;
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.db.CabinetInfo;
import com.jintoufs.zj.transfercabinet.db.DBManager;
import com.jintoufs.zj.transfercabinet.dialog.WaitDialog;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.model.bean.PoVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;
import com.orhanobut.logger.Logger;

import java.net.Socket;
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
 * 申领人取件
 * Created by zj on 2017/9/6.
 */

public class UserPickupActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_number01)
    EditText etNumber01;
    @BindView(R.id.et_number02)
    EditText etNumber02;
    @BindView(R.id.et_number03)
    EditText etNumber03;
    @BindView(R.id.et_number04)
    EditText etNumber04;
    @BindView(R.id.et_number05)
    EditText etNumber05;
    @BindView(R.id.et_number06)
    EditText etNumber06;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_all)
    RelativeLayout rlAll;

    private Unbinder unbinder;
    private Context mContext;
    private WaitDialog waitDialog;
    private DBManager dbManager;

    private CabinetInfo cabinetInfo;//当前操作的柜子
    private String IPAddress;//交接柜IP
    private String col, row;
    private Socket socket;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_pickup);
        unbinder = ButterKnife.bind(this);
        mContext = this;
        dbManager = DBManager.getInstance(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        IPAddress = (String) sharedPreferencesHelper.get("IpAddress", null);

        etNumber01.requestFocus();
        etNumber01.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber02.requestFocus();
                }
            }
        });
        etNumber02.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber03.requestFocus();
                }
            }
        });
        etNumber03.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber04.requestFocus();
                }
            }
        });
        etNumber04.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber05.requestFocus();
                }
            }
        });
        etNumber05.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber06.requestFocus();
                }
            }
        });
        etNumber06.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etNumber06.setCursorVisible(true);//显示光标
                return false;
            }
        });
        etNumber06.addTextChangedListener(new MyTextWatcher() {
            @Override
            void newTextChanged(CharSequence charSequence) {
                if (charSequence.toString().length() == 1) {
                    etNumber06.setCursorVisible(false);//隐藏光标
                    //软键盘消失
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        tvTime.setText("当前时间：" + TimeUtil.DateToString(new Date()));
        waitDialog = new WaitDialog(mContext);
        if (IPAddress == null) {
            btnSure.setEnabled(false);
            ToastUtils.showLongToast(mContext, "交接柜数据加载出错！");
            return;
        }
    }

    @OnClick({R.id.btn_sure, R.id.btn_back, R.id.rl_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                waitDialog.show(mContext, "验证中，请等待...");
                String code01 = etNumber01.getText().toString().trim();
                String code02 = etNumber02.getText().toString().trim();
                String code03 = etNumber03.getText().toString().trim();
                String code04 = etNumber04.getText().toString().trim();
                String code05 = etNumber05.getText().toString().trim();
                String code06 = etNumber06.getText().toString().trim();
                if (TextUtils.isEmpty(code01) || TextUtils.isEmpty(code02) || TextUtils.isEmpty(code03)
                        || TextUtils.isEmpty(code04) || TextUtils.isEmpty(code05) || TextUtils.isEmpty(code06)) {
                    waitDialog.dismiss();
                    ToastUtils.showShortToast(mContext, "验证码不全，请更正");
                    return;
                }
                String code = (code01 + code02 + code03 + code04 + code05 + code06).trim();
//                Call<ResponseInfo<String>> call = NetService.getApiService().validateMessageCode(code);
//                call.enqueue(new Callback<ResponseInfo<String>>() {
//                    @Override
//                    public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
//                        ResponseInfo<String> responseInfo = response.body();
//                        if (responseInfo != null) {
//                            if ("200".equals(responseInfo.getCode())) {
//                                String paperworkId = responseInfo.getData();
//                                //////////////
//                            } else {
//                                ToastUtils.showLongToast(mContext, "验证码出错，请检查！");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
//                        ToastUtils.showShortToast(mContext, t.getMessage());
//                    }
//                });
                String paperworkId = "E23638968";
                if (paperworkId == null) {
                    waitDialog.dismiss();
                    ToastUtils.showLongToast(mContext, "数据返回错误，请检查服务器！");
                    return;
                }
                cabinetInfo = dbManager.querySingleCabinetByPaperworkId(paperworkId);
                if (cabinetInfo == null) {
                    waitDialog.dismiss();
                    ToastUtils.showLongToast(mContext, "本地没有该证件的存储记录！");
                    return;
                }

                String cabinetNumber = cabinetInfo.getCabinetNumber();
                String[] strs = cabinetNumber.split(",");
                if (strs.length != 3) {
                    ToastUtils.showLongToast(mContext, "本地数据格式出错！");
                    return;
                }
                row = strs[1];
                col = strs[2];
                waitDialog.show(mContext, "验证成功，正在打开柜子...");
                openCabinet();//打开柜子
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_all://软键盘消失
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    private void openCabinet() {
        //开柜子操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null)
                        socket = new Socket(IPAddress, AppConstant.PORT);
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
                    Logger.i(e.getMessage());
                }


            }
        }).start();
    }

    private void closeCabinet() {
        //关柜子操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null)
                        socket = new Socket(IPAddress, AppConstant.PORT);
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
                    Logger.i(e.getMessage());
                }


            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                waitDialog.dismiss();
                boolean isOpen = (boolean) msg.obj;
                if (isOpen) {
                    showNoticeDialog("柜子已打开，请取出证件！", true);
                } else {
                    showNoticeDialog("柜子打开失败，请联系管理员！", false);
                }
            } else if (msg.what == 2) {
                waitDialog.dismiss();
                boolean isOpen = (boolean) msg.obj;
                if (isOpen) {
                    ToastUtils.showShortToast(mContext, "关闭柜子失败，请联系管理员！");
                } else {
                    String cabinetNumber = cabinetInfo.getCabinetNumber();
                    String[] strs = cabinetNumber.split(",");
                    if (strs.length != 3) {
                        ToastUtils.showShortToast(mContext, "本地数据错误");
                        return;
                    }
                    String serialNo = strs[0];
                    String row = strs[1];
                    String col = strs[2];

                    if (row.length() == 1) {
                        row = "0" + row;
                    }
                    if (col.length() == 1) {
                        col = "0" + col;
                    }

                    //提交取件记录
                    PoVo poVo = new PoVo();
                    poVo.setCabinetSerialNo(serialNo);
                    poVo.setNumber(cabinetInfo.getPaperworkId());
                    poVo.setLocationCode(row + col);
                    List<PoVo> poVoList = new ArrayList<>();
                    poVoList.add(poVo);
                    String strPovolist = GsonHelper.objectToJSONString(poVoList);
                    Call<ResponseInfo<String>> call = NetService.getApiService().tccOutSubmit(strPovolist, null);
                    call.enqueue(new retrofit2.Callback<ResponseInfo<String>>() {
                        @Override
                        public void onResponse(Call<ResponseInfo<String>> call, Response<ResponseInfo<String>> response) {
                            String code = response.body().getCode();
                            if ("200".equals(code)) {
                                Logger.i("申领人取件记录提交成功！");
                            } else {
                                Logger.i("申领人取件记录提交失败！");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseInfo<String>> call, Throwable t) {
                            Logger.i("申领人取件记录提交异常：" + t.getMessage());
                        }
                    });
                    //更新本地数据
                    cabinetInfo.setState("0");
                    cabinetInfo.setPaperworkId("0");
                    cabinetInfo.setUserIdCard("0");
                    dbManager.updateCabinetInfo(cabinetInfo);
                    ToastUtils.showShortToast(mContext, "关闭柜子成功！");
                }
            }
            super.handleMessage(msg);
        }
    };

    private void showNoticeDialog(String info, final boolean isOpen) {
        final Dialog dialog = new Dialog(mContext, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        View view = View.inflate(mContext, R.layout.dialog_open_all, null);
        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        TextView tv_try_again = (TextView) view.findViewById(R.id.tv_try_again);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        if (isOpen) {
            tv_back.setVisibility(View.GONE);
            tv_try_again.setText("关闭柜子");
        } else {
            tv_back.setVisibility(View.VISIBLE);
            tv_try_again.setText("重试");
        }
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
                if (isOpen) {//关闭柜子
                    closeCabinet();
                } else {//重试，打开柜子
                    openCabinet();//打开柜子
                }
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    abstract class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            newTextChanged(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        abstract void newTextChanged(CharSequence charSequence);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
