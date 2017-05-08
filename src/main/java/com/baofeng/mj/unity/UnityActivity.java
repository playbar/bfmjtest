package com.baofeng.mj.unity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.WhiteCheckBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.VrSettingActivity;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.systemutil.AudioManagerUtil;
import com.baofeng.mj.util.systemutil.BrightnessUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.unity.MojingVrActivity;
import com.storm.smart.common.utils.LogHelper;

/**
 * @author liuchuanchi
 * @description: UnityActivity，android与unity交互
 */
public class UnityActivity extends MojingVrActivity {
    public static UnityActivity INSTANCE;
    public static boolean isActive = false;
    private IAndroidCallback iAndroidCallback;//Android与Unity交互，Android回调
    private String hierarchyString;
    private boolean manualDisconnectJoystick  = false;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private HomeListener mHomeListener;
    private int mJoystickCount = 0;
    private int mMotionCount = 0;
//    private TestReceiver receiver;//测试耳机拔插行为
    private int mLastValue;
    public static boolean mIsYTJ = ChannelUtil.getChannelCode("running_platform").equals("0");

    private long mResumeTimes = 0;
    private long mPauseTimes = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检测是否有背景声音
        UnityPublicBusiness.setDefaultMusicActive(isMusicActive());
        INSTANCE = this;
        UnityLocalBusiness.getLocalVideoDataNoCall(0);
        BaseApplication.INSTANCE.addActivity(this);
        DownloadUtils.getInstance().getAllData();
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                if (iAndroidCallback != null) {//安装完成通知Unity
                    iAndroidCallback.sendApkInstallResult(packageName, ApkUtil.INSTALL_SUCCEEDED);
                }
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        hierarchyString = getIntent().getStringExtra("hierarchy");

        //保存系统亮度到本地
        if(mIsYTJ){
            SettingSpBusiness.getInstance().setSystemBrightnessValue(UnityPublicBusiness.getSysBrightnessValue());
            LogHelper.e("infos","=======onCreate=============="+BrightnessUtil.getSysBrightnessValue());
        }
//        UnityDownloadBusiness.startAllInstall();
        initHomeListen();
        mHomeListener.start();
        LogHelper.e("infos","====UnityActivity=onCreate======");
        BaseApplication.INSTANCE.registerNetWorkReceiver();
        BaseApplication.INSTANCE.registerBatteryBroadcast();
    }

    @Override
    protected void onResume() {
        isActive = true;
        BaseApplication.INSTANCE.registerBlackScreenBroadcast();
        UnityPublicBusiness.setDefaultMusicActive(isMusicActive());
        LogHelper.e("infos","====UnityActivity=onResume start====== resume times => " + ++mResumeTimes);
        if(!BaseApplication.INSTANCE.isBFMJ5Connection()){
            BaseApplication.INSTANCE.setSendTracker(0);
        }
        LogHelper.e("infos","====UnityActivity=onResume before super======");
        super.onResume();
		
        LogHelper.e("infos","====UnityActivity=onResume after super======");
        manualDisconnectJoystick = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntent().putExtras(intent);
        hierarchyString = getIntent().getStringExtra("hierarchy");
        LogHelper.e("infos","=====UnityActivity==onNewIntent=========");
        String json = getIntent().getStringExtra("json");
        if (json != null && !json.isEmpty()){
            iAndroidCallback.sendPlayLocalMovie(json);
        }
    }

    @Override
    protected void onDestroy() {
        BaseApplication.INSTANCE.removeActivty(this);
        super.onDestroy();
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
//        if(null != receiver){
//            unregisterReceiver(receiver);
//        }
        mHomeListener.stop();
        MjDownloadSDK.stopAll(this);
        stopBluetooth();
    }
    @Override
    protected void onPause()
    {
        BaseApplication.INSTANCE.unRegisterBlackScreenBroadcast();
        manualDisconnectJoystick = true;
        LogHelper.e("infos","====UnityActivity=onPause before super====== resume times => " + ++mPauseTimes);
        super.onPause();
        LogHelper.e("infos","====UnityActivity=onPause after super======");
        isActive = false;
    }

    public String getHierarchyString(){
        return hierarchyString;
    }
    public void SetHierarchyString(){
        hierarchyString = null;
    }
    @Override
    public void onMojingDeviceAttached(String deviceName)
    {
        System.out.println("====UnityActivity=onMojingDeviceAttached:" + deviceName);

        if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            return;
        }
        int inputDeviceType = 0;
        if(deviceName.contains("mojing-motion")){//体感手柄
            inputDeviceType = 3;
        }
        else {
            String model = WhiteCheckBusiness.getMobileModel();
            if (!StickUtil.filterDeviceName(deviceName)) {
                return;
            }
            StickUtil.isConnected = true;
            mJoystickCount++;
            BaseApplication.INSTANCE.setJoystickName(deviceName);
            inputDeviceType = 2;
        }
        if (iAndroidCallback != null) {//通知Unity
            if(inputDeviceType == 2) {
                if(BaseApplication.INSTANCE.getInputDeviceType()==3)
                    inputDeviceType = 3;
                iAndroidCallback.sendJoystickStatus(UnityPublicBusiness.getJoystickStatus());
            }
            System.out.println("====UnityActivity=onMojingDeviceAttached:" + inputDeviceType);

            iAndroidCallback.sendInputDeviceType(inputDeviceType);
        }else {
            if (inputDeviceType == 2){
                if(BaseApplication.INSTANCE.getInputDeviceType()==3)
                    inputDeviceType = 3;
            }
        }
        BaseApplication.INSTANCE.setInputDeviceType(inputDeviceType);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogHelper.e("infos", "======UnityActivity=onRestart==============");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.e("infos","======UnityActivity=onStart==============");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogHelper.e("infos", "======UnityActivity=onStop==============");
    }

    @Override
    public void finish() {
        super.finish();
        LogHelper.e("infos", "======UnityActivity=finish==============");
    }

    @Override
    public void onMojingDeviceDetached(String deviceName) {
        System.out.println("====UnityActivity=onMojingDeviceDetached:" + deviceName);
        int inputDeviceType = 0;
        if (deviceName.contains("mojing-motion")) {//体感手柄
            inputDeviceType = 3;
        } else {
            String model = WhiteCheckBusiness.getMobileModel();
            if (!StickUtil.filterDeviceName(deviceName)) {
                return;
            }
            mJoystickCount--;
            if(mJoystickCount == 0)
                StickUtil.isConnected = false;

            BaseApplication.INSTANCE.setJoystickName("");
            inputDeviceType = 2;
//            if (manualDisconnectJoystick) {
//                manualDisconnectJoystick = false;
//                return;
//            }
        }
        if (iAndroidCallback != null) {//通知Unity
            if(inputDeviceType == 2){
                if(BaseApplication.INSTANCE.getInputDeviceType() == 3)
                    inputDeviceType = 3;
                else
                    inputDeviceType = 0;
                iAndroidCallback.sendJoystickStatus(UnityPublicBusiness.getJoystickStatus());
            }else if(inputDeviceType == 3){
                if(UnityPublicBusiness.getJoystickStatus())
                    inputDeviceType = 2;
                else
                    inputDeviceType = 0;
            }
            iAndroidCallback.sendInputDeviceType(inputDeviceType);
        }
        else
        {
            if(inputDeviceType == 3)
            {
                if(UnityPublicBusiness.getJoystickStatus())
                    inputDeviceType = 2;
            }
            else
                inputDeviceType = 0;
        }
        System.out.println("====UnityActivity=onMojingDeviceDetached.inputDeviceType:" + inputDeviceType);

        BaseApplication.INSTANCE.setInputDeviceType(inputDeviceType);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            // 音量增大
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 获取手机当前音量值
                int volume = AudioManagerUtil.getInstance().getStreamCurrentVolume();
                if(null != iAndroidCallback){
                    iAndroidCallback.sendCurrentVolume(volume);
                    LogHelper.e("infos","当前音量===="+volume);
                }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Android与Unity交互，添加Android回调
     */
    public void addIAndroidCallback(IAndroidCallback iAndroidCallback) {
        this.iAndroidCallback = iAndroidCallback;
    }

    /**
     * Android与Unity交互，获取Android回调
     */
    public IAndroidCallback getIAndroidCallback() {
        return iAndroidCallback;
    }

    private void initHomeListen(){
        mHomeListener = new HomeListener(this);
        mHomeListener.setOnHomeBtnPressListener( new HomeListener.OnHomeBtnPressListener() {
            @Override
            public void onHomeBtnPress() {
                //播放视频时，按下home键，还远系统之前的亮度
                if(mIsYTJ){
                        int spValue =  SettingSpBusiness.getInstance().getSystemBrightnessValue();
                        int sysValue = BrightnessUtil.getSysBrightnessValue();
                        if(spValue != sysValue){
                            mLastValue = sysValue;
                        }else{
                            mLastValue = spValue;
                        }
                        LogHelper.e("infos","=======home===========spValue==="+spValue+"==sysValue=="+sysValue+"==lastValue=="+mLastValue);

                    UnityPublicBusiness.setSystemBrightnessValue(UnityActivity.this,mLastValue);
                }
              if(null != iAndroidCallback){
                  iAndroidCallback.sendHomeIsPress(1);
                  LogHelper.e("infos","=======iAndroidCallback.sendHomeIsPress(1)======");
              }
            }

            @Override
            public void onHomeBtnLongPress() {
                if(null != iAndroidCallback){
                    iAndroidCallback.sendHomeIsPress(2);
                    LogHelper.e("infos","=======iAndroidCallback.sendHomeIsPress(2)======");
                }

            }
        });
    }

    /**
     * state —— 0代表拔出，1代表插入
     name——字符串，代表headset的类型
     microphone —— 1代表插入的headset有麦克风，0表示没有麦克风
     */
  /*  private class TestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {//未连接
                    LogHelper.e("infos", "=========未连接=====");
                } else if (intent.getIntExtra("state", 0) == 1) {//连接
                    LogHelper.e("infos", "=========连接====="+intent.getIntExtra("microphone",0));
                }
            }
        }

    }


    private void registerHeadsetPlugReceiver() {
        receiver = new TestReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(receiver, intentFilter);
    }*/


    private void checkPermission(){
        CheckPermission.from(this)
                .setPermissions(PermissionUtil.ALL_PERMISSIONS)
                .setPermissionListener(new PermissionListener(){
                    @Override
                    public void permissionGranted() {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(UnityActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }
                    @Override
                    public void permissionDenied() {
                        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(UnityActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }
                }).check();
    }

    @TargetApi(value = 23)
    private void requesetPermission() {
        int permissionCheck1 = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("requesetPermission", "onRequestPermission grant");
                } else {
                    Log.d("requesetPermission", "onRequestPermission dened");
                }
            }
            return;
        }
    }

    public void startBluetooth(){
        LogHelper.e( "SpeechApp", "startBluetooth enter" );
        AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setBluetoothScoOn(true);
        mAudioManager.startBluetoothSco();
    }

    public void stopBluetooth(){
        LogHelper.e( "SpeechApp", "stopBluetooth enter" );
        AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.stopBluetoothSco();
    }

    private boolean isMusicActive(){
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return am.isMusicActive();
    }


}
