package com.basekit.util;

/**
 * Created by Spirit on 2016/12/5.
 *
 * @des ${TODO}
 */

public class NetworkUtils {
    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断网络是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailableByPing() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -w 1 123.125.114.144", false);
        return result.result == 0;
    }
}
