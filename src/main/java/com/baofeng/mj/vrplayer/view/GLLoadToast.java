package com.baofeng.mj.vrplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baofeng.mj.R;
import com.baofeng.mj.vrplayer.utils.BitmapUtil;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLTextView;

/**
 * Created by yushaochen on 2017/4/27.
 */
public class GLLoadToast extends GLLinearView {

    private Context mContext;

    private GLTextView textView;
    private GLImageView imageView;

    private boolean mRotate = false;
    private int mSpeed = 100;
    private float mAngle = 45f;


    public GLLoadToast(Context context){
        super(context);
        mContext = context;
        setLayoutParams(525,80);
        setOrientation(GLConstant.GLOrientation.HORIZONTAL);
        Bitmap bitmap = BitmapUtil.getBitmap(525, 80, 20f, "#000000");
        setBackground(bitmap);
        //创建旋转icon
        createImageView();
        //创建文本显示
        createTextView();

        setVisible(false);
    }

    private void createImageView() {
        imageView = new GLImageView(mContext);
        imageView.setLayoutParams(50,50);
        imageView.setBackground(R.drawable.play_icon_loading);
        imageView.setMargin(50,15,0,0);
        addView(imageView);
    }

    private void createTextView(){
        textView = new GLTextView(mContext);
        textView.setLayoutParams(525-170,80);
//        textView.setBackground(new GLColor(0xff0000));
        textView.setMargin(20,0,0,0);
        textView.setTextSize(28);
        textView.setTextColor(new GLColor(0x888888));
        textView.setPadding(0,20,0,0);
        textView.setAlignment(GLTextView.ALIGN_CENTER);
//        textView.setText("即将播放:这是一个测试的...");
        addView(textView);
    }

    /**
     * 1 样式为：即将播放:这是一个测试的...
     * 2 样式为：从00:00:00开始继续播放
     * @param text
     * @param type
     */
    public void setText(String text,int type){
        if(!TextUtils.isEmpty(text)) {
            if(type == 1) {
                int length = text.length();
                if(length > 7) {
                    textView.setText("即将播放:"+text.substring(0,6)+"...");
                } else {
                    textView.setText("即将播放:"+text);
                }
            } else if(type == 2) {
                textView.setText("从"+text+"开始继续播放");
            }
        } else {
            textView.setText("");
        }
    }

    private void rotate() {
        (new Thread(new Runnable() {
            public void run() {
                for(; GLLoadToast.this.mRotate; GLLoadToast.this.imageView.rotate(-GLLoadToast.this.mAngle, 0.0F, 0.0F, 1.0F)) {
                    try {
                        Thread.sleep((long)GLLoadToast.this.mSpeed);
                    } catch (InterruptedException var2) {
                        var2.printStackTrace();
                    }
                }

            }
        })).start();
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public void removeRotate() {
        this.mRotate = false;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible) {
            //开启转动
            if(!this.mRotate) {
                this.mRotate = true;
                this.rotate();
            }
        } else {
            //关闭转动
            this.mRotate = false;
        }
    }

}
