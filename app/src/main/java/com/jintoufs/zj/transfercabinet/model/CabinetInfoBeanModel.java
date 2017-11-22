package com.jintoufs.zj.transfercabinet.model;

import android.content.Context;

import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.util.SharedPreferencesHelper;

/**
 * Created by zj on 2017/11/22.
 */

public class CabinetInfoBeanModel {
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Context context;

    public CabinetInfoBeanModel(Context context) {
        this.context = context;
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }

    /**
     * 保存一个CabinetInfoBean对象
     * @param cabinetInfoBean
     */
    public void saveCabinetInfoBeanBySP(CabinetInfoBean cabinetInfoBean) {
        if (cabinetInfoBean == null) {
            return;
        }
        sharedPreferencesHelper.put("IpAddress", cabinetInfoBean.getIpAddress());
        sharedPreferencesHelper.put("Col", cabinetInfoBean.getCol());
        sharedPreferencesHelper.put("Row", cabinetInfoBean.getRow());
        sharedPreferencesHelper.put("Id", cabinetInfoBean.getId());
        sharedPreferencesHelper.put("Location", cabinetInfoBean.getLocation());
        sharedPreferencesHelper.put("Name", cabinetInfoBean.getName());
        sharedPreferencesHelper.put("OrgName", cabinetInfoBean.getOrgName());
        sharedPreferencesHelper.put("SerialNo", cabinetInfoBean.getSerialNo());
    }

    /**
     * 从缓存中获取一个CabinetInfoBean对象
     * @return
     */
    public CabinetInfoBean getCabinetInfoBean() {
        CabinetInfoBean cabinetInfoBean = new CabinetInfoBean();
        cabinetInfoBean.setCol((String) sharedPreferencesHelper.get("Col", null));
        cabinetInfoBean.setIpAddress((String) sharedPreferencesHelper.get("IpAddress", null));
        cabinetInfoBean.setRow((String) sharedPreferencesHelper.get("Row", null));
        cabinetInfoBean.setId((String) sharedPreferencesHelper.get("Id", null));
        cabinetInfoBean.setLocation((String) sharedPreferencesHelper.get("Location", null));
        cabinetInfoBean.setName((String) sharedPreferencesHelper.get("Name", null));
        cabinetInfoBean.setOrgName((String) sharedPreferencesHelper.get("OrgName", null));
        return cabinetInfoBean;
    }

}
