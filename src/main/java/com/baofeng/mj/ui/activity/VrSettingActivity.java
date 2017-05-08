package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.stickutil.StickUtil;

/**
 * Created by dupengwei on 2017/4/17.
 */

public class VrSettingActivity extends BaseStickActivity implements View.OnClickListener {

    private AppTitleBackView mAppTitleLayout;
    private RelativeLayout mDeviceSelectLayout;
    private RelativeLayout mFeelingHandleLayout;
    private CheckBox mIsLeftCheckBox;
    private RelativeLayout mCommonHandleLayout;
    private RelativeLayout mShowLayout;
    private RelativeLayout mScreenSelectLayout;
    private TextView mScreenSizeNameTextView;
    private TextView mDeviceNameTextView;
    private TextView body_feeling_handle_text;
    private TextView common_handle_text;
    private String mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_setting);
        initView();
        initData();
    }

    private void initData() {
        GlassesNetBean dataBean = GlassesManager.getGlassesNetBean();
        if(null != dataBean && dataBean.isSelected()) {
            mDeviceNameTextView.setText(dataBean.getGlass_name());
        }else{
            mDeviceNameTextView.setText("未设置");
        }
    }

    private void initView() {
        mAppTitleLayout = (AppTitleBackView) findViewById(R.id.vr_setting_title_layout);
        mAppTitleLayout.getNameTV().setText(getResources().getString(R.string.vr_setting));
        mAppTitleLayout.getInvrImgBtn().setVisibility(View.GONE);

        mDeviceSelectLayout = (RelativeLayout) findViewById(R.id.device_select_layout);
        mDeviceSelectLayout.setOnClickListener(this);

        mFeelingHandleLayout = (RelativeLayout) findViewById(R.id.body_feeling_handle_layout);
        mFeelingHandleLayout.setOnClickListener(this);

        mIsLeftCheckBox = (CheckBox) findViewById(R.id.is_left_checkBox);
        mIsLeftCheckBox.setOnCheckedChangeListener(mCheckChangeListener);

        if(SettingSpBusiness.getInstance().getLeftMode()){
            mIsLeftCheckBox.setChecked(true);
        }else {
            mIsLeftCheckBox.setChecked(false);
        }

        if(mIsLeftCheckBox.isChecked()){

        }else{

        }
        mCommonHandleLayout = (RelativeLayout) findViewById(R.id.common_handle_layout);
        mCommonHandleLayout.setOnClickListener(this);

        mShowLayout = (RelativeLayout) findViewById(R.id.vr_setting_show);
        mShowLayout.setOnClickListener(this);

        mScreenSelectLayout = (RelativeLayout) findViewById(R.id.device_screen_select_layout);
        mScreenSelectLayout.setOnClickListener(this);


        mScreenSizeNameTextView = (TextView) findViewById(R.id.vr_device_screen_name);

        mDeviceNameTextView = (TextView) findViewById(R.id.vr_device_name);
        body_feeling_handle_text = (TextView) findViewById(R.id.body_feeling_handle_text);
        common_handle_text = (TextView) findViewById(R.id.common_handle_text);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.device_select_layout:
                Intent intent = new Intent("com.main.intent.action.vr.DEVICE_SELECT");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra("from", ConstantKey.FROM_MAIN_APP_SETTING);
                startActivityForResult(intent, 100);
                break;
            case R.id.body_feeling_handle_layout:
                if(TextUtils.isEmpty(mDeviceName)){//未连接状态
                    Intent helpActivity = new Intent(this,HelpActivity.class);
                    helpActivity.putExtra(HelpActivity.SHOW,false);
                    helpActivity.putExtra(HelpActivity.IS_MJ,false);
                    startActivity(helpActivity);
                }else {
                    startActivity(new Intent(this,MotionHandleCheckActivity.class));
                }

                break;
            case R.id.common_handle_layout:
                if(TextUtils.isEmpty(mDeviceName)){//未连接状态
                        Intent helpActivity = new Intent(this,HelpActivity.class);
                        helpActivity.putExtra(HelpActivity.SHOW,false);

                        //5代镜片
                        String manufactureid = ConstantKey.ManufactureID_MJ5;
                        String productid = ConstantKey.ProductID_MJ5;
                        String glassesid = ConstantKey.GlassesID_MJ5;
                        GlassesNetBean bean = GlassesManager.getGlassesNetBean();
                        if(bean != null && manufactureid.equals(bean.getCompany_id()) && productid.equals(bean.getProduct_id()) && glassesid.equals(bean.getLens_id())){
                            helpActivity.putExtra(HelpActivity.IS_MJ,true);
                        }else{
                            helpActivity.putExtra(HelpActivity.IS_MJ,false);
                        }

                        startActivity(helpActivity);
                }else {
                    startActivity(new Intent(this,JoystickCheckActivity.class));
                }
                break;
            case R.id.vr_setting_show:
                startActivity(new Intent(this, GlassSettingActivity.class));
                break;
            case R.id.device_screen_select_layout:
                Intent resolSettingIntent = new Intent(this, ResolSettingActivity.class);
                if (TextUtils.isEmpty(getSize())) {
                    resolSettingIntent.putExtra("size", "");
                } else {
                    resolSettingIntent.putExtra("size", getSize());
                }
                startActivity(resolSettingIntent);
                break;
        }
    }

    @Override
    public void startCheck() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(getSize())) {
            mScreenSizeNameTextView.setText("点击设置");
        } else {
            mScreenSizeNameTextView.setText(getSize() + "英寸");
        }


    }

    private CompoundButton.OnCheckedChangeListener mCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            SettingSpBusiness.getInstance().setLeftMode(b);
            if(b){

            }else {

            }

        }
    };

    /**
     * 获取用户选择的尺寸
     *
     * @return
     */
    private String getSize() {
        return getSharedPreferences("size", MODE_PRIVATE).getString("size", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        if(100 == requestCode){
            GlassesNetBean dataBean = GlassesManager.getGlassesNetBean();
            if(null != dataBean && dataBean.isSelected()){
                mDeviceNameTextView.setText(dataBean.getGlass_name());
            }


            boolean isFeedback = data.getBooleanExtra("isFeedback", false);
            if(isFeedback){
                Intent intent = new Intent(this, FeedbackActivity.class);
                intent.putExtra("from","glasses_view");
                startActivityForResult(intent,201);

            }
        }

    }

    @Override
    public void onMojingDeviceAttached(final String arg0) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceName = arg0;
                if(StickUtil.isbodyFeelingHandle(arg0)){//体感手柄
                    if(body_feeling_handle_text != null){
                        body_feeling_handle_text.setText("手柄按键校验");
                    }
                }else if(StickUtil.isCommonHandle(arg0)){//普通手柄
                    if(common_handle_text != null){
                        common_handle_text.setText("手柄按键校验");
                    }
                }
            }
        });
    }

    @Override
    public void onMojingDeviceDetached(final String arg0) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceName = "";
                if(StickUtil.isbodyFeelingHandle(arg0)){//体感手柄
                    if(body_feeling_handle_text != null){
                        body_feeling_handle_text.setText("查看连接说明");
                    }
                }else if(StickUtil.isCommonHandle(arg0)){//普通手柄
                    if(common_handle_text != null){
                        common_handle_text.setText("查看连接说明");
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        BaseApplication.INSTANCE.setOrientationMode(false);
        super.onPause();
    }
}
