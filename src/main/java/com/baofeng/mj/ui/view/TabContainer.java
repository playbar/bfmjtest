package com.baofeng.mj.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/** Tab容器
 * Created by muyu on 2016/3/28.
 */
public class TabContainer extends LinearLayout implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final int SELECTED_NONE = -1;
    private List<ITabItem> tabs = new ArrayList<ITabItem>();
    private int currentSelectedIndex = SELECTED_NONE;
    private OnTabChangeListener onTabChangeListener;

    public TabContainer(Context context) {
        super(context);
    }

    public TabContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TabContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof ITabItem) {
            ITabItem tabItem = (ITabItem) child;
            tabs.add(tabItem);
            tabItem.getTabItemView().setOnClickListener(this);

            //如果在xml中设置了选中状态，则为期设置选中状态
            if (tabItem.getTabItemView().isSelected()) {
                tabItem.getTabItemView().performClick();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getCurrentTab() == SELECTED_NONE && getCount() > 0) {
            setCurrentTab(0);
        }
    }

    @Override
    public void onClick(View v) {

        int position = tabs.indexOf(v);

        // for (MainTabItemView item : tabs) {
        // item.setSelected(item == v);
        // }
        setSelectedStateForView(position);

    }

    /**
     * 为tab view设置选中状态
     *
     * @param position
     */
    private void setSelectedStateForView(int position) {
        if (position == SELECTED_NONE || position == currentSelectedIndex)
            return;

        if (onTabChangeListener != null) {
            // 是否被中止
            if (onTabChangeListener.preTabChange(position))
                return;
        }
        if(tabs == null || position >= tabs.size()){
            return;
        }
        tabs.get(position).getTabItemView().setSelected(true);
        if (currentSelectedIndex != SELECTED_NONE) {
            tabs.get(currentSelectedIndex).getTabItemView().setSelected(false);
        }

        currentSelectedIndex = position;

        if (onTabChangeListener != null) {
            onTabChangeListener.onTabChanged(position);
        }
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        }
    }

    public int getCurrentTab() {
        return currentSelectedIndex;
    }

    public void setCurrentTab(int position) {
        setSelectedStateForView(position);
    }

    public OnTabChangeListener getOnTabChangeListener() {
        return onTabChangeListener;
    }

    public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
        boolean isSetBefore = this.onTabChangeListener != null;

        this.onTabChangeListener = onTabChangeListener;

        //如果之前没有设置过监听，初始化到第一个标签下
        if (!isSetBefore && tabs.size() != 0) {
            setCurrentTab(0);
        }
    }

    /**
     * tab数量
     *
     * @return
     */
    public int getCount() {
        return tabs == null ? 0 : tabs.size();
    }

    /**
     * 绑定ViewPager,调用了次方法之后，若要为ViewPager设置OnPageChangeListener请使用
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        this.mViewPager.setOnPageChangeListener(this);
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return mOnPageChangeListener;
    }

    /**
     * 为绑定此TabContainer的ViewPager设置监听
     *
     * @param onPageChangeListener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentTab(position);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putInt("currentPosition", currentSelectedIndex);
        bundle.putParcelable("superState", superState);
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("superState"));
        setSelectedStateForView(bundle.getInt("currentPosition"));
    }

    public static interface OnTabChangeListener {
        boolean preTabChange(int position);

        void onTabChanged(int position);
    }

}
