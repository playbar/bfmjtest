package com.baofeng.mj.business.publicbusiness;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.MainTabUtil;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.MojingSDKReport;
import com.google.gson.Gson;
import com.mojing.dl.domain.DownloadItem;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaominglei on 2016/7/16.
 */
public class ReportBusiness {
    /**
     * 点击类型
     */
    public static final String CLICK_TYPE_DOWNLOAD = "download";//游戏下载
    public static final String CLICK_TYPE_INSTALL = "install";//游戏安装
    public static final String CLICK_TYPE_OPEN = "open";//游戏打开
    public static final String CLICK_TYPE_UPDATE = "update";//游戏更新
    /**
     * 页面类型
     */
    public static final String PAGE_TYPE_RECOMMEND = "recommend";//推荐页
    public static final String PAGE_TYPE_VIDEO = "video";//视频页
    public static final String PAGE_TYPE_APPGAME = "appgame";//应用市场页
    public static final String PAGE_TYPE_LOCAL = "local";//本地页
    public static final String PAGE_TYPE_ACCOUNT = "account"; //我的页面
    public static final String PAGE_TYPE_DETAIL = "detail";//详情页
    public static final String PAGE_TYPE_TOPIC_LIST = "topic_list";//专题列表页
    public static final String PAGE_TYPE_COLUMN_LIST = "column_list";//
    public static final String PAGE_TYPE_SUBCATE_LIST = "subcate_list";//

    public static final String UNKNOW = "UNKNOWN";

    private static ReportBusiness instance;
    private Map<String, ReportFromBean> mReportBean;
    private Map<String, Integer> mPVContants;
    private boolean isDebug = false;

    private ReportBusiness() {
        mReportBean = new HashMap<String, ReportFromBean>();
        mPVContants = new HashMap<String, Integer>();
        mPVContants.put("VR", 1);
        mPVContants.put("2D", 2);
        mPVContants.put("3D", 3);
        mPVContants.put("直播", 4);
        mPVContants.put("游戏", 1);
        mPVContants.put("软件", 2);
        mPVContants.put("福利", 3);
        mPVContants.put("分类", 4);
        mPVContants.put("榜单", 5);

        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(BaseApplication.INSTANCE);
        }
    }

    public static ReportBusiness getInstance() {
        if (instance == null) {
            instance = new ReportBusiness();
        }
        return instance;
    }

    public void put(String key, ReportFromBean reportFromBean) {
        try{
            if (TextUtils.isEmpty(key) || reportFromBean == null) {
                return;
            }
            ReportFromBean clone=(ReportFromBean)reportFromBean.clone();
            mReportBean.put(key, clone);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * 获取详细信息时，包含在header 中agent信息
     */
    public void putHeader(ContentInfo contentInfo, ReportFromBean reportFromBean) {
        if (contentInfo == null || reportFromBean == null) {
            return;
        }
        if (TextUtils.isEmpty(contentInfo.getUrl())) {
            return;
        }
        reportFromBean.setCompid(contentInfo.getParentResId());
        reportFromBean.setComponenttype(contentInfo.getLayout_type());
        reportFromBean.setCompsubtitle(contentInfo.getTitle());
        reportFromBean.setCompsubid(contentInfo.getRes_id());
        reportFromBean.setCurpage(contentInfo.getUrl());
        mReportBean.put(contentInfo.getUrl(), reportFromBean);
    }

    public ReportFromBean get(String key) {
        if (TextUtils.isEmpty(key) || !mReportBean.containsKey(key)) {
            return new ReportFromBean();
        }
        return mReportBean.get(key);
    }

    public void remove(String key) {
        if (!TextUtils.isEmpty(key) && mReportBean.containsKey(key)) {
            mReportBean.remove(key);
        }
    }

    public String getReportStr(String key) {
        if (TextUtils.isEmpty(key) || !mReportBean.containsKey(key)) {
            return null;
        }
        return JSONObject.toJSONString(mReportBean.get(key));
    }

    /***
     * 用于访问服务器时header agent内容
     *
     * @param msg
     * @return
     */
    public String getHeaderAgent(ReportFromBean msg) {
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("pid", MojingSDK.getCustomMetaData(BaseApplication.INSTANCE, "DEVELOPER_APP_ID")));
        params.add(new BasicNameValuePair("uid", MojingSDK.getUserID(BaseApplication.INSTANCE)));
        params.add(new BasicNameValuePair("rid", MojingSDK.AppGetRunID()));
        params.add(new BasicNameValuePair("ver", ApkUtil.getVersionName()));
        params.add(new BasicNameValuePair("sid", MojingSDK.getCustomMetaData(BaseApplication.INSTANCE, "DEVELOPER_CHANNEL_ID")));
        if (!TextUtils.isEmpty(msg.getCurpage())) {
            params.add(new BasicNameValuePair("curpage", msg.getCurpage()));
        }
        if (!TextUtils.isEmpty(msg.getFrompage())) {
            params.add(new BasicNameValuePair("frompage", msg.getFrompage()));
        }
        if (!TextUtils.isEmpty(msg.getCompid())) {
            params.add(new BasicNameValuePair("compid", msg.getCompid()));
        }
        if (!TextUtils.isEmpty(msg.getComponenttype())) {
            params.add(new BasicNameValuePair("componenttype", msg.getComponenttype()));
        }
        if (!TextUtils.isEmpty(msg.getCompsubtitle())) {
            params.add(new BasicNameValuePair("compsubtitle", msg.getCompsubtitle()));
        }
        if (!TextUtils.isEmpty(msg.getCompsubid())) {
            params.add(new BasicNameValuePair("compsubid", msg.getCompsubid()));
        }
        String encodedParams = URLEncodedUtils.format(params, "UTF-8");
        //区分默认header agent
        return "{" + encodedParams + "}";
    }

    public void reportDownloadCompleteClick(DownloadItem downloadItem) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("finish");
        bean.setTitle(downloadItem.getTitle());
        if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
            bean.setGameid(downloadItem.getAid());
        } else if(downloadItem.getDownloadType() == ResTypeUtil.res_type_movie){ //
            bean.setMovieid(downloadItem.getAid());
            bean.setMovietypeid(String.valueOf(downloadItem.getDownloadType()));
        } else{
            bean.setVideoid(downloadItem.getAid());
            bean.setTypeid(String.valueOf(downloadItem.getDownloadType()));
        }
        reportClick(bean);
    }

    //TODO 先注释上
    /***影视
     * vr键click报数
     * @param subMenuId 子目录id(从应用市场，视频点击vr键时)
     * @param subMenuName 子目录名称(从应用市场，视频点击vr键时)
     * @param localSubType 本地子目录类型
     * @param mainTab 主tab当前位置
     */
    public void reportVRKeyClick(int subMenuId, String subMenuName,int localSubType, int mainTab){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("jump");
        bean.setPagetype("VR_key");
        bean.setFrompage(getMainPageType(mainTab));
//        if (mainTab == MainTabUtil.VIDEO) {
//            if (!TextUtils.isEmpty(subMenuName)) {
//                bean.setVideo_menu_name(subMenuName);
//            }
//            bean.setVideo_menu_id(mPVContants.containsKey(subMenuName) ? String.valueOf(mPVContants.get(subMenuName)) : String.valueOf(subMenuId));
//        } else if (mainTab == MainTabUtil.APPGAME) {
//            if (!TextUtils.isEmpty(subMenuName)) {
//                bean.setAppgame_menu_name(subMenuName);
//            }
//            bean.setAppgame_menu_id(mPVContants.containsKey(subMenuName) ? String.valueOf(mPVContants.get(subMenuName)) : String.valueOf(subMenuId));
//        } else if (mainTab == MainTabUtil.LOCAL) {
//            int navId = 0;
//            if (localSubType == SubTypeUtil.native_main_local) {
//                navId = 1;
//            } else if (localSubType == SubTypeUtil.native_main_local_download) {
//                navId = 2;
//            } else if (localSubType == SubTypeUtil.native_fly_screen) {
//                navId = 3;
//            }
//            bean.setLocal_menu_id(String.valueOf(navId));
//        }
        reportClick(bean);
    }

    public void reportGlassesClick(GlassesNetBean glassBean, String pageType){
        ReportClickBean bean=new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("cut");
        bean.setTpos("1");
        bean.setPagetype(pageType);
        bean.setGlassid(glassBean.getGlass_id());
        bean.setGlassname(glassBean.getGlass_name());
        reportClick(bean);
    }

    public void reportPushClick(String pushId,String title,String pageTo){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setTpos("1");
        bean.setPagetype("msg_push");
        bean.setPushid(pushId);
        bean.setPushtitle(title);
        if (pageTo.equals(PushTypeBusiness.link_special)) {
            bean.setPageto("topic_list");
        } else if (pageTo.equals(PushTypeBusiness.link_detail)) {
            bean.setPageto("detail");
        }
        reportClick(bean);
    }

    public void reportClick(ReportClickBean bean) {
        try {
            String json = JSONObject.toJSONString(bean);
            writeLogToFile(json);
            MojingSDKReport.onEvent(json, UNKNOW, UNKNOW, 0, UNKNOW, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reportPV(int tab, int navId) {
        reportPV(tab, navId, null);
    }

    public void reportPV(int tab, int navId, String title) {
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype(getMainPageType(tab));
//        System.out.println("testtest reportPV tab:"+tab+"--navId--"+navId+"--title--"+title);
        switch (tab){
            case MainTabUtil.HOME:
                if (!TextUtils.isEmpty(title)) {
                    bean.setVideo_menu_name(title);
                }
                bean.setVideo_menu_id(mPVContants.containsKey(title) ? String.valueOf(mPVContants.get(title)) : String.valueOf(navId));
                break;
            case MainTabUtil.APPGAME:
                if (!TextUtils.isEmpty(title)) {
                    bean.setAppgame_menu_name(title);
                }
                bean.setAppgame_menu_id(mPVContants.containsKey(title) ? String.valueOf(mPVContants.get(title)) : String.valueOf(navId));
                break;
            case MainTabUtil.LOCAL:
                bean.setLocal_menu_id(String.valueOf(navId));
                break;
        }
        reportPV(bean);
    }

    public void reportPV(ReportPVBean bean) {
        try {
            String json = JSONObject.toJSONString(bean);
            writeLogToFile(json);
            MojingSDKReport.onEvent(json, UNKNOW, UNKNOW, 0, UNKNOW, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeLogToFile(String conent) {
        if (!isDebug) {
            return;
        }
        if (TextUtils.isEmpty(conent)) {
            return;
        }
        String path = FileStorageUtil.getDownloadDir() + "report.txt";
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 从url中截取resid
     * @param url
     * @return
     */
    public String getResIdFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int firstIndex = url.lastIndexOf("/");
        int lastIndex = url.lastIndexOf(".");
        if (firstIndex == -1 || lastIndex == -1) {
            return null;
        }
        return url.substring(firstIndex + 1, lastIndex);
    }

    /***
     * 获取点击类型
     * @param apkState apk状态
     */
    public static String getClickType(int apkState) {
        switch (apkState){
            case ApkUtil.NEED_DOWNLOAD:
                return CLICK_TYPE_DOWNLOAD;
            case ApkUtil.NEED_INSTALL:
                return CLICK_TYPE_INSTALL;
            case ApkUtil.CAN_PLAY:
                return CLICK_TYPE_OPEN;
            case ApkUtil.NEED_UPDATE:
                return CLICK_TYPE_UPDATE;
            default:
                return "";
        }
    }

    /**
     * 获取页面类型
     * @param resType 资源类型
     */
    public static String getPageType(int resType){
        switch (resType){//资源类型
            case ResTypeUtil.res_type_banner:
                return PAGE_TYPE_COLUMN_LIST;
            case ResTypeUtil.res_type_category:
                return PAGE_TYPE_SUBCATE_LIST;
            default:
                return null;
        }
    }

    /***
     * 获取页面类型
     * @param mCurrentTab 当前tab
     */
    public static String getMainPageType(int mCurrentTab) {
        switch (mCurrentTab){
            case MainTabUtil.RECOMMEND:
                return PAGE_TYPE_RECOMMEND;
            case MainTabUtil.HOME:
                return PAGE_TYPE_VIDEO;
            case MainTabUtil.APPGAME:
                return PAGE_TYPE_APPGAME;
            case MainTabUtil.LOCAL:
                return PAGE_TYPE_LOCAL;
            case MainTabUtil.ACCOUNT:
                return PAGE_TYPE_ACCOUNT;
            default:
                return PAGE_TYPE_RECOMMEND;
        }
    }

    /**
     * vv报数
     * @param params
     */
    public void reportVV(HashMap<String ,String> params){
        String vvParams = new Gson().toJson(params);
//        Log.d("login","---reportVV vvParams = "+params);
        MojingSDKReport.onEvent(vvParams,UNKNOW,UNKNOW,0f,UNKNOW,0f);
    }

    /**
     * 点击报数
     * @param params
     */
    public void reportClick(HashMap<String,String> params){
        String clickParams = new Gson().toJson(params);
//        Log.d("login","----reportClick params = "+clickParams);
        MojingSDKReport.onEvent(clickParams,UNKNOW,UNKNOW,0f,UNKNOW,0f);
    }
}
