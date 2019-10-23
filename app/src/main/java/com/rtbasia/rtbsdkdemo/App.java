package com.rtbasia.rtbsdkdemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Create by Juan.gong 2019-10-22
 */
public class App extends Application {
    /**
     * 设置MultiDex分包方法，解决方法数超过65536问题
     *
     * @param base mContext
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
