package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baofeng.mj.ui.fragment.BaseViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushaochen on 2017/2/28.
 */

public class LiveVideoViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseViewPagerFragment> fragments = new ArrayList<BaseViewPagerFragment>();
    public LiveVideoViewPagerAdapter(FragmentManager fm, List<BaseViewPagerFragment> fragments) {
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
