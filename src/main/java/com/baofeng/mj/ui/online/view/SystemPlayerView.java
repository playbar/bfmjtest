package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.bfmj.viewcore.interfaces.IGLPlayer;
import com.bfmj.viewcore.interfaces.IGLPlayerListener;
import com.bfmj.viewcore.interfaces.IGLViewClickListener;
import com.bfmj.viewcore.view.GLRootView;
import com.storm.smart.play.baseplayer.BaseSurfacePlayer;
import com.storm.smart.play.call.IBfPlayerConstant;

/**
 * Created by panxin on 2016/7/6.
 */
public class SystemPlayerView extends RelativeLayout {

    public GLRootView rootView;
    private GLMovieBasePlayer player;
    private Context mContext;

    private BottomContrallerView mBottomContrallerView;
    private String videoPath;
    private int currentPosition = -1;
    private boolean isDouble = true;
    public boolean isPlayCompletion = false;
    private long tempTime;
    private long allTime;
    private boolean isError = false;
    public boolean isPauseView = false;//是否切出播放页面

    private int playStatus = BaseApplication.FLAG_PAUSE;

    private VideoPlayerPreView PreView;
    public PlayerState mCurrentState = PlayerState.IDEL;
    public enum PlayerState{  //记录播放状态  目前主要用在 播放还未加载成功前断开网络，再次连上网后需要根据State是否为PrePared的来判断重新创建player播放
       IDEL, PREPARED,COMPLETE,ERROR
    }

    public SystemPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;


        rootView = new GLRootView(context);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;
        rootView.setLayoutParams(lp);

        if(MediaHelp.mPlayer == null) {
             MediaHelp.createPlayer(getContext());
         }
        player = new GLMovieBasePlayer(mContext, rootView);
        player.setLayoutParams( 2400, 2400);
//        player.setX(-500);
//        player.setX(-520);
//        player.setDepth(2);
        player.setVisible(true);
        rootView.addView(player);
        addView(rootView);
        setPlayerListener();

    }

//    public void showPlayer(){
//        if(rootView!=null){
//            rootView.setVisibility(VISIBLE);
//        }
//        if(player!=null){
//            player.setVisible(true);
//        }
//    }
    public void setOnGLClickListener(IGLViewClickListener mIGLViewClickListener) {
        rootView.setOnGLClickListener(mIGLViewClickListener);
    }

    public void initHeadView() {
        rootView.initHeadView();
    }

    /**
     * 设置双屏
     *
     * @param isDouble
     */
    public void setDoubleScreen(boolean isDouble) {
        this.isDouble = isDouble;
        rootView.setDoubleScreen(isDouble);
    }


    public void setVideoPath(String path) {
        this.videoPath = path;
//        if (!checkNetConnect()) {
//            return;
//        }
        mCurrentState = PlayerState.IDEL;
        isPlayCompletion =false;
        if(PreView!=null) {
            PreView.showLoading();
        }
        if(player!=null) {
            player.setVideoPath(path);
        }
    }

    public void setPlayMode(int mode) {
        if(player!=null) {
            player.setPlayMode(mode);
        }
    }

    public void setVideoPlayerPreView(VideoPlayerPreView PreView){
        this.PreView = PreView;
    }


    public void reSetPlay(){
        MediaHelp.release();
        MediaHelp.mPlayer = null;
        MediaHelp.createPlayer(getContext());

        playStatus = BaseApplication.FLAG_START;
        mCurrentState = PlayerState.IDEL;
        mBottomContrallerView.reStartHandler();
        mBottomContrallerView.initPlayStatus();

    }


    public void relesePlayView(){
        MediaHelp.release();
        MediaHelp.mPlayer = null;
        mCurrentState = PlayerState.IDEL;

    }

    public void setBottomView(BottomContrallerView mBottomContrallerView) {
        this.mBottomContrallerView = mBottomContrallerView;
        mBottomContrallerView.seekbar.setEnabled(false);
    }



    public void setCurrentPosition(int position) {
        currentPosition = position;
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
        if (isPlayCompletion) {
            setVideoPath(videoPath);
        } else {
            if (player != null) {
                tempTime = System.currentTimeMillis();
//                if (player.isPlaying()) {
//                    return;
//                }
                player.start();
            }
        }

    }

    public void pausePlay(){
        playStatus = BaseApplication.FLAG_PAUSE;
        pause2Play();
        mBottomContrallerView.initPlayStatus();
    }

    public void pause2Play() {

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
        if(PreView!=null){
            PreView.showLoading();
        }
        if (MediaHelp.mPlayer != null) {
            try {
                MediaHelp.mPlayer.seekTo(position);
            } catch (Exception e) {
                e.printStackTrace();
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
//        if (rootView != null) {
//            rootView.onPause();
//        }
    }

    public void destroyView() {
        mBottomContrallerView.releaseThread();

        relesePlayView();
        if(player!=null){
            player.releasePlay();
            player.release();
        }
        MediaHelp.decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
        MediaHelp.mState = MediaHelp.STATE_IDLE;
        mCurrentState = PlayerState.IDEL;
        if (rootView != null) {
            rootView.removeView(player);
            rootView.onDestroy();
        }

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

    public BaseSurfacePlayer getMediaPlayer() {
        if (player != null)
            return player.getMediaPlayer();
        return null;
    }

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
            public boolean onError(IGLPlayer player, int what, int extra) {
                mCurrentState = PlayerState.ERROR;
                if(PreView!=null){
                    PreView.doNetChanged(true);
                    PreView.onPlayError();
                }
                return false;
            }

            @Override
            public void onVideoSizeChanged(IGLPlayer player, int width, int height) {

            }

            @Override
            public void onTimedText(IGLPlayer player, String text) {

            }

            @Override
            public void onPrepared(IGLPlayer player) {
                isError = false;
                mBottomContrallerView.seekbar.setEnabled(true);
                mBottomContrallerView.setPalyParam();
                if(PreView!=null&&!PreView.isCanPlay()) {
                   pausePlay();
                }else {
                    startPlay();
                }
                mCurrentState = PlayerState.PREPARED;
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
                    if(PreView!=null) {
                        PreView.showLoading();
                    }

                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END
                        || what == IBfPlayerConstant.IOnInfoType.INFO_BUFFERING_END) {
                    if(PreView!=null) {
                        PreView.hideLoading();
                    }
                }
                return false;
            }

            @Override
            public void onBufferingUpdate(IGLPlayer player, int percent) {

            }


            @Override
            public void onCompletion(IGLPlayer player) {
                if (isError) {
                    return;
                }
                mBottomContrallerView.currentPosition = 0;
                currentPosition = 0;
                mCurrentState = PlayerState.COMPLETE;
                if(PreView!=null) {
                    PreView.onPlayComplete();
                }

            }

            @Override
            public void onSeekComplete(IGLPlayer player) {
                if(PreView!=null) {
                    PreView.hideLoading();
                }
                if (isPauseView||getPlayStatus()==BaseApplication.FLAG_PAUSE||(PreView!=null&&!PreView.isCanPlay())) {
                    pausePlay();
                } else {
                   startPlay();
                    mBottomContrallerView.reStartHandler();
                }

            }


        });


    }


    public void setPlayStatus(int status) {
        playStatus = status;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public boolean isPlaying(){
        if(player!=null){
            return player.isPlaying();
        }
        return false;
    }

}