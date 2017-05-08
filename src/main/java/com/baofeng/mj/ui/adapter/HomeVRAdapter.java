package com.baofeng.mj.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baofeng.mj.bean.HomeSubTabVRBean;
import com.baofeng.mj.ui.fragment.ContentFragment;
import com.baofeng.mj.ui.fragment.SingleListContentFragment;

import java.util.List;

/**
 * Home VR 适配器
 * Created by muyu on 2017/3/7.
 */
public class HomeVRAdapter extends FragmentStatePagerAdapter {
//    private List<ContentFragment> fragments;
    private List<HomeSubTabVRBean> mHomeSubTabVRBeans;
    private int mCurrentTab;  //首页中的 VR 3D 2D
    private int count;
    private Bundle bundle;
    private ContentFragment fragment;
    private SingleListContentFragment singleListContentFragment;

    public HomeVRAdapter(FragmentManager fm, List<HomeSubTabVRBean> homeSubTabVRBeans, int currentTab) {
        super(fm);
        this.mHomeSubTabVRBeans = homeSubTabVRBeans;
        this.mCurrentTab = currentTab;
        this.count = mHomeSubTabVRBeans.size();
//        fragments = new ArrayList<ContentFragment>();
//        for(int i = 0; i< mHomeSubTabVRBeans.size(); i++){
//            ContentFragment fragment = new ContentFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("currentTab", currentTab);
//            bundle.putInt("subCurrentTab", i);
//            bundle.putBoolean("show_title", false);
//            bundle.putString("next_url", mHomeSubTabVRBeans.get(i).getList_url());
//            fragment.setArguments(bundle);
//            fragments.add(fragment);
//        }
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                fragment = new ContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", 0); //VR 3D 2D 在VR tab上，为0
                bundle.putInt("categoryTab", position); //分类中的tab
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mHomeSubTabVRBeans.get(position).getList_url());
                fragment.setArguments(bundle);
                return fragment;

            default:
                singleListContentFragment = new SingleListContentFragment();
                bundle = new Bundle();
                bundle.putInt("currentTab", mCurrentTab);
                bundle.putInt("subCurrentTab", 0);
                bundle.putInt("categoryTab", position); //分类中的tab
                bundle.putBoolean("show_title", false);
                bundle.putString("next_url", mHomeSubTabVRBeans.get(position).getList_url());
                bundle.putBoolean("showPop", false);
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
