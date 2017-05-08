package com.baofeng.mj.vrplayer.utils;

import android.content.Context;
import android.media.AudioManager;

import com.baofeng.mj.business.publicbusiness.BaseApplication;

/**
 * Created by wanghongfang on 2016/7/22.
 */
public class SoundUtils {
    /**
     * 设置声音百分比
     * @param level
     */
    public static void SetSoundVolume(int level){
        AudioManager am = (AudioManager) BaseApplication.getInstance()
                .getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
       int  current =(int)( max*(level/100f));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }

    /**
     * 静音或关闭静音
     * @param ismute  true:静音  false:关闭静音
     */
    public static void SetVolumeMute(boolean ismute){
        AudioManager am = (AudioManager) BaseApplication.getInstance()
                .getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_MUSIC,ismute);
    }

    public static boolean isVolumeMute(){
        int volume = GetSystemCurVolume();
        return volume==0;
    }
    public static int GetCurrentVolumePercent(){
        AudioManager am = (AudioManager) BaseApplication.getInstance()
                .getSystemService(Context.AUDIO_SERVICE);
        int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int c = (int)((current/((float)max))*100);
        return c;
    }

    public static int GetSystemCurVolume(){
        AudioManager am = (AudioManager) BaseApplication.getInstance()
                .getSystemService(Context.AUDIO_SERVICE);
        int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        return current;
    }

    public static void SetSystemVolume(int value){
        AudioManager am = (AudioManager) BaseApplication.getInstance()
                .getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
    }

}
