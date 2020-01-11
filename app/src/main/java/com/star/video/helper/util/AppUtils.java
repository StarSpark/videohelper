package com.star.video.helper.util;

import android.content.pm.PackageManager;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.video.helper.MyApplication;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * <p>
 * 作者：Jensing Email: wangzhixing44@gmail.com
 * 创建时间：2018/4/2 下午5:22
 * 描述：
 * </p>
 **/
public class AppUtils {
    public static final String QQPkgName="com.tencent.mobileqq";
    private static final String WeiChatPkgName="";

    public static boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            MyApplication.getApplication().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 防止内存泄漏移除相关资源（ViewGroup背景,ImageView图片,EditText监听）
     * @param root
     */
    public static void traverse(ViewGroup root) {
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                child.setBackground(null);
                traverse((ViewGroup) child);
            } else {
                if (child != null) {
                    child.setBackground(null);
                }
                if (child instanceof ImageView) {
                    ((ImageView) child).setImageDrawable(null);
                } else if (child instanceof EditText) {
                    cleanWatchers((EditText) child);
                }
            }
        }
    }
    public static void cleanWatchers(TextView editText){
        try {
            Class<TextView> clazz = TextView.class;
            Field mLoadedApkField = clazz.getDeclaredField("mListeners");
            mLoadedApkField.setAccessible(true);
//            LogUtil.e("mListeners====>"+mLoadedApkField.getType().getName());
            Object mListeners = mLoadedApkField.get(editText);
            if (mListeners instanceof List) {
                List<TextWatcher> list= (List<TextWatcher>) mListeners;
                for (int i = 0; i < list.size(); i++) {
                    editText.removeTextChangedListener(list.get(i));
//                    LogUtil.e("editText.removeTextChangedListener执行====>"+i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
