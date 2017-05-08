package com.baofeng.mj.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.baofeng.mj.ui.fragment.ChoicenessFragment;

/**
 * 应用市场适配器
 * Created by muyu on 2016/5/5.
 */
public class AppStoreAdapter extends FragmentStatePagerAdapter {

    public AppStoreAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChoicenessFragment();
            case 1:
                return new ChoicenessFragment();
            case 2:
                return new ChoicenessFragment();
            case 3:
                return new ChoicenessFragment();
            case 4:
                return new ChoicenessFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((Fragment) obj).getView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = ((Fragment) object);
    }

}
