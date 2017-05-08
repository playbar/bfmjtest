package com.baofeng.mj.business.videoplayer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import com.baofeng.mj.ui.view.MojingVideoGvrView;
import com.baofeng.mj.util.publicutil.CachedThreadPoolUtil;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.MojingSDKReport;
import com.baofeng.mojing.MojingSDKServiceManager;
import com.baofeng.mojing.MojingSurfaceView;
import com.baofeng.mojing.MojingVrLib;
import com.google.vr.sdk.base.GvrView;
import com.mojing.sdk.pay.widget.mudoles.ManufacturerList;
import com.storm.bfprotocol.core.ProtocolSystem;
import com.storm.smart.core.PlayerCore;

/**
 * Created by liuyunlong on 2016/6/22.
 */
public class MojingSDKHandler {

    static MojingSDKHandler s_instance;
    public static MojingSDKHandler getInstance()
    {
        if(s_instance == null)
            s_instance = new MojingSDKHandler();

        return s_instance;
    }

    Activity m_activity;
    MojingSDKServiceManager mMojingSDKServiceManager;
    private MojingVideoGvrView mView;

    public void InitSDK(Activity activity)
    {
        InitSDK(activity,null);
    }
    public void InitSDK(Activity activity,String glassKey){
        m_activity = activity;
        mMojingSDKServiceManager = new MojingSDKServiceManager();
        MojingSDK.Init(activity);
        mView = new MojingVideoGvrView(activity);
        mView.requestFocus();
        mView.setFocusableInTouchMode(true);

        mView.requestFocus(); // get the focus.
        mView.setFocusableInTouchMode(true); // enable touchable.
    }
    public MojingVideoGvrView getView()
    {
        return mView;
    }
    public void setRender(GvrView.StereoRenderer r)
    {
        mView.setRenderer(r);
        mView.onResume();
    }

    public void onPause() {
        //mView.onPause();
        //this.mMojingSDKServiceManager.onPause(m_activity);
        MojingVrLib.stopVsync(m_activity);
        MojingSDKReport.onPause(m_activity);
    }

    public void onResume() {
       //this.mMojingSDKServiceManager.onResumeNoTrackerMode(m_activity);
        MojingVrLib.startVsync(m_activity);

        MojingSDKReport.onResume(m_activity);
    }

    public void onDestroy(){
       //mView.getHolder().removeCallback(mView);
        m_activity = null;
        if (mView != null) {
            mView.clearFocus();
            mView = null;
        }
        ProtocolSystem.getInstance(null);
        PlayerCore.getInstance(null);
        CachedThreadPoolUtil.runThread(new Runnable() {
            @Override
            public void run() {
                MojingSDK.AppExit();
            }
        });

    }
}
