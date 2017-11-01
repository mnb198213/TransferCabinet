package com.basekit.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by Spirit on 2017/3/30 16:01.
 */

public class BaseApplication extends Application {

    private static BaseApplication sInstance;
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContext = this.getApplicationContext();

        CrashHandler.getInstance().init(this);
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }

    public static Context getContext(){
        return sContext;
    }
}
