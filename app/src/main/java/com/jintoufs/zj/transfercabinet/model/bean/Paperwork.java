package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * 证件类
 * Created by zj on 2017/10/31.
 */

public class Paperwork {
    private String username;
    private String sex;
    private String birthDate;
    private String nation;      //民族
    private String IDNumber;    //身份证号
    private String phone;
    private String agency;      //机构
    private String type;        //证件类型
    private String number;      //证件号
    private String phyNumber;   //物理序号
    private String image;       //证件图片

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhyNumber() {
        return phyNumber;
    }

    public void setPhyNumber(String phyNumber) {
        this.phyNumber = phyNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
