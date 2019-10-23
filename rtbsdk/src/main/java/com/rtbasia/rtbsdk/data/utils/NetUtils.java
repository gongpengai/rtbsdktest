package com.rtbasia.rtbsdk.data.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.rtbasia.rtbsdk.RTBEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Juan.gong 2019-10-16
 */
public class NetUtils {

    private static final String NETWORKTYPE_INVALID = "Unkown network";// 没有网络
    private static final String NETWORKTYPE_WAP = "Wap"; // wap网络
    public static final String NETWORKTYPE_WIFI = "Wifi"; // wifi网络

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static Boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) RTBEngine.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }


    /**
     * 判断是否为WIFI；
     *
     * @param context
     * @return true为wifi
     */
    public static boolean netIsWifi(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                @SuppressLint("MissingPermission")
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null
                        && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取连接的网络类型
     *
     * @return String
     */

    public static String getNetWorkType() {
        String mNetWorkType = null;
        ConnectivityManager manager = (ConnectivityManager) RTBEngine.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return "ConnectivityManager not found";
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                if (TextUtils.isEmpty(proxyHost)) {
                    mNetWorkType = mobileNetworkType();
                } else {
                    mNetWorkType = NETWORKTYPE_WAP;
                }
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }


    /**
     * 判断手机连接的移动网络类型
     *
     * @return String
     */
    private static String mobileNetworkType() {
        TelephonyManager manager = (TelephonyManager) RTBEngine.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (manager == null) {
            return "TelephonyManager is null";
        }
        switch (manager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:// ~ 50-100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_CDMA:// ~ 14-64 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_EDGE:// ~ 50-100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_GPRS:// ~ 100 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_HSDPA:// ~ 2-14 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSPA:// ~ 700-1700 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_UMTS:// ~ 400-7000 kbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EHRPD:// ~ 1-2 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_HSPAP:// ~ 10-20 Mbps
                return "3G";
            case TelephonyManager.NETWORK_TYPE_IDEN:// ~25 kbps
                return "2G";
            case TelephonyManager.NETWORK_TYPE_LTE:// ~ 10+ Mbps
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN";
            default:
                return "4G";
        }
    }

    /**
     * 获取手机搜索到的Wi-Fi信息列表
     *
     * @return List<ScanResult> 搜索到的wifi信息
     */
    public static List<ScanResult> getWifiInfos() {
        WifiManager wifiManager = (WifiManager) RTBEngine.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
//        CustomWifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //搜索到的wifi列表信息
            List<ScanResult> scanResults = wifiManager.getScanResults();
            return scanResults;
        } else {
            return new ArrayList<>();
        }
    }


}
