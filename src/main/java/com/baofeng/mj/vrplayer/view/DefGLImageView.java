package com.baofeng.mj.vrplayer.view;

import android.content.Context;

import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.GLImageView;

/**
 * Created by wanghongfang on 2016/8/4.
 * 播放控制栏按钮
 */
public class DefGLImageView extends GLImageView {
    private final int VIEW_BG_COLOR = 0x294c68;
    private final int ITEM_BG_COLOR_NORMAL = 0xff767676;
    private final int ITEM_BG_COLOR_FOCUSE = 0xff355177;
    private int resImgDefault;
    private int resImgFocused;
    private boolean isSelect = false;
    public void setImage(int resImgDefault,int resImgFocused){
        this.resImgDefault = resImgDefault;
        this.resImgFocused = resImgFocused;

        setImage(isFocused()||isSelect?resImgFocused:resImgDefault);
    }
    public DefGLImageView(Context context){
        super(context);
    }
    public void updateFocuse(boolean focused){
        isSelect = focused;
        if(focused){
            setImage(resImgFocused);
        }else {
            setImage(resImgDefault);
        }
    }
}

