package com.baselib.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by Spirit on 2017/3/16 17:22.
 */

public class DBManager {

    private static DBConfig sDbConfig;
    private static DBManager instance;
    private DBOpenHelper mDBOpenHelper;
    private SQLiteDatabase db;
    /**
     * 用于保存DBManager对象
     */
    private static HashMap<String, DBManager> dbManagerMap = new HashMap<>();

    /**
     * 私有化构造器
     */
    private DBManager() {

        if (sDbConfig == null) {
            throw new RuntimeException("daoConfig is null");
        }
        if (sDbConfig.getContext() == null) {
            throw new RuntimeException("android context is null");
        }
        //
        mDBOpenHelper = new DBOpenHelper(sDbConfig);
        if (db == null) {
            db = mDBOpenHelper.getWritableDatabase();
        }

    }


    /**
     * 单例DbManager类
     *
     * @return 返回DbManager对象
     */
    public static void init(DBConfig dbConfig) {
        sDbConfig = dbConfig;
    }


    //单例模式 方便外部调用
    public static DBManager getInstance() {
        DBManager dbManager = dbManagerMap.get(sDbConfig.getDbName());
        if (dbManager == null) {
            dbManager = new DBManager();
            dbManagerMap.put(sDbConfig.getDbName(), dbManager);
        }
        return dbManager;
    }


    /**
     * 获取数据库的对象
     *
     * @return 返回SQLiteDatabase数据库的对象
     */
    public SQLiteDatabase getDataBase() {
        return db;
    }


    public void close() {
        if (mDBOpenHelper != null) {
            mDBOpenHelper.close();
        }
    }

    public void beginTransaction() {
        if (db != null) {
            db.beginTransaction();
        }
    }

    public void endTransaction() {
        if (db != null && db.inTransaction()) {
            db.endTransaction();
        }
    }

    public void setTransactionSuccessful() {
        if (db != null) {
            db.setTransactionSuccessful();
        }
    }
}
