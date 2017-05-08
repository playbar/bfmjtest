package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.NetworkUtil;

/**
 * Created by wanghongfang on 2016/11/24.
 */
public class SingleLoadingView extends RelativeLayout {
    ImageView loadingImg;
    TextView loadingTv;
    TextView loadingPercent;//加载百分比
    public SingleLoadingView(Context context, AttributeSet attr){
        super(context,attr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_loading_single_view,null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(view,params);
        loadingImg = (ImageView) findViewById(R.id.loading_img);
        loadingTv = (TextView)findViewById(R.id.loading_tv);
        loadingPercent = (TextView)findViewById(R.id.loading_percent);
        RotateAnimation animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        loadingImg.setAnimation(animation);
        animation.start();
    }
    public void setLoadingText(String text,int precent){
        if(loadingTv!=null){
            loadingTv.setText(text);
        }
        if(loadingPercent!=null){
            loadingPercent.setText(precent>0?precent+"%":"");
        }
    }
    public void setLoadingTvVisisable(int visisable){
        if(loadingTv!=null){
            loadingTv.setVisibility(visisable);
        }
        if(loadingPercent!= null){
            loadingPercent.setVisibility(visisable);
        }
    }
}
