package com.rtbasia.rtbsdk.data.utils;

/**
 * Create by Juan.gong 2019-10-16
 */
public class StringUtil {

    public static boolean isNotNull(String str){
        if (str == null || "".equals(str)){
            return false;
        }else {
            return true;
        }
    }
}
