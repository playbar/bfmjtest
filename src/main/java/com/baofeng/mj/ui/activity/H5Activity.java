package com.baofeng.mj.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SearchSpBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.dialog.GameOpenDialog;
import com.baofeng.mj.ui.dialog.StickGameDownloadDialog;
import com.baofeng.mj.ui.view.AppH5TitleBackView;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.systemutil.UUidUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;
import com.sina.weibo.sdk.api.share.Base;
import com.storm.smart.common.utils.LogHelper;

import java.io.File;

/**
 * Created by muyu on 2016/6/24.
 */
public class H5Activity extends BaseActivity {

    private WebView mWebView;
    private AppH5TitleBackView backView;
    private String loadUrl;
    private String titleName;
    private String uuid = "";
    private String userId = "";
    private  String packageName;
    private EmptyView emptyView;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private CustomProgressView progressView;
    private String resId;
    private StickGameDownloadDialog downloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        BaseApplication.INSTANCE.setBaseActivity(this);
        initUUId();
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UserSpBusiness.getInstance().isUserLogin()){
            UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
            mWebView.loadUrl("javascript:sendUserId('" + userInfo.getUid() + "')");
        }
    }

    private void initUUId(){
        checkPermission();
    }

    private void initUserId(){
        if (UserSpBusiness.getInstance().isUserLogin()) {
            UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
            userId = userInfo.getUid();
        }
    }

    private void checkPermission(){
        if(PermissionUtil.isOverMarshmallow()){
            CheckPermission.from(this)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setPermissionListener(new PermissionListener(){

                        @Override
                        public void permissionGranted() {
                            uuid = UUidUtil.getInstance().getUUID();
                        }

                        @Override
                        public void permissionDenied() {
                        }
                    }).check();
        } else{
            uuid = UUidUtil.getInstance().getUUID();
        }

    }

    private void initData(){
        type = getIntent().getIntExtra("next_type", 0);
        subType = getIntent().getIntExtra("next_subtype", 0);
        titleName = getIntent().getStringExtra("next_title");
        loadUrl = getIntent().getStringExtra("next_url");
        resId = getIntent().getStringExtra("next_resId");
        if(type == ResTypeUtil.res_type_apply || type == ResTypeUtil.res_type_game){ //游戏详情页面，拼接uuid和userid
            initUserId();
            packageName = getIntent().getStringExtra("next_packageName");
            loadUrl = loadUrl + "?uuid="+uuid+"&userId="+userId+"&resId="+resId;
        }
        if(!TextUtils.isEmpty(loadUrl)){
            if(!loadUrl.startsWith("http")){
                loadUrl = "http://" + loadUrl;
            }
        }
    }

    private void initView(){
        progressView = (CustomProgressView) findViewById(R.id.h5_loading);
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                mWebView.loadUrl("javascript:downloadType()");
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        emptyView = (EmptyView) findViewById(R.id.h5_empty_view);
        emptyView.getRefreshView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.networkEnable()) {
                    emptyView.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);
                    mWebView.loadUrl(loadUrl);
                } else {
                    Toast.makeText(H5Activity.this,"当前网络不可用，请稍候再试", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mWebView = (WebView) findViewById(R.id.h5_webview);
        backView = (AppH5TitleBackView) findViewById(R.id.h5_title_layout);
        backView.getBackImgBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoBack()){
                    mWebView.goBack();// 返回前一个页面
                }else {
                    finish();
                }
            }
        });
        backView.getNameTV().setText(titleName);
        WebSettings webSettings = mWebView.getSettings();
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置 缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // / 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //清除缓存
        mWebView.clearCache(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
        });
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //在前面加入下载监听器

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url != null && !(url.startsWith("http://") || url.startsWith("https://"))) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                emptyView.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressView.setVisibility(View.GONE);
            }
        });


//        mWebView.loadUrl("file:///android_asset/index.html");
        mWebView.loadUrl(loadUrl);
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "index");
        LogHelper.e("infosss","laodUrl=="+loadUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.INSTANCE.setBaseActivity(null);
        mWebView.clearCache(true);
        mWebView.clearSslPreferences();
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mWebView.canGoBack()){
                mWebView.goBack();// 返回前一个页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //下载完成
                String json = downloadItem.getPackageName();
                if (type == ResTypeUtil.res_type_apply || type == ResTypeUtil.res_type_game) {  //游戏详情页面下载完成只通知一个，其余页面所有下载均通知
                    if (packageName.equals(json)) {
                        mWebView.loadUrl("javascript:gameDownloaded('" + json + "')");
                    }
                } else {
                    mWebView.loadUrl("javascript:gameDownloaded('" + json + "')");
                }
            }
        });
    }

    /**
     * 开始下载回调
     * @param resId
     */
    public void beginDownLoadCallBack(final String resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:beginDownLoadCallBack('" + resId + "')");
            }
        });
    }


    private class MyWebViewDownLoadListener implements DownloadListener {
        //添加监听事件即可
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype,long contentLength)          {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        @JavascriptInterface
        public boolean isLogin(){ //返回是否登录
            return UserSpBusiness.getInstance().isUserLogin();
        }

        @JavascriptInterface
        public String getUUId(){ //获取uuid
            return uuid;
        }

        @JavascriptInterface
        public String getUserId(){ //获取用户Id
            if (UserSpBusiness.getInstance().isUserLogin()) {
                UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
                return userInfo.getUid();
            }
            return "";
        }

        @JavascriptInterface
        public void startLogin(){ //跳转登录页面
            startActivity(new Intent(H5Activity.this, LoginActivity.class));
        }

        /**
         * 返回给H5游戏资源的下载状态
         * @param json
         * @return
         */
        @JavascriptInterface
        public int isDownLoaded(String json){
            LogHelper.e("infosss","===isDownLoaded.json=="+json);
            DownloadItem gameDownloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
            DownloadItem downloadItem = BaseApplication.INSTANCE.getDownloadItemForGame(gameDownloadItem.getPackageName());//获取正在下载的DownloadItem
            if(downloadItem != null && downloadItem.getDownloadState() != MjDownloadStatus.COMPLETE){
                int status = downloadItem.getDownloadState();
                LogHelper.e("infossss","status=="+status);
                if(status == MjDownloadStatus.ABORT ){
                    return ApkUtil.PAUSE; //暂停中
                } else {
                    return ApkUtil.DOWNLOADING;//下载中
                }
            }
            File file = DownloadResBusiness.getDownloadResFile(gameDownloadItem.getDownloadType(), gameDownloadItem.getAid(), gameDownloadItem.getTitle(), gameDownloadItem.getHttpUrl());
            int status = ApkUtil.checkApk(file, gameDownloadItem.getPackageName(), Integer.parseInt(gameDownloadItem.getApkVersionCode()));
            return status;
        }

        @JavascriptInterface
        public void beginDownLoad(final String json){ //添加下载 完全按照游戏Bean来 GameDetailBean
            LogHelper.e("infosss","===beginDownLoad.json=="+json);
            final DownloadItem gameDownloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
            String playMode = gameDownloadItem.getPlay_mode();
            if((!TextUtils.isEmpty(playMode) && playMode.contains("6")) //此款游戏为体感游戏
                    && !SettingSpBusiness.getInstance().getGamenoMoreTips()){
                if (downloadDialog == null) {
                    downloadDialog = new StickGameDownloadDialog(H5Activity.this, new StickGameDownloadDialog.DownloadCallBack() {
                        @Override
                        public void onConfirm(boolean isChecked) { //不再提示，无需再计数
                            SettingSpBusiness.getInstance().setGameNoMoreTips(isChecked);
                            beginDownLoadCallBack(gameDownloadItem.getAid());
                            DemoUtils.startDownload(BaseApplication.INSTANCE, gameDownloadItem);//开始下载
                            String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading,gameDownloadItem.getTitle(), gameDownloadItem.getAid());
                            FileCommonUtil.writeFileString(json, baseInfoPath);//资源信息保存到正在下载文件夹
                            reportClick(gameDownloadItem.getDownloadType(), ApkUtil.NEED_DOWNLOAD, gameDownloadItem.getTitle(), gameDownloadItem.getAid());

                            if(!isChecked) {
                                int count = SettingSpBusiness.getInstance().getGameDownloadCount();
                                if (count >= 3) {
                                    SettingSpBusiness.getInstance().setGameNoMoreTips(true);
                                    return;
                                }
                                count = count + 1;
                                SettingSpBusiness.getInstance().setGameDownloadCount(count); //提示后连续点击三次下载以后不再提示
                            }
                        }

                        @Override
                        public void onCancel() {
                            SettingSpBusiness.getInstance().setGameDownloadCount(0);
                        }
                    });
                }
                downloadDialog.show();
            } else {
                beginDownLoadCallBack(gameDownloadItem.getAid());
                DemoUtils.startDownload(BaseApplication.INSTANCE, gameDownloadItem);//开始下载
                String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading,gameDownloadItem.getTitle(), gameDownloadItem.getAid());
                FileCommonUtil.writeFileString(json, baseInfoPath);//资源信息保存到正在下载文件夹
                reportClick(gameDownloadItem.getDownloadType(), ApkUtil.NEED_DOWNLOAD,gameDownloadItem.getTitle(),gameDownloadItem.getAid());
            }
        }

        @JavascriptInterface
        public void pauseDownLoad(String resId){ //暂停下载
            LogHelper.e("infosss","===pauseDownLoad=====");
            DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(resId);// 获取下载中的DownloadItem
            if(downloadingItem != null) {
                DemoUtils.pauseDownload(BaseApplication.INSTANCE, downloadingItem);//暂停下载
            }
        }

        @JavascriptInterface
        public void continueDownLoad(String resId){ //继续下载
            LogHelper.e("infosss","===continueDownLoad====="+resId);
            DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(resId);// 获取下载中的DownloadItem
            if(downloadingItem != null) {
                DemoUtils.startDownload(BaseApplication.INSTANCE, downloadingItem);//继续下载
            }
        }

        @JavascriptInterface
        public void installGame(String resId, String titleName, int type){ //应用和游戏
            LogHelper.e("infosss","===installGame====="+titleName+"==type=="+type+"==resid=="+resId);
            ApkUtil.installApk(H5Activity.this, type, resId, titleName);
            reportClick(type,ApkUtil.NEED_INSTALL, titleName, resId);
        }

        @JavascriptInterface
        public void installGame(String resId, String titleName){ //游戏
            LogHelper.e("infosss","===installGame=222===="+titleName+"==resid=="+resId);
            ApkUtil.installApk(H5Activity.this, ResTypeUtil.res_type_game, resId, titleName);
            reportClick(type,ApkUtil.NEED_INSTALL, titleName, resId);
        }

        @JavascriptInterface
        public void openGame(String json, String source){
            LogHelper.e("infosss","===openGame======="+json+"==source==="+source);
            DownloadItem gameInfo = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
//            File file = DownloadResBusiness.getDownloadResFile(gameInfo);//下载的资源文件
            String packageName = gameInfo.getPackageName();//游戏包名
            if(!source.equals("官方合作")&& SearchSpBusiness.getInstance().getGameOpenState(gameInfo.getAid())==0){
                new GameOpenDialog().showDialog(H5Activity.this,packageName,gameInfo.getAid());
            }else{
                ApkUtil.startPlayApk(H5Activity.this, packageName);

            }
            reportClick(gameInfo.getDownloadType(), ApkUtil.CAN_PLAY, gameInfo.getTitle(), gameInfo.getAid());
        }

        @JavascriptInterface
        public void updateGame(String json){
            LogHelper.e("infosss","===updateGame======="+json);
            DownloadItem gameDownloadItem = DownloadItemUtil.createDownloadItem(json);//创建下载实体类
//            DemoUtils.startDownload(BaseApplication.INSTANCE, gameDownloadItem);//开始下载
            DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE,gameDownloadItem);
            String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading,gameDownloadItem.getTitle(), gameDownloadItem.getAid());
            FileCommonUtil.writeFileString(json, baseInfoPath);//资源信息保存到正在下载文件夹
            reportClick(gameDownloadItem.getDownloadType(), ApkUtil.NEED_UPDATE, gameDownloadItem.getTitle(), gameDownloadItem.getAid());
        }

        @JavascriptInterface
        public boolean netWorkEnable(){
            return NetworkUtil.networkEnable();
        }

        /**
         * 更改H5页面Title
         * @param name title名字
         */
        @JavascriptInterface
        public void setTitle(String name){
            backView.getNameTV().setText(name);
        }


        @JavascriptInterface
        public String getVersionName(){
            return ApkUtil.getVersionNameSuffix();
        }

    }

    private void reportClick(int resType, int clickType,String title, String resid) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(ReportBusiness.getInstance().getClickType(clickType));
        bean.setTpos("1");
        if(resType == ResTypeUtil.res_type_apply || resType == ResTypeUtil.res_type_game) {
            bean.setPagetype("detail");
        }else {
            bean.setPagetype("game_active");
            bean.setActiveid(resId);
            bean.setActivetitle(titleName);
        }
        bean.setTitle(title);
        bean.setGameid(resid);
        ReportBusiness.getInstance().reportClick(bean);
    }
}
