package com.rtbasia.rtbsdk;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.rtbasia.rtbsdk.data.DeviceCollection;
import com.rtbasia.rtbsdk.data.InfoOuterClass;
import com.rtbasia.rtbsdk.data.LocationCollection;
import com.rtbasia.rtbsdk.data.WifiCollection;
import com.rtbasia.rtbsdk.data.entity.Config;
import com.rtbasia.rtbsdk.data.entity.WifiEntity;
import com.rtbasia.rtbsdk.data.utils.IOUtil;
import com.rtbasia.rtbsdk.data.utils.RequestCallback;
import com.rtbasia.rtbsdk.data.utils.RequestUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Create by Juan.gong 2019-10-16
 * 初始化入口
 */
public class RTBEngine {

    public static final String TAG = "rtbasia";
    private static Context context;
    private DeviceCollection deviceCollection;
    private WifiCollection wifiCollection;
    private static Builder builder;

    public static Context getContext() {
        return context;
    }

    protected void setContext(Context context) {
        RTBEngine.context = context;
    }

    public void onResume(){
        RequestUtils.getDataFromNetGet(Config.API_IPV4, new RequestCallback() {
            @Override
            public void onRequestCallback(String s) {
                requestIPv6(s);
            }
        });
    }


    private void requestIPv6(final String ipv4){
        RequestUtils.getDataFromNetGet(Config.API_IPV6, new RequestCallback() {
            @Override
            public void onRequestCallback(String s) {
                startPost(ipv4,s);
            }
        });
    }

    private void startPost(String ipv4,String ipv6) {
        final InfoOuterClass.Info.Builder info = InfoOuterClass.Info.newBuilder();

        /**
         * 设备信息
         */
        InfoOuterClass.DeviceInfo.Builder device = InfoOuterClass.DeviceInfo.newBuilder();
        if (deviceCollection == null) {
            Log.e(TAG, "请在onCreate里面调用 RTBEngine.Builder.build().with");
            return;
        }
        device.setAndroidId(device.getAndroidId());
        device.setAppName(deviceCollection.getAppName());
        device.setBrand(deviceCollection.getBrand());
        device.setDevice(deviceCollection.getDevice());
        device.setDeviceId(deviceCollection.getDeviceId());
        device.setModel(deviceCollection.getModel());
        device.setOsVersion(deviceCollection.getOSVersion());
        device.setProduct(deviceCollection.getProduct());
        device.setApplicationid(deviceCollection.getPackageName());
        info.setDevice(device);
        /**
         * wifi
         */
        if (wifiCollection == null){
            Log.e(TAG, "请在onCreate里面调用 RTBEngine.Builder.build().with");
            return;
        }
        WifiEntity wifiEntity = wifiCollection.getConnectWifiInfo();
        if (wifiEntity != null){

            info.setMySSID(wifiEntity.getSSID());
            info.setMyBSSID(wifiEntity.getBSSID());
            info.setMyLevel(wifiEntity.getLevel());
            if ("".equals(ipv4) || null == ipv4 || "null".equals(ipv4)){
                ipv4 = wifiEntity.getIp();
            }
        }


        List<WifiEntity> wifiList = wifiCollection.getWifiInfoList();
        if (wifiList != null && wifiList.size() > 0){
            for (WifiEntity wifiInfoBean : wifiList) {
                InfoOuterClass.WifiInfo.Builder wifi = InfoOuterClass.WifiInfo.newBuilder();
                wifi.setSSID(wifiInfoBean.getSSID());
                wifi.setBSSID(wifiInfoBean.getBSSID());
                wifi.setLevel(wifiInfoBean.getLevel());
                info.addWifiList(wifi);
            }
        }
        /**
         * 地点
         */

        Location sysLocation = LocationCollection.getInstance(context).getLocation();

        if (null != sysLocation) {
            InfoOuterClass.LocationInfo.Builder location = InfoOuterClass.LocationInfo.newBuilder();
            location.setLatitude(String.valueOf(sysLocation.getLatitude()));
            location.setLongitude(String.valueOf(sysLocation.getLongitude()));
            location.setAltitude(String.valueOf(sysLocation.getAltitude()));
            location.setTime(String.valueOf(sysLocation.getTime()));
            info.setLocation(location);
        }

        info.setIpV4(ipv4);
        info.setIpV6(ipv6);

        InfoOuterClass.Info sdkInfo = info.build();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            sdkInfo.writeTo(buffer);
            Log.i(TAG,"prepare to send data:"+sdkInfo.toString());
            RequestUtils.getDataFromNetPost(Config.API_POST,buffer.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IOUtil.closeGracefully(buffer);
        }



    }

    public static Builder builder(){
        if (builder == null){
            synchronized (Builder.class){
                builder = new Builder();
            }
        }
        return builder;
    }


    public static class Builder {

        public static RTBEngine engine;

        public Builder init() {
            synchronized (RTBEngine.class) {
                if (engine == null) {
                    engine = new RTBEngine();
                }
            }
            Log.i(TAG, "初始化成功");
            return this;
        }

        public Builder with(Context context) {
            engine.setContext(context);
            engine.deviceCollection = new DeviceCollection(context);
            engine.wifiCollection = new WifiCollection(context);
            return this;
        }

        public Builder resume(){
            engine.onResume();
            return this;
        }




    }
}
