package com.baofeng.mj.vrplayer.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.interfaces.IPlayerSettingCallBack;
import com.baofeng.mj.vrplayer.utils.BitmapUtil;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;

/**
 * Created by yushaochen on 2017/4/7.
 */

public class MovieSettingView extends GLRelativeView{

    private Context mContext;

    private int mType = 0;

    private GLLinearView glLinearView1;
    private SettingBottomRightView settingBottomRightView;
    private GLLinearView glLinearView2;
    private SkyboxView skyboxView;

    public MovieSettingView(Context context) {

        super(context);
        mContext = context;
        setLayoutParams(1000,285);
    }

    public void initView(int type) {
        mType = type;
        //创建场景选择button
        glLinearView1 = new GLLinearView(mContext);
        glLinearView1.setLayoutParams(1000,150);
        Bitmap bitmap = BitmapUtil.getBitmap(1000, 150, 20f, "#272729");
        glLinearView1.setBackground(bitmap);
        glLinearView1.setPadding(55f,35f,55f,35f);
        settingBottomRightView = new SettingBottomRightView(mContext);
        settingBottomRightView.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                glLinearView1.setVisible(false);
                glLinearView2.setVisible(true);
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
        HeadControlUtil.bindView(settingBottomRightView);
        glLinearView1.addView(settingBottomRightView);

        //创建场景选择view
        glLinearView2 = new GLLinearView(mContext);
        glLinearView2.setLayoutParams(1000,285);
        Bitmap bitmap2 = BitmapUtil.getBitmap(1000, 285, 20f,"#272729");
        glLinearView2.setBackground(bitmap2);
        glLinearView2.setPadding(105f,55f,105f,55f);
        skyboxView = new SkyboxView(mContext);
        skyboxView.setSelected(SettingSpBusiness.getInstance().getSkyboxIndex()+"");
        glLinearView2.addView(skyboxView);
        glLinearView2.setVisible(false);

        glLinearView1.setFocusListener(focusListener);
        glLinearView2.setFocusListener(focusListener);

        addViewBottom(glLinearView1);
        addViewBottom(glLinearView2);
    }

    public void refreshView() {
        glLinearView1.setVisible(true);
        glLinearView2.setVisible(false);
    }

    private IPlayerSettingCallBack mCallBack;

    public void setIPlayerSettingCallBack(IPlayerSettingCallBack callBack) {

        this.mCallBack = callBack;
    }

    private GLViewFocusListener focusListener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView view, boolean focused) {
            if(focused) {
                ((GLBaseActivity)getContext()).showCursorView();
                if(null != mCallBack) {
                    mCallBack.onHideControlAndSettingView(false);
                }
            } else {
                ((GLBaseActivity)getContext()).hideCursorView2();
                if(null != mCallBack) {
                    mCallBack.onHideControlAndSettingView(true);
                }
            }
        }
    };
}
