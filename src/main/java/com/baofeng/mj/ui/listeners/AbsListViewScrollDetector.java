package com.baofeng.mj.ui.listeners;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;

/**
 * Created by liuchuanchi on 2016/10/18.
 * 滑动监听器
 */
public class AbsListViewScrollDetector implements AbsListView.OnScrollListener{
    private Fragment fragment;

    public AbsListViewScrollDetector(){
    }

    public void addFragment(Fragment fragment){
        this.fragment = fragment;
    }

    public void removeFragment(){
        this.fragment = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState){//滑动拖拽

        }else if(AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState){//滑动停止
//            if(fragment != null){
//                Glide.with(fragment).resumeRequests();//开始请求图片加载
//            }
        }else{//滑动中
//            if(fragment != null){
//                Glide.with(fragment).pauseRequests();//暂停请求图片加载
//            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
}
