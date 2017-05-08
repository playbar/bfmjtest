package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.LiveVideoViewPagerAdapter;
import com.baofeng.mj.ui.fragment.BaseViewPagerFragment;
import com.baofeng.mj.ui.fragment.LiveVideoListFragment;
import com.baofeng.mj.ui.view.AppTitleBackView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yushaochen on 2017/2/28.
 */

public class LiveVideoListActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    public static final int SELECTED_TYPE_LATEST = 0;//最新

    public static final int SELECTED_TYPE_HOTTEST = 1;//最热

    private AppTitleBackView title_layout;

    private RadioGroup live_video_radiogroup;

    private RadioButton latestRadioBtn;

    private RadioButton hottestRadioBtn;

    private ViewPager live_video_viewpager;

    private ArrayList<BaseViewPagerFragment> fragments;

    private LiveVideoViewPagerAdapter adapter;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_video_list);

        initView(savedInstanceState);

        initListener();

    }

    private void initView(Bundle savedInstanceState) {

        if (getIntent() != null) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("next_url"))) {
                url = getIntent().getStringExtra("next_url");
            }
        }

        title_layout = (AppTitleBackView) findViewById(R.id.title_layout);
        title_layout.getNameTV().setText("直播");
        title_layout.getInvrImgBtn().setVisibility(View.GONE);
        title_layout.getAppTitleRight().setVisibility(View.GONE);

        live_video_radiogroup = (RadioGroup) findViewById(R.id.live_video_radiogroup);
        latestRadioBtn = (RadioButton) findViewById(R.id.latest);
        hottestRadioBtn = (RadioButton) findViewById(R.id.hottest);
        latestRadioBtn.setOnClickListener(this);
        hottestRadioBtn.setOnClickListener(this);

        live_video_viewpager = (ViewPager) findViewById(R.id.live_video_viewpager);
        live_video_viewpager.setOnPageChangeListener(this);

        fragments = new ArrayList<BaseViewPagerFragment>();

        if (savedInstanceState != null) {
            List<Fragment> supportFragments = getSupportFragmentManager().getFragments();
            fragments.add((BaseViewPagerFragment) supportFragments.get(0));
            fragments.add((BaseViewPagerFragment) supportFragments.get(1));
        } else {
            LiveVideoListFragment liveFragment1 = new LiveVideoListFragment();
            liveFragment1.setType(SELECTED_TYPE_LATEST);
            liveFragment1.setUrl(url);
            LiveVideoListFragment liveFragment2 = new LiveVideoListFragment();
            liveFragment2.setType(SELECTED_TYPE_HOTTEST);
            liveFragment2.setUrl(url);
            fragments.add(liveFragment1);
            fragments.add(liveFragment2);
        }

        adapter = new LiveVideoViewPagerAdapter(getSupportFragmentManager(), fragments);
        live_video_viewpager.setAdapter(adapter);
        live_video_radiogroup.check(live_video_radiogroup.getChildAt(0).getId());
    }

    private void initListener() {
        title_layout.getBackImgBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        live_video_radiogroup.check(live_video_radiogroup.getChildAt(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.latest) {
            live_video_viewpager.setCurrentItem(0);
            //报数
            reportClick("livepagelatest");
        } else if (id == R.id.hottest) {
            live_video_viewpager.setCurrentItem(1);
            //报数
            reportClick("livepagehot");
        }
    }

    private void reportClick(String pagetype) {
        HashMap<String, String> map = new HashMap<>();
        map.put("etype","click");
        map.put("clicktype","chooseitem");
        map.put("tpos","1");
        map.put("pagetype",pagetype);
        ReportBusiness.getInstance().reportClick(map);
    }
}
