package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * Created by zj on 2017/11/1.
 */

public class CabinetInfoBean {
    private String Type;
    private String username;
    private String agency;
    private String IDNumber;
    private String cabinetId;
    private String drawerId;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }
}
