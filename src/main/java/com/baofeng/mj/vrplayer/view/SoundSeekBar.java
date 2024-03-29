package com.baofeng.mj.vrplayer.view;

import android.content.Context;

import com.baofeng.mj.R;
import com.baofeng.mj.vrplayer.interfaces.IPlayerSettingCallBack;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.MJGLUtils;
import com.baofeng.mj.vrplayer.utils.ViewUtil;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.GLFocusUtils;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLProcessView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2017/4/11.
 */
public class SoundSeekBar extends GLProcessView {

    private int mWidth = 680;
    private int mHeight = 20;

    private int mResImg;
    private GLImageView mBarView;
    private GLTextView mDisplayView;
    private IPlayerSettingCallBack mCallBack;
    private int bar_width = 10,bar_height=32;
    private int display_width = 130,display_height=40;
    private int mDuration=100; //
    private GLImageView display_image;
    private int display_image_width = 10,display_image_height=32;

    private int offsetHeight = (bar_height-mHeight)/2;

    public void setBarImage(int barImage){
        this.mResImg = barImage;
        initView();
        this.setFocusListener(viewFocusListener);
        this.setOnKeyListener(mKeyListener);
    }
    public SoundSeekBar(Context context){
        super(context);
        setProcessColor(R.drawable.play_volume_slider_progress);
        setLayoutParams(mWidth,mHeight);
    }

    public void setDuration(int duration){
        mDuration = duration;
    }

    public void setIPlayerSettingCallBack(IPlayerSettingCallBack callBack){
        this.mCallBack = callBack;
    }

    private void initView(){
        mBarView = new GLImageView(getContext());
        mBarView.setImage(mResImg);
        mBarView.setLayoutParams(bar_width,bar_height);
        this.addView(mBarView);

        mDisplayView = new GLTextView(getContext());
        mDisplayView.setTextColor(new GLColor(0x888888));
        mDisplayView.setTextSize(20);
        mDisplayView.setText("00");
        mDisplayView.setLayoutParams(display_width,display_height);
        mDisplayView.setAlignment(GLTextView.ALIGN_CENTER);
        mDisplayView.setPadding(0,10,0,0);
        mDisplayView.setVisible(false);
        this.addView(mDisplayView);

        display_image = new GLImageView(getContext());
        display_image.setLayoutParams(display_image_width,display_image_height);
        display_image.setBackground(R.drawable.play_volume_cursor_hover);
        display_image.setVisible(false);
        this.addView(display_image);
    }

    @Override
    public void addView(GLRectView view) {
        view.setX(this.getX() + this.getPaddingLeft() + view.getMarginLeft());
        view.setY(this.getY() + this.getPaddingTop() + view.getMarginTop());
        super.addView(view);
    }

    @Override
    public void setProcess(int process) {
        super.setProcess(process);
         float width = (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) / 100.0F * process;
        this.mBarView.setLayoutParams(ViewUtil.getDip(bar_width, GLConst.Player_Settings_Scale) ,ViewUtil.getDip(bar_height,GLConst.Player_Settings_Scale));
        this.mBarView.setX(this.getX() + this.getPaddingLeft()+width-ViewUtil.getDip(bar_width,GLConst.Player_Settings_Scale)/2);
        this.mBarView.setY(this.getY() + this.getPaddingTop()-ViewUtil.getDip(offsetHeight,GLConst.Player_Settings_Scale));
    }
    private void setDisplayLayout(){
        if(mDisplayView!=null) {
            int xy[] = GLFocusUtils.getCursorPosition(this);
            int x=xy[0],y=xy[1];
            float viewX = SoundSeekBar.this.getX();
            float viewWidth = SoundSeekBar.this.getWidth();
            float viewY = SoundSeekBar.this.getY();

            if(x>0&&x< viewWidth){
                float process =(x)/viewWidth;

                int current = mDuration*process > 99 ? 100:(int) (mDuration*process);
//                int current = (int) (mDuration*process);
                mDisplayView.setText(current+"");
                mDisplayView.setLayoutParams(ViewUtil.getDip(display_width,GLConst.Player_Settings_Scale), ViewUtil.getDip(display_height,GLConst.Player_Settings_Scale));
                mDisplayView.setX(viewX+x - ViewUtil.getDip(display_width,GLConst.Player_Settings_Scale) / 2);
                mDisplayView.setY(viewY -  ViewUtil.getDip(display_height,GLConst.Player_Settings_Scale));
                display_image.setX(viewX+x -  ViewUtil.getDip(display_image_width,GLConst.Player_Settings_Scale) / 2);
                display_image.setY(viewY- ViewUtil.getDip(offsetHeight,GLConst.Player_Settings_Scale));
            }
        }
    }

    private GLViewFocusListener viewFocusListener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView glRectView, boolean b) {
            if(b){
                mDisplayView.setVisible(true);
                display_image.setVisible(true);
                startTimer();
            }else {
                stopTimer();
                mDisplayView.setVisible(false);
                display_image.setVisible(false);
            }
        }
    };

    Timer timer;
    public void startTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MJGLUtils.exeGLQueueEvent(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        setDisplayLayout();
                    }
                });
            }
        },0,300);

    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }


    private GLOnKeyListener mKeyListener = new GLOnKeyListener() {
        @Override
        public boolean onKeyDown(GLRectView glRectView, int keyCode) {
            if(keyCode== MojingKeyCode.KEYCODE_ENTER){

                float[] pos = GLFocusUtils.getPosition(SoundSeekBar.this.getMatrixState().getVMatrix(), SoundSeekBar.this.getDepth());
                int x=(int)pos[0];//;,y=xy[1];
                float viewX = SoundSeekBar.this.getX();
                float viewWidth = SoundSeekBar.this.getWidth();
                if(x>=viewX&&x< viewX+viewWidth){
                    float process =(x-viewX)/viewWidth;
                    int current = mDuration*process > 99 ? 100:(int) (mDuration*process);
//                    int current = (int) (mDuration*process);
//                    setProcess(current);
                    if(null != mCallBack) {
                        mCallBack.onSoundChange(current);
                    }
                }
            }
            return false;
        }
        @Override
        public boolean onKeyUp(GLRectView glRectView, int i) {
            return false;
        }

        @Override
        public boolean onKeyLongPress(GLRectView glRectView, int i) {
            return false;
        }
    };
}
