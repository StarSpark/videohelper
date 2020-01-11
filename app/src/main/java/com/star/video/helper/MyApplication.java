package com.star.video.helper;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 *
 *
 * <p>
 * 作者：Jensing Email: wangzhixing44@gmail.com
 * 创建时间：2019-10-11 12:45
 * 描述：
 * </p>
 **/
public class MyApplication extends Application {
    private static Application mApplication;
    private static Handler mHandler;

    public static Application getApplication() {
        return mApplication;
    }

    //主线程的handler
    public static Handler getHandle() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
