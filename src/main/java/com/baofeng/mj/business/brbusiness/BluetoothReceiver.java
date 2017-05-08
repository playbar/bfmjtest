package com.baofeng.mj.business.brbusiness;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;

/**
 * Created by hanyang on 2016/7/16.
 * 蓝牙耳机监听
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private IAndroidCallback iAndroidCallback;//Android与Unity交互，Android回调

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        TelephonyManager telephony =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://等待接听状态
                break;
            case TelephonyManager.CALL_STATE_IDLE://挂断
                if (isBlueToothHeadsetConnected()) {//蓝牙连接
                    sendStatusToUnity(false);
                }
//                else{//蓝牙未连接
//                    sendStatusToUnity(false);
//                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK://接听
                if(isBlueToothHeadsetConnected()){//蓝牙连接
                    sendStatusToUnity(true);
                }
//                else
//                {
//                   sendStatusToUnity(false);蓝牙未连接
//               }
                break;
        }
    }

    /*
    向unity发送消息通知
     */
    private void sendStatusToUnity(boolean status) {
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if (iAndroidCallback != null) {//通知Unity
                iAndroidCallback.sendGlassTrackerStatus(true);
            }
        }
    }

    /**
     * 判断蓝牙耳机是否处于连接状态
     * true：连接，false：未连接
     *
     * @return
     */
    public boolean isBlueToothHeadsetConnected() {
        boolean retval = true;
        try {
            retval = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(android.bluetooth.BluetoothProfile.HEADSET)
                    != android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

        } catch (Exception exc) {
            // nothing to do
        }
        return retval;
    }
}
