package com.baofeng.mj.util.publicutil;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import java.lang.reflect.Method;

/**
 * 手机有虚拟按键相关工具类
 * Created by muyu on 2016/4/1.
 */
public class MobileHasVirtualKey {

    public boolean hasVirtualKey(Context context){
        return !getScreen(context).equals(getDefaultScreen(context));
    }

    /**
     * 获取可用屏幕格式
     *
     * @return String
     * @author linzanxian  @Date 2015年3月19日 上午10:35:59
     * description:获取可用屏幕格式
     */
    private String getScreen(Context context) {
        String dpi = null;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();

        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.widthPixels + "*" + dm.heightPixels;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return dpi;
    }

    /**
     * 获取默认屏幕格式
     *
     * @return String
     * @author linzanxian  @Date 2015年3月19日 上午10:34:39
     * description:获取默认屏幕格式
     */
    private String getDefaultScreen(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels + "*" + dm.heightPixels;
    }
}
