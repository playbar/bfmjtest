package com.bfmj.viewcore.interfaces;

import com.bfmj.viewcore.view.GLPlayerView;

/**
 * 
 * ClassName: IGLPlayerListener <br/>
 * @author lixianke    
 * @date: 2015-4-3 上午11:00:23 <br/>  
 * description:
 */
public interface IGLPlayerListener {
	void onPrepared(IGLPlayer player);
	boolean onInfo(IGLPlayer player, int what, Object extra);
	void onBufferingUpdate(IGLPlayer player, int percent);
	void onCompletion(IGLPlayer player);
	void onSeekComplete(IGLPlayer player);
	boolean onError(IGLPlayer player, int what, int extra);
	void onVideoSizeChanged(IGLPlayer player, int width, int height);
	void onTimedText(IGLPlayer player, String text);
}
