package com.baofeng.mj.business.brbusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by dupengwei on 2017/3/6.
 */

public class AudioReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogHelper.e("infosss","action=="+intent);
        if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            if (UnityActivity.INSTANCE != null) {
                IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                if (iAndroidCallback != null) {//通知Unity
                    iAndroidCallback.sendAudioBecoming();
                }
            }
        }
    }
}
