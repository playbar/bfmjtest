package com.baofeng.mj.util.publicutil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.threadutil.ThreadPoolUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 应用程序工具类
 *
 * Created by muyu on 2016/4/7.
 */
public class ApplicationUtil {
    private static ActivityManager activityManager;
    private static Field processStateField;

    private static ActivityManager getActivityManager(){
        if(activityManager == null){
            activityManager = (ActivityManager) BaseApplication.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return activityManager;
    }

    private static Field getProcessStateField(){
        if(processStateField == null){
            try {
                processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return processStateField;
    }

    /**
     * 魔镜app是否在前台
     * @return true前台，false后台
     */
    public static boolean mojingAppInForeground() {
        String packageName = getPackageNameForCurRunningApp();//获取当前运行app的包名
        if (packageName.equals(BaseApplication.INSTANCE.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前运行app的包名
     * @return
     */
    public static String getPackageNameForCurRunningApp(){
        String packageName = "";
        if (Build.VERSION.SDK_INT < 21) {
            List<RunningTaskInfo> runningTaskInfoList = getActivityManager().getRunningTasks(1);
            if (runningTaskInfoList != null && runningTaskInfoList.size() > 0) {
                RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
                packageName = runningTaskInfo.topActivity.getPackageName();
            }
        } else {
            try {
                List<ActivityManager.RunningAppProcessInfo> appList = getActivityManager().getRunningAppProcesses();
                if(appList != null && appList.size() > 0){
                    final int PROCESS_STATE_TOP = 2;
                    for (ActivityManager.RunningAppProcessInfo app : appList) {
                        if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                                && app.importanceReasonCode == ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN) {
                            if (PROCESS_STATE_TOP == getProcessStateField().getInt(app)) {
                                packageName = app.processName;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return packageName;
    }

    /**
     * 根据进程id获取进程名称
     */
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
        if (runningAppList != null) {
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo procInfo : runningAppList) {
                if (procInfo.pid == myPid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * 退出程序
     */
    public static void exitApp() {
        //SpeechRecognize.getInstance().onDestroy();// 退出整个应用
        BaseApplication.INSTANCE.setEnableJoystickReport(true);
        //BaseApplication.INSTANCE.unregisterReceiver();//取消广播
        //BaseApplication.INSTANCE.unbindDownloadService();//解绑下载服务
        BaseApplication.INSTANCE.exitAllActivity();
        ThreadPoolUtil.clear();//清理线程池
        //BaseApplication.getInstance().stopService(new Intent(BaseApplication.getInstance(), RunningTaskMonitor.class));
        //退出应用不清理通知（刘川驰）
//		NotificationManager notificationManager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationManager.cancelAll();
        //System.exit(0);
    }

    public static void restartApp() {
        BaseApplication context = BaseApplication.INSTANCE;
        Intent startIntent = context.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(context.getBaseContext().getPackageName());
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlarmManager alarmManager = (AlarmManager) BaseApplication.INSTANCE.getSystemService(Service.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                PendingIntent.getActivity(BaseApplication.INSTANCE, (int) System.currentTimeMillis(),
                        startIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        System.exit(0);
    }
}
