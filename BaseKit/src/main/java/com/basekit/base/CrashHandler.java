package com.basekit.base;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Spirit on 2017/3/30 16:18.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    //程序的Context对象
    private Context mContext;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();


    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    //错误日志保存目录，默认目录
    private String crashDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jintou/crash/";

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 初始化
     * 可配置异常日志的目录
     * @param context
     */
    public void init(Context context,String crashDir) {
        mContext = context;
        this.crashDir = crashDir;

        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        // 打印异常信息
        e.printStackTrace();

        if (!handleException(e) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, e);
        } else {
            SystemClock.sleep(2000);
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }


    /**
     * 处理异常信息
     * @param ex
     * @return
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        try {
            //使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序出现异常", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            //收集设备参数信息
            collectDeviceInfo(mContext);
            //保存日志文件
            saveCrashInfo2File(ex);


            // 重启应用（按需要添加是否重启应用）
            /*Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            SystemClock.sleep(1000);*/
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    //用来存储设备信息和异常信息
    private Map<String, String> infoMap = new HashMap<>();
    /**
     * 收集设备信息
     * @param context
     */
    private void collectDeviceInfo(Context context) {

        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);

            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                infoMap.put("versionName", versionName);
                infoMap.put("versionCode", versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }

    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        try {
            //当前时间戳
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            long timestamp = System.currentTimeMillis();
            String time = sdf.format(new Date(timestamp));
            sb.append("CrashTime:" + time + "\n");

            for (Map.Entry<String, String> entry : infoMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);

            String fileName = writeFile(sb.toString());
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
        }
        return "";
    }

    @Nullable
    private String writeFile(String crashStr) {
        //开始写异常日志文件
        try {
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(crashDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File logFile = new File(dir, fileName);

                FileOutputStream fos = new FileOutputStream(logFile,true);
                fos.write(crashStr.getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }


    private void autoClear(int autoClearDay){

    }

    /**
     * 获取错误报告文件名
     * @param crashDir
     * @return
     */
    private String[] getCrashFiles(String crashDir) {
        File crashFileDir = new File(crashDir);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        };
        return crashFileDir.list(filter);
    }

}
