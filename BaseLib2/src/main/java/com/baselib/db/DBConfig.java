package com.baselib.db;

import android.content.Context;

/**
 * Created by Spirit on 2017/3/16 17:13.
 */

public class DBConfig {

    /**
     * 默认数据库名，可修改
     */
    public static String DEFAULT_DB_NAME = "app.db";
    /**
     * 默认版本号，可修改
     */
    public static int DEFAULT_VERSION = 1;

    private Context mContext = null; // android上下文
    private String mDbName;
    private int mDbVersion;
    private OnSqliteListener mOnSqliteListener;
    private boolean mDebug = true; // 是否是调试模式（调试模式 增删改查的时候显示SQL语句）

    public DBConfig(Builder builder) {
        mContext = builder.context;
        mDbName = builder.dbName;
        mDbVersion = builder.dbVersion;
        mOnSqliteListener = builder.sqliteListener;
        mDebug = builder.debug;
    }


    public Context getContext() {
        return mContext;
    }

    public String getDbName() {
        return mDbName;
    }

    public int getDbVersion() {
        return mDbVersion;
    }

    public OnSqliteListener getOnSqliteListener() {
        return mOnSqliteListener;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public static final class Builder {
        private Context context;
        private String dbName = DEFAULT_DB_NAME; // 数据库名字;
        private int dbVersion = DEFAULT_VERSION;
        private boolean debug = true;
        private OnSqliteListener sqliteListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setOnSqliteListener(OnSqliteListener listener){
            this.sqliteListener = listener;
            return this;
        }

        public DBConfig build() {
            return new DBConfig(this);
        }
    }
}
