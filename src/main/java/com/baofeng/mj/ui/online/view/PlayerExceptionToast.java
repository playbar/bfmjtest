package com.baofeng.mj.ui.online.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by wanghongfang on 2017/3/6.
 */
public class PlayerExceptionToast extends LinearLayout {
    private TextView leftView;
    private TextView rightView;
    private int duration;
    public PlayerExceptionToast(Context context) {
        super(context);
        initView();
    }

    private void initView(){
         leftView = createChild();
         rightView = createChild();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.weight = 1;
        this.addView(leftView,params);
        this.addView(rightView,params);
        setLayoutScreen(false);

    }

    private TextView createChild(){
        TextView view = new TextView(getContext());
        view.setText(getContext().getString(R.string.player_exception_tips));
        view.setTextColor(getResources().getColor(R.color.mj_color_white));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        view.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        view.setPadding(PixelsUtil.dip2px(35),PixelsUtil.dip2px(15),PixelsUtil.dip2px(35),PixelsUtil.dip2px(15));
        view.setBackgroundResource(R.drawable.corner_player_loading_bg);
        return view;
    }




    public void setTipText(String text){
        if(leftView!=null){
            leftView.setText(text);
        }
        if(rightView!=null){
            rightView.setText(text);
        }
    }

    public void setLayoutScreen(boolean isfull){
        if(isfull){
            leftView.setVisibility(VISIBLE);
            rightView.setVisibility(VISIBLE);
        }else {
            leftView.setVisibility(VISIBLE);
            rightView.setVisibility(GONE);
        }
    }
}
