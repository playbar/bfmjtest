package com.baofeng.mj.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.adapter.AppListAdapter;
import com.baofeng.mj.ui.popwindows.TDCategoryPopWindow;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.TransferData;
import com.baofeng.mj.utils.ACache;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**首页VR 2D tab下除推荐外的Tab内容页面
 * Created by muyu on 2017/3/7.
 */
public class SingleListContentFragment extends BaseViewPagerFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {
    private GridLayoutManager gridLayoutManager;//recyclerView布局管理器
    private LinearLayout contentLayout;
    private PullToRefreshRecyle pullToRefreshScrollView;
    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;
    private List<ContentBaseBean> contentBaseBeans;
    private int totalNum = 0;//数据总量
    private int showCount = 0;//已显示的数据量
    private int currentPage = 0;

    private boolean isLoadingMore;//true正在加载，false不是

    private int currentTab;  //一级tab位置
    private int subCurrentTab; //二级tab位置
    private int categoryTab; //分类下的Tab位置
    private String nextUrl = "";
    private CustomProgressView customProgressView;
    private RelativeLayout filterLayout;
    private SelectListBean<SelectListBean<SelectDetailBean>> categoryData;

    private boolean showPop = false; //判断是否为首页2D页面 true为2D页面
    private CheckBox arrowRadio;

    private String mHeadUrl;

    private TDCategoryPopWindow popWindow;
    private EmptyView emptyView;
    private int resId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTab = getArguments().getInt("currentTab");
            subCurrentTab = getArguments().getInt("subCurrentTab");
            categoryTab = getArguments().getInt("categoryTab");
            nextUrl = getArguments().getString("next_url");
            showPop = getArguments().getBoolean("showPop");
            resId = getArguments().getInt("res_id");
            if(showPop){
                categoryData = (SelectListBean<SelectListBean<SelectDetailBean>>) getArguments().getSerializable("categoryData");
                mHeadUrl = getArguments().getString("headUrl");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.frag_single_list_content, container, false);
            initViews();
            getData(false, false, false);
        } else {
            removeRootView();
        }
        return rootView;
    }
    /**
     * 初始化view
     */
    public void initViews() {
        contentLayout = (LinearLayout) rootView.findViewById(R.id.single_list_content_layout);
        pullToRefreshScrollView = (PullToRefreshRecyle) rootView.findViewById(R.id.choiceness_pulltorefresh);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);//禁止加载
        pullToRefreshScrollView.setOnRefreshListener(this);
        customProgressView = (CustomProgressView) rootView.findViewById(R.id.single_list_loading);
        gridLayoutManager = new GridLayoutManager(getActivity(), 12, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (appListAdapter.getItemViewType(position)) {
                    case FindViewGroup.VIDEO_H1_NUM: //VIDEO_H1
                    case FindViewGroup.APP_V_NUM: //APP_V
                    case FindViewGroup.APP_TOP_NUM: //APP_TOP
                    case FindViewGroup.NAV_SINGLE_NUM: //NAV_SINGLE
                    case FindViewGroup.NAV_MULT_NUM: //NAV_MULT
                    case FindViewGroup.GLOBAL_BANNNER_NUM://GLOBAL_BANNNER
                    case FindViewGroup.GLOBAL_TOPIC_NUM://GLOBAL_TOPIC
                    case FindViewGroup.LAYOUR_TITLE_NUM://LAYOUR_TITLE
                    case FindViewGroup.SELECT_TYPE_NUM:
                    case FindViewGroup.LINE_NUM:
                    case FindViewGroup.NO_DATA_NUM:
                    case FindViewGroup.LOAD_MORE_NUM:
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
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        recyclerView.setLayoutManager(gridLayoutManager);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(recyclerView.getLayoutParams());
        contentBaseBeans = new ArrayList<ContentBaseBean>();
        appListAdapter = new AppListAdapter(getActivity(), contentBaseBeans, new AppListAdapter.SelectChanged(){
            @Override
            public void change(int checkedId) {
                getData(true, false, false);
            }
        });
        recyclerView.setAdapter(appListAdapter);
        filterLayout = (RelativeLayout) rootView.findViewById(R.id.single_list_filter_layout);
        int padding = PixelsUtil.dip2px(20);
        if(showPop) {
            filterLayout.setVisibility(View.VISIBLE);
            padding = PixelsUtil.dip2px(60);
        }
        lp.setMargins(0, padding, 0, 0);
        recyclerView.setLayoutParams(lp);
        arrowRadio = (CheckBox) rootView.findViewById(R.id.single_list_filter_arrow);
        arrowRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showSelectDefinitionDialog(compoundButton, b);
            }
        });
        emptyView = (EmptyView) rootView.findViewById(R.id.single_list_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);
    }
    private String getUrlPart() {
        String urlPart = "";
        int p = categoryData.getList().size();
        for (int n = 0; n < p; n++) {
            int posId = categoryData.getList().get(n).getSelectPos();
            String key = categoryData.getList().get(n).getKeyname();
            if (posId < categoryData.getList().get(n).getList().size()) {
                urlPart = urlPart + key + categoryData.getList().get(n).getList().get(posId).getId() + "-";
            }
        }
        urlPart = urlPart.trim();
        return urlPart;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.refreshView){
            currentPage = 0;
            getData(false, false, false);
        }
    }

    private void showEmpty(boolean showEmpty){
        if(showEmpty){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showSelectDefinitionDialog(final CompoundButton compoundButton ,boolean isShow) {
        if(isShow) {
            if (popWindow == null) {
                popWindow = new TDCategoryPopWindow(getActivity(), categoryData, appListAdapter);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        compoundButton.setChecked(false);
                    }
                });
            }
            if (Build.VERSION.SDK_INT < 24) {
                popWindow.showAsDropDown(filterLayout);
            } else { //兼容7.0手机
                int[] a = new int[2];
                filterLayout.getLocationInWindow(a);
                popWindow.showAtLocation((getActivity()).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, a[1] + filterLayout.getHeight());
            }

        }else {
            if (popWindow != null) {
                popWindow.dismiss();
            }
        }
    }

    /**
     * 获取请求网络数据必须参数
     */
    private void getData(boolean isChecked, boolean isLoadingMore, boolean isPullDownToRefresh) {
        String listUrl = "";
        if (!showPop) {
            listUrl = ConfigUrl.getListMoreUrl(nextUrl, currentPage * ConfigConstant.pageCount12, ConfigConstant.pageCount12);
        } else {
            if (isChecked) {
                showCount = 0;
                totalNum = 0;
                currentPage = 0;
            }
            listUrl = ConfigUrl.getCategoryUrl1(mHeadUrl, categoryData.getKeyname(), categoryData.getRes_id() + "", getUrlPart(),currentPage * ConfigConstant.pageCount12, ConfigConstant.pageCount12);
        }
//        requestData(listUrl, isLoadingMore);
        boolean loaded = SettingSpBusiness.getInstance().getTabClickStatus(currentTab, subCurrentTab, categoryTab);  //是否请求过数据
        if (loaded && !isPullDownToRefresh) {
            initCache(listUrl, isLoadingMore);
        } else {
            requestData(listUrl, isLoadingMore);
        }

        initReportBean(resId);
    }

    private void initCache(String url, boolean isLoadingMore) {
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
        String value = mCache.getAsString("getAppListInfo" + url);
        if (value != null && !"".equals(value)) {
            ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(
                    value, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                    });
            bindListView(bean.getData(), isLoadingMore);
        } else {
            requestData(url, isLoadingMore);
        }
    }
    /**
     * 请求网络数据
     */
    private void requestData(String url, final boolean isLoadingMore) {
        new ChoicenessApi().getAppListInfo(getActivity(), url, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                super.onSuccess(result);
                if (result != null && result.getData() != null && result.getStatus() == 0) {
//                    String categoryUrl = result.getData().getCategory_url();
//                    if (!TextUtils.isEmpty(categoryUrl)) {
                        if(result.getData().getList().size() > 0){ //设置已请求过数据
                            SettingSpBusiness.getInstance().setTabClickStatus(currentTab, subCurrentTab, categoryTab, true);
                        }
                        bindListView(result.getData(), isLoadingMore);
//                    } else {
////                        recyclerView.setPadding(0, PixelsUtil.dip2px(20), 0, 0);
//                    }
                } else {
                    resFailRequest();
                }
                pullToRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                resFailRequest();
                pullToRefreshScrollView.onRefreshComplete();
                showEmpty(true);
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindListView(MainSubContentListBean<List<ContentInfo>> data, boolean isLoadMore) {
        showEmpty(false);
        String laType = data.getLayout_type();
        if (!TextUtils.isEmpty(laType)) {
            if (laType.equals(FindViewGroup.VIDEO_H2) || laType.equals(FindViewGroup.VIDEO_V2) || laType.equals(FindViewGroup.VIDEO_H3) || laType.equals(FindViewGroup.VIDEO_V3)) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, PixelsUtil.dip2px(10), 0, 0);
            }
        }

        if (data != null && data.getList() != null && data.getList().size() > 0) {
            contentLayout.setVisibility(View.VISIBLE);
            //移除加载更多进度条
//            int lastPosition = contentBaseBeans.size() - 1;//最后一个位置
//            if (lastPosition >= 0) {
//                if (FindViewGroup.LOAD_MORE.equals(contentBaseBeans.get(lastPosition).getLayout_type())) {
//                    contentBaseBeans.remove(lastPosition);
//                    appListAdapter.notifyItemRemoved(lastPosition);
//                }
//            }
            totalNum = data.getTotal();
            List<ContentBaseBean> infos = TransferData.getInstance().transToListModule(data);
            //添加加载更多进度条
//            ContentInfo loadMoreInfo = new ContentInfo();
//            loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
//            contentBaseBeans.add(loadMoreInfo);
            if (isLoadMore) {
                contentBaseBeans.addAll(infos);
                appListAdapter.notifyItemRangeChanged(showCount, contentBaseBeans.size());
            } else {
                contentBaseBeans.clear();
                contentBaseBeans.addAll(infos);
                appListAdapter.notifyDataSetChanged();
            }
            currentPage = currentPage + 1;
            showCount = showCount + data.getList().size();
        }else{
            emptyContent();
        }

        customProgressView.setVisibility(View.GONE);
    }

    private void emptyContent(){
        contentBaseBeans.clear();
//        ContentBaseBean lineBean = new ContentBaseBean();
//        lineBean.setLayout_type("line");
//        contentBaseBeans.add(lineBean);
        ContentBaseBean emptyBaseBean = new ContentBaseBean();
        emptyBaseBean.setLayout_type(FindViewGroup.NO_DATA);
        contentBaseBeans.add(emptyBaseBean);
        appListAdapter.notifyDataSetChanged();
    }

    /**
     * 网络请求失败或者无网络处理
     */
    private void resFailRequest() {
        contentLayout.setVisibility(View.GONE);
        contentBaseBeans.clear();
        appListAdapter.notifyDataSetChanged();
        customProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        showCount = 0;
        currentPage = 0;
        getData(false, false, true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if(showCount >= totalNum){
            Toast.makeText(getActivity(),"没有更多内容", Toast.LENGTH_SHORT).show();
            pullToRefreshScrollView.onRefreshComplete();
            return;
        }
        getData(false, true, false);
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
        appListAdapter.setReportBean(bean);
    }
}
