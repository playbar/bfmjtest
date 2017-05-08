package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

/**
 * Created by wanghongfang on 2017/1/20.
 * 检测
 */
public abstract class BaseSensorView extends RelativeLayout  {
    private static final int HANDLER_CODE = 888;
    private Activity activity;
    public LoadingView loadingView;
    private static final String TAG = BaseSensorView.class.getSimpleName();
    private OrientationEventListener mLandOrientationListener;
    private WeakReference<Activity> mActivityWeakRef;
    // 是否是竖屏
    private boolean isPortrait = true;
    public BaseSensorView(Context context) {
        super(context);
        activity = (Activity)context;
        init();
    }

    public BaseSensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity)context;
        init();

    }
    protected void init(){
        MySensorHelper(activity);
    }

    public void onResum(){
        enable();
    }
    public void onPause(){
        disable();
    }

    public void onDestory(){
        disable();
    }

    protected abstract void changeToLand();
    public abstract void hideLoading();
    public abstract void showLoading();

    /**
     * 横屏状态下停止检测
     * @param island
     */
    public void changePlayerScreen(boolean island){
        if(island){
            disable();
            isPortrait = !island;
        }else {
            enable();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_CODE:
                    int orientation = msg.arg1;
                    if (orientation > 225 && orientation < 280) {
                        if(activity==null||activity.isFinishing()){
                            return;
                        }
                       changeToLand();
                    }

                    break;

                default:
                    break;
            }
        }
    };


    public void MySensorHelper(final Activity activity) {
        this.mActivityWeakRef = new WeakReference(activity);
        this.mLandOrientationListener = new OrientationEventListener(activity, 3) {
            public void onOrientationChanged(int orientation) {
                if(orientation < 100 && orientation > 80 || orientation < 280 && orientation > 260) {
                    if (!isPortrait) {
                        return;
                   }
                    isPortrait = false;
                    if (mHandler != null) {
                        mHandler.obtainMessage(HANDLER_CODE, orientation, 0).sendToTarget();
                    }
                }

                if(orientation < 10 || orientation > 350 || orientation < 190 && orientation > 170) {
                    isPortrait = true;
                }

            }
        };
    }
    //禁用切换屏幕的开关
    public void disable() {
        this.mLandOrientationListener.disable();
    }
    //开启横竖屏切换的开关
    public void enable(){
        this.mLandOrientationListener.enable();
    }

}
