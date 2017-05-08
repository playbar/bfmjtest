package com.baofeng.mj.util.viewutil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baofeng.mj.bean.HierarchyBean;
import com.baofeng.mj.bean.LandscapeUrlBean;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.baofeng.mj.ui.activity.GoUnity;
import com.baofeng.mj.ui.activity.SearchActivity;
import com.baofeng.mj.ui.activity.SettingActivity;
import com.baofeng.mj.ui.dialog.OpenGprsDialog;
import com.baofeng.mj.ui.online.utils.PlayerModeChooseSubject;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.publicutil.SubTypeUtil;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;

import static com.baofeng.mj.util.publicutil.VideoTypeUtil.MJVideoPictureTypeSingle;

/** 跳转指定页面
 * Created by muyu on 2016/6/12.
 */
public class StartActivityHelper {
    private static final String TAG = StartActivityHelper.class.getSimpleName();

    /**
     * 本地资源来自哪里
     */
    public static final String resource_from_download = "1";//资源来自下载
    public static final String resource_from_local = "2";//资源来自本地
    public static final String resource_from_explorer = "3";//资源来自手机上的资源管理器
    public static final String resource_from_net = "4";//资源来自网络
    public static final String resource_from_flyscreen = "5";//资源来自飞屏

    /**
     * 在线资源来自哪里
     */
    public static final String online_resource_from_default = "1";//在线资源来自（默认值）
    public static final String online_resource_from_history = "2";//在线资源来自历史记录列表

    /**
     * 跳转GoUnity页面,type:-1, subType:23,28,29
     * (type为服务器传回类型，分为4视频、2图片、3漫游，调到直接播放页面，subType分别给U3D传23,28,29 )
     */
    public static void startPanoramaGoUnity(Activity activity,int type, String detailUrlUnity, String contents, String nav,String pageType, String onlineResourceFrom){
        if (!NetworkUtil.networkEnable()){
            Toast.makeText(activity,"当前网络不可用", Toast.LENGTH_SHORT).show();
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络播放
            showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络播放
        } else {

            int subType = 0;
            switch (type) {
                case ResTypeUtil.res_type_image: // 2:图片
                    subType = SubTypeUtil.native_pic_panorama;
                    break;
                case ResTypeUtil.res_type_roaming: //3:漫游
                    subType = SubTypeUtil.native_roam_panorama;
                    break;
                case ResTypeUtil.res_type_video: //4:视频
                    subType = SubTypeUtil.native_play_panorama;
                    break;
            }
            Intent intent = new Intent(activity, GoUnity.class);
            intent.putExtra("type", ResTypeUtil.res_type_native + "");
            intent.putExtra("subType", subType + "");
            intent.putExtra("detailUrl", detailUrlUnity);
            intent.putExtra("contents", contents + "");
            intent.putExtra("nav", nav + "");
            intent.putExtra("pageType", pageType);
            intent.putExtra("online_resource_from", onlineResourceFrom);
            activity.startActivity(intent);
        }
    }

    /**
     * 跳转GoUnity页面,type:-1, subType:24
     */
    public static void startVideoGoUnity(final Activity activity,final String detailUrlUnity, final String contents,final String nav, final String currentVideo,final String pageType){
        if(ReportBusiness.PAGE_TYPE_DETAIL.equals(pageType)){
            //详情页弹出预览播放
            PlayerModeChooseSubject.getInstance().notifyChooseCallBack(activity,currentVideo, new PlayerModeChooseSubject.IPlayerChooseCallback() {
                @Override
                public void doVRPlay(String SqlNo) {
                    if(GLConst.GoUnity) {
                    goVrPlay(activity,detailUrlUnity,contents,nav,currentVideo,pageType);
                    }
                }
                @Override
                public void doNormalPlay(String SqlNo) {}

                @Override
                public void onChooseViewClose() {

                }
            });

        }else {
          goVrPlay(activity,detailUrlUnity,contents,nav,currentVideo,pageType);
        }

    }

    public static void goVrPlay(final Activity activity,final String detailUrlUnity, final String contents,final String nav, final String currentVideo,final String pageType){
        Intent intent = null;
        if (UnityActivity.mIsYTJ){
            intent = new Intent(activity, GoUnity.class);
        } else if(!NetworkUtil.networkEnable()){
            Toast.makeText(activity,"当前网络不可用", Toast.LENGTH_SHORT).show();
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络播放
            showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络播放
        } else {
            intent = new Intent(activity, GoUnity.class);
        }

        if (intent != null){
            intent.putExtra("type", ResTypeUtil.res_type_native + "");
            intent.putExtra("subType", SubTypeUtil.native_play_2d + "");
            intent.putExtra("detailUrl", detailUrlUnity);
            intent.putExtra("contents", contents + "");
            intent.putExtra("nav", nav + "");
            intent.putExtra("currentVideoSeq", currentVideo);
            intent.putExtra("pageType", pageType);
            activity.startActivity(intent);
        }
    }

    /**
     * 跳转GoUnity页面,type:-1, subType:30  直播
     */
    public static void startLiveGoUnity(Activity activity,String detailUrlUnity, String contents, String nav,String pageType){
        if(!NetworkUtil.networkEnable()){
            Toast.makeText(activity,"当前网络不可用", Toast.LENGTH_SHORT).show();
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络播放
            showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络播放
        } else {
            Intent intent = new Intent(activity, GoUnity.class);
            intent.putExtra("type", ResTypeUtil.res_type_native + "");
            intent.putExtra("subType", SubTypeUtil.native_play_live + "");
            intent.putExtra("detailUrl", detailUrlUnity);
            intent.putExtra("contents", contents + "");
            intent.putExtra("nav", nav + "");
            intent.putExtra("pageType", pageType);
            activity.startActivity(intent);
        }
    }

    /**
     * 播放全景（已下载）
     * @param mContext 上下文
     * @param downloadItem 下载实体类
     */
    public static void playPanoramaWithDownloaded(Context mContext, DownloadItem downloadItem){
        int resType = downloadItem.getDownloadType();//资源类型
        if(ResTypeUtil.res_type_roaming == resType){//全景漫游
            File file = DownloadResBusiness.getDownloadResFile(downloadItem);
            String downloadUrl = downloadItem.getHttpUrl();
            String resourcePath = file.getAbsolutePath();
            playPanoramaRoamingWithDownloaded(mContext, downloadUrl, resourcePath);
        }else if(ResTypeUtil.res_type_video == resType
                ||ResTypeUtil.res_type_movie == resType){//全景视频
            File file = DownloadResBusiness.getDownloadResFile(downloadItem);
            String name = downloadItem.getTitle();
            String resourcePath = file.getAbsolutePath();
            int video_dimension = downloadItem.getVideo_dimension();//全景视频播放模式
            int is_panorama =  downloadItem.getIs_panorama();//全景控制
            int is4k = downloadItem.getIs4k();
            int videoType = getVideoTypeFromHistory(resourcePath);
            if(videoType<0){
                videoType = VideoTypeUtil.getVideoType(is_panorama, video_dimension);//视频类型
            }
            playPanoramaVideoWithDownloaded(mContext, name, resourcePath, videoType, is4k);
        }
    }


    /**
     * 读取播放记录数据
     */
    private static int getVideoTypeFromHistory(String mVideoPath){
        HistoryInfo historyInfo = null;
        String history = HistoryBusiness.readFromHistory(mVideoPath,0);
        try {
            if(history!=null) {
                org.json.JSONObject myJsonObject = new org.json.JSONObject(history);
                historyInfo = CreateHistoryUtil.localJsonToHistoryInfo(myJsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (historyInfo != null) {

            int m3dType = historyInfo.getVideo3dType();
            int videotype = historyInfo.getVideoType();
            if(m3dType>0&&videotype>0) {
                int  mVideoType = VideoTypeUtil.getVideoType(videotype, m3dType) ;
                return mVideoType;
            }

        }
        return -1;
    }



    /**
     * 播放全景漫游（已下载）
     * @param mContext 上下文
     * @param download_url 下载的url
     * @param resourcePath 本地路径
     */
    public static void playPanoramaRoamingWithDownloaded(Context mContext, String download_url, String resourcePath){
        Intent intent = new Intent(mContext, GoUnity.class);
        intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
        intent.putExtra("subType", String.valueOf(SubTypeUtil.play_panorama_roaming_with_download));
        intent.putExtra("resourcePath", resourcePath);
        intent.putExtra("download_url", download_url);//下载url，全景漫游用
        intent.putExtra("local_resource_from", resource_from_download);//资源来自下载
        //intent.putExtra("pageType", pageType);
        mContext.startActivity(intent);
    }

    /**
     * 播放全景视频（已下载）
     * @param mContext 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     * @param videoType 视频类型
     */
    public static void playPanoramaVideoWithDownloaded(Context mContext, String name, String resourcePath, int videoType){
        Intent intent = new Intent(mContext, GoUnity.class);
        intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
        intent.putExtra("subType", String.valueOf(SubTypeUtil.native_local_play));
        intent.putExtra("name", name);
        intent.putExtra("resourcePath", resourcePath);
        intent.putExtra("local_resource_from", resource_from_download);//资源来自下载
        intent.putExtra("videoType", String.valueOf(videoType));//视频类型
        //intent.putExtra("pageType", pageType);
        mContext.startActivity(intent);
    }

    public static void playPanoramaVideoWithDownloaded(Context mContext, String name, String resourcePath, int videoType, int is4k){
        Intent intent = new Intent(mContext, GoUnity.class);
        intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
        intent.putExtra("subType", String.valueOf(SubTypeUtil.native_local_play));
        intent.putExtra("name", name);
        intent.putExtra("resourcePath", resourcePath);
        /**修改local_resource_from 从resource_from_download更改为resource_from_local   原因：非全景视频使用resource_from_download 存在问题  by whf 20170303*/
        intent.putExtra("local_resource_from", resource_from_local);//资源来自下载
        intent.putExtra("videoType", String.valueOf(videoType));//视频类型
        intent.putExtra("is4k",String.valueOf(is4k)); //用于区分视频是否为4k解锁
        //intent.putExtra("pageType", pageType);
        mContext.startActivity(intent);
    }


    /**
     * 播放视频（本地）
     * @param mContext 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     */
    public static void playVideoWithLocal(final Context mContext, final String name, final String resourcePath, final GotoPlayCallback gotoPlayCallback){
        VideoTypeUtil.getVideoType(resourcePath, new VideoTypeUtil.VideoTypeCallback() {
            @Override
            public void result(int videoType) {
                Intent intent = new Intent(mContext, GoUnity.class);
                intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
                intent.putExtra("subType", String.valueOf(SubTypeUtil.native_local_play));
                intent.putExtra("name", name);
                intent.putExtra("resourcePath", resourcePath);
                intent.putExtra("local_resource_from", resource_from_local);//资源来自本地
                intent.putExtra("videoType", String.valueOf(videoType));//视频类型
                //intent.putExtra("pageType", pageType);
                mContext.startActivity(intent);
                if (gotoPlayCallback != null) {
                    gotoPlayCallback.callback();
                }
            }
        });
    }

    /**
     * 播放视频（本地）本地预览功能跳转横屏不需要开启GoUnity页面
     * @param mContext 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     */
    public static void playVideoWithLocalUnity(final BaseActivity mContext, final String name, final String resourcePath, final GotoPlayCallback gotoPlayCallback){
        VideoTypeUtil.getVideoType(resourcePath, new VideoTypeUtil.VideoTypeCallback() {
            @Override
            public void result(int videoType) {
                Intent intent = new Intent(mContext, UnityActivity.class);
                String type = String.valueOf(ResTypeUtil.res_type_native);
                String subType = String.valueOf(SubTypeUtil.native_local_play);//资源来自本地
                String videoTypeStr = String.valueOf(videoType);//视频类型
                mContext.initPlayParam(type, subType, "", "", "", name, resourcePath, "", "", resource_from_local, videoTypeStr, "", "");
                String str = JSON.toJSONString(BaseApplication.INSTANCE.hierarchyBeanList, SerializerFeature.DisableCircularReferenceDetect);
                intent.putExtra("hierarchy", str);
                mContext.startActivity(intent);
                if (gotoPlayCallback != null) {
                    gotoPlayCallback.callback();
                }
            }
        });
    }

    /**
     * 播放视频（飞屏）
     * @param mContext 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     */
    public static void playVideoWithFlyScreen(final Context mContext, final String name, final String resourcePath){
        Intent intent = new Intent(mContext, GoUnity.class);
        intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
        intent.putExtra("subType", String.valueOf(SubTypeUtil.native_local_play));
        intent.putExtra("name", name);
        intent.putExtra("resourcePath", resourcePath);
        intent.putExtra("local_resource_from", resource_from_local);//资源来自飞屏
        intent.putExtra("videoType", String.valueOf(MJVideoPictureTypeSingle));//视频类型
        mContext.startActivity(intent);
    }

    /**
     * 播放视频（手机上的资源管理器）
     * @param mContext 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     */
    public static void playVideoWithExplorer(final Context mContext, final String name, final String resourcePath ){

        VideoTypeUtil.getVideoType(resourcePath, new VideoTypeUtil.VideoTypeCallback() {
            @Override
            public void result(int videoType) {
                Intent intent;
                if (UnityActivity.mIsYTJ){
                    goUnityActivity(mContext, name, resourcePath, String.valueOf(SubTypeUtil.native_local_play), resource_from_explorer, videoType);
                    return;
                } else {
                    intent = new Intent(mContext, GoUnity.class);
                }

                intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
                intent.putExtra("subType", String.valueOf(SubTypeUtil.native_local_play));
                intent.putExtra("name", name);
                intent.putExtra("resourcePath", resourcePath);
                intent.putExtra("local_resource_from", resource_from_explorer);//资源来自本地
                intent.putExtra("videoType", String.valueOf(videoType));//视频类型
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 本地图片播放（手机上的资源管理器）
     * @param mContext 上下文
     * @param resourcePath 本地路径
     */
    public static void playImageWithExplorer(final Context mContext, final String resourcePath ){
        Intent intent = null;
        if (UnityActivity.mIsYTJ){
            goUnityActivity(mContext, "", resourcePath, String.valueOf(SubTypeUtil.native_image_play), resource_from_download, 0);
            return;
        } else {
            intent = new Intent(mContext, GoUnity.class);
        }

        intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
        intent.putExtra("subType", String.valueOf(SubTypeUtil.native_image_play));
        intent.putExtra("resourcePath", "");//全景图片不用这个参数
        intent.putExtra("download_url", resourcePath);
        intent.putExtra("local_resource_from", resource_from_download);//资源来自下载
        mContext.startActivity(intent);
    }

    private static void goUnityActivity(Context mContext, String name, String resourcePath, String subType, String from, int videoType){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", resourcePath);
        jsonObject.put("name", name);
        jsonObject.put("type", videoType);
        jsonObject.put("fromePage", from);

        Intent intent = new Intent(mContext, UnityActivity.class);
        intent.putExtra("json", jsonObject.toJSONString());
        mContext.startActivity(intent);
    }

    /**
     * 播放视频（ES浏览器网盘视频）
     * @param activity 上下文
     * @param name 名称
     * @param resourcePath 本地路径
     */
    public static void playVideoWithNetDisk(final Activity activity, final String name, final String resourcePath ){
        if (!NetworkUtil.networkEnable()){
            Toast.makeText(activity,"当前网络不可用", Toast.LENGTH_SHORT).show();
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络播放
            showOpenGprsDialog(activity);// 提示WiFi不可用，是否开启gprs网络播放
        } else {
            Intent intent = new Intent(activity, GoUnity.class);
            intent.putExtra("type", String.valueOf(ResTypeUtil.res_type_native));
            intent.putExtra("subType", String.valueOf(SubTypeUtil.native_netdisk_play));
            intent.putExtra("name", name);
            intent.putExtra("resourcePath", resourcePath);
            intent.putExtra("local_resource_from", resource_from_local);
            intent.putExtra("videoType", String.valueOf(VideoTypeUtil.MJVideoPictureTypeUnknown));//未设置
            activity.startActivity(intent);
        }
    }

    /**
     * 进入搜索页
     * @param activityGroup 主activity
     */
    public static void gotoSearchActivity(MainActivityGroup activityGroup){
        Intent intent = new Intent(activityGroup, SearchActivity.class);
        intent.putExtra("res_id", activityGroup.getMainTabResId());
        intent.putExtra("frompage", ReportBusiness.getMainPageType(activityGroup.getCurrentTab()));//来源哪个页面
        activityGroup.startActivity(intent);
        //报数
        ReportClickBean reportClickBean = new ReportClickBean();
        reportClickBean.setEtype("click");
        reportClickBean.setTpos("1");
        reportClickBean.setPagetype("search_icon");
        reportClickBean.setClicktype("search");
        ReportBusiness.getInstance().reportClick(reportClickBean);
    }

    /**
     * 提示WiFi不可用，是否打开gprs对话框
     */
    public static void showOpenGprsDialog(final Activity activity){
        new OpenGprsDialog(activity).showDialog(OpenGprsDialog.title_play, OpenGprsDialog.tip_play, new OpenGprsDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                activity.startActivity(new Intent(activity, SettingActivity.class));
            }
        });
    }

    /**
     * true走已下载播放，false走在线播放
     */
    public static boolean needPlayWithDownload(File file, int resType){
        if(file != null && file.exists()){//已下载
            if(ResTypeUtil.res_type_video == resType || ResTypeUtil.res_type_roaming == resType){//全景视频，全景漫游
//                if(!NetworkUtil.networkEnable()){//无网络
                    return true;
//                }
            }
        }
        return false;
    }

    /**
     * 播放视频回调
     */
    public interface GotoPlayCallback{
        void callback();
    }
}
