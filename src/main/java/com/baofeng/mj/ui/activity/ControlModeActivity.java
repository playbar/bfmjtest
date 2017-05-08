package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;

/**
 * Created by muyu on 2016/9/20.
 */
public class ControlModeActivity extends BaseActivity implements View.OnClickListener{

    private AppTitleBackView backViewLayout;
    private RelativeLayout mixLayout;
    private RelativeLayout pureLayout;

    private RadioButton mixRadioBtn;
    private RadioButton pureRadioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_mode);
        initView();
    }

    private void initView() {
        backViewLayout = (AppTitleBackView) findViewById(R.id.control_mode_title_layout);
        backViewLayout.getNameTV().setText("手柄控制模式");
        backViewLayout.getInvrImgBtn().setVisibility(View.GONE);

        mixLayout = (RelativeLayout) findViewById(R.id.guide_control_mode_mix_layout);
        pureLayout = (RelativeLayout) findViewById(R.id.guide_control_mode_pure_layout);

        mixRadioBtn = (RadioButton) findViewById(R.id.guide_control_mix_radiobtn);
        pureRadioBtn = (RadioButton) findViewById(R.id.guide_control_pure_radiobtn);

        mixRadioBtn.setOnClickListener(this);
        pureRadioBtn.setOnClickListener(this);

        if(SettingSpBusiness.getInstance().getControlMode() == 1){
            setLayoutStatus(false, true);
        } else {
            setLayoutStatus(true,false);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.guide_control_mix_radiobtn){
            setLayoutStatus(true, false);
            SettingSpBusiness.getInstance().setControlMode(0);
        } else if(id == R.id.guide_control_pure_radiobtn){
            setLayoutStatus(false,true);
            SettingSpBusiness.getInstance().setControlMode(1);
        }
    }

    private void setLayoutStatus(boolean mixStatus,boolean pureStatus){
        mixLayout.setSelected(mixStatus);
        mixRadioBtn.setChecked(mixStatus);
        pureLayout.setSelected(pureStatus);
        pureRadioBtn.setChecked(pureStatus);
    }
}
