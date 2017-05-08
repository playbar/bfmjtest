package com.baofeng.mj.util.systemutil;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.view.WindowManager;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.unity.UnityActivity;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by liuchuanchi on 2016/5/12.
 * 亮度工具类
 */
public class BrightnessUtil {
    /**
     * 设置用户亮度值
     * @param brightnessValue 亮度值 0 - 255
     */
    public static void setUserBrightnessValue(final Activity activity, int brightnessValue) {
        if (brightnessValue < 0){
            brightnessValue = 0;
        } else if (brightnessValue > 255){
            brightnessValue = 255;
        }
        if(UnityActivity.mIsYTJ){
            Settings.System.putInt(BaseApplication.INSTANCE.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
        }
        SettingSpBusiness.getInstance().setBrightnessValue(brightnessValue);
        final int tempBrightnessValue = brightnessValue;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.screenBrightness = tempBrightnessValue / 255f;
                activity.getWindow().setAttributes(lp);
                LogHelper.d("infos","==lp.screenBrightness=="+lp.screenBrightness+"===butes=="+activity.getWindow().getAttributes().screenBrightness);

            }
        });
    }
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }



    /**
     * 获取用户亮度值 0 - 255
     */
    public static int getUserBrightnessValue() {
        return SettingSpBusiness.getInstance().getBrightnessValue();
    }

    /**
     * 获取系统亮度值 0 - 255
     */
    public static int getSysBrightnessValue() {
        ContentResolver contentResolver = BaseApplication.INSTANCE.getContentResolver();
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
    }




    /**
     * 设置自适应亮度模式
     * @param isAutoBrightnessMode true打开，false关闭
     */
    public static void setAutoBrightnessMode(boolean isAutoBrightnessMode) {
        ContentResolver aContentResolver = BaseApplication.INSTANCE.getContentResolver();
        if(isAutoBrightnessMode){//打开
            Settings.System.putInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }else{//关闭
            Settings.System.putInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    /**
     * true是自适应亮度模式，false不是
     */
    public static boolean isAutoBrightnessMode() {
        try {
            ContentResolver aContentResolver = BaseApplication.INSTANCE.getContentResolver();
            int brightnessMode = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            return brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
        }
        return false;
    }
}
