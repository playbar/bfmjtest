package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.adapter.ChargeAdapter;
import com.baofeng.mj.ui.fragment.BaseFragment;
import com.baofeng.mj.ui.fragment.ChargeFragment;
import com.baofeng.mj.ui.fragment.ExchangeFragment;
import com.baofeng.mj.util.viewutil.LanguageValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值界面
 */
public class ChargeActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton charge_tab, exchange_tab;
    private ViewPager charge_viewpager;
    private ChargeAdapter chargeAdapter;
    private List<BaseFragment> baseFragmentList = new ArrayList<BaseFragment>();
    private ImageButton charge_back, charge_record;
    private TextView charge_title;
    private int curTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        curTab = getIntent().getIntExtra("curTab", 0);
        find();
    }

    private void find() {
        charge_back = (ImageButton) findViewById(R.id.charge_back);
        charge_title = (TextView) findViewById(R.id.charge_title);
        charge_record = (ImageButton) findViewById(R.id.charge_record);
        charge_back.setOnClickListener(this);
        charge_record.setOnClickListener(this);
        charge_title.setText(LanguageValue.getInstance().getValue(this, "SID_RECHARGE"));
        charge_tab = (RadioButton) findViewById(R.id.charge_tab);
        charge_tab.setOnClickListener(this);
        exchange_tab = (RadioButton) findViewById(R.id.exchange_tab);
        exchange_tab.setOnClickListener(this);
        charge_viewpager = (ViewPager) findViewById(R.id.charge_viewpager);
        charge_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    charge_tab.setChecked(true);
                } else if(i == 1){
                    exchange_tab.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        ChargeFragment chargeFragment = new ChargeFragment();
        baseFragmentList.add(chargeFragment);
        ExchangeFragment exChargeFragment = new ExchangeFragment();
        baseFragmentList.add(exChargeFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        chargeAdapter = new ChargeAdapter(fragmentManager, baseFragmentList);
        charge_viewpager.setAdapter(chargeAdapter);
        charge_viewpager.setCurrentItem(curTab);
        if(curTab == 0){
            charge_tab.setChecked(true);
        } else if(curTab == 1){
            exchange_tab.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.charge_tab == id) {
            charge_viewpager.setCurrentItem(0);
        } else if (R.id.exchange_tab == id) {
            charge_viewpager.setCurrentItem(1);
        } else if (R.id.charge_back == id) {
            finish();
        } else if (R.id.charge_record == id) {
            Intent intent = new Intent(this, RechargeRecordActivity.class);
            startActivity(intent);
        }
    }
}
