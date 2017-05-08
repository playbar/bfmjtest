package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.bean.SubList;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.adapter.Home2DAdapter;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.view.TabContainer;
import com.baofeng.mj.ui.view.UnderLineRadioButton;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.utils.ACache;

import java.io.File;
import java.util.List;

/**首页2D Tab
 * Created by muyu on 2017/3/7.
 */
public class Home2DFragment extends BaseViewPagerFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, TabContainer.OnTabChangeListener{

    private String mCategoryUrl;
    private LinearLayout.LayoutParams params;
    private TabContainer vrGroup;
    private ViewPager vrViewPager;
    private Home2DAdapter adapter;
    private EmptyView emptyView;
    private View home_2d_line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgumentData();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_home_2d, container, false);
            initViews();
            getData();
        }else{
            removeRootView();
        }
        return rootView;
    }

    private void getArgumentData(){
        if (getArguments() != null) {
            mCategoryUrl = getArguments().getString("categoryUrl");
        }
    }

    private void initViews(){
        vrGroup = (TabContainer) rootView.findViewById(R.id.home_2d_radiogroup);

        vrViewPager = (ViewPager) rootView.findViewById(R.id.home_2d_viewpager);
        vrGroup.setOnTabChangeListener(this);
        vrGroup.setOnPageChangeListener(this);
        vrGroup.setViewPager(vrViewPager);

        emptyView = (EmptyView) rootView.findViewById(R.id.home_2d_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);

        home_2d_line = rootView.findViewById(R.id.home_2d_line);

    }

    private void showEmpty(boolean showEmpty){
        if(showEmpty){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void getData(){
        boolean loaded = SettingSpBusiness.getInstance().getHome2DCate();  //是否请求过数据
        if (loaded) {
            initCache();
        } else {
            requestData();
        }
    }

    private void initCache() {
        File cacheDir;
        if (getActivity() != null) {
            cacheDir = getActivity().getExternalCacheDir();
        } else {
            cacheDir = BaseApplication.INSTANCE.getExternalCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = getContext().getCacheDir();
        }
        ACache mCache = ACache.get(cacheDir);
        String value = mCache.getAsString("getMainSubTab2D" + mCategoryUrl);
        if (value != null && !"".equals(value)) {
            ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>> bean = JSON.parseObject(
                    value, new TypeReference<ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>>>() {
                    });
            bindView(bean.getData());
        }else {
            requestData();
        }
    }

    private void requestData(){
        System.out.println("testtest Home2DFragment requestData");
        new ChoicenessApi().getMainSubTab2D(getActivity(), mCategoryUrl, new ApiCallBack<ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<SubList<SelectListBean<SelectListBean<SelectDetailBean>>>> result) {
                if (result != null && result.getStatus() == 0) {
                    bindView(result.getData());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                showEmpty(true);
            }
        });
    }

    private List<SelectListBean<SelectListBean<SelectDetailBean>>> mHomeSubTab2DBeans;
    private void bindView(SubList<SelectListBean<SelectListBean<SelectDetailBean>>> homeSubTab2DBeans){
        showEmpty(false);
        this.mHomeSubTab2DBeans = homeSubTab2DBeans.getList();
        int subTabCount = mHomeSubTab2DBeans.size();
        if(subTabCount > 0){
            SettingSpBusiness.getInstance().setHome2DCate(true);
        }
        UnderLineRadioButton itemView;

        for (int i = 0; i < subTabCount; i++) {
            itemView = new UnderLineRadioButton(getActivity());
            itemView.setLayoutParams(params);
            itemView.getTextView().setText(mHomeSubTab2DBeans.get(i).getTitle());
            vrGroup.addView(itemView);
        }

        adapter = new Home2DAdapter(getChildFragmentManager(), mHomeSubTab2DBeans, homeSubTab2DBeans.getUrl(), 0);
        vrViewPager.setAdapter(adapter);
        onPageSelected(0);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.refreshView) {
            requestData();
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0){
            home_2d_line.setVisibility(View.GONE);
        }else {
            home_2d_line.setVisibility(View.VISIBLE);
        }
        vrViewPager.setCurrentItem(position);
        vrGroup.setCurrentTab(position);
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