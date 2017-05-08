package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;


/**
 * Created by wanghongfang on 2017/1/16.
 * 极简播放view的中线
 */
public class PlayerCenterLine extends View {
    Paint mpaint;
    int width;
    int height;
    boolean hasNav;
    int top;
    int bottom;
    private boolean drawMargin = false;
    public PlayerCenterLine(Context context) {
        super(context);
    }

    public PlayerCenterLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initDrawMargin(boolean drawMargin){
        this.drawMargin = drawMargin;
       invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mpaint==null){
            mpaint = new Paint();
            mpaint.setAntiAlias(true);
            this.top = PixelsUtil.dip2px(40);
            this.bottom = PixelsUtil.dip2px(40);
        }
        width = getWidth();
        height = getHeight();
        int x = width/2-1;
        mpaint.setColor(Color.WHITE);
        if(!drawMargin){
            canvas.drawLine(x,0,x+1,height,mpaint);
            x = x+1;
            mpaint.setColor(Color.BLACK);
            canvas.drawLine(x,0,x+1,height,mpaint);
        }else {
            canvas.drawLine(x,top,x+1,height-bottom,mpaint);
            x = x+1;
            mpaint.setColor(Color.BLACK);
            canvas.drawLine(x,top,x+1,height-bottom,mpaint);
        }

        super.onDraw(canvas);
    }




}
