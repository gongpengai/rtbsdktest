package com.rtbasia.rtbsdk.data;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.rtbasia.rtbsdk.data.entity.WifiEntity;
import com.rtbasia.rtbsdk.data.utils.LocationUtils;
import com.rtbasia.rtbsdk.data.utils.NetUtils;
import com.rtbasia.rtbsdk.data.utils.PermissionUtils;
import com.rtbasia.rtbsdk.data.utils.StringUtil;
import com.rtbasia.rtbsdk.data.utils.WifiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Juan.gong 2019-10-16
 */
public class WifiCollection {


    private Context context;

    public WifiCollection(Context context) {
        this.context = context;
    }

    public WifiEntity getConnectWifiInfo() {
        WifiEntity entity = null;
        try {
            if (NetUtils.isNetworkConnected()) {
                if (NetUtils.netIsWifi(context)) {

                    WifiInfo wifiInfo = WifiUtils.getWifiInfo(context);
                    if (wifiInfo != null) {
                        entity = new WifiEntity();
                        entity.setSSID(WifiUtils.getWIFISSID(context));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                                && LocationUtils.isGPSOpen(context)) {
                            entity.setSSID(WifiUtils.getWIFISSID(context));
                        }
                        entity.setIp(intToIp(wifiInfo.getIpAddress()));
                        entity.setBSSID(wifiInfo.getBSSID());
                        entity.setLevel(String.valueOf(wifiInfo.getRssi()));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;

    }

    /**
     * 获取相应的wifi信息
     *
     * @return
     */
    public List<WifiEntity> getWifiInfoList() {
        List<WifiEntity> list = new ArrayList<>();
        if (NetUtils.isNetworkConnected()) {
            String netType = NetUtils.getNetWorkType();
//            if (NETWORKTYPE_WIFI.equals(netType)) {//wifi连接
            //6.0适配，需要开启GPS才能获取wifi列表
            if (Build.VERSION.SDK_INT >= 23 && !LocationUtils.isGPSOpen(context)) {
                if (PermissionUtils.checkPermission(PermissionUtils.ACCESS_COARSE_LOCATION) && PermissionUtils.checkPermission(PermissionUtils.ACCESS_FINE_LOCATION)) {
                    Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 1);
                }
            }
            List<ScanResult> scanResults = NetUtils.getWifiInfos();
            if (scanResults != null && scanResults.size() > 0) {
                for (ScanResult sr : scanResults) {
                    if (StringUtil.isNotNull(sr.SSID)) {
                        WifiEntity customWifiInfo = new WifiEntity();
                        customWifiInfo.setSSID(sr.SSID);
                        customWifiInfo.setBSSID(sr.BSSID);
                        customWifiInfo.setLevel(sr.level + "");
                        list.add(customWifiInfo);
                    }
                }
            }
        }
        return list;
    }

    //将获取的int转为真正的ip地址,参考的网上的，修改了下
    private String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

}
