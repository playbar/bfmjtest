package com.baofeng.mj.vrplayer.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.utils.BitmapUtil;
import com.baofeng.mj.vrplayer.utils.GLConst;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;

/**
 * Created by wanghongfang on 2017/4/14.
 * 在线播放锁屏按钮
 */
public class GLLockScreenView extends GLRelativeView{
    private float mDepth = GLConst.LockScreen_Depth;
    private Context mContext;
    public static final int lock_view_widht = 2400;
    public static final int lock_view_height= 165;
    private int img_width = 80;
    private int img_height = 80;
    private int mTextWidth = 150;
    private int mTextHeight = 60;
    private DefGLImageView imageView;
    private ILockScreenListener callBack;
    private GLTextView mText;
    private boolean isLocked = false; //是否锁屏
    private Bitmap bgBitmap;
    private GLTextToast glTextToast;
    GLRelativeView viewLayer;
    public GLLockScreenView(Context context) {
        super(context);
        this.mContext = context;
        this.setLayoutParams(lock_view_widht,lock_view_height);
        viewLayer = new GLRelativeView(mContext);
        viewLayer.setLayoutParams(lock_view_widht,lock_view_height);
        createBtn();
        createToast();
        this.addView(viewLayer);
        setListener();
    }

    private void setListener(){
        this.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(focused) {
                    ((GLBaseActivity)getContext()).showCursorView();
                } else {
                    ((GLBaseActivity)getContext()).hideCursorView();
                }
                GLLockScreenView.this.viewLayer.setVisible(focused);
            }
        });
    }
    private void createBtn() {
        imageView = new DefGLImageView(mContext);
        imageView.setImage(R.drawable.play_lock_button_lock_normal,R.drawable.play_lock_button_lock_hover);
        imageView.setId("lock_btn");
        imageView.setLayoutParams(img_width,img_height);
        imageView.setMargin(0f,10f,0f,0f);
        HeadControlUtil.bindView(imageView);
        imageView.setFocusListener(mViewFocusLisener);
        imageView.setOnKeyListener(mViewKeyListener);
        mText = new GLTextView(mContext);
        mText.setLayoutParams(mTextWidth,mTextHeight);
        mText.setMargin(0f,img_height+20f,0f,0f);
        mText.setTextSize(28);
        mText.setPadding(15,10,15,10);
        mText.setTextColor(new GLColor(0x888888));
        mText.setAlignment(GLTextView.ALIGN_CENTER);
        mText.setText("锁定屏幕");
        mText.setVisible(false);
        bgBitmap = BitmapUtil.getBitmap(mTextWidth, mTextHeight, 10f, "#19191a");
        mText.setBackground(bgBitmap);
        viewLayer.addViewCenterHorizontal(imageView);
        viewLayer.addViewCenterHorizontal(mText);
//        this.setBackground(new GLColor(1,0,0));
    }

    private void createToast(){
        glTextToast = new GLTextToast(getContext());
        glTextToast.setTextType(getContext().getString(R.string.gl_player_locked),GLTextToast.LONG,false);
        glTextToast.setMargin(lock_view_widht/2-250,img_height+20f,0f,0f);
        glTextToast.setBackground(R.drawable.play_lock_tips_bg);
        viewLayer.addView(glTextToast);
        glTextToast.setVisible(false);
    }

    public void showTips(String string){
        glTextToast.showToast(string,GLTextToast.LONG,false);
    }

    public void onFinish(){
        if(bgBitmap!=null&&!bgBitmap.isRecycled()){
            bgBitmap.recycle();
        }
    }
    private void updateLockView(){
           isLocked =!isLocked;
                if(isLocked){
                    imageView.setImage(R.drawable.play_lock_button_unlock_normal,R.drawable.play_lock_button_unlock_hover);
                    mText.setText("解锁屏幕");
                }else {
                    imageView.setImage(R.drawable.play_lock_button_lock_normal,R.drawable.play_lock_button_lock_hover);
                    mText.setText("锁定屏幕");
                }
    }

    public void setViewVisable(boolean viewVisable){
        viewLayer.setVisible(viewVisable);
    }

    public void setLockCallback(ILockScreenListener callback){
       this.callBack = callback;
    }

    private GLViewFocusListener mViewFocusLisener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView view, boolean focused) {
            ((DefGLImageView) view).updateFocuse(focused);
                mText.setVisible(focused);
        }
    };
    private GLOnKeyListener mViewKeyListener = new GLOnKeyListener() {
        @Override
        public boolean onKeyDown(GLRectView view, int keycode) {
            if(keycode== MojingKeyCode.KEYCODE_ENTER){
                updateLockView();
                if(callBack!=null) {
                    callBack.onLockChanged(isLocked);
                }
                if( isLocked&&SettingSpBusiness.getInstance().getGLPlayerFirstLockTip()){ //锁屏
                    showTips(getContext().getString(R.string.gl_player_locked));
                    mText.setVisible(false);
                    SettingSpBusiness.getInstance().setGLPlayerFirstLockTip(false);
                }else if(!isLocked&&SettingSpBusiness.getInstance().getGLPlayerFirstUnLockTip()){
                    showTips(getContext().getString(R.string.gl_player_unlocked));
                    mText.setVisible(false);
                    SettingSpBusiness.getInstance().setGLPlayerFirstUnLockTip(false);
                }


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
    };


    public interface ILockScreenListener{
        void onLockChanged(boolean islocked);
    }
}

