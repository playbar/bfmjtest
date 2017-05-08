package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;

/**
 * Created by wanghongfang on 2016/11/24.
 * 播放中正在加载的loadingview
 */
public class LoadingView extends RelativeLayout{
    View  View_Screen; //播放全屏时显示的loading
    SingleLoadingView view_halfScreen; //播放半屏时显示的loading
    SingleLoadingView view_full_left; //全屏时 左面loading
    SingleLoadingView view_full_right; //全屏时 右面loading
    LinearLayout view_fullScreen;
    public LoadingView(Context context){
        super(context);
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        View_Screen = LayoutInflater.from(getContext()).inflate(R.layout.player_loading_view,null);
        this.addView(View_Screen);
        view_fullScreen = (LinearLayout) findViewById(R.id.full_screen_loading);
        view_halfScreen = (SingleLoadingView) findViewById(R.id.single_screen_loading);
        view_full_left = (SingleLoadingView) findViewById(R.id.full_left);
        view_full_right = (SingleLoadingView) findViewById(R.id.full_right);

    }

    public void setLayoutScreen(boolean isfull){
      if(isfull){
          view_fullScreen.setVisibility(VISIBLE);
          view_halfScreen.setVisibility(GONE);
      }else {
          view_fullScreen.setVisibility(GONE);
          view_halfScreen.setVisibility(VISIBLE);
      }
    }

    public void setLoadingText(String text,int percent){

        if(view_full_left!=null){
            view_full_left.setLoadingText(text,percent);
        }
        if(view_full_right!=null){
            view_full_right.setLoadingText(text,percent);
        }
        if(view_halfScreen!=null){
            view_halfScreen.setLoadingText(text,percent);
        }
    }

    public void setLoadingTextVisiable(int visiable){
        if(view_full_left!=null){
            view_full_left.setLoadingTvVisisable(visiable);
        }
        if(view_full_right!=null){
            view_full_right.setLoadingTvVisisable(visiable);
        }
        if(view_halfScreen!=null){
            view_halfScreen.setLoadingTvVisisable(visiable);
        }
    }
}
