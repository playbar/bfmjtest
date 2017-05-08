package com.baofeng.mj.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.TabOrderBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoSearchBusiness;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.SplashImgBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.view.FlashView;
import com.baofeng.mj.ui.view.RoundProgressBar;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.MainAPI;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.ChannelUtil;
import com.baofeng.mj.util.publicutil.DateUtil;
import com.baofeng.mj.util.threadutil.HistoryProxy;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.viewutil.MainTabUtil;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 打开应用闪屏页面
 * Created by muyu on 2016/4/7.
 */
public class SplashActivity extends Activity{
    private FlashView iv_splash;
    private FrameLayout fl_jump;
    private RoundProgressBar roundProgressBar;
    private boolean toMainActivity = true;
    private int JUMP_CUTDOWN_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.isFromUnityOrStartApp = true;
        setContentView(R.layout.activity_splash);
        LogHelper.e("infosss", "========spla oncreate============");
//        BaseApplication.INSTANCE.registerNetWorkReceiver();
        iv_splash = (FlashView) findViewById(R.id.iv_splash);
        fl_jump = (FrameLayout) findViewById(R.id.fl_jump);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        iv_splash.setBitmapRes(R.drawable.splash);//首先显示默认图

        // 添加判断 6.0及以上的 需要在splashActivity中请求权限后再初始化  add by whf 20161228
        if(!PermissionUtil.isOverMarshmallow()){
            init();
        }else {
            checkPermission();
        }
    }


    private void init(){
        if(BaseApplication.INSTANCE.isBFMJ5Connection())
        {
            BaseApplication.INSTANCE.toLandscape();
        }
        requestGlasses();

        showSplashImg();//显示splash图片
        SettingSpBusiness.getInstance().setMCurrentTab(MainTabUtil.HOME);//重置当前位置为推荐页
        new UserInfoApi().autoLogin(this);
        getTabOrder();
        channelCheck();//渠道审核
        saveUserInfoForAIO();//保存用户登录信息（一体机可能会传过来）
        FileStorageUtil.checkDownloadDir();
        //检查fileSearchMap
        LocalDownloadProxy.getInstance().addProxyRunnable(new LocalDownloadProxy.ProxyRunnable() {
            @Override
            public void run() {
                DownloadResInfoSearchBusiness.getInstance().checkFileSearchMap();
            }
        });
        //已经登录
        if(UserSpBusiness.getInstance().isUserLogin()){
            //上报在线播放历史
            HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                @Override
                public void run() {
                    HistoryBusiness.reportAllCinemaTempHistory();
                }
            });
        }
    }

    /**
     * 请求Tab顺序接口
     */
    private void getTabOrder(){
        new MainAPI().getTabOrderInfo(new ApiCallBack<Response<TabOrderBean>>() {
            @Override
            public void onSuccess(Response<TabOrderBean> result) {
                super.onSuccess(result);
                if(result.data != null && result.data.getIfTab() != 0) {
                    SettingSpBusiness.getInstance().setTabOrder(result.data.getIfTab());
                }
            }
        });
    }

    private void checkPermission(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            String[] ALL_PERMISSIONS = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(
                    SplashActivity.this,
                    ALL_PERMISSIONS,//需要请求的所有权限，这是个数组String[]
                    1001//请求码
            );
        }else {
            init();
        }
    }


    @TargetApi(value = 23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        JUMP_CUTDOWN_TIME = 500;
        init();
        BaseApplication.INSTANCE.init();

    }

    /**
     * 渠道审核
     */
    private void channelCheck(){
        new MainAPI().channelCheck(new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject joResult = new JSONObject(result);
                        if (0 == joResult.getInt("status")) {//请求成功
                            JSONObject joData = joResult.getJSONObject("data");
                            if (joData != null) {
                                if (joData.getBoolean("inreview")) {//审核中
                                    BaseApplication.INSTANCE.channelCheckState = 2;
                                }else{//审核通过
                                    BaseApplication.INSTANCE.channelCheckState = 1;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkPermission();
//            }
//        },2000);
	}

    private void requestGlasses(){
        String channelId = ChannelUtil.getChannelCode("DEVELOPER_CHANNEL_ID");
        if(!TextUtils.isEmpty(channelId)){
            if( Integer.parseInt(channelId) >= 3000) {
                SettingSpBusiness.getInstance().setFinishGuide(true);
                String cmsGlassesId = ChannelUtil.getChannelCode("GLASSES_ID");
                GlassesManager.setDefaultGlasses(BaseApplication.INSTANCE,channelId,cmsGlassesId,true);
            }
        }


    }

    /**
     * 显示splash图片
     */
    private void showSplashImg(){
        timerForJump();//读秒（跳过）
//        iv_splash.get().setImageResource(R.drawable.splash);//首先显示默认图
        File file = SplashImgBusiness.getSplashInfo();//获取splash信息文件
        if(file.exists()){//splash信息文件存在
            try {
                String splashInfo = FileCommonUtil.readFileString(file);//从文件读取splash信息
                JSONObject joSplashInfo = new JSONObject(splashInfo);
                JSONArray jaSplashInfo = joSplashInfo.getJSONArray("data");
                for(int i = 0; i < jaSplashInfo.length(); i++){
                    JSONObject joChild = jaSplashInfo.getJSONObject(i);
                    String startTime = joChild.getString("ptime_begin");//开始时间
                    String endTime = joChild.getString("ptime_end");//结束时间
                    String curTime = DateUtil.getCurTime();//当前时间
                    if(DateUtil.compareDate(curTime, DateUtil.date2String(Long.parseLong(startTime)*1000,"yyyy-MM-dd HH:mm:ss")) && DateUtil.compareDate(DateUtil.date2String(Long.parseLong(endTime)*1000,"yyyy-MM-dd HH:mm:ss"), curTime)){//当前图片在有效时间内
                        JSONArray imgUrls=joChild.getJSONArray("img_info");
                        if(imgUrls.length()<=0){
                            return;
                        }
                        String imageUrl = ((JSONObject)imgUrls.get(0)).getString("download_url");//图片url
                        final String jumpUrl = joChild.getString("url");//跳转url
                        File img = SplashImgBusiness.getSplashImg(imageUrl);//根据图片url，获取本地图片
                        if(img.exists()){//图片存在
//                            ImageLoader imageLoader = ImageLoaderUtils.getInstance().getImageLoader();
//                            imageLoader.displayImage("file://" + img.getAbsolutePath(), iv_splash);//加载图片
//                            GlideUtil.displayImageSplash(this, iv_splash, "file://" + img.getAbsolutePath(), R.drawable.splash);
                            iv_splash.setBitmapFilePath(img.getPath());
                            iv_splash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gotoH5Activity(jumpUrl);//进入H5Activity界面
                                }
                            });
                            fl_jump.setVisibility(View.VISIBLE);//跳转按钮
                            fl_jump.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    toMainActivity = false;
                                    gotoMainActivity();//进入主界面
                                }
                            });
                        }
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SplashImgBusiness.createSplashInfo();//创建splash信息
    }

    /**
     * 进入主界面
     */
    private void gotoMainActivity(){
        Intent newIntent;
        if(SettingSpBusiness.getInstance().getFinishGuide()){
            newIntent = new Intent(this, MainActivityGroup.class);
            //如果启动app的Intent中带有额外的参数，表明app是从点击通知栏的动作中启动的
            //将参数取出，传递到MainActivity中
            if(getIntent().getBundleExtra("launchBundle") != null){
				newIntent.putExtra("launchBundle", getIntent().getBundleExtra("launchBundle"));
            }
        } else {
            newIntent = new Intent(this, GuideActivity.class);
        }
        Intent oldIntent = getIntent();
        if(oldIntent != null){//把schema协议传给MainActivityGroup
            newIntent.setAction(oldIntent.getAction());
            newIntent.setData(oldIntent.getData());
        }
        startActivity(newIntent);
        finish();
    }

    /**
     * 进入H5Activity界面
     */
    private void gotoH5Activity(String jumpUrl){
        if(TextUtils.isEmpty(jumpUrl)){
            return;
        }
        toMainActivity = false;
        Intent intent = new Intent(SplashActivity.this, H5Activity.class);
        intent.putExtra("next_url", jumpUrl);
        intent.putExtra("next_title", "");
//        startActivity(intent);
        Intent[] intents;
        Intent mainIntent = new Intent(SplashActivity.this, MainActivityGroup.class);
        intents = new Intent[]{mainIntent, intent};
        startActivities(intents);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toMainActivity = false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 保存用户登录信息（一体机可能会传过来）
     */
    private void saveUserInfoForAIO() {
        Intent intent = getIntent();
        if (intent != null) {
            String MOJING_AONE_USER = intent.getStringExtra("MOJING_AONE_USER");
            if (TextUtils.isEmpty(MOJING_AONE_USER)) {
                return;
            }
            try {
                JSONObject joUserInfo = new JSONObject(MOJING_AONE_USER);
                String uid = joUserInfo.getString("user_no");
                String userName = joUserInfo.getString("user_name");
                String mobile = joUserInfo.getString("user_tel");
                String logoUrl = joUserInfo.getString("user_head_url");
                UserSpBusiness.getInstance().saveUserInfo(uid, userName, mobile, logoUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读秒（跳过）
     */
    private void timerForJump(){
        //3秒后进入主界面
        //3秒倒计时
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                int time = 0;
                while (time <= JUMP_CUTDOWN_TIME) {
                    publishProgress(time);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time += 50;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if(roundProgressBar != null){
                    roundProgressBar.setProgress(values[0]);
                }
                if(values[0] == JUMP_CUTDOWN_TIME){
                    if (toMainActivity && !BaseApplication.INSTANCE.isBFMJ5Connection()) {
                        gotoMainActivity();//进入主界面
                    }
                }
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        if(iv_splash!=null){
            iv_splash.recyleBitmap();
        }
        super.onDestroy();
    }
}
