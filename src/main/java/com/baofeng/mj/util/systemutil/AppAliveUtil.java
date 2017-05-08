package com.baofeng.mj.util.systemutil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * 判断应用是否已经启动
 */
public class AppAliveUtil {

	/**
	 * @param context
	 * @param packageName 要判断应用的包名
	 * @return boolean
	 */
	public static boolean isAppAlive(Context context, String packageName){
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
		for(int i = 0; i < processInfos.size(); i++){
			if(processInfos.get(i).processName.equals(packageName)){
				Log.i("NotificationLaunch", String.format("the %s is running, isAppAlive return true", packageName));
				return true;
			}
		}
		Log.i("NotificationLaunch", String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

	public static void startDetailActivity(Context context, String guid_activity, String next_url, int next_type, int subType){
		Intent intent = new Intent();
		intent.setClassName(context, guid_activity);
		intent.putExtra("next_url", next_url);
		intent.putExtra("next_type", next_type);
		intent.putExtra("subType", subType);
		context.startActivity(intent);
	}
}
