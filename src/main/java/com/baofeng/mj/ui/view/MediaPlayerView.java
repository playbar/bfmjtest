package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.activity.MediaPlayerActivity;
import com.baofeng.mj.ui.online.utils.DataUtils;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by muyu on 2017/4/13.
 */
public class MediaPlayerView extends FrameLayout implements View.OnClickListener {
    private View rootView;
    private Context mContext;
    private RelativeLayout rl_parent;
    private LinearLayout ll_bottom_view;
    private SeekBar seekBar;// 播放控制条
    private VideoPlayerView mediaPlayer;// 播放视频
    private TextView tv_showtime;//显示视频当前播放时长
    private TextView tv_showtotaltime;//显示视频总时长
    private ImageView iv_play_or_stop;//播放或者暂停
    private ImageView fullscreen_btn;

    private int totalTime;//视频总时长
    private boolean isTracking;//true正在拖动seekbar
    private Timer mTimer;//定时器
    private TimerTask mTimerTask;//定时任务
    private Animation alphaAnimationIn;//淡入淡出动画
    private Animation alphaAnimationOut;//淡入淡出动画
    private boolean showTipViewClick;//控件的点击标记
    private boolean isDestroy = false;//是否调用了onDestroy()方法，true调用了
    private ImageView coverIV;
    private ProgressBar progressBar;
    private int playPosition = -1;


    private VideoPlayerManager<MetaData> mVideoPlayerManager;

    public void setVideoPlayerManager(VideoPlayerManager<MetaData> videoPlayerManager){
        this.mVideoPlayerManager = videoPlayerManager;
    }

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(isDestroy){
                return;//调用了onDestroy()方法，直接返回
            }
            ll_bottom_view.startAnimation(alphaAnimationOut);// 开启动画
            iv_play_or_stop.startAnimation(alphaAnimationOut);
        };
    };

    private String mVideoPath;
    public void setVideoPath(String path){
        this.mVideoPath = path;
    }

    private boolean isAutoPlay; //加载完是否自动播放
    public void setIsAutoPlay(boolean autoPlay){
        this.isAutoPlay = autoPlay;
    }

    public ImageView getCoverImage() {
        return coverIV;
    }

    /**
     * 是否有最大化功能
     * @param isMax
     */
    public void setMaximize(boolean isMax){
        if(isMax){
            fullscreen_btn.setVisibility(View.VISIBLE);
        }else {
            fullscreen_btn.setVisibility(View.GONE);
        }
    }

    public MediaPlayerView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public MediaPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_surfaceview_play, this);
        initOption();
        findView();
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());// 设置拖动监听
        updateSeekBar();//更新seekBar
//        showTipView();//显示提示View
    }

    /**
     * 初始化操作
     */
    private void initOption(){
        alphaAnimationIn = AnimationUtils.loadAnimation(mContext, R.anim.pubblico_alpha_anim_in);
        alphaAnimationOut = AnimationUtils.loadAnimation(mContext, R.anim.pubblico_alpha_anim_out);
        alphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {// 添加动画监听器
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isDestroy) {
                    return;//调用了onDestroy()方法，直接返回
                }
                ll_bottom_view.setVisibility(View.GONE);// 隐藏控件
                iv_play_or_stop.setVisibility(View.GONE);
                showTipViewClick = false;//置为false，可以再一次点击
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 查找控件
     */
    private void findView(){
        rl_parent = (RelativeLayout) rootView.findViewById(R.id.rl_parent);

        coverIV = (ImageView) rootView.findViewById(R.id.video_game_mediaplay_cover);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mediaPlayer = (VideoPlayerView) rootView.findViewById(R.id.video_game_mediaplay);
        ll_bottom_view = (LinearLayout) rootView.findViewById(R.id.ll_bottom_view);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekbar);
        tv_showtime = (TextView) rootView.findViewById(R.id.tv_showtime);
        tv_showtotaltime = (TextView) rootView.findViewById(R.id.tv_showtotaltime);
        iv_play_or_stop = (ImageView) rootView.findViewById(R.id.iv_play_or_stop);
        iv_play_or_stop.setOnClickListener(this);
        rl_parent.setOnClickListener(this);

        fullscreen_btn = (ImageView) rootView.findViewById(R.id.fullscreen_btn);
        fullscreen_btn.setOnClickListener(this);

        mediaPlayer.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
                coverIV.setVisibility(View.GONE);
                onMediaPrepared();
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
                startPlayUI();
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
                startPlayUI();
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {
                super.onBufferingUpdateMainThread(percent);
                // percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
                if (percent < 100) {
                    seekBar.setSecondaryProgress(percent * totalTime / 100);
                }
            }

            @Override
            public void onErrorMainThread(int what, int extra) {
                super.onErrorMainThread(what, extra);
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_play_or_stop:// 播放或者暂停
                playOrStop();
                break;
            case R.id.rl_parent://显示提示View
                showTipView();
                break;
            case R.id.fullscreen_btn:
                setPlayPosition();
                startPlayUI();
                Intent intent = new Intent(mContext, MediaPlayerActivity.class);
                intent.putExtra("play_url",mVideoPath);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 显示提示View
     */
    private void showTipView(){
        sendMessage();//发送消息
        if(showTipViewClick){
            return;//如果点击过，就不用再点击，直接返回
        }
        showTipViewClick = true;
        ll_bottom_view.setVisibility(View.VISIBLE);// 显示控件
        ll_bottom_view.startAnimation(alphaAnimationIn);// 开启动画
        iv_play_or_stop.setVisibility(View.VISIBLE);
        iv_play_or_stop.startAnimation(alphaAnimationIn);// 开启动画
    }

    /**
     * 发送消息
     */
    private void sendMessage(){
        if(handler!=null){
            handler.removeMessages(0);//取消msg.what=0的消息的执行
            handler.sendEmptyMessageDelayed(0, 3000);//延迟3秒发送msg.what=0的消息
        }
    }

    /**
     * 移除消息
     */
    private void removeMessage(){
        if(handler!=null){
            handler.removeMessages(0);//取消msg.what=0的消息的执行
        }
    }

    private void updateSeekBar(){
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(isTracking || isDestroy) {
                    return; //正在拖动seekbar，或者调用了onDestroy()方法，直接返回
                }
                try {
                    //mediaPlayer不为空且处于正在播放状态时，使进度条滚动
                    if (mediaPlayer != null && mediaPlayer.mMediaPlayer!= null && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 100);
    }

    /**
     * mediaPlayer设置到某个位置后开始播放
     */
    private void mediaPlayerSeekTo(){
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (BaseApplication.playPosition >= 0) {
            if (mediaPlayer != null && mediaPlayer.mMediaPlayer != null) {
                mediaPlayer.seekTo(BaseApplication.playPosition);
            }
            BaseApplication.playPosition = -1;
        }
    }

    private boolean isPrepareing;
    /**
     * 视频加载完毕监听
     */
    public void onMediaPrepared() {
        if(isDestroy){
            return;//调用了onDestroy()方法，直接返回
        }
        isPrepareing = false;
        progressBar.setVisibility(View.GONE);
        iv_play_or_stop.setVisibility(View.VISIBLE);
        if(mediaPlayer != null && mediaPlayer.mMediaPlayer != null) {
            totalTime = mediaPlayer.getDuration();// 当视频加载完毕以后，才可以获取播放总时间
        }
        seekBar.setMax(totalTime);//seekbar最大值
        showStartTime();//显示开始时间
        mediaPlayerSeekTo();//mediaPlayer设置到某个位置
        iv_play_or_stop.setEnabled(true);
        iv_play_or_stop.setBackgroundResource(R.drawable.recommend_icon_suspended);//显示暂停按钮
        iv_play_or_stop.setVisibility(View.GONE);
    }

    private void playOrStop(){
        sendMessage();//发送消息
        if (mediaPlayer != null) {
            if(mediaPlayer.mMediaPlayer != null){
                if (mediaPlayer.isPlaying()) {// 正在播放
                    iv_play_or_stop.setBackgroundResource(R.drawable.recommend_icon_play);//显示播放按钮
                    setPlayPosition();
                    mediaPlayer.pause();
                } else {
                    iv_play_or_stop.setBackgroundResource(R.drawable.recommend_icon_suspended);//显示暂停按钮
                    mediaPlayerSeekTo();//mediaPlayer设置到某个位置
                    MediaPlayerWrapper.State current = mediaPlayer.getCurrentState();
                    if(current == MediaPlayerWrapper.State.STOPPED
                            || current == MediaPlayerWrapper.State.PLAYBACK_COMPLETED
                            || current == MediaPlayerWrapper.State.PREPARED
                            || current == MediaPlayerWrapper.State.PAUSED) {
                        mediaPlayer.start();
                        coverIV.setVisibility(View.GONE);
                    } else {
                        beginPlay();
                    }
                }
            } else {
                beginPlay();
            }
        }
    }

    //准备播放样式
    private void startPlayUI(){
        coverIV.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        iv_play_or_stop.setVisibility(View.VISIBLE);
        iv_play_or_stop.setBackgroundResource(R.drawable.recommend_icon_play);//显示播放按钮
    }

    //点击开始播放，到MediaPlayer准备好，按钮不可反复点击
    private void beginPlay(){
        isPrepareing = true;
        coverIV.setVisibility(View.GONE);
        iv_play_or_stop.setEnabled(false);
        iv_play_or_stop.setVisibility(View.GONE);
        mVideoPlayerManager.playNewVideo(null, mediaPlayer, mVideoPath);
    }

    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (progress >= 0) {
//                if (fromUser) {// 如果是用户手动拖动控件，则设置视频跳转
//                    mediaPlayer.seekTo(progress);
//                }
            showCurrentTime(progress);//显示当前时间
//            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isTracking=true; //开始拖动seekbar
            removeMessage();//移除消息
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            isTracking=false;
            sendMessage();//发送消息
            if (mediaPlayer != null && mediaPlayer.mMediaPlayer != null) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    }

    public void setPlayPosition(){ //记录当前播放的位置
        if(mediaPlayer != null && mediaPlayer.mMediaPlayer != null) {
            BaseApplication.playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSeekBar();//更新seekBar
        startPlayUI();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        setPlayPosition();
        onHidePlayerView();
//        startPlayUI();
    }

    //当看不见播放器View时
    public void onHidePlayerView(){
        if (mTimer != null) {//移除定时器
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {//移除定时任务
            mTimerTask.cancel();
            mTimerTask = null;
        }
        removeMessage();//移除消息
    }

    public void onDestroyView(){
        isDestroy = true;//调用了onDestroy()方法
//        BaseApplication.playPosition = -1;//重置播放的位置
        onHidePlayerView();
    }

    public void showStartTime() {
        tv_showtime.setText("00:00:00");//开始时间
        tv_showtotaltime.setText(DataUtils.showTimeCount(totalTime));
    }

    public void showCurrentTime(long milliseconds) {
        tv_showtime.setText(DataUtils.showTimeCount(milliseconds));
    }
}
