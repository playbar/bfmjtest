package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baofeng.mj.ui.fragment.BaseViewPagerFragment;

import java.util.List;

/**
 * 本地适配器
 * Created by muyu on 2016/4/6.
 */
public class LocalAdapter extends FragmentPagerAdapter {
    private List<BaseViewPagerFragment> mAbsBaseFragments;

    public LocalAdapter(FragmentManager fm, List<BaseViewPagerFragment> mAbsBaseFragments) {
        super(fm);
        this.mAbsBaseFragments = mAbsBaseFragments;
    }

    @Override
    public Fragment getItem(int index) {
        return mAbsBaseFragments.get(index);
    }

    @Override
    public int getCount() {
        return mAbsBaseFragments == null ? 0 : mAbsBaseFragments.size();
    }
}

