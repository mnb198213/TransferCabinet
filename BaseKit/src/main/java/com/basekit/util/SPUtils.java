package com.basekit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;

import java.util.Map;
import java.util.Set;

/**
 * Created by Spirit on 2017/3/23.
 *
 * @des ${TODO}
 */

public class SPUtils {

    //SharePreferences默认文件名称
    private static String PREFERENCES_NAME = "PrefsName";
    private Context mContext;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static SPUtils instance;


    /**
     * 构造方法
     * @param context
     */
    private SPUtils(Context context) {
        this(context, PREFERENCES_NAME);
    }

    /**
     * 构造方法
     * @param context
     * @param preferencesName
     */
    private SPUtils(Context context, String preferencesName) {
        mContext = context;
        sp = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SPUtils getInstance(Context context) {
        return getInstance(context, PREFERENCES_NAME);
    }

    public static SPUtils getInstance(Context context, String preferencesName) {
        if (instance == null) {
            synchronized (SPUtils.class) {
                if (instance == null) {
                    instance = new SPUtils(context, preferencesName);
                }
            }
        }
        return instance;
    }

    /**
     * 存入某个key对应的value值
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 得到某个key对应的值
     *
     * @param key
     * @param defValue
     * @return
     */
    public Object get(String key, Object defValue) {
        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    public Map<String, ?> getAll() {

        return sp.getAll();
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    public Set<String> getStringSet(String key) {
        return getStringSet(key, null);
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    /**
     * SP中写入String集合类型value
     *
     * @param key    键
     * @param values 值
     */
    public void putStringSet(String key, @Nullable Set<String> values) {
        editor.putStringSet(key, values).apply();
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void remove(String key) {
        editor.remove(key).apply();
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * SP中清除所有数据
     */
    public void clear() {
        editor.clear().apply();
    }
}
