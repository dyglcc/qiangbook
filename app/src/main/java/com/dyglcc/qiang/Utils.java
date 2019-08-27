package com.dyglcc.qiang;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 工具类
 */
public class Utils {
    /**
     * Check whether an application is installed
     *
     * @param context     context
     * @param packageName the name of package
     */
    public static boolean isAppAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 獲取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//獲取所有已安裝程序的包信息
        if (pinfo != null) {
            int size = pinfo.size();
            for (int i = 0; i < size; i++) {
                String str = pinfo.get(i).packageName;
                if (str.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether service is running.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNeededPermissionsGranted(Context context) {
        boolean accessibilityGranted = isAccessibilitySettingsOn(context);

        if (!accessibilityGranted) { //判斷輔助功能是否打開  如果沒有打開 則跳轉至輔助功能界面
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
            return false;

        }
        return true;
    }

    private static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = Utils.getApp().getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("e", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        Application app = getApplicationByReflect();
        return app;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            Log.e("e", e.toString());
        }
        throw new NullPointerException("u should init first");
    }
}