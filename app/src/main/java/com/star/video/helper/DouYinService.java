package com.star.video.helper;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;

import com.star.video.helper.util.AppUtils;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 *
 *
 * <p>
 * 作者：Jensing Email: wangzhixing44@gmail.com
 * 创建时间：2019-10-09 12:00
 * 描述：
 * </p>
 **/
@RequiresApi(api = Build.VERSION_CODES.N)
public class DouYinService extends AccessibilityService {
    private static final int MSG_A = 1000;
    private static final int MSG_C = 1001;
    public static DouYinService mService;
    private Helper mHelper;
    private Random mRandom;
    private int mScreenHeight;
    private int mScreenWidth;
    private String pkName="com.ss.android.ugc.aweme.lite";
    //private Point mSendPoint = new Point(1007, 1709);
    private Point mSendPoint = new Point(990, 1880);

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
        mHelper = Helper.getInstance(this);
        mRandom = new Random();
        DisplayMetrics displayMetrics = MyApplication.getApplication().getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
    }

    private boolean mAccept = true;
    private boolean mFirst = true;
    private int mCountdown = 35;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_A:
                    mAccept = true;
                    break;
                case MSG_C:
                    mCountdown -= 5;
                    if (mCountdown < 0) {
                        mAccept = true;
                        mCountdown = 35;
                        praise();
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_C,5000);
                    break;
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (mFirst) {
            mFirst = false;
            performGlobalAction(GLOBAL_ACTION_BACK);
            performGlobalAction(GLOBAL_ACTION_BACK);
            if (AppUtils.checkApkExist(pkName)) {
                startApp(pkName);
            }
            mHandler.sendEmptyMessage(MSG_C);
            runOnUiThread(2500, new Runnable() {
                @Override
                public void run() {
                    mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/i2"));
                }
            });
            return;
        }
        if (mAccept&&(pkName.equals(accessibilityEvent.getPackageName()))) {
            mAccept = false;
            mHandler.removeMessages(MSG_A);
            //延时8s+(0~10s)
            mHandler.sendEmptyMessageDelayed(MSG_A, 8000+mRandom.nextInt(3)*1000);
            mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/imgClose"));
            runOnUiThread(800, new Runnable() {
                @Override
                public void run() {
                    swipe();
                    runOnUiThread(1000, new Runnable() {
                        @Override
                        public void run() {
                            int random = mRandom.nextInt(10);
                            if (random == 2) {
                                writeComment();
                            } else if (random == 8) {
                                praise();
                            }
                        }
                    });
                }
            });

        }
    }

    private void praise() {
        mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/a4p"));
    }


    private void startApp(String pkgName) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void swipe() {
        int random = mRandom.nextInt(30);
        mHelper.move(new Point(mScreenWidth / 2 + random, 4 * mScreenHeight / 5 + random), new Point(mScreenWidth / 2 + random, mScreenHeight / 4 + random), 0, 600 + random, null, null);
    }

    private void writeComment() {
        mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/q1"));
        runOnUiThread(600, new Runnable() {
            @Override
            public void run() {
                DouYinService.this.performGlobalAction(GLOBAL_ACTION_BACK);
//                List<AccessibilityNodeInfo> infos = mHelper.findViewsById("com.ss.android.ugc.aweme.lite:id/tv_user_comment");
//                String comment = "";
//                if (infos != null && infos.size() > 1) {
//                    AccessibilityNodeInfo info = infos.get(mRandom.nextInt(infos.size() - 1));
//                    comment = info.getText().toString();
//                }
//                if (!TextUtils.isEmpty(comment)) {
//                    mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/comment"));
//                    mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/editComment"));
//                    mHelper.setText(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/et_comment"), comment);
//                    mHelper.click(mSendPoint.x, mSendPoint.y, 1000, 100, new GestureResultCallback() {
//                        @Override
//                        public void onCompleted(GestureDescription gestureDescription) {
//                            runOnUiThread(1000, new Runnable() {
//                                @Override
//                                public void run() {
//                                    mHelper.clickView(mHelper.findViewById("com.ss.android.ugc.aweme.lite:id/imgClose"));
//                                }
//                            });
//                        }
//                    }, null);
//                }else {
//                    ShuaBaoService.this.performGlobalAction(GLOBAL_ACTION_BACK);
//                }
            }
        });


    }


    @Override
    public void onInterrupt() {
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;

    }

    public static boolean isStart() {
        return mService != null;
    }

    public static void runOnUiThread(long delayMillis, Runnable r) {
        MyApplication.getHandle().postDelayed(r, delayMillis);
    }
}
