package com.baofeng.mj.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.storm.smart.common.utils.LogHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dupengwei on 2017/4/20.
 * 体感手柄按键检测
 */
public class MotionHandleCheckActivity extends BaseStickActivity {
    private TextView cur_connect;
    private TextView connect_tip;

    private TextView mKey_longPress;
    private TextView mKey_up;
    private TextView mKey_down;
    private TextView mKey_left;
    private TextView mKey_right;
    private TextView mKey_back;
    private TextView mKey_home;
    private TextView mKey_longPressHome;
    private TextView mKey_confirm;
    private TextView mKey_volAdd;
    private TextView mKey_volCut;
    private TextView mGyro;
    private String mMojingMotionName;
    private long mStartHome;
    private long mEndHome;
    private long mStartLongPress;
    private long mEndLongPress;

    private Timer timer;
    private float mTouchStartX;
    private float mTouchEndX;
    private float mTouchStartY;
    private float mTouchEndY;
    private boolean mIsFirst = true;
    private TimerTask mTimerTask;
    private boolean mWaggel = true;
    private float[] mOne = new float[4];
    private float[] mTwo = new float[4];
    private float[] mThree = new float[3];
    private float[] mFour = new float[3];
    private float[] mFive = new float[3];
    private int[] mSix = new int[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_motion_handle);
        initView();
        initData();
        startTimeoutTimer();
    }

    private void initView() {
        AppTitleBackView titleView = (AppTitleBackView) findViewById(R.id.title_layout);
        titleView.getInvrImgBtn().setVisibility(View.GONE);
        titleView.getNameTV().setText(getResources().getString(R.string.activity_joystick_check_title));
        cur_connect = (TextView) findViewById(R.id.joystick_cur_connect);
        connect_tip = (TextView) findViewById(R.id.joystick_connecting_tip);
        connect_tip.setVisibility(View.INVISIBLE);

        mKey_longPress = (TextView) findViewById(R.id.long_press_touch_table);
        mKey_up = (TextView) findViewById(R.id.up_touch_table);
        mKey_down = (TextView) findViewById(R.id.down_touch_table);
        mKey_left = (TextView) findViewById(R.id.left_touch_table);
        mKey_right = (TextView) findViewById(R.id.right_touch_table);
        mKey_back = (TextView) findViewById(R.id.press_back);
        mKey_home = (TextView) findViewById(R.id.press_home);
        mKey_longPressHome = (TextView) findViewById(R.id.long_press_home);
        mKey_confirm = (TextView) findViewById(R.id.press_confirm_text);
        mKey_volAdd = (TextView) findViewById(R.id.key_vol_add);
        mKey_volCut = (TextView) findViewById(R.id.key_vol_cut);
        mGyro = (TextView) findViewById(R.id.gyro);


    }

    private void initData() {

        if (TextUtils.isEmpty(mMojingMotionName)) {// 未连接
            cur_connect.setText(getString(R.string.joystick_connecting));
            connect_tip.setVisibility(View.VISIBLE);
        } else {// 已连接
            String str = getResources().getString(R.string.activity_joystick_check_cur_connect);
            String name = mMojingMotionName;
            if (!TextUtils.isEmpty(name) && name.contains("_")) {
                name = name.substring(0, name.lastIndexOf("_"));
            }
            cur_connect.setText(Html.fromHtml("<font color='#999999'><b>" + str + "</b></font><font color='#7a4fc9'><b>" + name + "</b></font>"));
            connect_tip.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void startCheck() {

    }



    @Override
    public boolean onMojingKeyDown(String s, int keyCode) {
        Log.d("StickUtil", "---onMojingKeyDown keyCode = " + keyCode);
        switch (keyCode) {
            case MojingKeyCode.KEYCODE_BUTTON_B://返回键
                mKey_back.setBackgroundResource(R.drawable.corner_round_purple_bg);
                mKey_back.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_BUTTON_X://home键
                mStartHome = System.currentTimeMillis();
                mKey_home.setBackgroundResource(R.drawable.corner_round_purple_bg);
                mKey_home.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_BUTTON_A://确认键
                mStartLongPress = System.currentTimeMillis();
                mKey_confirm.setBackgroundResource(R.drawable.corner_round_purple_bg);
                mKey_confirm.setTextColor(getResources().getColor(R.color.white));
                break;

            case MojingKeyCode.KEYCODE_VOLUME_UP://音量增
                mKey_volAdd.setBackgroundResource(R.drawable.corner_round_purple_bg);
                mKey_volAdd.setTextColor(getResources().getColor(R.color.white));
                break;
            case MojingKeyCode.KEYCODE_VOLUME_DOWN://音量减
                mKey_volCut.setBackgroundResource(R.drawable.corner_round_purple_bg);
                mKey_volCut.setTextColor(getResources().getColor(R.color.white));
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

    private void reportPv() {
        ReportPVBean bean = new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("controlcheck");
        ReportBusiness.getInstance().reportPV(bean);
    }


    @Override
    public void onTouchPadPos(String s, float v, float v1) {
        super.onTouchPadPos(s, v, v1);
        LogHelper.e("StickUtil", "onTouchPadPosx==" + v + "===y===" + v1);
        if (mIsFirst) {
            if (v > 0 && v1 > 0) {
                mTouchStartX = v;
                mTouchStartY = v1;
                mIsFirst = false;
            }

        }
        if (v > 0) {
            mTouchEndX = v;
        }

        if (v1 > 0) {
            mTouchEndY = v1;
        }
        LogHelper.e("checktouch", "startx=" + mTouchStartX + "==endx==" + mTouchEndX + "==startY==" + mTouchStartY + "==endY==" + mTouchEndY);
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {
        super.onTouchPadStatusChange(s, b);
        LogHelper.e("StickUtil", "onTouchPadStatusChange==" + s + "===b===" + b);
        if (!b) {//滑动事件结束
            mIsFirst = true;
            float x = Math.abs(mTouchEndX - mTouchStartX);
            float y = Math.abs(mTouchEndY - mTouchStartY);
            if (x > y) {//左或者右
                if (mTouchEndX > mTouchStartX) {//右
                    if (x > 0.25) {
                        mKey_right.setBackgroundResource(R.drawable.corner_round_purple_bg);
                        mKey_right.setTextColor(getResources().getColor(R.color.white));
                    }
                } else {//左
                    if (x > 0.25) {
                        mKey_left.setBackgroundResource(R.drawable.corner_round_purple_bg);
                        mKey_left.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            } else {//上或者下
                if (mTouchEndY > mTouchStartY) {//下
                    if (y > 0.25) {
                        mKey_down.setBackgroundResource(R.drawable.corner_round_purple_bg);
                        mKey_down.setTextColor(getResources().getColor(R.color.white));
                    }
                } else {//上
                    if (y > 0.25) {
                        mKey_up.setBackgroundResource(R.drawable.corner_round_purple_bg);
                        mKey_up.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }
        }
    }


    @Override
    public boolean onMojingKeyUp(String s, int i) {
        Log.d("StickUtil", "--onMojingKeyUp- keyCode = " + i);
        switch (i) {
            case MojingKeyCode.KEYCODE_BUTTON_X:
                mEndHome = System.currentTimeMillis();
                if (mEndHome - mStartHome > 2000) {
                    mKey_longPressHome.setBackgroundResource(R.drawable.corner_round_purple_bg);
                    mKey_longPressHome.setTextColor(getResources().getColor(R.color.white));
                }
                break;

            case MojingKeyCode.KEYCODE_BUTTON_A:
                mEndLongPress = System.currentTimeMillis();
                if (mEndLongPress - mStartLongPress > 2000) {
                    mKey_longPress.setBackgroundResource(R.drawable.corner_round_purple_bg);
                    mKey_longPress.setTextColor(getResources().getColor(R.color.white));
                }

                break;
        }
        return super.onMojingKeyUp(s, i);
    }

    @Override
    public void onMojingDeviceAttached(String arg0) {
        if (!StickUtil.blutoothEnble()) {// 蓝牙关闭
            return;
        }
        LogHelper.e("infossss", "==========onMojingDeviceAttached============");
        if (arg0.contains("mojing-motion")) {
            mMojingMotionName = arg0;
        }
        LogHelper.e("infossss", "onMojingDeviceAttached----" + arg0);
        initData();
    }


    @Override
    public void onMojingDeviceDetached(String arg0) {
        LogHelper.e("infossss", "==========onMojingDeviceDetached============");
        if (arg0.contains("mojing-motion")) {
            mMojingMotionName = "";
        }
        initData();
        LogHelper.e("infossss", "onMojingDeviceDetached==" + arg0);
    }


    /**
     * 判断陀螺仪是否正常
     */
    int i = 0;
    private void startTimeoutTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                i++;
                //轮循判断陀螺仪
//                MojingSDK.Device_GetControlFixCurrentInfo(1,mOne,mThree,mFour,mFive,mSix);
//                MojingSDK.Device_GetInfo(1,mOne,null,null,null,null);
                MojingSDK.DeviceGetControlFixCurrentInfo(1,mOne,mThree,null,null,null);
//                if(mWaggel){
////                    LogHelper.e("timer","one1---mOne==="+mOne[0]+"==one2=="+mOne[1]+"==one3=="+mOne[2]+"==one4=="+mOne[3]);
//                    LogHelper.e("timer","mThree---mThree==="+mThree[0]+"==one2=="+mThree[1]+"==one3=="+mThree[2]);
//                        System.arraycopy(mOne,0,mTwo,0,mOne.length);
//                        mWaggel = false;
//
//                }

//                if( i > 3){
////                    LogHelper.e("timer","one1=="+mOne[0]+"==one2=="+mOne[1]+"==one3=="+mOne[2]+"==one4=="+mOne[3]);
////                    LogHelper.e("timer","Two1=="+mTwo[0]+"==Two2=="+mTwo[1]+"==Two3=="+mTwo[2]+"==Two4=="+mTwo[3]);
//                    LogHelper.e("timer","mThree======mThree==="+mThree[0]+"==one2=="+mThree[1]+"==one3=="+mThree[2]);
////                    stopTimer();
//                }


//                if(){
//                   stopTimer();
//                }
//                LogHelper.e("timer","======timer========");
                LogHelper.e("timer","mThree==="+mThree[0]+"==mThree=="+mThree[1]+"==mThree=="+mThree[2]);
                if((mThree[0] > 5 || mThree[0] < -5) && (mThree[1] > 5 || mThree[1] < -5)
                        && (mThree[1] > 3 || mThree[1] < -2)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGyro.setBackgroundResource(R.drawable.corner_round_purple_bg);
                            mGyro.setTextColor(getResources().getColor(R.color.white));
                        }
                    });
                    stopTimer();
                }
            }
        };

        timer.schedule(mTimerTask, 0,1000);

    }


    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
