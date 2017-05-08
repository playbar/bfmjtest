package com.baofeng.mj.business.videoplayer;

import com.storm.smart.play.call.BaofengPlayerListener;
import com.storm.smart.play.call.IBaofengPlayer;

/**
 * Created by liuyunlong on 2016/6/22.
 */
public class BFPlayerListener implements BaofengPlayerListener
{

    @Override
    public void onPrepared(IBaofengPlayer iBaofengPlayer) {

    }

    @Override
    public void onCompletion(IBaofengPlayer iBaofengPlayer) {

    }

    @Override
    public void onError(IBaofengPlayer iBaofengPlayer, int i) {

    }

    @Override
    public void onInfo(IBaofengPlayer iBaofengPlayer, int i, Object o) {

    }

    @Override
    public void onSeekToComplete(IBaofengPlayer iBaofengPlayer) {

    }

    @Override
    public boolean onSwitchPlayer(IBaofengPlayer iBaofengPlayer, Object o, int i) {
        return false;
    }

    @Override
    public String getCompleteUrl(String s) {
        return null;
    }

    @Override
    public boolean isCodecLibraryInstalled() {
        return false;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void onRawVideoDataUpdate() {

    }

    @Override
    public String getSite() {
        return null;
    }

    @Override
    public void onPlayerStop() {

    }

    @Override
    public void onP2pLocalToOnline() {

    }

    @Override
    public void onVideoInfo(int i, int i1, int i2, int i3, int i4, int i5, int i6) {

    }
}
