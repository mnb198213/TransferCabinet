package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * Created by zj on 2017/11/9.
 */

public class CertUser {
//    {String userName,String idCard，String orgName}
    private String userName;
    private String idCard;
    private String orgName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
