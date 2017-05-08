package com.baofeng.mj.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.WhiteCheckBusiness;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mojing.input.base.MojingInputCallback;
import com.storm.smart.common.utils.LogHelper;

/**
 * 遥控器基类
 * Created by muyu on 2016/5/25.
 */
public abstract class BaseStickActivity extends BaseActivity implements MojingInputCallback {
    private BlueToothBroadCast blueToothBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blueToothBroadCast = new BlueToothBroadCast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StickUtil.getInstance(this);
        StickUtil.setCallback(this);
        if(blueToothBroadCast!=null) {
            registerReceiver(blueToothBroadCast, new IntentFilter(
                    BluetoothAdapter.ACTION_STATE_CHANGED));
        }
//        startCheck();
    }

    @Override
    protected void onPause() {
        StickUtil.setCallback(null);
        StickUtil.disconnect();
        if (blueToothBroadCast != null) {
            unregisterReceiver(blueToothBroadCast);// 取消蓝牙广播接收者
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StickUtil.setCallback(null);
        StickUtil.disconnect();

    }

    private class BlueToothBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_ON:
                case BluetoothAdapter.STATE_OFF:
                    startCheck();// 开始校验
                    break;
                default:
                    break;
            }
        }
    }

    public abstract void startCheck();

    @Override
    public boolean onMojingKeyDown(String s, int i) {
        return false;
    }

    @Override
    public boolean onMojingKeyUp(String s, int i) {
        return false;
    }

    @Override
    public boolean onMojingKeyLongPress(String s, int i) {
        return false;
    }

    @Override
    public boolean onMojingMove(String s, int i, float v, float v1, float v2) {
        return false;
    }

    @Override
    public boolean onMojingMove(String s, int i, float v) {
        return false;
    }

    @Override
    public void onMojingDeviceAttached(String arg0) {
        if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            return;
        }
        if(!StickUtil.filterDeviceName(arg0)){
            return;
        }
        StickUtil.isConnected = true;
        BaseApplication.INSTANCE.setJoystickName(arg0);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                startCheck();
            }
        });
        StickUtil.onMojingDeviceAttached(arg0);
    }

    @Override
    public void onMojingDeviceDetached(String arg0) {
        if(!StickUtil.filterDeviceName(arg0)){
            return;
        }
        StickUtil.isConnected = false;
        BaseApplication.INSTANCE.setJoystickName("");

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                startCheck();
            }
        });
        StickUtil.onMojingDeviceDetached(arg0);
    }


    @Override
    public void onBluetoothAdapterStateChanged(int i) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("StickUtil","----joystickCheckActivity dispatchKeyEvent = "+event.getKeyCode());
        if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){// 音量减小
            return super.dispatchKeyEvent(event);
        }else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){// 音量增大
            return super.dispatchKeyEvent(event);
        }
        if (StickUtil.dispatchKeyEvent(event))
            return true;
        else
            return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {

        if (StickUtil.dispatchGenericMotionEvent(event))
            return true;
        else
            return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {
    }

    @Override
    public void onTouchPadPos(String s, float v, float v1) {

    }

}
