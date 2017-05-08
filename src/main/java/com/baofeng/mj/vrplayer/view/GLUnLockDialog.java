package com.baofeng.mj.vrplayer.view;

import android.content.Context;
import android.text.TextUtils;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;
import com.sina.weibo.sdk.api.share.Base;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2017/4/10.
 * 不在白名单的手机播放高清度需要弹窗提示
 */

public class GLUnLockDialog extends GLRelativeView{

    private Context mContext;

    private int width = 800;
    private int height = 240;
    private int btnWidth = 240;
    private int btnHeight = 70;

    private GLTextView textView;
    private GLTextView leftBtn;
    private GLTextView rightBtn;
    private UnLockCallBack mUnLockCallBack;
    public GLUnLockDialog(Context context) {
        super(context);
        mContext = context;
        setLayoutParams(width,height);
        setBackground(new GLColor(0x000000));
        //创建需要显示的view
        createView();
        setVisible(false);
    }

    public void setmUnLockCallBack(UnLockCallBack unLockCallBack){
        this.mUnLockCallBack = unLockCallBack;
    }

    private void createView() {
        //创建文案显示
        textView = new GLTextView(mContext);
        textView.setLayoutParams(width-20,130);
        textView.setTextSize(30);
        textView.setTextColor(new GLColor(0x888888));
        textView.setMargin(10,10,10,10);
        textView.setAlignment(GLTextView.ALIGN_CENTER);
        textView.setPadding(15,20,15,10);
        textView.setText(BaseApplication.getInstance().getResources().getString(R.string.player_lock_tips));
        addView(textView);
        //创建退出按钮
        leftBtn = new GLTextView(mContext);
        leftBtn.setLayoutParams(btnWidth,btnHeight);
        leftBtn.setBackground(R.drawable.play_button_bg_ok_normal);
        leftBtn.setMargin((800f-540f)/2,0,0f,20f);
        leftBtn.setTextSize(32);
        leftBtn.setTextColor(new GLColor(0x888888));
        leftBtn.setAlignment(GLTextView.ALIGN_CENTER);
        leftBtn.setText(BaseApplication.getInstance().getResources().getString(R.string.cancel));
        leftBtn.setPadding(0f,10f,0f,0f);
        leftBtn.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(focused) {
                    leftBtn.setBackground(R.drawable.play_button_bg_ok_hover);
                    leftBtn.setTextColor(new GLColor(0x19191a));
                } else {
                    leftBtn.setBackground(R.drawable.play_button_bg_ok_normal);
                    leftBtn.setTextColor(new GLColor(0x888888));
                }
            }
        });
        HeadControlUtil.bindView(leftBtn);
        addViewBottom(leftBtn);
        //创建继续播放按钮
        rightBtn = new GLTextView(mContext);
        rightBtn.setLayoutParams(btnWidth,btnHeight);
        rightBtn.setBackground(R.drawable.play_button_bg_ok_normal);
        rightBtn.setMargin((800f-540f)/2+300f,0,0f,10f);
        rightBtn.setTextSize(32);
        rightBtn.setTextColor(new GLColor(0x888888));
        rightBtn.setAlignment(GLTextView.ALIGN_CENTER);
        rightBtn.setText("继续播放");
        rightBtn.setPadding(0f,10f,0f,0f);
        rightBtn.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(focused) {
                    rightBtn.setBackground(R.drawable.play_button_bg_ok_hover);
                    rightBtn.setTextColor(new GLColor(0x19191a));
                } else {
                    rightBtn.setBackground(R.drawable.play_button_bg_ok_normal);
                    rightBtn.setTextColor(new GLColor(0x888888));
                }
            }
        });
        HeadControlUtil.bindView(rightBtn);
        addViewBottom(rightBtn);
        setListener();
    }

    private void setListener(){
        rightBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(mUnLockCallBack!=null){
                    mUnLockCallBack.onConfirm();
                }
                GLUnLockDialog.this.setVisible(false);
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
        leftBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(mUnLockCallBack!=null){
                    mUnLockCallBack.onCancel();
                }
                GLUnLockDialog.this.setVisible(false);
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
    }

    public void setTitleText(String text) {
        if(!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    public void setLeftText(String text) {
        if(!TextUtils.isEmpty(text)) {
            leftBtn.setText(text);
        }
    }

    public void setRightText(String text) {
        if(!TextUtils.isEmpty(text)) {
            rightBtn.setText(text);
        }
    }

    public interface UnLockCallBack {
        public void onConfirm();
        public void onCancel();
    }

}
