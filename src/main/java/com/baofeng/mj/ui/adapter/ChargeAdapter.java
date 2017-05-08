package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baofeng.mj.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyang on 2016/5/11.
 */
public class ChargeAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mList = new ArrayList<BaseFragment>();

    public ChargeAdapter(FragmentManager fm, List<BaseFragment> list) {
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
