package com.baofeng.mj.pubblico.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.bean.MainTabBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.accountbusiness.AppUpdateBusiness;
import com.baofeng.mj.business.accountbusiness.ExperienceReportBusiness;
import com.baofeng.mj.business.brbusiness.DeleteDownloadingReceiver;
import com.baofeng.mj.business.brbusiness.NetworkChangeReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusinessForScheme;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.pluginbusiness.PluginDownloadBusiness;
import com.baofeng.mj.business.pluginbusiness.PluginUIBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.baofeng.mj.ui.activity.GoUnity;
import com.baofeng.mj.ui.activity.TaskListActivity;
import com.baofeng.mj.ui.activity.WebExperienceReportActivity;
import com.baofeng.mj.ui.fragment.LocalFragment;
import com.baofeng.mj.ui.fragment.RecommendFragement;
import com.baofeng.mj.ui.view.MyFragmentTabHost;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.ApplicationUtil;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.publicutil.SchemeOpenUtil;
import com.baofeng.mj.util.systemutil.AppAliveUtil;
import com.baofeng.mj.util.viewutil.MainTabUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.iflytek.thirdparty.E;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序主页面 （项目主页面更改为FragmentTabHost，更改请求数据时机--- by liuchuanchi）
 * Created by muyu on 2016/3/28.
 */
public class MainActivityGroup extends BaseActivity implements TabHost.OnTabChangeListener {
    private MyFragmentTabHost myFragmentTabHost;
    private List<MainTabBean<List<MainSubTabBean>>> mainTabBeanList;
    private long exitTime = 0;
    private int mCurrentTab = 0;
    private View localTabView;
    private View appStoreTabView;
    private TextView localTabRedPoint;
    private LocalFragment localFragment;

    /**
     * 新的外部打开协议（各个字段值查询SchemeOpenUtil类）
     */
    private String schemeAction;//打开方式
    private String schemeType;//资源类型
    private String schemePlayUrl;//资源地址
    private String schemeResource;//来源
    private String schemeFromWhere;//来自哪儿
    private String schemeFromAppName;//应用名称
    /**
     * 老的外部打开协议
     */
    private int fromWhere = PushTypeBusiness.from_where_normal;
    private String fromAppName;//来自第三方应用的名称
    private String operateJson;//操作json
    /**
     * 通知
     */
    private String notifyId;//通知id
    private String linkType;//链接类型
    private String redirectId;//跳转id
    private String redirectUrl;//跳转url
    private String resourceTypeParent;//资源类型
    private String resourceType;//资源子类型


    private boolean isOpenByNewIntent = false;
    private int downloadingSize = 0;//资源正在下载的个数（不包括暂停的）

    private DeleteDownloadingReceiver.DeleteDownloadingNotify deleteDownloadingNotify;
    private DeleteDownloadingReceiver deleteDownloadingReceiver;
    private NetworkChangeReceiver networkChangedReceiver;//监听网络连接
    private List<DownloadItem> downloadingList = new ArrayList<>();
    /**
     * 置空
     */
    private void resetNull(){
        if(myFragmentTabHost != null){
            myFragmentTabHost = null;
        }
        if(mainTabBeanList != null){
            mainTabBeanList = null;
        }
        if(localTabView != null){
            localTabView = null;
        }
        if(appStoreTabView != null){
            appStoreTabView = null;
        }
        if(localTabRedPoint != null){
            localTabRedPoint = null;
        }
        if(localFragment != null){
            localFragment = null;
        }
    }

    @Override
    protected void onDestroy() {
        resetNull();//置空
        deleteDownloadingReceiver.removeDeleteDownloadingNotify(deleteDownloadingNotify);
        MjDownloadSDK.stopAll(this);
//        DownloadUtils.getInstance().onDestroy();
        DownloadUtils.getInstance().mIsInit = false;
        PluginUIBusiness.getmInstance().destory();
        super.onDestroy();
        LogHelper.e("infosss","========main=onDestroy===========");
    }

    private long mCurTime;
    private long mLastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        BaseApplication.INSTANCE.setAppUddateBusiness(new AppUpdateBusiness());
        DownloadUtils.getInstance().getAllData();
//        BaseApplication.INSTANCE.registerNetWorkReceiver();
        processIntent();//处理意图
        SettingSpBusiness.getInstance().setNeedUpdate(false);
        myFragmentTabHost = (MyFragmentTabHost) findViewById(android.R.id.tabhost);
        myFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        myFragmentTabHost.setOnTabChangedListener(this);
        MainTabUtil.initMainTab(this, myFragmentTabHost);//初始化主tab

        myFragmentTabHost.getTabWidget().getChildTabViewAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!----------单击");
    //                    mLastTime = mCurTime;
    //                    mCurTime = System.currentTimeMillis();
    //                    if (mCurTime - mLastTime < 500) {
    //
    //                        System.out.println("!!!!!!!!!!!!---------双击");
                            String currentTabTag = myFragmentTabHost.getCurrentTabTag();
                            Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(currentTabTag);
                            if(fragmentByTag instanceof RecommendFragement) {
                                ((RecommendFragement)fragmentByTag).resetRecyclerView();
                            }
                            return false;
    //                    }
    //                    break;

                    default:
                        break;
                }

                return false;
            }
        });
        if (savedInstanceState == null) {//切换当前页面
            switchCurrentTab();
        } else {//恢复实例状态
            restoreInstanceState(savedInstanceState);
        }
        checkPermission();
        showGuide();
//        PushBusiness.getInstance().receiverPush();//接收push oom
        ExperienceReportBusiness.getInstance().getExperienceReportData(this);
        SettingSpBusiness.getInstance().clearTabInfo();
        //渠道审核
        String channelId = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        if(ChannelUtil.CHANNEL_CODE_OPPO.equals(channelId)){//OPPO渠道，无论审核是否通过，都隐藏
            appStoreTabView.setVisibility(View.GONE);//隐藏应用市场tab
        }else if(BaseApplication.INSTANCE.channelCheckState == 1){//审核通过
            appStoreTabView.setVisibility(View.VISIBLE);//显示应用市场tab
        }else{//审核中
            appStoreTabView.setVisibility(View.GONE);//隐藏应用市场tab
        }

        initDeleteDownloadReceiver();

        //启动插件服务
        PluginUIBusiness.getmInstance();
        PluginDownloadBusiness.getmInstance().checkRequestPluginData();
        LogHelper.e("infosss","========main oncreate============");
    }


    private void initDeleteDownloadReceiver(){
        deleteDownloadingReceiver = new DeleteDownloadingReceiver();
        deleteDownloadingNotify = new DeleteDownloadingReceiver.DeleteDownloadingNotify() {
            @Override
            public void deleteNotify(DownloadItem downloadItem) {
               //处理红点显示下载数量问题
                int loadingSize = 0;
                for(int i = 0;i<downloadingList.size();i++){
                    if(downloadingList.get(i).getDownloadState() == MjDownloadStatus.DOWNLOADING){
                        loadingSize++;
                    }
                }
                if(loadingSize <= 1){//由于回调会停止
                    if(null != localTabRedPoint){
                        localTabRedPoint.setVisibility(View.GONE);
                    }
                    if (localFragment != null) {//LocalFragment还可能为空
                        localFragment.showRedPoint(0);
                    }
                }
            }
        };
        deleteDownloadingReceiver.addDeleteDownloadingNotify(deleteDownloadingNotify);
    }

	/**
	 * 信鸽跳转详情页面
     */
    private void XgGuidActivity(){

		Bundle bundle = getIntent().getBundleExtra("launchBundle");
		if(bundle != null){
			//如果bundle存在，取出其中的参数，启动DetailActivity
			String guid_activity = bundle.getString("guid_activity");
			String next_url = bundle.getString("next_url");
			String next_type = bundle.getString("next_type");
			String subType = bundle.getString("subType");
			AppAliveUtil.startDetailActivity(this, guid_activity, next_url, Integer.valueOf(next_type), Integer.valueOf(subType));
		}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isOpenByNewIntent = true;
        setIntent(intent);
        processIntent();//处理意图
    }

    private void showGuide(){
        if(!SettingSpBusiness.getInstance().getFinishGuide()) {
            final ViewStub stub = (ViewStub) findViewById(R.id.main_guide_viewstub);
            stub.inflate();
            findViewById(R.id.guide_content_bg).setOnClickListener(null);

            ImageView imageView = (ImageView) findViewById(R.id.guide_startvr_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SettingSpBusiness.getInstance().setFinishGuide(true);
                    stub.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAPPUpdate();
        BaseApplication.INSTANCE.addMainActivityGroup(this);
        registerReceiver(deleteDownloadingReceiver, new IntentFilter(DeleteDownloadingReceiver.ACTION_DELETE_DOWNLOADING));

//        if (isOpenByNewIntent){
//            isOpenByNewIntent = false;
//            switchCurrentTab();//切换当前页面
//        }
        LogHelper.e("infosssss","localTabRedPoint=="+localTabRedPoint);
        LogHelper.e("infosssss","localTabRedPoint=="+localTabRedPoint);
        if(null != localTabRedPoint ){
            getLoadingList();
        }
        LogHelper.e("infossss","========MainActivityGroup=onResume============");
        Log.w("px","main  "+BaseApplication.isFromUnityOrStartApp);
        if(BaseApplication.isFromUnityOrStartApp){

            ArrayList<DownloadItem> list = DownloadUtils.getInstance().getAllDownLoadings(BaseApplication.INSTANCE);
            for(int i = 0;i<list.size();i++){
                Log.w("px","list  "+list.get(i).getDownloadState());
                if(list.get(i).getDownloadState() == MjDownloadStatus.DOWNLOADING
                        || list.get(i).getDownloadState() == MjDownloadStatus.CONNECTING
                        || list.get(i).getDownloadState() == MjDownloadStatus.DEFAULT){
                    DemoUtils.startDownload(BaseApplication.INSTANCE,list.get(i));
                }
            }
            BaseApplication.isFromUnityOrStartApp = false;
        }
    }

    /**
     * 处理意图
     */
    private void processIntent() {
        mCurrentTab = SettingSpBusiness.getInstance().getMCurrentTab();
        Intent intent = getIntent();
        if (intent != null) {
            fromWhere = intent.getIntExtra(PushTypeBusiness.FROM_WHERE, PushTypeBusiness.from_where_normal);
            fromAppName = intent.getStringExtra(PushTypeBusiness.FROM_APP_NAME);
            operateJson = intent.getStringExtra(PushTypeBusiness.OPERATE_JSON);
            notifyId = intent.getStringExtra(PushTypeBusiness.NOTIFY_ID);
            linkType = intent.getStringExtra(PushTypeBusiness.LINK_TYPE);
            redirectId = intent.getStringExtra(PushTypeBusiness.REDIRECT_ID);
            redirectUrl = intent.getStringExtra(PushTypeBusiness.REDIRECT_URL);
            resourceTypeParent = intent.getStringExtra(PushTypeBusiness.RESOURCE_TYPE_PARENT);
            resourceType = intent.getStringExtra(PushTypeBusiness.RESOURCE_TYPE);
            schemeAction = null;//置为默认值
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                if (uri != null) {
                    schemeAction = uri.getQueryParameter("action");
                    schemeType = uri.getQueryParameter("type");
                    schemePlayUrl = uri.getQueryParameter("playurl");
                    schemeResource = uri.getQueryParameter("resource");
                    schemeFromWhere = uri.getQueryParameter("fromscheme");
                    schemeFromAppName = uri.getQueryParameter("fromappname");
                }
            }
        }
    }

    /**
     * 处理scheme协议
     */
    private void processScheme(){
        if(!TextUtils.isEmpty(schemePlayUrl)){
            String serviceUrlSuffix = ConfigUrl.getServiceUrlSuffix();
            int index = schemePlayUrl.indexOf(serviceUrlSuffix);
            if(index >= 0){
                schemePlayUrl = schemePlayUrl.substring(index + serviceUrlSuffix.length());
            }
        }
        if(SchemeOpenUtil.action_open_vr.equals(schemeAction)){//打开横屏
            startActivity(new Intent(this, GoUnity.class));
        } else if (SchemeOpenUtil.action_open.equals(schemeAction)) {//打开竖屏
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            if(SchemeOpenUtil.type_game.equals(schemeType)){//游戏
                ResTypeUtil.onClickToGameDetail(this, ResTypeUtil.res_type_game, 0, schemePlayUrl, "",false, null);//进入游戏详情页
            }else if(SchemeOpenUtil.type_app.equals(schemeType)){//应用
                ResTypeUtil.onClickToGameDetail(this, ResTypeUtil.res_type_apply, 0, schemePlayUrl, "", false, null);//进入游戏详情页
            }else if(SchemeOpenUtil.type_special.equals(schemeType)){//专题
                ResTypeUtil.onClickToTopicActivity(this, ResTypeUtil.res_type_special, 0, schemePlayUrl, false, null);//进入专题详情页
            }else if(SchemeOpenUtil.type_panorama.equals(schemeType)){//全景视频
                ResTypeUtil.onClickToPanoramaDetailActivity(this, ResTypeUtil.res_type_video, 0, schemePlayUrl, false);//进入全景详情页
            }else if(SchemeOpenUtil.type_image.equals(schemeType)){//全景图片
                ResTypeUtil.onClickToPanoramaDetailActivity(this, ResTypeUtil.res_type_image, 0, schemePlayUrl, false);//进入全景详情页
            }else if(SchemeOpenUtil.type_video_2d_3d.equals(schemeType)){//普通2d,3d视频
                ResTypeUtil.onClickToVideoDetailActivity(this, ResTypeUtil.res_type_movie, 0, schemePlayUrl, false);//进入影视详情页
            }
        } else if (SchemeOpenUtil.action_download.equals(schemeAction)) {//下载
            myFragmentTabHost.setCurrentTab(MainTabUtil.LOCAL);
            LocalFragment localFragment = (LocalFragment) getFragment(MainTabUtil.LOCAL);
            if(localFragment != null){
                localFragment.setCurrentItem(1);
                DownLoadBusinessForScheme.requestDetailInfo(this, SchemeOpenUtil.getResType(schemeType), schemePlayUrl);
            }
        } else {//播放
            if(schemeResource.equals(SchemeOpenUtil.resource_official)){//官方资源
                if(SchemeOpenUtil.type_video_2d_3d.equals(schemeType)){//普通2d,3d视频
                    StartActivityHelper.startVideoGoUnity(this, schemePlayUrl, "", "", "0", "");
                }else{//全景视频，全景图片
                    StartActivityHelper.startPanoramaGoUnity(this, SchemeOpenUtil.getResType(schemeType), schemePlayUrl, "", "", "", StartActivityHelper.online_resource_from_default);
                }
            }else{//外部资源
                StartActivityHelper.playVideoWithNetDisk(this, "", schemePlayUrl);
            }
        }
    }

    /**
     * 切换当前页面
     */
    private void switchCurrentTab() {
        if (TextUtils.isEmpty(linkType)) {
            linkType = PushTypeBusiness.link_home;
        }
        if (fromWhere == PushTypeBusiness.from_where_out) {//从第三方应用进入的主页面
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
        } else if (fromWhere == PushTypeBusiness.from_where_landscape_local) {//从横屏进入竖屏本地页面
            myFragmentTabHost.setCurrentTab(MainTabUtil.LOCAL);
        }else if (fromWhere == PushTypeBusiness.from_where_landscape_download) {//从横屏进入竖屏下载页面
            myFragmentTabHost.setCurrentTab(MainTabUtil.LOCAL);
            LocalFragment localFragment = (LocalFragment) getFragment(MainTabUtil.LOCAL);
            if(localFragment != null) {
                localFragment.setCurrentItem(1);
                ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
            }
            operateJson = null;
        } else if (!TextUtils.isEmpty(schemeAction)) {//来自scheme协议
            processScheme();//处理scheme协议
        } else if (linkType.equals(PushTypeBusiness.link_home)) {//首页
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
        } else if (linkType.equals(PushTypeBusiness.link_special)) {//专题页
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            ResTypeUtil.onClickToTopicActivity(this, ResTypeUtil.res_type_special, 0, redirectUrl, false, null);//进入专题页
        } else if (linkType.equals(PushTypeBusiness.link_detail)) {
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            int resType = Integer.valueOf(resourceTypeParent);//资源类型
            if(ResTypeUtil.res_type_movie == resType){//影视
                ResTypeUtil.onClickToVideoDetailActivity(this, ResTypeUtil.res_type_movie, 0, redirectUrl, false);//进入影视详情页
            }else if(ResTypeUtil.res_type_image == resType
                    || ResTypeUtil.res_type_roaming == resType
                    || ResTypeUtil.res_type_video == resType){//全景图片，全景漫游，全景视频
                ResTypeUtil.onClickToPanoramaDetailActivity(this, resType, 0, redirectUrl, false);//进入全景详情页
            }else if(ResTypeUtil.res_type_live == resType){//直播
                ResTypeUtil.onClickToLiveDetailActivity(this, resType, 0, redirectUrl, false);//进入直播页
            }else if(ResTypeUtil.res_type_apply == resType
                    || ResTypeUtil.res_type_game == resType){//应用，游戏
                ResTypeUtil.onClickToH5Activity(this, redirectId, redirectUrl, false);
            }
        } else if (linkType.equals(PushTypeBusiness.link_experience_report)) {//体验报告页
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            if (UserSpBusiness.getInstance().isUserLogin()) {//已经登录
                startActivity(new Intent(this, WebExperienceReportActivity.class));
            } else {//未登录
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        } else if (linkType.equals(PushTypeBusiness.link_task)) {//任务页
            myFragmentTabHost.setCurrentTab(MainTabUtil.HOME);
            if (UserSpBusiness.getInstance().isUserLogin()) {//已经登录
                startActivity(new Intent(this, TaskListActivity.class));
            } else {//未登录
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        } else {
            myFragmentTabHost.setCurrentTab(mCurrentTab);
            ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
        }

        if (!TextUtils.isEmpty(notifyId)) {
            //报数
            ReportBusiness.getInstance().reportPushClick(notifyId, "", linkType);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        onTabChanged(myFragmentTabHost.getCurrentTab());
    }

    public void onTabChanged(int position) {
        final int size = myFragmentTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View view = myFragmentTabHost.getTabWidget().getChildAt(i);
            if (i == position) {
                view.setSelected(true);
                this.mCurrentTab = position;
                SettingSpBusiness.getInstance().setMCurrentTab(mCurrentTab);//保存当前位置
            } else {
                view.setSelected(false);
            }
        }
        reportPV(position);
    }

    private void reportPV(int position){  //0,1,2,3,4,5
        int dataListPosition = position - 1; //推荐页面接口和其他的不同，接口数据0，对应页面Tab 1
        if(appStoreTabView!=null&&appStoreTabView.getVisibility()==View.GONE){
           if(dataListPosition>MainTabUtil.APPGAME){
               dataListPosition  = dataListPosition-1;
           }
        }
        if(dataListPosition == MainTabUtil.RECOMMEND){
            currentNavId = 1; //推荐页面单独请求一个接口，没看到有resId字段
            currentMenuName = "推荐";
            ReportBusiness.getInstance().reportPV(dataListPosition, currentNavId, currentMenuName);
        } else if (mainTabBeanList != null) {
            switch (dataListPosition) {
                case MainTabUtil.HOME:
                case MainTabUtil.APPGAME://视频页面和应用市场页面接口中有resId
                    if(mainTabBeanList!=null&&mainTabBeanList.size()>0) {
                        int subPositon = SettingSpBusiness.getInstance().getSubTabPosition(dataListPosition);  //获取当前Tab里面的子Tab位置
                        if(mainTabBeanList.get(dataListPosition).getPages()!=null&&mainTabBeanList.get(dataListPosition).getPages().size()>subPositon) {
                            currentNavId = mainTabBeanList.get(dataListPosition).getPages().get(subPositon).getRes_id(); //获取到对应的ResId
                            currentMenuName = mainTabBeanList.get(dataListPosition).getPages().get(subPositon).getTitle();
                        }
                    }
                    break;
                case MainTabUtil.LOCAL:
                case MainTabUtil.ACCOUNT:
                    currentNavId = 1; //本地页面和我的页面报数写死1
                    if(mainTabBeanList!=null&&mainTabBeanList.size()>0) {
                        currentMenuName = mainTabBeanList.get(dataListPosition).getTitle();
                    }
                    break;
            }
            ReportBusiness.getInstance().reportPV(dataListPosition, currentNavId, currentMenuName);
        }
    }

    /**
     * 恢复实例状态
     */
    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentTab = savedInstanceState.getInt(ConstantKey.M_CURRENT_TAB);
            fromWhere = savedInstanceState.getInt(PushTypeBusiness.FROM_WHERE);
            fromAppName = savedInstanceState.getString(PushTypeBusiness.FROM_APP_NAME);
            linkType = savedInstanceState.getString(PushTypeBusiness.LINK_TYPE);
            redirectId = savedInstanceState.getString(PushTypeBusiness.REDIRECT_ID);
            redirectUrl = savedInstanceState.getString(PushTypeBusiness.REDIRECT_URL);
            resourceTypeParent = savedInstanceState.getString(PushTypeBusiness.RESOURCE_TYPE_PARENT);
            resourceType = savedInstanceState.getString(PushTypeBusiness.RESOURCE_TYPE);
            myFragmentTabHost.setCurrentTab(mCurrentTab);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        outState.putInt(ConstantKey.M_CURRENT_TAB, mCurrentTab);
        outState.putInt(PushTypeBusiness.FROM_WHERE, fromWhere);
        outState.putString(PushTypeBusiness.FROM_APP_NAME, fromAppName);
        outState.putString(PushTypeBusiness.LINK_TYPE, linkType);
        outState.putString(PushTypeBusiness.REDIRECT_ID, redirectId);
        outState.putString(PushTypeBusiness.REDIRECT_URL, redirectUrl);
        outState.putString(PushTypeBusiness.RESOURCE_TYPE_PARENT, resourceTypeParent);
        outState.putString(PushTypeBusiness.RESOURCE_TYPE, resourceType);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(this, "再按一次退出暴风魔镜", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ApplicationUtil.exitApp();//退出app
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkAPPUpdate() {
        BaseApplication.INSTANCE.getAppUpdateBusiness().checkUpdate(this, true, true, true);
    }

    private void checkPermission(){
        CheckPermission.from(this)
                .setPermissions(PermissionUtil.ALL_PERMISSIONS)
                .setPermissionListener(new PermissionListener(){
                    @Override
                    public void permissionGranted() {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivityGroup.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }
                    @Override
                    public void permissionDenied() {
                        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(MainActivityGroup.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }
                }).check();
    }

    @Override
    protected void onPause() {
        try {
            if (BaseApplication.INSTANCE.getAppUpdateBusiness().getAppUpdateDialog(this).isProgressShowing()) {
                BaseApplication.INSTANCE.getAppUpdateBusiness().setIsClickHome(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        BaseApplication.INSTANCE.getAppUpdateBusiness().dismisssUpdateProgressDialog();
        BaseApplication.INSTANCE.removeMainActivityGroup();
        unregisterReceiver(deleteDownloadingReceiver);
        super.onPause();


    }

    /**
     * 显示小红点
     */
    public void showRedPoint(List<DownloadItem> downloadItemList){
        synchronized (downloadingList) {
            downloadingSize = 0;
            downloadingList.clear();
            downloadingList.addAll(downloadItemList);
            if (!downloadingList.isEmpty()) {
                for (int i = 0; i < downloadingList.size(); i++) {
                    try {
                        if (downloadingList.get(i) != null) {
                            if (MjDownloadStatus.DOWNLOADING == downloadingList.get(i).getDownloadState()) {
                                downloadingSize++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (localTabRedPoint != null) {
                    showRedPoint(localTabRedPoint);
                } else {
                    if (localTabView != null) {
                        localTabRedPoint = (TextView) localTabView.findViewById(R.id.tv_red_point);
                        showRedPoint(localTabRedPoint);
                    }
                }
                if (localFragment == null) {
                    localFragment = (LocalFragment) getFragment(MainTabUtil.LOCAL);//此时LocalFragment可能还没实例化
                }
                if (localFragment != null) {//LocalFragment还可能为空
                    localFragment.showRedPoint(downloadingSize);
                }
            }
        });
        }
    }

    /**
     * 显示小红点
     */
    public void showRedPoint(TextView tv_red_point) {
//        getLoadingSize();
        if(downloadingSize == 0){
            tv_red_point.setVisibility(View.GONE);
        }else{
            tv_red_point.setVisibility(View.VISIBLE);
            if(downloadingSize <= 99){
                tv_red_point.setText(String.valueOf(downloadingSize));
            }else{
                tv_red_point.setText("99+");
            }
        }
    }

    public Fragment getFragment(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                MainTabUtil.getFragmentTabTag(this, position));
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(
                myFragmentTabHost.getCurrentTabTag());
    }

    public int getCurrentTab(){
        return mCurrentTab;
    }

    public void setLocalTabView(View view){
        localTabView = view;
    }

    public void setAppStoreTabView(View view){
        appStoreTabView = view;
    }

    /**
     * 获取mainTab对应的资源id(给搜索功能用)
     */
    public String getMainTabResId(){
        if(mainTabBeanList == null || mainTabBeanList.size() == 0 || mCurrentTab >= mainTabBeanList.size() || mCurrentTab == 0){
            return "0";
        }
        return String.valueOf(mainTabBeanList.get(mCurrentTab -1 ).getRes_id());
    }

    /**
     * 获取mainTabBeanList
     * @param mainTabBeanListCallback 回调
     */
    public void getMainTabBeanList(final MainTabBeanListCallback mainTabBeanListCallback){
        if(mainTabBeanList == null){//请求接口数据
            new ChoicenessApi().getMainTabList(this, new ApiCallBack<ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>>>() {

                @Override
                public void onSuccess(ResponseBaseBean<List<MainTabBean<List<MainSubTabBean>>>> result) {
                    if (result != null && result.getStatus() == 0) {
                        mainTabBeanList = result.getData();
                    }
                    mainTabBeanListCallback.callback(mainTabBeanList);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    mainTabBeanListCallback.callback(mainTabBeanList);
                }
            });
        }else{
            mainTabBeanListCallback.callback(mainTabBeanList);
        }
    }

    /**
     * mainTabBeanList回调
     */
    public interface MainTabBeanListCallback{
        void callback(List<MainTabBean<List<MainSubTabBean>>> mainTabBeanList);
    }

    private void getLoadingList(){
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(DownloadUtils.getInstance().getAllDownLoadsByState(this,MjDownloadStatus.DOWNLOADING,true));
        if(null != localTabRedPoint && list.isEmpty()){
            localTabRedPoint.setVisibility(View.GONE);
            if (localFragment != null) {//LocalFragment还可能为空
                localFragment.showRedPoint(0);
            }
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (isOpenByNewIntent){
            isOpenByNewIntent = false;
            switchCurrentTab();//切换当前页面
        }
        LogHelper.e("infossss","========MainActivityGroup=onResumeFragments============");
    }

    public ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public interface MyOnTouchListener {
        public boolean dispatchTouchEvent(MotionEvent ev);
    }
}
