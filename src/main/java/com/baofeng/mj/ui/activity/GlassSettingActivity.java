package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.viewutil.LanguageValue;

/**
 * Created by hanyang on 2016/5/25.
 * 眼镜高级设置界面
 */
public class GlassSettingActivity extends BaseActivity implements View.OnClickListener {
    private AppTitleBackView backView;
    private TextView sur_switch;//曲面开关
    private TextView bg_switch;//球模背景开关
    private TextView anti_aliasing_switch,anti_ali_tag;//反锯齿开关
    private TextView tran_ani_switch;//过渡动画特效开关
    private TextView trans_switch;//透明效果开关
    private TextView mask_switch;//Mask特效开关

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_advanced_settings);
        findView();
    }

    private void findView() {
        backView = (AppTitleBackView) findViewById(R.id.glasses_advanced_title_layout);
        backView.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_ADVANCE_SETTING"));
        backView.getInvrImgBtn().setVisibility(View.GONE);
        sur_switch = (TextView) findViewById(R.id.sur_switch);
        sur_switch.setOnClickListener(this);
        bg_switch = (TextView) findViewById(R.id.bg_switch);
        bg_switch.setOnClickListener(this);
        anti_aliasing_switch = (TextView) findViewById(R.id.anti_aliasing_switch);
        anti_aliasing_switch.setOnClickListener(this);
        tran_ani_switch = (TextView) findViewById(R.id.tran_ani_switch);
        tran_ani_switch.setOnClickListener(this);
        trans_switch = (TextView) findViewById(R.id.trans_switch);
        trans_switch.setOnClickListener(this);
        mask_switch = (TextView) findViewById(R.id.mask_switch);
        mask_switch.setOnClickListener(this);
        anti_ali_tag=(TextView)findViewById(R.id.anti_ali_tag);
        anti_ali_tag.setText(LanguageValue.getInstance().getValue(this, "SID_ANT_SAWTOOTH"));
        initView();
    }

    /**
     * 按钮初始化
     */
    private void initView() {
        SettingSpBusiness spBusiness = SettingSpBusiness.getInstance();
        int sur_tag = spBusiness.getSur_Switch();
        int bg_tag = spBusiness.getBgSwitch();
        int ani_tag = spBusiness.getAnti_aliasing();
        int tran_ani_tag = spBusiness.getTrans_Ani_Switch();
        int tran_tag = spBusiness.getTrans_Switch();
        int mask_tag = spBusiness.getMask();
        //曲面开关
        if (sur_tag == 0) {
            sur_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            sur_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
        //球模
        if (bg_tag == 0) {
            bg_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            bg_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
        //反锯齿
        if (ani_tag == 0) {
            anti_aliasing_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            anti_aliasing_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
        //过渡动画特效
        if (tran_ani_tag == 0) {
            tran_ani_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            tran_ani_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
        //透明
        if (tran_tag == 0) {
            trans_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            trans_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
        //Mask特效
        if (mask_tag == 0) {
            mask_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
        } else {
            mask_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        setTag(id);
    }

    /**
     * 保存设置值以及改变状态背景
     *
     * @param id
     */
    private void setTag(int id) {
        SettingSpBusiness spBusiness = SettingSpBusiness.getInstance();
        int sur_tag = spBusiness.getSur_Switch();
        int bg_tag = spBusiness.getBgSwitch();
        int ani_tag = spBusiness.getAnti_aliasing();
        int tran_ani_tag = spBusiness.getTrans_Ani_Switch();
        int trans_tag = spBusiness.getTrans_Switch();
        int mask_tag = spBusiness.getMask();
        //曲面开关
        if (R.id.sur_switch == id) {
            if (sur_tag == 0) {
                spBusiness.setSur_Switch(1);
                sur_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setSur_Switch(0);
                sur_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        } else if (id == R.id.bg_switch) {//球模背景
            if (bg_tag == 0) {
                spBusiness.setBgSwitch(1);
                bg_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setBgSwitch(0);
                bg_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        } else if (id == R.id.anti_aliasing_switch) {//反锯齿
            if (ani_tag == 0) {
                spBusiness.setAnti_aliasing(1);
                anti_aliasing_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setAnti_aliasing(0);
                anti_aliasing_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        } else if (id == R.id.tran_ani_switch) {//过渡动画
            if (tran_ani_tag == 0) {
                spBusiness.setTrans_Ani_Switch(1);
                tran_ani_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setTrans_Ani_Switch(0);
                tran_ani_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        } else if (id == R.id.trans_switch) {//透明
            if (trans_tag == 0) {
                spBusiness.setTrans_Switch(1);
                trans_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setTrans_Switch(0);
                trans_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        } else if (id == R.id.mask_switch) {//Mask特效
            if (mask_tag == 0) {
                spBusiness.setMask(1);
                mask_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchon));
            } else {
                spBusiness.setMask(0);
                mask_switch.setBackground(getResources().getDrawable(R.drawable.my_setting_switchoff));
            }
        }
    }
}
