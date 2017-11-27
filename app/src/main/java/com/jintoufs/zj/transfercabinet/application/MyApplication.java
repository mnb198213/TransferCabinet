package com.jintoufs.zj.transfercabinet.application;

import android.app.Application;

import com.jintoufs.zj.transfercabinet.net.NetService;
import com.jintoufs.zj.transfercabinet.port.SerialPort;
import com.jintoufs.zj.transfercabinet.port.SerialPortFinder;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidParameterException;

/**
 * Created by zj on 2017/9/5.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    @Override
    public void onCreate() {
        super.onCreate();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("zoujiang")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
//            mSerialPort = new SerialPort(new File("/dev/ttyHBB0"), 9600, 0);//金属键盘
            mSerialPort = new SerialPort(new File("/dev/ttyACM0"), 9600, 0);//扫描仪
        }
        return mSerialPort;
    }

    @Override
    public void onTerminate() {
        closeSerialPort();
        super.onTerminate();
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
