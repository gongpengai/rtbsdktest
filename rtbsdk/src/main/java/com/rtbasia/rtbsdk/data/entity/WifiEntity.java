package com.rtbasia.rtbsdk.data.entity;

import java.io.Serializable;

/**
 * Create by Juan.gong 2019-10-16
 */
public class WifiEntity implements Serializable {

    private String SSID;
    private String BSSID;// = 2;
    private String level;// = 3;//wifi的信号强度
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
