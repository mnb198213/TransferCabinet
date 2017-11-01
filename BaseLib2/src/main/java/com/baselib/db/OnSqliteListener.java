package com.baselib.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库更新监听器
 * Created by Spirit on 2017/4/6 15:40.
 */

public interface OnSqliteListener {

    void onCreateTable(SQLiteDatabase db);

    void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion);
}
