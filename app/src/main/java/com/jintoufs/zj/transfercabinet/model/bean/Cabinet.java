package com.jintoufs.zj.transfercabinet.model.bean;

import com.jintoufs.zj.transfercabinet.db.CabinetInfo;

/**
 * Created by zj on 2017/12/4.
 */

public class Cabinet {
    private CabinetInfo cabinetInfo;
    private boolean isOpen;

    public CabinetInfo getCabinetInfo() {
        return cabinetInfo;
    }

    public void setCabinetInfo(CabinetInfo cabinetInfo) {
        this.cabinetInfo = cabinetInfo;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
