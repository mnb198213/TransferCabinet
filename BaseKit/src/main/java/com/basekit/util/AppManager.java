package com.basekit.util;

import android.app.Activity;

import com.basekit.base.BaseActivity;

import java.util.LinkedList;

/**
 * Created by Spirit on 2016/11/28.
 *
 * @des ${TODO}
 */

public class AppManager {

    private static AppManager instance;

    private LinkedList<BaseActivity> mActivityList = new LinkedList<>();

    private AppManager() {
    }

    public static AppManager getInstance(){
        if(instance == null){
            synchronized (AppManager.class){
                if(instance == null){
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获取当前Activity
     * @return
     */
    public BaseActivity getCurrent(){
        return mActivityList.getLast();
    }

    /**
     * 添加 Activity
     * @param activity
     */
    public void add(BaseActivity activity){
        mActivityList.add(activity);
    }

    /**
     * 移除指定的Activity
     * @param activity
     */
    public void remove(BaseActivity activity){
        //在遍历集合时，作删除操作时，需要注意：使用Interator 或 反向遍历
        for (int i = mActivityList.size() - 1; i >= 0; i--) {
            Activity tempActivity = mActivityList.get(i);
            if(tempActivity.getClass().equals(activity.getClass())){
                tempActivity.finish();
                mActivityList.remove(tempActivity);
                activity = null;
                break;
            }
        }
    }

    /**
     * 移除所有Activity
     */
    public void removeAll(){
        for (int i = mActivityList.size() - 1; i >= 0 ; i--) {
            Activity activity = mActivityList.get(i);
            activity.finish();
            mActivityList.remove(activity);
        }
    }

    /**
     * 移除当前栈顶的Activity
     */
    public void removeCurrent(){
        BaseActivity lastActivity = mActivityList.getLast();
        lastActivity.finish();
        mActivityList.remove(lastActivity);
    }

    /**
     *
     * @return 集合的大小
     */
    public int getSize(){
        return mActivityList.size();
    }

}
