package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.business.mediaplayerbusiness.PlayerBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.online.view.BaseSensorView;
import com.baofeng.mj.ui.online.view.PlayerActivityTitleView;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.systemutil.AudioManagerUtil;

/**
 * Created by wanghongfang on 2017/1/22.
 */
public abstract class BasePlayerActivity  extends BaseStickActivity implements View.OnClickListener {
        protected ViewGroup contentView;
        protected EmptyView emptyView;
        protected RelativeLayout titleBgLayout;
        protected PlayerActivityTitleView titleBackView;
        protected CustomProgressView progressView;

    private boolean isDouble = false;
    //隐藏音量
    private static final int WHAT_HIDE_VOLUME = 1004;
    private ProgressBar left_vloum_view;
    private ProgressBar right_vloum_view;
    public BaseSensorView mPlayerView;
    private View mVloumView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_baseview);
            findViews();
        }

    @Override
    public void startCheck() {
        if(titleBackView==null)
            return;
        if (BaseApplication.INSTANCE.isBFMJ5Connection() && BaseApplication.INSTANCE.getJoystickConnect()) { //魔镜5代usb连接，并且遥控器连接上
            titleBackView.connectManager(true);
        } else if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            titleBackView.connectManager(false);
        } else if (!StickUtil.isBondBluetooth()) {// 蓝牙与魔镜设备未配对
            titleBackView.connectManager(false);
        } else if (!StickUtil.isConnected) {// 设备未开启或者设备休眠
            titleBackView.connectManager(false);
        } else {// 已连接
            titleBackView.connectManager(true);
        }
    }
    IPlayerMojingInputCallBack mojingInputCallBack;
    public  void setPlayerMojingInputCallBack(IPlayerMojingInputCallBack  callBack){
        mojingInputCallBack = callBack;
    }

    @Override
    public boolean onMojingKeyLongPress(String s, int i) {

        if(!isDouble){
            return super.onMojingKeyDown(s, i);
        }

        return super.onMojingKeyLongPress(s, i);
    }

    @Override
    public boolean onMojingKeyDown(String s, int i) {
        if(!isDouble){
            return super.onMojingKeyDown(s, i);
        }
        if(mojingInputCallBack!=null){
            mojingInputCallBack.onMojingKeyDown(s,i);
        }
        if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_UP) {
            increaseVolume();
        } else if (i == PlayerBusiness.getInstance().KEY_BLUETOOTH_DOWN) {
            decreaseVolume();
        }
        return super.onMojingKeyDown(s, i);
    }

    @Override
    public boolean onMojingKeyUp(String s, int i) {
        if(!isDouble){
            return super.onMojingKeyUp(s, i);
        }
        if(mojingInputCallBack!=null ){
            mojingInputCallBack.onMojingKeyUp(s,i);
        }
        return super.onMojingKeyUp(s, i);
    }

    public void setTitle(String title){
            titleBackView.getNameTV().setText(title);
        }

        private void findViews(){
            contentView = (ViewGroup) findViewById(R.id.base_contentview);
            emptyView = (EmptyView) findViewById(R.id.base_empty_view);
            emptyView.getRefreshView().setOnClickListener(this);

            titleBgLayout = (RelativeLayout) findViewById(R.id.base_title_layout);
            findViewById(R.id.base_title).setVisibility(View.GONE);
            titleBackView = (PlayerActivityTitleView) findViewById(R.id.player_activity_title);

            progressView = (CustomProgressView) findViewById(R.id.base_loading);
            contentView.addView(View.inflate(this, getContentView(), null)); //填充具体内容布局
            init();
        }

        /**
         * 显示子类页面
         */
        public void showContentView(){
            contentView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        /**
         * 隐藏子类页面
         */
        public void hideContent(){
            contentView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
        /**
         * ProgressView 隐藏
         */
        public void dismissProgressDialog(){
            progressView.setVisibility(View.GONE);
        }

        /**
         * ProgressView 显示
         */
        public void showProgressDialog(){
            progressView.setVisibility(View.VISIBLE);
        }

        /**
         * ProgressView 显示指定文字
         * @param message
         */
        public void showProgressDialog(String message) {
            progressView.setVisibility(View.VISIBLE);
            progressView.setMessage(message);
        }

        @Override
        public void onClick(View view) {
        }

        protected abstract int getContentView();


    protected void init(){
        mVloumView =  LayoutInflater.from(this).inflate(R.layout.player_volume_layout,null);
        contentView.addView(mVloumView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        left_vloum_view = (ProgressBar) mVloumView.findViewById(R.id.pb_remote_player_left_volume);
        right_vloum_view = (ProgressBar) mVloumView.findViewById(R.id.pb_remote_player_right_volume);
        mVloumView.setVisibility(View.GONE);

    }

    public void changePlayerScreen(boolean isdouble){
        this.isDouble = isdouble;
        titleBackView.setScreenDouble(isdouble);
        if(!isdouble){
            mVloumView.setVisibility(View.GONE);
        }

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
        setRemoteControllerVisibility(mVloumView,WHAT_HIDE_VOLUME);
        int currentVolume = AudioManagerUtil.getInstance().getStreamCurrentVolume();
        left_vloum_view.setProgress(currentVolume);
        right_vloum_view.setProgress(currentVolume);
    }

    private void setRemoteControllerVisibility(View view, int what) {
        if(mPlayerView!=null&&mPlayerView.loadingView!=null&&mPlayerView.loadingView.getVisibility()==View.VISIBLE)
        {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(what);
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = view;
        mHandler.sendMessageDelayed(msg, 3000);
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_HIDE_VOLUME:
                    hideVloumView();
                    break;
                default:
                    break;
            }
        }
    };

    public void hideVloumView(){
        if(mVloumView!=null){
            mVloumView.setVisibility(View.GONE);
        }
    }


    public interface IPlayerMojingInputCallBack{
        void onMojingKeyUp(String s, int i);
        void onMojingKeyDown(String s, int i);
    }
}
