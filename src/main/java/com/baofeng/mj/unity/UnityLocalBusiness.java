package com.baofeng.mj.unity;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.baofeng.mj.bean.DeviceInfo;
import com.baofeng.mj.bean.LocalVideoBean;
import com.baofeng.mj.business.localbusiness.LocalVideoBusiness;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.business.localbusiness.flyscreen.interfaces.FlyScreenInterface;
import com.baofeng.mj.business.localbusiness.flyscreen.util.FlyScreenUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.entityutil.CreateLocalVideoUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.ImageUtil;
import com.baofeng.mj.util.threadutil.LocalVideoProxy;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.util.threadutil.ThreadPoolUtil;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author liuchuanchi
 * @description: Unity本地业务类
 */
public class UnityLocalBusiness {
    /**
     * 获取本地视频数据
     */
    public static void getLocalVideoData() {
        getLocalVideoData(0);
    }


    public static void breakVideoThumbnail(){
        LocalVideoBusiness.getInstance().setBreakThumbnail(true);
    }

    /**
     * 获取本地视频数据
     * @param sortRule 排序规则，0文件上次修改时间排序，1文件名排序，2文件大小排序
     */
    public static void getLocalVideoData(final int sortRule) {
        LogHelper.e("infos","=======getLocalVideoData=========");
        LocalVideoProxy.getInstance().addProxyRunnable(new LocalVideoProxy.ProxyRunnable() {
            @Override
            public void run() {
                LocalVideoBusiness.getInstance().searchLocalVideo(sortRule, new LocalVideoBusiness.LocalVideoDataCallback() {
                    @Override
                    public void callback(TreeMap localVideoMap) {
                        if(UnityActivity.INSTANCE != null){
                            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                            if (iAndroidCallback != null) {//通知Unity
                                if (localVideoMap == null || localVideoMap.size() == 0) {
                                    LogHelper.e("infossss","jsonarry======null");
                                    iAndroidCallback.sendLocalVideoJSONArray("");
                                } else {
                                    JSONArray localVideoJSONArray = CreateLocalVideoUtil.createLocalVideoJSONArray(localVideoMap);
                                    LogHelper.e("infossss","jsonarry=="+localVideoJSONArray.toString());
                                    iAndroidCallback.sendLocalVideoJSONArray(localVideoJSONArray.toString());

                                }
                            }
                        }
                    }
                },true);
            }
        });
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return true删除成功，false删除失败
     */
    public static boolean deleteFile(String filePath){
        if(FileCommonUtil.deleteFile(filePath)){//删除文件成功
            File file = new File(filePath);
            LogHelper.e("infos","file.exists=="+file.exists()+"file.directory=="+file.isDirectory());
            boolean isFile = true;
            if(file.exists() && file.isDirectory()){
                isFile = false;
            }
            FileCommonUtil.scanFile(isFile,file);
            LogHelper.e("infos","=====true====");
            return true;
        }
        LogHelper.e("infos","=====false====");
        return false;
    }

    //----------------------飞屏start---------------------
    /**
     * 初始化设备列表
     *
     * @return void
     * @author linzanxian  @Date 2015-8-18 下午5:11:12
     */
    public static void flyScreenInit() {
        //FlyScreenInterface.getInstance(BaseApplication.INSTANCE).init();
        FlyScreenBusiness.getInstance().init(BaseApplication.INSTANCE);
        FlyScreenBusiness.getInstance().setTcpReceiver(true);
    }

    /**
     * 刷新设备列表
     *
     * @return void
     * @author linzanxian  @Date 2015-8-18 下午5:13:33
     */
    public static void freshDevlistClick() {
        //FlyScreenInterface.getInstance(BaseApplication.INSTANCE).freshDevlistClick();
        FlyScreenBusiness.getInstance().startScan();
    }
    /*
    * 获取设备URL
    * @return 设备URL
     */
    public static String getDeviceURL(){
        String url =  "http://" +FlyScreenBusiness.getInstance().getCurrDevInfo().getIp() + ":" + FlyScreenBusiness.getInstance().getCurrentDeviceServerPort();
        return url;
    }
    /**
     * 获取设备资源列表
     *
     * @param id
     * @return void
     * @author linzanxian  @Date 2015-8-18 下午5:18:14
     */
    public static void getDeviceResourceList(String id, boolean fresh) {
        //FlyScreenInterface.getInstance(BaseApplication.INSTANCE).getDeviceResourceList(index, fresh);
        DeviceInfo deviceInfo = null;

        List<DeviceInfo> deviceInfos = FlyScreenBusiness.getInstance().getmDeviceInfos();
        for (DeviceInfo device:deviceInfos) {
            if(device.getId().equals(id))
            {
                deviceInfo = device;
                break;
            }
        }
        LogHelper.e("infosss","deviceInfo==="+deviceInfo);
        if(deviceInfo != null)
            FlyScreenBusiness.getInstance().requestLoginData(deviceInfo);
        else
            System.err.println("zl->No device");
    }

//    public static void getDeviceResourceList(int index) {
//        FlyScreenInterface.getInstance(BaseApplication.INSTANCE).getDeviceResourceList(index, false);
//    }
    /*
    * 重连飞屏
     */
    public static void checkSokect(String ip)
    {
        FlyScreenBusiness.getInstance().reConnect();
    }

    /**
     * 进入目录
     *
     * @param dirUri 目录地址
     * @return void
     * @author linzanxian  @Date 2015-8-18 下午5:18:49
     */
    public static void forwardDir(String dirUri) {
        //FlyScreenInterface.getInstance(BaseApplication.INSTANCE).forwardDir(dirUri);
        FlyScreenBusiness.getInstance().forwardDirectory(dirUri);
    }

    /**
     * 返回进入
     *
     * @return void
     * @author linzanxian  @Date 2015-8-18 下午5:19:18
     */
    public static void backDir() {
        //FlyScreenInterface.getInstance(BaseApplication.INSTANCE).backDir();
        FlyScreenBusiness.getInstance().backToParentDir();
    }
    public static boolean getCurrentDirectory(){
        return FlyScreenBusiness.getInstance().getCurrentDirectory();
    }

    /**
     *   获取缓存好的飞屏字幕文件路径，播放时加载字幕使用  （u3d调用）
     * @param filePath 本地字幕路径
     * @return
     */
    public static String[] getSubtitleList(String filePath){
        List<String> strings = FlyScreenUtil.getSubtitleList(filePath);
        if(strings==null||strings.size()==0){
            return new String[0];
        }
        String[] arry = (String[])strings.toArray(new String[strings.size()]);
         return arry;
    }

    //--------飞屏callback begin-------
    public static void sendFlyScreenDeviceList(String deviceList)
    {
        LogHelper.i("infos","111111111111111111111111"+deviceList);
        if(UnityActivity.INSTANCE != null){
            LogHelper.i("infos","22222222222222222222222222");
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                LogHelper.i("infos","333333333333333");
                iAndroidCallback.sendFlyScreenDeviceList(deviceList);
                LogHelper.i("infos","44444444444444444444");
            }
        }
    }

    public static void sendFlyScreenDeviceResourceList(String VideoList)
    {
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                iAndroidCallback.sendFlyScreenDeviceResourceList(VideoList);
            }
        }
    }

    public static void sendFlyScreenServerPort(int port)
    {
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            if(iAndroidCallback != null){//通知Unity
                iAndroidCallback.sendFlyScreenServerPort(port);
            }
        }
    }
    public static void sendFlyScreenException(int code){
        LogHelper.e("infosss","=======sendFlyScreenException1111=========="+UnityActivity.INSTANCE );
        if(UnityActivity.INSTANCE != null){
            IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
            LogHelper.e("infosss","=======sendFlyScreenException22222=========="+iAndroidCallback);
            if(iAndroidCallback != null){//通知Unity
                iAndroidCallback.sendFlyScreenException(code);
                LogHelper.e("infosss","=======sendFlyScreenException3333==========");
            }
        }
    }
    //--------飞屏callback end-------
    //----------------------飞屏end-----------------------

    /**
     * 获取视频类型
     * @param videoPath 视频地址
     */
    public static void getVideoType(final String videoPath){
        VideoTypeUtil.getVideoType(videoPath, new VideoTypeUtil.VideoTypeCallback() {
            @Override
            public void result(int videoType) {
                if(UnityActivity.INSTANCE != null){
                    IAndroidCallback iAndroidCallback = UnityActivity.INSTANCE.getIAndroidCallback();
                    if(iAndroidCallback != null) {//通知Unity
                        iAndroidCallback.sendGetVideoTypeCompleted(videoType);//发送获取视频类型成功
                    }
                }
            }
        });
    }

    public static void getLocalVideoDataNoCall(final int sortRule) {
        LogHelper.e("infosssss","--------getLocalVideoDataNoCall-------------");
        LocalVideoProxy.getInstance().addProxyRunnable(new LocalVideoProxy.ProxyRunnable() {
            @Override
            public void run() {
                LocalVideoBusiness.getInstance().searchLocalVideo(sortRule);
            }
        });
    }

}
