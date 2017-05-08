package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.ControlSettingAdapter;
import com.baofeng.mj.ui.fragment.BaseFragment;
import com.baofeng.mj.ui.fragment.ControlModeFragment;
import com.baofeng.mj.ui.listeners.GlassesScanResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制方式设置页面
 * Created by muyu on 2016/5/19.
 */
public class ControlSettingActivity extends BaseStickActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, GlassesScanResultListener.ScanResultListener {

    private ImageButton backBtn;
    private RadioGroup controlRadioGroup;
    private RadioButton glassesRadioBtn;
    private RadioButton modeRadioBtn;
    private ViewPager controlViewPager;
    private ControlSettingAdapter adapter;
    private List<BaseFragment> fragments;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_setting);
        if (getIntent() != null) {
            from = getIntent().getIntExtra("from", 0);
        }
        initView(savedInstanceState);
        GlassesScanResultListener.getInstance().onBind(this);
    }

    private void initView(Bundle savedInstanceState) {
        backBtn = (ImageButton) findViewById(R.id.control_setting_back);
        controlRadioGroup = (RadioGroup) findViewById(R.id.control_setting_radiogroup);
        glassesRadioBtn = (RadioButton) findViewById(R.id.control_glasses);
        modeRadioBtn = (RadioButton) findViewById(R.id.control_mode);

        controlViewPager = (ViewPager) findViewById(R.id.control_viewpager);
        controlViewPager.setOnPageChangeListener(this);

        if (from == 1) {
            backBtn.setImageResource(R.drawable.nav_icon_back);
        } else {
            backBtn.setImageResource(R.drawable.my_scan_close);
        }
        backBtn.setOnClickListener(this);
        glassesRadioBtn.setOnClickListener(this);
        modeRadioBtn.setOnClickListener(this);

        fragments = new ArrayList<BaseFragment>();

        if (savedInstanceState != null) {
            List<Fragment> supportFragments = getSupportFragmentManager().getFragments();
            fragments.add((BaseFragment) supportFragments.get(0));
            fragments.add((BaseFragment) supportFragments.get(1));
        } else {
           /* GlassesFragment glassesFragment = new GlassesFragment();
            ControlModeFragment controlModeFragment = new ControlModeFragment();
            fragments.add(glassesFragment);
            fragments.add(controlModeFragment);*/
        }

        adapter = new ControlSettingAdapter(getSupportFragmentManager(), fragments);
        controlViewPager.setAdapter(adapter);
        controlRadioGroup.check(controlRadioGroup.getChildAt(0).getId());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.control_setting_back) {
            finish();
            if(from!=1){
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        } else if (i == R.id.control_glasses) {
            controlViewPager.setCurrentItem(0);
            reportClick("glassestypetab");
        } else if (i == R.id.control_mode) {
            controlViewPager.setCurrentItem(1);
            reportClick("controltypetab");
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        controlRadioGroup.check(controlRadioGroup.getChildAt(i).getId());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void startCheck() {
        if (fragments.get(1).isAdded()) {
            ((ControlModeFragment) fragments.get(1)).startCheck();
        }
    }


    @Override
    public void onScanResult(String code) {
        updateAfterScan(code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlassesScanResultListener.getInstance().unBind(this);
    }

    private void updateAfterScan(String dataCode) {
        /*if (!TextUtils.isEmpty(dataCode)) {
            String key = GlassesManager.getInstance().getGenerationGlassKey(null, dataCode);
            if (!TextUtils.isEmpty(key)) {
                SettingSpBusiness.getInstance().setGlassesModeKey(key);
                SettingSpBusiness.getInstance().setCMSGlassesId("");
                ((GlassesFragment) fragments.get(0)).callBackUpdate(key);
            } else {
                Toast.makeText(this, "扫描数据错误！", Toast.LENGTH_LONG).show();
            }
        }*/
    }

    /* 从意见反馈也返回来时 进我的主页*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201&&resultCode==200){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from != 1) {
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
    }

    @Override
    public void onTouchPadStatusChange(String s, boolean b) {

    }

    @Override
    public void onTouchPadPos(String s, float v, float v1) {

    }

   //click 报数
    private void reportClick(String devicesetting){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setPagetype("mydevice");
        bean.setDevicesetting(devicesetting);

        ReportBusiness.getInstance().reportClick(bean);
    }

    /**
     * 黑色半透蒙版 (镜片选择弹窗时显示)
     */
    public View getBlackLayer(){
       return findViewById(R.id.black_layer);
    }
}
