package com.star.video.helper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;

/**
 *
 *
 * <p>
 * 作者：Jensing Email: wangzhixing44@gmail.com
 * 创建时间：2019-10-09 16:10
 * 描述：
 * </p>
 **/
public class Helper {

    public static String pkName="pkName";
    public static String startApp="startApp";
    public static String lclassName="lclassName";
    public static String MY_BROADCAST="net.deniro.android.MY_BROADCAST.action";
    private static Helper mHelper = null;
    private static AccessibilityService mService = null;

    public static synchronized Helper getInstance(AccessibilityService service) {
        if (null == mHelper) {
            mHelper = new Helper(service);
        }
        return mHelper;
    }

    private Helper(AccessibilityService service) {
        mService = service;
    }


    public List<AccessibilityNodeInfo> findViewsByContainsText(String containsText) {
        AccessibilityNodeInfo info = mService.getRootInActiveWindow();
        if (info == null) return null;
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByText(containsText);
        info.recycle();
        return list;
    }

    public List<AccessibilityNodeInfo> findViewsByEqualsText(@NonNull String equalsText) {
        List<AccessibilityNodeInfo> listOld = findViewsByContainsText(equalsText);
        if (isEmptyArray(listOld)) {
            return null;
        }
        ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
        for (AccessibilityNodeInfo ani : listOld) {
            if (ani.getText() != null && equalsText.equals(ani.getText().toString())) {
                listNew.add(ani);
            } else {
                ani.recycle();
            }
        }
        return listNew;
    }

    /**
     * @param idFullName id全称:com.android.xxx:id/tv_main
     */
    public AccessibilityNodeInfo findViewById(String idFullName) {
        List<AccessibilityNodeInfo> list = findViewsById(idFullName);
        return isEmptyArray(list) ? null : list.get(0);
    }

    /**
     * 类似listview这种一个页面重复的id很多
     *
     * @param idFullName id全称:com.android.xxx:id/tv_main
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewsById(String idFullName) {
        try {
            AccessibilityNodeInfo rootInfo = mService.getRootInActiveWindow();
            if (rootInfo == null) return null;
            List<AccessibilityNodeInfo> list = rootInfo.findAccessibilityNodeInfosByViewId(idFullName);
            rootInfo.recycle();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 只找第一个ClassName
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public AccessibilityNodeInfo findViewByFirstClassName(String className) {
        AccessibilityNodeInfo rootInfo = mService.getRootInActiveWindow();
        if (rootInfo == null) return null;
        AccessibilityNodeInfo info = findViewByFirstClassName(rootInfo, className);
        rootInfo.recycle();
        return info;
    }

    /**
     * 只找第一个ClassName
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public AccessibilityNodeInfo findViewByFirstClassName(AccessibilityNodeInfo parent, String className) {
        if (parent == null) return null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            if (className.equals(child.getClassName().toString())) {
                return child;
            }
            AccessibilityNodeInfo childChild = findViewByFirstClassName(child, className);
            child.recycle();
            if (childChild != null) {
                return childChild;
            }
        }
        return null;
    }

    /**
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public List<AccessibilityNodeInfo> findViewByClassName(String className) {
        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = mService.getRootInActiveWindow();
        if (rootInfo == null) return list;
        findViewByClassName(list, rootInfo, className);
        rootInfo.recycle();
        return list;
    }

    /**
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public void findViewByClassName(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, String className) {
        if (parent == null) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            if (className.equals(child.getClassName().toString())) {
                list.add(child);
            } else {
                findViewByClassName(list, child, className);
                child.recycle();
            }
        }
    }


    private boolean isEmptyArray(List list) {
        return list == null || list.size() == 0;
    }

    public void setText(AccessibilityNodeInfo textInfo, String text) {
        if (textInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
    }

    /**
     * @param x         X
     * @param y         Y
     * @param startTime 开始时间
     * @param duration  手势总时长
     * @param callback  回调函数
     * @param handler   回调的线程(null表示主线程)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void click(float x, float y, long startTime, long duration, @Nullable AccessibilityService.GestureResultCallback callback, @Nullable Handler handler) {
        //发送一个点击事件
        Path mPath = new Path();//线性的path代表手势路径,点代表按下,封闭的没用
        mPath.moveTo(x, y);
        GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(mPath, startTime, duration);
        GestureDescription gestureDescription = new GestureDescription.Builder().addStroke(strokeDescription).build();
        mService.dispatchGesture(gestureDescription, callback, handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void move(Point startPoint, Point endPoint, long startTime, long duration, @Nullable AccessibilityService.GestureResultCallback callback, @Nullable Handler handler) {
        Path path = new Path();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, startTime, duration);
        //先横滑
        mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), callback, handler);
    }


    public void pasteText(AccessibilityNodeInfo nodeInfo, String text) {
        ClipboardManager manager = (ClipboardManager) mService.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            manager.setPrimaryClip(ClipData.newPlainText("复制", text));
            nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);//粘贴
            nodeInfo.recycle();//尽量在最后都回收掉
        }
    }

    /**
     * 点击该控件
     */
    public boolean clickView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            } else {
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                if (parent != null) {
                    boolean b = clickView(parent);
                    parent.recycle();
                    if (b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        //通知栏,打开红包
        switch (event.getEventType()) {//先判断是否是通知栏红包和转圈圈界面,这两个任何状态都会去点击
            //第一步：监听通知栏消息,拦截通知的红包
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                for (CharSequence text : event.getText()) {
                    String content = text.toString();
                    //收到红包提醒
                    if (content.contains("[微信红包]") || content.contains("[QQ红包]")) {
                        //模拟打开通知栏消息,打开后会有新的广播进入微信或者qq
                        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                           /* HongBaoService.pingUnLock();//开屏,打开屏幕
                            final PendingIntent contentIntent = ((Notification) event.getParcelableData()).contentIntent;
                            //延时的handler(因为开屏有动画)
                            TimeUtil.mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        contentIntent.send();
                                    } catch (PendingIntent.CanceledException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);*/
                        }
                        break;
                    }
                }
                break;
        }
    }
}

