package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.mediaplayerbusiness.PlayerBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.videoplayer.VideoPlayerManager;
import com.baofeng.mj.ui.activity.BasePlayerActivity;
import com.baofeng.mj.ui.online.utils.DataUtils;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mojing.input.base.MojingKeyCode;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang
 * 播放底部控制栏
 */
public class BottomContrallerView extends RelativeLayout implements BasePlayerActivity.IPlayerMojingInputCallBack {
    //播放控制栏延迟隐藏时间
    public static  final int DELAY_DISMISS_TIME = 5*1000;
    private Context mContext;
    private ImageView imageview_play;
    private boolean flag = true;
    public boolean canThreadHandler = true;//控制进度条刷新
    private boolean releaseThread = false;//控制刷新线程
    public SeekBar seekbar;//播放进度条
    private TextView textview_progress_str, textview_progress_all,textview_progress_str1;
    private SystemPlayerView mSystemPlayerView;
    private PanoramVideoPlayerView mPanoramVideoPlayerView;
    private PanoramPlayerPreView mPanoramPlayerPreView; //包含PanoramVideoPlayerView的父View
    private VideoPlayerPreView mVideoPlayerPreView; //包含mSystemPlayerView的父View

    private TextView player_definition_btn;//清晰度按钮
    private TextView player_select_btn ;//选集按钮
    private ImageView fullScreenBtn; //全屏播放按钮
    int duration = 0;
    private boolean seeking_flag =  false; //标志当前是否正在seek
    public int currentPosition = 0; //当前播放进度
    public int initPosition = 0; //从历史中读到的上次播放进度
    private long mKeyLongPressTime = -1;//记录遥控器长按时间

    public BottomContrallerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void setSystemPlayerView(SystemPlayerView mSystemPlayerView) {
        this.mSystemPlayerView = mSystemPlayerView;
    }

    public void setPanoramVideoPlayerView(PanoramVideoPlayerView mPanoramVideoPlayerView) {
        this.mPanoramVideoPlayerView = mPanoramVideoPlayerView;
    }
    public void setPanoramPlayerPreView(PanoramPlayerPreView mPanoramVideoPlayerView) {
        this.mPanoramPlayerPreView = mPanoramVideoPlayerView;
    }


    public void setVidoPlayerPreView(VideoPlayerPreView mPlayerPreView){
        this.mVideoPlayerPreView = mPlayerPreView;
    }

    private VideoSelectHdView mSelectHDView;
    public void setSelectHDView(VideoSelectHdView videoSelectHdView){
        this.mSelectHDView = videoSelectHdView;
    }

    View bottomview;
    private void initView() {
          bottomview = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_contraller, null);
        imageview_play = (ImageView) bottomview.findViewById(R.id.imageview_play);
        seekbar = (SeekBar) bottomview.findViewById(R.id.seekbar);
        textview_progress_str = (TextView) bottomview.findViewById(R.id.textview_progress_str);
        textview_progress_all = (TextView) bottomview.findViewById(R.id.textview_progress_all);
        player_definition_btn = (TextView) bottomview.findViewById(R.id.video_player_definition_text);
        player_select_btn = (TextView) bottomview.findViewById(R.id.video_player_select_text);
        fullScreenBtn = (ImageView) bottomview.findViewById(R.id.fullscreen_btn);
        textview_progress_str1 =  (TextView)bottomview.findViewById(R.id.textview_progress_str1);

        setCilckListener();
        addView(bottomview);
        duration = 0;
        setLandScreen(false,false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if(mContext instanceof BasePlayerActivity){
            ((BasePlayerActivity)mContext).setPlayerMojingInputCallBack(this);
        }
    }


    /**
     * 初始化播放状态
     */
    public void initPlayStatus() {
        switch (getPlayStatus()) {
            case BaseApplication.FLAG_START:
                flag = false;
                imageview_play.setImageResource(R.drawable.toolbar_icon_suspended);
                break;
            case BaseApplication.FLAG_PAUSE:
                flag = true;
                imageview_play.setImageResource(R.drawable.toolbar_icon_play);
                break;
        }

    }

    private int getPlayStatus() {
        if (mPanoramVideoPlayerView != null) {
            return mPanoramVideoPlayerView.getPlayStatus();
        } else if (mSystemPlayerView != null) {
            return mSystemPlayerView.getPlayStatus();
        }
        return 0;
    }

    private void setCilckListener() {
        imageview_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doChangePlay();
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                canThreadHandler = false;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              doSeekTo(seekBar.getProgress());
            }
        });

        player_definition_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectHDView!=null){
                   boolean ishow =  mSelectHDView.showDdfinitionView(player_definition_btn);
                    if(ishow){

                        if(mVideoPlayerPreView!=null&&mVideoPlayerPreView.mPlaySelectWindow!=null&&mVideoPlayerPreView.mPlaySelectWindow.getVisibility()==VISIBLE){
                            mVideoPlayerPreView.mPlaySelectWindow.setVisibility(GONE);
                        }
                    }
                }
            }
        });

        player_select_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mVideoPlayerPreView!=null&&mVideoPlayerPreView.mPlaySelectWindow!=null){
                   if(mVideoPlayerPreView.mPlaySelectWindow.getVisibility()==VISIBLE){
                       mVideoPlayerPreView.mPlaySelectWindow.setVisibility(GONE);
                   }else {
                       mVideoPlayerPreView.mPlaySelectWindow.setVisibility(VISIBLE);
                   }
               }
            }
        });
        fullScreenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoPlayerPreView!=null){
                    mVideoPlayerPreView.changePlayerScreen(VideoPlayerPreView.PlayerScreenMode.fullscreen,false);
                }
                if(mPanoramPlayerPreView!=null){
                    mPanoramPlayerPreView.changePlayerScreen(PanoramPlayerPreView.PlayerScreenMode.fullscreen,false);
                }
                reportClick();
            }
        });
    }

    private void doSeekTo(int progress){
        if (mSystemPlayerView != null) {
            if(mVideoPlayerPreView!=null&&mSystemPlayerView.isPlayCompletion()){
                mVideoPlayerPreView.rePlay();
                return;
            }
            if (progress >= 0) {
                canThreadHandler = false;//seek时停止刷新进度
                mSystemPlayerView.seekTo(progress);
            }
            mSystemPlayerView.setPlayStatus(BaseApplication.FLAG_START);
        } else if (mPanoramVideoPlayerView != null) {
            if(mPanoramVideoPlayerView.isPlayCompletion()){
                mPanoramVideoPlayerView.startPlay();
                return;
            }
            if (progress >= 0) {
                canThreadHandler = false;//seek时停止刷新进度
                mPanoramVideoPlayerView.seekTo(progress);
            }
            mPanoramVideoPlayerView.setPlayStatus(BaseApplication.FLAG_START);
        }

        initPlayStatus();
    }

    /**
     * 点击播放或暂停事件处理
     */
    private void doChangePlay(){
        if (flag) {
            if (mSystemPlayerView != null) {
                if(mVideoPlayerPreView.isDialogVisiable()){ //如果当前
                    mVideoPlayerPreView.doExceptionRePlay();
                }else {
                    if(mVideoPlayerPreView!=null&&mSystemPlayerView.isPlayCompletion()){
                        mVideoPlayerPreView.rePlay();
                    }else {
                        mSystemPlayerView.startPlay();
                    }
                }
            } else if (mPanoramVideoPlayerView != null) {
                if(mPanoramPlayerPreView.isDialogVisiable()){ //如果当前
                    mPanoramPlayerPreView.doExceptionRePlay();
                }else {
                    mPanoramVideoPlayerView.startPlay();
                }
            }
        } else {
            if (mSystemPlayerView != null) {
                mSystemPlayerView.pausePlay();
            } else if (mPanoramVideoPlayerView != null) {
                mPanoramVideoPlayerView.pausePlay();
            }
        }

    }

    /**
     * 设置清晰度上下箭头
     * @param isup
     */
    public void setDefinitonBtnUP(boolean isup){
        if(isup){
            player_definition_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nav_icon_arrow_up, 0);
        }else {
            player_definition_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nav_icon_arrow_down, 0);
        }
    }

    /**
     * 更新清晰度选中的类型
     * @param hdType
     */
    public void setDefinitionText(String hdType){
        if(player_definition_btn ==null){
           return;
        }
        if(TextUtils.isEmpty(hdType)){
            return;
        }
        if(!(hdType.endsWith("k")||hdType.endsWith("K"))) {
            player_definition_btn.setText(hdType+"P");
        }else {
            player_definition_btn.setText(hdType);
        }
    }

    Handler mMJProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mSystemPlayerView != null) {

                if (MediaHelp.mPlayer == null||mSystemPlayerView.isPlayCompletion()) {
                    seekbar.setProgress(0);
                    updateProgressText(0, 0);
                    releaseThread();
                    currentPosition = MediaHelp.mPlayer == null?initPosition:0;
                    return;
                }
                if (!MediaHelp.mPlayer.isPlaying()) {
                    return;
                }
                int time = mSystemPlayerView.getCurrentPosition();
                currentPosition = time;
                if (time > 0) {
                    seekbar.setProgress(time);
                    updateProgressText(time, mSystemPlayerView.getDuration());
                }
            } else if (mPanoramVideoPlayerView != null) {


                if (MediaHelp.mPlayer == null||mPanoramVideoPlayerView.isPlayCompletion()) {
                    seekbar.setProgress(0);
                    updateProgressText(0,0);
                    releaseThread();
                    currentPosition = MediaHelp.mPlayer == null?initPosition:0;
                    return;
                }
                if (!MediaHelp.mPlayer.isPlaying()) {
                    return;
                }
                int time = mPanoramVideoPlayerView.getCurrentPosition();
                currentPosition = time;
                if (time > 0) {
                    seekbar.setProgress(time);
                    updateProgressText(time, mPanoramVideoPlayerView.getDuration());
                }
            }
        }
    };

    /**
     * 刷新进度时间
     * @param curtime 当前播放时间
     * @param duration 总时长
     */
    private void updateProgressText(int curtime,int duration){
        textview_progress_str1.setText(DataUtils.showTimeCount(curtime));
        textview_progress_str.setText(DataUtils.showTimeCount(curtime)+"/" );
        textview_progress_all.setText(DataUtils.showTimeCount(duration));
    }


    public void releaseThread() {
        releaseThread = true;
        canThreadHandler = false;
    }


    public void reStartHandler() {
        if(seeking_flag)
            return;
        canThreadHandler = true;
    }


    /**
     * 设置播放参数，并开启一个线程读取进度
     */
    public void setPalyParam() {
        releaseThread = false;
        canThreadHandler = true;

        if (mSystemPlayerView != null) {
            duration = mSystemPlayerView.getDuration();
        } else if (mPanoramVideoPlayerView != null) {
            duration = mPanoramVideoPlayerView.getDuration();
        }
        seekbar.setMax(duration);
        updateProgressText(0,duration);
        startDelayThread();
        initPlayStatus();
    }

    DelayThread delayThread;
    public void startDelayThread(){
        if(delayThread!=null){
            delayThread.interrupt();
            delayThread = null;
        }
        delayThread = new DelayThread(1000);
        delayThread.start();
    }

    public void stopDelayThread(){
        if(delayThread!=null){
            delayThread.interrupt();
            delayThread = null;
        }
    }

    /**
     * 遥控器mojingkeyLongPress事件回调
     * @param s
     * @param downKeyValue
     */
    @Override
    public void onMojingKeyUp(String s, int downKeyValue) {
        if (downKeyValue == PlayerBusiness.getInstance().KEY_BLUETOOTH_OK
                || downKeyValue == PlayerBusiness.getInstance().KEY_JOYSTICK_OK) {
            delayHide();
        }else if (downKeyValue ==  PlayerBusiness.getInstance().KEY_BLUETOOTH_LEFT) {
            seeking_flag = false;
            cancelSeekProgress();
            rewind();
            delayHide();
        } else if (downKeyValue ==  PlayerBusiness.getInstance().KEY_BLUETOOTH_RIGHT) {
            seeking_flag = false;
            cancelSeekProgress();
            fastForward();
            delayHide();
        }

        mKeyLongPressTime = -1;

    }

    /**
     * 遥控器MojingKeyDown事件回调
     * @param s
     * @param i
     */
    @Override
    public void onMojingKeyDown(String s, int i) {
        if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_OK
                || i == PlayerBusiness.getInstance().KEY_JOYSTICK_OK) {
            showContrallerView();
            doChangePlay();
        }  else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_LEFT) {
            mKeyLongPressTime = System.currentTimeMillis();
            canThreadHandler = false;
            showContrallerView();
            changeSeekProgress(true);
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_RIGHT) {
            mKeyLongPressTime = System.currentTimeMillis();
            canThreadHandler = false;
            showContrallerView();
            changeSeekProgress(false);
        }
    }

    public void onDestroy(){
        stopDelayThread();
        cancelSeekProgress();
    }

    /**
     * 取消seek时的实时进度刷新
     */
    private synchronized void cancelSeekProgress(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }
    Timer timer;

    /**
     * 长按摇杆快进时 实时刷新进度
     * @param rewind
     */
    private synchronized void changeSeekProgress(final boolean rewind){
        canThreadHandler = false;
        seeking_flag = true;
        cancelSeekProgress();
         timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(seeking_flag){
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int curpos = currentPosition;
                            int rateProgress = getRateProgress();
                            if(rewind) {
                                if (curpos > rateProgress) {
                                    curpos -= rateProgress;
                                } else {
                                    curpos = 0;
                                }
                            }else {
                                if (curpos < seekbar.getMax() - rateProgress) {
                                    curpos += rateProgress;//
                                } else {
                                    curpos =  seekbar.getMax() - 5000;
                                }
                            }

                            seekbar.setProgress(curpos);
                            updateProgressText(curpos, duration);
                        }
                    });


                }else {
                    timer.cancel();
                    timer = null;
                }
            }
        },0,500);

    }

    /**
     * 显示控制栏
     */
    private void showContrallerView(){
        if(mVideoPlayerPreView!=null){
            mVideoPlayerPreView.cancelDelay();
            if(this.getVisibility()==GONE){
                mVideoPlayerPreView.hideContrallerView();
            }
        }else if(mPanoramPlayerPreView!=null){
            mPanoramPlayerPreView.cancelDelay();
            if(this.getVisibility()==GONE){
                mPanoramPlayerPreView.hideContrallerView();
            }
        }
    }

    private void delayHide(){
        if(mVideoPlayerPreView!=null){
            mVideoPlayerPreView.setDelayVisiable();
        }else if(mPanoramPlayerPreView!=null){
            mPanoramPlayerPreView.setDelayVisiable();
        }
    }


    /***
     * 快进
     *
     */
    int curpos = 0;
    public synchronized void fastForward() {
        if (seekbar == null) {
            return;
        }
        canThreadHandler = false;
         curpos = currentPosition;
        if (curpos < 0) {
            canThreadHandler = true;
            return;
        }
        int totalTime = seekbar.getMax();
        int rateProgress = getRateProgress();
        if (curpos < totalTime - rateProgress) {
            curpos += rateProgress;//
        } else {
            curpos = totalTime - 5000;
        }
        seekbar.setProgress(curpos);
        updateProgressText(curpos,duration);
        if(mVideoPlayerPreView!=null){
            mVideoPlayerPreView.showLoading();
        }else if(mPanoramPlayerPreView!=null){
            mPanoramPlayerPreView.showLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doSeekTo(curpos);
            }
        },1000);

    }

    /***
     * 快退
     *
     */
    public synchronized void rewind() {
        if (seekbar == null) {
            return;
        }
        canThreadHandler = false;
          curpos = currentPosition;
        if (curpos < 0) {
            canThreadHandler = true;
            return;
        }
        int rateProgress = getRateProgress();
        if (curpos > rateProgress) {
            curpos -= rateProgress;
        } else {
            curpos = 0;
        }
        seekbar.setProgress(curpos);
        updateProgressText(curpos,duration);
        if(mVideoPlayerPreView!=null){
            mVideoPlayerPreView.showLoading();
        }else if(mPanoramPlayerPreView!=null){
            mPanoramPlayerPreView.showLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doSeekTo(curpos);
            }
        },1000);


    }

    /**
     * 快进或快退的进度
     * @return
     */
    private synchronized int getRateProgress() {
        double rate = 5.0f;
        if (mKeyLongPressTime > 0) {  //小于等于3s时 每0.5s跳5s的进度
            long time = (System.currentTimeMillis() - mKeyLongPressTime)/1000;

             if (time>0&&time <= 3) {
                rate = 5*2.0f*time;
            } else if (time > 3 ) { //大于3s 每秒跳进度，按倍率调 为前一秒的1.5倍
                rate = 5f*Math.pow(1.5,time-3)*time;
            }
        }
        return (int)(rate*1000);
    }

    /**
     * 刷新播放进度
     */
    class DelayThread extends Thread {
        int milliseconds;

        public DelayThread(int i) {
            milliseconds = i;
        }

        public void run() {
            while (true) {
                try {
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (releaseThread) {
                    break;
                }
                if (canThreadHandler) {
                    mMJProgressHandler.sendEmptyMessage(0);
                }

            }
        }
    }

    private int bg_land = R.color.player_title_bg;
    private int bg_prot = R.color.player_bottom_prot_bg;

    /**
     * @param isLand  true 标示为横屏, false 标示为竖屏
     * @param isDouble  true 双屏  false 单屏
     */
    public void setLandScreen(boolean isLand,boolean isDouble){
            if(bottomview!=null){
                bottomview.setBackgroundResource(isLand?bg_land:bg_prot);
            }
        if(fullScreenBtn!=null){
            fullScreenBtn.setVisibility(isLand?GONE:VISIBLE);
        }
//        if(isDouble==false){
//            textview_progress_str1.setVisibility(VISIBLE);
//            textview_progress_str.setVisibility(GONE);
//        }else {
//            textview_progress_str.setVisibility(VISIBLE);
//            textview_progress_str1.setVisibility(GONE);
//        }

        setDoubleScreen(isDouble);

    }

    /**
     * 设置播放是否双屏状态。单屏状态不显示选集和清晰度
     * @param isDouble
     */
    private void  setDoubleScreen(boolean isDouble){
        //全景没有选集
        if(mPanoramPlayerPreView!=null) {
            if (player_definition_btn != null) {
                player_definition_btn.setVisibility(isDouble ? VISIBLE : GONE);
            }
        }else if(mVideoPlayerPreView!=null) {
            if (player_definition_btn != null) {
                player_definition_btn.setVisibility(isDouble ? VISIBLE : GONE);
            }
            if (player_select_btn != null) {
                player_select_btn.setVisibility(isDouble ? VISIBLE : GONE);
            }
        }
    }

    private void reportClick(){

        HashMap<String,String> params = new HashMap<>();
        params.put("etype","click");
        params.put("tpos","1");
        params.put("clicktype","fullscreen");
        params.put("pagetype","detail");
        ReportBusiness.getInstance().reportClick(params);
    }
}
