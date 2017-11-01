package com.baselib.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Spirit on 2017/4/6 17:37.
 */

public class DB {

    private DBConfig mDBConfig;

    private SQLiteDatabase mDatabase;

    private DBOpenHelper mDBOpenHelper;

    public DB(DBConfig dbConfig) {
        mDBConfig = dbConfig;

        createDb();
    }

    public void createDb(){
        mDBOpenHelper = new DBOpenHelper(mDBConfig);
        mDatabase = mDBOpenHelper.getWritableDatabase();
        if(mDatabase == null) {
            throw new NullPointerException("创建数据库对象失败");
        }
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    /**
     * 判断当前数据库是否打开
     * @return
     */
    public boolean isOpen() {
        return this.mDatabase.isOpen();
    }


    public void close() {
        if (mDBOpenHelper != null) {
            mDBOpenHelper.close();
        }
    }

    public void beginTransaction() {
        if (mDatabase != null) {
            mDatabase.beginTransaction();
        }
    }

    public void endTransaction() {
        if (mDatabase != null && mDatabase.inTransaction()) {
            mDatabase.endTransaction();
        }
    }

    public void setTransactionSuccessful() {
        if (mDatabase != null) {
            mDatabase.setTransactionSuccessful();
        }
    }
}
