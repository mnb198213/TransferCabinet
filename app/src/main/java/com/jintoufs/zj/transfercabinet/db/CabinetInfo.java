package com.jintoufs.zj.transfercabinet.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by zj on 2017/11/6.
 */
@Entity
public class CabinetInfo {
    @Id
    private Long id;
    @Property(nameInDb = "USERIDCARD")
    private String userIdCard;//一个柜子对应的用户身份证号
    @Property(nameInDb = "USERNAME")
    private String username;//一个柜子对应的用户名
    @Property(nameInDb = "DEPARTMENT")
    private String department;//一个柜子对应用户的所属部门
    @Property(nameInDb = "CABINETNUMBER")
    private String cabinetNumber;//交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
    @Property(nameInDb = "PAPERWORKID")
    private String paperworkId;//一个柜子里面证件的证件号
    @Property(nameInDb = "TYPE")//0 护照 ；1 港澳通行证 ；2 台湾通行证
    private String type;//证件类型
    @Property(nameInDb = "STATE")//1 待申领人领取   2 待库管员回收  0 其他
    private String state;//证件状态


    @Generated(hash = 1467445998)
    public CabinetInfo(Long id, String userIdCard, String username,
            String department, String cabinetNumber, String paperworkId,
            String type, String state) {
        this.id = id;
        this.userIdCard = userIdCard;
        this.username = username;
        this.department = department;
        this.cabinetNumber = cabinetNumber;
        this.paperworkId = paperworkId;
        this.type = type;
        this.state = state;
    }

    @Generated(hash = 1973192954)
    public CabinetInfo() {
    }


    public String getUserIdCard() {
        return userIdCard;
    }

    public void setUserIdCard(String userIdCard) {
        this.userIdCard = userIdCard;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public String getPaperworkId() {
        return paperworkId;
    }

    public void setPaperworkId(String paperworkId) {
        this.paperworkId = paperworkId;
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
