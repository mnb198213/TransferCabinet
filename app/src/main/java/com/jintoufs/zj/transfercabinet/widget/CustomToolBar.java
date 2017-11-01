package com.jintoufs.zj.transfercabinet.widget;

import android.content.Context;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;


/**
 * 自定义ToolBar
 * Created by zoujiang on 2016/11/16.
 */

public class CustomToolBar extends Toolbar {
    private ImageView iv_left, iv_right;
    private TextView tv_title, tv_left, tv_right;
    private LinearLayout ll_left, ll_right;
    private View view;

    private OnLeftClickListener mOnLeftClickListener;
    private OnRightClickListener mOnRightClickListener;

    public CustomToolBar(Context context) {
        this(context, null);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = LayoutInflater.from(context).inflate(R.layout.custom_toolbar, null);
        iv_left = (ImageView) view.findViewById(R.id.iv_left);
        iv_right = (ImageView) view.findViewById(R.id.iv_right);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_left = (TextView) view.findViewById(R.id.tv_left);
        tv_right = (TextView) view.findViewById(R.id.tv_right);
        ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
        ll_right = (LinearLayout) view.findViewById(R.id.ll_right);

        TintTypedArray ta = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.CustomToolBar, defStyleAttr, 0);
        ll_left.setPadding((int) ta.getDimension(R.styleable.CustomToolBar_distance_left, 0), 0, 0, 0);
        ll_right.setPadding(0, 0, (int) ta.getDimension(R.styleable.CustomToolBar_distance_right, 0), 0);
        //中间标题
        tv_title.setText(ta.getString(R.styleable.CustomToolBar_title));
        //左边图标
        if (ta.getBoolean(R.styleable.CustomToolBar_isShowLeftImage, false)) {
            iv_left.setImageResource(ta.getResourceId(R.styleable.CustomToolBar_iv_left, R.mipmap.ic_launcher));
        } else {
            iv_left.setVisibility(View.GONE);
        }
        //左边标题
        if (ta.getBoolean(R.styleable.CustomToolBar_isShowLeftText, false)) {
            tv_left.setText(ta.getString(R.styleable.CustomToolBar_tv_left));
        } else {
            tv_left.setVisibility(View.GONE);
        }
        //右边标题
        if (ta.getBoolean(R.styleable.CustomToolBar_isShowRightText, false)) {
            tv_right.setText(ta.getString(R.styleable.CustomToolBar_tv_right));
        } else {
            tv_right.setVisibility(View.GONE);
        }
        //右边图标
        if (ta.getBoolean(R.styleable.CustomToolBar_isShowRightImage, false)) {
            iv_right.setImageResource(ta.getResourceId(R.styleable.CustomToolBar_iv_right, R.mipmap.ic_launcher));
        } else {
            iv_right.setVisibility(View.GONE);
        }

        ll_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLeftClickListener != null) {
                    mOnLeftClickListener.onLeftClick();
                }
            }
        });
        ll_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRightClickListener != null) {
                    mOnRightClickListener.onRightClick();
                }
            }
        });

        ta.recycle();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL);
        addView(view, lp);
    }

    public interface OnLeftClickListener {
        void onLeftClick();
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    public void setmOnLeftClickListener(OnLeftClickListener mOnLeftClickListener) {
        this.mOnLeftClickListener = mOnLeftClickListener;
    }

    public void setmOnRightClickListener(OnRightClickListener mOnRightClickListener) {
        this.mOnRightClickListener = mOnRightClickListener;
    }
}
