package com.bfmj.sdk.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by wangfuzheng on 2015/12/11.
 */
public class PlayMp3Util {

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mMp3Path = "";
    public PlayMp3Util(Context context){
        this.mContext = context;
        mMediaPlayer= new MediaPlayer();
    }

    public void setMp3(String mp3Path){
        mMp3Path = mp3Path;
        play(mp3Path);
    }

    public void play(String mp3Path){
        try {
            // 停止
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            // 重置参数
            mMediaPlayer.reset();

            mMediaPlayer.setDataSource(mp3Path);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });

            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay(){
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();//停止播放
            mMediaPlayer.release();//释放资源
            mMediaPlayer=null;
        }
    }
}
