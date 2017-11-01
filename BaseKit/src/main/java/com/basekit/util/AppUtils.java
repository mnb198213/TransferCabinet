package com.basekit.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Spirit on 2017/4/11.
 *
 * @des ${TODO}
 */

public class AppUtils {


    /**
     * 获取App版本名称
     *
     * @param context 上下文
     * @return App版本名称
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * 获取指定包名App版本名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本名称
     */
    public static String getAppVersionName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)){
            return "";
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取App版本码
     *
     * @param context 上下文
     * @return App版本码
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 获取App版本码
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本码
     */
    public static int getAppVersionCode(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)){
            return -1;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 安装App(支持6.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        installApp(context, new File(filePath));
    }

    /**
     * 安装App（支持6.0）
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void installApp(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        context.startActivity(getInstallAppIntent(context,file));
    }

    /**
     * 安装App（支持6.0）
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param requestCode 请求值
     */
    public static void installAppForResult(Activity activity, String filePath, int requestCode) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        installAppForResult(activity, new File(filePath), requestCode);
    }

    /**
     * 安装App(支持6.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param requestCode 请求值
     */
    public static void installAppForResult(Activity activity, File file, int requestCode) {
        if (file == null || !file.exists()) {
            return;
        }
        activity.startActivityForResult(getInstallAppIntent(activity,file), requestCode);
    }






    /**
     * 获取安装App（支持6.0）的意图
     *
     * @param filePath 文件路径
     * @return intent
     */
    public static Intent getInstallAppIntent(Context context,String filePath) {
        return getInstallAppIntent(context, new File(filePath));
    }

    /**
     * 获取安装App(支持6.0)的意图
     *
     * @param file 文件
     * @return intent
     */
    public static Intent getInstallAppIntent(Context context,File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            type = "application/vnd.android.package-archive";
        } else {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getFileExtension(file));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
