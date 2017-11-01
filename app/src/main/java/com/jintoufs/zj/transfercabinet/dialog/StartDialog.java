package com.jintoufs.zj.transfercabinet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by zj on 2017/9/5.
 */

public class StartDialog extends Dialog {
    private View view;
    private TextView tv_action1;
    private TextView tv_action2;
    private TextView tv_cancel;
    private OnAction1ClickListener mOnAction1ClickListener;
    private OnAction2ClickListener mOnAction2ClickListener;

    int width;
    int height;

    public StartDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public StartDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context) {
        view = View.inflate(context, R.layout.dialog_start, null);
        tv_action1 = (TextView) view.findViewById(R.id.tv_action1);
        tv_action2 = (TextView) view.findViewById(R.id.tv_action2);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAction1ClickListener != null) {
                    mOnAction1ClickListener.onAction1Click(v);
                }
            }
        });
        tv_action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAction2ClickListener != null) {
                    mOnAction2ClickListener.onAction2Click(v);
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height / 3);
        getWindow().setContentView(view, lp);

        setCanceledOnTouchOutside(false);
    }

    public interface OnAction1ClickListener {
        void onAction1Click(View view);
    }

    public interface OnAction2ClickListener {
        void onAction2Click(View view);
    }

    public void setOnAction1ClickListener(OnAction1ClickListener mOnAction1ClickListener) {
        this.mOnAction1ClickListener = mOnAction1ClickListener;
    }

    public void setOnAction2ClickListener(OnAction2ClickListener mOnAction2ClickListener) {
        this.mOnAction2ClickListener = mOnAction2ClickListener;
    }

    public void setStrAction1(String strAction1) {
        tv_action1.setText(strAction1);
    }

    public void setStrAction2(String strAction2) {
        tv_action2.setText(strAction2);
    }
}
