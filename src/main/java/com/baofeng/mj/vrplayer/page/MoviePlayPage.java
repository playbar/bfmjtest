package com.baofeng.mj.vrplayer.page;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mj.ui.online.utils.SortComparatorVideo;
import com.baofeng.mj.ui.online.view.GLMovieBasePlayer;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.interfaces.ISkyBoxChangedCallBack;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.MJGLUtils;
import com.baofeng.mj.vrplayer.utils.SkyboxManager;
import com.baofeng.mj.vrplayer.view.GLLockScreenView;
import com.baofeng.mj.vrplayer.view.GLTextToast;
import com.baofeng.mj.vrplayer.view.GLUnLockDialog;
import com.baofeng.mj.vrplayer.view.MoviePlayerControlView;
import com.bfmj.sdk.util.TimeFormat;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLPlayerView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.google.gson.Gson;
import com.storm.smart.core.IProxy;
import com.storm.smart.core.URlHandleProxyFactory;
import com.storm.smart.domain.P2pInfo;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.utils.PlayCheckUtil;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2017/4/1.
 * 影院在线播放
 */
public class MoviePlayPage extends BasePlayerPage implements ISkyBoxChangedCallBack {
    private   int playerWidth = 1460;
    private   int playerHeight = 821;
    private final int playerLeft = 500;
    private final int playerTop = 800;
    private final int player_x=0;

    private static String VIDEO_SCREEN = "video_screen";
    private GLRelativeView mMovieView;
    VideoDetailBean videoBean;
    List<VideoDetailBean.AlbumsBean> mDataList;
    private int mHdIndex = 0; //当前选择的清晰度index
    private int mIndex = 0;  //当前播放的第几集
    private VideoDetailBean.AlbumsBean.VideosBean videoInfo;//当前播放的剧集

    HistoryInfo historyInfo;//存储的播放的历史数据
    private IProxy proxy;
    private String path;
    private String mCurDefinition; //当前选择的清晰度text
    String detailUrl;//保存数据库  u3d播放需要用到
    private GLMovieBasePlayer playerView;
    boolean isP2p = false;
    private int decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
    GLLockScreenView lockView;
    private Timer mSpeedTimer;
    public MoviePlayPage(Context context) {
        super(context, MoviePlayerControlView.MOVIE);
    }

    @Override
    protected GLRectView createView(GLExtraData glExtraData) {
        super.createView(glExtraData);
        	/*切换播放场景*/
        int skyBoxtype = SettingSpBusiness.getInstance().getSkyboxIndex();
        mActivity.showSkyBox(skyBoxtype);
        reSetDepthParams(skyBoxtype);
        SkyboxManager.getInstance().onBind(this);
        createPlayerView();

        /*底部播放控制*/
        createPlayerSettingView();
        createPlayerControlView();
        // 顶部锁屏
        createLockView();
        createToastView();
        initData(glExtraData);
        moviePlayerControlView.updateDisplayDuration(videoInfo.getDuration()*1000);
        moviePlayerControlView.setName(videoBean.getTitle());
        moviePlayerSettingView.setMovieVideoDatas(videoBean,mIndex);
        return mRootView;
    }

    private void createLockView(){
        GLRelativeView layer = new GLRelativeView(mActivity);
        layer.setLayoutParams(2400,2400);
        layer.setHandleFocus(false);
        lockView= new GLLockScreenView(mActivity);
//        lockView.setX(1200-GLLockScreenView.lock_view_widht/2);
//        lockView.setY(1200-playerHeight/2-80);
        lockView.setMargin(1200-GLLockScreenView.lock_view_widht/2,1200-playerHeight/2, 0,0);
        layer.addView(lockView);
        layer.setDepth(GLConst.LockScreen_Depth,GLConst.LockScreen_Scale);
        mRootView.addView(layer);
        lockView.setViewVisable(false);
        setLockListener();
    }
    GLRelativeView mPlayLayer;
    private void createPlayerView(){
        	/*中间的播放view*/
//        playerWidth = (int)MJGLUtils.GLUnitToPx(10);
//        playerWidth = 2400;
//        playerHeight = playerWidth*9/16;
        mPlayLayer = new GLRelativeView(mActivity);
        mPlayLayer.setLayoutParams(2400,2400);
        mPlayLayer.setHandleFocus(false);
        mMovieView = new GLRelativeView(mActivity);
        mMovieView.setId(VIDEO_SCREEN);
        mMovieView.setLayoutParams(playerWidth,
                playerHeight);
        mMovieView.setHandleFocus(false);
//        mMovieView.setX(playerLeft);
//        mMovieView.setY(1200-playerHeight/2);
        mMovieView.setMargin(1200-playerWidth/2,1200-playerHeight/2, 0, 0);
        mMovieView.setOnKeyListener(pageKeyListener);
        mPlayLayer.addView(mMovieView);
        mPlayLayer.setDepth(GLConst.Movie_Player_Depth,GLConst.Movie_Player_Scale);
        mPlayLayer.setEyeDeviation(0);
        mRootView.addView(mPlayLayer);

    }


    private void initData(GLExtraData data) {
        if(data==null)
            return;
         VideoDetailBean bean = (VideoDetailBean) data.getExtraObject("videobean");
        int index = data.getExtraInt("index");
         detailUrl = data.getExtraString("detail_url");
        videoBean = bean;
        mIndex = index;
        setData();
    }

    /**
     * 设置播放资源数据
     */
    public void setData(){
        if(videoBean==null){
            return ;
        }
        startReport = System.currentTimeMillis();
        reSetRoundId();
        mDataList = videoBean.getAlbums();
        SortComparatorVideo comparator = new SortComparatorVideo();
        Collections.sort(mDataList,comparator);
        initPlayFromHistory();
        initPlay();
        getHDData();
        show_load_toast = true;
        if(mPrepareSeekTo>1000) {
            String time = TimeFormat.formatCH(mPrepareSeekTo/1000,false);
            glLoadToast.setText(time,2);
        }else {
            glLoadToast.setText(videoBean.getTitle(),1);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPlayer();
            }
        });

    }

    public void setLockListener(){
        lockView.setLockCallback(new GLLockScreenView.ILockScreenListener() {
            @Override
            public void onLockChanged(boolean isLocked) {
                mRootView.setFixed(isLocked);
//                mSelectView.setLocked(isLocked);
                ((GLBaseActivity)getContext()).setCursorFixed(!isLocked);
                ((GLBaseActivity)getContext()).setSkyboxFixed(isLocked);
                ((GLBaseActivity) getContext()).fixedResetView(isLocked);

            }
        });
    }
    @Override
    public void onResume() {
        isPauseView = false;
        if(playerView==null){

        }else {
            startPlay();
        }
        super.onResume();

            //没有异常弹窗并且是可以播放的状态下，根据上次的播放状态设置继续播放或暂停状态。否则只显示页面不进行播放操作
            if(!isDialogShowing()) {
                if (moviePlayerControlView.isPlayFlag()) {
                    pause2Play();
                } else {
                    if (mCurrentState != PlayerState.PREPARED) {
                        rePlay();
                    } else {
                        startPlay();
                    }
                }
            }
        startUpdateSpeed();
    }

    @Override
    public void onPause() {
        isPauseView = true;
        if(playerView!=null) {
            pausePlay();
            saveHistory();
        }
        super.onPause();
        stopUpdateSpeed();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if(!(mCurrentState== PlayerState.COMPLETE||mCurrentState== PlayerState.ERROR)) {
            reportPlaySuccess();
        }
        if(playerView!=null) {
            pausePlay();
            destroyView();
        }
        stopUpdateSpeed();
        if(proxy!=null) {
            proxy.p2pUninit();
        }
        SkyboxManager.getInstance().unBind(this);
        playerView = null;
    }
    public void destroyView() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaHelp.release();
                MediaHelp.mPlayer = null;
                mCurrentState = PlayerState.IDEL;
//        if(playerView!=null){
//            playerView.release();
//        }
                MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
                MediaHelp.mState = MediaHelp.STATE_IDLE;
                mCurrentState = PlayerState.IDEL;
            }
        });


    }


    /**
     * 进入后台，保存播放记录
     */
    private void saveHistory(){
        if(videoBean==null||videoInfo==null)
            return;
        if(lastPlaytime<=0){
            return;
        }
        if(historyInfo==null){
            historyInfo = new HistoryInfo();
            historyInfo.setAudioTrack(-1);
            historyInfo.setType(1);
            historyInfo.setResType(1);
            historyInfo.setVideo3dType(videoBean.getIs_3d()+1);
            historyInfo.setVideoType(1);
            historyInfo.setDetailUrl(detailUrl);
            historyInfo.setLastSetIndex(mIndex);
            historyInfo.setPlayDuration(lastPlaytime);
            historyInfo.setPlayFinished(isPlayCompletion?1:0);
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
            historyInfo.setPlayDuration(lastPlaytime);
            historyInfo.setPlayFinished(isPlayCompletion?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setDetailUrl(detailUrl);
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
            if(mHdIndex<0){
                mHdIndex =initDefaultHd();
            }

            int index = historyInfo.getLastSetIndex();
            if(index==mIndex){
                mPrepareSeekTo = playPos ;
            }
//            mPlaySelectWindow.setCurIndex(mIndex);
            int type = HistoryBusiness.JudgePlayerTypeCoreToHistroy(historyInfo.getPlayType());
            if(type>0) {
                decodeType = type;
            }

        } else {
            mHdIndex = initDefaultHd();
        }

    }

    private void getHDData() {
        if(mDataList==null||mDataList.size()<=0)
            return;
        if(mHdTextList!=null){
            mHdTextList.clear();
        }
//        mHdTextList.add("自动");
        for (int i=0;i<=mDataList.size()-1;i++) {
            mHdTextList.add(mDataList.get(i).getHdtype() + "");
        }
        setHDData(mHdTextList,mCurDefinition);
    }

    /**
     * 首次初始完数据开始播放
     */
    private void initPlay(){
        videoInfo = getVideoInfo(mHdIndex,mIndex);
        if(videoInfo==null)
            return;
        path = videoInfo.getPlay_url();
         /**特殊格式用软解*/
        if (path.startsWith("qstp:")||path.startsWith("yun:") || path.startsWith("yunlive:")){
            decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;
        }
        MediaHelp.decodeType = decodeType;
//        playerView = new GLMovieBasePlayer(mActivity, simpleBaofengListener,decodeType);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaHelp.release();
                MediaHelp.mPlayer = null;
                MediaHelp.createPlayer(mActivity);
            }
        });
        playerView = new GLMovieBasePlayer(mActivity,null);
        playerView.setLayoutParams(playerWidth,playerHeight);
        playerView.scale(GLConst.Movie_Player_Scale);
        mMovieView.addView(0, playerView);
        playerView.setDepth(GLConst.Movie_Player_Depth);
        playerView.setListener(simpleBaofengListener);
        playerView.setEyeDeviation(0);
        int is3D = videoBean.getIs_3d();
        if (is3D==1) {
            playerView.setPlayMode(GLPlayerView.MODE_3D_LEFT_RIGHT);
        } else {
            playerView.setPlayMode(GLPlayerView.MODE_2D);
        }
    }


    //播放
    public void startPlayer() {
        PlayCheckUtil.setSupportLeftEye(true);
        isP2p = false;
        if (path.startsWith("qstp:")) {  //qstp 格式的用p2p播放
            URlHandleProxyFactory.getIProxy(getContext(), path);
            proxy = URlHandleProxyFactory.getInstance();
            path = P2pInfo.P2P_PLAY_SERVER_PATH;
            isP2p = true;
        } else if (path.startsWith("yun:") || path.startsWith("yunlive:")) { //云视频
            URlHandleProxyFactory.getIProxy(getContext(), path);
            proxy = URlHandleProxyFactory.getInstance();
            proxy.setcallback(new IProxy.UrlCallBack() {
                @Override
                public void mcallBack(String state) {
                    if (state != null && !"4".equals(state)) {
                        path = state;
                    }
                    setPath(path);

                }
            });
            if(proxy!=null){
                proxy.p2pStartPlay(path);
            }
            isP2p = true;
        }

        if(isP2p) {
//            loadingView.setLoadingTextVisiable(View.VISIBLE);
        }
        setPath(path);

    }

    @Override
    protected void setPath(final String path ){
        super.setPath(path);
        this.path = path;
        isPlayCompletion = false;
        mCurrentState = PlayerState.IDEL;
        if(playerView!=null) {
            playerView.setVideoPath(path);
        }

    }

    /**
     * 重播
     */
    public void rePlay(){
        if(videoInfo==null)
            return;
        path = videoInfo.getPlay_url();
        if (path.startsWith("qstp:")||path.startsWith("yun:") || path.startsWith("yunlive:")){
            decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;//如果默认软解播可以设置这里
        }else {
            decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
        }
        MediaHelp.decodeType = decodeType;
        reSetPlay();

    }

    public void reSetPlay(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaHelp.release();
                MediaHelp.mPlayer = null;
                MediaHelp.createPlayer(getContext());

                mCurrentState = PlayerState.IDEL;
                startPlayer();
            }
        });

    }

    /**
     * 选集
     * @param num
     */
    public void onChangeSelectIndex(int num) {
        int index = mIndex;
        if(num>0){
            index = num-1;
        }
        if(mIndex==index)
            return;
        final VideoDetailBean.AlbumsBean.VideosBean mvideoInfo = getVideoInfo(mHdIndex, index);
        if (mvideoInfo != null) {
		 /*切换剧集时需要报播放完成vv*/
            reportPlaySuccess();
            startReport = System.currentTimeMillis();
            reSetRoundId();
//            bootomView.currentPosition = 0;
            mIndex = index;
            videoInfo = mvideoInfo;
            rePlay();
            reportVV("1", "0", "0"  );
        }

                if(videoInfo!=null) {
                    show_load_toast = true;
                    glLoadToast.setText("第"+(mIndex + 1)+ "集",1);
                }
                if(moviePlayerControlView!=null){
                    moviePlayerControlView.setName(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
                }
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
     * 清晰度选择
     * @param hdtype
     */
    @Override
    public void onChangeHd(final String hdtype) {
        int result = SettingSpBusiness.getInstance().getHigh();
        if (2 != result && "4k".equals(hdtype)) {
            unLockDialog.setmUnLockCallBack(new GLUnLockDialog.UnLockCallBack() {
                @Override
                public void onConfirm() {
                    changeHdType(hdtype);
                }

                @Override
                public void onCancel() {
                    moviePlayerSettingView.setSelectedHD(mCurDefinition);
                }
            });
            unLockDialog.setVisible(true);
            return;
        }
        changeHdType(hdtype);
    }
    /**
     * 切换清晰度
     * @param hdType
     */
    private void changeHdType(String hdType) {
        if (mDataList == null || mDataList.size() == 0) {
            return ;
        }
        int hdIndex = -1;
        for (int i = mDataList.size()-1; i>=0 ; i--) {
            if (hdType.equals(mDataList.get(i).getHdtype()+"")) {
                hdIndex = i;
                break;
            }
        }
        if (hdIndex<0){ //自动
            hdIndex =initDefaultHd();
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
            mPrepareSeekTo = lastPlaytime;
            rePlay();
            reportVV("1", "0", "0"  );
            glTextToast.showToast("切换中", GLTextToast.SHORT);
            hd_change_flag = true;
            moviePlayerSettingView.setSelectedHD(hdType);
        } else {
            // TODO 为找到播放信息
        }

        return;
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


    @Override
    public void startPlay() {
        start2play();
    }

    public void start2play(){
        if (playerView == null || playerView.isPlaying()) {
            return;
        }
        if (isPlayCompletion) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerView.setVideoPath(path);
                }
            });

        }else {
            playerView.start();
        }

    }

    @Override
    public void pausePlay(){
        pause2Play();
    }

    public void pause2Play() {
        if (playerView == null || !playerView.isPlaying()) {
            return;
        }
        playerView.pause();

    }
    /**
     * 显示loading
     */
    @Override
    public void hideLoading(){
        super.hideLoading();
    }

    /**
     * 隐藏loading
     */
    @Override
    public void showLoading(){
       super.showLoading();
    }

    @Override
    protected void updateProgress() {
        if (playerView == null ) {
            return;
        }
        if(MediaHelp.mPlayer==null)
            return;
        /**java.lang.NullPointerException  添加 try-catch 20151019 whf*/
        try{

            int duration = playerView.getDuration();
			/*添加该判断是因为有戏视频流获取不到duration*/
            if(duration<=0&&videoInfo!=null){
                duration = videoInfo.getDuration()*1000;
            }
            final int current = mPrepareSeekTo > -1 ? mPrepareSeekTo : playerView
                    .getCurrentPosition();
            if (current < 0) {
                return;
            }
            if(lastPlaytime>0&&lastPlaytime==current){
                return;
            }
            if(moviePlayerControlView!=null){
                moviePlayerControlView.updateProgress(current,duration);
            }
            lastPlaytime = current;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(int position) {
        if(playerView==null)
            return;
           showLoading();
            try {
               playerView.seekTo(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void networkChange(int currentNetwork) {
        super.networkChange(currentNetwork);
    }


    @Override
    public void onPlayPrepared() {
        super.onPlayPrepared();
        if(hd_change_flag){
            glTextToast.showToast("已为您切换到"+mCurDefinition+"P清晰度",GLTextToast.MEDIUM);
            hd_change_flag = false;
        }
    }

    /**
     * 播放完成后回调了该方法（在playerView中），播放完后自动播放下集或者重新播放
     */
    @Override
    public void onPlayComplete(){
        super.onPlayComplete();
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
             reSetPlay();

            reportVV("1", "0", "0"  );
            show_load_toast = true;
            glLoadToast.setText("第" + (mIndex + 1) + "集",1);
            showLoading();
            if(moviePlayerSettingView!=null){
                moviePlayerSettingView.setCurrentNum(mIndex);
            }
            if(moviePlayerControlView!=null){
                moviePlayerControlView.setName(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
            }


        }else {
            isPlayCompletion = true;
            mCurrentState = PlayerState.COMPLETE;

//            bootomView.initPlayStatus();
            mIndex = 0;
            VideoDetailBean.AlbumsBean.VideosBean mvideoInfo = getVideoInfo(mHdIndex,mIndex);
            if(mvideoInfo!=null){
                this.videoInfo = mvideoInfo;
            }
            rePlay();
            reportVV("1", "0", "0"  );
            if(moviePlayerSettingView!=null){
                moviePlayerSettingView.setCurrentNum(mIndex);
            }
            if(moviePlayerControlView!=null){
                moviePlayerControlView.setName(videoBean.getTitle()+" 第"+(mIndex+1)+"集");
            }
        }
    }


    @Override
    protected void reportVV(String type, String utime, String ltime) {
        if(videoBean==null)
            return;
        HashMap<String, String> hs = new HashMap<String, String>();
        hs.put("etype", "vv");
        hs.put("tpos", "0");
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
            if(playerView!=null&&decodeType!= IBfPlayerConstant.IBasePlayerType.TYPE_SYS) {
                averaspeed = playerView.getAvgSpeed();
            }
            if(averaspeed>0) {
                hs.put("averaspeed", averaspeed + "byte/s");
            }
        }

        ReportBusiness.getInstance().reportVV(hs);
    }




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
//                    final int speed = proxy.p2pGetSpeed(0);
//                    updateSpeed(speed);
                }
                if (getLoadToastVisiable()) {
                    handleNetWorkException();
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

        if (!getLoadToastVisiable()) {
            return;
        }
//        final String speedString = speed / 1024 + " kb/s";
//         if(speed<=0 && !isP2p ){
//             glProcessView.setSpeedText("");
//             return;
//        }
//        int percent = 0;
//        if(MediaHelp.mPlayer!=null) {
//            percent = MediaHelp.mPlayer.getDownloadPercent();
//            Log.d("login","---getdownloadPercent percent = "+percent);
//        }
//        if(!TextUtils.isEmpty(speedString)) {
            /*加载百分比*/
//            if(MediaHelp.mPlayer!=null) {
//                percent = MediaHelp.mPlayer.getDownloadPercent();
//                if (percent < 0||percent>100) {
//                    percent = 0;
//                }
//            }
//            glProcessView.setSpeedText(speedString);
//        }

    }

    private void stopUpdateSpeed() {
        if (mSpeedTimer != null) {
            mSpeedTimer.cancel();
            mSpeedTimer = null;
        }
    }

    /*如果发现是非wifi网络 需要停止p2p加载数据*/
    @Override
    protected void onMobileNet() {
        super.onMobileNet();
        stopUpdateSpeed();
        if(proxy!=null)
          proxy.p2pStopPlay();
    }

    /**
     * 网络异常下的重播
     */
    @Override
    public void doExceptionRePlay(){
        super.doExceptionRePlay();
        if(!NetworkUtil.isNetworkConnected(getContext())){ //无网络时 不处理
            return;
        }
        if(playerView!=null){
            if(glDialogView.getIsContinueBtn()){//继续播放
                if(proxy!=null) {
                    path = videoInfo.getPlay_url();
                    proxy.p2pStartPlay(path);
                    startUpdateSpeed();
                }
                startPlay();
//                rePlay();
            }else{//从新播放

                rePlay();
            }
           hideDialogView();
        }
    }

    @Override
    public void onSkyBoxChanged(int type) {
       int curtype =  SettingSpBusiness.getInstance().getSkyboxIndex();
        if(type==curtype)
            return;
        reSetDepthParams(type);
        switch (type){
            case GLBaseActivity.SCENE_TYPE_CINEMA:  //18
                 switch (curtype){
                     case GLBaseActivity.SCENE_TYPE_HOME:
                         handleSkyBoxChange(3.6f);

                         break;
                     case GLBaseActivity.SCENE_TYPE_OUTCINEMA:

                         handleSkyBoxChange(2.5f);
                         break;
                 }
                break;
            case GLBaseActivity.SCENE_TYPE_HOME:   //5
                switch (curtype){
                    case GLBaseActivity.SCENE_TYPE_CINEMA:
                        handleSkyBoxChange(0.28f);
                        break;
                    case GLBaseActivity.SCENE_TYPE_OUTCINEMA:
                        handleSkyBoxChange(0.7f);
                        break;
                }
                    break;
            case GLBaseActivity.SCENE_TYPE_OUTCINEMA:  //7
                switch (curtype){
                    case GLBaseActivity.SCENE_TYPE_CINEMA:
                        handleSkyBoxChange(0.39f);
                        break;
                    case GLBaseActivity.SCENE_TYPE_HOME:
                        handleSkyBoxChange(1.4f);
                        break;

                }
                    break;
        }

    }

    public void reSetDepthParams(int type){
        switch (type) {
            case GLBaseActivity.SCENE_TYPE_CINEMA: //18
                GLConst.Movie_Player_Depth = 18;
                GLConst.Movie_Player_Scale = 4.5f;
                break;
            case GLBaseActivity.SCENE_TYPE_HOME: //5
                GLConst.Movie_Player_Depth = 5;
                GLConst.Movie_Player_Scale = 1.25f;
                break;
            case GLBaseActivity.SCENE_TYPE_OUTCINEMA: //7
                GLConst.Movie_Player_Depth = 7;
                GLConst.Movie_Player_Scale = 1.75f;
                break;
        }
    }

    private void handleSkyBoxChange(float scale){
        if(mPlayLayer!=null) {
            mPlayLayer.setDepth(GLConst.Movie_Player_Depth,scale);
        }
    }

    @Override
    protected void doOpenMenu(boolean isOpen) {
        super.doOpenMenu(isOpen);
       lockView.setViewVisable(isOpen);
    }
}
