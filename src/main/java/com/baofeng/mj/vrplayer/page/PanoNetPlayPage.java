package com.baofeng.mj.vrplayer.page;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.Matrix;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.PanoramaDetailActivity;
import com.baofeng.mj.ui.online.utils.GLQiuPlayer;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mj.ui.online.utils.SortComparator;
import com.baofeng.mj.ui.online.view.PanoramVideoPlayerView;
import com.baofeng.mj.ui.online.view.VideoSelectHdView;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.view.GLTextToast;
import com.baofeng.mj.vrplayer.view.GLUnLockDialog;
import com.baofeng.mj.vrplayer.view.MoviePlayerControlView;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.sdk.util.TimeFormat;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.interfaces.IGLPlayer;
import com.bfmj.viewcore.interfaces.IGLPlayerListener;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.google.gson.Gson;
import com.storm.smart.play.call.IBfPlayerConstant;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2017/4/1.
 * 全景在线播放
 */
public class PanoNetPlayPage extends BasePlayerPage {
    private static String VIDEO_SCREEN = "video_screen";
    PanoramaVideoBean videoBean;
    PanoramaVideoAttrs videoInfo;
    HistoryInfo historyInfo;//存储的播放的历史数据
    private String mCurDefinition; //当前选择的清晰度text
    String detailUrl;//保存数据库  u3d播放需要用到
    private String mPath;
    GLQiuPlayer playerView;
    private int decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
    public PanoNetPlayPage(Context context) {
        super(context, MoviePlayerControlView.PANO);
    }

    @Override
    protected GLRectView createView(GLExtraData glExtraData) {
        super.createView(glExtraData);
        	/*切换播放场景*/
        mActivity.hideSkyBox();

//		/*中间的播放view*/
//        mMovieView = new GLRelativeView(mActivity);
//        mMovieView.setId(VIDEO_SCREEN);
//        mMovieView.setLayoutParams(playerWidth,
//                playerHeight);
//        mMovieView.setX(layout_x);
//        mMovieView.setY(layout_y);
//        mMovieView.setMargin(playerLeft,layout_y, 0, 0);
//        mMovieView.setOnKeyListener(pageKeyListener);
//        mMovieView.setFocusListener(pageFocusListener);
//        mRootView.addView(mMovieView);

        /*底部播放控制*/
        createPlayerControlView();
        createPlayerSettingView();
        createToastView();
        initData(glExtraData);
        moviePlayerControlView.updateDisplayDuration(videoBean.getDuration()*1000);
        moviePlayerControlView.setName(videoBean.getTitle());
        return mRootView;
    }

    @Override
    protected void updateProgress() {
        if (playerView == null ) {
            return;
        }
        /**java.lang.NullPointerException  添加 try-catch 20151019 whf*/
        try{

            int duration = playerView.getDuration();
			/*添加该判断是因为有戏视频流获取不到duration*/
            if(duration<=0&&videoBean!=null){
                duration = videoBean.getDuration()*1000;
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
    protected void startPlay() {
        if (playerView == null || playerView.isPlaying()) {
            return;
        }
        if (isPlayCompletion) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerView.setVideoPath(mPath);
                }
            });

        }else {
            playerView.start();
        }
    }

    @Override
    protected void pausePlay() {
        if (playerView == null || !playerView.isPlaying()) {
            return;
        }
        playerView.pause();
    }

    @Override
    protected void seekTo(int current_pos) {
        if(playerView==null)
            return;
        showLoading();
        try {
            playerView.seekTo(current_pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayComplete() {
        super.onPlayComplete();
        isPlayCompletion = true;
        mCurrentState = PlayerState.COMPLETE;
        lastPlaytime = 0;
        mPrepareSeekTo = -1;
        rePlay();
        reportVV("1", "0", "0"  );
//            activity.setCurrentIndex(mIndex);
//            mPlaySelectWindow.setCurIndex(mIndex);

    }

    @Override
    public void onPlayPrepared() {
        super.onPlayPrepared();
        if(hd_change_flag){
            glTextToast.showToast("已为您切换到"+mCurDefinition+"P清晰度",GLTextToast.MEDIUM);
            hd_change_flag = false;
        }
    }

    @Override
    protected void reportVV(String type, String utime, String ltime) {
        if(videoBean==null)
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
         //       averaspeed = MediaHelp.mPlayer.getAverageSpeed();
            }
            if(averaspeed>0) {
                hs.put("averaspeed", averaspeed + "byte/s");
            }
        }

        ReportBusiness.getInstance().reportVV(hs);
    }

    @Override
    protected void rePlay() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaHelp.release();
                MediaHelp.createPlayer(getContext());
                mCurrentState = PlayerState.IDEL;
                startPlayer();
            }
        });

    }

    @Override
    protected void showLoading() {
        super.showLoading();
        if(getLoadToastVisiable()) {
            startCheckLoading();
        }
    }

    @Override
    protected void hideLoading() {
        super.hideLoading();
        stopCheckTimer();
    }

    private void initData(GLExtraData data) {
        if(data==null)
            return;
        PanoramaVideoBean bean = (PanoramaVideoBean) data.getExtraObject("videobean");
        int index = data.getExtraInt("index");
        detailUrl = data.getExtraString("detail_url");
        videoBean = bean;
        setData();
    }


    /**
     * 设置播放资源数据
     */
    public void setData( ){
        if(videoBean==null)
            return ;
        startReport = System.currentTimeMillis();
        reSetRoundId();
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

           int playerType = HistoryBusiness.JudgePlayerTypeHistroyToCore(historyInfo.getPlayType());
            if(playerType>0) {
                decodeType = playerType;
            }
            if (!(historyInfo.getPlayFinished() == 1)) {
                playPos = historyInfo.getPlayDuration();
            }
            mCurDefinition = historyInfo.getVideoClarity();
            PanoramaVideoAttrs info =  getVideoInfoByDefinition(videos,mCurDefinition);
            if(info!=null) {
                videoInfo = info;
            }else {
                setDefaultDefinitionVideo(videos);
            }
            mPrepareSeekTo = playPos ;
        } else {
            setDefaultDefinitionVideo(videos);
        }

        if(videoInfo==null){
            videoInfo = videos.get(0);
        }
        MediaHelp.decodeType = decodeType > 0 ? decodeType : IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
        initPlay();
        getHDData();
        if(mPath==null){
            return;
        }
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


    private void startPlayer() {
        setPath(mPath);
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
                pausePlay();
            } else {
                if (mCurrentState != PlayerState.PREPARED) {
                    rePlay();
                } else {
                    startPlay();
                }
            }
        }

    }

    @Override
    public void onPause() {
        isPauseView = true;
        if(playerView!=null) {
            pausePlay();
            saveHistory();
        }
        super.onPause();

    }
    private void getHDData() {
        List<PanoramaVideoAttrs> videos = videoBean.getVideo_attrs();
        if(videos==null||videos.size()<=0)
            return;
        if(mHdTextList!=null){
            mHdTextList.clear();
        }
//        mHdTextList.add("自动");
        for (int i=0;i<=videos.size()-1;i++) {
            mHdTextList.add(videos.get(i).getDefinition_name() + "");
        }
        setHDData(mHdTextList,mCurDefinition);
    }


    /**
     * 存储历史记录
     */
    public void saveHistory(){
        if(videoBean==null||playerView==null||videoInfo==null)
            return;
        if(lastPlaytime<=0){
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
            historyInfo.setDetailUrl(detailUrl);
            historyInfo.setLastSetIndex(0);
            historyInfo.setPlayDuration(lastPlaytime);
            historyInfo.setPlayFinished(isPlayCompletion?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            historyInfo.setTotalDuration(videoBean.getDuration()*1000);
            historyInfo.setVideoId(videoBean.getRes_id());
            historyInfo.setVideoImg(videoBean.getThumb_pic_url().get(0));
            if(videoInfo!=null) {
                historyInfo.setVideoPlayUrl(videoInfo.getPlay_url());
            }
            historyInfo.setVideoSet(1);
            historyInfo.setVideoTitle(videoBean.getTitle());
        }else {
            historyInfo.setPlayDuration(lastPlaytime);
            historyInfo.setPlayFinished(isPlayCompletion?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setVideoClarity(mCurDefinition);
            if(videoInfo!=null) {
                historyInfo.setVideoPlayUrl(videoInfo.getPlay_url());
            }
            historyInfo.setDetailUrl(detailUrl);
            historyInfo.setVideoTitle(videoBean.getTitle());
            historyInfo.setVideoImg(videoBean.getThumb_pic_url().get(0));
        }
        historyInfo.setPlayType(HistoryBusiness.JudgePlayerTypeCoreToHistroy(MediaHelp.decodeType));
        String json=new Gson().toJson(historyInfo);
        HistoryBusiness.writeToHistory(json,videoBean.getRes_id(),1);
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
                videoInfo = video;
                break;
            }
        }
    }

    @Override
    protected void setPath(String path ){
        super.setPath(path);
        this.mPath = path;
        isPlayCompletion = false;
        if(playerView!=null) {
            playerView.setVideoPath(path);
        }
        showLoading();
    }

    private void initPlay(){
        if(videoInfo==null)
            return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MediaHelp.mPlayer == null) {
                    MediaHelp.createPlayer(mActivity);
                }
            }
        });
        playerView = new GLQiuPlayer(mActivity, null){
            @Override
            public void draw() {
                if (getSceneType() == SCENE_TYPE_SPHERE){
                    // 针对球模型增大可视范围 lixianke
                    float[] fM = getMatrixState().getVMatrix();

                    float[] vMatrix = new float[16];
                    Matrix.setLookAtM(vMatrix, 0, 0, 0, 45, 0f, 0.0f, -0.1f, 0f, 1.0f, 0.0f);
                    getMatrixState().setVMatrix(vMatrix);

                    System.arraycopy(fM, 0, getMatrixState().getCurrentMatrix(), 0, 16);

                    float near = (float) (1 / Math.tan(Math.toRadians(88 / 2)));
                    GLScreenParams.setNear(near);
                }
                super.draw();
            }
        };
        playerView.translate(6, 0, 0);
        playerView.setVisible(true);
        playerView.setListener(simpleBaofengListener);
        mActivity.getRootView().addView(playerView);
            String ballUrl = videoInfo.getBall_url();
        if(!TextUtils.isEmpty(ballUrl)){    /*如果ball_url不为空 则用球模型播放*/
            mPath = ballUrl;
            if(playerView!=null){
                setInit_Pov_head(videoBean.getPov_heading());
                setSenceAndMode(2,videoBean.getVideo_dimension());
            }
        }else {
            mPath = videoInfo.getPlay_url();
            if(playerView!=null){
                setInit_Pov_head(videoBean.getPov_heading());
                setSenceAndMode(videoBean.getIs_panorama(),videoBean.getVideo_dimension());
            }
        }

    }

    public void setInit_Pov_head(int pov_head){
        if(mActivity!=null&&mActivity.getRootView()!=null){
//            mActivity.getRootView().ResetRoteDegree();
//            mActivity.getRootView().setInit_Pov_head(pov_head);
            playerView.rotate(-102, 0, 1, 0); //uv贴图偏移值
        }
    }

    /**
     * 设置播放场景和播放模式
     * @param sence
     * @param mode
     */
    public void setSenceAndMode(int sence,int mode){
        //case数值是随接口定义匹配的
        switch (sence){
            case 2://360
                playerView.setSceneType(GLPanoView.SCENE_TYPE_SPHERE);
                break;
            case 3://180
                playerView.setSceneType(GLPanoView.SCENE_TYPE_HALF_SPHERE);
                break;
            case 4://立方体
                playerView.setSceneType(GLPanoView.SCENE_TYPE_SKYBOX);
                break;
        }
        switch (mode){
            case 1:
                playerView.setPlayType(GLPanoView.PLAY_TYPE_2D);
                break;
            case 2:
                playerView.setPlayType(GLPanoView.PLAY_TYPE_3D_TB);
                break;
            case 3:
                playerView.setPlayType(GLPanoView.PLAY_TYPE_3D_LR);
                break;
        }

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
                startPlay();
            }else{//从新播放
                rePlay();
            }
            hideDialogView();
        }
    }

    //清晰度切换
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
     * @param defi_type 清晰度
     */
    private void changeHdType(String defi_type) {
        if(defi_type.equals(mCurDefinition))
            return;

        if(defi_type.equals("自动")){
            setDefaultDefinitionVideo(videoBean.getVideo_attrs());
        }else {
            videoInfo = getVideoInfo(defi_type);
            if(videoInfo!=null) {
                mCurDefinition = defi_type;
            }
        }
        if(videoInfo!=null){
            if(playerView!=null){
                reportPlaySuccess();
                startReport = System.currentTimeMillis();
                reSetRoundId();
                initPath();
                mPrepareSeekTo = lastPlaytime;
                rePlay();
                reportVV("1","0","0");
                moviePlayerSettingView.setSelectedHD(mCurDefinition);
                glTextToast.showToast("切换中", GLTextToast.SHORT);
                hd_change_flag = true;
            }
        }else {
            glTextToast.showToast("不存在该清晰度视频源",GLTextToast.MEDIUM);
        }
    }
    private void initPath(){
        if(videoInfo!=null){
            String ballUrl = videoInfo.getBall_url();
            if(!TextUtils.isEmpty(ballUrl)){    /*如果ball_url不为空 则用球模型播放*/
                mPath = ballUrl;
                if(playerView!=null){
                    setInit_Pov_head(videoBean.getPov_heading());
                    setSenceAndMode(2,videoBean.getVideo_dimension());
                }
            }else {
                mPath = videoInfo.getPlay_url();
                if(playerView!=null){
                    setInit_Pov_head(videoBean.getPov_heading());
                    setSenceAndMode(videoBean.getIs_panorama(),videoBean.getVideo_dimension());
                }
            }
        }



    }
    private PanoramaVideoAttrs  getVideoInfo(String hdtype){
        if(videoBean==null)
            return null;
        List<PanoramaVideoAttrs> videoAttrses = videoBean.getVideo_attrs();
        for(int i=0;i<videoAttrses.size();i++){
            PanoramaVideoAttrs video = videoAttrses.get(i);
            if(video.getDefinition_name().equals(hdtype)){
                //获取1080P的
                return video;
            }
        }
        return null;

    }

    @Override
    public void onFinish() {
        super.onFinish();
        if(!(mCurrentState== PlayerState.COMPLETE||mCurrentState== PlayerState.ERROR)) {
            reportPlaySuccess();
        }
        if(playerView!=null) {
            playerView.rotate(102, 0, 1, 0); //uv贴图偏移值
            pausePlay();
            destroyView();
        }
        stopCheckTimer();
        playerView = null;
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
               handleNetWorkException();
            }
        }, 300, 1000);
    }

    private void stopCheckTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
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


}
