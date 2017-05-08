package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.VideoDetailActivity;
import com.baofeng.mj.ui.dialog.UnLockDialog;
import com.baofeng.mj.ui.online.utils.ChooseDialogManager;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mj.ui.online.utils.PlayerModeChooseSubject;
import com.baofeng.mj.ui.online.utils.PlayerNetworkSubject;
import com.baofeng.mj.ui.online.utils.SortComparatorVideo;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.vrplayer.activity.MjVrPlayerActivity;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mojing.MojingSDK;
import com.bfmj.viewcore.interfaces.IGLViewClickListener;
import com.bfmj.viewcore.view.GLPlayerView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.vr.ndk.base.AndroidCompat;
import com.storm.smart.core.IProxy;
import com.storm.smart.core.URlHandleProxyFactory;
import com.storm.smart.domain.P2pInfo;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.utils.PlayCheckUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2016/11/23.
 * 在线视频播放预览View
 */

public class VideoPlayerPreView extends BaseSensorView implements PlayerNetworkSubject.PlayerNetWorkChangeListener,PlayerModeChooseSubject.IPlayerChooseCallback ,VideoSelectHdView.IChangeHDSelectIndexListener{
    SystemPlayerView playerView;
    ImageView top_layer; //视频未播放时显示的缩略图
    ImageView top_layer2; //缩略图上的阴影
    PlayerCenterLine center_line;
    BottomContrallerView bootomView;
    ExceptionDialogView exceptionDialog;// 异常提示框
    VideoDetailActivity activity;
    RelativeLayout playerLayout;
    PlayerActivityTitleView titleBackView;
    VideoDetailBean videoBean;
    List<VideoDetailBean.AlbumsBean> mDataList;
    PlaySelectPopupWindow mPlaySelectWindow; //选集view
    private String mCurDefinition; //当前选择的清晰度text
    private TextView preViewBtn;
    private int mHdIndex = 0; //当前选择的清晰度index
    private int mIndex = 0;  //当前播放的第几集
    private VideoDetailBean.AlbumsBean.VideosBean videoInfo;//当前播放的剧集
    /*播放成功时时间，退出时报数计算utime3 使用*/
    private long playSuccessTime= 0;
    /*播放报数随机数*/
    private String ReportRoundID="0";
    /*计算尝试时长的报数*/
    private long startReport = 0;
    HistoryInfo historyInfo;//存储的播放的历史数据
    int screenWidth = 1080;
    int screenHeight=1920;
    boolean isImgloaded = false; //预览缩略图是否加载成功
    public  enum PlayerScreenMode{  //播放模式 半屏  全屏
        half_screen,fullscreen
    }
    public PlayerScreenMode currentMode = PlayerScreenMode.half_screen; //记录当前播放模式
    private boolean doubleScreen = false; // 是否为双屏状态
    private Timer mSpeedTimer;
    private long mLoadingTime = -1;
    private long mStartTipsTime = -1;  //播放中加载超过10s提示的计时
    private int mLoadingCount = 0;
    private long mLoadingAllTime = 0;
    private VideoSelectHdView mVideoSelectView; //清晰度选择view
    private IProxy proxy;
    private String path;
    boolean isP2p = false;
    private UnLockDialog unLockDialog;
    public VideoPlayerPreView(Context context){
        super(context);
        PlayCheckUtil.setSupportLeftEye(true);
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(getContext());
        }

        initView();
        PlayerNetworkSubject.getInstance().Bind(this);
        PlayerModeChooseSubject.getInstance().Bind(this);

    }

    public VideoPlayerPreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PlayCheckUtil.setSupportLeftEye(true);
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(getContext());
        }
        initView();
        PlayerNetworkSubject.getInstance().Bind(this);
    }


    public void setActivityViews(final VideoDetailActivity activity, RelativeLayout playerLayout, PlayerActivityTitleView appTitleBackView){
        this.activity = activity;
        this.playerLayout = playerLayout;
        titleBackView = appTitleBackView;
        screenWidth = PixelsUtil.getWidthPixels();
        screenHeight = PixelsUtil.getheightPixels();
        titleBackView.setOnClickListener(PlayAndBacklistener);

        titleBackView.getNameTV().setVisibility(GONE);
        titleBackView.setVisibility(VISIBLE);
        titleBackView.setBackgroundResource(R.drawable.public_bg_banner);
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

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void initView(){
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.activity_general,null);
        this.addView(view);
        super.init();
        top_layer = ((ImageView) view.findViewById(R.id.top_layer));
        top_layer2 = ((ImageView) view.findViewById(R.id.top_layer_2));
        center_line = (PlayerCenterLine)view.findViewById(R.id.center_line);
        preViewBtn = (TextView)view.findViewById(R.id.player_preview_btn);
        playerView = (SystemPlayerView) view.findViewById(R.id.playerView);
        bootomView = (BottomContrallerView)view.findViewById(R.id.layout_bottomview);
        exceptionDialog = (ExceptionDialogView)findViewById(R.id.player_exception_dialog);
        loadingView = (LoadingView) findViewById(R.id.loadingvew);
        mPlaySelectWindow = (PlaySelectPopupWindow)findViewById(R.id.playseletview);
        mPlaySelectWindow.setOnItemClickListener(this);
        playerView.setVideoPlayerPreView(this);
        playerView.setBottomView(bootomView);
        bootomView.setSystemPlayerView(playerView);
        bootomView.setVidoPlayerPreView(this);
        bootomView.setVisibility(GONE);

        bootomView.releaseThread();
        exceptionDialog.setLayoutScreen(false);

        hideLoading();
        setListener();
        startUpdateSpeed();

        MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;//如果默认软解播可以设置这里
        playerView.setGyroscopeEnable(false);
        playerView.setDoubleScreen(false);
        playerView.setScreenTouch(false);

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
//                doNetChanged(false);
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
            if(playerView.mCurrentState== SystemPlayerView.PlayerState.PREPARED){
                onPrepared();
            }
            playerView.startPlay();
            if(playerView.mCurrentState== SystemPlayerView.PlayerState.ERROR){
                onPlayError();
                doNetChanged(false);
            }

        }
        hideTopLayer();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setCurrentIndex(mIndex);
            }
        });
        if(playerView!=null&&playerView.mCurrentState== SystemPlayerView.PlayerState.IDEL){
            showLoading();
        }
    }

    /**
     * 判断当前是否可播放   （缩略图未显示出来时不可播放，预览按钮未显示时不可播放。先显示出缩略图才会显示预览按钮）
     * @return
     */
    public boolean isCanPlay(){
        if(!isImgloaded)
            return false;
        if(preViewBtn!=null&&preViewBtn.getVisibility()==VISIBLE)
            return false;
        return true;
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
                if(proxy!=null) {
                    path = videoInfo.getPlay_url();
                    proxy.p2pStartPlay(path);
                    startUpdateSpeed();
                }
                playerView.startPlay();
//                rePlay();
            }else{//从新播放

                rePlay();
            }
            exceptionDialog.setVisibility(View.GONE);
        }
    }

    public SystemPlayerView getPlayerView(){
        return playerView;
    }

    private void setPath(String path ){
        this.path = path;
        if(playerView!=null) {
            playerView.setVideoPath(path);
        }
    }


    public void onResume() {
        super.onResum();
          stopDelayReleseTimer();
        if(playerView!=null) {
            playerView.resumeView();
             //没有异常弹窗并且是可以播放的状态下，根据上次的播放状态设置继续播放或暂停状态。否则只显示页面不进行播放操作
            if(exceptionDialog.getVisibility()!=VISIBLE&&isCanPlay()) {
                if(playerView.getPlayStatus()==BaseApplication.FLAG_PAUSE){
                    playerView.pause2Play();
                }else {
                    if (MediaHelp.mPlayer == null||playerView.mCurrentState!= SystemPlayerView.PlayerState.PREPARED) {
                        rePlay();
                    } else {
                        playerView.start2play();
                    }
                }
            }
        }
        startUpdateSpeed();
    }

    public void onPause() {
        super.onPause();
        if(playerView!=null) {
            playerView.pauseView();
            playerView.pause2Play();
            saveHistory();
        }
        stopUpdateSpeed();
    }

    /**
     * 进入后台，保存播放记录
     */
    private void saveHistory(){
        if(videoBean==null||videoInfo==null)
            return;
        if(bootomView.currentPosition<=0||bootomView.initPosition==bootomView.currentPosition){
            return;
        }
        if(historyInfo==null){
            historyInfo = new HistoryInfo();
            historyInfo.setAudioTrack(-1);
            historyInfo.setType(1);
            historyInfo.setResType(1);
            historyInfo.setVideo3dType(videoBean.getIs_3d()+1);
            historyInfo.setVideoType(1);
            historyInfo.setDetailUrl(((VideoDetailActivity)activity).contents);
            historyInfo.setLastSetIndex(mIndex);
            historyInfo.setPlayDuration(bootomView.currentPosition);
            historyInfo.setPlayFinished(playerView.isPlayCompletion()?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setTotalDuration(videoInfo.getDuration()*1000);
            historyInfo.setVideoId(videoBean.getId()+"");
            historyInfo.setVideoImg(videoBean.getHpic());
            historyInfo.setVideoPlayUrl(TextUtils.isEmpty(videoInfo.getSlice_path())?videoInfo.getPlay_url():videoInfo.getSlice_path());
            if(!TextUtils.isEmpty(videoBean.getTotal())){
                historyInfo.setVideoSet( Integer.parseInt(videoBean.getTotal()));
            }

            historyInfo.setVideoTitle(videoBean.getTitle());
        }else {
            historyInfo.setVideoId(videoBean.getId()+"");
            historyInfo.setVideoImg(videoBean.getHpic());
            historyInfo.setVideo3dType(videoBean.getIs_3d()+1);
            historyInfo.setTotalDuration(videoInfo.getDuration()*1000);
            historyInfo.setPlayDuration(bootomView.currentPosition);
            historyInfo.setPlayFinished(playerView.isPlayCompletion()?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setDetailUrl(((VideoDetailActivity)activity).contents);
            historyInfo.setVideoImg(videoBean.getHpic());
            historyInfo.setVideoTitle(videoBean.getTitle());
            historyInfo.setVideoPlayUrl(TextUtils.isEmpty(videoInfo.getSlice_path())?videoInfo.getPlay_url():videoInfo.getSlice_path());
            historyInfo.setLastSetIndex(mIndex);
        }
        historyInfo.setPlayType(HistoryBusiness.JudgePlayerTypeCoreToHistroy(MediaHelp.decodeType));
        String json=new Gson().toJson(historyInfo);
        HistoryBusiness.writeToHistory(json,videoBean.getId()+"",1);
    }

    /**
     * 检测到手机横屏自动切换极简模式
     */
    @Override
    protected void changeToLand() {
        if( ChooseDialogManager.getInstance().isShowing())
            return;
        if(!isCanPlay())
            return;
        if(isImgloaded&&!playerView.isPauseView&&currentMode==PlayerScreenMode.half_screen){
            changePlayerScreen(PlayerScreenMode.fullscreen,true);
            reportRotate();
        }
    }
    public void changeSurface(){
        if(playerView==null||playerView.rootView==null)
            return;
        playerView.rootView.queueEvent(new Runnable() {
            @Override
            public void run() {
//                ((MojingSurfaceView)playerView.rootView).surfaceChanged(null,0,PixelsUtil.getMaxDpi(activity),screenWidth);
            }
        });

    }

    public void onDestroy() {
        super.onDestory();
        if(!(playerView.mCurrentState== SystemPlayerView.PlayerState.COMPLETE||playerView.mCurrentState== SystemPlayerView.PlayerState.ERROR)) {
            reportPlaySuccess();
        }
        if(playerView!=null) {
            playerView.pausePlay();
            playerView.destroyView();
        }
        if(bootomView!=null){
            bootomView.onDestroy();
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PlayerNetworkSubject.getInstance().UnBind(this);
        PlayerModeChooseSubject.getInstance().unBind(this);
        stopUpdateSpeed();
        if(proxy!=null) {
            proxy.p2pUninit();
        }
        System.gc();
    }

    /**
     * 设置播放资源数据
     * @param data 详情数据
     * @param index 第几集
     */
    public void setData(final VideoDetailBean data,int index){
        if(data==null){
            return ;
        }
        videoBean = data;
        mIndex = index;
        startReport = System.currentTimeMillis();
        reSetRoundId();
        mDataList = data.getAlbums();
        SortComparatorVideo comparator = new SortComparatorVideo();
        Collections.sort(mDataList,comparator);
        mVideoSelectView = new VideoSelectHdView(activity,this,bootomView);
        mVideoSelectView.setVideoBean(videoBean);
        bootomView.setSelectHDView(mVideoSelectView);
        mPlaySelectWindow.setMovieVideoDatas(videoBean);
        mPlaySelectWindow.setCurIndex(mIndex);
        setImag();
        initPlayFromHistory();
        initPlay();


    }

    /**
     * 读取播放记录数据
     */
    private void initPlayFromHistory(){
        String history = HistoryBusiness.readFromHistory(videoBean.getId()+"",1);
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
                path = historyInfo.getVideoPlayUrl();
                if (!(historyInfo.getPlayFinished() == 1)) {
                    playPos = historyInfo.getPlayDuration();
                }
                mCurDefinition = historyInfo.getVideoClarity();
                mHdIndex = getHdIndex(mCurDefinition);
                bootomView.setDefinitionText(mCurDefinition);
                mVideoSelectView.setCurDefinition(mCurDefinition);
                if(mHdIndex<0){
                    mHdIndex =initDefaultHd();
                }
                bootomView.initPosition = playPos ;
                bootomView.currentPosition = bootomView.initPosition;
                mIndex = historyInfo.getLastSetIndex();
                mPlaySelectWindow.setCurIndex(mIndex);
                int type = HistoryBusiness.JudgePlayerTypeCoreToHistroy(historyInfo.getPlayType());
                if(type>0) {
                    MediaHelp.decodeType = type;
                }

            } else {
                mHdIndex = initDefaultHd();
            }
    }

    /**
     * 首次初始完数据开始播放
     */
    private void initPlay(){
        videoInfo = getVideoInfo(mHdIndex,mIndex);
        if(videoInfo==null)
            return;
        path = videoInfo.getPlay_url();
        if (path.startsWith("qstp:")||path.startsWith("yun:") || path.startsWith("yunlive:")){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;
                    playerView.reSetPlay();
                    loadingView.setLoadingTextVisiable(View.VISIBLE);
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
                startPlayer();

                int is3D = videoBean.getIs_3d();
                if (is3D==1) {
                    playerView.setPlayMode(GLPlayerView.MODE_3D_LEFT_RIGHT);
                } else {
                    playerView.setPlayMode(GLPlayerView.MODE_2D);
                }

            }
        }).start();

    }

    /**
     * 播放未加载出来之前显示缩略图
     */
    private void setImag(){
        int width = PixelsUtil.getWidthPixels();
        int height =  (int) (width * (9.0f/16));
                RelativeLayout.LayoutParams bgLayoutParams=new RelativeLayout.LayoutParams(width,height);
        top_layer.setLayoutParams(bgLayoutParams);
        GlideUtil.displayImage(activity, new WeakReference<ImageView>(top_layer), videoBean.getHpic(),R.drawable.img_default_banner , width, height,new RequestListener<String, GlideDrawable>() {
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

    /**
     * 重播
     */
   public void rePlay(){
       if(videoInfo==null)
           return;
       path = videoInfo.getPlay_url();
       if (path.startsWith("qstp:")||path.startsWith("yun:") || path.startsWith("yunlive:")){
           MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;//如果默认软解播可以设置这里
       }else {
           MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
       }
       playerView.reSetPlay();
       startPlayer();
   }

   //播放
    public void startPlayer() {
        PlayCheckUtil.setSupportLeftEye(true);
        int pid;
        isP2p = false;
        if (path.startsWith("qstp:")) {  //qstp 格式的用p2p播放
            MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;//如果默认软解播可以设置这里
            pid = URlHandleProxyFactory.getIProxy(getContext(), path);
            proxy = URlHandleProxyFactory.getInstance();
            path = P2pInfo.P2P_PLAY_SERVER_PATH;
            isP2p = true;
        } else if (path.startsWith("yun:") || path.startsWith("yunlive:")) { //云视频
            MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;//如果默认软解播可以设置这里
            URlHandleProxyFactory.getIProxy(getContext(), path);
            proxy = URlHandleProxyFactory.getInstance();
            proxy.setcallback(new IProxy.UrlCallBack() {
                @Override
                public void mcallBack(String state) {
                    if (state != null && !"4".equals(state)) {
                        path = state;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           setPath(path);
                        }
                    });
                }
            });
            if(proxy!=null){
                proxy.p2pStartPlay(path);
            }
            isP2p = true;
        }


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isP2p) {
                    loadingView.setLoadingTextVisiable(View.VISIBLE);
                }
                setPath(path);
            }
        });

    }

    /**
     *  初始化清晰度 默认取720P
     * @return 无
     */
    private int initDefaultHd() {
        if (mDataList == null || mDataList.size() == 0) {
            return 0;
        }

        String hdtype = "";
        int defaultIndex = -1;
        if (TextUtils.isEmpty(hdtype)){ //默认720p
            for (int i=0;i<=mDataList.size()-1;i++){
                if (mDataList.get(i).getHdtype() <= 720
                        && defaultIndex == -1) {
                    defaultIndex = i;
                    hdtype = mDataList.get(i).getHdtype()+"";
                    break;
                }
            }
        }else {
            for (int i=0;i<=mDataList.size()-1;i++) {
                if (!TextUtils.isEmpty(hdtype) && (mDataList.get(i).getHdtype()+"").equals(hdtype)) {
                    defaultIndex = i;
                    break;
                }
            }

        }
        bootomView.setDefinitionText(hdtype);
        mVideoSelectView.setCurDefinition(hdtype+"");
        mCurDefinition = hdtype+"";
        return Math.max(defaultIndex, 0);
    }


    /**
     *  获取视频信息
     * @return 视频信息
     */
    private VideoDetailBean.AlbumsBean.VideosBean getVideoInfo(int hdIndex, int index) {
        if (mDataList == null || hdIndex >= mDataList.size()) {
            return null;
        }
        VideoDetailBean.AlbumsBean.VideosBean videoInfo = null;
        VideoDetailBean.AlbumsBean list = mDataList.get(hdIndex);
        videoInfo = getVideoInfoByNum(list,(index+1));
        if(list==null||index>=list.getVideos().size()||videoInfo==null){
            for (int i = mDataList.size()-1;i>=0;i--){
                VideoDetailBean.AlbumsBean albums = mDataList.get(i);
                VideoDetailBean.AlbumsBean.VideosBean video = getVideoInfoByNum(albums,(index+1));
                if(video!=null) {
                    videoInfo = video;
                    list = albums;
                    break;
                }
            }
        }

        return videoInfo;
    }

    /**
     * 根据集数获取视频数据VideoBean
     * @param list
     * @param indexNum  第几集
     * @return
     */
    private VideoDetailBean.AlbumsBean.VideosBean getVideoInfoByNum(VideoDetailBean.AlbumsBean list, int indexNum){

        if(list==null)
            return null;
        List<VideoDetailBean.AlbumsBean.VideosBean> videos = list.getVideos();
        if(videos==null||videos.size()<=0)
            return null;
        VideoDetailBean.AlbumsBean.VideosBean curVideo = null;
        for( VideoDetailBean.AlbumsBean.VideosBean video:videos){
            int seq = video.getSeq();
            if(seq<0)
                continue;
            if(indexNum== seq){
                curVideo = video;
                break;
            }
        }
        return curVideo;
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
        if(!isCanPlay())
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isPlayError){
                    if(exceptionDialog!=null){
                        exceptionDialog.showExceptionDialog(getResources().getString(R.string.player_load_failed),getContext().getString(R.string.player_reload),false);
                        exceptionDialog.setVisibility(VISIBLE);
                    }
                    hideLoading();
                    if(playerView!=null) {
                        playerView.pausePlay();
                    }

                    return;
                }


                if(!NetworkUtil.isNetworkConnected(getContext())){//无网络
                    if(exceptionDialog!=null){
                        exceptionDialog.showNoNetwork();
                        exceptionDialog.setVisibility(VISIBLE);
                    }
                    hideLoading();
                    if(playerView!=null) {
                        playerView.pausePlay();
                    }

                    return;
                }
                if(!NetworkUtil.canPlayAndDownload() ){ //有网络 但为非WIFI网络
                    if(exceptionDialog!=null){
                        exceptionDialog.showExceptionDialog(getResources().getString(R.string.player_no_wifi),getContext().getString(R.string.player_play_continue),true);
                        exceptionDialog.setVisibility(VISIBLE);
                    }
                    hideLoading();
                    if(playerView!=null) {
                        playerView.pausePlay();
                        stopUpdateSpeed();
                        if(proxy!=null)
                            proxy.p2pStopPlay();
                    }
                    return;
                }else { //连上网络
//            if(exceptionDialog.getVisibility()==VISIBLE){
                    if(playerView!=null&&playerView.mCurrentState== SystemPlayerView.PlayerState.PREPARED) {
                        if(!playerView.isPauseView) {
                            playerView.startPlay();
                        }
                    }else {
                        if(!playerView.isPauseView) {
                            rePlay();
                        }
                    }
                    if(playerView.isPauseView){
                        playerView.updateStartStatus();
                    }
                    exceptionDialog.setVisibility(GONE);
//            }
                }
            }
        });


    }

    /**
     * 隐藏或显示状态栏
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
            if(mPlaySelectWindow!=null&&mPlaySelectWindow.getVisibility()==VISIBLE){
                mPlaySelectWindow.setVisibility(GONE);
            }
        }else {
            bootomView.setVisibility(VISIBLE);
            if(titleBackView!=null) {
                titleBackView.setVisibility(VISIBLE);
            }
            center_line.initDrawMargin(true);
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP){
            setDelayVisiable();
        }else {
            cancelDelay();
        }
        return super.dispatchTouchEvent(ev);
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

    public boolean isDialogVisiable(){
        if(exceptionDialog==null)
            return false;
        return exceptionDialog.getVisibility()==VISIBLE;
    }

    /**
     * 成功播放后回调改方法（在playerView中），处理了报数问题
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

    /**
     * 播放完成后回调了该方法（在playerView中），播放完后自动播放下集或者重新播放
     */
    public void onPlayComplete(){

        reportPlaySuccess();
        startReport = System.currentTimeMillis();
        reSetRoundId();
        int total=0;
        if(videoBean!=null) {
            total = videoBean.getMaxseq();
        }

        if (mIndex+1<total) {
            VideoDetailBean.AlbumsBean.VideosBean videoInfo = getVideoInfo(mHdIndex,mIndex + 1);
            if(videoInfo!=null) {
                mIndex++;
                this.videoInfo = videoInfo;
                path = videoInfo.getPlay_url();
            }
            playerView.reSetPlay();
            startPlayer();

            reportVV("1", "0", "0"  );

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"即将为您播放第" + (mIndex + 1) + "集",Toast.LENGTH_SHORT).show();
                    showLoading();
                    activity.setCurrentIndex(mIndex);
                    mPlaySelectWindow.setCurIndex(mIndex);
                    if(titleBackView!=null){
                        titleBackView.getNameTV().setText(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
                    }
                }
            });


        }else {
            playerView.isPlayCompletion = true;
            playerView.mCurrentState = SystemPlayerView.PlayerState.COMPLETE;
            playerView.setPlayStatus(BaseApplication.FLAG_PAUSE);
            bootomView.initPlayStatus();
            playerView.relesePlayView();
            mIndex = 0;
            VideoDetailBean.AlbumsBean.VideosBean mvideoInfo = getVideoInfo(mHdIndex,mIndex);
            if(mvideoInfo!=null){
                this.videoInfo = mvideoInfo;
            }
            rePlay();
            reportVV("1", "0", "0"  );
            activity.setCurrentIndex(mIndex);
            mPlaySelectWindow.setCurIndex(mIndex);
            if(titleBackView!=null){
                titleBackView.getNameTV().setText(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
            }
        }
    }

    /**
     * 显示loading
     */
    public void hideLoading(){
//        Log.d("login","---hideLoading--");
        if(loadingView!=null){
            loadingView.setVisibility(GONE);
            loadingView.setLoadingText("",0);
        }
        mLoadingTime = -1;
        if (playerView!=null&&playerView.mCurrentState== SystemPlayerView.PlayerState.PREPARED) {
            mStartTipsTime = -1;
        }
    }

    /**
     * 隐藏loading
     */
    public void showLoading(){
        if(!isCanPlay()){
            return;
        }
//        Log.d("login","---showLoading--");
        if(loadingView!=null){
            loadingView.setVisibility(VISIBLE);
        }
        if(activity!=null){
            activity.hideVloumView();
        }
        mLoadingTime = System.currentTimeMillis();
        if (playerView!=null&&playerView.mCurrentState== SystemPlayerView.PlayerState.PREPARED) {
            mStartTipsTime = System.currentTimeMillis();
            if (mLoadingCount > 0) {
                mLoadingCount++;
            }
        }
    }

    /**
     * 隐藏缩略图（开始播放后）
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
     * 重置报数用的随机数
     */
    private void reSetRoundId(){
        Random random = new Random();
        ReportRoundID = random.nextInt()+"";
    }


    /**
     * 播放半屏和全屏切换
     */
    public void changePlayerScreen(PlayerScreenMode type,boolean doubleFullScreen ){
        doubleScreen = doubleFullScreen;
        currentMode = type;
        if(type==PlayerScreenMode.half_screen){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_VISIBLE);
            int width = screenWidth;
            int height =  (int) (width * (9.0f/16));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            playerLayout.setLayoutParams(params);
            titleBackView.setVisibility(VISIBLE);
            titleBackView.getNameTV().setVisibility(View.GONE);
            titleBackView.setBackgroundResource(R.drawable.public_bg_banner);
            bootomView.setLandScreen(false,doubleFullScreen);
            mPlaySelectWindow.setVisibility(GONE);
            if(activity!=null){
                activity.reSetPos();
            }
        }else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            setImmersiveSticky();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            playerLayout.setLayoutParams(layoutParams);
            titleBackView.getNameTV().setText(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
            titleBackView.getNameTV().setVisibility(View.VISIBLE);
            titleBackView.getBackImgBtn().setOnClickListener(PlayAndBacklistener);
            titleBackView.setBackgroundResource(R.color.player_title_bg);
            titleBackView.setVisibility(bootomView.getVisibility());
            bootomView.setLandScreen(true,doubleFullScreen);
            hideTopLayer();
            if(doubleFullScreen) {
                doPrePlayBtn();
            }
//            ((MojingSurfaceView)playerView.rootView).surfaceChanged(null,0,screenHeight,screenWidth);
        }
        getPlayerView().setDoubleScreen(doubleFullScreen);
        exceptionDialog.setLayoutScreen(doubleFullScreen);
        loadingView.setLayoutScreen(doubleFullScreen);
        super.changePlayerScreen(currentMode==PlayerScreenMode.fullscreen);
        activity.changePlayerScreen(doubleFullScreen);

    }


    //监听播放模式选择按键事件 和返回事件
    private   View.OnClickListener PlayAndBacklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(v.getId() == R.id.video_player_back) {// 返回
                if(currentMode==PlayerScreenMode.fullscreen) {
                    changePlayerScreen(PlayerScreenMode.half_screen,false);
                }else {
                    activity.finish();
                }
            }
        }
    };



    /**
     * 重新开始加载
     */
    private void startUpdateSpeed() {
        if (mSpeedTimer != null) {
            mSpeedTimer.cancel();
            mSpeedTimer = null;
        }

        mSpeedTimer = new Timer();
        mSpeedTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (proxy != null) {

                   final int speed = proxy.p2pGetSpeed(0);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateSpeed(speed);
                        }
                    });
                }
                if (loadingView.getVisibility()==VISIBLE) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleNetWorkException();
                        }
                    });
                }

            }
        }, 300, 300);
    }

    /**
     * 更新加载网速
     * @param speed
     *            网速
     * @return
     */
    public void updateSpeed(int speed) {

        if (loadingView.getVisibility()!=VISIBLE) {
            return;
        }
        final String speedString = speed / 1024 + " kb/s";
        if(speed>0){
            loadingView.setLoadingTextVisiable(View.VISIBLE);
        }else if(speed<=0 && !isP2p ){
            loadingView.setLoadingTextVisiable(View.GONE);
        }
        int percent = 0;
        if(MediaHelp.mPlayer!=null) {
            percent = MediaHelp.mPlayer.getDownloadPercent();
        }
        if(!TextUtils.isEmpty(speedString)) {

            if(MediaHelp.mPlayer!=null) {
                  percent = MediaHelp.mPlayer.getDownloadPercent();
                if (percent < 0||percent>100) {
                  percent = 0;
                }
            }
            loadingView.setLoadingText(speedString,percent);
        }

    }

    private void handleNetWorkException(){
        if (loadingView.getVisibility()!=VISIBLE) {
            return;
        }
//        Log.d("login"," mLoadingtime = "+mLoadingTime+" mLoadingCount = "+mLoadingCount+"  current = "+System.currentTimeMillis()+",alltime = "+mLoadingAllTime);
        /**当次播放时，首次出现卡顿超过10s，提示*/
        if(mStartTipsTime!=-1 && System.currentTimeMillis() - mStartTipsTime>3*1000){
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

    private void stopUpdateSpeed() {
        if (mSpeedTimer != null) {
            mSpeedTimer.cancel();
            mSpeedTimer = null;
        }
    }

    /**
     * 关闭播放模式选择框的回调
     */
    @Override
    public void onChooseViewClose() {
        activity.setCurrentIndex(mIndex);
    }

    /**
     * 选择VR播放模式的回调
     */
    @Override
    public void doVRPlay(String sqlNo) {
        if(GLConst.GoUnity) {
           startDelayReleseTimer();
            return;
        }
        if(playerView!=null)
            playerView.relesePlayView();
        if(proxy!=null){
            proxy.p2pUninit();
        }
        stopUpdateSpeed();
        Intent intent = new Intent(getContext(), MjVrPlayerActivity.class);
//        intent.putExtra("videobean",videoBean);
        intent.putExtra("index",Integer.parseInt(sqlNo)>0?Integer.parseInt(sqlNo)-1:0);
        intent.putExtra("detail_url",((VideoDetailActivity)activity).contents);
        intent.putExtra("type",1);
        MjVrPlayerActivity.VideoBean = videoBean;
        getContext().startActivity(intent);

    }

    /**
     *选择极简模式播放回调
     * @param sqlNo
     */
    @Override
    public void doNormalPlay(String sqlNo) {

        if(!TextUtils.isEmpty(sqlNo)){
            int index = Integer.parseInt(sqlNo)-1;
            if(index>=0&&index!=mIndex){
                mIndex = index;
                videoInfo = getVideoInfo(mHdIndex,mIndex);
                if(videoInfo!=null) {
                     /*切换剧集时需要报播放完成vv*/
                    reportPlaySuccess();
                    startReport = System.currentTimeMillis();
                    reSetRoundId();
                    bootomView.currentPosition = 0;
                    mPlaySelectWindow.setCurIndex(mIndex);
                    path = videoInfo.getPlay_url();
                    playerView.reSetPlay();
                    startPlayer();
                    reportVV("1", "0", "0"  );
                }
            }


        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changePlayerScreen(PlayerScreenMode.fullscreen,true);
                activity.setCurrentIndex(mIndex);
            }
        });

    }



    private Timer delayReleseTimer;

    private void stopDelayReleseTimer(){
        if(delayReleseTimer!=null){
            delayReleseTimer.cancel();
            delayReleseTimer = null;
        }
    }

    /**
     * 用户选择VR播放时 release详情播放的player（由于跳转U3D有3秒钟等待时间所以延迟3s release）
     */
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
                        if(playerView!=null)
                            playerView.destroyView();
                        if(proxy!=null){
                            proxy.p2pUninit();
                        }
                        stopUpdateSpeed();
                    }
                });
            }
        },3*1000);

    }

    private int getHdIndex(String hdType){
        if(mDataList==null||mDataList.size()<=0)
            return -1;
        int hdIndex = -1;
        for (int i = mDataList.size()-1; i>=0 ; i--) {
            if (hdType.equals(mDataList.get(i).getHdtype()+"")) {
                hdIndex = i;
                break;
            }
        }
        return hdIndex;
    }

    /**
     * 切换清晰度
     * @param hdType
     */
    private void changeHdType(final String hdType) {
          int hdIndex = -1;
        for (int i = mDataList.size()-1; i>=0 ; i--) {
            if (hdType.equals(mDataList.get(i).getHdtype()+"")) {
                hdIndex = i;
                break;
            }
        }

        if (hdIndex>=0&&mHdIndex == hdIndex) {
            return;
        }

        final VideoDetailBean.AlbumsBean.VideosBean mVideoInfo = getVideoInfo(hdIndex, mIndex);

        if (mVideoInfo != null) {
            mHdIndex = hdIndex;
            mCurDefinition = hdType;
						/*切换清晰度时需要报播放完成vv*/
            reportPlaySuccess();
            startReport = System.currentTimeMillis();
            reSetRoundId();
            videoInfo = mVideoInfo;
            rePlay();
            reportVV("1", "0", "0"  );
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,"正在切换到"+hdType+"P清晰度",Toast.LENGTH_SHORT).show();
                    bootomView.setDefinitionText(hdType);
                }
            });

        } else {
            // TODO 为找到播放信息
        }

        return;
    }


    /**
     * 清晰度选择
     * @param hdtype
     */
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

    /**
     * 选集
     * @param index
     */
    @Override
    public void onChangeSelectIndex(int index) {
        if(mIndex==index)
            return;
        final VideoDetailBean.AlbumsBean.VideosBean mvideoInfo = getVideoInfo(mHdIndex, index);
        if (mvideoInfo != null) {
		 /*切换剧集时需要报播放完成vv*/
            reportPlaySuccess();
            startReport = System.currentTimeMillis();
            reSetRoundId();
            bootomView.currentPosition = 0;
            mIndex = index;
            videoInfo = mvideoInfo;
            rePlay();
            reportVV("1", "0", "0"  );
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(videoInfo!=null) {
                    activity.setCurrentIndex(mIndex);
                    Toast.makeText(activity,"正在为您播放第" + (mIndex + 1) + "集",Toast.LENGTH_SHORT).show();
                }
                if(titleBackView!=null){
                    titleBackView.getNameTV().setText(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
                }
            }
        });
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
        reportVV("3",utime<0?"0":utime+"",ltime<0?"0":ltime+"" );
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
        reportVV("7",utime<0?"0":utime+"",ltime<0?"0":ltime+"");
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
            hs.put("movieid", videoBean.getId()+"");
        }
        hs.put("movietypeid",videoBean.getCategory_type()+"");
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus &&currentMode==PlayerScreenMode.fullscreen) {
            setImmersiveSticky();
        }
    }

    private void setImmersiveSticky() {
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

