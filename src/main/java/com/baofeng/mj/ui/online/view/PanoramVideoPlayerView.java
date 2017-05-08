package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.online.utils.GLQiuPlayer;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.bfmj.viewcore.interfaces.IGLPlayer;
import com.bfmj.viewcore.interfaces.IGLPlayerListener;
import com.bfmj.viewcore.interfaces.IGLViewClickListener;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLRootView;
import com.storm.smart.play.call.IBfPlayerConstant;


/**
 * 全景视频播放器
 */
public class PanoramVideoPlayerView extends RelativeLayout {

    public GLRootView rootView;
    private GLQiuPlayer player;
    private Context mContext;
    private BottomContrallerView mBottomContrallerView;
    PanoramPlayerPreView PreView;

    private String videoPath;
    private int currentPosition = -1;
    private boolean isDouble = false;
    private boolean isPlayCompletion = false;
    private long tempTime;//报数播放时长的临时记录每一次开始到暂停之间的时间
    private long allTime;//报数的播放时长累计数
    private boolean isError = false;
    public boolean isPauseView = false;//是否切出播放页面
    public boolean isPreparedForReport = false;
    private int playStatus = BaseApplication.FLAG_START;
    public PlayerState mCurrentState = PlayerState.IDEL;
    public enum PlayerState{  //记录播放状态  目前主要用在 播放还未加载成功前断开网络，再次连上网后需要根据State是否为PrePared的来判断重新创建player播放
        IDEL, PREPARED,COMPLETE,ERROR
    }

    public PanoramVideoPlayerView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public PanoramVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
         initView();
    }

    public PanoramVideoPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }


    public void initView(){
        rootView = new GLRootView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(lp);
        if (MediaHelp.mPlayer == null) {
            MediaHelp.createPlayer(mContext);
        }
        player = new GLQiuPlayer(mContext, rootView){
            @Override
            public void draw() {
                if (getSceneType() == SCENE_TYPE_SPHERE && PreView.doubleScreen){
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
//        player.scale(0.8f);
//        player.rotate(180, 0, 0, 1);
        player.translate(6, 0, 0);
        player.setVisible(true);
//        player.setRootView(rootView);

        rootView.addView(player);
        addView(rootView);
        this.setBackgroundResource(R.color.black);
        this.setLayoutParams(lp);
        setPlayerListener();

    }


    public void setLandScreen(final boolean island){
        if(rootView==null)
            return;
//        rootView.isLandscape = island;

    }

    public void setOnGLClickListener(IGLViewClickListener mIGLViewClickListener) {
        if(rootView==null)
            return;
        rootView.setOnGLClickListener(mIGLViewClickListener);
    }

    public void initHeadView() {
        if(rootView==null)
            return;
        rootView.queueEvent(new Runnable() {
            @Override
            public void run() {
                rootView.ResetRoteDegree();
            }
        });

    }

    /**
     * 设置全景视频播放的初始视角
     * @param pov_head
     */
    public void setInit_Pov_head(int pov_head){
        if(rootView!=null){
            
            rootView.ResetRoteDegree();
            rootView.setInit_Pov_head(pov_head);
        }
    }

    /**
     * 设置双屏
     *
     * @param isDouble
     */
    public void setDoubleScreen(boolean isDouble) {
        this.isDouble = isDouble;
        if(rootView!=null) {
            rootView.setDoubleScreen(isDouble);
        }
    }



    public void setVideoPath(String path) {
        this.videoPath = path;
        mCurrentState = PlayerState.IDEL;
        PreView.showLoading();
        player.setVideoPath(path);
    }

    public void updatePath(String videoPath){
        this.videoPath = videoPath;
    }

    public void rePlay(){
        isPlayCompletion = false;
        mCurrentState = PlayerState.IDEL;
        MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
        MediaHelp.createPlayer(mContext);
        setVideoPath(videoPath);
    }



    public void setBottomView(BottomContrallerView mBottomContrallerView) {
        this.mBottomContrallerView = mBottomContrallerView;
        mBottomContrallerView.seekbar.setEnabled(false);
    }

    public void setPanoramPlayerPreView(PanoramPlayerPreView PreView){
        this.PreView = PreView;
    }


    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public void setPlayStatus(int status) {
        playStatus = status;
    }

    public int getPlayStatus() {
        return playStatus;
    }


    public void updateStartStatus(){
        playStatus = BaseApplication.FLAG_START;
        mBottomContrallerView.initPlayStatus();
    }
    public void startPlay() {
        playStatus = BaseApplication.FLAG_START;
        mBottomContrallerView.initPlayStatus();
        start2play();
    }

    public void start2play(){
        mBottomContrallerView.reStartHandler();
        if (isPlayCompletion|| MediaHelp.mPlayer == null) {
            rePlay();
        } else {
            if (player != null) {
                tempTime = System.currentTimeMillis();
                if (player.isPlaying()) {
                    return;
                }
                player.start();
            }
        }
    }

    public void pausePlay() {
        playStatus = BaseApplication.FLAG_PAUSE;
        pause2play();
        mBottomContrallerView.initPlayStatus();
    }

    public void pause2play(){
        if (player != null) {
            if (tempTime != 0) {
                allTime += (System.currentTimeMillis() - tempTime);
                tempTime = 0;
            }
            if (!player.isPlaying()) {
                return;
            }
            player.pause();
        }
    }

    public void seekTo(int position) {

        if (player != null) {
            if(PreView!=null){
                PreView.showLoading();
            }
            if (MediaHelp.mPlayer != null) {
                try {
                    MediaHelp.mPlayer.seekTo(position);
                } catch (Exception e) {
                }
            }
        }
        playStatus = BaseApplication.FLAG_START;
        mBottomContrallerView.initPlayStatus();
    }


    public void resumeView() {
        isPauseView = false;
        if (rootView != null) {
            rootView.onResume();
        }
    }

    public void pauseView() {
        isPauseView = true;
//        if(rootView!=null){
//            rootView.onPause();
//        }
    }

    public void destroyView() {
        mBottomContrallerView.releaseThread();
        relesePlayView();
        if(player!=null){
            player.release();
        }
        if (rootView != null) {
            rootView.removeView(player);
            rootView.onDestroy();
            this.removeView(rootView);
        }
    }

    public void relesePlayView(){
        MediaHelp.release();
        MediaHelp.mPlayer = null;
        mCurrentState = PlayerState.IDEL;

    }

    /**
     * 设置陀螺仪开关
     *
     * @param value
     */
    public void setGyroscopeEnable(boolean value) {
        if (rootView != null) {
            rootView.setGroyEnable(value);
        }
    }

    /**
     * 获取陀螺仪开关状态
     *
     * @return
     */
    public boolean isGyroscopeEnable() {
        if (rootView != null) {
            return rootView.isGroyEnable();
        }
        return false;
    }

    public void setScreenTouch(boolean mIsScreenTouch) {
        if (rootView != null) {
            rootView.setScreenTouch(mIsScreenTouch);
        }
    }

    /**
     * 获取视频总时长
     *
     * @return 毫秒
     */
    public int getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return -1;
    }

    /**
     * 获取视频当前播放进度
     *
     * @return 毫秒
     */
    public int getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return -1;
    }

//    public BaseSurfacePlayer getMediaPlayer(){
//        if(player != null)
//            return player.getMediaPlayer();
//        return null;
//    }

    public long getCurrentPlayAllTime() {
        if (tempTime != 0 && playStatus == BaseApplication.FLAG_START) {
            allTime += (System.currentTimeMillis() - tempTime);
        }
        return allTime;
    }

    public void setAllTime(long time) {
        this.allTime = time;
    }

    /**
     * 获取当前播放状态
     *
     * @return
     */
    public int getCurrentPlayStatus() {
        return player.getCurrentPlayStatus();
    }

    public boolean isPlayCompletion() {
        return isPlayCompletion;
    }

    private void setPlayerListener() {
        player.setListener(new IGLPlayerListener() {
            @Override
            public void onPrepared(IGLPlayer player) {
                isError = false;
                mBottomContrallerView.seekbar.setEnabled(true);
                mBottomContrallerView.setPalyParam();
                mCurrentState = PlayerState.PREPARED;

                if((PreView!=null&&!PreView.isCanPlay())) {
                    pausePlay();
                }else {
                    startPlay();
                }
                if(mBottomContrallerView.currentPosition>0){
                    seekTo(mBottomContrallerView.currentPosition);
                }
                if(PreView!=null) {
                    PreView.onPrepared();
                    PreView.checkPlay();
                    PreView.hideLoading();
                    PreView.hideTopLayer();
                }
            }

            @Override
            public boolean onInfo(IGLPlayer player, int what, Object extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START
                        || what == IBfPlayerConstant.IOnInfoType.INFO_BUFFERING_START) {
                     PreView.showLoading();

                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END
                        || what == IBfPlayerConstant.IOnInfoType.INFO_BUFFERING_END) {
                    PreView.hideLoading();
                }

                return false;
            }

            @Override
            public void onBufferingUpdate(IGLPlayer player, int percent) {

            }

//            @Override
//            public void onBufferingUpdate(GLQiuPlayerView player, int percent) {
//                mBottomContrallerView.setSecondaryProgress(percent);
//            }

            @Override
            public void onCompletion(IGLPlayer player) {
                if (isError) {
                    return;
                }
                currentPosition = 0;
                mBottomContrallerView.currentPosition = 0;
                isPlayCompletion = true;
                playStatus = BaseApplication.FLAG_PAUSE;
                mCurrentState = PlayerState.COMPLETE;
                mBottomContrallerView.initPlayStatus();
                relesePlayView();
                PreView.onPlayComplete();
            }

            @Override
            public void onSeekComplete(IGLPlayer player) {
                PreView.hideLoading();
                if (isPauseView||getPlayStatus()==BaseApplication.FLAG_PAUSE||!PreView.isCanPlay()) {
                    pausePlay();
                } else {
                    if (!player.isPlaying()) {
                        player.start();
                    }
                    mBottomContrallerView.reStartHandler();
                }
            }

            @Override
            public boolean onError(IGLPlayer player, int what, int extra) {
                if (!isError) {
                    isError = true;
                }
                mCurrentState = PlayerState.ERROR;
                if(PreView!=null){
                    PreView.onPlayError();
                    PreView.doNetChanged(true);
                }
                return false;
            }

            @Override
            public void onVideoSizeChanged(IGLPlayer player, int width, int height) {

            }

            @Override
            public void onTimedText(IGLPlayer player, String text) {

            }



        });
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
                player.setSceneType(GLPanoView.SCENE_TYPE_SPHERE);
                break;
            case 3://180
                player.setSceneType(GLPanoView.SCENE_TYPE_HALF_SPHERE);
                break;
            case 4://立方体
                player.setSceneType(GLPanoView.SCENE_TYPE_SKYBOX);
                break;
        }
        switch (mode){
            case 1:
                player.setPlayType(GLPanoView.PLAY_TYPE_2D);
                break;
            case 2:
                player.setPlayType(GLPanoView.PLAY_TYPE_3D_TB);
                break;
            case 3:
                player.setPlayType(GLPanoView.PLAY_TYPE_3D_LR);
                break;
        }

    }

    public boolean isPlaying(){
        if(player!=null){
            return player.isPlaying();
        }
        return false;
    }


}
