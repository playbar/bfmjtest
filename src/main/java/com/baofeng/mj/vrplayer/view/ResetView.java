package com.baofeng.mj.vrplayer.view;

import android.content.Context;

import com.baofeng.mj.R;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.activity.MjVrPlayerActivity;
import com.baofeng.mj.vrplayer.interfaces.IResetLayerCallBack;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.ViewUtil;
import com.bfmj.viewcore.animation.GLAlphaAnimation;
import com.bfmj.viewcore.animation.GLAnimation;
import com.bfmj.viewcore.animation.GLTranslateAnimation;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.view.GLRectView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2017/4/3.
 */

public class ResetView extends ResetRelativeLayout {
    private Context mContext;
//    private float mDepth = 3.8f;
    private float mWidth = 550f;
    private float mHeight = 140f;

    public ResetView(Context context) {
        super(context);
        mContext = context;
        setCostomHeadView(true);
//        setDepth(mDepth);
//        setBackground(new GLColor(0xffffff));
        //创建返回按钮
        createBackBtn();
        //创建加号按钮
        createAddBtn();
        //创建复位按钮
        createResetBtn();
    }

    private ResetButton resetBtn;

    private void createResetBtn() {
        resetBtn = new ResetButton(mContext);
//        resetBtn.setDepth(mDepth);
//        resetBtn.setMargin((mWidth-150f)/2+200f,0f,0f,0f);
        resetBtn.setMargin((mWidth-150f)/2,0f,0f,0f);
        resetBtn.setImageBg(R.drawable.play_menu_button_view_normal);
//        addBtn.setBackground(new GLColor(0xffffff));
        resetBtn.setText("视角复位");
        resetBtn.setVisible(false);
        resetBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, boolean b) {
                if(b) {
                    resetBtn.setImageBg(R.drawable.play_menu_button_view_hover);
                    resetBtn.setTextVisible(true);
                } else {
                    resetBtn.setImageBg(R.drawable.play_menu_button_view_normal);
                    resetBtn.setTextVisible(false);
                }
            }
        });
        resetBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                ((GLBaseActivity)mContext).initHeadView();
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
        addView(resetBtn);
    }

    private ResetButton addBtn;

    private void createAddBtn() {
        addBtn = new ResetButton(mContext);
//        addBtn.setDepth(mDepth);
        addBtn.setMargin((mWidth-150f)/2,0f,0f,0f);
        addBtn.setImageBg(R.drawable.play_menu_button_launch_normal);
//        addBtn.setBackground(new GLColor(0xffffff));
        addBtn.setText("展开菜单");
        addBtn.setSelected(false);
        addBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, boolean b) {
                if(b) {
                    addBtn.setImageBg(R.drawable.play_menu_button_launch_hover);
                    addBtn.setTextVisible(true);
                } else {
                    addBtn.setImageBg(R.drawable.play_menu_button_launch_normal);
                    addBtn.setTextVisible(false);
                }
            }
        });
        addBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {

                if(!addBtn.isSelected()) {
                    addBtn.setText("关闭菜单");
                    addBtn.startTranslate(true);
                    setTranslateAnimation(resetBtn, ViewUtil.getDip(200, GLConst.Bottom_Menu_Scale),0,0);
                    startGLAlphaAnimation(resetBtn,0,1);
                    resetBtn.setVisible(true);
                    setTranslateAnimation(backBtn,-ViewUtil.getDip(200, GLConst.Bottom_Menu_Scale),0,0);
                    startGLAlphaAnimation(backBtn,0,1);
                    backBtn.setVisible(true);
                    addBtn.setSelected(true);
                } else {
                    addBtn.setText("展开菜单");
                    addBtn.startTranslate(false);
                    setTranslateAnimation(resetBtn,-ViewUtil.getDip(200, GLConst.Bottom_Menu_Scale),0,0);
                    startGLAlphaAnimation(resetBtn,1,0);
            //        resetBtn.setVisible(false);
                    setTranslateAnimation(backBtn,ViewUtil.getDip(200, GLConst.Bottom_Menu_Scale),0,0);
                    startGLAlphaAnimation(backBtn,1,0);
            //        backBtn.setVisible(false);
                    addBtn.setSelected(false);
                    //保证关闭动画完成，再隐藏view，否则view不能到指定位置
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            resetBtn.setVisible(false);
                            backBtn.setVisible(false);
                        }
                    },283);
                }
                if(null != mCallBack) {
                    mCallBack.isOpen(addBtn.isSelected());
                }
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
        addView(addBtn);
    }

    public void showOpen() {
        if(!addBtn.isSelected()) {
            addBtn.setText("关闭菜单");
            addBtn.startTranslate(true);
            setTranslateAnimation(resetBtn,200,0,0);
            startGLAlphaAnimation(resetBtn,0,1);
            resetBtn.setVisible(true);
            setTranslateAnimation(backBtn,-200,0,0);
            startGLAlphaAnimation(backBtn,0,1);
            backBtn.setVisible(true);
            addBtn.setSelected(true);
        }
    }

    public void showClose() {
        if(addBtn.isSelected()) {
            addBtn.setText("展开菜单");
            addBtn.startTranslate(false);
            resetBtn.setX(addBtn.getX());
            resetBtn.setVisible(false);
            backBtn.setX(addBtn.getX());
            backBtn.setVisible(false);
            addBtn.setSelected(false);
        }
    }

    private ResetButton backBtn;

    private void createBackBtn() {
        backBtn = new ResetButton(mContext);
//        backBtn.setDepth(mDepth);
//        backBtn.setMargin((mWidth-150f)/2-200f,0f,0f,0f);
        backBtn.setMargin((mWidth-150f)/2,0f,0f,0f);
        backBtn.setImageBg(R.drawable.play_menu_button_quit_normal);
//        addBtn.setBackground(new GLColor(0xffffff));
        backBtn.setText("退出播放");
        backBtn.setVisible(false);
        backBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, boolean b) {
                if(b) {
                    backBtn.setImageBg(R.drawable.play_menu_button_quit_hover);
                    backBtn.setTextVisible(true);
                } else {
                    backBtn.setImageBg(R.drawable.play_menu_button_quit_normal);
                    backBtn.setTextVisible(false);
                }
            }
        });
        backBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {

                ((MjVrPlayerActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MjVrPlayerActivity)mContext).getPageManager().getIndexView().finish();
                        ((MjVrPlayerActivity)mContext).finish();
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
        addView(backBtn);
    }

    private IResetLayerCallBack mCallBack;

    public void setIResetLayerCallBack(IResetLayerCallBack callBack) {
        mCallBack = callBack;
    }

    public void setTranslateAnimation(GLRectView view, final float x, float y,float z){
        if(view == null)
            return;
        GLAnimation animation = new GLTranslateAnimation(x, y, z);
        animation.setAnimView(view);
        animation.setDuration(83);
        view.startAnimation(animation);
    }

    public void startGLAlphaAnimation(GLRectView view, float startAlpha, float endAlpha) {
        if (view == null)
            return;
        GLAnimation animation = new GLAlphaAnimation(startAlpha,endAlpha);
        animation.setAnimView(view);
        animation.setDuration(83);
        view.startAnimation(animation);
    }

    @Override
    public void setResetFixed(boolean resetFixed) {
        super.setResetFixed(resetFixed);
    }
}
