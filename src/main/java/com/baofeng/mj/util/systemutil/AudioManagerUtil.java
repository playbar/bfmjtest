package com.baofeng.mj.util.systemutil;

import android.content.Context;
import android.media.AudioManager;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by liuchuanchi on 2016/5/12.
 * AudioManager工具类
 */
public class AudioManagerUtil {
    private static AudioManagerUtil instance;
    private AudioManager audioManager;
    private AudioManagerUtil(){
    }

    public static AudioManagerUtil getInstance(){
        if(instance == null){
            instance = new AudioManagerUtil();
        }
        return instance;
    }

    /**
     * 初始化音量管理者
     */
    private void initAudioManager(){
        if(audioManager == null){
            audioManager = (AudioManager) BaseApplication.INSTANCE.getSystemService(Context.AUDIO_SERVICE);
        }
    }

    /**
     * 获取最大音量（不返回系统最大音量：15，直接返回100）
     */
    public int getStreamMaxVolume(){
//        initAudioManager();
//        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
          return 100;
    }

    public int getSysCurrentVolume(){
       return audioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
    }

    /**
     * 获取当前音量（音量范围是 0 - 100，不是系统的音量范围 0 - 15）
     */
    public int getStreamCurrentVolume() {
        initAudioManager();
        int sysMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//系统最大音量
        int sysCurVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//系统当前音量
        int userCurVolume =  Math.round(sysCurVolume * 100 / sysMaxVolume);//用户当前音量

//        return userCurVolume > 100 ? 100 : userCurVolume;


        int lastVolume = SettingSpBusiness.getInstance().getCurrentVolume() / sysMaxVolume;
        int currentVolume = Math.round(SettingSpBusiness.getInstance().getCurrentVolume() / 100.0f);
        if(currentVolume != sysCurVolume){
            LogHelper.e("infos","currentVolume=="+currentVolume+"===sysCurVolume==="+sysCurVolume);
            lastVolume = userCurVolume > 100 ? 100 : userCurVolume;
        }else {
            if(sysCurVolume == 0){
                lastVolume = 0;
            }else {
                if(lastVolume == 0){
                    lastVolume = userCurVolume > 100 ? 100 : userCurVolume;
                }

            }
        }

        LogHelper.e("infos","===sysCurVolume=="+sysCurVolume+"==userCurVolume=="+userCurVolume+"==lastVolume=="+lastVolume);

        return lastVolume;

    }

    /**
     * 设置当前音量（音量范围是 0 - 100）
     */
    public void setStreamCurrentVolume(int userCurVolume) {
        if (userCurVolume < 0) {
            userCurVolume = 0;
        }
        if (userCurVolume > 100) {
            userCurVolume = 100;
        }

        initAudioManager();
        int sysMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//系统最大音量
        int sysCurVolume = Math.round(userCurVolume * sysMaxVolume / 100.0f);//系统当前音量
        SettingSpBusiness.getInstance().setCurrentVolume(userCurVolume * sysMaxVolume );
        if(sysCurVolume > sysMaxVolume){
            sysCurVolume = sysMaxVolume;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sysCurVolume, 0);
    }
}
