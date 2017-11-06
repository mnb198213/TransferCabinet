package com.jintoufs.zj.transfercabinet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * Created by zj on 2017/11/6.
 */

public class DBManager {
    private final static String dbName = "cabinetinfo_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    private CabinetInfoDao getWriteCabinetInfoDao() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CabinetInfoDao cabinetInfoDao = daoSession.getCabinetInfoDao();
        return cabinetInfoDao;
    }

    /**
     * 插入一条数据
     *
     * @param cabinetInfo
     */
    public void insertCabinetInfo(CabinetInfo cabinetInfo) {
        getWriteCabinetInfoDao().insert(cabinetInfo);
    }

    /**
     * 插入集合数据
     *
     * @param cabinetInfoList
     */
    public void insertCabinetInfoList(List<CabinetInfo> cabinetInfoList) {
        if (cabinetInfoList == null || cabinetInfoList.isEmpty()) {
            return;
        }
        getWriteCabinetInfoDao().insertInTx(cabinetInfoList);
    }

    /**
     * 删除一条数据
     *
     * @param cabinetInfo
     */
    public void deleteCabinetInfo(CabinetInfo cabinetInfo) {
        getWriteCabinetInfoDao().delete(cabinetInfo);
    }

    /**
     * 更新一条数据
     *
     * @param cabinetInfo
     */
    public void updateCabinetInfo(CabinetInfo cabinetInfo) {
        getWriteCabinetInfoDao().update(cabinetInfo);
    }

    private CabinetInfoDao getReadCabinetInfoDao() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CabinetInfoDao cabinetInfoDao = daoSession.getCabinetInfoDao();
        return cabinetInfoDao;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<CabinetInfo> queryAllCabinetInfos() {
        QueryBuilder<CabinetInfo> queryBuilder = getReadCabinetInfoDao().queryBuilder();
        List<CabinetInfo> cabinetInfoList = queryBuilder.list();
        return cabinetInfoList;
    }

    /**
     * 根据柜子行列号查询单个柜子信息
     *
     * @param cabinetNumber
     * @return
     */
    public List<CabinetInfo> querySingleCabinet(String cabinetNumber) {
        QueryBuilder<CabinetInfo> queryBuilder = getReadCabinetInfoDao().queryBuilder();
        queryBuilder.where(CabinetInfoDao.Properties.CabinetNumber.eq(cabinetNumber));
        List<CabinetInfo> cabinetInfoList = queryBuilder.list();
        return cabinetInfoList;
    }

}
