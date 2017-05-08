package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.RoundProgressBar;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mojing.MojingSDK;

/**
 * 陀螺仪校验页面
 * Created by muyu on 2016/5/18.
 */
public class GyroscopeActivity extends BaseActivity implements View.OnClickListener {
    private AppTitleBackView gyroscope_check_title;
    private RoundProgressBar gyroscope_roundprogressbar;
    private TextView gyroscope_check_tip;
    private TextView gyroscope_check_notice;
    private TextView gyroscope_check_begin;
    private int count = 0;
    private boolean checkTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        initView();
    }

    /**
     * view初始化
     */
    private void initView() {
        gyroscope_check_title = (AppTitleBackView) findViewById(R.id.gyroscope_check_title);
        gyroscope_check_title.getNameTV().setText("头控灵敏度校准");
        gyroscope_roundprogressbar = (RoundProgressBar) findViewById(R.id.gyroscope_roundprogressbar);
        gyroscope_check_tip = (TextView) findViewById(R.id.gyroscope_check_tip);
        gyroscope_check_notice = (TextView) findViewById(R.id.gyroscope_check_notice);
        gyroscope_check_begin = (TextView) findViewById(R.id.gyroscope_check_begin);
        gyroscope_check_begin.setText(LanguageValue.getInstance().getValue(this, "SID_BEGIN_VERIFICATION"));
        gyroscope_check_begin.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        checkTag = false;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//返回键
            checkTag = false;
            finish();
            overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.gyroscope_check_begin == id) {
            if (gyroscope_check_begin.getText().toString().trim().equals("开始校准") || gyroscope_check_begin.getText().toString().trim().equals("重新校准")) {
                gyroscope_check_begin.setText(LanguageValue.getInstance().getValue(this, "SID_CANCEL_VERIFICATION"));
                gyroscope_check_tip.setVisibility(View.VISIBLE);
                gyroscope_check_tip.setText("正在校准");
                gyroscope_check_notice.setVisibility(View.GONE);
                gyroscope_check_begin.setClickable(false);
                startCheck();
            } else {
                checkTag = false;
                finish();
                overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
            }
        }
    }

    /**
     * 开始校准
     */
    private void startCheck() {
        checkTag = true;
        new Thread() {// 开启线程
            public void run() {
                MojingSDK.StartTrackerCalibration();
                MojingSDK.StartTracker(100);
                final int checkSensorsResult = MojingSDK.CheckSensors();
                if (checkSensorsResult == 0) {
                    while (checkTag) {
                        final float fValue = MojingSDK.IsTrackerCalibrated();// 当前校正进度
                        runOnUiThread(new Runnable() {// 运行在主线程（更新界面）
                            @Override
                            public void run() {// 显示当前校正进度
                                gyroscope_check_begin.setClickable(true);
                                if (count >= 667) {// 校对失败（过了20秒，线程休眠30毫秒
                                    // ，20秒=20000毫秒，20000/30=666.6，约等于667）
                                    checkTag = false;
                                    checkFail(gyroscope_check_tip, gyroscope_roundprogressbar, "校准失败", gyroscope_check_begin);// 刷新界面(校正失败)
                                } else if (fValue >= 0.9998) {
                                    checkTag = false;
                                    checkEnd(gyroscope_check_tip, gyroscope_roundprogressbar, gyroscope_check_begin);
                                } else {
                                    checkIng(gyroscope_roundprogressbar, fValue, gyroscope_check_begin);// 刷新界面（校正过程中）
                                }
                            }
                        });
                        count++;
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    int result = checkResult(checkSensorsResult);
                    gyroscope_check_begin.setClickable(true);
                    if (result == 0) {
                        runOnUiThread(new Runnable() {// 没有陀螺仪
                            @Override
                            public void run() {
                                checkFail(gyroscope_check_tip, gyroscope_roundprogressbar, getResources().getString(R.string.msg_no_gyroscope), gyroscope_check_begin);
                            }
                        });
                    } else if (result == 4) {//校验失败
                        runOnUiThread(new Runnable() {// 陀螺仪档次不够
                            @Override
                            public void run() {
                                checkFail(gyroscope_check_tip, gyroscope_roundprogressbar, getResources().getString(R.string.msg_error_gyroscope), gyroscope_check_begin);
                            }
                        });
                    } else {//校验异常
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkFail(gyroscope_check_tip, gyroscope_roundprogressbar, getResources().getString(R.string.msg_warn_gyroscope), gyroscope_check_begin);
                            }
                        });
                    }
                }
            }
        }.start();
    }

    /**
     * 校验失败
     *
     * @param gyroscope_check_tip
     * @param gyroscope_roundprogressbar
     */
    private void checkFail(TextView gyroscope_check_tip, RoundProgressBar gyroscope_roundprogressbar, String msg, TextView gyroscope_check_begin) {
        gyroscope_roundprogressbar.setProgress(100);
        gyroscope_check_tip.setText(msg);
        gyroscope_check_tip.setVisibility(View.VISIBLE);
        gyroscope_check_begin.setText("重新校准");
    }

    /**
     * 校验结束
     *
     * @param gyroscope_check_tip
     * @param gyroscope_roundprogressbar
     */
    private void checkEnd(TextView gyroscope_check_tip, RoundProgressBar gyroscope_roundprogressbar, TextView gyroscope_check_begin) {
        gyroscope_roundprogressbar.setProgress(100);
        gyroscope_check_tip.setText("校准完毕");
        gyroscope_check_tip.setVisibility(View.VISIBLE);
        gyroscope_check_begin.setText("完成");
    }

    /**
     * 校验进度
     *
     * @param gyroscope_roundprogressbar
     * @param processValue
     */
    private void checkIng(RoundProgressBar gyroscope_roundprogressbar, float processValue, TextView gyroscope_check_begin) {
        if (processValue < 0) {
            processValue = 0;
        } else if (processValue > 1) {
            processValue = 1;
        }
        gyroscope_roundprogressbar.setProgress((int) (processValue * 100));
        gyroscope_check_begin.setText("校准中");
    }

    /**
     * 判断陀螺仪校验异常或者失败
     *
     * @param sensorResult
     * @return 等于4失败，大于等于1小于4异常
     */
    private int checkResult(int sensorResult) {
        int checkResult = 0;
        //1、没有地磁，4、没有陀螺仪、8、陀螺仪采样速率过低<50hz， 16、没有加速度传感器，32、加速度传感器采样速率过低<50hz
        int[] standResult = {4, 1, 8, 16, 32};
        for (int i = 0; i < standResult.length; i++) {
            if (i == 0 && (sensorResult & standResult[i]) == standResult[i]) {
                return checkResult;
            } else {
                if ((sensorResult & standResult[i]) == standResult[i]) {
                    checkResult++;
                }
            }
        }
        return checkResult;
    }
}
