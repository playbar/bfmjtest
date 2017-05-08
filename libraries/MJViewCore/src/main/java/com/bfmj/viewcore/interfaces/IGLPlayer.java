package com.bfmj.viewcore.interfaces;

/**
 * Created by lixianke on 2017/3/27.
 */

public interface IGLPlayer {
    void setVideoPath(String path);
    void start();
    void pause();
    void stop();
    void releasePlay();
    void seekTo(int pos);
    int getCurrentPosition();
    int getDuration();
    boolean isPlaying();
}
