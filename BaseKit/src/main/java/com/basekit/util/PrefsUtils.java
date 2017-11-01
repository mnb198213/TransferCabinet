package com.basekit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;

import java.util.Map;
import java.util.Set;

/**
 * Created by Spirit on 2017/4/14 15:01.
 */

public class PrefsUtils {

    //SharePreferences默认文件名称
    private final String PREFS_NAME_DEFAULT = "Prefs";
    private String mPrefsFileName = PREFS_NAME_DEFAULT;
    private Context mContext;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static PrefsUtils instance;
    private boolean valid = false;

    private PrefsUtils() {
    }

    public static void init(Context context){
        getInstance().initPrefs(context);
    }

    public static void init(Context context,String prefsFileName){
        getInstance().initPrefs(context,prefsFileName);
    }

    private void initPrefs(Context context){
        initPrefs(context, PREFS_NAME_DEFAULT);
    }

    /**
     * 初始化
     * @param context
     * @param prefsFileName
     */
    private void initPrefs(Context context,String prefsFileName){
        mContext = context;
        mPrefsFileName = prefsFileName;
        valid = true;
        sp = mContext.getSharedPreferences(mPrefsFileName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    private static PrefsUtils getInstance() {
        if (null == instance) {
            synchronized (PrefsUtils.class) {
                if (null == instance) {
                    instance = new PrefsUtils();
                }
            }
        }
        return instance;
    }


    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("this should only be called when LitePrefs didn't initialize once");
        }
    }


    public static void put(String key,Object value){
        getInstance().putValue(key, value);
    }


    /**
     * 存入某个key对应的value值
     *
     * @param key
     * @param value
     */
    private void putValue(String key, Object value) {
        checkValid();

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


    public static Object get(String key,Object defValue){
        return getInstance().getValue(key, defValue);
    }
    /**
     * 得到某个key对应的值
     *
     * @param key
     * @param defValue
     * @return
     */
    private Object getValue(String key, Object defValue) {

        checkValid();

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
        return defValue;
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    public static Map<String, ?> getAll() {
        return getInstance().getAllMap();
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    private Map<String, ?> getAllMap() {
        return sp.getAll();
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    public static Set<String> getStringSet(String key) {
        return getInstance().getStringSetPres(key);
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    private Set<String> getStringSetPres(String key) {
        return getStringSetPres(key, null);
    }


    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        return getInstance().getStringSetPres(key, defaultValue);
    }
    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    private Set<String> getStringSetPres(String key, Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }


    /**
     * SP中写入String集合类型value
     *
     * @param key    键
     * @param values 值
     */
    public static void putStringSet(String key, @Nullable Set<String> values) {
        getInstance().putStringSetApply(key,values);
    }

    /**
     * SP中写入String集合类型value
     *
     * @param key    键
     * @param values 值
     */
    private void putStringSetApply(String key, @Nullable Set<String> values) {
        editor.putStringSet(key, values).apply();
    }


    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public static void remove(String key) {
        getInstance().removeKey(key);
    }


    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void removeKey(String key) {
        editor.remove(key).apply();
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean contains(String key) {
        return getInstance().containsKey(key);
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean containsKey(String key) {
        return sp.contains(key);
    }

    /**
     * SP中清除所有数据
     */
    public static void clear() {
        getInstance().clearPrefs();
    }
    /**
     * SP中清除所有数据
     */
    private void clearPrefs() {
        editor.clear().apply();
    }
}
