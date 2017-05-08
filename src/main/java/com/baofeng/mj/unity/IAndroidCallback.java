package com.baofeng.mj.unity;

/**
 * Created by zhanglei1 on 2016/5/12.
 */
public interface IAndroidCallback {
    void sendLocalVideoJSONArray(String localVideoJSONArray);//发送本地视频json

    void sendDownloadingJSONArray(String downloadingJSONArray);//发送下载中json（资源信息）

    void sendDownloadedJSONArray(String downloadedJSONArray);//发送已下载json（资源信息）

    void sendDownloadCompleted(String downloadJSONObject);//发送下载完成json（资源信息）

    void sendHistoryJSONObject(String historyJSONObject);//发送历史json

    void sendDownloadDeleted(String downloadJSONObject);//发送删除item
    //void sendHistoryJson(String historyJson);//发送历史json

    void sendCleanAppCacheResult(boolean cleanResult);//发送清除app缓存结果

    void sendLocalVideoThumbnailCompleted(String videoPath);//发送本地视频缩略图创建完成

    void sendGetVideoTypeCompleted(int videoType);//发送获取视频类型成功

    void sendNetworkChange(int network);//发送网络改变

    void sendWifiLevelChange(int wifiLevel);//发送WiFi信号强度改变

    void sendWifiEnabled(int enabled);//发送wifi打开关闭状态

    void sendBatteryLevel(int batteryLevel);//发送电池电量

    void sendBatteryChanged(int status,int batteryLevel,int batterySum);//status==1未知(一般表示状态切换  无法获取电量 使用之前值);2充电;3放电;4没充电;5充满电;

    void sendBatteryTemperatureWarning(boolean temperatureWarning);//发送电池温度警告

    void sendGlassTrackerStatus(boolean status); //发送魔镜5代USB接口连接状态

    void sendLowPowerStatus(boolean status); //发送光感传感器

    void sendJoystickStatus(boolean status); //发送魔镜5代连接状态

    void sendInputDeviceType(int value);

    void sendBlueToothPhoneState(boolean status);//发送蓝牙耳机接听电话状态，true：蓝牙耳机连接中并处于通话状态，false:其他，例如：蓝牙耳机未连接或通话挂断

    void sendFirewareUpdate(int firewareType, int upgradeType); // 发送固件升级 firewareType:1:MCU,2:BLE

    void sendFirewareUpdateProgress(int type, float progress);// 发送对应固件升级进度

    void sendUsbDeviceState(boolean status);//发送usb设备状态，true：插入，false：移除

    void sendHMDUpgradeResult(boolean flag);// 发送HMD升级结果，true,升级成功，false,升级失败

    void sendFirewareVersion(int mcuVersion, int bleVersion);// 发送固件版本号

    void sendFirewareUploadComplete();// 发送固件上传完成

    void sendIfPayed(int status,String resId);//发送是否购买，0已购买，1未购买，2请求失败

    void sendPayStatus(int status,String resId);//发送购买结果，0购买成功，1资源已订购，2魔豆余额不足，3魔币余额不足，4购买失败

    void sendFlyScreenDeviceList(String deviceList);// 发送飞屏设备列表

    void sendFlyScreenDeviceResourceList(String VideoList);// 发送设备上的视频列表

    void sendFlyScreenServerPort(int port); // 发送服务端口号
    void sendFlyScreenException(int code);  // 发送飞屏异常
    void sendIatResult(String iatResult);//发送语音转义结果
    void sendIatError(int iatErrorCode);//发送语音转义错误
    void sendIatBegin();//发送语音转义开始
    void sendIatEnd();//发送语音转义结束
    void sendIatVolumeAndData(int volume, String data); //volume音量值0~30,data音频数据

    void sendApkInstallResult(String packageName,int status); //静默安装返回码 1成功 -1失败
    void sendApkUninstallResult(String packageName, int status);//静默卸载返回码

    void sendHomeIsPress(int value);//短按1，长按2

    void sendLocalVideoThumbnailEnd();//本地视频缩略图加载完成后通知u3d

    void sendCurrentVolume(int value);//发送当前音量

    void sendClearOtherApp(int count,long size);//清理数量，空间(M)


    void sendLoginMsg(int status,String msg);// 1 登录成功  -1 登录失败

    void sendLogout(int status);//1 退出成功

    void sendBlankScreen();//表示接收到这个黑屏广播

    void sendAudioBecoming();//插入HDMI监听

    void sendPlayLocalMovie(String json); //播放本地视频
}