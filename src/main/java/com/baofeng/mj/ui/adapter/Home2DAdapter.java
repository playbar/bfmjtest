package com.baofeng.mj.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.ui.fragment.BaseViewPagerFragment;
import com.baofeng.mj.ui.fragment.ContentFragment;
import com.baofeng.mj.ui.fragment.SingleListContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Home 2D 适配器
 * Created by muyu on 2017/3/8.
 */
public class Home2DAdapter extends FragmentStatePagerAdapter {
//    private List<BaseViewPagerFragment> fragments;
    private List<SelectListBean<SelectListBean<SelectDetailBean>>> mHomeSubTab2DBeans;
    private int mCurrentTab;
    private String mUrl;
    private SingleListContentFragment singleListContentFragment;
    private ContentFragment fragment;
    private Bundle bundle;
    private int count;

    public Home2DAdapter(FragmentManager fm, List<SelectListBean<SelectListBean<SelectDetailBean>>>  homeSubTab2DBeans, String url,int currentTab) {
        super(fm);
        this.mHomeSubTab2DBeans = homeSubTab2DBeans;
        this.mCurrentTab = currentTab;
        this.mUrl = url;
        count = mHomeSubTab2DBeans.size();
//        fragments = new ArrayList<BaseViewPagerFragment>();
//        ContentFragment fragment = new ContentFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("currentTab", mCurrentTab);
//        bundle.putInt("subCurrentTab", 0);
//        bundle.putBoolean("show_title", false);
//        bundle.putString("next_url", mHomeSubTab2DBeans.get(0).getList_url());
//        fragment.setArguments(bundle);
//        fragments.add(fragment);
//
//        for (int i = 1; i< homeSubTab2DBeans.size(); i++){
//            singleListContentFragment = new SingleListContentFragment();
//            Bundle singleListBundle = new Bundle();
//            singleListBundle.putInt("currentTab", mCurrentTab);
//            singleListBundle.putInt("subCurrentTab", i);
//            singleListBundle.putBoolean("show_title", false);
//            singleListBundle.putBoolean("showPop", true);
//            singleListBundle.putString("headUrl", mUrl);
//            singleListBundle.putSerializable("categoryData", mHomeSubTab2DBeans.get(i));
//            singleListContentFragment.setArguments(singleListBundle);
//            fragments.add(singleListContentFragment);
//        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                fragment = new ContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", 2);
                bundle.putInt("categoryTab", position); //分类中的tab
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mHomeSubTab2DBeans.get(position).getList_url());
                bundle.putInt("res_id", mHomeSubTab2DBeans.get(position).getRes_id());
                fragment.setArguments(bundle);
                return fragment;

            default:
                singleListContentFragment = new SingleListContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", 2);
                bundle.putInt("categoryTab", position); //分类中的tab
                bundle.putBoolean("show_title", false);
                bundle.putBoolean("showPop", true);
                bundle.putString("headUrl", mUrl);
                bundle.putSerializable("categoryData", mHomeSubTab2DBeans.get(position));
                singleListContentFragment.setArguments(bundle);
                return singleListContentFragment;
        }
//        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return count;
    }

}
