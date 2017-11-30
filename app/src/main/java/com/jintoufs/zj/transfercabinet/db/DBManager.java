package com.jintoufs.zj.transfercabinet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jintoufs.zj.transfercabinet.config.AppConstant;
import com.jintoufs.zj.transfercabinet.model.CabinetModel;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zj on 2017/11/6.
 */

public class DBManager {
    private final static String dbName = "cabinetinfo_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;
    private ExecutorService threadPool;//线程池
    private SharedPreferencesHelper sharedPreferencesHelper;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        threadPool = Executors.newCachedThreadPool();
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
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
     * 插入一条柜子信息数据
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
     * 删除一条柜子信息数据
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
        List<CabinetInfo> cabinetInfoList = queryBuilder.build().list();
        return cabinetInfoList;
    }

    /**
     * 根据柜子行列号查询单个柜子信息
     *
     * @param cabinetNumber
     * @return
     */
    public CabinetInfo querySingleCabinet(String cabinetNumber) {
        QueryBuilder<CabinetInfo> queryBuilder = getReadCabinetInfoDao().queryBuilder();
        queryBuilder.where(CabinetInfoDao.Properties.CabinetNumber.eq(cabinetNumber));
        List<CabinetInfo> cabinetInfoList = queryBuilder.list();
        if (cabinetInfoList != null && cabinetInfoList.size() == 1)
            return cabinetInfoList.get(0);
        else return null;
    }

    /**
     * 查询到所有空箱子列表中的第一个空箱子
     *
     * @return
     */
    private List<CabinetInfo> emptyCabinetInfoList;
    private Socket socket;
    private CabinetInfo cabinetInfo;

    public CabinetInfo openAEmptyCabinet() {
        emptyCabinetInfoList = queryAllEmptyCabinet();
        if (emptyCabinetInfoList != null && emptyCabinetInfoList.size() > 0) {
            final String ipAdress = (String) sharedPreferencesHelper.get("IpAddress", null);
            if (ipAdress == null) return null;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    int count = emptyCabinetInfoList.size();
                    for (int i = 0; i < count; i++) {
                        CabinetInfo tempCabinetInfo = emptyCabinetInfoList.get(i);
                        //交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
                        String str = tempCabinetInfo.getCabinetNumber();
                        String strs[] = str.split(",");
                        if (strs.length != 3) return;
                        String row = strs[1];
                        String col = strs[2];
                        try {
                            if (socket == null) socket = new Socket(ipAdress, AppConstant.PORT);
                            int number = 0;
                            boolean isOpen = false;
                            while (!isOpen && number != 5) {
                                CabinetModel.openDrawer(socket, row, col);
                                Thread.sleep(500);
                                isOpen = CabinetModel.isOpen(socket, row, col);
                                number++;
                            }
                            if (isOpen) {
                                cabinetInfo = emptyCabinetInfoList.get(i);
                                return;
                            }
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Logger.i("开空柜子 " + row + "|" + col + " 异常：" + e.getMessage());
                        }
                    }
                }
            });
            return cabinetInfo;
        } else
            return null;
    }

    /**
     * 查询到所有空的柜子
     *
     * @return
     */
    public List<CabinetInfo> queryAllEmptyCabinet() {
        QueryBuilder<CabinetInfo> queryBuilder = getReadCabinetInfoDao().queryBuilder();
        queryBuilder.where(CabinetInfoDao.Properties.PaperworkId.eq("0"), CabinetInfoDao.Properties.UserIdCard.eq("0"));
        List<CabinetInfo> cabinetInfoList = queryBuilder.list();
        return cabinetInfoList;
    }

    /**
     * 查询所有已使用的柜子
     *
     * @return
     */
    public List<CabinetInfo> queryUseredCabinetList() {
        QueryBuilder<CabinetInfo> queryBuilder = getReadCabinetInfoDao().queryBuilder();
        queryBuilder.where(CabinetInfoDao.Properties.PaperworkId.notEq("0"), CabinetInfoDao.Properties.UserIdCard.notEq("0"));
        List<CabinetInfo> cabinetInfoList = queryBuilder.list();
        return cabinetInfoList;
    }

    /**
     * 释放线程池和socket占用的资源
     */
    public void releaseThreadPool() throws IOException {
        if (threadPool != null) {
            threadPool.shutdown();
        }
        if (socket != null) {
            socket.close();
        }
    }


}
