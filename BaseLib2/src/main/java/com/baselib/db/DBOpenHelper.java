package com.baselib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Spirit on 2017/3/16 16:04.
 */

public class DBOpenHelper extends SQLiteOpenHelper {


    /**
     * 建表语句列表
     */

    private OnSqliteListener onSqliteListener;


    public DBOpenHelper(DBConfig dbConfig){
        super(dbConfig.getContext(),dbConfig.getDbName(),null,dbConfig.getDbVersion());
        onSqliteListener = dbConfig.getOnSqliteListener();
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (onSqliteListener != null) {
            onSqliteListener.onCreateTable(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (onSqliteListener != null) {
            onSqliteListener.onUpdate(db,oldVersion,newVersion);
        }


    }
}
