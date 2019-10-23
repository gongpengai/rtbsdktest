package com.rtbasia.rtbsdk.data.entity;

import java.io.Serializable;

/**
 * Create by Juan.gong 2019-10-16
 */
public class DeviceEntity implements Serializable {

    private String device_id;//1;
    private String android_id;//2;
    private String model;//3;//手机型号
    private String brand;//4;//手机系统定制商
    private String product;//5;//手机制造商
    private String device;//6;//设备参数
    private String app_name;//7;//当前应用的名字
    private String os_version;//8;//系统版本

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }
}
