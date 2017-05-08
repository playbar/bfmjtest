package com.baofeng.mj.unity;

import android.app.Activity;
import android.os.Handler;
import android.os.RemoteException;

import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mojing.MojingSDKServiceManager;
import com.baofeng.mojing.service.MojingSDKAIDLService;

import java.io.File;

import bf.cloud.android.modules.p2p.P2pImp;

/**
 * Created by zhanglei1 on 2016/8/4.
 */
public class UnityFirewareBusiness {
    private static boolean  isContinue = false;
    private static float lastProgress = 0.0F;
    private static MojingSDKAIDLService mService;
    // 更新进度
    public static boolean getUpgradeProgress(int type)
    {
        boolean isStatus = true;
        try {
            isStatus = mService.isUpgrading();
            if (isStatus) {
                if(UnityActivity.INSTANCE != null){
                    final IAndroidCallback callback = UnityActivity.INSTANCE.getIAndroidCallback();
                    float progress = mService.getUpgradeProgress();
                    //System.out.println("zl->updateFireware:progress1:" + progress);
                    if(Math.abs(progress - 1.0F)<0.0001F)
                    {
                        callback.sendFirewareUpdateProgress(type, progress);
                        lastProgress = 0.0F;
                        return false;
                    }
                    if (progress - lastProgress > 0.01F) {
                        lastProgress = progress;
                        //System.out.println("zl->updateFireware:progress2:" + progress);
                        callback.sendFirewareUpdateProgress(type, progress);
                    }
                }
            }
            else
            {
                lastProgress = 0.0F;
            }
        }
        catch (RemoteException e){
            e.printStackTrace();
        }
        return isStatus;
    }
    // 更新固件
    public static void updateFireware(final boolean flag, final int type){
        if(flag) {
            mService = MojingSDKServiceManager.getService();
            String path = FirewareBusiness.getInstance().getFirewareCachePath(type);
            try {
                if (mService.startUpgrade(path)) {
                    if(mService.isUpgrading()){
                        if(UnityActivity.INSTANCE != null){
                            final IAndroidCallback callback = UnityActivity.INSTANCE.getIAndroidCallback();
                            float progress = mService.getUpgradeProgress();
                            System.out.println("zl->updateFireware:progress" + progress);
                            //if (progress - lastProgress > 0.01F) {
                            //    lastProgress = progress;
                            //   System.out.println("zl->updateFireware:progress" + progress);
                            callback.sendFirewareUpdateProgress(type, progress);
                            //   }
                        }
                    }
                }
            }catch (RemoteException e) {
                e.printStackTrace();
            }

//                    if (callback == null)
//                        return;


//            try {
//                if (mService.startUpgrade(path)) {
//                    final IAndroidCallback callback = BaseApplication.INSTANCE.getIAndroidCallback();
//                    if (callback == null)
//                        return;
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            do {
//                                try {
//                                    progress = mService.getUpgradeProgress();
//                                    if (progress - lastProgress > 0.01F) {
//                                        lastProgress = progress;
//                                        Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
//                                        if (currrentActivity instanceof UnityActivity) {
//                                            currrentActivity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    callback.sendFirewareUpdateProgress(type, progress);
//                                                }
//                                            });
//                                        }
//                                    }
//                                    try {
//                                        Thread.sleep(100);
//                                        isContinue = mService.isUpgrading();
//
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                } catch (RemoteException e) {
//                                    e.printStackTrace();
//                                }
//                            } while (isContinue);
//                        }
//                    });
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
        else{
        }
    }

    //检查固件升级
    public static void checkFirewareUpdate()
    {
        Activity currrentActivity = BaseApplication.INSTANCE.getCurrentActivity();
        if(currrentActivity instanceof UnityActivity) {
            currrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FirewareBusiness.getInstance().checkUpdate();
                }
            });
        }
    }
    //
    public static void recordFirewareVersion()
    {
        SettingSpBusiness.getInstance().setFirewareVersionCode(FirewareBusiness.getInstance().getRequestData().getFirewareType(),FirewareBusiness.getInstance().getRequestData().getVersionCode());
    }
    public static int getFirewareVersion(int type)
    {
        int version = -1;
        switch (type){
            case FirewareBusiness.FIREWARE_TYPE_BLE:
                version = FirewareBusiness.getInstance().getBleVersionCode();
                break;
                case FirewareBusiness.FIREWARE_TYPE_MCU:
                    version = FirewareBusiness.getInstance().getMcuVersionCode();
                    break;
                default:break;
        }
        return version;

    }
    public static String getJoystickName(){
        return BaseApplication.INSTANCE.getJoystickName();
    }
    public static boolean getEnableJoystickReport(){
        return BaseApplication.INSTANCE.isEnableJoystickReport();
    }
    public static void setEnableJoystickReport(boolean flag){
        BaseApplication.INSTANCE.setEnableJoystickReport(flag);
    }
    public  static  int getPCIType(){
        return FirewareBusiness.getInstance().getCurrentPCIType();
    }
}
