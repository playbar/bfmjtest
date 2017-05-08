package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.util.publicutil.MinifyImageUtil;
import com.baofeng.mj.util.stickutil.StickUtil;

/** 控制方式选择页面
 * Created by muyu on 2016/5/19.
 */
public class ControlActivity extends BaseStickActivity implements View.OnClickListener {

    private ImageView headStatusLight;
    private TextView headStatusTV;

    private ImageView stickStatusLight;
    private TextView stickStatusTV;
    private TextView finishSettingTV;

    private TextView helpTipsTV;
    private RelativeLayout bgLayout;
    private Bitmap backgroundBitmap;
    private Bundle bundleData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_control);
        bundleData = getIntent().getExtras(); //上个页面的数据
        initView();
        startCheck();
    }

    @Override
    public void startCheck() {
        if(BaseApplication.INSTANCE.isBFMJ5Connection() && BaseApplication.INSTANCE.getJoystickConnect()){ //魔镜5代usb连接，并且遥控器连接上
            connectZkey();
        }else if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            unConnectZkey();
        } else if (!StickUtil.isBondBluetooth()) {// 蓝牙与魔镜设备未配对
            unConnectZkey();
        } else if (!StickUtil.isConnected) {// 设备未开启或者设备休眠
            unConnectZkey();
        } else {// 已连接
            connectZkey();
        }
    }


    //连接绿色，未连接红色
    private void unConnectZkey() {
//        headStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_green_dot));
//        headStatusTV.setText("已连接");

        stickStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_red_dot));
        stickStatusTV.setText("未连接");
    }

    private void connectZkey() {
        stickStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_green_dot));
        stickStatusTV.setText("已连接");

//        headStatusLight.setImageDrawable(getResources().getDrawable(R.drawable.public_red_dot));
//        headStatusTV.setText("未连接");
    }

    private void initView(){
//        bgLayout = (RelativeLayout) findViewById(R.id.control_bg);
        backgroundBitmap = MinifyImageUtil.getInstance().zoomBitmap(this);
        bgLayout.setBackground(new BitmapDrawable(this.getResources(), backgroundBitmap));

        headStatusLight = (ImageView) findViewById(R.id.control_head_status_light);
        headStatusTV = (TextView) findViewById(R.id.control_head_status);

        stickStatusLight = (ImageView) findViewById(R.id.control_stick_status_light);
        stickStatusTV = (TextView) findViewById(R.id.control_stick_status);

//        finishSettingTV = (TextView) findViewById(R.id.control_finish_setting);
        finishSettingTV.setOnClickListener(this);

//        helpTipsTV = (TextView) findViewById(R.id.control_help);
        helpTipsTV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if(i == R.id.control_finish_setting){
//            SettingSpBusiness.getInstance().setFirstVRSetting(false);
//            Intent intent = new Intent(this, GoUnity.class);
//            if(bundleData != null) {
//                intent.putExtras(bundleData);
//            }
//            startActivity(intent);
//            finish();
//        } else if( i == R.id.control_help){
//            startActivity(new Intent(this, HelpActivity.class));
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!backgroundBitmap.isRecycled() && backgroundBitmap!= null){
            backgroundBitmap.recycle();
        }
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {

    }

    @Override
    public void onTouchPadPos(String s, float v, float v1) {

    }
}
