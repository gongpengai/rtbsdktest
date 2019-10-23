package com.rtbasia.rtbsdk.data.entity;

import java.io.Serializable;

/**
 * Create by Juan.gong 2019-10-16
 */
public class LocationEntity implements Serializable {

    private String latitude;//1;//纬度
    private String longitude;//2;//经度
    private String altitude;//3;//海拔
    private String time;//4;//时间

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
