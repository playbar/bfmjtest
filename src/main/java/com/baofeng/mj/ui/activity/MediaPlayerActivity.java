package com.baofeng.mj.ui.activity;

import android.os.Bundle;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.MediaPlayerView;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

/**
 * Created by muyu on 2017/3/22.
 */
public class MediaPlayerActivity extends BaseActivity {

    private MediaPlayerView playerView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);
        initView();
    }

    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private void initView(){
        if(getIntent() != null){
            path = getIntent().getStringExtra("play_url");
        }
        playerView = (MediaPlayerView) findViewById(R.id.video_game_mediaplay_view);
        playerView.setVideoPath(path);
        playerView.setIsAutoPlay(true);
        playerView.setMaximize(false);
        playerView.setVideoPlayerManager(mVideoPlayerManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView.setPlayPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.onDestroyView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mVideoPlayerManager != null) {
            mVideoPlayerManager.resetMediaPlayer();
        }
    }
}
