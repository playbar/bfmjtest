package com.baofeng.mj.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.ui.fragment.ChoicenessFragment;
import com.baofeng.mj.ui.fragment.SingleListContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频适配器
 * Created by muyu on 2016/5/4.
 */
public class VideoAdapter extends FragmentStatePagerAdapter {
//    private List<ChoicenessFragment> fragments;
    private ChoicenessFragment fragment;
    private Bundle bundle;
    private int mCurrentTab;
    private List<MainSubTabBean> mVideoSubTabInfo;
    private int count;
    private SingleListContentFragment singleListContentFragment;
    public VideoAdapter(FragmentManager fm, List<MainSubTabBean> videoSubTabInfo, int currentTab) {
        super(fm);
        this.mCurrentTab = currentTab;
        this.mVideoSubTabInfo = videoSubTabInfo;
        count = mVideoSubTabInfo.size();
//        fragments = new ArrayList<ChoicenessFragment>();
//        for(int i = 0; i< videoSubTabInfo.size(); i++){
//            fragment = new ChoicenessFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("currentTab", currentTab);
//            bundle.putInt("subCurrentTab",i);
//            bundle.putBoolean("show_title", false);
//            bundle.putString("next_url", mVideoSubTabInfo.get(i).getUrl());
//            fragment.setArguments(bundle);
//            fragments.add(fragment);
//        }
    }

    @Override
    public Fragment getItem(int position) {

        int type = mVideoSubTabInfo.get(position).getType();
        switch (type){
            case 9:
                fragment = new ChoicenessFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", position);
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mVideoSubTabInfo.get(position).getUrl());
                bundle.putInt("res_id",mVideoSubTabInfo.get(position).getRes_id());
                fragment.setArguments(bundle);
                return fragment;
            case 11:
                singleListContentFragment = new SingleListContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", 0);
                bundle.putInt("categoryTab", position); //分类中的tab
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mVideoSubTabInfo.get(position).getUrl());
                bundle.putBoolean("showPop", false);
                singleListContentFragment.setArguments(bundle);
                return singleListContentFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

}
