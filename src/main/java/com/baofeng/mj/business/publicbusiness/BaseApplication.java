package com.baofeng.mj.business.publicbusiness;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baofeng.mj.bean.HierarchyBean;
import com.baofeng.mj.business.accountbusiness.AppUpdateBusiness;
import com.baofeng.mj.business.brbusiness.AudioReceiver;
import com.baofeng.mj.business.brbusiness.BatteryStateReceiver;
import com.baofeng.mj.business.brbusiness.BlankScreenReceiver;
import com.baofeng.mj.business.brbusiness.CloseOrOpenReceiver;
import com.baofeng.mj.business.brbusiness.ExternalStorageReceiver;
import com.baofeng.mj.business.brbusiness.HomeKeyEventReceiver;
import com.baofeng.mj.business.brbusiness.NetworkChangeReceiver;
import com.baofeng.mj.business.dl.domain.DownloadConfig;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.downloadlistener.DownLoaderListener;
import com.baofeng.mj.business.downloadutil.DownloadObserver;
import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.pluginbusiness.PluginDownloadBusiness;
import com.baofeng.mj.business.sebusiness.FeedbackService;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.baofeng.mj.ui.activity.VrSettingActivity;
import com.baofeng.mj.ui.dialog.AppUpdateDialog;
import com.baofeng.mj.ui.fragment.BaseViewPagerFragment;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.unity.UnityDownloadBusiness;
import com.baofeng.mj.unity.UnityFirewareBusiness;
import com.baofeng.mj.unity.UnityPublicBusiness;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ApplicationUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.GlassesUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.MojingSDKServiceManager;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.baofeng.mojing.service.MojingSDKAIDLService;
import com.mojing.dl.domain.DownloadItem;
import com.mojing.dl.utils.DownloadConstant;
import com.morgoo.droidplugin.PluginHelper;
import com.storm.smart.common.utils.LogHelper;
import com.storm.smart.play.utils.LibraryUtils;
import com.storm.smart.play.utils.PlayCheckUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 应用程序类
 * Created by muyu on 2016/4/7.
 */
public class BaseApplication extends Application implements DownLoaderListener {
    private static final String TAG = BaseApplication.class.getName();
    public static BaseApplication INSTANCE;//当前实例
    private List<Activity> activityList = new LinkedList<Activity>();
    private int batteryStatus; //电池状态：充电，放电，充电完成
    private int batteryLevel;//电池电量
    private int batteryScale;//电池最大电量
    private int batteryTemperature;//电池温度
    //private boolean firstStartAndroidMainActivity = false;//false没有调过UnityActivity类的startAndroidMainActivity()方法，true调过
    private HashMap<String, HashMap<String, String>> LanguageMap;
    private HashMap<String, HashMap<String, String>> serverUrlMap;
    private BatteryStateReceiver batteryStateReceiver;//监听电量广播
    private NetworkChangeReceiver networkChangedReceiver;//监听网络连接
    private ExternalStorageReceiver externalStorageReceiver;//外部存储广播
    private HomeKeyEventReceiver mHomeKeyEventReceiver;//home键监听
    //private UsbDeviceReceiver usbDeviceReceiver;//usb设备广播
    private BlankScreenReceiver mBlackScreenReceiver;//黑屏广播
    private AudioReceiver mAudioReceiver;//HDMI
    private CloseOrOpenReceiver mCloseReceiver;//关机
    private boolean isBFMJ5Connection = false;//魔镜5代接入
    private int isSendTracker = -1;
    private String joystickName = "";
    private boolean enableJoystickReport = false;
    private boolean enableToLandscapeCondition1 = false;
    private boolean enableToLandscapeCondition2 = false;
    private int mInputDeviceType = 0;// 0:无，2：普通手柄，3：体感手柄
    private AppUpdateBusiness mAppUpdateBusiness;
    public static boolean isFromUnityOrStartApp = false;

    public static int playPosition = -1;// 记录播放位置

    public static BaseApplication getInstance(){
        return INSTANCE;
    }

    public boolean isEnableMojingSDKService() {
        return enableMojingSDKService;
    }

    public int channelCheckState = 1;//渠道审核状态，1审核通过，2审核中
    private long time;
    private List<DownloadItem> unityList = new ArrayList<>();
    public void setEnableMojingSDKService(boolean enableMojingSDKService) {
        this.enableMojingSDKService = enableMojingSDKService;
    }

    private boolean enableMojingSDKService = false;

    public boolean getJoystickConnect() {
        return isJoystickConnect;
    }

    private boolean isJoystickConnect = false;
    public static final int FLAG_START = 0;
    public static final int FLAG_PAUSE = 1;
    private DownloadConfig downloadConfig;
    //    private List<DownloadItem> mCompleteList = new ArrayList<>();
    public static final String TEMP = ".temp";
    public static int mainClickPosition = -1;//记录首页点击哪个影片进入unity,回来时滚动到对应位置

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;//初始化当前实例
        LogHelper.setLogEnable(true);//控制log
        String processName = ApplicationUtil.getProcessName(this);
        String packageName = getPackageName();
        Log.d(TAG, "onCreate");
        //开启插件服务
        if(processName.contains(packageName+":Plugin")||packageName.equals(processName)){
            PluginHelper.getInstance().applicationOnCreate(getBaseContext());
        }
        if (packageName.equals(processName)) { //UI进程(主进程)
            // 添加判断 6.0及以上的 需要在splashActivity中请求权限后再初始化  add by whf 20161228
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate 1");
                init();
            } else {
                if (!PermissionUtil.isOverMarshmallow()) {
                    Log.d(TAG, "onCreate 2");
                    init();
                }
            }

            PushNewBusiness.initUmengPush(this);//初始化友盟push

        } else if ((packageName + ":channel").equals(processName)) {//Umeng推送的进程
            PushNewBusiness.initUmengPush(this);//初始化友盟push
        }
    }

    public void initDownloadInfo() {
        if (null == downloadConfig) {
            downloadConfig = new DownloadConfig();
        }
        downloadConfig.setCachePath(FileStorageUtil.getInternalMojingdownloadDir());
        MjDownloadSDK.init(this, downloadConfig.getMemoryMaxSize() * 1024 * 1024,
                downloadConfig.getFileMaxSize() * 1024 * 1024,
                downloadConfig.getSingleMemoryMaxSize() * 1024 * 1024,
                downloadConfig.getSingleFileMaxSize() * 1024 * 1024,
                downloadConfig.getMaxTask(),
                downloadConfig.getCachePath());
        DownloadObserver.getInstance().registerListener(this, this);
        LogHelper.d("infoss", "=======initDownloadInfo=======" + "==cache==" + getCacheDir().getPath());

    }

    public void init() {
        Log.d(TAG, "init 1");
        try {
            if (!MojingSDK.GetInitSDK()) {
                MojingSDK.Init(BaseApplication.INSTANCE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "init 2");
        //第一次创建路径，避免下载库创建失败影响下载
        FileStorageUtil.getDownloadDir();
        enableJoystickReport = true;
        Log.d(TAG, "init 3");
        LanguageValue.getInstance().initLanguageMap(this); //初始化多语言表
        LanguageValue.getInstance().initServerUrl(this); //初始化服务器地址
        libSOInit();//初始化播放相关
        initSDKListeners();//初始化魔镜5代
        Log.d(TAG, "init 4");
        new WhiteCheckBusiness().getData(getApplicationContext());
//            BaseApplication.INSTANCE.bindDownloadService();//绑定下载服务
        //umengService();
        registerReceiver();//注册广播
        Log.d(TAG, "init 5");
    }

    public void initSDKListeners() {
        MojingSDKServiceManager.setHMDTrackerListener(new MojingSDKServiceManager.HMDTrackerListener() {
            @Override
            public void onHMDTrackerStateChanged(boolean isGlassTracker) {
                LogHelper.d(TAG,"zl->Android HDMTracker Statu:"+isGlassTracker);

                // 获取Joystick状态
                MojingSDKAIDLService mojingSDKAIDLService = MojingSDKServiceManager.getService();
                if (mojingSDKAIDLService == null)
                    return;
                else {
                    try {
                        isJoystickConnect = mojingSDKAIDLService.getHMDJoystickState();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                LogHelper.d(TAG,"zl->Android HMDJoystick Statu:"+isJoystickConnect);

                if (isGlassTracker) {
                    if (isSendTracker == 0)
                        isSendTracker = 1;
                } else if (isSendTracker == 1) {
                    isSendTracker = 2;
                }
                if (isBFMJ5Connection != isGlassTracker) {
                    if (isGlassTracker)
                        isBFMJ5Connection = isGlassTracker;
                } else {
                    if (enableToLandscapeCondition1 && enableToLandscapeCondition2) {
//                        if(!isSecondEnableToLandscape) {
//                            isSecondEnableToLandscape = true;
//                            return;
//                        }
                    } else {
                        return;
                    }
                }
                enableToLandscapeCondition1 = false;
                enableToLandscapeCondition2 = false;
                Activity currrentActivity = getCurrentActivity();
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(iAndroidCallback !=null)
                        LogHelper.d(TAG,"zl->sendGlassTrackerStatus:0"+isGlassTracker+ ":"+getOrientationMode()+ ":"+isSendTracker+ ":"+isBFMJ5Connection);

                    if (iAndroidCallback != null && isBFMJ5Connection && (!getOrientationMode() || isSendTracker == 2)) {//通知Unity
                        LogHelper.d(TAG,"zl->sendGlassTrackerStatus:1"+isGlassTracker);
                        boolean isSend = false;

                        if(isSendTracker == 2){
                            isSendTracker = -1;
                        }
                        if(currrentActivity instanceof UnityActivity)
                            isSend = true;
                        if(isSend) {
                            iAndroidCallback.sendGlassTrackerStatus(isGlassTracker);
                        }
                    }
                }
                isBFMJ5Connection = isGlassTracker;
                LogHelper.d(TAG,"zl->if " + getOrientationMode());
                LogHelper.d(TAG,"zl->if " + isGlassTracker);
                LogHelper.d(TAG,"zl->if " + UnityActivity.isActive);
                if (getOrientationMode() && isGlassTracker && !UnityActivity.isActive) {
                    //设置5代镜片
                    LogHelper.d(TAG,"zl->Android To Landscape...0");
                    String manufactureid = ConstantKey.ManufactureID_MJ5;
                    String productid = ConstantKey.ProductID_MJ5;
                    String glassesid = ConstantKey.GlassesID_MJ5;
                    try {
                        int pid = mojingSDKAIDLService.getPID();
                        int vid = mojingSDKAIDLService.getVID();
                        if (pid == 4097 && vid == 11561) { // 魔镜5 1，8，16
                            manufactureid = ConstantKey.ManufactureID_MJ5;
                            productid = ConstantKey.ProductID_MJ5;
                            glassesid = ConstantKey.GlassesID_MJ5;
                        } else if (pid == 4417 && vid == 44113) { // 海智南
                            manufactureid = "241";
                            productid = "241";
                            glassesid = "241";
                        }
                        LogHelper.d(TAG,"zl->Android To Landscape...1");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    LogHelper.d(TAG,"zl->Android To Landscape...2");
                    GlassesManager.setSpecialGlasses(INSTANCE,manufactureid,productid,glassesid);//魔镜5代参数

                    LogHelper.d(TAG,"zl->Android To Landscape...");
                    toLandscape();
                }
            }
        });

        MojingSDKServiceManager.setHMDLowPowerListener(new MojingSDKServiceManager.HMDLowPowerListener() {
            @Override
            public void onHMDLowPowerStateChanged(boolean isLowPower) {
                if (!isBFMJ5Connection)
                    return;
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//通知Unity
                        LogHelper.d(TAG,"zl->setLowPowerListener1:" + isLowPower);
                        iAndroidCallback.sendLowPowerStatus(isLowPower);
                    }
                }
            }
        });

        MojingSDKServiceManager.setHMDJoystickListener(new MojingSDKServiceManager.HMDJoystickListener() {
            @Override
            public void onHMDJoystickStateChanged(boolean b) {
                isJoystickConnect = b;
                LogHelper.d(TAG,String.format("zl->Android HMDJoystick Connect:%b, IsBFMJ5Connection:%b", b, isBFMJ5Connection));

                boolean hasInputTypeChanged = false;
                int inputType = UnityPublicBusiness.getJoystickStatus() ? 2:0;
                if(inputType == 2) {
                    if (mInputDeviceType == 0) {
                        hasInputTypeChanged = true;
                        mInputDeviceType = inputType;
                    }
                }else {
                    if(mInputDeviceType == 2) {
                        hasInputTypeChanged = true;
                        mInputDeviceType = inputType;
                    }
                }

                if (!isBFMJ5Connection)
                    return;
                Activity currrentActivity = getCurrentActivity();
                // 通知控制器状态改变
                if (currrentActivity instanceof VrSettingActivity) {
                    if (b) {
                        ((VrSettingActivity) currrentActivity).onMojingDeviceAttached("mojing5");
                    } else {
                        ((VrSettingActivity) currrentActivity).onMojingDeviceDetached("mojing5");
                    }
                }

                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//通知Unity
                        LogHelper.d(TAG,String.format("zl->Android HMDJoystick Call Unity SendJoystickStatus:%b, mInputDeviceType:%d", UnityPublicBusiness.getJoystickStatus(), mInputDeviceType));
                        iAndroidCallback.sendJoystickStatus(UnityPublicBusiness.getJoystickStatus());

                        if(hasInputTypeChanged)
                            iAndroidCallback.sendInputDeviceType(inputType);
                    }
                }
            }
        });
        MojingSDKServiceManager.setHMDUpgradeResultListener(new MojingSDKServiceManager.HMDUpgradeResultListener() {
            @Override
            public void onHMDUpgradeResult(boolean b) {
                LogHelper.d(TAG,String.format("zl->Android HMDUpgradeResult:%b", b));
                if (!b) {
                    UnityFirewareBusiness.recordFirewareVersion();
                }
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendHMDUpgradeResult(b);
                    }
                }
            }
        });
        MojingSDKServiceManager.setHMDWorkingListener(new MojingSDKServiceManager.HMDWorkingListener() {
            @Override
            public void onHMDWorkingStateChanged(boolean b) {
                LogHelper.d(TAG,"zl->onHMDWorkingStateChanged:"+b);
                if (b) {
                    MojingSDKAIDLService mojingSDKAIDLService = MojingSDKServiceManager.getService();
                    if (mojingSDKAIDLService == null)
                        return;
                    int mcuVersion = -1;
                    int bleVersion = -1;
                    try {
                        mcuVersion = mojingSDKAIDLService.getMCUVersion();
                        bleVersion = mojingSDKAIDLService.getBLEVersion();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    FirewareBusiness.getInstance().setBleVersionCode(bleVersion);
                    FirewareBusiness.getInstance().setMcuVersionCode(mcuVersion);
//                    if (mcuVersion != -1) {
//                        IAndroidCallback iAndroidCallback = getIAndroidCallback();
//                        if (iAndroidCallback != null) {//通知Unity
//                            iAndroidCallback.sendFirewareVersion(mcuVersion, bleVersion);
//                        }
//                    }
                }
            }
        });
        MojingSDKServiceManager.setHMDUploadCompleteListener(new MojingSDKServiceManager.HMDUploadCompleteListener() {
            @Override
            public void onHMDUploadComplete() {
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if (iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendFirewareUploadComplete();
                    }
                }
            }
        });
    }

    private void initSDK() {
        try {
            if (!MojingSDK.GetInitSDK()) {
                MojingSDK.Init(INSTANCE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------下载业务逻辑    开始---------------
     */
    private MainActivityGroup mainActivityGroup;
    private BaseViewPagerFragment baseFragment;
    private BaseActivity baseActivity;
    private HashSet<DownLoadBusiness> downLoadBusinessList = new HashSet<DownLoadBusiness>();
    private AppUpdateDialog appUpdateDialog;
    //    private IDownloadServiceCallback mCallback;//下载回调
    private ServiceConnection serviceConnection;
    //    private IBfDlService dlService;
    private boolean bind;
    private List<DownloadItem> downloadingList = new ArrayList<DownloadItem>();

    public void addMainActivityGroup(MainActivityGroup mainActivityGroup) {
        this.mainActivityGroup = mainActivityGroup;
    }

    public void removeMainActivityGroup() {
        this.mainActivityGroup = null;
    }

    public void setBaseFragment(BaseViewPagerFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void addDownLoadBusiness(DownLoadBusiness downLoadBusiness) {
        downLoadBusinessList.add(downLoadBusiness);
    }

    public void removeDownLoadBusiness(DownLoadBusiness downLoadBusiness) {
        downLoadBusinessList.remove(downLoadBusiness);
    }

    public void setAppUpdateDialog(AppUpdateDialog appUpdateDialog) {
        this.appUpdateDialog = appUpdateDialog;
    }

    public List<DownloadItem> getDownloadingList() {
        getDownLoadItemNoComplete();
        return downloadingList;
    }

    /**
     * 获取正在下载的DownloadItem
     *
     * @param resId 资源id
     */
    public DownloadItem getDownloadItem(String resId) {
        getDownLoadItemNoComplete();
        if (downloadingList != null && downloadingList.size() > 0) {//遍历正在下载的集合
            for(int i = 0;i<downloadingList.size();i++){
                DownloadItem downloadItem = downloadingList.get(i);
                if (resId.equals(downloadItem.getAid())) {
                    return downloadItem;
                }
            }
        }
        return null;
    }

    /**
     * 获取正在下载的DownloadItem
     *
     * @param packageName apk包名
     */
    public DownloadItem getDownloadItemForGame(String packageName) {
        getDownLoadItemNoComplete();
        if (downloadingList != null && downloadingList.size() > 0) {//遍历正在下载的集合
            for(int i = 0;i<downloadingList.size();i++){
                DownloadItem downloadItem = downloadingList.get(i);
                if (packageName.equals(downloadItem.getPackageName())) {
                    return downloadItem;
                }
            }
        }
        return null;
    }

    public void getDownLoadItemNoComplete() {
        downloadingList.clear();
        List<DownloadItem> list = DownloadUtils.getInstance().getAllDownLoadsByState(this, MjDownloadStatus.COMPLETE, false);
        for(int i = 0; i < list.size(); i++){
            LogHelper.d("infosss","=====getDownLoadItemNoComplete======="+list.get(i).getAid());
            if(list.get(i).getAid().equals(DownloadItemUtil.DOWNLOAD_ID_MJ)){
                LogHelper.d("infosss","---------getDownLoadItemNoComplete--------------");
                list.remove(i);
                i--;
            }
        }
        downloadingList.addAll(list);
//        LogHelper.e("infoss","size==="+downloadingList.size());
    }


    @Override
    public void updateDownLoadStatus(DownloadItem downloadItem) {
        List<DownloadItem> intentList = new ArrayList<>();
        intentList.addAll(DownloadUtils.getInstance().getAllDownLoadings(this));
        if (intentList.isEmpty()) {
            return;
        }
            for(int i = 0;i<intentList.size();i++) {
                DownloadItem item = intentList.get(i);
                if (item.getApkDownloadType() == DownloadConstant.ApkDownloadType.DOWNLOAD_TYPE_PREDOWN) {
                    continue;
                }
                if (DownloadItemUtil.DOWNLOAD_ID_OTA.equals(item.getAid())) {//魔镜OTA下载
                    if (item.getDownloadState() == MjDownloadStatus.COMPLETE) {//下载完成
                        DemoUtils.deleteDownload(INSTANCE, item);//删除下载记录
                        LogHelper.d("infos", "===删除下载记录===");
                    }
                    continue;
                }
                LogHelper.d("infosss", "item.getaid==" + item.getAid() + "==state==" + item.getDownloadState() + "==appUpdateDialog==" + appUpdateDialog);
                if (DownloadItemUtil.DOWNLOAD_ID_MJ.equals(item.getAid())) {//魔镜app升级i
                    if (item.getDownloadState() == MjDownloadStatus.COMPLETE) {//下载完成
                        File file = DownloadResBusiness.getDownloadResFileNoEx(item.getDownloadType(), item.getTitle(), item.getAid());
                        if (file != null) {
                            LogHelper.d("infosss", "file.path==" + file.getAbsolutePath());
                            file.renameTo(DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(), item.getTitle(),item.getAid(), item.getHttpUrl()));
                        }

                        LogHelper.d("infos", "==DOWNLOAD_ID_MJ=删除下载记录===");
                        if (appUpdateDialog != null) {
                            appUpdateDialog.updateDownloaded(item);
                        }
                        DemoUtils.deleteDownload(INSTANCE, item);//删除下载记录
                    } else {
                        if (appUpdateDialog != null) {
                            appUpdateDialog.updateDownloading(item);
                        }
                    }
                    continue;
                }
                if (DownloadItemUtil.DOWNLOAD_ID_FRIEWARE_MCU.equals(item.getAid())) {//固件MCU升级
                    if (item.getDownloadState() == MjDownloadStatus.COMPLETE) {//下载完成
                        DemoUtils.deleteDownload(INSTANCE, item);//删除下载记录
                        LogHelper.d("infos", "==DOWNLOAD_ID_FRIEWARE_MCU=删除下载记录===");
                        //调用自己逻辑
                        FirewareBusiness.getInstance().notifyUpdate(FirewareBusiness.FIREWARE_TYPE_MCU);
                    } else { // Downloading

                    }
                    continue;
                }
                if (DownloadItemUtil.DOWNLOAD_ID_FRIEWARE_BLE.equals(item.getAid())) {//固件BLE升级
                    if (item.getDownloadState() == MjDownloadStatus.COMPLETE) {//下载完成
                        DemoUtils.deleteDownload(INSTANCE, item);//删除下载记录
                        LogHelper.d("infos", "==DOWNLOAD_ID_FRIEWARE_BLE=删除下载记录===");
                        //调用自己逻辑
                        FirewareBusiness.getInstance().notifyUpdate(FirewareBusiness.FIREWARE_TYPE_BLE);
                    } else { // Downloading

                    }
                    continue;
                }
                if (!unityList.isEmpty()) {
                    for (int a = 0; a < unityList.size(); a++) {
                        if (item.getDownloadState() != MjDownloadStatus.COMPLETE && unityList.get(a).getAid().equals(item.getAid())) {
                            LogHelper.d("infosss", "unitytitle==" + item.getTitle());
                            unityList.remove(a);
                            a--;
                        }
                    }
                }
                if (item.getDownloadState() == MjDownloadStatus.COMPLETE) {//下载完成

                    File tempFile = DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(),item.getTitle() ,item.getAid(), item.getHttpUrl());
                    if (null != tempFile && tempFile.exists()) {
                        LogHelper.d("tests", "tempFile==" + tempFile);
                        continue;
                    }
                    if (ResTypeUtil.res_type_roaming == item.getDownloadType()) {
                        File zipCompleteFile = DownloadResBusiness.getDownloadResFile(item.getDownloadType(), item.getAid(), item.getTitle(), item.getHttpUrl());
                        if (null != zipCompleteFile && zipCompleteFile.exists()) {
                            LogHelper.d("tests", "zipCompleteFile==" + zipCompleteFile);
                            continue;
                        }
                    }

                    LogHelper.d("infossss", "packageName==" + item.getPackageName() + "==title==" + item.getTitle() + "==apkversion==" + item.getApkVersionCode());
                    if (ResTypeUtil.res_type_game == item.getDownloadType()) {
                        File updateFile = DownloadResBusiness.getDownloadResFile(item);
                        //升级判断，本地安装版本号 服务器版本号
                        if (ApkUtil.getVersionCodeByPackageName(item.getPackageName()) >= Integer.valueOf(item.getApkVersionCode())) {
                            if (null != updateFile && updateFile.exists()) {
                                continue;
                            } else {
                                if (isAppInstalled(item.getPackageName())) {
                                    continue;
                                }
                            }
                        }
                    }
//                File tempFile = DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(), item.getTitle(), item.getHttpUrl());
//                if (null != tempFile && tempFile.exists()) {
//                    continue;
//

                        if (ResTypeUtil.res_type_roaming == item.getDownloadType()) {
                            File zipCompleteFile = DownloadResBusiness.getDownloadResFile(item.getDownloadType(), item.getAid(), item.getTitle(), item.getHttpUrl());
                            if (null != zipCompleteFile && zipCompleteFile.exists()) {
                                continue;
                            }
                        }

                        LogHelper.d("infosss", "===============过来了================" + item.getTitle());
                        File renameFile = DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(),item.getTitle(), item.getAid(), item.getHttpUrl());
                        if (renameFile == null || !renameFile.exists()) {
                            File file = DownloadResBusiness.getDownloadResFileNoEx(item.getDownloadType(), item.getTitle(), item.getAid());
                            if (file != null) {
                                file.renameTo(DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(), item.getTitle(),item.getAid(), item.getHttpUrl()));
                            }
                        }

                        File roamFile = DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(), item.getTitle(),item.getAid(), item.getHttpUrl());
                        //File roamFile = DownloadResBusiness.getDownloadResFileHasEx(item.getDownloadType(), item.getTitle(), item.getHttpUrl());
                        LogHelper.d("infos", "roamFile==" + roamFile.getAbsolutePath());
                        if (ResTypeUtil.res_type_roaming == item.getDownloadType() && roamFile.exists()) {//如果是漫游
                            UnZipUtil.unZip(item, new UnZipUtil.UnZipNotify() {//解压漫游资源
                                @Override
                                public void notify(DownloadItem downloadItem, int unZipResult) {
                                    if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                                        UnityDownloadBusiness.updateDownloaded(downloadItem);//通知unity
                                    }
                                }
                            });
                        }

                        String sourceFilePath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading, item.getTitle(), item.getAid());
                        String targetFilePath = DownloadResInfoBusiness.getDownloadResInfoFilePath(item.getDownloadType(),item.getTitle(), item.getAid());
                        LogHelper.d("infoss", "downloadType==" + item.getDownloadType());
                        FileCommonUtil.copyFile(sourceFilePath, targetFilePath);//文件拷贝
                        FileCommonUtil.deleteFile(sourceFilePath);//删除下载中的资源信息文件
                        if (ResTypeUtil.res_type_roaming == item.getDownloadType()) {//是漫游
                            //因为漫游下载完成需要解压，所以暂时不通知unity，等到解压完成再通知unity
                        } else {//不是
                        }

                        Activity curActivity = getCurrentActivity();
                        if (curActivity != null && curActivity instanceof UnityActivity) {//当前界面是横屏
                            boolean isHave = false;
                            if (!unityList.isEmpty()) {
                                for (int k = 0; k < unityList.size(); k++) {
                                    if (unityList.get(k).getAid().equals(item.getAid())) {
                                        LogHelper.d("infosss", "title==" + item.getTitle());
                                        isHave = true;
                                    }
                                }
                            }

                            if (!isHave) {
                                UnityDownloadBusiness.updateDownloaded(item);//通知unity
                                unityList.add(item);
                            }


                        }
                        if (baseFragment != null) {
                            baseFragment.updateDownloaded(item);
                        }
                        if (baseActivity != null) {
                            baseActivity.updateDownloaded(item);
                        }
                        if (downLoadBusinessList.size() > 0) {
                            for (Iterator<DownLoadBusiness> it = downLoadBusinessList.iterator(); it.hasNext(); ) {
                                DownLoadBusiness business = it.next();
                                business.updateDownloaded(item);
                            }
                        }

                        ReportBusiness.getInstance().reportDownloadCompleteClick(item);
                    } /*else {//正在下载
                downloadingList.add(item);
            }*/
                }

        List<DownloadItem> loadingList = new ArrayList<>();
        for(int i = 0;i<intentList.size();i++){
            DownloadItem item = intentList.get(i);
            if (item.getDownloadState() != MjDownloadStatus.COMPLETE && !DownloadItemUtil.DOWNLOAD_ID_MJ.equals(item.getAid())) {
                loadingList.add(item);
            }
        }

        int downloadingSize = loadingList.size();
        if (mainActivityGroup != null) {//显示小红点
            mainActivityGroup.showRedPoint(loadingList);
        }

        Activity curActivity = getCurrentActivity();
        if (curActivity != null && curActivity instanceof UnityActivity) {//当前界面是横屏
            UnityDownloadBusiness.updateDownloading(downloadingSize, loadingList);//通知unity
        }

        if (baseFragment != null) {
            baseFragment.updateDownloading(downloadingSize, loadingList);
        }
        if (baseActivity != null) {
            baseActivity.updateDownloading(downloadingSize, loadingList);
        }

        if (downLoadBusinessList.size() > 0) {
            for (Iterator<DownLoadBusiness> it = downLoadBusinessList.iterator(); it.hasNext();) {
                DownLoadBusiness business = it.next();
                business.updateDownloading(downloadingSize, loadingList);
            }
        }
    }

    /**
     * ------------下载业务逻辑    结束---------------
     */

    public void addActivity(Activity activity) {
        if (activity != null && activity != getCurrentActivity()) {
            activityList.add(activity);
        }
    }

    public void removeActivty(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
        }
    }

    public void exitAllActivity() {
        for (Activity activity : activityList) {
            if (activity != null) {

                activity.finish();
            }
        }
        activityList.clear();
    }

    /**
     * 获取当前的activity
     *
     * @return
     */
    public Activity getCurrentActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }

    /**
     * 获取主activity
     *
     * @return
     */
    public Activity getMainActivity() {
        for (Activity activity : activityList) {
            if (activity instanceof MainActivityGroup) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 设置电池状态
     * @param status
     */
    public void setBatteryStatus(int status){
        this.batteryStatus = status;
    }

    public int getBatteryStatus(){
        return batteryStatus;
    }

    /**
     * 设置电池电量
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    /**
     * 获取电池电量 0 - 100
     */
    public int getBatteryLevel() {
        if (batteryScale == 0) {
            return 0;
        }
        return batteryLevel * 100 / batteryScale;
    }

    /**
     * 设置电池最大电量
     */
    public void setBatteryScale(int batteryScale) {
        this.batteryScale = batteryScale;
    }

    /**
     * 设置电池温度
     */
    public void setBatteryTemperature(int batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    /**
     * 获取电池温度
     */
    public String getBatteryTemperature() {
        int tens = batteryTemperature / 10;
        return Integer.toString(tens) + "." + (batteryTemperature - 10 * tens);
    }

    /**
     * true电池温度过高，发出警告，false不用发出警告
     */
    public boolean isBatteryTemperatureWarning() {
        if (batteryTemperature > 600) {
            return true;
        }
        return false;
    }

    /**
     * false没有调过UnityActivity类的startAndroidMainActivity()方法，true调过
     *
     * @param firstStartAndroidMainActivity
     */
//    public void setFirstStartAndroidMainActivity(boolean firstStartAndroidMainActivity) {
//        this.firstStartAndroidMainActivity = firstStartAndroidMainActivity;
//    }

    /**
     * false没有调过UnityActivity类的startAndroidMainActivity()方法，true调过
     */
//    public boolean getFirstStartAndroidMainActivity() {
//        return firstStartAndroidMainActivity;
//    }
    public HashMap<String, HashMap<String, String>> getLanguageMap() {
        return LanguageMap;
    }

    public void setLanguageMap(HashMap<String, HashMap<String, String>> languageMap) {
        LanguageMap = languageMap;
    }

    public HashMap<String, HashMap<String, String>> getServerUrlMap() {
        return serverUrlMap;
    }

    public void setServerUrlMap(HashMap<String, HashMap<String, String>> serverUrlMap) {
        this.serverUrlMap = serverUrlMap;
    }

    //维护全局的交互层级关系
    public List<HierarchyBean> hierarchyBeanList = new ArrayList<HierarchyBean>();

    public void addHierarchy(HierarchyBean bean) {
        if (bean != null) {
            hierarchyBeanList.add(bean);
        }
    }

    public void removeHierarchy(HierarchyBean bean) {
        if (bean != null) {
            hierarchyBeanList.remove(bean);
        }
    }

    /* ------------判断在横屏还是竖屏--------------- */
    private boolean isPortraitOrLandscape = true; // ture:竖屏，false横屏

    public boolean getOrientationMode() {
        return isPortraitOrLandscape;
    }

    public void setOrientationMode(boolean flag) {
        isPortraitOrLandscape = flag;
        System.out.println("zl->setOrientationMode:"+flag);
    }

    public void libSOInit() {
        PlayCheckUtil.checkLibs(this, new LibraryUtils.OnLibraryInitListener() {
            @Override
            public void onLibraryInitResult(boolean result) {
                // 解压SO库的结果,如果解压失败,软解无法播放.解压失败的原因一般是内置存储空间不足;
//                LogHelper.d(TAG, "onLibraryInitResult result = " + result );
            }
        });
    }

    /**
     * 友盟服务
     */
    private void umengService() {
        try {
            Intent serviceIntent = FeedbackService.getIntent();
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            serviceIntent.setPackage(getPackageName());
            startService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理app数据
     */
    public void dealAppData() {
        int mojingApp = ConfigConstant.getMojingApp();
        int mojingVersionCode = ApkUtil.getVersionCode();//魔镜app当前版本号
        if (ConfigConstant.MOJING_PRO == mojingApp) {//魔镜pro
            String fileName = ".savedVersionCode";//保存的版本号
            if (mojingVersionCode != FileStorageUtil.getSavedVersionCode(fileName)) {//魔镜app当前版本号，不等于保存的版本号
                FileStorageUtil.setSavedVersionCode(mojingVersionCode, fileName);
                FileCommonUtil.cleanAppCache(null);//清除app缓存
            }
        } else if (ConfigConstant.MOJING_MINI == mojingApp) {//魔镜mini
            String fileName = ".savedMiniVersionCode";//保存的版本号
            if (mojingVersionCode != FileStorageUtil.getSavedVersionCode(fileName)) {//魔镜app当前版本号，不等于保存的版本号
                FileStorageUtil.setSavedVersionCode(mojingVersionCode, fileName);
                FileCommonUtil.cleanAppCache(null);//清除app缓存
            }
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
//        batteryStateReceiver = new BatteryStateReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction(Intent.ACTION_POWER_CONNECTED);
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        registerReceiver(batteryStateReceiver, filter);

//        registerNetWorkReceiver();

        IntentFilter externalStorageFilter = new IntentFilter();
        externalStorageFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        externalStorageFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        externalStorageFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        externalStorageFilter.setPriority(1000);
        externalStorageFilter.addDataScheme("file");
        externalStorageReceiver = new ExternalStorageReceiver();
        registerReceiver(externalStorageReceiver, externalStorageFilter);

//        IntentFilter usbDeviceFilter = new IntentFilter();
//        usbDeviceFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        usbDeviceFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        usbDeviceReceiver = new UsbDeviceReceiver();
//        registerReceiver(usbDeviceReceiver, usbDeviceFilter);

        mHomeKeyEventReceiver = new HomeKeyEventReceiver();
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));



    }

    public void registerBlackScreenBroadcast(){
        if(null == mBlackScreenReceiver){
            mBlackScreenReceiver = new BlankScreenReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PRE_SCREEN_OFF");
        registerReceiver(mBlackScreenReceiver,intentFilter);
    }

    public void unRegisterBlackScreenBroadcast(){
        if(null != mBlackScreenReceiver){
            unregisterReceiver(mBlackScreenReceiver);
        }

    }
    public void registerNetWorkReceiver() {
        initDownloadInfo();
        networkChangedReceiver = new NetworkChangeReceiver(new NetworkChangeReceiver.NetworkChangeListener() {
            @Override
            public void networkChange(int currentNetwork) {
                //添加检测连接网络后从网络上获取镜片信息 add by whf 20161220
                if (NetworkUtil.isNetworkConnected(INSTANCE)) {//网络已连接
                    if (System.currentTimeMillis() - time < 500) {
                       return;
                    }
                    time = System.currentTimeMillis();
                    GlassesUtils.setDefaultGlasses(true);

                    DownloadUtils.getInstance().mIsInit = true;
                    if (NetworkUtil.canPlayAndDownload()) {
                        if (System.currentTimeMillis() - time > 500) {
                            time = System.currentTimeMillis();
                            PluginDownloadBusiness.getmInstance().updateDownload();
                            DownloadUtils.getInstance().startAllDownload(BaseApplication.INSTANCE);
                            LogHelper.d("infossss", "==================================");
                        }
                    } else {
                        DownloadUtils.getInstance().changePauseReason(BaseApplication.INSTANCE);
                        PluginDownloadBusiness.getmInstance().stopDownload();
                    }
                } else {
                    DownloadUtils.getInstance().changePauseReason(BaseApplication.INSTANCE);
                    PluginDownloadBusiness.getmInstance().stopDownload();
                }
                currentNetwork = NetworkUtil.convertNetwork(currentNetwork);//转换网络
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendNetworkChange(currentNetwork);
                    }
                }
            }

            @Override
            public void wifiLevelChange(int wifiLevel) {
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendWifiLevelChange(wifiLevel);//0忽略；1，2，3,4对应信号强度
                    }
                }
            }

            @Override
            public void wifiEnabled(int enabled) {
                LogHelper.d("infos","enabled====="+enabled);
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendWifiEnabled(enabled);//1已关闭3已打开
                    }
                }
            }

        });
        IntentFilter networkChangedIntentFilter = new IntentFilter();
        networkChangedIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangedIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        networkChangedIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(networkChangedReceiver, networkChangedIntentFilter);


    }

    public void registerBatteryBroadcast(){
        batteryStateReceiver = new BatteryStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(batteryStateReceiver, filter);


        mAudioReceiver = new AudioReceiver();
        IntentFilter audioIntentFilter = new IntentFilter();
        audioIntentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mAudioReceiver,audioIntentFilter);

        mCloseReceiver = new CloseOrOpenReceiver();
        IntentFilter closeIntentFilter = new IntentFilter();
        closeIntentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        closeIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(mCloseReceiver,closeIntentFilter);

    }

    /**
     * 取消广播
     */
    public void unregisterReceiver() {
        unregisterReceiver(batteryStateReceiver);
        unregisterReceiver(networkChangedReceiver);
        unregisterReceiver(externalStorageReceiver);
        //unregisterReceiver(usbDeviceReceiver);
        unregisterReceiver(mHomeKeyEventReceiver);
    }

    public boolean isBFMJ5Connection() {
        return isBFMJ5Connection;
    }

    public int getSendTracker() {
        return isSendTracker;
    }

    public void setSendTracker(int value) {
        isSendTracker = value;
    }

    /*
    * 转到横屏
     */
    public void toLandscape() {
        LogHelper.d(TAG,"zl->Android -- toLandscape");
        Intent intent = new Intent(getApplicationContext(), UnityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /**
         * 只有播放才需要传参，其他方式不传，此处注释掉 -- by liuchuanchi
         */
//        String str = JSON.toJSONString(hierarchyBeanList, SerializerFeature.DisableCircularReferenceDetect);
//        intent.putExtra("hierarchy", str);
        startActivity(intent);

    }

    public void addExternalStorageNotify(ExternalStorageReceiver.ExternalStorageNotify externalStorageNotify) {
        if (externalStorageReceiver != null) {
            externalStorageReceiver.addExternalStorageNotify(externalStorageNotify);
        }
    }

    public void removeExternalStorageNotify() {
        if (externalStorageReceiver != null) {
            externalStorageReceiver.removeExternalStorageNotify();
        }
    }

    public String getJoystickName() {
        return joystickName;
    }

    public void setJoystickName(String joystickName) {
        this.joystickName = joystickName;
    }

    public boolean isEnableJoystickReport() {
        return enableJoystickReport;
    }

    public void setEnableJoystickReport(boolean enableJoystickReport) {
        this.enableJoystickReport = enableJoystickReport;
    }

    public int getInputDeviceType(){
        return mInputDeviceType;
    }

    public void setInputDeviceType(int type){
        mInputDeviceType = type;
    }
    public boolean isEnableToLandscapeCondition1() {
        return enableToLandscapeCondition1;
    }

    public void setEnableToLandscapeCondition1(boolean enableToLandscapeCondition1) {
        this.enableToLandscapeCondition1 = enableToLandscapeCondition1;
    }

    public boolean isEnableToLandscapeCondition2() {
        return enableToLandscapeCondition2;
    }

    public void setEnableToLandscapeCondition2(boolean enableToLandscapeCondition2) {
        this.enableToLandscapeCondition2 = enableToLandscapeCondition2;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        MultiDex.install(base);
    }

    public DownloadConfig getDownloadConfig() {
        return downloadConfig;
    }


    public boolean isAppInstalled(String packagename)
    {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo == null){
            return false;
        }else{
            return true;
        }
    }

    public void setAppUddateBusiness(AppUpdateBusiness bean){
        mAppUpdateBusiness = bean;
    }


    public AppUpdateBusiness getAppUpdateBusiness(){
        return mAppUpdateBusiness;
    }





}
