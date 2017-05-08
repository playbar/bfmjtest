package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.PanoramaDetailActivity;
import com.baofeng.mj.ui.activity.VideoDetailActivity;
import com.baofeng.mj.ui.dialog.UnLockDialog;
import com.baofeng.mj.ui.online.utils.ChooseDialogManager;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mj.ui.online.utils.PlayerNetworkSubject;
import com.baofeng.mj.ui.online.utils.SortComparator;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.vrplayer.activity.MjVrPlayerActivity;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.MojingSurfaceView;
import com.bfmj.sdk.util.TimeFormat;
import com.bfmj.viewcore.interfaces.IGLViewClickListener;
import com.bfmj.viewcore.view.GLRootView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.storm.smart.core.P2P;
import com.storm.smart.core.URlHandleProxyFactory;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.utils.PlayCheckUtil;
import com.google.vr.ndk.base.AndroidCompat;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2016/11/23.
 * 全景详情页播放预览
 */
public class PanoramPlayerPreView extends BaseSensorView implements PlayerNetworkSubject.PlayerNetWorkChangeListener,VideoSelectHdView.IChangeHDSelectIndexListener {
    PanoramVideoPlayerView playerView;
    BottomContrallerView bootomView;
    ExceptionDialogView exceptionDialog;// 异常提示框
    PanoramaDetailActivity activity;
    RelativeLayout playerLayout;
    PlayerActivityTitleView titleBackView;
    PanoramaVideoBean videoBean;
    PanoramaVideoAttrs videoInfo;
    ImageView top_layer;
    ImageView top_layer2;
    PlayerCenterLine center_line;
    TextView preViewBtn;
    VideoSelectHdView mVideoSelectView; //清晰度选择view处理类
    HistoryInfo historyInfo;//存储的播放的历史数据
    int screenWidth = 1080;
    int screenHeight = 960;
    boolean isImgloaded = false;  //缩略图是否已经加载完成
    public  enum PlayerScreenMode{  //播放模式 半屏  全屏
        half_screen,fullscreen
    }
    public PlayerScreenMode currentMode = PlayerScreenMode.half_screen; //记录当前播放模式
    public boolean doubleScreen = false;
    private String mPath; //播放路径
    private String mCurDefinition ; //当前选择的清晰度
    private int playerType = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
    /*播放成功时时间，退出时报数计算utime3 使用*/
    private long playSuccessTime= 0;
    /*播放报数随机数*/
    private String ReportRoundID="0";
    /*计算尝试时长的报数*/
    private long startReport = 0;
    private long mStartTipsTime = -1;  //播放中加载超过10s提示的计时
    private long mLoadingTime = -1;
    private int mLoadingCount = 0;
    private long mLoadingAllTime = 0;
    private UnLockDialog unLockDialog;
    public PanoramPlayerPreView(Context context){
        super(context);
        PlayCheckUtil.setSupportLeftEye(true);
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(getContext());
        }
        initView();

        PlayerNetworkSubject.getInstance().Bind(this);
    }

    public PanoramPlayerPreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PlayCheckUtil.setSupportLeftEye(true);
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(getContext());
        }
        initView();
        PlayerNetworkSubject.getInstance().Bind(this);
    }


    public void setActivityViews(final PanoramaDetailActivity act,RelativeLayout playerLayout,PlayerActivityTitleView appTitleBackView){
        this.activity = act;
        this.playerLayout = playerLayout;
        titleBackView = appTitleBackView;
        screenWidth = PixelsUtil.getWidthPixels();
        screenHeight = PixelsUtil.getheightPixels();
        titleBackView.setOnClickListener(PlayAndBacklistener);
        titleBackView.getNameTV().setGravity(Gravity.LEFT);
        titleBackView.getNameTV().setVisibility(GONE);
        titleBackView.setVisibility(VISIBLE);
        titleBackView.setBackgroundResource(R.drawable.public_bg_banner);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setImmersiveSticky();
        activity.getWindow()
                .getDecorView()
                .setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    if(currentMode==PlayerScreenMode.fullscreen) {
                                        setImmersiveSticky();
                                    }
                                }
                            }
                        });
    }

    private void initView(){
        RelativeLayout  view = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.online_playerview_panoram,null);
        this.addView(view);
        super.init();
        top_layer = (ImageView)view.findViewById(R.id.top_layer);
        top_layer2 = ((ImageView) view.findViewById(R.id.top_layer_2));
        center_line = (PlayerCenterLine)view.findViewById(R.id.center_line);
        preViewBtn = (TextView)findViewById(R.id.player_preview_btn);
        playerView = (PanoramVideoPlayerView) view.findViewById(R.id.playerView);
        bootomView = (BottomContrallerView)view.findViewById(R.id.layout_bottomview);
        exceptionDialog = (ExceptionDialogView)findViewById(R.id.player_exception_dialog);
        loadingView = (LoadingView) findViewById(R.id.loadingvew);
        playerView.setBottomView(bootomView);
        bootomView.setPanoramVideoPlayerView(playerView);
        playerView.setPanoramPlayerPreView(this);
        bootomView.setPanoramPlayerPreView(this);
        bootomView.setVisibility(GONE);


        loadingView.setLoadingTextVisiable(View.GONE);
        exceptionDialog.setLayoutScreen(false);
        hideLoading();
        playerView.setLandScreen(false);
        setListener();
        initData();
    }


    private void setListener(){
        exceptionDialog.setBtnOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doExceptionRePlay();
            }
        });
        playerView.setOnGLClickListener(new IGLViewClickListener() {
            @Override
            public void click() {
                if(!isCanPlay())
                    return;
                hideContrallerView();
                if(playerView!=null){
                    playerView.initHeadView();
                }
            }
        });
        preViewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reportClick();
                doPrePlayBtn();
                hideContrallerView();
                setDelayVisiable();
            }
        });
    }

    /**
     * 点击预览和切换横屏自动播放
     */
    private void doPrePlayBtn(){
        isImgloaded = true;
        preViewBtn.setVisibility(GONE);
        if(!playerView.isPlaying()){
            reportVV("1","0","0");
            playerView.startPlay();
//            if(bootomView.initPosition>0) {
//                String text = ("正在从第" + TimeFormat.formatCH(bootomView.initPosition / 1000) + "续播");
//                Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
//            }
            if(playerView.mCurrentState== PanoramVideoPlayerView.PlayerState.PREPARED){
                onPrepared();
            }else if(playerView.mCurrentState == PanoramVideoPlayerView.PlayerState.ERROR){
                onPlayError();
                doNetChanged(true);
            }
        }
        hideTopLayer();
        if(playerView!=null&&playerView.mCurrentState== PanoramVideoPlayerView.PlayerState.IDEL){
            showLoading();
        }
    }

    /**
     * 网络异常下的重播
     */
    public void doExceptionRePlay(){
        if(!NetworkUtil.isNetworkConnected(getContext())){ //无网络时 不处理
            return;
        }
        if(playerView!=null){
            if(exceptionDialog.getIsContinueBtn()){//继续播放
                playerView.startPlay();
            }else{//从新播放
                playerView.rePlay();
            }
            exceptionDialog.setVisibility(View.GONE);
        }
    }

    private void initData(){
//        MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;//如果默认软解播可以设置这里
        playerView.setGyroscopeEnable(true);
        playerView.setDoubleScreen(false);
        playerView.setScreenTouch(true);
    }

    public PanoramVideoPlayerView getPlayerView(){
        return playerView;
    }

    private void setPath(String path ){
        this.mPath = path;

        if(playerView!=null) {
            playerView.setVideoPath(path);
        }
    }

    private void initPath(){
        if(videoInfo!=null){
            String ballUrl = videoInfo.getBall_url();
            if(!TextUtils.isEmpty(ballUrl)){    /*如果ball_url不为空 则用球模型播放*/
              mPath = ballUrl;
                if(playerView!=null){
                    playerView.setInit_Pov_head(videoBean.getPov_heading());
                    playerView.setSenceAndMode(2,videoBean.getVideo_dimension());
                }
            }else {
                mPath = videoInfo.getPlay_url();
                if(playerView!=null){
                    playerView.setInit_Pov_head(videoBean.getPov_heading());
                    playerView.setSenceAndMode(videoBean.getIs_panorama(),videoBean.getVideo_dimension());
                }
            }
        }



    }


    public void onResume() {
        super.onResum();
        stopDelayReleseTimer();
        if(playerView!=null) {
            playerView.resumeView();
            if(playerView.getPlayStatus()== BaseApplication.FLAG_PAUSE){
                playerView.pause2play();
            }else if(exceptionDialog.getVisibility()!=VISIBLE&&isCanPlay()) {
                if (MediaHelp.mPlayer == null||playerView.mCurrentState!= PanoramVideoPlayerView.PlayerState.PREPARED) {
                    playerView.rePlay();
                }else {
                    playerView.start2play();
                }
            }
            startCheckLoading();
        }
    }

    public void onPause() {
        super.onPause();
        if(playerView!=null) {
            playerView.pauseView();
            playerView.pause2play();
            saveHistory();
            stopCheckTimer();
        }
    }

    /**
     * 存储历史记录
     */
    public void saveHistory(){
        if(videoBean==null||playerView==null)
            return;
        if(bootomView.currentPosition<=0||bootomView.initPosition==bootomView.currentPosition){
            return;
        }
        if(historyInfo==null){
            historyInfo = new HistoryInfo();
            historyInfo.setAudioTrack(-1);
            historyInfo.setType(1);
            historyInfo.setResType(4);
            HistoryBusiness.VideoViewparam videoViewparam = HistoryBusiness.JudgeVideoType(videoBean.getVideo_dimension()+"",videoBean.getIs_panorama()+"");
            historyInfo.setVideo3dType(videoViewparam.mVideo3DType);
            historyInfo.setVideoType(videoViewparam._videoModelType);
            historyInfo.setDetailUrl(((PanoramaDetailActivity)activity).contents);
            historyInfo.setLastSetIndex(0);
            historyInfo.setPlayDuration(bootomView.currentPosition);
            historyInfo.setPlayFinished(playerView.isPlayCompletion()?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setTotalDuration(videoBean.getDuration()*1000);
            historyInfo.setVideoId(videoBean.getRes_id());
            historyInfo.setVideoImg(videoBean.getThumb_pic_url().get(0));
            historyInfo.setVideoPlayUrl(videoInfo.getPlay_url());
            historyInfo.setVideoSet(1);
            historyInfo.setVideoTitle(videoBean.getTitle());
        }else {
            historyInfo.setPlayDuration(bootomView.currentPosition);
            historyInfo.setPlayFinished(playerView.isPlayCompletion()?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setVideoPlayUrl(videoInfo.getPlay_url());
            historyInfo.setDetailUrl(((PanoramaDetailActivity)activity).contents);
            historyInfo.setVideoTitle(videoBean.getTitle());
            historyInfo.setVideoImg(videoBean.getThumb_pic_url().get(0));
        }
        historyInfo.setPlayType(HistoryBusiness.JudgePlayerTypeCoreToHistroy(MediaHelp.decodeType));
        String json=new Gson().toJson(historyInfo);
        HistoryBusiness.writeToHistory(json,videoBean.getRes_id(),1);
    }
    /**
     * 检测到手机横屏自动切换极简模式
     */
    @Override
    protected void changeToLand() {
        if(ChooseDialogManager.getInstance().isShowing())
            return;
        if(!isCanPlay())
            return;
        if(isImgloaded&&!playerView.isPauseView&&currentMode==PlayerScreenMode.half_screen) {
            changePlayerScreen(PlayerScreenMode.fullscreen, true);
            reportRotate();
        }
    }


    public void changeSurface(){

        if(playerView==null||playerView.rootView==null)
            return;
       playerView.rootView.queueEvent(new Runnable() {
            @Override
            public void run() {
//                ((GLRootView)playerView.rootView).surfaceChanged(null,0,PixelsUtil.getMaxDpi(activity),screenWidth);
            }
        });
    }

    public void onDestroy() {
        super.onDestory();
        if(!(playerView.mCurrentState== PanoramVideoPlayerView.PlayerState.COMPLETE|| playerView.mCurrentState==PanoramVideoPlayerView.PlayerState.ERROR)) {//退出的时候
            reportPlaySuccess();
        }

        if(playerView!=null) {
            playerView.destroyView();
            playerView=null;
        }
        if(bootomView!=null){
            bootomView.onDestroy();
        }
        stopCheckTimer();

        PlayerNetworkSubject.getInstance().UnBind(this);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopDelayReleseTimer();
        System.gc();
    }

    /**
     * 设置播放资源数据
     * @param data
     */
   public void setData(PanoramaVideoBean data){
       if(data==null)
           return ;

       videoBean = data;
       startReport = System.currentTimeMillis();
       reSetRoundid();
       setImage();
       mVideoSelectView = new VideoSelectHdView(activity,this,bootomView);
       mVideoSelectView.setmPanoramVideoBean(videoBean);
       bootomView.setSelectHDView(mVideoSelectView);
       List<PanoramaVideoAttrs> videos = videoBean.getVideo_attrs();
       SortComparator comparator = new SortComparator();
       Collections.sort(videos,comparator);
       /**读取播放记录数据*/
       String history = HistoryBusiness.readFromHistory(videoBean.getRes_id(),1);
       try {
           if(history!=null) {
               JSONObject myJsonObject = new JSONObject(history);
               historyInfo = CreateHistoryUtil.localJsonToHistoryInfo(myJsonObject);
           }
       }catch (Exception e){
          e.printStackTrace();
       }
           if (historyInfo != null && !TextUtils.isEmpty(historyInfo.getVideoPlayUrl())) {
               int playPos = 0;

               playerType = HistoryBusiness.JudgePlayerTypeHistroyToCore(historyInfo.getPlayType());
               if (!(historyInfo.getPlayFinished() == 1)) {
                   playPos = historyInfo.getPlayDuration();
               }
               mCurDefinition = historyInfo.getVideoClarity();
              PanoramaVideoAttrs info =  getVideoInfoByDefinition(videos,mCurDefinition);
               if(info!=null) {
                   videoInfo = info;
                   bootomView.setDefinitionText(mCurDefinition);
                   mVideoSelectView.setCurDefinition(mCurDefinition);
               }else {
                   setDefaultDefinitionVideo(videos);
               }
               bootomView.initPosition = playPos ;
               bootomView.currentPosition = bootomView.initPosition;
           } else {
               setDefaultDefinitionVideo(videos);
           }

       if(videoInfo==null){
           videoInfo = videos.get(0);
       }
       initPath();

       if(mPath==null){
           return;
       }

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               if(activity==null||activity.isFinishing()||activity.isDestroyed())
                   return;
               MediaHelp.release();
               MediaHelp.decodeType = playerType>0?playerType:IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
               MediaHelp.createPlayer(activity);
               setPath(mPath);

           }
       },1000);



   }

    /**
     * 根据清晰度获取视频数据
     * @param videos
     * @param definition 清晰度 name
     * @return
     */
    private  PanoramaVideoAttrs getVideoInfoByDefinition( List<PanoramaVideoAttrs> videos,String definition){
        for (int i = 0; i < videos.size(); i++) {
            PanoramaVideoAttrs video = videos.get(i);
            if (video.getDefinition_name().equals(definition)) {
             return video;
            }
        }
        return null;
    }

    /**
     * 设置默认播放的清晰度（1080）数据
     * @return
     */
    private void setDefaultDefinitionVideo(List<PanoramaVideoAttrs> videos){
        for (int i = 0; i < videos.size(); i++) {
            PanoramaVideoAttrs video = videos.get(i);
            if (video.getDefinition_id() == 5 || video.getDefinition_id() == 6) {
                //获取1080P的
                mCurDefinition = video.getDefinition_name();
                mVideoSelectView.setCurDefinition(mCurDefinition);
                bootomView.setDefinitionText(mCurDefinition);
                videoInfo = video;
                break;
            }
        }
    }


    /**
     * 播放未加载出来之前显示缩略图
     */
    private void setImage(){
        int width = PixelsUtil.getWidthPixels();
         int height = (int) (width /10f * 9f);
        RelativeLayout.LayoutParams bgLayoutParams=new RelativeLayout.LayoutParams(width,height);
        top_layer.setBackgroundResource(R.color.white);
        top_layer.setLayoutParams(bgLayoutParams);
        if (videoBean.getScreenshot() != null) {
            GlideUtil.displayImage(activity, new WeakReference<ImageView>(top_layer), videoBean.getScreenshot().get(0), R.drawable.preview_img_default, width, height,new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isImgloaded) {
                                preViewBtn.setVisibility(VISIBLE);
                            }
                            isImgloaded = true;
                        }
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isImgloaded) {
                                preViewBtn.setVisibility(VISIBLE);
                            }
                            isImgloaded = true;
                        }
                    });
                    return false;
                }
            } );
        }

    }

    /**
     * 网络状态改变监听
     * @param currentNetwork
     */
    @Override
    public void networkChange(int currentNetwork) {
        doNetChanged(false);
    }

    /*播放1s后检查 网络 如果非WIFI则提示用户*/
    public void checkPlay(){
        ThreadProxy.getInstance().addRunDelay(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doNetChanged(false);
                    }
                });
            }
        },1000);
    }

    /**
     * 网络状态改变后的处理
     * @param isPlayError
     */
    public void doNetChanged(final boolean isPlayError){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isCanPlay())
                    return;
                if(isPlayError){
                    if(exceptionDialog!=null){
                        exceptionDialog.showExceptionDialog(getResources().getString(R.string.player_load_failed),getContext().getString(R.string.player_reload),false);
                        exceptionDialog.setVisibility(VISIBLE);
                        hideLoading();
                    }
                    if(null != playerView){//解决空指针
                        playerView.pausePlay();
                    }
                    return;
                }

                if(!NetworkUtil.isNetworkConnected(getContext())){//无网络
                    if(exceptionDialog!=null){
                        exceptionDialog.showNoNetwork();
                        exceptionDialog.setVisibility(VISIBLE);
                        hideLoading();
                    }
                    if(playerView!=null) {
                        playerView.pausePlay();
                    }
                    return;
                }
                if(!NetworkUtil.canPlayAndDownload()){ //有网络 但为非WIFI网络
                    if(exceptionDialog!=null){
                        exceptionDialog.showExceptionDialog(getResources().getString(R.string.player_no_wifi),getContext().getString(R.string.player_play_continue),true);
                        exceptionDialog.setVisibility(VISIBLE);
                        hideLoading();
                    }
                    if(playerView!=null) {
                        playerView.pausePlay();
                    }
                    return;
                }else { //连上网络

                    if (playerView == null)
                        return;
                    if (playerView.mCurrentState == PanoramVideoPlayerView.PlayerState.PREPARED) {
                        if (!playerView.isPauseView) {
                            playerView.startPlay();
                        }
                    } else {
                        if (!playerView.isPauseView) {
                            playerView.rePlay();
                        }
                    }

                    if (playerView.isPauseView) {
                        playerView.updateStartStatus();
                    }
                    exceptionDialog.setVisibility(GONE);
                }

            }
        });


    }


    /**
     * 隐藏状态栏
     *
     */
    public void hideContrallerView(){

        if(bootomView!=null&&bootomView.getVisibility()==VISIBLE){
            bootomView.setVisibility(GONE);
            center_line.initDrawMargin(false);
            if(titleBackView!=null&&currentMode!=PlayerScreenMode.half_screen) {
                titleBackView.setVisibility(GONE);
            }
            if(mVideoSelectView!=null&&mVideoSelectView.isShowing()){
                mVideoSelectView.dismiss();
            }
        }else {

            bootomView.setVisibility(VISIBLE);
            if(titleBackView!=null) {
                titleBackView.setVisibility(VISIBLE);
            }
            center_line.initDrawMargin(true);
        }
    }
    public boolean isDialogVisiable(){
        if(exceptionDialog==null)
            return false;
        return exceptionDialog.getVisibility()==VISIBLE;
    }

    /**
     * 播放完成回调
     */
    public void onPlayComplete(){
        reportPlaySuccess();
        startReport = System.currentTimeMillis();
        reSetRoundid();
        if(playerView!=null){
            playerView.startPlay();
        }
        reportVV("1","0","0");

    }

    /**
     * 播放失败回调
     */
    public void onPlayError() {
        if(!isCanPlay())
            return;
        long utime = 0,ltime=0;
        if(playSuccessTime>0){
            utime = System.currentTimeMillis()-playSuccessTime;
        }
        if(startReport>0){
            ltime = System.currentTimeMillis()-startReport;
        }
        playSuccessTime = 0;
        startReport = 0;
        reportVV("3",utime+"",ltime+"" );
    }

    /**
     * 播放成功回调
     */
    public void onPrepared(){
        if(!isCanPlay())
            return;
        	/*记录播放成功时的时间*/
        if(playSuccessTime<=0){
            playSuccessTime = System.currentTimeMillis();
        }

        if(startReport>0) {
            reportVV("2", "0", (System.currentTimeMillis() - startReport) + "" );
            startReport = 0;
        }
    }

    public void hideLoading(){
        if(loadingView!=null){
            loadingView.setVisibility(GONE);
            mLoadingTime =-1;
        }
        if(playerView!=null&&playerView.mCurrentState== PanoramVideoPlayerView.PlayerState.PREPARED){
            mStartTipsTime = -1;
        }
    }

    public void showLoading(){
        if(loadingView!=null&&isCanPlay()){
            loadingView.setVisibility(VISIBLE);
            if(activity!=null){
                activity.hideVloumView();
            }
            mLoadingTime = System.currentTimeMillis();
            if(playerView!=null&&playerView.mCurrentState== PanoramVideoPlayerView.PlayerState.PREPARED){
                mStartTipsTime = System.currentTimeMillis();
                if(mLoadingCount>0){
                    mLoadingCount++;
                }
            }

        }
    }

    /**
     * 隐藏视频缩略图
     */
    public void hideTopLayer(){
        if(!isCanPlay())
            return;

        if(top_layer!=null&&top_layer.getVisibility()==VISIBLE){
            top_layer.setVisibility(GONE);
            top_layer2.setVisibility(GONE);
        }
    }

    /**
     * 播放半屏和全屏切换
     */
    public void changePlayerScreen(PlayerScreenMode type,boolean doubleFullScreen){
        currentMode = type;
        doubleScreen = doubleFullScreen;

        if(type==PlayerScreenMode.half_screen){
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            int width = screenWidth;
            int height = (int) (screenWidth/10f*9f);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            playerLayout.setLayoutParams(params);
            titleBackView.getNameTV().setVisibility(View.GONE);
            titleBackView.setVisibility(VISIBLE);
            titleBackView.getBackImgBtn().setVisibility(View.VISIBLE);
            titleBackView.setBackgroundResource(R.drawable.public_bg_banner);
            playerView.setLandScreen(false);
            bootomView.setLandScreen(false,doubleFullScreen);
            activity.reSetPos();

        }else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            setImmersiveSticky();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            playerLayout.setLayoutParams(layoutParams);
            titleBackView.getNameTV().setText(videoBean.getTitle());
            titleBackView.getNameTV().setVisibility(View.VISIBLE);
            titleBackView.getBackImgBtn().setOnClickListener(PlayAndBacklistener);
            if(doubleFullScreen){
                titleBackView.getBackImgBtn().setVisibility(View.INVISIBLE);
            }else{
                titleBackView.getBackImgBtn().setVisibility(View.VISIBLE);
            }
            titleBackView.setBackgroundResource(R.color.player_title_bg);
            titleBackView.setVisibility(bootomView.getVisibility());
            playerView.setLandScreen(true);
            bootomView.setLandScreen(true,doubleFullScreen);
            hideTopLayer();
            if(doubleFullScreen) {
                doPrePlayBtn();
            }
//            ((MojingSurfaceView)playerView.rootView).surfaceChanged(null,0,screenHeight,screenWidth);
        }
        getPlayerView().setDoubleScreen(doubleFullScreen);
        loadingView.setLayoutScreen(doubleFullScreen);
        exceptionDialog.setLayoutScreen(doubleFullScreen);
        super.changePlayerScreen(type==PlayerScreenMode.fullscreen);
        activity.changePlayerScreen(doubleFullScreen);
    }


    //监听播放模式选择按键事件 和返回事件
    private   View.OnClickListener PlayAndBacklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.player_choose_dialog_simple_layout == v.getId()){ //极简模式
                changePlayerScreen(PlayerScreenMode.fullscreen,true);

            }else if(R.id.player_choose_dialog_vr_layout == v.getId()){ //沉浸模式
                 doVrPlay();

            }else if(v.getId() == R.id.video_player_back) {// 返回
                if(currentMode==PlayerScreenMode.fullscreen) {
                    changePlayerScreen(PlayerScreenMode.half_screen,false);
                }else {
                    activity.finish();
                }
            }
        }
    };

    private void doVrPlay(){
        if(GLConst.GoUnity){
              startDelayReleseTimer();
              activity.doVRPlay();
            return;
        }

        if(playerView!=null)
            playerView.relesePlayView();

        stopCheckTimer();
        Intent intent = new Intent(getContext(), MjVrPlayerActivity.class);
//        intent.putExtra("videobean",videoBean);
        intent.putExtra("index",0);
        intent.putExtra("detail_url",((PanoramaDetailActivity)activity).contents);
        intent.putExtra("type",2);
        MjVrPlayerActivity.panoramaVideoBean = videoBean;
        getContext().startActivity(intent);
    }

    /**
     * 显示播放模式选择弹窗
     */
    public void showPlayerChooseDialog(){
        int playerMode = SettingSpBusiness.getInstance().getPlayerMode();
        if(playerMode==0){//极简模式
            changePlayerScreen(PlayerScreenMode.fullscreen,true);
        }else if(playerMode==1){//沉浸模式
            doVrPlay();
        }else {
            if(activity!=null&&!activity.isFinishing()) {
                ChooseDialogManager.getInstance().showChooseDialog(activity,PlayAndBacklistener);
            }
        }
    }


    private Timer delayReleseTimer;

    private void stopDelayReleseTimer(){
        if(delayReleseTimer!=null){
            delayReleseTimer.cancel();
            delayReleseTimer = null;
        }
    }
    private void startDelayReleseTimer(){
        if(delayReleseTimer!=null){
            delayReleseTimer.cancel();
            delayReleseTimer = null;
        }
        delayReleseTimer = new Timer();
        delayReleseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(playerView!=null) {
                            playerView.destroyView();
                        }
                        if(URlHandleProxyFactory.getInstance()!=null) {
                            URlHandleProxyFactory.getInstance().p2pUninit();
                        }

                    }
                });
            }
        },3*1000);

    }


    /**
     * 触屏事件监测，无操作5s后隐藏控制栏
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result= super.dispatchTouchEvent(ev);
        if(ev.getAction()==MotionEvent.ACTION_UP){
            setDelayVisiable();
        }else {
            cancelDelay();
        }
        return result;
    }

    public void cancelDelay(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }
    /**
     * 无操作时延迟3s隐藏
     */
    Timer timer ;
    public void setDelayVisiable(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bootomView!=null&&bootomView.getVisibility()==VISIBLE) {
                            hideContrallerView();
                        }
                    }
                });
            }
        };
        timer.schedule(task,BottomContrallerView.DELAY_DISMISS_TIME);
    }



    /**
     * 切换清晰度
     * @param defi_type 清晰度
     */
    private void changeHdType(String defi_type) {
        if(defi_type.equals(mCurDefinition))
            return;
          mCurDefinition = defi_type;
        videoInfo = getVideoInfo();
        if(videoInfo!=null){
            if(playerView!=null){
                reportPlaySuccess();
                startReport = System.currentTimeMillis();
                reSetRoundid();
                initPath();
                playerView.updatePath(mPath);
                playerView.rePlay();
                reportVV("1","0","0");
                bootomView.setDefinitionText(mCurDefinition);
                Toast.makeText(activity,"正在切换到"+mCurDefinition+"清晰度",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(activity,"不存在该清晰度视频源",Toast.LENGTH_SHORT).show();
        }
    }

    private PanoramaVideoAttrs  getVideoInfo(){
        if(videoBean==null)
            return null;
      List<PanoramaVideoAttrs> videoAttrses = videoBean.getVideo_attrs();
        for(int i=0;i<videoAttrses.size();i++){
            PanoramaVideoAttrs video = videoAttrses.get(i);
            if(video.getDefinition_name().equals(mCurDefinition)){
                //获取1080P的
               return video;
            }
        }
        return null;

    }

  //清晰度切换
    @Override
    public void onChangeHd(final String hdtype) {
        int result = SettingSpBusiness.getInstance().getHigh();
        if (2 != result && "4k".equals(hdtype)) {
            if (unLockDialog == null) {
                unLockDialog = new UnLockDialog(activity, new UnLockDialog.UnLockCallBack() {
                    @Override
                    public void onConfirm() {
                        changeHdType(hdtype);
                    }

                    @Override
                    public void onCancel() {
                        if(bootomView!=null) {
                            bootomView.setDefinitionText(mCurDefinition);
                        }
                        if(mVideoSelectView!=null) {
                            mVideoSelectView.setCurDefinition(mCurDefinition);
                        }
                    }
                },PixelsUtil.getheightPixels()- PixelsUtil.dip2px(40));
            }
            unLockDialog.setContentText(getResources().getString(R.string.player_lock_tips));
            unLockDialog.setConfirmText(getResources().getString(R.string.play));
            unLockDialog.show();
            return;
        }
        changeHdType(hdtype);
    }

    @Override
    public void onChangeSelectIndex(int index) {

    }

    /**
     * 报数用的随机数
     */
    private void reSetRoundid(){
        Random random = new Random();
        ReportRoundID = random.nextInt()+"";
    }
    public void reportPlaySuccess() {
        long utime = 0,ltime=0;
        if(playSuccessTime>0){
            utime = System.currentTimeMillis()-playSuccessTime;
        }
        if(startReport>0){
            ltime = System.currentTimeMillis()-startReport;
        }
        playSuccessTime = 0;
        reportVV("7",utime+"",ltime+"");
    }

    private void reportVV(String type, String utime, String ltime) {
        if(videoBean==null)
            return;
        if(!isCanPlay())
            return;
        HashMap<String, String> hs = new HashMap<String, String>();
        hs.put("etype", "vv");
        hs.put("tpos", "1");
        if(type!=null) {
            hs.put("vvtype", type);
        }
        hs.put("pagetype", "detail");
        hs.put("roundid",ReportRoundID );

        if(utime!=null) {
            hs.put("utime3", utime);
        }
        if(ltime!=null) {
            hs.put("ltime", ltime + "");
        }
        hs.put("is_ol","1");
        if(videoBean!=null) {
            hs.put("title", videoBean.getTitle());
            hs.put("videoid", videoBean.getRes_id()+"");
        }
        hs.put("typeid",videoBean.getType()+"");
        hs.put("joystick", StickUtil.isConnected?"1":"0");
        if(type.equals("7")){
            int averaspeed = 0;
            if(MediaHelp.mPlayer!=null&&MediaHelp.decodeType!= IBfPlayerConstant.IBasePlayerType.TYPE_SYS) {
                averaspeed = MediaHelp.mPlayer.getAverageSpeed();
            }
            if(averaspeed>0) {
                hs.put("averaspeed", averaspeed + "byte/s");
            }
        }

        ReportBusiness.getInstance().reportVV(hs);
    }
    /**
     * 判断当前是否可播放   （缩略图未显示出来时不可播放，预览按钮显示时不可自动播放。先显示出缩略图才会显示预览按钮）
     * @return
     */
    public boolean isCanPlay(){
        if(!isImgloaded)
            return false;
        if(preViewBtn!=null&&preViewBtn.getVisibility()==VISIBLE)
            return false;
        return true;
    }


    private void reportClick(){
        HashMap<String, String> hs = new HashMap<String, String>();
        hs.put("etype", "click");
        hs.put("tpos", "1");
        hs.put("clicktype","viewvideo");
        hs.put("pagetype","detail");
        ReportBusiness.getInstance().reportClick(hs);
    }
    /**
     * 转横屏报数
     */
    private void reportRotate(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("cut");
        bean.setTpos("1");
        bean.setPagetype("playmode");
        bean.setMode("rotate");
        bean.setIs_rem("0");
        ReportBusiness.getInstance().reportClick(bean);
    }
    private Timer mTimer;
    /**
     *
     */
    private void startCheckLoading() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleNetWorkException();
                    }
                });

            }
        }, 300, 1000);
    }

    private void stopCheckTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void handleNetWorkException(){
        if (loadingView.getVisibility()!=VISIBLE) {
            return;
        }
        /**当次播放时，首次出现卡顿超过10s，提示*/
        if(mStartTipsTime!=-1 && System.currentTimeMillis() - mStartTipsTime>10*1000){
            if(mLoadingCount==0){
                loadingView.setVisibility(GONE);
                exceptionDialog.showNetworkTips(4 * 1000, new ExceptionDialogView.DismissListener() {
                    @Override
                    public void dismiss() {
                        if(mStartTipsTime>0){
                            loadingView.setVisibility(VISIBLE);
                        }
                    }
                });
                mLoadingAllTime = System.currentTimeMillis();
                mLoadingCount++;
            }

        }
        /**当首次卡顿到第三次卡顿间隔<=30s*/
        if(mLoadingCount==2&&mLoadingAllTime>0&&System.currentTimeMillis()-mLoadingAllTime<=30*1000){
            loadingView.setVisibility(GONE);
            exceptionDialog.showNetworkTips(4 * 1000, new ExceptionDialogView.DismissListener() {
                @Override
                public void dismiss() {
                    if(mStartTipsTime>0){
                        loadingView.setVisibility(VISIBLE);
                    }
                }
            });
        }
        /**loading超过1分钟显示网络加载失败*/
        if (mLoadingTime != -1 && System.currentTimeMillis() - mLoadingTime >60000 ) {
            doNetChanged(true);
            hideLoading();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus &&currentMode==PlayerScreenMode.fullscreen) {
            setImmersiveSticky();
        }
    }

    private void setImmersiveSticky() {
        Log.d("login","---setImmerSiveSticky--");
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
