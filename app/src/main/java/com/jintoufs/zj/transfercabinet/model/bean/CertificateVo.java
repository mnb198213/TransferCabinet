package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * Created by zj on 2017/11/9.
 */

public class CertificateVo {

    private String certUserId;//证件用户Id
    private String userName;//证件用户名
    private String sex;//1 男  ； 2 女
    private String bornDate;
    private String nation;//民族
    private String idCard;//用户身份证
    private String phone;//用户电话
    private String orgName;//用户机构
    private String type;//0 护照 ； 1 港澳通行证 ； 2 台湾通行证
    private String number;//证件号
    private String image;//base64形式

    public String getCertUserId() {
        return certUserId;
    }

    public void setCertUserId(String certUserId) {
        this.certUserId = certUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
