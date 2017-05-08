package com.baofeng.mj.unity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Debug;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.WhiteCheckBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.unity.launcher.GetResourceUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.systemutil.AudioManagerUtil;
import com.baofeng.mj.util.systemutil.BrightnessUtil;
import com.baofeng.mj.util.systemutil.LanguageUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.viewutil.LoadAssetsConfig;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;
import java.util.List;

/**
 * @author liuchuanchi
 * @description: Unity公共业务类
 */
public class UnityPublicBusiness {

    public static final String  BATTERY_LEVEL = "level";
    public static final String  BATTERY_SCALE = "scale";
    public static final String  BATTERY_STATUS = "status";
    public static final String  BATTERY_TEMPERATURE = "temperature";

    private final static int HANDCONTRL_LEFT = 0;//左手
    private final static int HANDCONTRL_RIGHT = 1;//右手
    private final static String HANDCONTRL_KEY = "remotecontroller_mode";// 存储key

    private static boolean isDefaultMusicActive = false;

    /**
     * 设置当前音量（音量范围是 0 - 100）
     */
    public static void setStreamCurrentVolume(int currentVolume) {
        LogHelper.e("infos","======setStreamCurrentVolume========"+currentVolume);
        AudioManagerUtil.getInstance().setStreamCurrentVolume(currentVolume);
    }

    /**
     * 获取当前音量（音量范围是 0 - 100，不是系统的音量范围 0 - 15）
     */
    public static int getStreamCurrentVolume() {
        LogHelper.e("infos","======getStreamCurrentVolume========"+AudioManagerUtil.getInstance().getStreamCurrentVolume());
        return AudioManagerUtil.getInstance().getStreamCurrentVolume();
    }

    /**
     * 获取最大音量（不返回系统最大音量：15，直接返回100）
     */
    public static int getStreamMaxVolume() {
        return AudioManagerUtil.getInstance().getStreamMaxVolume();
    }

    /**
     * 设置用户亮度值
     *
     * @param brightnessValue 亮度值 0 - 255
     */
    public static void setUserBrightnessValue(int brightnessValue) {
        LogHelper.d("lxk","setUserBrightnessValue : " + brightnessValue);
        BrightnessUtil.setUserBrightnessValue(UnityActivity.INSTANCE, brightnessValue);
    }

    /**
     * 获取用户亮度值 0 - 255
     */
    public static int getUserBrightnessValue() {
        LogHelper.d("lxk","getUserBrightnessValue : " + BrightnessUtil.getUserBrightnessValue());
        return BrightnessUtil.getUserBrightnessValue();
    }

    /**
     * 设置系统亮度值
     *
     * @param brightnessValue 亮度值 0 - 255
     */
    public static void setSystemBrightnessValue(Context context,int brightnessValue) {
        LogHelper.d("lxk","setSystemBrightnessValue : " + brightnessValue);
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }


    /**
     * 获取系统亮度值 0 - 255
     */
    public static int getSysBrightnessValue() {
        LogHelper.d("lxk0","getSysBrightnessValue : " + BrightnessUtil.getSysBrightnessValue());
        return BrightnessUtil.getSysBrightnessValue();
    }



    public static void backSetSysBrightnessValue(){
        UnityPublicBusiness.setSystemBrightnessValue(BaseApplication.INSTANCE,SettingSpBusiness.getInstance().getSystemBrightnessValue());
    }
    /**
     * 设置自适应亮度模式
     *
     * @param isAutoBrightnessMode true打开，false关闭
     */
    public static void setAutoBrightnessMode(boolean isAutoBrightnessMode) {
        BrightnessUtil.setAutoBrightnessMode(isAutoBrightnessMode);
    }

    /**
     * true是自适应亮度模式，false不是
     */
    public static boolean isAutoBrightnessMode() {
        return BrightnessUtil.isAutoBrightnessMode();
    }

    /**
     * 返回电池是否在充电中
     * @return
     */
    public static boolean isBatteryCharging(){
        int status = BaseApplication.INSTANCE.getBatteryStatus();
        if(status == BatteryManager.BATTERY_STATUS_CHARGING ||status == BatteryManager.BATTERY_STATUS_FULL){
            return true;
        }
        return false;
    }

    /**
     * 获取电池电量 0 - 100
     */
    public static int getBatteryLevel() {
        return BaseApplication.INSTANCE.getBatteryLevel();
    }

    /**
     * 获取电池温度 例如值：29.0
     */
    public static String getBatteryTemperature() {
        return BaseApplication.INSTANCE.getBatteryTemperature();
    }

    /**
     * wifi信号强度，0表示信号最好，1表示信号偏好，2表示信号偏差，3表示最差，有可能连接不上或者掉线
     */
    public static int getWifiLevel() {
        return NetworkUtil.getWifiLevel();
    }

    /**
     * 获取网络，0：无网络，1：WiFi，2：2G，3：3G，4：4G
     */
    public static int getNetwork() {
        int currentNetwork = NetworkUtil.getNetwork();
        return NetworkUtil.convertNetwork(currentNetwork);
    }

    /**
     * 判断wifi是否打开
     *
     * @return
     */
    public static synchronized boolean isWifiOpen() {
        android.net.wifi.WifiManager mWfMgr = (android.net.wifi.WifiManager) BaseApplication.INSTANCE
                .getSystemService(Context.WIFI_SERVICE);
        boolean isenable = mWfMgr.isWifiEnabled();
        return isenable;
    }

    public static boolean getCanGPRSDownload(){
        return SettingSpBusiness.getInstance().getCanGPRSDownload();
    }

    /**
     * 当wifi打开时，判断是否可用
     * @return
     */
    public static boolean openCanUse(){
        return NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE);
    }
    /**
     * 清除app缓存
     */
    public static void cleanAppCache() {
        if(UnityActivity.INSTANCE != null){
            FileCommonUtil.cleanAppCache(UnityActivity.INSTANCE.getIAndroidCallback());
        }
    }

    /**
     * 获取缓存大小 例如值：35MB
     */
    public static String getAppCacheSize() {
        return FileSizeUtil.getAppCacheSize();
    }

    /**
     * false没有调过UnityActivity类的startAndroidMainActivity()方法，true调过
     */
//    public boolean getFirstStartAndroidMainActivity() {
//        return BaseApplication.INSTANCE.getFirstStartAndroidMainActivity();
//    }

    /**
     * 进入android的主activity
     */
//    public void startAndroidMainActivity() {
//        //BaseApplication.INSTANCE.setFirstStartAndroidMainActivity(true);//必须执行此代码
//        String runningPlatform = SystemUtil.getRunningPlatform();
//        if ("1".equals(runningPlatform)) {//运行平台是手机
//            UnityActivity.INSTANCE.startActivity(new Intent(UnityActivity.INSTANCE, MainActivityGroup.class));
//        }
//    }

    /**
     * Android与Unity交互，添加Android回调
     */
    public void addIAndroidCallback(IAndroidCallback iAndroidCallback) {
        if(UnityActivity.INSTANCE != null){
            UnityActivity.INSTANCE.addIAndroidCallback(iAndroidCallback);
        }
    }

    /**
     * 获取测试的文件夹
     *
     * @return
     */
//    public static String getTestFolder(){
//        return LocalVideoPathBusiness.getTestFolder();
//    }
    public static String unityHierarchy() {
        Log.d("gounity","----call  unityHiearchy ----");
        String hierarchy = UnityActivity.INSTANCE.getHierarchyString();
        Log.d("gounity","----  unityHiearchy hierarchy = "+hierarchy);
        if (hierarchy == null)
            return "";
        return hierarchy;
    }
    public static void cleanHierarchy() {
        Log.d("gounity","----call  cleanHierarchy ----");
        UnityActivity.INSTANCE.SetHierarchyString();
    }
    /*
     * 获取屏幕模式
     */
    public static boolean getOrientationMode() {
        return BaseApplication.INSTANCE.getOrientationMode();
    }

    /*
     * 设置屏幕模式
     */
    public static void setOrientationMode(boolean flag) {
        BaseApplication.INSTANCE.setOrientationMode(flag);
    }

    /**
     * 获取系统语言
     */
    public static String getLanguage() {
        return LanguageUtil.getLanguage();
    }

    /**
     * 获取是否设置反锯齿
     *
     * @return false：未设置，true：设置
     */
    public static boolean getAntiSwitch() {
        if (SettingSpBusiness.getInstance().getAnti_aliasing() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取是否设置曲面
     *
     * @return false：未设置，true：设置
     */
    public static boolean getSurSwitch() {
        if (SettingSpBusiness.getInstance().getSur_Switch() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取是否设置球模背景
     *
     * @return false：未设置，true：设置
     */
    public static boolean getBgSwitch() {
        if (SettingSpBusiness.getInstance().getBgSwitch() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取是否设置过渡动画
     *
     * @return false：未设置，true：设置
     */
    public static boolean getAniSwitch() {
        if (SettingSpBusiness.getInstance().getTrans_Ani_Switch() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取是否设置透明
     *
     * @return false：未设置，true：设置
     */
    public static boolean getTransSwitch() {
        if (SettingSpBusiness.getInstance().getTrans_Switch() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取是否设置Mask特效
     *
     * @return false：未设置，true：设置
     */
    public static boolean getMaskSwitch() {
        if (SettingSpBusiness.getInstance().getMask() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前魔镜镜片类型
     *
     * @return 镜片Key值
     */
    public static String getGlassesModeKey() {
        return SettingSpBusiness.getInstance().getGlassesModeKey();
    }

    /**
     * 更改1:横屏跳转到竖屏 20161027版本去掉横竖屏跳转功能，此方法作废
     * 更改2: 20170324版，彻底打破横竖屏对应关系：
     */
    public static void clickToNative(int type, int subType, String url, String title, String resId, String operateJson) {
        ResTypeUtil.onClickToActivity(UnityActivity.INSTANCE, type, subType, url, title, resId, "", "", operateJson);
        BaseApplication.isFromUnityOrStartApp = true;
    }

    /**
     * 将app切至后台
     */
    public static void moveTaskToBack(){
        UnityActivity.INSTANCE.moveTaskToBack(true);
    }

    /**
     * 横屏接口URL
     *
     * @return
     */
    public static String horizonServiceUrl() {
        return LanguageValue.getInstance().getServerhorizonValue(UnityActivity.INSTANCE);
    }

    /*
    * 获取魔镜5USB连接状态
    * @return true:连接,false:未连接
     */
//    public static boolean getUSBConnectState() {
//        return MojingSDKServiceManager.isGlassTracker();
//    }

    /**
     * 获取高清测试结果
     * true:通过，false:不通过
     *
     * @return
     */
    public static boolean getHigh() {
        LogHelper.i("infos","getHigh==="+SettingSpBusiness.getInstance().getHigh()+"==品牌=="+WhiteCheckBusiness.getMobileBrand()+"==型号=="+WhiteCheckBusiness.getMobileModel());
        if (SettingSpBusiness.getInstance().getHigh() == 2
                || (WhiteCheckBusiness.getMobileBrand().equals("Baofeng") && WhiteCheckBusiness.getMobileModel().equals("KE-01"))) {
            return true;
        }
        return false;
    }

    /**
     * 用户型号手机入库,更新服务器黑白名单列表
     * 用户解锁后完整播放完了视频或连续播放超过30秒，则自动将该手机型号入库，且开启用户的高清播放能力
     */
    public static void updateHdTestResult(){
        Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        currrentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WhiteCheckBusiness().updateWhiteCheckness(BaseApplication.INSTANCE);
            }
        });
    }

    /**
     * 获取hook测试结果
     * true:通过，false:不通过
     *
     * @return
     */
    public static boolean getHook() {
        if (SettingSpBusiness.getInstance().getHook() == 2) {
            return true;
        }
        return false;
    }

    /**
     * 获取otg测试结果
     * true:通过，false:不通过
     *
     * @return
     */
    public static boolean getOtg() {
        if (SettingSpBusiness.getInstance().getOtg() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取魔镜app版本号
     */
    public static int getAPKVersionCode() {
        return ApkUtil.getVersionCode();
    }

    /**
     * 获取多语言文本 Android assets下LocalizationString.json文件
     */
    public static String languageFile() {
        return LoadAssetsConfig.loadLanguageConfig(UnityActivity.INSTANCE);
    }

    /*
    * 获取手机时间格式
    * @returen true:24小时制，false:12小时制
     */
    public static boolean getTimeFormat() {
        return android.text.format.DateFormat.is24HourFormat(UnityActivity.INSTANCE);
    }

    /*
    *获取操纵杆状态
    * @return ture:连接，false：断开
     */
    public static boolean getJoystickStatus() {
        boolean isJoystickConnect = StickUtil.isConnected || BaseApplication.INSTANCE.getJoystickConnect();
        return isJoystickConnect;
    }

    /**
     * @return  deviceType
     */

    public static int getInputDeviceType(){
        System.out.println("zl->getInputDeviceType:" + BaseApplication.INSTANCE.getInputDeviceType());

        return BaseApplication.INSTANCE.getInputDeviceType();
    }

    /*
    *获取魔镜5代连接状态
    * @return ture:连接，false：断开
     */
    public static boolean getBFMJ5Connect() {
        return BaseApplication.INSTANCE.isBFMJ5Connection();
    }

    /*
    *获取渠道号
    * @return 渠道号
     */
    public static String getChannelCode() {
        String channelCode = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        if (channelCode == null)
            return "";
        return channelCode;
    }

    /*
    * 获取设备ID，
    * @return 设备ID
     */
    public static String getDeviceID() {
        String deviceID = ((TelephonyManager) UnityActivity.INSTANCE.getSystemService(UnityActivity.INSTANCE.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceID == null)
            return "";
        return deviceID;
    }

    /*
    *获取版本名
    * @return 版本名
     */
    public static String getAPKVersionName() {
        return ApkUtil.getVersionName();
    }

    /*
    * 获取系统版本号
    * @return 系统版本号
     */
    public static String getOSVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;
        if (osVersion == null)
            return "";
        return osVersion;
    }

    /**
     * 获取控制模式
     * @return
     */
    public static int getControlMode(){
        return SettingSpBusiness.getInstance().getControlMode();
    }

    /**
     * 设置控制模式
     * @param mode 默认0：头控+手柄，1：纯手柄
     */
    public static void setControlMode(int mode){
        SettingSpBusiness.getInstance().setControlMode(mode);
    }

    /**
     * 手柄左右手 0：左手  1： 右手
     * @return  默认为 右手
     */
    public static  int getHandContrl(){
     return Settings.Secure.getInt(BaseApplication.INSTANCE.getContentResolver(),HANDCONTRL_KEY,HANDCONTRL_RIGHT);
    }

    /**
     * 跳转到设置wifi界面
     */
    public static void skipWlan(){
        final Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
        try {
            addAnim(intent,currrentActivity);
        }catch (RuntimeException e) {
            e.printStackTrace();

        }
    }
    private static  void addAnim(final Intent launchIntent,final Activity activity ) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.startActivity(launchIntent);
                    activity.overridePendingTransition(GetResourceUtil.getAnimId
                            (activity, "fade_in_fast"), GetResourceUtil.getAnimId(activity, "fade_out_fast"));
                }
            });

        }
    }

    public static void clearOtherApp(){
        //To change body of implemented methods use File | Settings | File Templates.
        ActivityManager am = (ActivityManager) BaseApplication.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);

        long beforeMem = getAvailMemory();
        int count = 0;
        if (infoList != null) {
            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
//                Log.d(TAG, "process name : " + appProcessInfo.processName);
                //importance 该进程的重要程度  分为几个级别，数值越低就越重要。
//                Log.d(TAG, "importance : " + appProcessInfo.importance);

                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
//                        Log.d(TAG, "It will be killed, package name : " + pkgList[j]);
                        LogHelper.e("infos","pkg==="+pkgList[j]);
                        if(pkgList[j].equals("com.baofeng.mj")
                                || pkgList[j].equals("com.baofeng.fota")){
                            continue;
                        }
                        am.killBackgroundProcesses(pkgList[j]);
                        count++;
                    }
                }
                LogHelper.e("infos","=======================pkg==========================");
                String[] pkgList = appProcessInfo.pkgList;
                for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
//                        Log.d(TAG, "It will be killed, package name : " + pkgList[j]);
                    LogHelper.e("infos","pkg---------"+pkgList[j]);
                }

            }
        }

        long afterMem = getAvailMemory();
//        Toast.makeText(ClearMemoryActivity.this, "clear " + count + " process, "
//                + (afterMem - beforeMem) + "M", Toast.LENGTH_LONG).show();
        if(null != UnityActivity.INSTANCE && null != UnityActivity.INSTANCE.getIAndroidCallback()){
            UnityActivity.INSTANCE.getIAndroidCallback().sendClearOtherApp(count,(afterMem - beforeMem));
            LogHelper.e("infos","===========clear=================="+count+"===Mem==="+(afterMem - beforeMem)+"M");
        }
    }

    //获取可用内存大小
    private static long getAvailMemory() {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) BaseApplication.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        return mi.availMem / (1024 * 1024);
    }





    /**
     * 获取背景图片
     * @param packageName
     * @return
     */
    public static String getAppBgIcon(String packageName){
        String iconBg = "";
        File file = new File(getRootPath() + "/APPIcon/");
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            String path;
            for(File f : files){
                path = subPath(f.getName());
                if((packageName+"bj").equals(path)){
                    iconBg = f.getAbsolutePath();
                }
            }
        }

        return iconBg;
    }


    /**
     * 获取应用icon
     * @param packageName
     * @return
     */
    public static String getAppIcon(String packageName){
        String icon = "";
        File file = new File(getRootPath() + "/APPIcon/");
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            String path;
            for(File f : files){
                path = subPath(f.getName());
                if(packageName.equals(path)){
                    icon = f.getAbsolutePath();
                }

            }
        }

        return icon;
    }


    private static String getRootPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }
    }

    private static String subPath(String path){
        if(!TextUtils.isEmpty(path)){
            String[] strings = path.split(".png");

            return strings[0];
        }

        return "";
    }

    /**
     * 发送QVR_INIT_COMPLETED广播
     */
    public static void sendBroadcastQVR(){
        Intent intent=new Intent("android.intent.action.QVR_INIT_COMPLETED");
        BaseApplication.INSTANCE.sendBroadcast(intent);
    }


    private void getRunningAppProcessInfo() {
        ActivityManager am = (ActivityManager) BaseApplication.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE);

        //获得系统里正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            String[] pkgList = runningAppProcessInfo.pkgList;
//            String packageName =
            // 进程ID号
            int pid = runningAppProcessInfo.pid;
            // 用户ID
            int uid = runningAppProcessInfo.uid;
            // 进程名
            String processName = runningAppProcessInfo.processName;
            // 占用的内存
            int[] pids = new int[] {pid};
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(pids);
            int memorySize = memoryInfo[0].dalvikPrivateDirty;

            System.out.println("processName="+processName+",pid="+pid+",uid="+uid+",memorySize="+memorySize+"kb");
        }
    }

    public static boolean isPlayAudioByBackground(){
        return isDefaultMusicActive;
    }

    public static void setDefaultMusicActive(boolean active){
        isDefaultMusicActive = active;
    }
}
