package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.unity.UnityActivity;
import com.baofeng.mj.util.publicutil.MinifyImageUtil;
import com.baofeng.mj.util.stickutil.StickUtil;
import com.baofeng.mj.util.viewutil.TimeCount;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.storm.smart.common.utils.LogHelper;

/**
 * 进入Unity页面
 * Created by muyu on 2016/5/5.
 */
public class GoUnity extends BaseStickActivity implements SensorEventListener,View.OnClickListener{

    private SensorManager mSensorManager;
    private ImageButton settingBtn;
    private ImageView backBtn;
    private TextView glassTV;
    private TextView controlTV;
    private TimeCount timeCount;
    private TextView timeTV;
    private String str;

    private LinearLayout bgLayout;
    private Bitmap backgroundBitmap;

    private AudioManager mAudioManager; //系统音频管理
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener; //音频焦点监听器

    private boolean isFinish;//当前activity是否执行了finish

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gounity);
        initView();
        processIntent();//处理意图
    }

    private void initView(){
        bgLayout = (LinearLayout) findViewById(R.id.go_unity_bg);
        backgroundBitmap = MinifyImageUtil.getInstance().zoomBitmap(this);
        bgLayout.setBackground(new BitmapDrawable(this.getResources(), backgroundBitmap));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        settingBtn = (ImageButton) findViewById(R.id.go_unity_setting);
        settingBtn.setOnClickListener(this);
        backBtn = (ImageView) findViewById(R.id.go_unity_back);
        backBtn.setOnClickListener(this);
        glassTV = (TextView) findViewById(R.id.go_unity_glass);
        controlTV = (TextView) findViewById(R.id.go_unity_control);
        timeTV = (TextView) findViewById(R.id.go_unity_second);//数3秒

        startCheck();
    }

    private void processIntent(){
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        String type = intent.getStringExtra("type");
        if(type == null){
            type = "";
        }
        if(!TextUtils.isEmpty(type)){
            String subType = intent.getStringExtra("subType");
            if(subType == null){
                subType = "";
            }
            String detailUrl = intent.getStringExtra("detailUrl");
            if(detailUrl == null){
                detailUrl = "";
            }
            String contents = intent.getStringExtra("contents");
            if(contents == null){
                contents = "";
            }
            String nav = intent.getStringExtra("nav");
            if(nav == null){
                nav = "";
            }
            String name = intent.getStringExtra("name");
            if(name == null){
                name = "";
            }
            String resourcePath = intent.getStringExtra("resourcePath");
            if(resourcePath == null){
                resourcePath = "";
            }
            String seq = intent.getStringExtra("currentVideoSeq");
            if(seq == null){
                seq = "";
            }
            String download_url = intent.getStringExtra("download_url");
            if(download_url == null){
                download_url = "";
            }
            String local_resource_from = intent.getStringExtra("local_resource_from");
            if(local_resource_from == null){
                local_resource_from = "";
            }
            String online_resource_from = intent.getStringExtra("online_resource_from");
            if(online_resource_from == null){
                online_resource_from = "";
            }
            String videoType = intent.getStringExtra("videoType");
            if(videoType == null){
                videoType = "";
            }
            String pageType = intent.getStringExtra("pageType");
            if(pageType == null){
                pageType = "";
            }
            String is4k = intent.getStringExtra("is4k");
            if(is4k == null){
                is4k = "";
            }
            initPlayParam(type, subType, detailUrl, contents, nav, name, resourcePath, seq, download_url, local_resource_from, videoType, pageType, online_resource_from,is4k);
            str = JSON.toJSONString(BaseApplication.INSTANCE.hierarchyBeanList, SerializerFeature.DisableCircularReferenceDetect);
            Log.d("gounity","----processIntent =="+str);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        if(timeCount != null) {
            timeCount.cancel();
        }
    }

    public void updateGlassesInfo() {
        GlassesNetBean bean = GlassesManager.getGlassesNetBean();
        if(null != bean && bean.isSelected()){
            glassTV.setText(bean.getGlass_name());
        }else {
            glassTV.setText("未设置");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlassesInfo();
        if(mSensorManager != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
        }
        timeCount = new TimeCount(GoUnity.this, 3200, 1000, str, timeTV);
        timeCount.start();
    }

    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;
    public static final int ORIENTATION_UNKNOWN = -1;
    private static final int HANDLER_CODE = 888;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_CODE:
                    int orientation = msg.arg1;
                    if (orientation > 240 && orientation < 300) {
                        if(!isFinish) {
                            if (timeCount != null) {
                                timeCount.cancel();
                            }
                            rportVV("move");
                            mAudioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
                            mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
                                @Override
                                public void onAudioFocusChange(int i) {

                                }
                            };
                            int result = mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) ;

                            LogHelper.e("px", "--------GoUnity--stopAll--------------");
                            MjDownloadSDK.stopAll(BaseApplication.INSTANCE);
                            DownloadUtils.getInstance().mIsInit = false;
                            Intent intent = new Intent(GoUnity.this, UnityActivity.class);
                            intent.putExtra("hierarchy", str);
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void finish() {
        super.finish();
        isFinish = true;
        StickUtil.setCallback(null);
        StickUtil.disconnect();
//        MjDownloadSDK.stopAll(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int sensorType = event.sensor.getType();
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            int orientation =ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;

//             Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (mHandler != null) {
                mHandler.obtainMessage(HANDLER_CODE, orientation, 0).sendToTarget();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.go_unity_setting){
            reportClick("jump");
            startActivity(new Intent(this, VrSettingActivity.class));
            overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
        } else if(i == R.id.go_unity_back){
            reportClick("close");
            finish();
        }
    }

    @Override
    public synchronized void startCheck() {
        if(BaseApplication.INSTANCE.isBFMJ5Connection() && BaseApplication.INSTANCE.getJoystickConnect()){ //先判断魔镜5是否连接，并且遥控器连接上
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


    private void unConnectZkey() {
//        DefaultSharedPreferenceManager.getInstance().setHeadContol(true);// 头控可用
        controlTV.setText("头控");
    }

    private void connectZkey() {
        controlTV.setText("遥控器");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!backgroundBitmap.isRecycled() && backgroundBitmap!= null){
            backgroundBitmap.recycle();
        }

//        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {

    }

    @Override
    public void onTouchPadPos(String s, float v, float v1) {

    }


    //click 报数
    private void reportClick(String clickType){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(clickType);
        bean.setTpos("1");
        bean.setPagetype("countdown");

        ReportBusiness.getInstance().reportClick(bean);
    }


    private void rportVV(String counttype) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("count");
        bean.setPagetype("countdown");
        bean.setCountype(counttype);
        ReportBusiness.getInstance().reportClick(bean);
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogHelper.e("infossss","keyCode================="+keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            LogHelper.e("infossss","keyCode==="+keyCode);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
