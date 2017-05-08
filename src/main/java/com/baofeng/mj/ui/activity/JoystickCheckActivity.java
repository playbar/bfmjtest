package com.baofeng.mj.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.storm.smart.common.utils.LogHelper;

/**
 * Created by wanghongfang on 2017/2/23.
 * 手柄按键检测
 */
public class JoystickCheckActivity extends BaseStickActivity {
   private  TextView key_confirm;
    private  TextView key_back;
    private  TextView key_menu;
    private  TextView key_left;
    private  TextView key_right;
    private  TextView key_top;
    private  TextView key_bottom;
    private TextView cur_connect;
    private TextView connect_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_joystick);
        initView();
    }

    private void initView() {
        AppTitleBackView titleView  = (AppTitleBackView) findViewById(R.id.title_layout);
        titleView.getInvrImgBtn().setVisibility(View.GONE);
        titleView.getNameTV().setText(getResources().getString(R.string.activity_joystick_check_title));
        key_confirm = (TextView) findViewById(R.id.key_confirm);
        key_back = (TextView) findViewById(R.id.key_back);
        key_menu = (TextView) findViewById(R.id.key_menu);
        key_left = (TextView) findViewById(R.id.key_left);
        key_right = (TextView) findViewById(R.id.key_right);
        key_top = (TextView) findViewById(R.id.key_top);
        key_bottom = (TextView) findViewById(R.id.key_bottom);
        cur_connect = (TextView)findViewById(R.id.joystick_cur_connect);
        connect_tip = (TextView)findViewById(R.id.joystick_connecting_tip);
        connect_tip.setVisibility(View.INVISIBLE);

    }

    @Override
    public void startCheck() {
        if (!StickUtil.blutoothEnble()||!StickUtil.isBondBluetooth()||!StickUtil.isConnected) {// 未连接
            cur_connect.setText(getString(R.string.joystick_connecting));
            connect_tip.setVisibility(View.VISIBLE);
        } else {// 已连接
             String str = getResources().getString(R.string.activity_joystick_check_cur_connect);
            String name = BaseApplication.INSTANCE.getJoystickName();
            if(!TextUtils.isEmpty(name)&&name.contains("_")){
                name = name.substring(0,name.lastIndexOf("_"));
            }
            cur_connect.setText(Html.fromHtml("<font color='#999999'><b>"+str+"</b></font><font color='#7a4fc9'><b>"+name+"</b></font>"));
            connect_tip.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public boolean onMojingKeyDown(String s, int keyCode) {
        Log.d("StickUtil","---onMojingKeyDown keyCode = "+keyCode);
        switch (keyCode){
            case MojingKeyCode.KEYCODE_BACK:
            case MojingKeyCode.KEYCODE_BUTTON_B:
                key_back.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_back.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_MENU:
            case MojingKeyCode.KEYCODE_BUTTON_X:
                key_menu.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_menu.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_ENTER:
            case MojingKeyCode.KEYCODE_BUTTON_A:
                key_confirm.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_confirm.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_DPAD_LEFT:
                key_left.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_left.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_DPAD_RIGHT:
                key_right.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_right.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_DPAD_UP:
                key_top.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_top.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_DPAD_DOWN:
                key_bottom.setBackgroundResource(R.drawable.corner_round_purple_bg);
                key_bottom.setTextColor(getResources().getColor(R.color.white));
                break;
        }


        return super.onMojingKeyDown(s, keyCode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportPv();
    }

    private void reportPv(){
        ReportPVBean bean=new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("controlcheck");
        ReportBusiness.getInstance().reportPV(bean);
    }

}
