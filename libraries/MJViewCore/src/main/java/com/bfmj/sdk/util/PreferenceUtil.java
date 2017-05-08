/**
 * 
 */
package com.bfmj.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author xiongwei
 *
 */
public class PreferenceUtil {
	private static final String TAG = "PrefenceUtil";
	public static final String DEFAULT_NAME="hasPress";
	
	/**
	 * 更新设置
	 * @param prefKey
	 * @param newValue
	 */
	public static void updateSetting(SharedPreferences sp, String prefKey, Object newValue) {
		Log.i(TAG, "update " + prefKey + "=" + newValue);
		Editor editor = sp.edit();
		if(newValue == null) {
			editor.remove(prefKey);
		} else {
		
			if (newValue.getClass() == Boolean.class) {
				editor.putBoolean(prefKey, (Boolean) newValue);
			} else if(newValue.getClass() == Float.class) {
				editor.putFloat(prefKey, (Float) newValue);
			} else if(newValue.getClass() == Integer.class) {
				editor.putInt(prefKey, (Integer) newValue);
			} else if(newValue.getClass() == Long.class) {
				editor.putLong(prefKey, (Long) newValue);
			} else{
				editor.putString(prefKey, newValue.toString());
			}
		}
			
		editor.commit();
	}
	
	public static void updateSetting(Context ctx, String prefKey, Object newValue) {
		updateSetting(PreferenceManager.getDefaultSharedPreferences(ctx), prefKey, newValue);
	}
	
	public static String getShared(Context ctx, String prefKey, String defValue) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(prefKey, defValue);
	}
	
	public static boolean getShared(Context ctx, String prefKey, boolean defValue) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(prefKey, defValue);
	}
	
	public static float getShared(Context ctx, String prefKey, float defValue) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(prefKey, defValue);
	}
	
	public static long getShared(Context ctx, String prefKey, long defValue) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getLong(prefKey, defValue);
	}
	
	/**
	 * 创建私有sharedPreference文件
	 * @param ctx
	 * @param preferenceName
	 * @param mode
	 * @return SharedPreferences
	 */
	public static SharedPreferences getPressSharedPreferences(Context ctx, String preferenceName, int mode) {
		return ctx. getSharedPreferences(preferenceName, mode);
	}
	
	
}
