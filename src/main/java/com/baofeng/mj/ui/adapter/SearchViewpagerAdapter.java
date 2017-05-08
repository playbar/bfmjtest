package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baofeng.mj.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunshine on 16/9/21.
 * 搜索页Viewpager Adapter
 */
public class SearchViewpagerAdapter extends FragmentStatePagerAdapter {
    private List<BaseFragment> mList = new ArrayList<BaseFragment>();

    public SearchViewpagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
