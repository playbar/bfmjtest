package com.baofeng.mj.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.baofeng.mj.bean.HierarchyBean;
import com.baofeng.mj.bean.LandscapeUrlBean;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.view.CustomProgressDialog;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mojing.MojingSDKReport;
import com.baofeng.mojing.MojingSDKServiceManager;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muyu on 2016/3/28.
 */
public abstract class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";
    // loading对话框启动次数
    private int mProgressDialogCount = 0;
    // 自定义ProgressDialog
    private CustomProgressDialog mProgressDialog;
    private boolean isVisible;

    private HierarchyBean hierarchyBean;//层级关系bean
    private LandscapeUrlBean landscapeUrlBean;
    private HierarchyBean.HeadData headData;
    private HierarchyBean.Local local;

    private boolean enableMojingSDKService = false;
    public String detailUrl;
    public String subActivityName;
    public int type;
    public int subType;
    public String contents;
    public String nav;

    public int currentNavId; //二级层级id号

    public String headUrl = "1/nav-index.js"; //首页入口
    public int currentNavID; //一级层级id号

    public String name;
    public String localPath;

    public String currentMenuName;
    private PushAgent mPushAgent;

    /**
     * 初始化横竖屏对应压栈对象
     */
    public void initBaseBean(){
        if(hierarchyBean == null){
            hierarchyBean = new HierarchyBean();//层级关系bean
        }
        if(landscapeUrlBean == null){
            landscapeUrlBean = new LandscapeUrlBean();
        }
        if(headData == null){
            headData = new HierarchyBean.HeadData();
        }
        if(local == null){
            local = new HierarchyBean.Local();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.e("infossss","mIsInit=="+DownloadUtils.getInstance().mIsInit);
        if(!DownloadUtils.getInstance().mIsInit){
            BaseApplication.INSTANCE.registerNetWorkReceiver();
        }
        BaseApplication.INSTANCE.addActivity(this);
        BaseApplication.INSTANCE.setOrientationMode(true);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if(BaseApplication.INSTANCE.isBFMJ5Connection()){
//            BaseApplication.INSTANCE.toLandscape();
//        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MojingSDKServiceManager.onResumeNoTrackerMode(this);
        //BaseApplication.INSTANCE.setEnableMojingSDKService(true);
        BaseApplication.INSTANCE.setOrientationMode(true);
        enableMojingSDKService  = true;
        MojingSDKReport.onResume(this);
        LogHelper.e("infossss","mIsInit=="+DownloadUtils.getInstance().mIsInit);
        if(!DownloadUtils.getInstance().mIsInit){
            BaseApplication.INSTANCE.registerNetWorkReceiver();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if(enableMojingSDKService) {
            MojingSDKServiceManager.onPause(this);
            enableMojingSDKService = false;
        }
        MojingSDKReport.onPause(this);
    }

    @Override
    public void finish() {
        super.finish();
        if(enableMojingSDKService) {
            MojingSDKServiceManager.onPause(this);
            enableMojingSDKService = false;
        }

    }

    @Override
    protected void onDestroy() {
        BaseApplication.INSTANCE.removeActivty(this);
        BaseApplication.INSTANCE.removeHierarchy(hierarchyBean);
        super.onDestroy();

    }

    /**
     * 20170324版本去掉大多数横竖屏跳转逻辑，调用此方法是，在初始化对象
     */
    public void initHierarchy(){
        initBaseBean();
        hierarchyBean.setSubActivityName(subActivityName);
        hierarchyBean.setDetailUrl(detailUrl);
        hierarchyBean.setType(type + "");
        hierarchyBean.setSubType(subType + "");
        hierarchyBean.setCurrentNavId(currentNavId + "");

        landscapeUrlBean.setContents(contents);
        landscapeUrlBean.setNav(nav);
        hierarchyBean.setLandscapeUrlBean(landscapeUrlBean);

        headData.setHeadUrl(headUrl);
        headData.setCurrentNavId(currentNavID + "");
        hierarchyBean.setHeadData(headData);

        local.setName(name);
        local.setLocalPath(localPath);
        hierarchyBean.setLocal(local);

        int count = BaseApplication.INSTANCE.hierarchyBeanList.size();
        if (count != 0) {
            String name = BaseApplication.INSTANCE.hierarchyBeanList.get(count - 1).getSubActivityName();
            if (!TextUtils.isEmpty(name) && name.equals(subActivityName)) {
                BaseApplication.INSTANCE.hierarchyBeanList.remove(count - 1);
            }
        }
        BaseApplication.INSTANCE.addHierarchy(hierarchyBean);
//        String str = JSON.toJSONString(BaseApplication.INSTANCE.hierarchyBeanList);
    }

    /**
     * 显示Loading对话框
     */
    public void showProgressDialog(String message) {
        mProgressDialogCount++;
        checkProgressDialog();
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()&&!isFinishing()) {
            mProgressDialog.show();
        }
    }

    private synchronized void checkProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
    }

    /**
     * 显示一个默认的Loading对话框
     */
    public void showProgressDialog() {
        showProgressDialog("正在加载……");
    }

    /**
     * 显示一个Loading对话框
     *
     * @param resId string资源id
     */
    public void showProgressDialog(int resId) {
        showProgressDialog(getString(resId));
    }

    public CustomProgressDialog getProgressDialog() {
        checkProgressDialog();
        return mProgressDialog;
    }

    /**
     * 隐藏加载Dialog
     */
    public void dismissProgressDialog() {
        mProgressDialogCount--;
        if (mProgressDialogCount < 0) {
            mProgressDialogCount = 0;
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()
                && mProgressDialogCount == 0) {

            try {//某些情况下，对话框未加载就被取消有可能抛出异常
                mProgressDialog.dismiss();
            } catch (Exception e) {}
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    //压入播放记录
    public void initPlayParam(String type,String subType,String detailUrl,String contents,String nav, String name, String localPath,String seq, String pageType){
        initBaseBean();
        hierarchyBean.setSubActivityName("GoUnity");
        hierarchyBean.setType(type + "");
        hierarchyBean.setSubType(subType + "");
        hierarchyBean.setDetailUrl(detailUrl);
        hierarchyBean.setCurrentVideoIndex(seq);
        landscapeUrlBean.setContents(contents);
        landscapeUrlBean.setNav(nav);
        hierarchyBean.setLandscapeUrlBean(landscapeUrlBean);
        local.setName(name);
        local.setLocalPath(localPath);
        hierarchyBean.setLocal(local);
        hierarchyBean.setPageType(pageType);
        BaseApplication.INSTANCE.addHierarchy(hierarchyBean);
    }

    //压入播放记录
    public void initPlayParam(String type,String subType,String detailUrl,String contents,String nav, String name, String localPath,String seq, String download_url, String local_resource_from, String videoType, String pageType, String online_resource_from){
        initPlayParam(type, subType, detailUrl, contents, nav,
                 name,  localPath, seq,  download_url,
                 local_resource_from,  videoType,  pageType,  online_resource_from, "0");
    }

    //压入播放记录
    public void initPlayParam(String type,String subType,String detailUrl,String contents,String nav,
                              String name, String localPath,String seq, String download_url,
                              String local_resource_from, String videoType, String pageType, String online_resource_from, String is4k){
        initPlayParam(type, subType, detailUrl, contents, nav, name, localPath, seq, pageType);
        local.setDownload_url(download_url);
        local.setLocal_resource_from(local_resource_from);
        local.setVideo_type(videoType);
        local.setIs4k(is4k);
        hierarchyBean.setOnline_resource_from(online_resource_from);
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(DownloadItem downloadItem){
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize,List<DownloadItem> downloadItemList){
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop(){
        super.onStop();
//        if(!ApplicationUtil.mojingAppInForeground()){
//            if(BaseApplication.INSTANCE.isEnableToLandscapeCondition1()){
//                BaseApplication.INSTANCE.setEnableToLandscapeCondition2(true);
//
//            }
//        }
    }
}

