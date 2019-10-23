package com.rtbasia.rtbsdk.data.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import com.rtbasia.rtbsdk.RTBEngine;

/**
 * Create by Juan.gong 2019-10-22
 */
public class PermissionUtils {

    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String WRITE_SECURE_SETTINGS = Manifest.permission.WRITE_SECURE_SETTINGS;

    public static boolean checkPermission(String premission) {
        // 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
        PackageManager pm = RTBEngine.getContext().getPackageManager();
        boolean hasPermission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(premission, RTBEngine.getContext().getPackageName()));
        return hasPermission;
    }

}
