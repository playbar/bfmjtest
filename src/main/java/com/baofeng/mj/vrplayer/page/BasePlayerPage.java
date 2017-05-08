package com.baofeng.mj.vrplayer.page;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.online.utils.MediaHelp;
import com.baofeng.mj.ui.online.utils.PlayerNetworkSubject;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.vrplayer.interfaces.IPlayerSettingCallBack;
import com.baofeng.mj.vrplayer.interfaces.IResetLayerCallBack;
import com.baofeng.mj.vrplayer.interfaces.IViewVisiableListener;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.MJGLUtils;
import com.baofeng.mj.vrplayer.utils.SoundUtils;
import com.baofeng.mj.vrplayer.view.GLDialogView2;
import com.baofeng.mj.vrplayer.view.GLDialogViewSingleBtn;
import com.baofeng.mj.vrplayer.view.GLLoadToast;
import com.baofeng.mj.vrplayer.view.GLLoadingToast;
import com.baofeng.mj.vrplayer.view.GLProcessToast;
import com.baofeng.mj.vrplayer.view.GLTextToast;
import com.baofeng.mj.vrplayer.view.GLUnLockDialog;
import com.baofeng.mj.vrplayer.view.MoviePlayerControlView;
import com.baofeng.mj.vrplayer.view.MoviePlayerSettingView;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.interfaces.IGLPlayer;
import com.bfmj.viewcore.interfaces.IGLPlayerListener;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLGroupView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLViewPage;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.utils.PlayCheckUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2016/10/14.
 */
public abstract class BasePlayerPage extends GLViewPage implements PlayerNetworkSubject.PlayerNetWorkChangeListener,IPlayerControlCallBack {
    private Timer mUpdateTimer;
    protected int lastPlaytime;
    protected GLBaseActivity mActivity;
    protected GLGroupView mRootView;
    protected GLRelativeView mControlLayer;
    protected GLRelativeView mSettingLayer;
    protected MoviePlayerControlView moviePlayerControlView;
    protected MoviePlayerSettingView moviePlayerSettingView;
    protected GLTextToast glTextToast; //toast提示
    protected GLLoadingToast glLoadingToast; //loading
    protected GLLoadToast glLoadToast;
    protected GLDialogView2 glDialogView; //提示框
    protected GLDialogViewSingleBtn mSingleBtnDialog;
    protected GLUnLockDialog unLockDialog;
    protected final int layout_width=2400;
    protected final int layout_height=2400;
    protected final int layout_x=0;
    protected final int layout_y=720;
    public PlayerState mCurrentState = PlayerState.IDEL;
    protected int mPrepareSeekTo;
    /*播放成功时时间，退出时报数计算utime3 使用*/
    protected long playSuccessTime= 0;
    protected long mLoadingTime = -1;
    protected long mStartTipsTime = -1;  //播放中加载超过10s提示的计时
    protected int mLoadingCount = 0;
    protected long mLoadingAllTime = 0;
    public boolean isPlayCompletion = false;
    /*播放报数随机数*/
    protected String ReportRoundID="0";
    /*计算尝试时长的报数*/
    protected long startReport = 0;
    protected boolean hd_change_flag = false;  //正在切换清晰度
    protected boolean show_load_toast = true;  //标志是否显示"即将播放..."的toast
    protected boolean delay_showloadin_flag = false; //房子显示重叠 delay显示loading
    public enum PlayerState{  //记录播放状态  目前主要用在 播放还未加载成功前断开网络，再次连上网后需要根据State是否为PrePared的来判断重新创建player播放
        IDEL, PREPARED,COMPLETE,ERROR
    }
    protected List<String> mHdTextList = new ArrayList<>(); //清晰度
    protected boolean isError = false;
    protected boolean isPauseView = false;//是否切出播放页面
//    protected Handler mHandler;
    protected int mPageType;
    private final float anim_depth = 0.15f;
    private final float anim_scale = 0.0375f;
    public BasePlayerPage(Context context,int type){
        super(context);
        mActivity = ((GLBaseActivity) getContext());
        this.mPageType = type;
        PlayCheckUtil.setSupportLeftEye(true);
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(getContext());
        }
        PlayerNetworkSubject.getInstance().Bind(this);

    }
    @Override
    protected GLRectView createView(GLExtraData glExtraData) {
        mRootView = new GLGroupView(mActivity);
//        mRootView.setLayoutParams(layout_width,
//                layout_height);
        mRootView.setOnKeyListener(pageKeyListener);
        mRootView.setDepth(GLConst.Movie_Player_Depth);
//        mHandler = new Handler();
//        mRootView.setDepth(GLConst.Movie_Player_Depth,GLConst.Movie_Player_Scale);
//        mRootView.setBackground(new GLColor(0,1,0));
        ((GLBaseActivity)getContext()).setIResetLayerCallBack(new IResetLayerCallBack() {
            @Override
            public void isOpen(boolean isOpen) {
                if(isOpen) {
                    moviePlayerControlView.showAllView();

                } else {
                    //全部高级菜单子view隐藏
                    moviePlayerSettingView.hideAllView();
                    //隐藏控制菜单
                    moviePlayerControlView.hideAllView();
                }
                doOpenMenu(isOpen);
            }

            @Override
            public void onFocusChange(boolean focused) {
                if(!focused) {
                    hideViewTimer();
                } else {
                    cancelHideViewTimer();
                }
            }
        });
        startUpdateTimer();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
         startUpdateTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdateTimer();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        PlayerNetworkSubject.getInstance().UnBind(this);
    }
    private void startUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                updateProgress();
            }
        }, 1000, 1000);
    }

    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
    }


    @Override
    public void networkChange(int currentNetwork) {
        doNetChanged(false);
    }
    protected void doExceptionRePlay(){

    }
    /**
     * 更新播放进度
     * @param
     * @return
     */
    protected abstract void updateProgress();
    protected  abstract void startPlay();
    protected abstract void pausePlay();
    protected abstract void seekTo(int current_pos);
    protected abstract void reportVV(String type, String utime, String ltime);
    protected abstract void rePlay();
    protected abstract void onChangeHd(String hdtype);
    protected void doOpenMenu(boolean isOpen){

    }
    protected void setPath(String path){
        showLoading();
    }
    protected void onChangeSelectIndex(int SeqNo){

    }
    protected  void showLoading(){
        showLoadToast(!show_load_toast);
        if(show_load_toast == true &&mCurrentState== PlayerState.PREPARED){
            show_load_toast = false;
        }
        mLoadingTime = System.currentTimeMillis();
        if (mCurrentState== PlayerState.PREPARED) {
            mStartTipsTime = System.currentTimeMillis();
            if (mLoadingCount > 0) {
                mLoadingCount++;
            }
        }
    }
    protected void onMobileNet(){    }
    protected  void hideLoading(){
        hideLoadToast();
           mLoadingTime = -1;
        if ( mCurrentState== PlayerState.PREPARED) {
            mStartTipsTime = -1;
        }
    }

    /**
     * 播放监听
     */
    protected IGLPlayerListener simpleBaofengListener = new IGLPlayerListener() {
        @Override
        public void onPrepared(IGLPlayer player) {
            isError = false;
            mCurrentState = PlayerState.PREPARED;
            if(moviePlayerControlView!=null) {
                moviePlayerControlView.setPlayOrPauseBtn(false);
            }
//            startPlay();
            MediaHelp.mPlayer.start();
            if (mPrepareSeekTo > 0) {
                seekTo(mPrepareSeekTo);
            }else {
                mPrepareSeekTo = -1;
            }
            onPlayPrepared();
            checkPlay();
            hideLoading();

        }

        @Override
        public boolean onInfo(IGLPlayer player, int what, Object extra) {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START
                    || what == IBfPlayerConstant.IOnInfoType.INFO_BUFFERING_START) {
                showLoading();

            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END
                    || what == IBfPlayerConstant.IOnInfoType.INFO_BUFFERING_END) {
                hideLoading();
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
            onPlayComplete();
        }

        @Override
        public void onSeekComplete(IGLPlayer player) {
            mPrepareSeekTo = -1;
            hideLoading();
            if (isPauseView) {
                pausePlay();
            } else {
                startPlay();
            }
            if(moviePlayerControlView!=null) {
                moviePlayerControlView.setPlayOrPauseBtn(isPauseView);
            }

        }

        @Override
        public boolean onError(IGLPlayer player, int what, int extra) {
            mCurrentState = PlayerState.ERROR;
            doNetChanged(true);
            onPlayError();

            return false;
        }

        @Override
        public void onVideoSizeChanged(IGLPlayer player, int width, int height) {

        }

        @Override
        public void onTimedText(IGLPlayer player, String text) {

        }


    };


    /**
     * 成功播放后回调改方法（在playerView中），处理了报数问题
     */
    public void onPlayPrepared(){

      /*记录播放成功时的时间*/
        if(playSuccessTime<=0){
            playSuccessTime = System.currentTimeMillis();
        }
        if(startReport>0) {
            reportVV("2", "0", (System.currentTimeMillis() - startReport) + "" );
            startReport = 0;
        }
    }

    public void onPlayComplete() {

        reportPlaySuccess();
        startReport = System.currentTimeMillis();
        reSetRoundId();
    }
    /**
     * 播放失败回调
     */
    public void onPlayError() {

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

    /**
     * 重置报数用的随机数
     */
    protected void reSetRoundId(){
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
        reportVV("7",utime<0?"0":utime+"",ltime<0?"0":ltime+"");
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
                if(isPlayError){
                    if(glDialogView!=null){
                        glDialogView.showExceptionDialog(BaseApplication.getInstance().getResources().getString(R.string.player_load_failed),getContext().getString(R.string.player_reload),false);
//                        glDialogView.setVisible(true);
                        showDialogView(1);
                    }
                    hideLoading();
                    pausePlay();

                    return;
                }


                if(!NetworkUtil.isNetworkConnected(getContext())){//无网络
                    if(glDialogView!=null){
                        glDialogView.showExceptionDialog(BaseApplication.INSTANCE.getResources().getString(R.string.player_network_exception),getContext().getString(R.string.player_reload),false);
                        showDialogView(1);
                    }
                    hideLoading();
                    pausePlay();

                    return;
                }
                if(!NetworkUtil.canPlayAndDownload() ){ //有网络 但为非WIFI网络
                    if(glDialogView!=null){
                        glDialogView.showExceptionDialog(BaseApplication.getInstance().getResources().getString(R.string.player_no_wifi),getContext().getString(R.string.player_play_continue),true);
                        showDialogView(1);
                    }
                    hideLoading();
                    pausePlay();
                    onMobileNet();
                    return;
                }else { //连上网络
                    if(glDialogView!=null&&mCurrentState== PlayerState.PREPARED) {
                        if(!isPauseView) {
                            startPlay();
                        }
                    }else {
                        if(!isPauseView) {
                            rePlay();
                        }
                    }
                    if(isPauseView){
                        moviePlayerControlView.setPlayOrPauseBtn(false);
                    }
                    hideDialogView();
                }


    }

    protected void handleNetWorkException(){
        if (!getLoadToastVisiable()) {
            return;
        }
//        Log.d("login"," mLoadingtime = "+mLoadingTime+" mLoadingCount = "+mLoadingCount+"  current = "+System.currentTimeMillis()+",alltime = "+mLoadingAllTime);
        /**当次播放时，首次出现卡顿超过10s，提示*/
        if(mStartTipsTime!=-1 && System.currentTimeMillis() - mStartTipsTime>3*1000){
            if(mLoadingCount==0){
                hideLoadToast();
                mSingleBtnDialog.showNetworkTips(4 * 1000,false, new  GLDialogViewSingleBtn.IGLDismissListener() {
                    @Override
                    public void dismiss() {
                        if(mStartTipsTime>0){
                           showLoadToast(true);
                        }
                    }
                });
                showDialogView(2);
                mLoadingAllTime = System.currentTimeMillis();
                mLoadingCount++;
            }
        }
        /**当首次卡顿到第三次卡顿间隔<=30s*/
        if(mLoadingCount==2&&mLoadingAllTime>0&&System.currentTimeMillis()-mLoadingAllTime<=30*1000){
            hideLoadToast();
            mSingleBtnDialog.showNetworkTips(4 * 1000,false, new GLDialogViewSingleBtn.IGLDismissListener() {
                @Override
                public void dismiss() {
                    if(mStartTipsTime>0){
                        showLoadToast(true);
                    }
                }
            });
            showDialogView(2);
        }
        /**loading超过1分钟显示网络加载失败*/
        if (mLoadingTime != -1 && System.currentTimeMillis() - mLoadingTime >60000 ) {
            doNetChanged(true);
            hideLoading();
        }
    }

//
//    /**
//     * 页面焦点事件
//     */
//    protected GLViewFocusListener pageFocusListener = new GLViewFocusListener() {
//        @Override
//        public void onFocusChange(GLRectView glRectView, boolean b) {
//            if(b){
////				if(!mTimeOutDialog.isVisible()) {
//                mActivity.hideCursorView();
////				}
//            }else {
//                mActivity.showCursorView();
//            }
//        }
//    };


    /**
     * 页面按键事件处理
     */
    protected GLOnKeyListener pageKeyListener = new GLOnKeyListener() {

        @Override
        public boolean onKeyUp(GLRectView view, int keycode) {
            return false;
        }

        @Override
        public boolean onKeyLongPress(GLRectView view, int keycode) {
            return false;
        }

        @Override
        public boolean onKeyDown(GLRectView view, int keycode) {

            switch (keycode) {
                case MojingKeyCode.KEYCODE_DPAD_LEFT://快退
                    return true;
                case MojingKeyCode.KEYCODE_DPAD_RIGHT://快进
                    return true;
                case MojingKeyCode.KEYCODE_BACK:
                    return true;
                case MojingKeyCode.KEYCODE_MENU:

                    return true;

                default:
                    break;
            }

            return false;
        }
    };

    protected void setHDData(List<String> hdtext,String cur){
        String[] arry = (String[])hdtext.toArray(new String[hdtext.size()]);
            moviePlayerSettingView.setHDdata(arry, cur);
    }



    /**
     * 底部控制栏
     */
    protected void createPlayerControlView() {
        mControlLayer = new GLRelativeView(mActivity);
        mControlLayer.setLayoutParams(2400,2400);
        mControlLayer.setHandleFocus(false);
        moviePlayerControlView = new MoviePlayerControlView(mActivity);
        moviePlayerControlView.setType(mPageType);
//        moviePlayerControlView.setX( 1200f-500f);
//        moviePlayerControlView.setY(1320f);
        moviePlayerControlView.setMargin(1200-500,1320f,0,0);
        moviePlayerControlView.setIPlayerControlCallBack(this);
        mControlLayer.addView(moviePlayerControlView);
        mControlLayer.setDepth(GLConst.Player_Controler_Depth,GLConst.Player_Controler_Scale);
        mRootView.addView(mControlLayer);
        moviePlayerControlView.setVisible(true);
    }
    protected void createPlayerSettingView() {
        mSettingLayer = new GLRelativeView(mActivity);
        mSettingLayer.setLayoutParams(2400,2400);
        mSettingLayer.setHandleFocus(false);
        moviePlayerSettingView = new MoviePlayerSettingView(mActivity);
        moviePlayerSettingView.initView(MoviePlayerSettingView.MOVIE);
//        moviePlayerSettingView.setX(1200f-500f);
//        moviePlayerSettingView.setY(330);
        moviePlayerSettingView.setMargin(1200-500,0,0,1090);
//        String[] strs = new String[]{"自动", "蓝光", "1080P", "720P", "480P"};
//        moviePlayerSettingView.setHDdata(strs,"自动");
        moviePlayerSettingView.setIPlayerSettingCallBack(settingCallBack);
        mSettingLayer.addViewBottom(moviePlayerSettingView);
        mSettingLayer.setDepth(GLConst.Player_Settings_Depth,GLConst.Player_Settings_Scale);
        mRootView.addView(mSettingLayer);
        //在addview后设置下进度初始位置，这样可以避免第一次显示0进度，icon位置偏下
        boolean ismute = SettingSpBusiness.getInstance().getPlayerSoundMute();
        moviePlayerSettingView.setSoundIcon(!ismute);
    }

    protected void createToastView() {
        GLRelativeView layer = new GLRelativeView(mActivity);
        layer.setLayoutParams(2400,2400);
        layer.setHandleFocus(false);
        glDialogView = new GLDialogView2(mActivity);
        glDialogView.setMargin(1200f-400,1200f-100,0f,0f);
//        glDialogView.setDepth(GLConst.Dialog_Depth,GLConst.Dialog_Scale);
        layer.addView(glDialogView);
        glDialogView.setVisible(false);

        mSingleBtnDialog = new GLDialogViewSingleBtn(mActivity);
        mSingleBtnDialog.setMargin(1200f-400,1200f-100,0f,0f);
//        mSingleBtnDialog.setDepth(GLConst.Dialog_Depth,GLConst.Dialog_Scale);
        layer.addView(mSingleBtnDialog);
        mSingleBtnDialog.setVisible(false);

        unLockDialog = new GLUnLockDialog(mActivity);
        unLockDialog.setMargin(1200f-400,1200f-100,0f,0f);
//        unLockDialog.setDepth(GLConst.Dialog_Depth,GLConst.Dialog_Scale);
        layer.addView(unLockDialog);
        unLockDialog.setVisible(false);


        glTextToast = new GLTextToast(mActivity);
        glTextToast.setMargin(1200f-200,1200-100f,0f,0f);

//        glTextToast.showToast("切换中",GLTextToast.SHORT);
//        glTextToast.showToast("已为您切换到1080P清晰度",GLTextToast.MEDIUM);
//        glTextToast.showToast("网络原因，已为您切换到1080P清晰度",GLTextToast.LONG);
        layer.addView(glTextToast);
        glTextToast.setVisible(false);

        //显示加载中的loading
        glLoadingToast = new GLLoadingToast(mActivity);
        glLoadingToast.setMargin(1200f-270/2,1200f-80,0f,0f);
        layer.addView(glLoadingToast);
        glLoadingToast.setVisible(false);

        //首次播放显示即将播放和续播toast
        glLoadToast = new GLLoadToast(mActivity);
        glLoadToast.setMargin(1200f-525/2,1200f-80,0f,0f);
        layer.addView(glLoadToast);
        glLoadToast.setVisible(true);

        layer.setDepth(GLConst.Dialog_Depth,GLConst.Dialog_Scale);
        mRootView.addView(layer);
        setListener();
    }

    public boolean getLoadToastVisiable(){
        return glLoadToast.isVisible()||glLoadingToast.isVisible();
    }

    public void showLoadToast(boolean loading){
        if(glTextToast!=null&&glTextToast.isVisible()){
            delay_showloadin_flag = true;
            return;
        }
        if(loading){
            glLoadingToast.setVisible(true);
            glLoadToast.setVisible(false);
        }else {
            glLoadingToast.setVisible(false);
            glLoadToast.setVisible(true);
        }

    }

    public void hideLoadToast(){
        delay_showloadin_flag = false;
        if(glLoadingToast!=null){
            glLoadingToast.setVisible(false);
        }
        if(glLoadToast!=null){
            glLoadToast.setVisible(false);
        }
    }

    public void hideDialogView(){
        if(glDialogView!=null){
            glDialogView.setVisible(false);
        }
        if(mSingleBtnDialog!=null){
            mSingleBtnDialog.setVisible(false);
        }
    }
    public void showDialogView(int type){
        if(type==1){//双按钮
           if(glDialogView!=null){
               glDialogView.setVisible(true);
           }
            if(mSingleBtnDialog!=null){
                mSingleBtnDialog.setVisible(false);
            }
        }else if(type==2){//单按钮
            if(glDialogView!=null){
                glDialogView.setVisible(false);
            }
            if(mSingleBtnDialog!=null){
                mSingleBtnDialog.setVisible(true);
            }
        }
    }

    public boolean isDialogShowing(){
       return mSingleBtnDialog.isVisible()||glDialogView.isVisible();
    }



    public void setListener(){
        glDialogView.setLeftKeyListener(new GLOnKeyListener() {  //退出
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                (mActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BasePlayerPage.this.finish();
                        mActivity.finish();
                    }
                });

                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        glDialogView.setRightKeyListener(new GLOnKeyListener() { //继续播放 或 重新播放
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                doExceptionRePlay();
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });

        glDialogView.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                    if(focused) {
                        ((GLBaseActivity)getContext()).showCursorView();
                    } else {
                        ((GLBaseActivity)getContext()).hideCursorView2();
                    }
            }
        });
        unLockDialog.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(focused) {
                    ((GLBaseActivity)getContext()).showCursorView();
                } else {
                    ((GLBaseActivity)getContext()).hideCursorView2();
                }
            }
        });

        glTextToast.setOnToastDismisListener(new GLTextToast.IToastDismisListener() {
            @Override
            public void onDismiss() {
                 if(delay_showloadin_flag){
                     showLoading();
                 }
            }
        });

        moviePlayerSettingView.setOnViewVisiableListener(new IViewVisiableListener() {
            @Override
            public void onVisibility(boolean isvisible) {
                if(!isvisible){
                    if(mSettingLayer.getDepth()!=GLConst.Player_Settings_Depth){
                        startAnim(true);
                    }
                }else {
                    startAnim(false);
                }
            }
        });
        moviePlayerControlView.setOnViewVisiableListener(new IViewVisiableListener() {
            @Override
            public void onVisibility(boolean isvisible) {
                if(!isvisible){
                     if(mControlLayer.getDepth()!=GLConst.Player_Controler_Depth){
                         startAnim(true);
                     }
                }
            }
        });


    }

    private void startAnim(boolean isReset){
        Log.d("login","---startAnim isRest = "+isReset);
        if(isReset){
//            mControlLayer.setDepth(GLConst.Player_Controler_Depth);
//            mSettingLayer.setDepth(GLConst.Player_Settings_Depth);
            mControlLayer.setDepth(mControlLayer.getDepth()-anim_depth);
            mSettingLayer.setDepth(mSettingLayer.getDepth()+anim_depth);
            Log.d("login","---startAnim mSettingLayer = "+mSettingLayer.getDepth()+",mControlLayer depth = "+mControlLayer.getDepth());
            return;
        }
        //基础面板后退放大  高级面板迁移缩小
        mControlLayer.setDepth(mControlLayer.getDepth()+anim_depth);
        mSettingLayer.setDepth(mSettingLayer.getDepth()-anim_depth);

        Log.d("login","---startAnim mSettingLayer = "+mSettingLayer.getDepth()+",mControlLayer depth = "+mControlLayer.getDepth());
    }


    @Override
    public void onPlayChanged(boolean status) {
        if(!status){ //播放
            startPlay();
        }else {//暂停
            pausePlay();
        }
    }

    @Override
    public void onSeekToChanged(int curPosition) {
        if(curPosition>=0) {
            mPrepareSeekTo = curPosition;
            pausePlay();
            seekTo(mPrepareSeekTo);
        }
    }

    @Override
    public void onControlChanged(String id, boolean selectedStatus) {
        if(MoviePlayerControlView.SETTING.equals(id)) {
            moviePlayerSettingView.hideAllView();//全部高级菜单子view隐藏
            moviePlayerSettingView.setMovieSettingViewShow(selectedStatus);
        } else if(MoviePlayerControlView.SELECTED_SOURCE.equals(id)) {
            moviePlayerSettingView.hideAllView();//全部高级菜单子view隐藏
            moviePlayerSettingView.setSelectSourceViewShow(selectedStatus);
        } else if(MoviePlayerControlView.SOUND.equals(id)) {
            moviePlayerSettingView.hideAllView();//全部高级菜单子view隐藏
            moviePlayerSettingView.setSoundBarViewShow(selectedStatus);
        } else if(MoviePlayerControlView.HD.equals(id)) {
            moviePlayerSettingView.hideAllView();//全部高级菜单子view隐藏
            moviePlayerSettingView.setHDTypeViewShow(selectedStatus);
        }


    }

    @Override
    public void onHideControlAndSettingView(boolean isHide) {
        if(isHide) {
            hideViewTimer();
        } else {
            cancelHideViewTimer();
        }
    }

    private Timer timer;

    public void setDelayVisiable(int duration){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //全部高级菜单子view隐藏
                moviePlayerSettingView.hideAllView();
                //隐藏控制菜单
                moviePlayerControlView.hideAllView();
                //菜单失去焦点隐藏，要设置底部按钮关闭
                ((GLBaseActivity)getContext()).showClose();
                doOpenMenu(false);
            }
        };
        timer.schedule(task,duration);
    }

    public void hideViewTimer(){
        setDelayVisiable(2*1000);
    }

    public void cancelHideViewTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    private IPlayerSettingCallBack settingCallBack = new IPlayerSettingCallBack(){

        @Override
        public void onSettingShowChange(String id,boolean isShow) {

        }

        @Override
        public void onHideControlAndSettingView(boolean isHide) {
            if(isHide) {
                hideViewTimer();
            } else {
                cancelHideViewTimer();
            }
        }

        @Override
        public void onHDChange(String hd) {
              onChangeHd(hd);
        }

        @Override
        public void onSoundChange(int vm) {
            if(vm>0){
                SoundUtils.SetVolumeMute(false);
                SettingSpBusiness.getInstance().setPlayerSoundMute(false);
            }
            SoundUtils.SetSoundVolume(vm);
            if(moviePlayerSettingView!=null){
                moviePlayerSettingView.setVolume(vm);
            }
        }

        @Override
        public void isOpenSound(boolean isOpen) {
            SoundUtils.SetVolumeMute(!isOpen);
            SettingSpBusiness.getInstance().setPlayerSoundMute(!isOpen);
            if(moviePlayerSettingView!=null){
                moviePlayerSettingView.setSoundIcon(isOpen);
            }
        }

        @Override
        public void onSelected(int num) {
            onChangeSelectIndex(num);
        }
    };


    public void updateVolumChange(final int keyCode){
        MJGLUtils.exeGLQueueEvent(getContext(), new Runnable() {
            @Override
            public void run() {
                if(KeyEvent.KEYCODE_VOLUME_MUTE == keyCode){
                    if(moviePlayerSettingView!=null) {
                        moviePlayerSettingView.setSoundIcon(!SoundUtils.isVolumeMute());
                        SettingSpBusiness.getInstance().setPlayerSoundMute(SoundUtils.isVolumeMute());
                    }
                } else if(keyCode==MojingKeyCode.KEYCODE_DPAD_DOWN||keyCode==MojingKeyCode.KEYCODE_DPAD_UP){
                    int sound = SoundUtils.GetCurrentVolumePercent();
                    sound = keyCode==MojingKeyCode.KEYCODE_DPAD_DOWN?sound-10:sound+10;
                    sound = sound>100?100:sound<0?0:sound;
                    sound=sound<0?0:sound/10;
                    SoundUtils.SetSoundVolume(sound*10);
                    if(moviePlayerSettingView!=null) {
                        moviePlayerSettingView.setVolume(sound);
                    }

                }
                else {
                    if(moviePlayerSettingView!=null) {
                        moviePlayerSettingView.setSoundIcon(true);
                    }
                    int sound = SoundUtils.GetCurrentVolumePercent();
                    sound=sound<0?0:sound;
                    if(sound==0&&KeyEvent.KEYCODE_VOLUME_DOWN==keyCode){
                        SoundUtils.SetSoundVolume(0);
                    }
                    if(moviePlayerSettingView!=null) {
                        moviePlayerSettingView.setVolume(sound);
                    }
                }
            }
        });
    }

}
