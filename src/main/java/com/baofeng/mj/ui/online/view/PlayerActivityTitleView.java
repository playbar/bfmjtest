package com.baofeng.mj.ui.online.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;

/**
 * Created by wanghongfang on 2017/1/22.
 * 播放View的标题栏
 */
public class PlayerActivityTitleView extends FrameLayout implements View.OnClickListener{
    private View playerBack;
    private TextView videoNameTv;
    private ImageButton inVrBtn;
    private ImageView img_bt_connct;
    private TextView tv_bt_connect;
    public PlayerActivityTitleView(Context context) {
        super(context);
        initView();
    }

    public PlayerActivityTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_controller_bar_top, this);
        playerBack = rootView.findViewById(R.id.video_player_back);
        videoNameTv = (TextView)rootView.findViewById(R.id.tv_video_name);
        inVrBtn = (ImageButton) rootView.findViewById(R.id.imagebtn_bar_top_in_vr);
        img_bt_connct = (ImageView)rootView.findViewById(R.id.iv_bluetooth_connect_flag);
        tv_bt_connect = (TextView)rootView.findViewById(R.id.tv_bluetooth_connect_flag);
        inVrBtn.setVisibility(GONE);
        setScreenDouble(false);
        playerBack.setOnClickListener(this);
        inVrBtn.setOnClickListener(this);

    }

    public void setScreenDouble(boolean isdouble){
        if(isdouble){
            img_bt_connct.setVisibility(VISIBLE);
            tv_bt_connect.setVisibility(VISIBLE);
        }else {
            img_bt_connct.setVisibility(GONE);
            tv_bt_connect.setVisibility(GONE);
        }
    }

    public void connectManager(boolean isConnected){
        if(isConnected){
            img_bt_connct.setImageResource(R.drawable.bluetooth_conneced_img);
            tv_bt_connect.setText(getResources().getString(R.string.bluetooth_control_conneced));
        }else{
            img_bt_connct.setImageResource(R.drawable.bluetooth_disconneced_img);
            tv_bt_connect.setText(getResources().getString(R.string.bluetooth_control_disconneced));
        }
    }

    public TextView getNameTV() {
        return videoNameTv;
    }
    public View getBackImgBtn(){
        return playerBack;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_player_back) {
            ((Activity)getContext()).finish();
        }
    }
}
