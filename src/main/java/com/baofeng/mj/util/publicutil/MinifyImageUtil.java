package com.baofeng.mj.util.publicutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.utils.FastBlur;

import java.io.InputStream;

/**
 * 高斯模糊工具类/图片压缩工具类
 * Created by muyu on 2016/5/11.
 */
public class MinifyImageUtil {

    private static MinifyImageUtil instance;
    public static MinifyImageUtil getInstance(){
        if(instance == null){
            instance = new MinifyImageUtil();
        }
        return instance;
    }
    private MinifyImageUtil(){

    }

    public void blur(Context context,Bitmap bkg,View view,int radius){
        long startMs = System.currentTimeMillis();
        bkg = FastBlur.doBlur(bkg, (int) radius, true);
        if(bkg != null && bkg.getWidth() > 0 && bkg.getHeight() > 0){
            Bitmap overlay = Bitmap.createScaledBitmap(bkg, view.getWidth(),view.getHeight(),true);
            view.setBackground(new BitmapDrawable(context.getResources(), overlay));
        }
    }
    public void blur(Context context, Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float radius = 10;
        bkg = FastBlur.doBlur(bkg, (int) radius, true);
        if(bkg != null && bkg.getWidth() > 0 && bkg.getHeight() > 0 && view != null && view.getWidth()> 0 && view.getHeight() > 0){
            Bitmap overlay = Bitmap.createScaledBitmap(bkg, view.getWidth(),view.getHeight(),true);
            view.setBackground(new BitmapDrawable(context.getResources(), overlay));
        }
    }

    //进入横屏页面背景图片压缩，屏幕宽1080，高1920
    public Bitmap zoomBitmap(Context context){
        Bitmap bitmap;
        InputStream is = context.getResources().openRawResource(+R.drawable.enter_bg);
        bitmap =BitmapFactory.decodeStream(is, null, ImageOption());

        Bitmap overlay = Bitmap.createScaledBitmap(bitmap, 108, 192, true);
        return overlay;
    }

    //ImageLoader默认Option
    public BitmapFactory.Options ImageOption(){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;
        return options;
    }
}
