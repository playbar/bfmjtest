package com.bfmj.sdk.util;

import android.app.Activity;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.bfmj.sdk.common.App;

/**
 * ClassName: ScreenBrightnessUtils <br/>
 * @author wanghongfang
 * @date: 2015-1-19 下午5:04:17 <br/>
 *        description:系统屏幕亮度 管理设置类
 */
public class ScreenBrightnessUtils {

	public static int oldModel = getScreenMode();
	public static float oldBrightness = getScreenBrightness();

	/**
	 * @author wanghongfang @Date 2015-1-19 下午5:04:31
	 *         description: 保存当前系统的屏幕亮度 （用户退出横屏时将恢复保存的该亮度）
	 * @param {引入参数名  {引入参数说明}
	 * @return {返回值说明}
	 */
	public static void initModel(Activity activity) {
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
				App.getInstance());
		oldModel = getScreenMode();
		oldBrightness = getScreenBrightness();
		sharedPreferencesUtil.setInt("screenModel", oldModel);
		sharedPreferencesUtil.setFloat("screenBrightness", oldBrightness);
		setModel(activity, false);
		// activity.
	}

	/**
	 * 设置系统屏幕亮度
	 * @author wanghongfang @Date 2015-1-19 下午5:05:04
	 *         description:进入横屏修改 时改为手动模式 并设置亮度为三分之一总亮度
	 * @param activity
	 * @param ishandle true:手动模式 false:自动亮度 模式
	 * @return {返回值说明}
	 */
	public static void setModel(Activity activity, boolean ishandle) {
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
				App.getInstance());
		oldModel = sharedPreferencesUtil.getInt("screenModel", oldModel);
		if (ishandle) {
			if (DefaultSharedPreferenceManager.getInstance(activity).getLightModel() == 1) {// 本地自动亮度调节关
				int pagevalue = DefaultSharedPreferenceManager.getInstance(activity)
						.getScencePageLightValue();
				setScreenBrightness(activity, pagevalue / 255.0f);
			} else {
				// setScreenMode(0);
				if (oldModel == 1) {// 如果之前是自动，进入横屏后设置亮度为30%，否则按用户同用户设置一样
					setScreenBrightness(activity, 0.3f);
				}
			}
		} else {
			setScreenMode(oldModel);
		}
	}

	/**
	 * @author wanghongfang @Date 2015-1-19 下午5:06:06
	 *         description:获得当前屏幕亮度的模式
	 *         SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 *         SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 * @param {引入参数名  {引入参数说明}
	 * @return {返回值说明}
	 */
	public static int getScreenMode() {
		int screenMode = 0;
		try {
			screenMode = Settings.System.getInt(App.getInstance()
					.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {

		}
		return screenMode;
	}

	/**
	 * 设置当前屏幕亮度的模式
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 * @param paramInt 亮度值
	 * @author wanghongfang @Date 2015-1-19 下午5:06:06
	 */
	public static void setScreenMode(int paramInt) {
		try {
			Settings.System.putInt(App.getInstance().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * @author wanghongfang @Date 2015-1-19 下午5:07:46
	 *         description: 获得当前屏幕亮度值 0--255
	 * @param {引入参数名  {引入参数说明}
	 * @return {返回值说明}
	 */
	public static int getScreenBrightness() {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(App.getInstance()
					.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {

		}
		return screenBrightness;
	}

	/**
	 * @author wanghongfang @Date 2015-1-19 下午5:14:38
	 *         description:设置当前屏幕亮度值 0--255
	 * @param paramInt 亮度值
	 * @return {返回值说明}
	 */
	public static void saveScreenBrightness(int paramInt) {
		try {
			Settings.System.putInt(App.getInstance().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * @author wanghongfang @Date 2015-1-19 下午5:15:09
	 *         description:保存当前的屏幕亮度值，并使之生效
	 * @param paramInt 亮度值
	 * @return {返回值说明}
	 */
	public static void setScreenBrightness(Activity context, float paramInt) {
		Window localWindow = ((Activity) context).getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow
				.getAttributes();
		// float f = paramInt / 255.0F;
		localLayoutParams.screenBrightness = paramInt;
		localWindow.setAttributes(localLayoutParams);
	}

}
