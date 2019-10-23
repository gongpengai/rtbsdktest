package com.rtbasia.rtbsdk.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.rtbasia.rtbsdk.RTBEngine;
import com.rtbasia.rtbsdk.data.utils.PermissionUtils;

import java.util.List;


/**
 * LBS 定位相关数据的采集管理
 */
public class LocationCollection {

    /* 是否使用 位置变更监听器 */
    private static final boolean ENABLE_LOCATION_UPDATELISTENER = true;
    /* 位置更新最小时间间隔 单位毫秒 默认300s*/
    private static final long LOCATION_UPDATE_MINTIME = 300 * 1000l;
    /* 位置更新最小距离 单位m 默认100M*/
    private static final float LOCATION_UPDATE_MINDISTANCE = 0f;
    /* 位置更新时间间隔 单位毫秒 120s*/
    private static final long UPDATES_INTERVAL = 120 * 1000l;
    //private static final String TAG = "LocationUtils";
    /* 移除更新时间 单位毫秒 20s*/
    private static final long REMOVE_INTERVAL = 20 * 1000l;

    private static LocationCollection instance = null;
    private static Handler handler;
    private LocationManager locationManager;
    private Context context;
    private boolean synced;
    /* 上一次成功获取位置时的时间*/
    private long lastLocationTime = 0;
    private Location currentLocation;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //Log.i(TAG, "onLocationChanged:" + location + "    t:" + location.getTime());
            //位置发生变化
            lastLocationTime = System.currentTimeMillis();
            currentLocation = location;

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //Location Provider状态改变
            //Log.i(TAG, "onStatusChanged:" + s + " i:" + i);
        }

        @Override
        public void onProviderEnabled(String s) {
            //Provider可用
            //Log.i(TAG, "onProviderEnabled:" + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            //Provider不可用
            //Log.i(TAG, "onProviderDisabled:" + s);
        }
    };

    private final Runnable stopListener = new Runnable() {
        @Override
        public void run() {
            //Log.e(TAG, "remove Location Updates:" + lastLocationTime);
            locationManager.removeUpdates(locationListener);
            synced = false;
            handler.removeCallbacks(stopListener);
        }
    };

    private LocationCollection(Context context) {
        try {
            this.context = context;
            synced = false;
            currentLocation = null;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            handler = new Handler(Looper.getMainLooper());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static LocationCollection getInstance(Context context) {
        if (instance == null) {
            synchronized (LocationCollection.class) {
                if (instance == null) {
                    instance = new LocationCollection(context);
                }
            }
        }
        return instance;
    }

    public Location getLocation() {
        if (currentLocation == null) {
            //Log.w(TAG, "Location is empty,return null,start get syncLocation");
            syncLocation();
            return null;
        } else {

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastLocationTime) > UPDATES_INTERVAL) {
                //Log.i(TAG, "Location is expired,start update syncLocation");
                //更新位置
                syncLocation();
            }
            return currentLocation;//String.format("%fx%fx%f", latitude, longitude, accuracy);
        }

    }

    /**
     * 注册Location Update
     */
    public void syncLocation() {
        if (!PermissionUtils.checkPermission(PermissionUtils.ACCESS_COARSE_LOCATION)
                && !PermissionUtils.checkPermission(PermissionUtils.ACCESS_FINE_LOCATION)) {
            Log.i(RTBEngine.TAG, "need permisson ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION");
            synced = false;
            return;
        }

        try {
            //long start = System.currentTimeMillis();
            //获取所有可用的位置提供器
            List<String> providers = locationManager.getProviders(true);
            String locationProvider = null;
            Location location = null;

            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
                location = locationManager.getLastKnownLocation(locationProvider);
            }

            if (location == null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
                location = locationManager.getLastKnownLocation(locationProvider);
            }

            if (TextUtils.isEmpty(locationProvider)) {
                Log.i(RTBEngine.TAG, "no available Location Provider!");
                return;
            }

            //Log.d(TAG, "locationProvider:" + locationProvider + "  LastKnownLocation is :" + location);

            if (location != null) {
                //不为空,显示地理位置经纬度
                //long end = System.currentTimeMillis();
                //String cost = String.valueOf(end - start) + " ms";
                //Log.d(TAG, "cost:" + cost + " lat:" + location.getLatitude() + "  lon:" + location.getLongitude() + "  acc:" + location.getAccuracy() + "   time:" + location.getTime());
                currentLocation = location;
                lastLocationTime = System.currentTimeMillis();
            }

            if (ENABLE_LOCATION_UPDATELISTENER && !synced) {

                //Logger.e("request Location Updates:" + lastLocationTime + ",mintime:" + LOCATION_UPDATE_MINTIME + ",distance:" + LOCATION_UPDATE_MINDISTANCE);

                final String provider = locationProvider;

                handler.post(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(provider, LOCATION_UPDATE_MINTIME, LOCATION_UPDATE_MINDISTANCE, locationListener);
                    }
                });

                synced = true;
                //10ms 后停止更新Location
                handler.postDelayed(stopListener, REMOVE_INTERVAL);
            }
        } catch (Exception e) {
            synced = false;
        }

    }

    /**
     * 停止监听位置信息
     */
    public void stopSyncLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            handler.removeCallbacks(stopListener);
        }
    }

}
