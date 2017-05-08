package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baofeng.mj.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制方式设置适配器
 * Created by muyu on 2016/4/6.
 */
public class ControlSettingAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();
    public ControlSettingAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

