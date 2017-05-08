package com.baofeng.mj.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.view.MediaGLSurfaceView;
import com.baofeng.mj.util.publicutil.DateUtil;
import com.baofeng.mj.util.publicutil.SubUtil;
import com.baofeng.mj.util.publicutil.TimerHandle;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.MojingSDK;
import com.storm.smart.core.IProxy;
import com.storm.smart.core.URlHandleProxyFactory;
import com.storm.smart.domain.P2pInfo;
import com.storm.smart.play.baseplayer.BaseSurfacePlayer;
import com.storm.smart.play.baseplayer.SubtitleListener;
import com.storm.smart.play.call.IBaofengPlayer;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.call.SimpleBaofengListener;
import com.storm.smart.play.utils.PlayCheckUtil;

import java.util.ArrayList;
import java.util.TimeZone;

public class LocalVideoPreviewActivity extends BaseActivity implements View.OnClickListener,
        SubtitleListener {
    private TextView tv_title;
    private LinearLayout ll_close;
    private MediaGLSurfaceView mediaGLSurfaceView;
    private LinearLayout ll_play;
    private ImageView iv_play;
    private SeekBar seekBar;
    private TextView tv_current_time;
    private TextView tv_total_time;
    private LinearLayout ll_vrplay;
    private BaseSurfacePlayer player;
    private boolean isPause = false;
    private String videoPath;
    private String videoName;
    private boolean isTracking;//true正在拖动seekbar
    private ArrayList<String> subtilesList = new ArrayList<String>();
    private ArrayList<Integer> decodeList = new ArrayList<Integer>();
    private int totalTime;//视频总时长
    private boolean showHourFormat;//显示小时的格式
    private TimerHandle timerHandle;
    private IProxy proxy;
    private boolean isReleasePlay = false;//正在释放播放
    private int rawOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        videoPath = getIntent().getStringExtra("videoPath");
        videoName = getIntent().getStringExtra("videoName");
        rawOffset = TimeZone.getDefault().getRawOffset();
        try {
            if (!MojingSDK.GetInitSDK()) {
                MojingSDK.Init(BaseApplication.INSTANCE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_local_video_preview);
        initView();
        initPlayer();
        timerHandle = new TimerHandle(new Runnable() {
            @Override
            public void run() {
                setPlayProgress();
            }
        });
        timerHandle.setTimes(1000);
        tv_title.setText(videoName);
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_close = (LinearLayout) findViewById(R.id.ll_close);
        mediaGLSurfaceView = (MediaGLSurfaceView) findViewById(R.id.mediaGLSurfaceView);
        ll_play = (LinearLayout) findViewById(R.id.ll_play);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        tv_total_time = (TextView) findViewById(R.id.tv_total_time);
        ll_vrplay = (LinearLayout) findViewById(R.id.ll_vrplay);
        ll_close.setOnClickListener(this);
        ll_play.setOnClickListener(this);
        ll_vrplay.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true; //开始拖动seekbar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false; //开始拖动seekbar
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });
        iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_pause);
    }

    private void initPlayer() {
        PlayCheckUtil.setSupportLeftEye(true);
        int pid;
        if (videoPath.startsWith("qstp:")) {
//            proxy = URlHandleProxyFactory.getIProxy(this, path);
//            p2pPrxy = new P2PProxy(this);
//            p2pPrxy.p2pInit();
            pid =  URlHandleProxyFactory.getIProxy(this, videoPath);
            proxy = URlHandleProxyFactory.getInstance();
            videoPath = P2pInfo.P2P_PLAY_SERVER_PATH;
        }
        else if(videoPath.startsWith("yun:")||videoPath.startsWith("yunlive:")){

             URlHandleProxyFactory.getIProxy(this, videoPath);
            proxy = URlHandleProxyFactory.getInstance();
            proxy.setcallback(new IProxy.UrlCallBack() {
                @Override
                public void mcallBack(String state) {
                    if (state != null && !"4".equals(state)) {
                        videoPath = state;
                    }
                    LocalVideoPreviewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mediaGLSurfaceView.setPath(videoPath);
                            mediaGLSurfaceView.createPlayer();
                            mediaGLSurfaceView.doSDKMedia();
                        }
                    });
                }
            });
            proxy.p2pStartPlay(videoPath);
            mediaGLSurfaceView.setAutoCreateMedia(false);
        }
        startPlayer();

    }
    private void startPlayer(){
//        mediaGLSurfaceView.setAutoCreateMedia(false);
        mediaGLSurfaceView.setDecodeType(IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS);
        mediaGLSurfaceView.setIsDouble(false);
        //        mediaGLSurfaceView.setIs3d(true);
        mediaGLSurfaceView.setChangePlayer(new MediaGLSurfaceView.ChangePlayer() {
            @Override
            public void changed() {
                player = mediaGLSurfaceView.getMediaPlayer();
            }
        });
        //        player=mediaGLSurfaceView.createPlayer();
        mediaGLSurfaceView.setMediaListener(simpleBaofengListener);
        mediaGLSurfaceView.setRote(270);
        mediaGLSurfaceView.setPath(videoPath);
        decodeList.add(IBfPlayerConstant.IBasePlayerType.TYPE_SYS);
        decodeList.add(IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS);
        decodeList.add(IBfPlayerConstant.IBasePlayerType.TYPE_SOFT);
        //        playerCoreh = PlayCorehUtil.getInstance(this);
        //        playerCoreh.setSubtitlehCallback(this);
        //        playerCoreh.OpenPlugSub(srtpath);
        // (srtpath));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_close) {//关闭当前界面
            finish();
        } else if (i == R.id.ll_play) {//播放
            if (player.isPlaying()) {
                player.pause();
                iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_play);
            }else{
                player.start();
                iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_pause);
            }
        } else if (i == R.id.ll_vrplay) {//vr播放
            showProgressDialog();//显示加载进度条
            releasePlay();//释放播放
            StartActivityHelper.playVideoWithLocal(this, videoName, videoPath, new StartActivityHelper.GotoPlayCallback() {
                @Override
                public void callback() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();//隐藏加载进度条
                            finish();
                        }
                    });
                }
            });
        }
    }

    private SimpleBaofengListener simpleBaofengListener = new SimpleBaofengListener() {
        @Override
        public void onPrepared(IBaofengPlayer bfPlayer) {
            mediaGLSurfaceView.setVideoSize(bfPlayer.getVideoWidth(), bfPlayer.getVideoHeight());
            bfPlayer.start();
            totalTime = player.getDuration();// 当视频加载完毕以后，才可以获取播放总时间
            showHourFormat = totalTime / 60000 > 60;// 判断是否大于60分钟，如果大于就显示小时。设置日期格式
            seekBar.setMax(player.getDuration());
            timerHandle.start();
            showStartTime();
        }

        @Override
        public void onCompletion(IBaofengPlayer bfPlayer) {
            finish();
        }

        @Override
        public void onError(IBaofengPlayer bfPlayer, int what) {
            mediaGLSurfaceView.errorToChangeSoft();
        }

        @Override
        public void onInfo(IBaofengPlayer bfPlayer, int what, final Object extra) {
            if (what == 1023)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!extra.equals(BaseSurfacePlayer.noSubInfo)) {
                            //                            player.setSubTitleIndex(0);
                            //                            testInnerSub();
                            subtilesList.clear();
                            SubUtil.parseInnerSubLists(extra, subtilesList);
                            SubUtil.addSubtitlePlug(videoPath, subtilesList);
                        }
                    }

                });


        }

        @Override
        public void onSeekToComplete(IBaofengPlayer bfPlayer) {
            bfPlayer.start();
            iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_pause);
        }

        @Override
        public boolean onSwitchPlayer(IBaofengPlayer bfPlayer, Object item, int playTime) {
            return false;
        }

        @Override
        public void onRawVideoDataUpdate() {

        }

        @Override
        public void onVideoInfo(int y, int u, int v, int stride, int width, int height, int
                aspect) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(player != null && !player.isPlaying()){
            player.start();
            iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_pause);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isReleasePlay){//没有在释放播放
            if(player != null && player.isPlaying()){
                player.pause();
                iv_play.setBackgroundResource(R.drawable.local_video_perview_icon_play);
            }
        }
    }

    @Override
    public void finish() {
        if(!isReleasePlay){//没有在释放播放
            releasePlay();//释放播放
        }
        super.finish();
    }

    /**
     * 释放播放
     */
    private void releasePlay(){
        isReleasePlay = true;
        mediaGLSurfaceView.finish();
        timerHandle.release();
        if(proxy!=null){
            proxy.p2pUninit();
        }
    }

    @Override
    public void OpenSubtitlehCallback(int type, int index, int msg_id) {

    }

    private void setPlayProgress() {
        if(isTracking) {
            return; //正在拖动seekbar，或者调用了onDestroy()方法，直接返回
        }
        if (player != null && player.isPlaying()) {
            int posion = player.getCurrentPosition();
            seekBar.setProgress(posion);
            showCurrentTime(posion);
        }
    }

    /**
     * 显示开始时间
     */
    public void showStartTime() {
        if (showHourFormat) {// 判断是否大于60分钟，如果大于就显示小时。设置日期格式
            tv_current_time.setText("00:00:00");//开始时间
            tv_total_time.setText("/"+ DateUtil.hour2String(totalTime - rawOffset));
        } else {
            tv_current_time.setText("00:00");//开始时间
            tv_total_time.setText("/" + DateUtil.min2String(totalTime));
        }
    }

    /**
     * @author liuchuanchi  @Date 2015-8-13 下午3:46:38
     * @description:{显示当前时间}
     *@param milliseconds
     */
    public void showCurrentTime(long milliseconds) {
        if (showHourFormat) {// 判断是否大于60分钟，如果大于就显示小时。设置日期格式
            tv_current_time.setText(DateUtil.hour2String(milliseconds - rawOffset));
        } else {
            tv_current_time.setText(DateUtil.min2String(milliseconds));
        }
    }
}
