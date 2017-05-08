package com.baofeng.mj.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.ui.fragment.ContentFragment;
import com.baofeng.mj.ui.fragment.Home2DFragment;
import com.baofeng.mj.ui.fragment.HomeVRFragment;
import java.util.List;

/**
 * 首页适配器
 * Created by muyu on 2017/3/2.
 */
public class HomeAdapter extends FragmentStatePagerAdapter {
//    private List<ChoicenessFragment> fragments;
    private List<MainSubTabBean> mVideoSubTabInfo;
    private ContentFragment contentFragment;
    private int mCurrentTab;
    private int count;
    private Bundle bundle;
    private HomeVRFragment homeVRfragment;
    private Home2DFragment home2Dfragment;

    public HomeAdapter(FragmentManager fm, List<MainSubTabBean> videoSubTabInfo, int currentTab) {
        super(fm);
        this.mVideoSubTabInfo = videoSubTabInfo;
        this.mCurrentTab = currentTab;
        this.count = mVideoSubTabInfo.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                homeVRfragment = new HomeVRFragment();
                bundle = new Bundle();
                bundle.putString("categoryUrl", mVideoSubTabInfo.get(position).getCategory_url());
                homeVRfragment.setArguments(bundle);
                return homeVRfragment;

            case 2:
                home2Dfragment = new Home2DFragment();
                bundle = new Bundle();
                bundle.putString("categoryUrl", mVideoSubTabInfo.get(position).getCategory_url());
                home2Dfragment.setArguments(bundle);
                bundle = null;
                return home2Dfragment;

            default:
                contentFragment = new ContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", position);
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mVideoSubTabInfo.get(position).getUrl());
                contentFragment.setArguments(bundle);
                return contentFragment;
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
