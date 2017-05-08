package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.SelectBean;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.AppListAdapter;
import com.baofeng.mj.ui.view.SelectItemView;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.netutil.VideoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.TransferData;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 筛选界面
 * Created by hanyang on 2016/5/6.
 */
public class AppListActivity extends BaseLoadingActivity {
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private GridLayoutManager gridLayoutManager;//ercyclerView布局管理器
    private PullToRefreshRecyle app_list_recyle;
    private RecyclerView resultRecycler;
    private AppListAdapter appListAdapter;
    private List<ContentBaseBean> contentBaseBeans;
    private int totalNum = 0;//数据总量
    private int showCount = 0;//已显示的数据量
    private int id;
    private String urlHead, urlTail;
    private String cate;
    private List<SelectListBean> selectListBeans;
    private LinearLayout top_type_layout, type_layout;
    private RelativeLayout select_layout;
    private TextView select_tag;
    private int typeHeight;
    private int scroType;
    private boolean isRank = false;//判断是否排行榜列表页，显示排名
    private boolean isLoadingMore;//true正在加载，false不是
    private boolean isReported = false;
    private LoadMoreViewHolder loadMoreViewHolder;
    private int scrolledDY;
    private String detailUrl;

    public void setLoadMoreViewHolder(LoadMoreViewHolder loadMoreViewHolder){
        this.loadMoreViewHolder = loadMoreViewHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String operateJson = getIntent().getStringExtra(PushTypeBusiness.OPERATE_JSON);
        downLoadBusiness = new MyDownLoadBusiness(this);
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                downLoadBusiness.apkInstallNotify(packageName);
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.addDownLoadBusiness(downLoadBusiness);
        initViews();
        ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_app_list_content;
    }

    @Override
    protected void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.removeDownLoadBusiness(downLoadBusiness);
        downLoadBusiness = null;
        apkInstallNotify = null;
        super.onDestroy();
    }

    /**
     * 初始化view
     */
    public void initViews() {
        select_tag = (TextView) findViewById(R.id.select_tag);
        top_type_layout = (LinearLayout) findViewById(R.id.top_type_layout);
        type_layout = (LinearLayout) findViewById(R.id.type_layout);
        select_layout = (RelativeLayout) findViewById(R.id.select_layout);
        app_list_recyle = (PullToRefreshRecyle) findViewById(R.id.app_list_recyle);
        app_list_recyle.setMode(PullToRefreshBase.Mode.DISABLED);//禁止加载
        gridLayoutManager = new GridLayoutManager(this, 12, GridLayoutManager.VERTICAL, false);
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
        resultRecycler = app_list_recyle.getRefreshableView();
        resultRecycler.setLayoutManager(gridLayoutManager);
        selectListBeans = new ArrayList<SelectListBean>();
        contentBaseBeans = new ArrayList<ContentBaseBean>();
        appListAdapter = new AppListAdapter(this, contentBaseBeans, new AppListAdapter.SelectChanged(){
            @Override
            public void change(int checkedId) {
                showCount = 0;
                totalNum = 0;
                getSelectData(getUrlPart(), showCount, 12, urlTail, false);
            }
        });
        appListAdapter.setDownLoadBusiness(downLoadBusiness);
        resultRecycler.setAdapter(appListAdapter);
        select_layout.setOnClickListener(this);
        getData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if(id == R.id.select_layout){
            if (type_layout.getVisibility() == View.VISIBLE) {
                type_layout.setVisibility(View.GONE);
                select_tag.setBackground(getResources().getDrawable(R.drawable.public_arrow_down));
            } else {
                type_layout.setVisibility(View.VISIBLE);
                select_tag.setBackground(getResources().getDrawable(R.drawable.public_arrow_up));
            }
        }else if(id == R.id.refreshView){
            getData();
        }
    }

    /**
     * 获取请求网络数据必须参数
     */
    private void getData() {
        if (getIntent() != null) {
            if (getIntent().hasExtra("res_id")) {
                String res_id = getIntent().getStringExtra("res_id");
                if (!TextUtils.isEmpty(res_id)) {
                    id = Integer.parseInt(res_id.trim());
                }
            }
            if (getIntent().hasExtra("isRank")) {
                isRank = getIntent().getBooleanExtra("isRank", false);
                appListAdapter.setIsRank(isRank);
            }
            type = getIntent().getIntExtra("next_type", 0);
            subType = getIntent().getIntExtra("next_subType", 0);
            if (getIntent().getStringExtra("next_url") != null) {
                detailUrl = getIntent().getStringExtra("next_url");
                //报数
                initReportBean(ReportBusiness.getInstance().getResIdFromUrl(detailUrl));
                String listUrl = ConfigUrl.getListMoreUrl(detailUrl, 0, ConfigConstant.pageCount12);

                ReportBusiness.getInstance().put(listUrl, ReportBusiness.getInstance().get(detailUrl));
                ReportBusiness.getInstance().remove(detailUrl);

                requestData(listUrl);
            }
            titleBackView.setPageType(ReportBusiness.getPageType(type));
        }
    }

    /**
     * 请求网络数据
     */
    private void requestData(String url) {
        new ChoicenessApi().getAppListInfo(this, url, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                super.onSuccess(result);
                if (result != null && result.getData() != null && result.getStatus() == 0) {
                    titleBackView.setBackgroundColor(getResources().getColor(R.color.app_white_title_bg));
                    app_list_recyle.setVisibility(View.VISIBLE);
                    String categoryUrl = result.getData().getCategory_url();
                    if (TextUtils.isEmpty(categoryUrl)) {
                        listenRecyclerView2();
                        String laType = result.getData().getLayout_type();
                        if (!TextUtils.isEmpty(laType)) {
                            if (laType.equals(FindViewGroup.VIDEO_H2) || laType.equals(FindViewGroup.VIDEO_V2) || laType.equals(FindViewGroup.VIDEO_H3) || laType.equals(FindViewGroup.VIDEO_V3)) {
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, PixelsUtil.dip2px(10), 0, 0);
                                contentView.setLayoutParams(layoutParams);
                            }
                        }
                        bindListView(result.getData(), false);
                    } else {
                        listenRecyclerView();
                        resultRecycler.setPadding(0, PixelsUtil.dip2px(20), 0, 0);
                        getSelectTypeData(result.getData(), categoryUrl);
                    }
                } else {
                    resFailRequest();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                resFailRequest();
            }
        });
    }

    /**
     * 获取所有筛选类型
     */
    private void getSelectTypeData(final MainSubContentListBean<List<ContentInfo>> data, String cateUrl) {
        new VideoApi().getCateList(this, ConfigUrl.getMjCateListUrl(AppListActivity.this, cateUrl), new ApiCallBack<ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }

            @Override
            public void onSuccess(ResponseBaseBean<SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>>> result) {
                super.onSuccess(result);
                if (result != null && result.getData() != null && result.getStatus() == 0) {
                    SelectBean<SelectListBean<SelectListBean<SelectDetailBean>>> selectBean = result.getData();
                    urlHead = Common.getUrlHead(selectBean.getUrl());
                    urlTail = Common.getUrlTail(selectBean.getUrl());
                    List<SelectListBean<SelectListBean<SelectDetailBean>>> list = selectBean.getList();
                    for (int i = 0; i < list.size(); i++) {
                        SelectListBean<SelectListBean<SelectDetailBean>> listbean = list.get(i);
                        if (id == (listbean.getRes_id())) {
                            cate = listbean.getKeyname();
                            selectListBeans.clear();
                            type_layout.removeAllViews();
                            for (int j = 0; j < listbean.getList().size(); j++) {
                                listbean.getList().get(j).setLayout_type("select_type");
                                selectListBeans.add(listbean.getList().get(j));
                                SelectItemView selectItemView = new SelectItemView(AppListActivity.this, listbean.getList().get(j), appListAdapter.getSelectChange());
                                type_layout.addView(selectItemView, j);
                            }
                            type_layout.measure(0, 0);
                            typeHeight = type_layout.getMeasuredHeight();
                            break;
                        }
                    }
                    bindListView(data, false);
                } else {
                    resFailRequest();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                resFailRequest();
            }
        });
    }

    /**
     * 获取所选类型数据
     * @param url
     * @param startNum
     * @param dataNo
     * @param urlEnd
     */
    private void getSelectData(String url, int startNum, int dataNo, String urlEnd, final boolean isMore) {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "网络已断开，请检查网络设置!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isLoadingMore){
            return;
        }
        isLoadingMore = true;//正在加载
        String dataUrl = ConfigUrl.getSelectDataUrl(this, url, startNum, dataNo, urlEnd);
        new VideoApi().getSelectApi(this, dataUrl, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                if (isMore) {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
                    }
                } else {
                    showProgressDialog("正在加载...");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoadingMore = false;//正在加载完成
                if (isMore) {
                } else {
                    dismissProgressDialog();
                }
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                super.onSuccess(result);
                if (result != null && result.getData() != null && result.getStatus() == 0) {
                    MainSubContentListBean<List<ContentInfo>> mainSubContentListBean = result.getData();
                    if (!isMore) {
                        resultRecycler.scrollToPosition(0);
                        scroType = 0;
                        top_type_layout.setVisibility(View.GONE);
                        type_layout.setVisibility(View.GONE);
                        select_tag.setBackground(getResources().getDrawable(R.drawable.public_arrow_down));
                    }
                    if (mainSubContentListBean.getList() != null && mainSubContentListBean.getList().size() > 0) {
                        totalNum = mainSubContentListBean.getTotal();
                        emptyView.setVisibility(View.GONE);
                        //移除加载更多进度条
                        int lastPosition = contentBaseBeans.size() - 1;//最后一个位置
                        if (lastPosition >= 0) {
                            if (FindViewGroup.LOAD_MORE.equals(contentBaseBeans.get(lastPosition).getLayout_type())) {
                                contentBaseBeans.remove(lastPosition);
                                appListAdapter.notifyItemRemoved(lastPosition);
                            }
                        }
                        List<ContentBaseBean> infos = TransferData.getInstance().transToListModule(mainSubContentListBean);
                        if (isMore) {
                            int notifyPosition = contentBaseBeans.size();
                            contentBaseBeans.addAll(infos);
                            //添加加载更多进度条
                            ContentInfo loadMoreInfo = new ContentInfo();
                            loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
                            contentBaseBeans.add(loadMoreInfo);
                            appListAdapter.notifyItemRangeChanged(notifyPosition, contentBaseBeans.size());
                        } else {
                            while (contentBaseBeans.size() > selectListBeans.size()) {
                                if (!"select_type".equals(contentBaseBeans.get(selectListBeans.size()).getLayout_type())) {
                                    contentBaseBeans.remove(contentBaseBeans.get(selectListBeans.size()));
                                }
                            }
                            ContentBaseBean lineBean = new ContentBaseBean();
                            lineBean.setLayout_type("line");
                            contentBaseBeans.add(lineBean);
                            contentBaseBeans.addAll(infos);
                            //添加加载更多进度条
                            ContentInfo loadMoreInfo = new ContentInfo();
                            loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
                            contentBaseBeans.add(loadMoreInfo);
                            appListAdapter.notifyDataSetChanged();
                            if (loadMoreViewHolder != null) {
                                loadMoreViewHolder.hideLoadMoreUI();//隐藏加载更多UI
                            }
                        }
                        showCount = showCount + mainSubContentListBean.getList().size();
                    } else {
                        if (isMore) {
                            if (loadMoreViewHolder != null) {
                                loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
                            }
                        } else {
                            while (contentBaseBeans.size() > selectListBeans.size()) {
                                if (!"select_type".equals(contentBaseBeans.get(selectListBeans.size()).getLayout_type())) {
                                    contentBaseBeans.remove(contentBaseBeans.get(selectListBeans.size()));
                                }
                            }
                            ContentBaseBean lineBean = new ContentBaseBean();
                            lineBean.setLayout_type("line");
                            contentBaseBeans.add(lineBean);
                            ContentBaseBean emptyBaseBean = new ContentBaseBean();
                            emptyBaseBean.setLayout_type("no_data");
                            contentBaseBeans.add(emptyBaseBean);
                            appListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (isMore) {
                        if (loadMoreViewHolder != null) {
                            loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                        }
                    } else {
                        resFailRequest();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (isMore) {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                    }
                } else {
                    resFailRequest();
                }
            }
        });
    }

    /**
     * 列表页数据加载更多
     */
    private void getListMoreData() {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "网络已断开，请检查网络设置!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isLoadingMore){
            return;
        }
        isLoadingMore = true;//正在加载
        String url = ConfigUrl.getListMoreUrl(detailUrl, showCount, ConfigConstant.pageCount12);
        new ChoicenessApi().getAppListInfoNoHeader(this, url, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                if (loadMoreViewHolder != null) {
                    loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoadingMore = false;//正在加载完成
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                if (result != null && result.getData() != null && result.getStatus() == 0) {
                    titleBackView.setBackgroundColor(getResources().getColor(R.color.app_white_title_bg));
                    bindListView(result.getData(), true);
                } else {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (loadMoreViewHolder != null) {
                    loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                }
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindListView(MainSubContentListBean<List<ContentInfo>> data, boolean isLoadMore) {
        if (data != null && data.getList() != null && data.getList().size() > 0) {
            titleBackView.getNameTV().setText(data.getTitle());
            //移除加载更多进度条
            int lastPosition = contentBaseBeans.size() - 1;//最后一个位置
            if (lastPosition >= 0) {
                if (FindViewGroup.LOAD_MORE.equals(contentBaseBeans.get(lastPosition).getLayout_type())) {
                    contentBaseBeans.remove(lastPosition);
                    appListAdapter.notifyItemRemoved(lastPosition);
                }
            }
            totalNum = data.getTotal();
            if (!isLoadMore) {
                contentBaseBeans.clear();
                contentBaseBeans.addAll(selectListBeans);
            }
            if (selectListBeans != null && selectListBeans.size() > 0) {
                ContentBaseBean lineBean = new ContentBaseBean();
                lineBean.setLayout_type("line");
                contentBaseBeans.add(lineBean);
            }
            List<ContentBaseBean> infos = TransferData.getInstance().transToListModule(data);
            contentBaseBeans.addAll(infos);
            //添加加载更多进度条
            ContentInfo loadMoreInfo = new ContentInfo();
            loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
            contentBaseBeans.add(loadMoreInfo);
            if (isLoadMore) {
                appListAdapter.notifyItemRangeChanged(showCount, contentBaseBeans.size());
            } else {
                appListAdapter.notifyDataSetChanged();
            }
            showCount = showCount + data.getList().size();
        }else{
            if(isLoadMore){
                if (loadMoreViewHolder != null) {
                    loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
                }
            }
        }
        showContentView();
    }

    /**
     * RecyclerView滑动监听事件
     */
    private void listenRecyclerView() {
        resultRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrolledDY = dy;
                scroType = scroType + dy;
                if (top_type_layout.getVisibility() == View.GONE) {
                    if (scroType > typeHeight) {
                        top_type_layout.setVisibility(View.VISIBLE);
                        type_layout.removeAllViews();
                        for (int j = 0; j < selectListBeans.size(); j++) {
                            SelectItemView selectItemView = new SelectItemView(AppListActivity.this, selectListBeans.get(j), appListAdapter.getSelectChange());
                            selectItemView.setCheck(selectListBeans.get(j).getSelectPos());
                            type_layout.addView(selectItemView, j);
                            type_layout.measure(0, 0);
                            typeHeight = type_layout.getMeasuredHeight();
                        }
                        select_tag.setBackground(getResources().getDrawable(R.drawable.public_arrow_down));
                    }
                } else {
                    if (scroType < typeHeight) {
                        type_layout.removeAllViews();
                        top_type_layout.setVisibility(View.GONE);
                        type_layout.setVisibility(View.GONE);
                    } else {
                        select_tag.setBackground(getResources().getDrawable(R.drawable.public_arrow_down));
                        type_layout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {//滑动拖拽

                } else if (RecyclerView.SCROLL_STATE_IDLE == newState) {//滑动停止
                    if (gridLayoutManager != null) {
                        int lastPosition = gridLayoutManager.findLastVisibleItemPosition();
                        int itemCount = gridLayoutManager.getItemCount();
                        if (lastPosition >= itemCount - 1) {//滑动到底部
                            if(scrolledDY > 0){
                                getSelectData(getUrlPart(), showCount, 12, urlTail, true);//列表页数据加载更多
                            }
                        }
                    }
                } else {//滑动中

                }
            }
        });
    }

    /**
     * RecyclerView滑动监听事件
     */
    private void listenRecyclerView2() {
        resultRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledDY = dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {//滑动拖拽

                } else if (RecyclerView.SCROLL_STATE_IDLE == newState) {//滑动停止
                    if (gridLayoutManager != null) {
                        int lastPosition = gridLayoutManager.findLastVisibleItemPosition();
                        int itemCount = gridLayoutManager.getItemCount();
                        if (lastPosition >= itemCount - 1) {//滑动到底部
                            if(scrolledDY > 0){
                                getListMoreData();//列表页数据加载更多
                            }
                        }
                    }
                } else {//滑动中

                }
            }
        });
    }

    /**
     * 获取url后缀的前半部分
     */
    private String getUrlPart() {
        String urlPart = "";
        urlPart = urlPart + urlHead + "-" + cate + id + "-";
        int p = selectListBeans.size();
        for (int n = 0; n < p; n++) {
            int posId = selectListBeans.get(n).getSelectPos();
            String key = selectListBeans.get(n).getKeyname();
            if (posId < selectListBeans.get(n).getList().size()) {
                urlPart = urlPart + key + ((SelectDetailBean) selectListBeans.get(n).getList().get(posId)).getId() + "-";
            }
        }
        urlPart = urlPart.trim();
        return urlPart;
    }

    /**
     * 网络请求失败或者无网络处理
     */
    private void resFailRequest() {
        app_list_recyle.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        selectListBeans.clear();
        contentBaseBeans.clear();
        appListAdapter.notifyDataSetChanged();
    }

    private void initReportBean(String resId) {
        if (!isReported) {
            isReported = true;
            //报数类型
            ReportFromBean bean = new ReportFromBean();
            if (type == ResTypeUtil.res_type_banner) {
                bean.setColid(resId);
            } else if (type == ResTypeUtil.res_type_category) {
                bean.setSubcateid(resId);
            }
            bean.setPagetype(ReportBusiness.getPageType(type));
            bean.setFrompage(detailUrl);
            appListAdapter.setReportBean(bean);
        }
    }
}