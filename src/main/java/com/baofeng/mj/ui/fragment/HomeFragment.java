package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HomeSubTabVRBean;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.bean.MainTabBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SubList;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.adapter.HomeAdapter;
import com.baofeng.mj.ui.adapter.VideoAdapter;
import com.baofeng.mj.ui.view.AppTitleView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.view.OvalRadioButton;
import com.baofeng.mj.ui.view.TabContainer;
import com.baofeng.mj.ui.view.UnderLineRadioButton;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

import java.util.List;

/**
 * Created by muyu on 2017/3/1.
 */
public class HomeFragment extends BaseViewPagerFragment implements ViewPager.OnPageChangeListener, TabContainer.OnTabChangeListener {
    private EmptyView emptyView;
    private TabContainer liveGroup;
    private ViewPager viewPager;
    private HomeAdapter adapter;
    private List<MainSubTabBean> mainSubTabBeanList;
    private int currentTab; //一级Tab position
    private LinearLayout.LayoutParams params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTab = getArguments().getInt("currentTab");
        }
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frag_home, container, false);
            initView();
            getData();//请求数据
        }else{
            removeRootView();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        //removeRootView();
        super.onDestroyView();
    }

    private void initView(){
        emptyView = (EmptyView) rootView.findViewById(R.id.empty_view);
        emptyView.getRefreshView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();//请求数据
            }
        });
        viewPager = (ViewPager) rootView.findViewById(R.id.video_viewpager);
        //viewPager.setOnPageChangeListener(VideoFragment.this);
        //viewPager.setOffscreenPageLimit(3);
        liveGroup = (TabContainer) rootView.findViewById(R.id.video_radiogroup);
        liveGroup.setOnTabChangeListener(HomeFragment.this);
        liveGroup.setOnPageChangeListener(HomeFragment.this);
        liveGroup.setViewPager(viewPager);
    }

    /**
     * 请求数据
     */
    private void getData() {
        ((MainActivityGroup) getActivity()).getMainTabBeanList(new MainActivityGroup.MainTabBeanListCallback() {
            @Override
            public void callback(List<MainTabBean<List<MainSubTabBean>>> mainTabBeanList) {
                if (mainTabBeanList == null) {
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                emptyView.setVisibility(View.GONE);
                mainSubTabBeanList = mainTabBeanList.get(currentTab).getPages();
                int subTabCount = mainSubTabBeanList.size();
                OvalRadioButton itemView;
                for (int i = 0; i < subTabCount; i++) {
                    itemView = new OvalRadioButton(getActivity());
                    itemView.setLayoutParams(params);
                    itemView.getTextView().setText(mainSubTabBeanList.get(i).getTitle());
                    liveGroup.addView(itemView);
                }
                adapter = new HomeAdapter(getChildFragmentManager(), mainSubTabBeanList, currentTab);
                viewPager.setAdapter(adapter);
                onPageSelected(0);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        viewPager.setCurrentItem(position);
        liveGroup.setCurrentTab(position);
        SettingSpBusiness.getInstance().setSubTabPosition(currentTab, position); //存储当前Tab，SubTab位置
        int resId = mainSubTabBeanList.get(position).getRes_id();
        String currentMenuName = mainSubTabBeanList.get(position).getTitle();
        ReportBusiness.getInstance().reportPV(currentTab, resId, currentMenuName);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean preTabChange(int position) {
        return false;
    }

    @Override
    public void onTabChanged(int position) {
    }
}
