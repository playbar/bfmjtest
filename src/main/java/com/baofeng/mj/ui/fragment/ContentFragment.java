package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.NoNetWorkActivity;
import com.baofeng.mj.ui.adapter.ChoicenessAdapter;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.TransferData;
import com.baofeng.mj.utils.ACache;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muyu on 2017/3/7.
 */
public class ContentFragment extends BaseViewPagerFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {
    private LinearLayout contentLayout;
    private PullToRefreshRecyle pullToRefreshScrollView;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> fragData;
    private RelativeLayout noNetWorkInnerLayout;
    private ChoicenessAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private List<ContentBaseBean> beans;
    private CustomProgressView loadingView; //在fragment中显示loading
    private int currentTab;  //一级tab位置
    private int subCurrentTab; //二级tab位置
    private int categoryTab; //categoryTab位置
    private String nextUrl;
    private int resId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTab = getArguments().getInt("currentTab");
            subCurrentTab = getArguments().getInt("subCurrentTab");
            nextUrl = getArguments().getString("next_url");
            categoryTab = getArguments().getInt("categoryTab");
            resId = getArguments().getInt("res_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.frag_choiceness, container, false);
            initViews();
            getData();
        } else {
            removeRootView();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Glide.with(this).onStop();//停止图片加载
        //removeRootView();
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        showNetWork();
    }

    private void initViews() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 12, GridLayoutManager.VERTICAL, false);
        contentLayout = (LinearLayout) rootView.findViewById(R.id.choiceness_content_layout);
        emptyView = (EmptyView) rootView.findViewById(R.id.choiceness_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);
        pullToRefreshScrollView = (PullToRefreshRecyle) rootView.findViewById(R.id.choiceness_pulltorefresh);
        pullToRefreshScrollView.setOnRefreshListener(this);
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case FindViewGroup.VIDEO_H1_NUM: //VIDEO_H1
                    case FindViewGroup.APP_V_NUM: //APP_V
                    case FindViewGroup.APP_TOP_NUM: //APP_TOP
                    case FindViewGroup.NAV_SINGLE_NUM: //NAV_SINGLE
                    case FindViewGroup.NAV_MULT_NUM: //NAV_MULT
                    case FindViewGroup.GLOBAL_BANNNER_NUM://GLOBAL_BANNNER
                    case FindViewGroup.GLOBAL_TOPIC_NUM://GLOBAL_TOPIC
                    case FindViewGroup.LAYOUR_TITLE_NUM://LAYOUR_TITLE
                    case FindViewGroup.PIC_HSV_NUM:
                    case FindViewGroup.APP_HSV_NUM:
                    case FindViewGroup.APP_VIDEO_NUM:
                        return 12;
                    case FindViewGroup.VIDEO_H2_NUM: //VIDEO_H2
                    case FindViewGroup.VIDEO_V2_NUM: //VIDEO_V2
                    case FindViewGroup.APP_CATEGORY_NUM: //APP_CATEGORY
                        return 6;
                    case FindViewGroup.VIDEO_H3_NUM: //VIDEO_H3
                    case FindViewGroup.VIDEO_V3_NUM: //VIDEO_V3
                        return 4;
                    case FindViewGroup.APP_H_NUM: //APP_H
                        return 3;
                    default:
                        return -1;
                }

            }
        });

        beans = new ArrayList<ContentBaseBean>();
        adapter = new ChoicenessAdapter(getActivity(), this, beans);
        adapter.setHasShadow(false);
        recyclerView.setAdapter(adapter);
        noNetWorkInnerLayout = (RelativeLayout) rootView.findViewById(R.id.video_no_network_tab_layout);
        noNetWorkInnerLayout.setOnClickListener(this);
        loadingView = (CustomProgressView) rootView.findViewById(R.id.choiceness_loading);
    }


    private void getData() {
        boolean loaded = SettingSpBusiness.getInstance().getTabClickStatus(currentTab, subCurrentTab, categoryTab);  //是否请求过数据
        if (loaded) {
            initCache();
        } else {
            requestPageData();
        }
        initReportBean(resId);
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
        String value = mCache.getAsString("getMainSubTabInfo" + nextUrl);
        if (value != null && !"".equals(value)) {
            ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> bean = JSON.parseObject(
                    value, new TypeReference<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
                    });
            bindView(bean.getData());
        } else {
            requestPageData();
        }
    }

    private void requestPageData() {
        new ChoicenessApi().getMainSubTabInfo(getActivity(), nextUrl, new ApiCallBack<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> result) {
                if (result != null && result.getStatus() == 0) {
                    showOrHideEmptyView(false);//隐藏空页面

                    if(result.getData().getList().size() > 0){ //设置已请求过数据
                        SettingSpBusiness.getInstance().setTabClickStatus(currentTab, subCurrentTab, categoryTab, true);
                    }
                    bindView(result.getData());
//                    pushHierarchy(res_id);
                } else {
                    showOrHideEmptyView(true);//显示空页面
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                showOrHideEmptyView(true);//显示空页面
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pullToRefreshScrollView.onRefreshComplete();
            }
        });
    }

    /**
     * 显示或隐藏空页面
     */
    private void showOrHideEmptyView(boolean show) {
        if (show) {//显示空页面
            if (beans == null || beans.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {//隐藏空页面
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showNetWork() {
        if (!NetworkUtil.networkEnable()) {
            noNetWorkInnerLayout.setVisibility(View.VISIBLE);
        } else {
            noNetWorkInnerLayout.setVisibility(View.GONE);
        }
    }

    private void bindView(MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> data) {
        this.fragData = data;
        if (adapter != null) {
            adapter.setHasTag(true);
        }
        List<ContentBaseBean> contentInfos = TransferData.getInstance().transToSmallModule(data);
        beans.clear();
        beans.addAll(contentInfos);
        adapter.notifyDataSetChanged();

        loadingView.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refreshView) {
            requestPageData();
            showNetWork();
        } else if (i == R.id.video_no_network_tab_layout) {
            Intent intent = new Intent(getActivity(), NoNetWorkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        requestPageData();
        showNetWork();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }


    private void initReportBean(int resId) {
        //报数类型
        ReportFromBean bean = new ReportFromBean();
//        if (type == ResTypeUtil.res_type_banner) {
//            bean.setColid(resId);
//        } else if (type == ResTypeUtil.res_type_category) {
//            bean.setSubcateid(resId);
//        }
//        bean.setPagetype(ReportBusiness.getPageType(type));
        bean.setFrompage(nextUrl);
        adapter.setReportBean(bean);
    }
}