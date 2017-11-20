package com.jintoufs.zj.transfercabinet.model.bean;

/**
 * Created by zj on 2017/11/20.
 */

public class CabinetInfoBean {
    //    {"col":30,
    // "id":"00015fb8740ee003",
    // "ipAddress":"192.168.0.1",
    // "location":"一号房间",
    // "name":"一号柜子",
    // "orgName":"作业中心",
    // "row":10,
    // "serialNo":"0001"}

    private String col;
    private String id;
    private String ipAddress;
    private String location;
    private String name;
    private String orgName;
    private String row;
    private String serialNo;

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
