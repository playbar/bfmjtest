package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.adapter.AppStoreAdapter;

/** 应用市场页面
 * Created by muyu on 2016/5/5.
 */
public class AppStoreFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private View rootView;
    private RadioGroup liveGroup;
    private RadioButton commendationRadioBtn;
    private RadioButton welfareRadioBtn;
    private RadioButton appRadioBtn;
    private RadioButton classifyRadioBtn;
    private RadioButton rankingRadioBtn;
    private ViewPager viewPager;

    private AppStoreAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_appstore, null);
        initViews();
        return rootView;
    }

    private void initViews(){
        liveGroup = (RadioGroup) rootView.findViewById(R.id.appstore_radiogroup);
        commendationRadioBtn = (RadioButton) rootView.findViewById(R.id.appstore_commendation);
        welfareRadioBtn = (RadioButton)rootView.findViewById(R.id.appstore_welfare);
        appRadioBtn = (RadioButton) rootView.findViewById(R.id.appstore_app);
        classifyRadioBtn = (RadioButton) rootView.findViewById(R.id.appstore_classify);
        rankingRadioBtn = (RadioButton) rootView.findViewById(R.id.appstore_ranking);
        viewPager = (ViewPager) rootView.findViewById(R.id.appstore_viewpager);

        FragmentManager fm = getChildFragmentManager();
        adapter = new AppStoreAdapter(fm);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        liveGroup.check(R.id.appstore_commendation);

        commendationRadioBtn.setOnClickListener(this);
        welfareRadioBtn.setOnClickListener(this);
        appRadioBtn.setOnClickListener(this);
        classifyRadioBtn.setOnClickListener(this);
        rankingRadioBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.appstore_commendation) {
            viewPager.setCurrentItem(0);

        } else if (i == R.id.appstore_welfare) {
            viewPager.setCurrentItem(1);

        } else if (i == R.id.appstore_app) {
            viewPager.setCurrentItem(2);

        } else if (i == R.id.appstore_classify) {
            viewPager.setCurrentItem(3);

        } else if (i == R.id.appstore_ranking) {
            viewPager.setCurrentItem(4);

        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        liveGroup.check(liveGroup.getChildAt(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
