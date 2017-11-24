package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * Created by zj on 2017/11/23.
 */

public class PoVo {
    private String number;//证件号
    private String cabinetSerialNo;//交接柜编号
    private String locationCode;//柜门号

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCabinetSerialNo() {
        return cabinetSerialNo;
    }

    public void setCabinetSerialNo(String cabinetSerialNo) {
        this.cabinetSerialNo = cabinetSerialNo;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
}
