package com.baofeng.mj.business.firewarebusiness;

import android.os.RemoteException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baofeng.mj.bean.FirewareUpdateBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.unity.IAndroidCallback;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.FirewareUpdateApi;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mojing.MojingSDKServiceManager;
import com.baofeng.mojing.service.MojingSDKAIDLService;
import com.mojing.dl.domain.DownloadItem;

/**
 * Created by zhanglei1 on 2016/8/3.
 */
public class FirewareBusiness {
    private FirewareBusiness(){}
    private static FirewareBusiness instance = null;
    public static FirewareBusiness getInstance(){
        if(instance == null){
            instance = new FirewareBusiness();
        }
        return instance;
    }
    public final static int FIREWARE_TYPE_MCU = 1;
    public final static int FIREWARE_TYPE_BLE = 2;
    private final int PCI_TYPE_NULL = 0;
    private final int PCI_TYPE_BFMJ5 = 1;
    private final int PCI_TYPE_HZN = 2;
    private int currentPCIType = PCI_TYPE_NULL;
    public int getMcuVersionCode() {
        return mcuVersionCode;
    }

    public void setMcuVersionCode(int mcuVersionCode) {
        this.mcuVersionCode = mcuVersionCode;
    }

    public int getBleVersionCode() {
        return bleVersionCode;
    }

    public void setBleVersionCode(int bleVersionCode) {
        this.bleVersionCode = bleVersionCode;
    }

    private int mcuVersionCode = -1;
    private int bleVersionCode = -1;
    private int currentUpgradeType;
    private int currentUpgradeMode;

    public FirewareUpgradeRequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(FirewareUpgradeRequestData requestData) {
        this.requestData = requestData;
    }

    private FirewareUpgradeRequestData requestData;
    public int getCurrentUpgradeType() {
        return currentUpgradeType;
    }

    public void setCurrentUpgradeType(int currentUpgradeType) {
        this.currentUpgradeType = currentUpgradeType;
    }

    public int getCurrentUpgradeMode() {
        return currentUpgradeMode;
    }

    public void setCurrentUpgradeMode(int currentUpgradeMode) {
        this.currentUpgradeMode = currentUpgradeMode;
    }

    private void setCurrentPCIType(FirewareData data) {
        if(data.vid == 11561 && data.pid == 4097){
            currentPCIType = PCI_TYPE_BFMJ5;
        }
        else if(data.vid == 44113 && data.pid == 4417){
            currentPCIType = PCI_TYPE_HZN;
        }
        else
            currentPCIType = PCI_TYPE_NULL;
    }

    public int getCurrentPCIType() {
        return currentPCIType;
    }

    public class FirewareData {
        public int pid;
        public int vid;
        public int mcu;
        public int ble;
    }

    public class FirewareUpgradeRequestData
    {
        private int firewareType;
        private int versionCode;
        private int upgradeMode;
        public FirewareUpgradeRequestData(int firewareType,int versionCode,int upgradeMode)
        {
            this.firewareType = firewareType;
            this.versionCode = versionCode;
            this.upgradeMode = upgradeMode;
        }

        public int getFirewareType() {
            return firewareType;
        }

        public int getUpgradeMode() {
            return upgradeMode;
        }
        public int getVersionCode() {
            return versionCode;
        }
    }
    //获取固件信息
    private FirewareData getFirewareInfo() {
        MojingSDKAIDLService mojingSDKAIDLService = MojingSDKServiceManager.getService();
        if(mojingSDKAIDLService == null) {
            return null;
        }
        FirewareData data = new FirewareData();
        try {
            data.mcu = mojingSDKAIDLService.getMCUVersion();
            data.ble = mojingSDKAIDLService.getBLEVersion();
            data.vid = mojingSDKAIDLService.getVID();// 11561;
            data.pid = mojingSDKAIDLService.getPID();//4097;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return data;
    }
    //检查更新
    public void checkUpdate() {
        System.out.println("zl->Android CheckUpdate Start...");
        FirewareData data = getFirewareInfo();
        System.out.println(String.format("zl->Android CheckUpdate -> BLE:%d, MCU:%d, PID:%d, VID:%d", data.ble, data.mcu, data.pid, data.vid));
        setCurrentPCIType(data);
        System.out.println(String.format("zl->Android CheckUpdate -> Current PCIType:%s", currentPCIType));
        UpdateFirewareMCU(data);
        System.out.println("zl->Android CheckUpdate End.");
    }
    private boolean checkDownload(int type,String value)
    {
        boolean isUpdateFireware = true;
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        try {
            FirewareUpdateBean statusData = JSON.parseObject(value,FirewareUpdateBean.class);
            System.out.println(String.format("zl->Android CheckDownload Parse HttpResponse:%b", statusData != null && statusData.isStatus() && statusData.getData().size() != 0));
            if (statusData != null) {
                if (statusData.isStatus()) {
                    if (statusData.getData().size() != 0) {
                        int version = -1;
                        FirewareUpdateBean.FirewareUpdateData updateData = null;
                        for (int i = 0; i < statusData.getData().size(); i++) {
                            int newVersion = statusData.getData().get(i).getSjbbbh();
                            if (newVersion > version) {
                                version = newVersion;
                                updateData = statusData.getData().get(i);
                            }
                        }
                        if(updateData.getSjbbbh() <= SettingSpBusiness.getInstance().getFirewareVersionCode(type)) {
                            System.out.println(String.format("zl->Android CheckDownload updateData.getSjbbbh():%d <= getFirewareVersionCode(%s):%d", updateData.getSjbbbh(), type, SettingSpBusiness.getInstance().getFirewareVersionCode(type)));
                            return true;
                        }
                        if (!TextUtils.isEmpty(updateData.getXzdz())) {
                            downloadNewVersion(type, updateData.getXzdz());
                            isUpdateFireware = false;
                            setRequestData(new FirewareUpgradeRequestData(type,updateData.getSjbbbh(),updateData.getSjfs()));
                            setCurrentUpgradeMode(updateData.getSjfs());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(String.format("zl->Android CheckDownload(%s) return %b", type, isUpdateFireware));
        return isUpdateFireware;
    }
    // 下载新的固件版本
    private void downloadNewVersion(int type, String downloadURL){
        System.out.println(String.format("zl->Android Fireware(%d) Download New Version(%s) Start...", type, downloadURL));
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItemForFireware(type, downloadURL);
        DemoUtils.startDownload(BaseApplication.INSTANCE, downloadItem);//开始下载
    }
    // 通知更新请求
    public void notifyUpdate(int type){
        System.out.println(String.format("zl->Android Download New Verson(%d) Finished, Call Unity...", type));
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback callback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(callback != null){
                callback.sendFirewareUpdate(type,requestData.getUpgradeMode());
            }
        }
    }
    public String getFirewareCachePath(int type)
    {
        String path = "";
        switch (type){
            case FIREWARE_TYPE_MCU:
                path = FileStorageUtil.getExternalMojingFileDir()+  DownloadItemUtil.DOWNLOAD_TITLE_FRIEWARE_MCU;
                break;
            case FIREWARE_TYPE_BLE:
                path = FileStorageUtil.getExternalMojingFileDir()+ DownloadItemUtil.DOWNLOAD_TITLE_FRIEWARE_BLE;
                break;
            default:break;
        }
        return path;
    }

    private  void UpdateFirewareMCU(FirewareData data)
    {
        new FirewareUpdateApi().checkFirewareUpdate(FIREWARE_TYPE_MCU, data, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                System.out.println(String.format("zl->Android UpdateFirewareMCU Success:%s", result));
                if (checkDownload(FIREWARE_TYPE_MCU, result)) {
                    updateFirewareBLE();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                System.out.println(String.format("zl->Android UpdateFirewareMCU Failed:%s", content));
                updateFirewareBLE();
            }
        });
    }
    private  void updateFirewareBLE()
    {
        System.out.println("zl->Android Update Firewar BLE Start...");
        FirewareData data = getFirewareInfo();
        System.out.println(String.format("zl->Android UpdateFirewareBLE -> BLE:%d, MCU:%d, PID:%d, VID:%d", data.ble, data.mcu, data.pid, data.vid));
        new FirewareUpdateApi().checkFirewareUpdate(FIREWARE_TYPE_BLE, data, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                System.out.println(String.format("zl->Android UpdateFirewareBLE Success:%s", result));
                checkDownload(FIREWARE_TYPE_BLE, result);
            }
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                System.out.println(String.format("zl->Android UpdateFirewareBLE Failed:%s", content));
            }
        });
    }
}
