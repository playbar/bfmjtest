package com.baofeng.mj.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import java.lang.reflect.Field;

/**
 * 自定义ViewPager类，可以控制是否禁用左右滑动
 * @author Administrator
 *
 */
public class MyViewPager extends ViewPager {
    private boolean noScroll = false;//false,可以左右滑动，true，禁用左右滑动  
  
    public MyViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public MyViewPager(Context context) {  
        super(context);  
    }  
  
    public void setNoScroll(boolean noScroll) {  
        this.noScroll = noScroll;  
    }  
  
    @Override  
    public void scrollTo(int x, int y) {  
        super.scrollTo(x, y);  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent arg0) {  
        if (noScroll)  
            return false;  
        else  
            return super.onTouchEvent(arg0);  
    }  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent arg0) {  
        if (noScroll)  
            return false;  
        else  
            return super.onInterceptTouchEvent(arg0);  
    }  
  
    @Override  
    public void setCurrentItem(int item, boolean smoothScroll) {  
        super.setCurrentItem(item, smoothScroll);  
    }  
  
    @Override  
    public void setCurrentItem(int item) {  
        super.setCurrentItem(item);  
    }  
    
    /**
     * @author liuchuanchi  @Date 2015-8-24 下午1:56:38
     * @description:{设置滑动的间隔时间}
     */
    public void setSpeedScroller(int mDuration){
 		try {
 			Field mField = ViewPager.class.getDeclaredField("mScroller");
 			mField.setAccessible(true);
 			FixedSpeedScroller mScroller = new FixedSpeedScroller(getContext(),new LinearInterpolator());
 			mScroller.setmDuration(mDuration);// 可以用setDuration的方式调整速率
 			mField.set(this, mScroller);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}	
    }
}
