package com.baofeng.mj.vrplayer.page;

import android.content.Context;

import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.activity.MjVrPlayerActivity;
import com.baofeng.mj.vrplayer.view.SplashSkyboxView;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLViewPage;

/**
 * Created by yushaochen on 2017/4/5.
 */

public class SplashPage extends GLViewPage {

    private Context mContext;
    private GLRelativeView indexRootView;

    private SplashSkyboxView skyboxView;

    public SplashPage(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected GLRectView createView(GLExtraData data) {
        indexRootView = new GLRelativeView(mContext);
        indexRootView.setLayoutParams(GLRectView.MATCH_PARENT,
                GLRectView.MATCH_PARENT);
//        indexRootView.setBackground(new GLColor(0x414141));
        //创建中间场景选择
        createSkyboxView();

        return indexRootView;
    }

    private void createSkyboxView() {
        skyboxView = new SplashSkyboxView(mContext);
        skyboxView.setMargin(1200f-500f,(1200f-(60 + 70 + 284 + 70 + 70)/2),0f,0f);
//        skyboxView.setBackground(new GLColor(0xff0000));
        skyboxView.setOkKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                ((GLBaseActivity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭当前页面
                        finish();
                        //必现跳转播放页面底部按钮是已打开的状态，先设置关闭
                        ((GLBaseActivity)getContext()).showClose();
                        //开启播放页
                        ((MjVrPlayerActivity) getContext()).startPlayPage();
                        SettingSpBusiness.getInstance().setVrGuide(true);
                    }
                });

                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        skyboxView.setFocusListener(focusListener);
        indexRootView.addView(skyboxView);
    }

    private GLViewFocusListener focusListener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView view, boolean focused) {
            if(focused) {
                ((GLBaseActivity)getContext()).showCursorView();
            } else {
                ((GLBaseActivity)getContext()).hideCursorView2();
            }
        }
    };
}
