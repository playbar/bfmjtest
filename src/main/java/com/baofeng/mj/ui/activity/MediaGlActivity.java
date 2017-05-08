package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.bean.VRModelBean;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.mediaplayerbusiness.PlayerBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.sqlitebusiness.SqliteManager;
import com.baofeng.mj.business.videoplayer.MojingSDKHandler;
import com.baofeng.mj.business.videoplayer.VideoPlayerManager;
import com.baofeng.mj.business.videoplayer.vrSurface.VrModel;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.ui.online.view.PlayerCenterLine;
import com.baofeng.mj.ui.popwindows.PlayStrategyPopupWindow;
import com.baofeng.mj.ui.view.MojingVideoGvrView;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.publicutil.MD5Util;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.TimeUtil;
import com.baofeng.mj.util.publicutil.TimerHandle;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.systemutil.AudioManagerUtil;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.threadutil.SqliteProxy;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.MojingSurfaceView;
import com.google.gson.Gson;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.umeng.analytics.ReportPolicy;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MediaGlActivity extends BaseStickActivity implements View.OnClickListener, Handler.Callback {

    private static final int LIST_ITEM_HEIGHT_DIP = 35;

    private static final int SHOW_PLAY_STRATEGY_MAX = 8;
    //退出播放
    private static final int WHAT_QUIT_PLAYER = 1000;
    //隐藏控制条
    private static final int WHAT_HIDE_CONTROLLER = 1001;
    //隐藏暂停
    private static final int WHAT_HIDE_PAUSE = 1002;
    //隐藏快进、快退
    private static final int WHAT_HIDE_PROGRESS = 1003;
    //隐藏音量
    private static final int WHAT_HIDE_VOLUME = 1004;

    private static final int WHAT_NETWORK_DISCONNECT = 2000;
    //显示眼镜选择框
    private static final int WHAT_SHOW_GLASSES_LIST_DIALOG = 2001;
    private static final int STATE_NOT_STARTED = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private String mVideoPath;
    private String mVideoTitle;
    private VRModelBean mCurrentPlayStrategy;
    private Map<String, VRModelBean> mVRModels;
    private String mVideoType;
    private Handler mControlHandler;

    //时间循环
    private TimerHandle timerHandle;
    //当前播放状态
    private int mPlayState;

    private TextView video_player_total_time_text, video_player_current_time_text;
    private SeekBar videoSeekBar;
    private ImageView video_play_pause_img;
    private RelativeLayout video_player_back;
    private RelativeLayout video_player_root_layout;
    private RelativeLayout video_player_control_bar_bottom_layout;
    private RelativeLayout video_player_control_bar_top_layout;
    private TextView tv_video_name;
    private TextView video_player_strategy_text;
    private FrameLayout mContainer;
    private VideoPlayerManager mVideoMgr;
    private MojingVideoGvrView mSurfaceView;
    private PlayStrategyPopupWindow mStrategyPopWindow;
    private ProgressBar mLeftLoading;
    private ProgressBar mRightLoading;
    private ImageButton imagebtn_bar_top_in_vr;
//    private GlassesListDialog mGlassesListDialog;
    //左侧提示
    private RelativeLayout rl_remote_player_pause_left_layout;
    private RelativeLayout rl_remote_player_volume_left_layout;
    private ProgressBar pb_remote_player_left_volume;
    private RelativeLayout rl_remote_player_progress_left_layout;
    private TextView tv_remote_player_left_current_time;
    private TextView tv_remote_player_left_total_time;
    private ImageView iv_remote_player_left_progress;
    //右侧提示
    private RelativeLayout rl_remote_player_pause_right_layout;
    private RelativeLayout rl_remote_player_volume_right_layout;
    private ProgressBar pb_remote_player_right_volume;
    private RelativeLayout rl_remote_player_progress_right_layout;
    private TextView tv_remote_player_right_current_time;
    private TextView tv_remote_player_right_total_time;
    private ImageView iv_remote_player_right_progress;
    private RelativeLayout bluetooth_connect_flag_layout;
    private ImageView iv_bt_connect_flag;
    private TextView tv_bt_connect_flag;
    enum PlayStatus {
        START, COMPLETE, ERROR
    }
    String suffix = "";
    String ReportRoundID="0";
    String fileMd5="";
    long reportStart;
    int file_duration;//总时长
    String VRType = "1";//播放模式选择 1原片
    private PlayStatus complete = PlayStatus.START;
    /*播放成功时时间，退出时报数计算utime3 使用*/
    private long playSuccessTime= 0;
    private AudioManager mAudioManager; //系统音频管理
    private OnAudioFocusChangeListener mAudioFocusListener; //音频焦点监听器
    HistoryInfo historyInfo;//存储的播放的历史数据
    int playerType = VideoPlayerManager.PLAYER_SYSPLUS;
    int playPos = 0;
    int rePortBeginVrType; //记录进入播放时选择的模式，退出播放时报数使用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_gl);
        setSystemUIListener();
        Random random = new Random();
        ReportRoundID = random.nextInt()+"";
        reportStart = System.currentTimeMillis();
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(this.getApplicationContext());
        }
        findViewByIds();
        if (getIntent() != null) {
            mVideoTitle = getIntent().getStringExtra("videoName");
            initView();
            initVideoInfo();
        } else {
            showPlayUrlInvalid();
        }
    }

    private void setSystemUIListener(){
        setImmersiveSticky();
        this.getWindow()
                .getDecorView()
                .setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    setImmersiveSticky();
                                }
                            }
                        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setImmersiveSticky();
        }
    }

    private void setImmersiveSticky() {
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initVideoInfo() {
        //本地播放
        mVideoPath = getIntent().getStringExtra("videoPath");
        mVideoType = getIntent().getStringExtra("videoType");
        if (TextUtils.isEmpty(mVideoType)||mVideoType.equals(VideoTypeUtil.MJVideoPictureTypeUnknown+"")) {
            mVideoType = String.valueOf(VideoTypeUtil.MJVideoPictureTypeSingle);
        }
        initPlayFromHistory();
        getSuffix();
        initPlayStrategys();
        setPlayerConfigHistoryInfo();
        //本地直接播放
        startPlay();
        reportVV("1","0","0",null);
    }

    /**
     * 读取播放记录数据
     */
    private void initPlayFromHistory(){
        String history = HistoryBusiness.readFromHistory(mVideoPath,0);
        try {
            if(history!=null) {
                JSONObject myJsonObject = new JSONObject(history);
                historyInfo = CreateHistoryUtil.localJsonToHistoryInfo(myJsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (historyInfo != null) {
            if (!(historyInfo.getPlayFinished() == 1)) {
                playPos = historyInfo.getPlayDuration();
            }
            int m3dType = historyInfo.getVideo3dType();
            int videotype = historyInfo.getVideoType();
            if(m3dType>0&&videotype>0) {
                mVideoType = VideoTypeUtil.getVideoType(videotype, m3dType) + "";
            }
            int type = HistoryBusiness.JudgePlayerTypeHistroyToCore( historyInfo.getPlayType());
            if(type>0) {
                playerType = type;
            }
        }
    }



    private void getSuffix(){
        if (!TextUtils.isEmpty(mVideoPath) && mVideoPath.contains(".")) {
            int index = mVideoPath.lastIndexOf(".");
            if (index > 0 && index < mVideoPath.length()) {
                suffix = mVideoPath.substring(index);
            }
        }
        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                //取视频文件的前50M的md5值上传报数
                fileMd5 = MD5Util.md5FileSum(mVideoPath,30);
                reportVV(null,null,null,fileMd5);
            }
        });

    }

    private void findViewByIds() {
        video_player_back = (RelativeLayout) findViewById(R.id.video_player_back);
        tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        video_player_current_time_text = (TextView) findViewById(R.id.video_player_current_time_text);
        video_player_total_time_text = (TextView) findViewById(R.id.video_player_total_time_text);
        videoSeekBar = (SeekBar) findViewById(R.id.video_player_seekbar);
        video_player_strategy_text = (TextView) findViewById(R.id.video_player_strategy_text);
        video_play_pause_img = (ImageView) findViewById(R.id.video_play_pause_img);
        video_player_root_layout = (RelativeLayout) findViewById(R.id.video_player_root_layout);
        mContainer = (FrameLayout) findViewById(R.id.player_container);
        video_player_control_bar_bottom_layout = (RelativeLayout) findViewById(R.id.video_player_control_bar_bottom_layout);
        video_player_control_bar_top_layout = (RelativeLayout) findViewById(R.id.video_player_control_bar_top_layout);
        video_player_control_bar_top_layout.setBackgroundResource(R.color.player_title_bg);
        mLeftLoading = (ProgressBar) findViewById(R.id.pb_left_loading);
        mRightLoading = (ProgressBar) findViewById(R.id.pb_right_loading);

        rl_remote_player_pause_left_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_pause_left_layout);
        rl_remote_player_volume_left_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_volume_left_layout);
        pb_remote_player_left_volume = (ProgressBar) findViewById(R.id.pb_remote_player_left_volume);
        rl_remote_player_progress_left_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_progress_left_layout);
        tv_remote_player_left_current_time = (TextView) findViewById(R.id.tv_remote_player_left_current_time);
        tv_remote_player_left_total_time = (TextView) findViewById(R.id.tv_remote_player_left_total_time);
        iv_remote_player_left_progress = (ImageView) findViewById(R.id.iv_remote_player_left_progress);

        rl_remote_player_pause_right_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_pause_right_layout);
        rl_remote_player_volume_right_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_volume_right_layout);
        pb_remote_player_right_volume = (ProgressBar) findViewById(R.id.pb_remote_player_right_volume);
        rl_remote_player_progress_right_layout = (RelativeLayout) findViewById(R.id.rl_remote_player_progress_right_layout);
        tv_remote_player_right_current_time = (TextView) findViewById(R.id.tv_remote_player_right_current_time);
        tv_remote_player_right_total_time = (TextView) findViewById(R.id.tv_remote_player_right_total_time);
        iv_remote_player_right_progress = (ImageView) findViewById(R.id.iv_remote_player_right_progress);

        bluetooth_connect_flag_layout = (RelativeLayout) findViewById(R.id.bluetooth_connect_flag_layout);
        iv_bt_connect_flag = (ImageView)findViewById(R.id.iv_bluetooth_connect_flag);
        tv_bt_connect_flag = (TextView)findViewById(R.id.tv_bluetooth_connect_flag);

        imagebtn_bar_top_in_vr = (ImageButton) findViewById(R.id.imagebtn_bar_top_in_vr);
        imagebtn_bar_top_in_vr.setOnClickListener(this);
        imagebtn_bar_top_in_vr.setVisibility(View.GONE);
    }

    private void initView() {
        video_play_pause_img.setOnClickListener(this);
        video_player_back.setOnClickListener(this);
        video_player_strategy_text.setOnClickListener(this);
//        video_player_root_layout.setOnClickListener(this);
        mContainer.setOnClickListener(this);
        tv_video_name.setText(mVideoTitle);
        video_player_strategy_text.setVisibility(View.VISIBLE);
        mControlHandler = new Handler(this);
        //默认为未开始
        mPlayState = STATE_NOT_STARTED;
    }

    private void initPlayerView() {
        MojingSDKHandler.getInstance().InitSDK(this, SettingSpBusiness.getInstance().getGlassesModeKey());
        mVideoMgr = new VideoPlayerManager(this);
        mVideoMgr.setHandler(mControlHandler);
        mVideoMgr.setModelType(VrModel.ModelType.MODEL_RECT);
        MojingSDKHandler.getInstance().setRender(mVideoMgr.getSurface());
        MojingSDKHandler.getInstance().onResume();
        mSurfaceView = MojingSDKHandler.getInstance().getView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mSurfaceView, params);

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayState != STATE_NOT_STARTED) {
                    mVideoMgr.playSeek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        timerHandle = new TimerHandle(new Runnable() {
            @Override
            public void run() {
                setPlayProgress();
            }
        });
        timerHandle.setTimes(1000);
        mControlHandler.sendEmptyMessageDelayed(WHAT_HIDE_CONTROLLER, 5000);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP){
            if(mControlHandler!=null) {
                mControlHandler.sendEmptyMessageDelayed(WHAT_HIDE_CONTROLLER, 5000);
            }
        }else {
            if(mControlHandler!=null) {
                mControlHandler.removeMessages(WHAT_HIDE_CONTROLLER);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startPlay() {

        mAudioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {

            }
        };
        int result = mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mVideoMgr == null) {
                    initPlayerView();
                }

                //报数
                mVideoMgr.loadMovie(mVideoPath, mCurrentPlayStrategy.getScreenType(), playerType, playPos);
                mVideoMgr.setModelType(mCurrentPlayStrategy.getModelType());
            }
        });
    }

    private void setPlayProgress() {
        if (mVideoMgr != null && mVideoMgr.isPlaying()) {
            if (mVideoMgr.getCurPos() != -1) {
                int posion = mVideoMgr.getCurPos();
                videoSeekBar.setProgress(posion);
                video_player_current_time_text.setText(TimeUtil.format(posion / 1000));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.video_player_strategy_text) {
            showVRModelWindow();
        } else if (i == R.id.video_player_back) {
            mControlHandler.sendEmptyMessage(WHAT_QUIT_PLAYER);
        } else if (i == R.id.video_play_pause_img) {
            setVideoPlayOrPause();
        } else if (i == R.id.player_container) {
            setControllBarVisibility();
        } else if(i == R.id.imagebtn_bar_top_in_vr){
            StickUtil.disconnect();
            showProgressDialog();//显示加载进度条
            clearInfos();//释放播放
            StartActivityHelper.playVideoWithLocalUnity(this, mVideoTitle, mVideoPath, new StartActivityHelper.GotoPlayCallback() {
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
            reportVRKeyClick();
        }

    }

    private void setControllBarVisibility() {
        if (video_player_control_bar_bottom_layout.getVisibility() == View.VISIBLE
                && video_player_control_bar_top_layout.getVisibility() == View.VISIBLE) {
            video_player_control_bar_bottom_layout.setVisibility(View.GONE);
            video_player_control_bar_top_layout.setVisibility(View.GONE);
            if (mStrategyPopWindow != null && mStrategyPopWindow.isShowing()) {
                mStrategyPopWindow.dismiss();
            }
            mControlHandler.removeMessages(WHAT_HIDE_CONTROLLER);
        } else {
            video_player_control_bar_bottom_layout.setVisibility(View.VISIBLE);
            video_player_control_bar_top_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setFullScreenVisibility(int visibility) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (visibility == View.VISIBLE) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /***
     * 显示播放格式
     */
    private void showVRModelWindow() {
        if (mStrategyPopWindow == null || !mStrategyPopWindow.isShowing()) {
            mStrategyPopWindow = new PlayStrategyPopupWindow(this, new ArrayList<VRModelBean>(mVRModels.values()));
            int[] location = new int[2];
            int yDelta = mVRModels.size() > SHOW_PLAY_STRATEGY_MAX ? SHOW_PLAY_STRATEGY_MAX : mVRModels.size()
                    * PixelsUtil.dip2px(LIST_ITEM_HEIGHT_DIP);
            video_player_strategy_text.getLocationOnScreen(location);
            int totalHeight =
//                    video_player_strategy_text.getMeasuredHeight() +
//                    video_player_strategy_text.getHeight() +
                    video_player_strategy_text.getPaddingBottom() +
                            yDelta;
            mStrategyPopWindow.showAtLocation(video_player_strategy_text, Gravity.NO_GRAVITY, location[0], location[1] - totalHeight);
            video_player_strategy_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nav_icon_arrow_down, 0);
            mStrategyPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    video_player_strategy_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.nav_icon_arrow_up, 0);
                }
            });
            mStrategyPopWindow.setOnItemClickListener(new PlayStrategyPopupWindow.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mVideoType = String.valueOf(getLocalVideoType(position));
                    String localFormat = mVideoPath.substring(mVideoPath.lastIndexOf(".")+1);
                    VRType =  String.valueOf(position+1);
                    reportClick(mVideoTitle,localFormat, mVideoPath,VRType);
                    //切换播放模式时报MD5值
                    reportVV(null,null,null,fileMd5);

                    VRModelBean bean = mVRModels.get(mVideoType);
                    if (bean != null) {
                        //用户选择的视频类型存入数据库（横屏播放就按照用户选择的视频类型播）
                        SqliteProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                            @Override
                            public void run() {
                                SqliteManager.getInstance().updateToLocalVideoType(mVideoPath, Integer.valueOf(mVideoType));
                                SqliteManager.getInstance().closeSQLiteDatabase();
                            }
                        });
                        setPlayStrategy(bean);
                    }
                }
            });
            mStrategyPopWindow.setCurrentPlayStrategy(mCurrentPlayStrategy);
        }
    }

    private void initPlayStrategys() {
        mVRModels = new LinkedHashMap<>();
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypeSingle), new VRModelBean(1,VrModel.ScreenType.TYPE_2D, VrModel.ModelType.MODEL_RECT, "原片", true));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypeSideBySide), new VRModelBean(2,VrModel.ScreenType.TYPE_LR3D, VrModel.ModelType.MODEL_RECT, "3D左右", false));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypeStacked), new VRModelBean(3,VrModel.ScreenType.TYPE_UD3D, VrModel.ModelType.MODEL_RECT, "3D上下", true));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypePanorama360), new VRModelBean(4,VrModel.ScreenType.TYPE_2D, VrModel.ModelType.MODEL_SPHERE, "360°", false));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypePanorama3603DSide), new VRModelBean(5,VrModel.ScreenType.TYPE_LR3D, VrModel.ModelType.MODEL_SPHERE, "360°左右", false));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypePanorama3603DStacked), new VRModelBean(6,VrModel.ScreenType.TYPE_UD3D, VrModel.ModelType.MODEL_SPHERE, "360°上下", false));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypePanorama1803DSide), new VRModelBean(7,VrModel.ScreenType.TYPE_LR3D, VrModel.ModelType.MODEL_SPHERE180, "180°左右", true));
        mVRModels.put(String.valueOf(VideoTypeUtil.MJVideoPictureTypePanorama360Cube), new VRModelBean(8,VrModel.ScreenType.TYPE_2D, VrModel.ModelType.MODEL_BOX, "立方体", false));
    }

    //设置显示不同策略
    private void setPlayStrategy(VRModelBean modelBean) {
        mCurrentPlayStrategy = modelBean;
        mStrategyPopWindow.setCurrentPlayStrategy(mCurrentPlayStrategy);
        video_player_strategy_text.setText(modelBean.getName());
        mVideoMgr.set3DType(mCurrentPlayStrategy.getScreenType());
        mVideoMgr.setModelType(mCurrentPlayStrategy.getModelType());
    }

    private void setVideoPlayOrPause() {
        if (mVideoMgr.isPlaying()) {
            videoPause();
        } else {
            videoStart();
        }
    }

    private void videoStart() {
        if (mVideoMgr == null) {
            return;
        }
        mVideoMgr.playContinue();
        mPlayState = STATE_PLAYING;
        rl_remote_player_pause_left_layout.setVisibility(View.GONE);
        rl_remote_player_pause_right_layout.setVisibility(View.GONE);
        video_play_pause_img.setImageResource(R.drawable.seekbar_suspended_icon);
    }

    private void videoPause() {
        if (mVideoMgr == null) {
            return;
        }
        rl_remote_player_pause_left_layout.setVisibility(View.VISIBLE);
        rl_remote_player_pause_right_layout.setVisibility(View.VISIBLE);
        video_play_pause_img.setImageResource(R.drawable.seekbar_play_icon);
        mVideoMgr.playPause();
        mPlayState = STATE_PAUSE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoMgr != null) {
            saveHistory();
            MojingSDKHandler.getInstance().onPause();
            mVideoMgr.playPause();
        }
    }

    @Override
    public void startCheck() {
        if (BaseApplication.INSTANCE.isBFMJ5Connection() && BaseApplication.INSTANCE.getJoystickConnect()) { //魔镜5代usb连接，并且遥控器连接上
            connectManager(true);
        } else if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            connectManager(false);
        } else if (!StickUtil.isBondBluetooth()) {// 蓝牙与魔镜设备未配对
            connectManager(false);
        } else if (!StickUtil.isConnected) {// 设备未开启或者设备休眠
            connectManager(false);
        } else {// 已连接
            connectManager(true);
        }
    }


    @Override
    protected void onDestroy() {
        StickUtil.disconnect();
        //MojingSDKHandler.getInstance().onDestroy();

        if (mSurfaceView != null){
            clearInfos();
        }

        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoMgr != null) {
            MojingSDKHandler.getInstance().onResume();
        }
        if (mVideoMgr != null && mPlayState == STATE_PLAYING) {
            mVideoMgr.playContinue();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == mVideoMgr.MSG_LOADING) {
            mLeftLoading.setVisibility(View.VISIBLE);
            mRightLoading.setVisibility(View.VISIBLE);
        } else if (msg.what == mVideoMgr.MSG_LOADING_END) {
            dimissLoadingProgressBar();
        } else if (msg.what == mVideoMgr.MSG_PLAY_START) {
            dimissLoadingProgressBar();
            onPrepared();
        } else if (msg.what == mVideoMgr.MSG_PLAY_END) {
            onComplete();
            dimissLoadingProgressBar();
            finish();
        } else if (msg.what == mVideoMgr.MSG_PLAY_ERROR) {
            onPlayError();
        } else if (msg.what == WHAT_QUIT_PLAYER) {
            reportBack();
            finish();
        } else if (msg.what == WHAT_HIDE_CONTROLLER) {
            setControllBarVisibility();
        } else if (msg.what == WHAT_NETWORK_DISCONNECT) {
            Toast.makeText(this, getResources().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
        } else if (msg.what == WHAT_SHOW_GLASSES_LIST_DIALOG) {
//            showGlassesDialog();
        } else if (msg.what == WHAT_HIDE_PAUSE
                || msg.what == WHAT_HIDE_PROGRESS
                || msg.what == WHAT_HIDE_VOLUME) {
            if (msg.obj != null && msg.obj instanceof View) {
                View view = ((View) msg.obj);
                if (view.getVisibility() == View.VISIBLE) {
                    if (rl_remote_player_pause_left_layout == view) {
                        rl_remote_player_pause_left_layout.setVisibility(View.GONE);
                        rl_remote_player_pause_right_layout.setVisibility(View.GONE);
                    } else if (rl_remote_player_volume_left_layout == view) {
                        rl_remote_player_volume_left_layout.setVisibility(View.GONE);
                        rl_remote_player_volume_right_layout.setVisibility(View.GONE);
                    } else if (rl_remote_player_progress_left_layout == view) {
                        rl_remote_player_progress_left_layout.setVisibility(View.GONE);
                        rl_remote_player_progress_right_layout.setVisibility(View.GONE);
                    }
                    //缓冲框显示时,隐藏
                    dimissLoadingProgressBar();
                }
            }
        }
        return false;
    }

    private void onPrepared() {
        video_player_total_time_text.setText(" / " + TimeUtil.format(mVideoMgr.getDuration() /
                1000));
        videoSeekBar.setMax(mVideoMgr.getDuration());
        video_play_pause_img.setImageResource(R.drawable.seekbar_suspended_icon);
        timerHandle.start();
        //设置当前状态为播放
        mPlayState = STATE_PLAYING;
        complete = PlayStatus.START;
        file_duration = mVideoMgr.getDuration();
       	/*尝试报数*/
        if(reportStart>0) {
            reportVV("2", "0", (System.currentTimeMillis() - reportStart) + "",null);
            reportStart = 0;
        }
          /*记录播放成功时的时间*/
        if(playSuccessTime<=0){
            playSuccessTime = System.currentTimeMillis();
        }
    }

    private void onComplete(){
        if (complete==PlayStatus.ERROR ) {
            return;
        }
        long utime = 0,ltime=0;
        if(playSuccessTime>0){
            utime = System.currentTimeMillis()-playSuccessTime;
        }
        if(reportStart>0){
            ltime = System.currentTimeMillis()-reportStart;
        }
        if(reportStart>0){
            ltime = System.currentTimeMillis()-reportStart;
        }
        reportVV("7", utime<0?"0":utime+ "",ltime<0?"0":ltime+"",null);
        reportStart = 0;
        playSuccessTime = 0;
        complete = PlayStatus.COMPLETE;
    }

    /**
     * 退出时报VV
     * @return 无
     */
    public void reportBack() {
        if (complete == PlayStatus.COMPLETE || complete == PlayStatus.ERROR)
            return;
        long utime = 0,ltime=0;
        if(playSuccessTime>0){
            utime = System.currentTimeMillis()-playSuccessTime;
        }
        if(reportStart>0){
            ltime = System.currentTimeMillis()-reportStart;
        }
        reportVV("7", utime<0?"0":utime+ "",ltime<0?"0":ltime+"",null);
        playSuccessTime = 0;
        reportStart = 0;
    }

    /***
     * 本地播放器播放类型设置
     */
    private void setPlayerConfigHistoryInfo() {
        mCurrentPlayStrategy = mVRModels.get(mVideoType);
        rePortBeginVrType = mCurrentPlayStrategy.getId();
        VRType = rePortBeginVrType+"";
        video_player_strategy_text.setText(mCurrentPlayStrategy.getName());
    }

    private void showPlayUrlInvalid() {
        Toast.makeText(this, "无播放地址", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void dimissLoadingProgressBar() {
        if (mLeftLoading.getVisibility() == View.VISIBLE) {
            mLeftLoading.setVisibility(View.GONE);
            mRightLoading.setVisibility(View.GONE);
        }
    }

    private void onPlayError() {
        if(complete==PlayStatus.ERROR)
            return;
        complete = PlayStatus.ERROR;
        long utime = 0,ltime=0;
        if(playSuccessTime>0){
            utime = System.currentTimeMillis()-playSuccessTime;
        }
        if(reportStart>0){
            ltime = System.currentTimeMillis()-reportStart;
        }
        reportVV("3", utime<0?"0":utime+"", ltime<0?"0":ltime+"",null);
        reportStart = 0;
        playSuccessTime = 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            mControlHandler.sendEmptyMessage(WHAT_QUIT_PLAYER);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onMojingKeyLongPress(String s, int i) {
        PlayerBusiness.getInstance().setPlayerTime(mVideoMgr, i);
        return super.onMojingKeyLongPress(s, i);
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {
             super.onTouchPadStatusChange(s,b);
    }

    @Override
    public void onTouchPadPos(String s, float v, float v1) {
         super.onTouchPadPos(s,v,v1);
    }

    @Override
    public boolean onMojingKeyDown(String s, int i) {
        if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_OK
                || i == PlayerBusiness.getInstance().KEY_JOYSTICK_OK) {
            setRemoteControllerVisibility(rl_remote_player_pause_left_layout, WHAT_HIDE_PAUSE);
            setVideoPlayOrPause();
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_BACK
                || i == PlayerBusiness.getInstance().KEY_JOYSTICK_BACK) {
            mControlHandler.sendEmptyMessage(WHAT_QUIT_PLAYER);
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_UP) {
            increaseVolume();
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_DOWN) {
            decreaseVolume();
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_LEFT) {
            rewind();
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_RIGHT) {
            fastForward();
        }
        return super.onMojingKeyDown(s, i);
    }

    /***
     * 遥控提示
     *
     * @param view
     */
    private void setRemoteControllerVisibility(View view, int what) {
        if (view.getVisibility() == View.GONE) {
            rl_remote_player_pause_left_layout.setVisibility(view == rl_remote_player_pause_left_layout ? View.VISIBLE : View.GONE);
            rl_remote_player_pause_right_layout.setVisibility(view == rl_remote_player_pause_left_layout ? View.VISIBLE : View.GONE);
            rl_remote_player_volume_left_layout.setVisibility(view == rl_remote_player_volume_left_layout ? View.VISIBLE : View.GONE);
            rl_remote_player_volume_right_layout.setVisibility(view == rl_remote_player_volume_left_layout ? View.VISIBLE : View.GONE);
            rl_remote_player_progress_left_layout.setVisibility(view == rl_remote_player_progress_left_layout ? View.VISIBLE : View.GONE);
            rl_remote_player_progress_right_layout.setVisibility(view == rl_remote_player_progress_left_layout ? View.VISIBLE : View.GONE);
        }
        mControlHandler.removeMessages(what);
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = view;
        mControlHandler.sendMessageDelayed(msg, 3000);
    }

    /***
     * 放大音量
     */
    private void increaseVolume() {
        setVolumePopup(true);
    }

    /***
     * 减小音量
     */
    private void decreaseVolume() {
        setVolumePopup(false);
    }

    /***
     * 设置音量
     *
     * @param isIncrease 是否为放大音量
     */
    private void setVolumePopup(boolean isIncrease) {
        if (isIncrease) {
            PlayerBusiness.getInstance().increaseVolume();
        } else {
            PlayerBusiness.getInstance().decreaseVolume();
        }
        setRemoteControllerVisibility(rl_remote_player_volume_left_layout, WHAT_HIDE_VOLUME);
        int currentVolume = AudioManagerUtil.getInstance().getStreamCurrentVolume();
        pb_remote_player_left_volume.setProgress(currentVolume);
        pb_remote_player_right_volume.setProgress(currentVolume);
    }


    /***
     * 快退
     */
    private void rewind() {
        setProgressPopup(false);
    }

    /***
     * 快进
     */
    private void fastForward() {
        setProgressPopup(true);
    }

    /***
     * 设置快进 快退 弹框的提示
     *
     * @param isFastForward 是否快进
     */
    private void setProgressPopup(boolean isFastForward) {
        if (isFastForward) {
            PlayerBusiness.getInstance().fastForward(mVideoMgr);
            iv_remote_player_left_progress.setImageResource(R.drawable.fast_forward_icon);
            iv_remote_player_right_progress.setImageResource(R.drawable.fast_forward_icon);
        } else {
            PlayerBusiness.getInstance().rewind(mVideoMgr);
            iv_remote_player_left_progress.setImageResource(R.drawable.rewind_icon);
            iv_remote_player_right_progress.setImageResource(R.drawable.rewind_icon);
        }
        setRemoteControllerVisibility(rl_remote_player_progress_left_layout, WHAT_HIDE_PROGRESS);
        String currentTime = TimeUtil.format(mVideoMgr.getCurPos() / 1000);
        String totalTime = TimeUtil.format(mVideoMgr.getDuration() / 1000);
        tv_remote_player_left_current_time.setText(currentTime);
        tv_remote_player_right_current_time.setText(currentTime);
        tv_remote_player_left_total_time.setText("/ " + totalTime);
        tv_remote_player_right_total_time.setText("/ " + totalTime);
    }

    private void clearInfos() {
        if (mVideoMgr != null) {
            mVideoMgr.Quite(true);
            if(mSurfaceView!=null){
                mSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mVideoMgr.relseSurface();
                        mVideoMgr = null;
                    }
                });
            }
        }

        if (timerHandle != null) {
            timerHandle.release();
        }
        mControlHandler.removeCallbacksAndMessages(null);

//        mSurfaceView = null;
    }

    private void connectManager(boolean isConnected) {
        if (isConnected) {
            iv_bt_connect_flag.setImageResource(R.drawable.bluetooth_conneced_img);
            tv_bt_connect_flag.setText(R.string.bluetooth_control_conneced);
        } else {
            iv_bt_connect_flag.setImageResource(R.drawable.bluetooth_disconneced_img);
            tv_bt_connect_flag.setText(R.string.bluetooth_control_disconneced);
        }
    }

    public int getLocalVideoType(int postion) {
        switch (postion) {
            case 0:
                return VideoTypeUtil.MJVideoPictureTypeSingle;
            case 1:
                return VideoTypeUtil.MJVideoPictureTypeSideBySide;
            case 2:
                return VideoTypeUtil.MJVideoPictureTypeStacked;
            case 3:
                return VideoTypeUtil.MJVideoPictureTypePanorama360;
            case 4:
                return VideoTypeUtil.MJVideoPictureTypePanorama3603DSide;
            case 5:
            return VideoTypeUtil.MJVideoPictureTypePanorama3603DStacked;
            case 6:
                return VideoTypeUtil.MJVideoPictureTypePanorama1803DSide;
            case 7:
                return VideoTypeUtil.MJVideoPictureTypePanorama360Cube;
        }
        return VideoTypeUtil.MJVideoPictureTypeSingle;
    }

    /**
     * 点击切换片源类型时上报
     * @param title
     * @param localFormat
     * @param filePath
     */
    private void reportClick(String title, String localFormat, String filePath,String viewtype){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("cut");
        bean.setTpos("1");
        bean.setPagetype("preview");
        bean.setTitle(title);
        bean.setLocal_format(localFormat);
        bean.setFilepath(filePath);
        bean.setViewtype(viewtype);
        ReportBusiness.getInstance().reportClick(bean);
    }

    /***
     * vr key 点击报数
     */
    private void reportVRKeyClick(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("jump");
        bean.setPagetype("VR_key");
        bean.setFrompage("preview");
        ReportBusiness.getInstance().reportClick(bean);
    }

        private void reportVV(String type, String utime, String ltime,String md5) {
            HashMap<String, String> hs = new HashMap<String, String>();
            hs.put("etype", "vv");
            hs.put("tpos", "1");
            if(type!=null) {
                hs.put("vvtype", type);
            }
            hs.put("pagetype", "local");
            hs.put("local_menu_id", "1");
            hs.put("roundid",ReportRoundID );
            if(utime!=null) {
                hs.put("utime3", utime);
            }
            if(ltime!=null) {
                hs.put("ltime", ltime + "");
            }
            hs.put("title", mVideoTitle );
            hs.put("local_format", suffix);
            hs.put("filetime",file_duration+"");
            hs.put("filepath",mVideoPath);
            if(md5!=null) {
                hs.put("md5", md5);
            }
            hs.put("viewtype",VRType);
            hs.put("joystick", StickUtil.isConnected?"1":"0");
            if("7".equals(type)){ //退出播放时 上报视频播放配置
                hs.put("vrsetting","{begin:"+rePortBeginVrType+",end:"+VRType+"}");
            }

            ReportBusiness.getInstance().reportVV(hs);
        }

    /**
     * 进入后台，保存播放记录
     */
    private void saveHistory(){

        if(mVideoMgr==null)
            return;
        if(historyInfo==null){
            historyInfo = new HistoryInfo();
            historyInfo.setAudioTrack(-1);
            historyInfo.setType(0);
            historyInfo.setResType(0);
            historyInfo.setLastSetIndex(0);
            historyInfo.setPlayDuration(mVideoMgr.getCurPos());
            historyInfo.setPlayFinished(complete==PlayStatus.COMPLETE?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());
            historyInfo.setTotalDuration(mVideoMgr.getDuration());
            historyInfo.setVideoPlayUrl(mVideoPath);
            historyInfo.setVideoTitle(mVideoTitle);

            historyInfo.setVideoId("");
            historyInfo.setVideoImg("");
            historyInfo.setVideoClarity("");
            historyInfo.setDetailUrl("");
            historyInfo.setVideoSet(1);
        }else {
            historyInfo.setTotalDuration(mVideoMgr.getDuration());
            historyInfo.setPlayDuration(mVideoMgr.getCurPos());
            historyInfo.setPlayFinished(complete==PlayStatus.COMPLETE?1:0);
            historyInfo.setPlayTimestamp(System.currentTimeMillis());

            historyInfo.setVideoPlayUrl(mVideoPath);
            historyInfo.setLastSetIndex(0);
        }
        historyInfo.setPlayType( HistoryBusiness.JudgePlayerTypeCoreToHistroy( mVideoMgr.m_decodeType));
        if(!TextUtils.isEmpty(mVideoType)) {
           HistoryBusiness.VideoViewparam videoViewparam =  HistoryBusiness.JudgeVideoType(Integer.parseInt(mVideoType));
            historyInfo.setVideo3dType(videoViewparam.mVideo3DType);
            historyInfo.setVideoType(videoViewparam._videoModelType);
        }
        String json=new Gson().toJson(historyInfo);
        HistoryBusiness.writeToHistory(json,mVideoPath,0);
    }

}
