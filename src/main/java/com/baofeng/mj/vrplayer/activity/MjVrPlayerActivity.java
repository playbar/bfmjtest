package com.baofeng.mj.vrplayer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.vrplayer.page.BasePlayerPage;
import com.baofeng.mj.vrplayer.page.MoviePlayPage;
import com.baofeng.mj.vrplayer.page.PanoNetPlayPage;
import com.baofeng.mj.vrplayer.page.SplashPage;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLViewPage;
import com.google.vr.ndk.base.AndroidCompat;

/**
 * Created by wanghongfang on 2017/4/1.
 * 魔镜VR模式播放activity
 */
public class MjVrPlayerActivity extends GLBaseActivity {

    public static PanoramaVideoBean panoramaVideoBean;
    public static VideoDetailBean VideoBean;
    private int mType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setImmersiveSticky();
        this.getWindow()
                .getDecorView()
                .setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    setImmersiveSticky();
                                }
                            }
                        });

//        AndroidCompat.setSustainedPerformanceMode(this, true);
//        AndroidCompat.setVrModeEnabled(this, true);
        mType = getIntent().getExtras().getInt("type");
//        getPageManager().push(new TestPage(this), null);
        init();
    }

    private void init() {
        if(mType != 2 && !SettingSpBusiness.getInstance().getVrGuide()) {
            getPageManager().push(new SplashPage(this), null);
        } else {
            startPlayPage();
        }

    }

    public void startPlayPage(){
        GLExtraData playExtraData = new GLExtraData();
        if(getIntent().getExtras()!=null){

            playExtraData.putExtraObject("index",getIntent().getExtras().getInt("index"));
            playExtraData.putExtraString("detail_url",getIntent().getExtras().getString("detail_url"));
        }
        if(mType == 1){//影院
            playExtraData.putExtraObject("videobean",VideoBean);
            getPageManager().push(new MoviePlayPage(this), playExtraData);
        }else if(mType == 2){//全景
            playExtraData.putExtraObject("videobean",panoramaVideoBean);
            getPageManager().push(new PanoNetPlayPage(this), playExtraData);
        }else{ //本地

        }

    }

    @Override
    protected void onDestroy() {
        if( getPageManager().getIndexView()!=null){
            getPageManager().getIndexView().finish();
        }
        VideoBean = null;
        panoramaVideoBean = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if( getPageManager().getIndexView()!=null){
                getPageManager().getIndexView().finish();
            }
            finish();
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN ||keyCode==KeyEvent.KEYCODE_VOLUME_UP||keyCode==KeyEvent.KEYCODE_VOLUME_MUTE){ /**系统音量按键事件检测*/
            GLViewPage page = getPageManager().getIndexView();
            if(page!=null){
               if(page instanceof BasePlayerPage){
                   ((BasePlayerPage)page).updateVolumChange(keyCode);
               }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setImmersiveSticky();
        }
    }

    private void setImmersiveSticky() {
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
