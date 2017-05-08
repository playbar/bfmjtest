package com.baofeng.mj.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

public class MyViewpagerAdapter extends PagerAdapter {
	private List<View> viewList;

	public MyViewpagerAdapter(List<View> viewList){
		this.viewList=viewList;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		if(viewList.size() == 1){
			return 1;//如果只有一个，不要设置为无限大
		}
		return Integer.MAX_VALUE;// 设置为无限大
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if(viewList.size() > 3){
			((ViewPager) arg0).removeView(viewList.get(arg1 % viewList.size()));
		}
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		View view = viewList.get(arg1 % viewList.size());
		if (view.getParent() != null) {
			((ViewPager) view.getParent()).removeView(view);
		}
		((ViewPager) arg0).addView(view, 0);
		return view;
	}
}
