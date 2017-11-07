package com.jintoufs.zj.transfercabinet.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
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
import com.jintoufs.zj.transfercabinet.R;
import com.jintoufs.zj.transfercabinet.util.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Override
    public void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_pickup);
        unbinder = ButterKnife.bind(this);
        mContext = this;
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

        tvTime.setText("当前时间："+TimeUtil.DateToString(new Date()));
    }

    @OnClick({R.id.btn_sure, R.id.btn_back,R.id.rl_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                showNoticeDialog("抽屉已打开，请取走你的证件");
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
            }
        });
        window.setContentView(view);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
