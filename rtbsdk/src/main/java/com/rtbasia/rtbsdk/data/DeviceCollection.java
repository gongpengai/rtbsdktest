package com.rtbasia.rtbsdk.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rtbasia.rtbsdk.RTBEngine;
import com.rtbasia.rtbsdk.data.utils.PermissionUtils;

import org.json.JSONObject;

import static android.provider.Settings.Secure;

/**
 * Create by Juan.gong 2019-10-16
 */
public class DeviceCollection {

    private Context context;

    public DeviceCollection(Context context) {
        this.context = context;
    }


    public String initDeviceJson() {
        String deviceid = getDeviceId();
        String android_id = getAndroid();
        String model = getModel();
        String brand = getBrand();
        String product = getProduct();
        String device = getDevice();
        String app_name = getAppName();
        String os_version = getOSVersion();
        String applicationid = getPackageName();
        JSONObject object = new JSONObject();
        try {
            object.put("deviceid", deviceid);
            object.put("android_id", android_id);
            object.put("model", model);
            object.put("brand", brand);
            object.put("product", product);
            object.put("device", device);
            object.put("app_name", app_name);
            object.put("os_version", os_version);
            object.put("applicationid", applicationid);
            return object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 获取手机型号
     *
     * @return
     */
    public String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机系统定制商
     *
     * @return
     */
    public String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机制造商
     *
     * @return
     */
    public String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取设备参数
     *
     * @return
     */
    public String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取当前应用的名字
     *
     * @return
     */
    public String getAppName() {
        try {
            PackageInfo pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String appName = pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
            return appName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得系统版本
     *
     * @return
     */
    public String getOSVersion() {
        try {
            return Build.VERSION.RELEASE;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得应用的包名
     *
     * @return
     */
    public String getPackageName() {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID.
     * Return null if device ID is not available.
     */
    public String getDeviceId() {
        try {
            if (PermissionUtils.checkPermission(PermissionUtils.READ_PHONE_STATE)) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (null == telephonyManager) {
                    Log.i(RTBEngine.TAG,"TelephonyManager is null");
                    return "";
                }
                return telephonyManager.getDeviceId();
            } else {
                return "unauthorized";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }

    }

    public String getAndroid() {
        try {
            final String androidId;
            androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
