package com.rtbasia.rtbsdk.data.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Create by Juan.gong ${DATA}
 * 获取Wi-Fi信息
 */
public class WifiUtils {


    /**
     * 获取SSID
     *
     * @param activity 上下文
     * @return WIFI 的SSID
     */
    public static String getWIFISSID(Context activity) {
        if (activity == null) {
            return "";
        }
        String ssid = "unknown id";
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.O ||
                android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.P) {
            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.O_MR1) {
            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return "";
    }

    /**
     * 获取已连接的Wi-Fi信息
     * @param context
     * @return
     */
    public static WifiInfo getWifiInfo(Context context) {
        if (context == null) {
            return null;
        }
        AtomicReference<WifiInfo> wifiInfo = new AtomicReference<>();
        if (PermissionUtils.checkPermission(PermissionUtils.ACCESS_WIFI_STATE)) {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();
            wifiInfo.set(info);
            return wifiInfo.get();
        } else {
            return null;
        }


    }
}
