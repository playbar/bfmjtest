package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.AboutActivity;
import com.baofeng.mj.ui.activity.AccountActivity;
import com.baofeng.mj.ui.activity.AppListActivity;
import com.baofeng.mj.ui.activity.ChargeActivity;
import com.baofeng.mj.ui.activity.ChoicenessActivity;
import com.baofeng.mj.ui.activity.ConnectActivity;
import com.baofeng.mj.ui.activity.CustomInfoActivity;
import com.baofeng.mj.ui.activity.GameDetailActivity;
import com.baofeng.mj.ui.activity.GlassSettingActivity;
import com.baofeng.mj.ui.activity.GuideActivity;
import com.baofeng.mj.ui.activity.GyroscopeActivity;
import com.baofeng.mj.ui.activity.H5Activity;
import com.baofeng.mj.ui.activity.HelpActivity;
import com.baofeng.mj.ui.activity.LiveDetailActivity;
import com.baofeng.mj.ui.activity.LiveVideoListActivity;
import com.baofeng.mj.ui.activity.PanoramaDetailActivity;
import com.baofeng.mj.ui.activity.SettingActivity;
import com.baofeng.mj.ui.activity.ShopWebActivity;
import com.baofeng.mj.ui.activity.SubscribeActivity;
import com.baofeng.mj.ui.activity.TaskListActivity;
import com.baofeng.mj.ui.activity.TopicActivity;
import com.baofeng.mj.ui.activity.VideoDetailActivity;
import com.baofeng.mj.ui.activity.VideoHistoryActivity;
import com.baofeng.mj.ui.activity.VrSettingActivity;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.GameApi;
import com.mojing.dl.domain.DownloadItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by liuchuanchi on 2016/5/9.
 * 资源类型工具类
 */
public class ResTypeUtil {
    public static final int res_type_downloading = -2;//下载中资源
    public static final int res_type_native = -1;//跳转native页面
    public static final int res_type_movie = 1;//影视

    public static final int res_type_image = 2;//图片
    public static final int res_type_roaming = 3;//漫游
    public static final int res_type_video = 4;//视频

    public static final int res_type_live = 5;//直播
    public static final int res_type_apply = 6;//应用
    public static final int res_type_game = 7;//游戏
    public static final int res_type_special = 8;//专题
    public static final int res_type_channel = 9;//频道页
    public static final int res_type_banner = 10;//栏目列表
    public static final int res_type_category = 11;//分类
    public static final int res_type_live_url = 12;//直播地址
    public static final int res_type_html5 = 13;//HTML5 页面
    public static final int res_type_game_rank = 14;//游戏排行榜
    public static final int res_type_game_welfare = 15;//福利页
    public static final int res_type_fireware = 16;// 固件
    public static final int res_type_plugin = 17;// 插件

    //直播的状态
    public static final int res_live_video_status_stop = 0;//直播休息结束
    public static final int res_live_video_status_playing = 1;//直播中
    public static final int res_live_video_status_replay = 2;//直播回放

    /**
     * true 是游戏或者应用，false相反
     * @return
     */
    public static boolean isGameOrApp(int resType){
        if(res_type_game == resType || res_type_apply == resType){
            return true;
        }
        return false;
    }

    /**
     * true 不是游戏，也不是应用，false相反
     * @return
     */
    public static boolean isNotGameAndApp(int resType){
        if(res_type_game != resType && res_type_apply != resType){
            return true;
        }
        return false;
    }

    /**
     * 是不是全景视频
     * @param resType 资源类型
     * @return true是，false不是
     */
    public static boolean isPanoramaVideo(int resType){
        if(res_type_video == resType){
            return true;
        }
        return false;
    }

    public static void moreClick(Context mContext, ContentInfo contentInfo){
        //跳转更多页面
        Intent categoryIntent = new Intent(mContext, AppListActivity.class);
        categoryIntent.putExtra("next_type",ResTypeUtil.res_type_banner);
        categoryIntent.putExtra("next_subType",0);
        categoryIntent.putExtra("next_title",contentInfo.getTitle());
        categoryIntent.putExtra("next_url", contentInfo.getUrl());
        mContext.startActivity(categoryIntent);
    }

    /**
     * 页面跳转工具类
     *
     * @param mContext
     * @param contentInfo
     */
    public static void onClickToActivity(Context mContext, ContentInfo contentInfo) {
        int type = contentInfo.getType();
        int subType = contentInfo.getSubtype();
        String url = contentInfo.getUrl();
        String h5Url = "";
        String packageName = "";
        if(contentInfo.getApp_extra() != null && contentInfo.getApp_extra().getH5_url() != null){
            h5Url = contentInfo.getApp_extra().getH5_url();
            packageName = contentInfo.getApp_extra().getPackage_name();
        }
        onClickToActivityAndroid(mContext, type, subType, url, contentInfo.getTitle(), contentInfo.getRes_id() + "", contentInfo.getCategory_type(), h5Url, packageName, null);
    }

    public static void onClickToActivityAndroid(Context mContext, int type, int subType, String url, String title, String resId, int categoryType, String h5Url, String packageName, String operateJson){
        BaseApplication.INSTANCE.setEnableToLandscapeCondition1(true);
        switch (type) {
            case 0: //跳转Type没传，容错
                Intent intent = new Intent(mContext, MainActivityGroup.class);
                intent.putExtra(PushTypeBusiness.OPERATE_JSON,  operateJson);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent);
                break;
            case ResTypeUtil.res_type_native: //跳转native页面
                openNative(mContext, subType, false, operateJson);
                break;
            case ResTypeUtil.res_type_movie: //影视  1
                onClickToVideoDetailActivity(mContext, type, subType, url, false);//进入影视详情页
                break;
            //全景
            case ResTypeUtil.res_type_image: //图片  2
            case ResTypeUtil.res_type_roaming: //漫游  3
            case ResTypeUtil.res_type_video: //视频  4
                onClickToPanoramaDetailActivity(mContext, type, subType, url,false);//进入专题页
                break;
            case ResTypeUtil.res_type_live: //直播 5
                onClickToLiveDetailActivity(mContext, type, subType, url, false);
                break;
            case ResTypeUtil.res_type_apply: //应用 6
            case ResTypeUtil.res_type_game: //游戏 7
//                    onClickToGameDetail(mContext, type, subType, url, title, isBackMain, operateJson);
                onClickToH5Activity(mContext, type, subType, h5Url, resId, title, packageName,false);
                break;
            case ResTypeUtil.res_type_special://专题 8
                onClickToTopicActivity(mContext, type, subType, url, false, operateJson);//进入专题页
                break;
            case ResTypeUtil.res_type_channel://频道页 9 ChoicenessFragment换成Activity
                onClickToChoicenessDetail(mContext, type, subType, url, title, false, operateJson);
                break;
            case ResTypeUtil.res_type_banner://栏目 10  更多
                onClickToAppListActivity(mContext, type, subType, url, title, resId, false, operateJson);
                break;
            case ResTypeUtil.res_type_category://分类 11  新起页面列表  10和11基本等同  有筛选功能，横屏返回数据不对，返回首页
                //因为服务器没有考虑前端UI的页面改变，不能兼容，直播秀场列表页面type返回的还是11，所以要加用categoryType来判断进入直播秀场列表页面
                if(categoryType == 7) {
                    onClickToLiveVideoList(mContext, type, subType, url, title, resId, false, operateJson);
                } else {
                    onClickToAppListActivity(mContext, type, subType, url, title, resId, false, operateJson);
                }
                break;
            case ResTypeUtil.res_type_live_url://直播地址
                break;
            case ResTypeUtil.res_type_html5://HTML5 页面
                onClickToH5Activity(mContext, type, subType, url, resId, title, "",false);
                break;
            case ResTypeUtil.res_type_game_rank://游戏排行榜  14
                onClickToAppListActivity(mContext, type, subType, url, title, "", false, operateJson);
            case ResTypeUtil.res_type_game_welfare: ////福利页
                break;
        }
    }

    /** 20170324版
     * 彻底打破横竖屏对应关系：
     * 在竖屏任何页面（视频详情页除外），点击“进入VR”进入横屏，跳转到横屏首页。
     * 在横屏任何页面（视频详情页除外），返回到竖屏时，跳转到竖屏首页。
     * 对于视频详情页，保持原有的逻辑不变：从竖屏进入横屏时，进入极简/沉浸模式开始播放视频；从横屏返回到竖屏时，回到视频的详情页。
     *
     * 201704XX版 横屏回来跳转到竖屏首页
     */
    public static void onClickToActivity(Context mContext, int type, int subType, String url, String title, String resId, String h5Url, String packageName, String operateJson){
        BaseApplication.INSTANCE.setEnableToLandscapeCondition1(true);
        Intent nullIntent = new Intent(mContext, MainActivityGroup.class);
        nullIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mContext.startActivity(nullIntent);
    }

    /**
     * 进入直播秀场页面
     */
    public static void onClickToLiveVideoList(Context mContext, int type, int subType, String url,String title, String resId, boolean isBackMain, String operateJson){
        Intent rankIntent = new Intent(mContext, LiveVideoListActivity.class);
        rankIntent.putExtra("next_type", type);
        rankIntent.putExtra("next_subType", subType);
        rankIntent.putExtra("next_title", title);
        rankIntent.putExtra("next_url", url);
        if(!"".equals(resId)){
            rankIntent.putExtra("res_id", resId);
        }
        rankIntent.putExtra(PushTypeBusiness.OPERATE_JSON, operateJson);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, rankIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(rankIntent);
        }

    }

    /**
     * 进入游戏详情页面
     */
    public static void onClickToGameDetail(Context mContext, int type, int subType, String url,String title, boolean isBackMain, String operateJson){
        Intent gameIntent = new Intent(mContext, GameDetailActivity.class);
        gameIntent.putExtra("title", title);
        gameIntent.putExtra("next_type", type);
        gameIntent.putExtra("next_subType", subType);
        gameIntent.putExtra("detail_url", url);
        gameIntent.putExtra(PushTypeBusiness.OPERATE_JSON, operateJson);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, gameIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(gameIntent);
        }
    }

    /**
     *  进入ChoicenessDetail页面
     */
    public static void onClickToChoicenessDetail(Context mContext, int type, int subType, String url,String title, boolean isBackMain, String operateJson){
        Intent channelIntent = new Intent(mContext, ChoicenessActivity.class);
        channelIntent.putExtra("next_type", type);
        channelIntent.putExtra("next_subType", subType);
        channelIntent.putExtra("next_url", url);
        channelIntent.putExtra("next_title", title);
        channelIntent.putExtra(PushTypeBusiness.OPERATE_JSON, operateJson);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, channelIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(channelIntent);
        }
    }

    /**
     * 进入AppList页面
     */
    public static void onClickToAppListActivity(Context mContext, int type, int subType, String url,String title, String resId, boolean isBackMain, String operateJson){
        Intent rankIntent = new Intent(mContext, AppListActivity.class);
        rankIntent.putExtra("next_type", type);
        rankIntent.putExtra("next_subType", subType);
        rankIntent.putExtra("next_title", title);
        rankIntent.putExtra("next_url", url);
        if(!"".equals(resId)){
            rankIntent.putExtra("res_id", resId);
        }
        rankIntent.putExtra(PushTypeBusiness.OPERATE_JSON, operateJson);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, rankIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(rankIntent);
        }

    }

    /**
     * 跳转H5页面
     */
    public static void onClickToH5Activity(final Context mContext, final String detailId, final String redirectUrl, final boolean isBackMain){
        new GameApi().getGameDetailInfoNoHeader(mContext, redirectUrl, new ApiCallBack<ResponseBaseBean<GameDetailBean>>() {
            @Override
            public void onSuccess(ResponseBaseBean<GameDetailBean> result) {
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
                            GameDetailBean gameDetailBean = result.getData();
                            int resType = gameDetailBean.getType();
                            String url = "http://detailpage.game.mojing.cn?resId=" + detailId;
                            String title = gameDetailBean.getTitle();
                            String packageName = gameDetailBean.getPackage_name();
                            onClickToH5Activity(mContext, resType, 0, url, detailId, title, packageName, isBackMain);
                        }
                    }
                }
            }
        });
    }

    /**
     * 跳转H5页面
     */
    public static void onClickToH5Activity(Context mContext, int type, int subType, String url, String resId, String title, String packageName,boolean isBackMain){
        Intent htmlIntent = new Intent(mContext, H5Activity.class);
        htmlIntent.putExtra("next_type", type);
        htmlIntent.putExtra("next_subType", subType);
        htmlIntent.putExtra("next_url", url);
        htmlIntent.putExtra("next_title", title);
        htmlIntent.putExtra("next_resId",resId);
        htmlIntent.putExtra("next_packageName",packageName);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, htmlIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(htmlIntent);
        }

    }

    /**
     * 进入专题页
     */
    public static void onClickToTopicActivity(Context mContext, int type, int subType, String url, boolean isBackMain, String operateJson){
        Intent specialIntent = new Intent(mContext, TopicActivity.class);
        specialIntent.putExtra("next_type", type);
        specialIntent.putExtra("next_subType", subType);
        specialIntent.putExtra("next_url", url);
        specialIntent.putExtra(PushTypeBusiness.OPERATE_JSON, operateJson);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, specialIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(specialIntent);
        }
    }

    /**
     *  进入直播详情页面
     */
    public static void onClickToLiveDetailActivity(Context mContext, int type, int subType, String url, boolean isBackMain){
        Intent liveIntent = new Intent(mContext, LiveDetailActivity.class);
        liveIntent.putExtra("next_type", type);
        liveIntent.putExtra("next_subType", subType);
        liveIntent.putExtra("next_url", url);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, liveIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(liveIntent);
        }
    }
    /**
     * 进入全景详情页
     */
    public static void onClickToPanoramaDetailActivity(Context mContext, int type, int subType, String url, boolean isBackMain){
        Intent intent = new Intent(mContext, PanoramaDetailActivity.class);
        intent.putExtra("next_type", type);
        intent.putExtra("next_subType", subType);
        intent.putExtra("next_url", url);

        if(isBackMain){
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent,intent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(intent);
        }
    }

    /**
     * 进入影视详情页
     */
    public static void onClickToVideoDetailActivity(Context mContext, int type, int subType, String url, boolean isBackMain){

        Intent movieIntent = new Intent(mContext, VideoDetailActivity.class);
        movieIntent.putExtra("next_type", type);
        movieIntent.putExtra("next_subType", subType);
        movieIntent.putExtra("next_url", url);
        if(isBackMain) {
            Intent[] intents;
            Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intents = new Intent[]{mainIntent, movieIntent};
            mContext.startActivities(intents);
        }else {
            mContext.startActivity(movieIntent);
        }
    }

    private static void openNative(Context mContext, int subType,boolean isBackMain, String operateJson) {
        Intent[] intents;
        switch (subType) {
            case SubTypeUtil.native_history_record:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, VideoHistoryActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, VideoHistoryActivity.class));
                }
                break;
            case SubTypeUtil.native_main_local:
                Intent intent = new Intent(mContext, MainActivityGroup.class);
                intent.putExtra(PushTypeBusiness.FROM_WHERE, PushTypeBusiness.from_where_landscape_local);
                mContext.startActivity(intent);
                break;

            case SubTypeUtil.native_main_local_download:
                Intent intentDownload = new Intent(mContext, MainActivityGroup.class);
                intentDownload.putExtra(PushTypeBusiness.FROM_WHERE,  PushTypeBusiness.from_where_landscape_download);
                intentDownload.putExtra(PushTypeBusiness.OPERATE_JSON,  operateJson);
                mContext.startActivity(intentDownload);
                break;

            case SubTypeUtil.native_my_subscription:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, SubscribeActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, SubscribeActivity.class));
                }
                break;
            case SubTypeUtil.native_modou_recharge:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, ChargeActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, ChargeActivity.class));
                }
                break;
            case SubTypeUtil.native_modou_shop:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, ShopWebActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, ShopWebActivity.class));
                }
                break;
            case SubTypeUtil.native_guide:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, GuideActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, GuideActivity.class));
                }
                break;

            case SubTypeUtil.native_my_mission:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, TaskListActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent,videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, TaskListActivity.class));
                }
                break;

            case SubTypeUtil.native_setting:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, SettingActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, SettingActivity.class));
                }
                break;

            case SubTypeUtil.native_about:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, AboutActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, AboutActivity.class));
                }
                break;

            case SubTypeUtil.native_experience:
                break;

            case SubTypeUtil.native_contact:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, ConnectActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, ConnectActivity.class));
                }
                break;

            case SubTypeUtil.native_help:
//                FeedbackAgent agent = new FeedbackAgent(mContext);
//                agent.startFeedbackActivity();
                break;

            case SubTypeUtil.native_gyroscope:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, GyroscopeActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, GyroscopeActivity.class));
                }
                break;

            case SubTypeUtil.native_stick_connect_help:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, HelpActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, HelpActivity.class));
                }
                break;

            case SubTypeUtil.native_glasses_control:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, VrSettingActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, VrSettingActivity.class));
                }
                break;

            case SubTypeUtil.native_glasses_setting:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, GlassSettingActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, GlassSettingActivity.class));
                }
                break;

            case SubTypeUtil.native_profile:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, CustomInfoActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, CustomInfoActivity.class));
                }
                break;

            case SubTypeUtil.native_account:
                if(isBackMain){
                    Intent videoIntent = new Intent(mContext, AccountActivity.class);
                    Intent mainIntent = new Intent(mContext, MainActivityGroup.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intents = new Intent[]{mainIntent, videoIntent};
                    mContext.startActivities(intents);
                }else {
                    mContext.startActivity(new Intent(mContext, AccountActivity.class));
                }
                break;

            default:
                Intent defaultIntent = new Intent(mContext, MainActivityGroup.class);
                defaultIntent.putExtra(PushTypeBusiness.OPERATE_JSON,  operateJson);
                defaultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(defaultIntent);
                break;
        }
    }

    /**
     * 处理operateJson
     */
    public static void processOperateJson(final Context context, String operateJson){
        if(TextUtils.isEmpty(operateJson)){
            return;
        }
        try {
            JSONObject joOperate = new JSONObject(operateJson);
            int resType = joOperate.getInt("resType");//资源类型
            String resId = joOperate.getString("resId");//资源id
            String resTitle = joOperate.getString("resTitle");//资源title
            int operate = joOperate.getInt("operate");//操作

            if(PushTypeBusiness.operate_install_apk == operate){//安装apk
                File apkFile = DownloadResBusiness.getApkFile(resType, resId, resTitle);
                String apkPath = apkFile.getAbsolutePath();
                if(apkPath.endsWith(".apk")){//apk文件，直接安装
                    ApkUtil.installApk(context, apkPath);
                }else if(apkPath.endsWith(".zip")){//zip文件，先解压再安装
                    DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(resType, resId, resTitle, ".zip");
                    downloadItem.setAppFromType(ConstantKey.OBB);
                    UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                        @Override
                        public void notify(DownloadItem downloadItem, int unZipResult) {
                            if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                                File file = DownloadResBusiness.getDownloadResFile(downloadItem);
                                ApkUtil.installApk(context, file.getAbsolutePath());//安装apk
                            }
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}