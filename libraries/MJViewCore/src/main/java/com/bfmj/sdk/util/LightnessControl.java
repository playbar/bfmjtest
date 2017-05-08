package com.bfmj.sdk.util;


import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings.System;
import android.view.WindowManager;

/**
 * 
 * ClassName: LightnessControl <br/>
 * @author lixianke    
 * @date: 2015-1-21 下午4:10:50 <br/>  
 * description: 亮度控制类
 */
public class LightnessControl {

	/**
	 * 
	 * @author lixianke  @Date 2015-1-21 下午4:11:10
	 * description: 判断是否开启自适应亮度
	 * @param act 当前的Activity
	 * @return 是否开启自适应亮度
	 */
	public static boolean isAutoBrightness(Activity act) {
		boolean automicBrightness = false; 
		ContentResolver aContentResolver = act.getContentResolver();
		try { 
			automicBrightness = System.getInt(aContentResolver,
			System.SCREEN_BRIGHTNESS_MODE) == System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (Exception e) { 
			Logger.d("aaaaaaaa", "无法获取亮度");
		} 
		return automicBrightness; 
	}
	
	/**
	 * 
	 * @author lixianke  @Date 2015-1-21 下午4:11:58
	 * description: 设置亮度
	 * @param act 当前的 Activity
	 * @param value 亮度值
	 * @return 无
	 */
	public static void SetLightness(Activity act, int value, boolean isPlayPage)
	{   
		if (value < 0){
			value = 0;
		} else if (value > 255){
			value = 255;
		}
		
		try {
			if(DefaultSharedPreferenceManager.getInstance(act).getLightModel()==1){
				if (isPlayPage){
					DefaultSharedPreferenceManager.getInstance(act).setPlayPageLightValue(value);
				}
			}
//			System.putInt(act.getContentResolver(),System.SCREEN_BRIGHTNESS,value); 
			WindowManager.LayoutParams lp = act.getWindow().getAttributes(); 
			lp.screenBrightness = (value<=0?1:value) / 255f;
			act.getWindow().setAttributes(lp);
		} catch (Exception e) {
			Logger.d("aaaaaaaa", "无法改变亮度");
		}        
	}

	/**
	 * 
	 * @author lixianke  @Date 2015-1-21 下午4:13:20
	 * description: 获取当前亮度
	 * @param act 当前的 Activity
	 * @return 亮度值
	 */
	public static int GetLightness(Activity act)
	{
		return System.getInt(act.getContentResolver(),System.SCREEN_BRIGHTNESS,-1);
	}
 
	/**
	 * 
	 * @author lixianke  @Date 2015-1-21 下午4:14:03
	 * description: 关闭自适应亮度
	 * @param activity 当前的 Activity
	 * @return 无
	 */
	public static void stopAutoBrightness(Activity activity) { 
	    System.putInt(activity.getContentResolver(),
	    System.SCREEN_BRIGHTNESS_MODE,
	    System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}
	
	/**
	 * 
	 * @author lixianke  @Date 2015-1-21 下午4:14:03
	 * description: 开启自适应亮度
	 * @param activity 当前的 Activity
	 * @return 无
	 */
	public static void startAutoBrightness(Activity activity) { 
	    System.putInt(activity.getContentResolver(),
	    System.SCREEN_BRIGHTNESS_MODE,
	    System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	} 
}
