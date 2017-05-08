package com.baofeng.mj.ui.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.HomeSubTabVRBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SubList;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.adapter.HomeVRAdapter;
import com.baofeng.mj.ui.popwindows.VRCategoryPopWindow;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.view.TabContainer;
import com.baofeng.mj.ui.view.UnderLineRadioButton;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.utils.ACache;

import java.io.File;
import java.util.List;

/**
 * 首页VR Fragment
 * Created by muyu on 2016/3/28.
 */
public class HomeVRFragment extends BaseViewPagerFragment implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private String mCategoryUrl;
    private LinearLayout.LayoutParams params;
    private TabContainer vrGroup;
    private ImageView moreIV;
    private ViewPager vrViewPager;
    private HomeVRAdapter adapter;
    private int screenWidth;
    private HorizontalScrollView horizontalScrollView;
    private EmptyView emptyView;
    private int subTabCount;

    private int count; //几页
    private int maxNum; //15
    private  int remain;
    private VRCategoryPopWindow popWindow;
    private View home_vr_line;
    private int scrollPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null ){
            scrollPosition = savedInstanceState.getInt("sroll_position");
        }
        getArgumentData();
        screenWidth = PixelsUtil.getWidthPixels();
        params = new LinearLayout.LayoutParams(screenWidth / 5, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != horizontalScrollView){
            outState.putInt("sroll_position", horizontalScrollView.getScrollX());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_home_vr, container, false);
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
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.home_vr_title_scroll);
        vrGroup = (TabContainer) rootView.findViewById(R.id.home_vr_radiogroup);
        moreIV = (ImageView) rootView.findViewById(R.id.home_vr_more_imageview);
        moreIV.setOnClickListener(this);

        vrViewPager = (ViewPager) rootView.findViewById(R.id.home_vr_viewpager);
        vrGroup.setOnPageChangeListener(this);
        vrGroup.setViewPager(vrViewPager);

        emptyView = (EmptyView) rootView.findViewById(R.id.home_vr_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);

        home_vr_line = rootView.findViewById(R.id.home_vr_line);

    }

    private void getData(){
        boolean loaded = SettingSpBusiness.getInstance().getHomeVRCate();  //是否请求过数据
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
        String value = mCache.getAsString("getMainSubTabVR" + mCategoryUrl);
        if (value != null && !"".equals(value)) {
            ResponseBaseBean<SubList<HomeSubTabVRBean>> bean = JSON.parseObject(
                    value, new TypeReference<ResponseBaseBean<SubList<HomeSubTabVRBean>>>() {
                    });
            bindView(bean.getData().getList());
        }else {
            requestData();
        }
    }

    private void requestData(){
        new ChoicenessApi().getMainSubTabVR(getActivity(), mCategoryUrl, new ApiCallBack<ResponseBaseBean<SubList<HomeSubTabVRBean>>>() {

            @Override
            public void onSuccess(ResponseBaseBean<SubList<HomeSubTabVRBean>> result) {
                if (result != null && result.getStatus() == 0) {
                    bindView(result.getData().getList());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                showEmpty(true);
            }
        });
    }

    private void showEmpty(boolean showEmpty){
        if(showEmpty){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private List<HomeSubTabVRBean> mHomeSubTabVRBeans;
    private void bindView(List<HomeSubTabVRBean> homeSubTabVRBeans){
        showEmpty(false);
        this.mHomeSubTabVRBeans = homeSubTabVRBeans;
//        int subTabCount = homeSubTabVRBeans.size() > 5 ? 5 :homeSubTabVRBeans.size();
        subTabCount = homeSubTabVRBeans.size();
        if(subTabCount > 0){
            SettingSpBusiness.getInstance().setHomeVRCate(true);
        }
        count = subTabCount / 5; //几页
        maxNum = count * 5; //15
        remain = subTabCount - maxNum;

        UnderLineRadioButton itemView;
        for (int i = 0; i < subTabCount; i++) {
            itemView = new UnderLineRadioButton(getActivity());
            itemView.setLayoutParams(params);
            itemView.getTextView().setText(homeSubTabVRBeans.get(i).getTitle());
            vrGroup.addView(itemView);
        }

        adapter = new HomeVRAdapter(getChildFragmentManager(), homeSubTabVRBeans, 0);
        vrViewPager.setAdapter(adapter);
        onPageSelected(0);

        if(scrollPosition != 0){
            horizontalScrollView.smoothScrollTo(scrollPosition, 0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.home_vr_more_imageview){
            showSelectDefinitionDialog();
        } if (id == R.id.refreshView) {
            requestData();
        }
    }

    private void showSelectDefinitionDialog() {
        initSelectDefinitionDialog();
        if (Build.VERSION.SDK_INT < 24) {
            popWindow.showAsDropDown(moreIV, 0, -moreIV.getHeight());
        } else { //兼容7.0手机
            int[] a = new int[2];
            moreIV.getLocationInWindow(a);
            popWindow.showAtLocation((getActivity()).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, a[1] + moreIV.getHeight());
        }
    }

    private void setDialogSelectIndex(int position){
        initSelectDefinitionDialog();
        popWindow.setSelectedIndex(position);
    }

    private void initSelectDefinitionDialog() {
        if(popWindow == null) {
            popWindow = new VRCategoryPopWindow(getActivity(), mHomeSubTabVRBeans);
            popWindow.setOnItemClickCallback(new VRCategoryPopWindow.OnItemClickCallback() {
                @Override
                public void onItemClick(int position) {
                    onCheckedPageSelected(position);
                }
            });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    private void onCheckedPageSelected(int position){
        if(position == 0){
            home_vr_line.setVisibility(View.GONE);
        }else {
            home_vr_line.setVisibility(View.VISIBLE);
        }
        vrViewPager.setCurrentItem(position);
        vrGroup.setCurrentTab(position);

        //Tab居中效果
        if(position >= 2) {
            int distance = (position - 2) * (screenWidth / 5);
            horizontalScrollView.smoothScrollTo(distance, 0);
        } else {
            horizontalScrollView.smoothScrollTo(0, 0);
        }
//        horizontalScrollView.smoothScrollTo(position * screenWidth / 5, 0);
    }

//    private int prePostion;
    @Override
    public void onPageSelected(int position) {
        onCheckedPageSelected(position);
        //Tab翻页效果
//        int tabPos =position % 5;
//        if(prePostion < position){ //move to left
//            if(tabPos == 0){
//                horizontalScrollView.smoothScrollTo((position / 5) * screenWidth, 0);
//            }
//        } else { //move to right
//            if(position <= maxNum -1 && tabPos == 0){
//                horizontalScrollView.smoothScrollTo((position + remain - 5 ) * screenWidth / 5, 0);
//            }
//        }
//        prePostion = position;
        setDialogSelectIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
