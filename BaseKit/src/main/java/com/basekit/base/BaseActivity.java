package com.basekit.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.basekit.util.AppManager;
import com.basekit.util.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity {

    private BaseActivity mCurActivity;

    public static final String TAG = "zoujiang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initListener();
        AppManager.getInstance().add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurActivity = AppManager.getInstance().getCurrent();
        //强制横屏
//        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
    }

    @Override
    protected void onDestroy() {
        AppManager.getInstance().remove(this);
        super.onDestroy();
    }

    public abstract void initView();

    public void initData() {
    }

    public void initListener() {
    }

    /**
     * 显示短Toast
     *
     * @param text
     */
    public void showShortToast(String text) {
        ToastUtils.showShortToast(this, text);
    }

    /**
     * 显示长Toast
     *
     * @param text
     */
    public void showLongToast(String text) {
        ToastUtils.showLongToast(this, text);
    }

    /**
     * 完全退出
     */
    protected void exit() {
        AppManager.getInstance().removeAll();
    }

    /**
     * 关闭当前activity
     */
    public void finishCurrent() {
        AppManager.getInstance().removeCurrent();
    }


}
