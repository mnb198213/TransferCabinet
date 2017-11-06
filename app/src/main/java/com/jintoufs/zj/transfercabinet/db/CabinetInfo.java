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
    @Property(nameInDb = "USERID")
    private String userId;//一个柜子对应的用户id
    @Property(nameInDb = "CABINETNUMBER")
    private String cabinetNumber;//交接柜的编号+柜子的行列号（xxxxxxxxxxx,xx,xx）
    @Property(nameInDb = "PAPERWORKID")
    private String paperworkId;//一个柜子里面所有证件的id号
    @Property(nameInDb = "STATUE")
    private int statue;//标识柜子的开关状态，1为开，0为关

    @Generated(hash = 1560636593)
    public CabinetInfo(Long id, String userId, String cabinetNumber,
                       String paperworkId, int statue) {
        this.id = id;
        this.userId = userId;
        this.cabinetNumber = cabinetNumber;
        this.paperworkId = paperworkId;
        this.statue = statue;
    }

    @Generated(hash = 1973192954)
    public CabinetInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }
}
