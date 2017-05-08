package com.baofeng.mj.vrplayer.page;

import android.content.Context;

import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.vrplayer.interfaces.IPlayerSettingCallBack;
import com.baofeng.mj.vrplayer.interfaces.IResetLayerCallBack;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.view.MoviePlayerControlView;
import com.baofeng.mj.vrplayer.view.MoviePlayerSettingView;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLGroupView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2017/4/5.
 */

public class TestPage extends GLViewPage {

    private Context mContext;
    private GLGroupView indexRootView;

    private MoviePlayerControlView moviePlayerControlView;
    private MoviePlayerSettingView moviePlayerSettingView;

    public TestPage(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected GLRectView createView(GLExtraData data) {
        indexRootView = new GLGroupView(mContext);
//        indexRootView.setLayoutParams(2400,2400);
//        indexRootView.setDepth(GLConst.Player_Controler_Depth,GLConst.Player_Controler_Scale);
        //创建播放控制view
        createPlayerControlView();

        //创建设置菜单
        createPlayerSettingView();
        String[] strs = new String[]{"自动", "蓝光", "1080P", "720P", "480P"};
        moviePlayerSettingView.setHDdata(strs,"自动");
        //创建提示菜单
        createToastView();

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
//        moviePlayerSettingView.setMovieVideoDatas(new VideoDetailBean(),0);
        return indexRootView;
    }

    private void createToastView() {
//        GLTextToast glTextToast = new GLTextToast(mContext);
//        glTextToast.setMargin(1200f,1200f,0f,0f);
//        glTextToast.showToast("切换中",GLTextToast.SHORT);
//        glTextToast.showToast("已为您切换到1080P清晰度",GLTextToast.MEDIUM);
//        glTextToast.showToast("网络原因，已为您切换到1080P清晰度",GLTextToast.LONG);
//        indexRootView.addView(glTextToast);

//        GLImageToast glImageToast = new GLImageToast(mContext);
//        glImageToast.setMargin(1200f,1200f,0f,0f);
//        glImageToast.showToast(R.drawable.play_icon_function_model_click);
//        indexRootView.addView(glImageToast);

//        GLSeekToast glSeekToast = new GLSeekToast(mContext);
//        glSeekToast.setMargin(1200f,1200f,0f,0f);
//        glSeekToast.showToast("360:54","120:45", R.drawable.play_toast_icon_fastforward);
//        indexRootView.addView(glSeekToast);

//        GLProcessToast glProcessView = new GLProcessToast(mContext);
//        glProcessView.setMargin(1200f,1200f,0f,0f);
//        glProcessView.setSpeedText("1020KB/S");
//        indexRootView.addView(glProcessView);

//        GLDialogView2 glDialogView2 = new GLDialogView2(mContext);
//        glDialogView2.setMargin(1200f,1200f,0f,0f);
//        indexRootView.addView(glDialogView2);

//        GLLoadingToast glLoadingToast = new GLLoadingToast(mContext);
//        glLoadingToast.setMargin(1200f,1200f,0f,0f);
//        glLoadingToast.setVisible(true);
//        indexRootView.addView(glLoadingToast);

//        GLLoadToast glLoadToast = new GLLoadToast(mContext);
//        glLoadToast.setMargin(1200f,1200f,0f,0f);
////        glLoadToast.setText("爱乐之城",1);
////        glLoadToast.setText("00:00:00",2);
//        glLoadToast.setVisible(true);
//        indexRootView.addView(glLoadToast);
    }

    private void createPlayerSettingView() {
        GLRelativeView layer = new GLRelativeView(mContext);
        layer.setLayoutParams(2400,2400);
        layer.setHandleFocus(false);
        moviePlayerSettingView = new MoviePlayerSettingView(mContext);
        moviePlayerSettingView.initView(MoviePlayerSettingView.MOVIE);
//        moviePlayerSettingView.setX(1200f-500f);
//        moviePlayerSettingView.setY(330);
        moviePlayerSettingView.setMargin(1200-500,0,0,1070);
//        String[] strs = new String[]{"自动", "蓝光", "1080P", "720P", "480P"};
//        moviePlayerSettingView.setHDdata(strs,"自动");
        moviePlayerSettingView.setIPlayerSettingCallBack(settingCallBack);
        layer.addViewBottom(moviePlayerSettingView);
        layer.setDepth(GLConst.Player_Settings_Depth,GLConst.Player_Settings_Scale);
        indexRootView.addView(layer);
        moviePlayerSettingView.setVolume(0);//在addview后设置下进度初始位置，这样可以避免第一次显示0进度，icon位置偏下
    }

    private void createPlayerControlView() {
        GLRelativeView layer = new GLRelativeView(mContext);
        layer.setLayoutParams(2400,2400);
        layer.setHandleFocus(false);
        moviePlayerControlView = new MoviePlayerControlView(mContext);
        moviePlayerControlView.setType(MoviePlayerSettingView.MOVIE);
//        moviePlayerControlView.setX( 1200f-500f);
//        moviePlayerControlView.setY(1320f);
        moviePlayerControlView.setMargin(1200-500,1320f,0,0);
        moviePlayerControlView.setIPlayerControlCallBack(controlCallBack);
        layer.addView(moviePlayerControlView);
        layer.setDepth(GLConst.Player_Controler_Depth,GLConst.Player_Controler_Scale);
        indexRootView.addView(layer);
        moviePlayerControlView.setVisible(true);
        moviePlayerControlView.setProcess(0);//在addview后设置下进度初始位置，这样可以避免第一次显示0进度，icon位置偏下
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

        }

        @Override
        public void onSoundChange(int vm) {

        }

        @Override
        public void isOpenSound(boolean isOpen) {

        }

        @Override
        public void onSelected(int num) {

        }
    };

    private IPlayerControlCallBack controlCallBack = new IPlayerControlCallBack() {

        @Override
        public void onPlayChanged(boolean status) {

        }

        @Override
        public void onSeekToChanged(int curPosition) {

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
    };

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
//				MJGLUtils.exeGLQueueEvent(this, new Runnable() {
//					@Override
//					public void run() {
                //全部高级菜单子view隐藏
                moviePlayerSettingView.hideAllView();
                //隐藏控制菜单
                moviePlayerControlView.hideAllView();

                ((GLBaseActivity)getContext()).showClose();
//					}
//				});
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
}
