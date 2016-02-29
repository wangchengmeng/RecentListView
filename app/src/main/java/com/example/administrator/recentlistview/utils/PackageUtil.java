package com.example.administrator.recentlistview.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.List;

/**
 * @author Zeng.hh
 * @version 王先佑 2012-12-06 增加getPackageName方法，返回应用程序的包名<br>
 *          2013-03-20 xu.xb <br>
 *          1.修改获取macAddress，若获取WifiInfo对象为空时，返回空字符串<br>
 *          2.增加获取渠道信息的方法<br>
 */
public class PackageUtil {

    private static final String TAG       = "PackageUtil";
    private static final String DEVICE_ID = "Unknow";





    /**
     * @return 获得手机型号
     */
    public static String getDeviceType() {
        return android.os.Build.MODEL;
    }


    /**
     * @return 获得操作系统版本号
     */

    public static String getSysVersion() {
        return android.os.Build.VERSION.RELEASE;
    }




    /**
     * 指定的activity所属的应用，是否是当前手机的顶级
     *
     * @param context activity界面或者application
     * @return 如果是，返回true；否则返回false
     */
    public static boolean isTopApplication(Context context) {
        if (context == null) {
            return false;
        }

        try {
            String packageName = context.getPackageName();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // 应用程序位于堆栈的顶层
                if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 什么都不做
        }
        return false;
    }

    /**
     * 判断指定进程是否已经打开
     *
     * @param context activity界面或者application
     * @param process 指定进程 ；
     * @return true表示已经打开 false表示没有打开
     */
    public static boolean isAppOpen(Context context, String process) {
        ActivityManager mManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> mRunningApp = mManager.getRunningAppProcesses();
        int size = mRunningApp.size();
        for (int i = 0; i < size; i++) {
            if (process.equals(mRunningApp.get(i).processName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检测是否安装对应的应用程序
     *
     * @param context     上下文对象
     * @param packageName 包名
     * @return 是否安装对应的应用程序
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (StringUtil.isNullOrEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取应用的VersionName
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 获取应用的VersionName
     */
    public static String getApkVersion(Context context, String packageName) {
        String version = "0.0.0";
        if (StringUtil.isNullOrEmpty(packageName)) {
            return version;
        }
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo.versionName;
            }
        }
        return version;
    }
}
