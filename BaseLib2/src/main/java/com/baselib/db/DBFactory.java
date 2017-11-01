package com.baselib.db;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;


/**
 * Created by Spirit on 2017/4/6 17:46.
 */

public class DBFactory {

    /**
     * 多个数据库集合对象<dbName, {@link}SQLiteDB>
     */
    private static SimpleArrayMap<String, DB> dbMap = new SimpleArrayMap<>();

    /**
     * 在默认目录下生成默认名称的数据库
     * Author: hyl
     * Time: 2015-8-23下午6:37:55
     * @param context
     * @return
     */
    public static DB createDB(Context context) {
        return createDB(new DBConfig.Builder(context).build());
    }

    /**
     * 生成一个名为dnName的数据库，目录为默认目录(参考SQLiteDBConfig里面的目录设置)}
     * Time: 2015-8-23下午6:36:34
     * @param context
     * @param dbName		要生成的数据库名称
     * @return
     */
    public static DB createDB(Context context, String dbName) {
        DBConfig.Builder builder = new DBConfig.Builder(context);
        builder.setDbName(dbName);
        return createDB(builder.build());
    }


    /**
     * 根据自定义配置生成数据库
     * Time: 2015-8-23下午6:38:15
     * @param dbConfig
     * @return
     */
    public static DB createDB(DBConfig dbConfig) {
        DB db = dbMap.get(dbConfig.getDbName());
        if (db == null) {
            synchronized (DBFactory.class) {
                if (db == null) {
                    db = new DB(dbConfig);
                    dbMap.put(dbConfig.getDbName(), db);
                }
            }
        }
        if(!db.isOpen()) {
            db.createDb();
        }
        return db;
    }
}
