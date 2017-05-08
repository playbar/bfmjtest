package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.sdk.gvr.vrcore.settings.VrHeadDeviceSelectActivity;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.fragment.BaseFragment;
import com.baofeng.mj.ui.fragment.GuideFragment;
import com.baofeng.mj.ui.fragment.GuideTipsFragment;
import com.baofeng.mj.ui.view.NoSwipeViewPager;

import java.util.ArrayList;
import java.util.List;

/**引导页
 * Created by muyu on 2016/9/18.
 */
public class GuideActivity extends BaseActivity {

//    private NoSwipeViewPager viewPager;
//    private GuideAdapter adapter;
//    private int currentNum;
//
//    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }




}
