package com.baofeng.mj.ui.online.utils;

import android.app.Activity;
import android.content.Context;

import com.bfmj.sdk.util.MD5;
import com.storm.smart.play.baseplayer.BaseSurfacePlayer;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.call.PlayerWithoutSurfaceFactory;

public class MediaHelp {
    public static BaseSurfacePlayer mPlayer;

    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static int mState = STATE_IDLE;

    public static int decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;
    private static String videoPath;


    public static void doSDKMedia(Context mContext, String videoPath) {
        try {
            MediaHelp.videoPath = videoPath;
            mPlayer.setVideoID(MD5.getMD5(MediaHelp.videoPath));
            if (!mPlayer.setVideoPath(MediaHelp.videoPath)) {
                errorToChangeSoft(mContext);
            }
            mState = STATE_PREPARING;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BaseSurfacePlayer createPlayer(Context mContext) {
        if (mPlayer == null) {
            mPlayer = PlayerWithoutSurfaceFactory.createPlayer((Activity) mContext, decodeType,false);
        }
        return mPlayer;
    }

    /**
     * 播放失败切换到软解
     */
    public static void errorToChangeSoft(Context mContext) {
        decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;
        release();
        createPlayer(mContext);
        doSDKMedia(mContext, MediaHelp.videoPath);
    }

    public static void errorToChangeSys(Context mContext) {
        decodeType = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
        release();
        createPlayer(mContext);
        doSDKMedia(mContext, MediaHelp.videoPath);
    }


    /**
     * MediaPlayer release
     */
    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mState = STATE_IDLE;
        }
    }

}
