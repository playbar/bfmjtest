package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.TopicBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.ChoicenessAdapter;
import com.baofeng.mj.ui.listeners.RecycleViewScrollDetector;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题
 * Created by hanyang on 2016/5/6.
 */
public class TopicActivity extends BaseLoadingActivity implements View.OnClickListener {
    private PullToRefreshRecyle pullToRefreshScrollView;
    private RecyclerView recyclerView;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private ChoicenessAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private RecycleViewScrollDetector viewScrollDetector;
    private List<ContentBaseBean> beanList;
    private int showCount = 0;//当前显示数量位置
    private boolean isLoadingMore;//true正在加载，false不是
    private String moreUrl;//下一页Url
    private boolean isReported = false;
    private String operateJson;
    private LoadMoreViewHolder loadMoreViewHolder;
    private String detailUrl;

    public void setLoadMoreViewHolder(LoadMoreViewHolder loadMoreViewHolder){
        this.loadMoreViewHolder = loadMoreViewHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            detailUrl = intent.getStringExtra("next_url");
            operateJson = intent.getStringExtra(PushTypeBusiness.OPERATE_JSON);
        }
        downLoadBusiness = new MyDownLoadBusiness(this);
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                downLoadBusiness.apkInstallNotify(packageName);
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.addDownLoadBusiness(downLoadBusiness);
        titleBackView.setPageType(ReportBusiness.PAGE_TYPE_TOPIC_LIST);
        initView();
		initData();
        ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topic_content;
    }

	@Override
    protected void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.removeDownLoadBusiness(downLoadBusiness);
        downLoadBusiness = null;
        apkInstallNotify = null;
        super.onDestroy();
    }

    private void initView() {
        pullToRefreshScrollView = (PullToRefreshRecyle) findViewById(R.id.topic_pulltorefresh);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.DISABLED);//禁止加载
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        gridLayoutManager = new GridLayoutManager(this, 12, GridLayoutManager.VERTICAL, false);
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
                    case FindViewGroup.LOAD_MORE_NUM://load_more
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
        recyclerView.setLayoutManager(gridLayoutManager);
        viewScrollDetector = new RecycleViewScrollDetector(gridLayoutManager, titleBgLayout, true, new RecycleViewScrollDetector.ScrollStateCallback() {
            @Override
            public void scrollToEnd(int scrolledDY) {//滑动到底部
                if(scrolledDY > 0){
                    initListData(true);
                }
            }
        });
        recyclerView.setOnScrollListener(viewScrollDetector);
        viewScrollDetector.addScrollCallback(new RecycleViewScrollDetector.ScrollCallback() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (beanList.size() > 0) {
                    ContentInfo contentInfo = (ContentInfo) beanList.get(0);
                    if (contentInfo != null && titleBgLayout != null) {
                        if (titleBgLayout.getAlpha() >= 1) {//完全显示出来
                            setTitle(contentInfo.getTitle());
                            titleBackView.setVRResBlack();
                        } else {
                            setTitle("");
                            titleBackView.setVRResWhite();
                        }
                    }
                }
            }
        });

        beanList = new ArrayList<ContentBaseBean>();
        adapter = new ChoicenessAdapter(this, beanList);
        adapter.setDownLoadBusiness(downLoadBusiness);
        recyclerView.setAdapter(adapter);
        hideTopLine();
        titleBackView.setVRResWhite();
    }

    private void initData() {
        new ChoicenessApi().getTopicDetailInfo(this, detailUrl, new ApiCallBack<ResponseBaseBean<TopicBean>>() {
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
            public void onSuccess(ResponseBaseBean<TopicBean> result) {
                super.onSuccess(result);
                if (result != null && result.getStatus() == 0 && result.getData() != null) {
                    showContentView();
                    beanList.clear();
                    //专题大图
                    TopicBean topicBean = result.getData();
                    ContentInfo topicInfo = new ContentInfo();
                    topicInfo.setBanner(topicBean.getThumb_pic_url());
                    topicInfo.setTitle(topicBean.getTitle());
                    topicInfo.setSubtitle(topicBean.getSubtitle());
                    topicInfo.setLayout_type(FindViewGroup.GLOBAL_TOPIC);
                    beanList.add(topicInfo);
                    //加载更多
                    ContentInfo loadMoreInfo = new ContentInfo();
                    loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
                    beanList.add(loadMoreInfo);
                    adapter.notifyDataSetChanged();

                    moreUrl = topicBean.getList_url();
                    initListData(false);
                    initReportBean(topicBean.getRes_id() == 0 ? null : String.valueOf(topicBean.getRes_id()));
                } else {
                    hideContent();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                hideContent();
            }
        });
    }

    private void initListData(final boolean isLoadMore) {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(this, "网络已断开，请检查网络设置!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isLoadingMore){
            return;
        }
        isLoadingMore = true;//正在加载
        String listUrl = ConfigUrl.getListMoreUrl(moreUrl, showCount, ConfigConstant.pageCount12);
        new ChoicenessApi().getTopicDetailListInfo(this, listUrl, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                if (isLoadMore) {//是加载更多
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoadingMore = false;//正在加载完成
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                if (result != null) {
                    MainSubContentListBean<List<ContentInfo>> mainSubContentListBean = result.getData();
                    if (mainSubContentListBean != null) {
                        List<ContentInfo> contentInfoList = mainSubContentListBean.getList();
                        if (contentInfoList != null && contentInfoList.size() > 0) {
                            //移除加载更多进度条
                            int lastPosition = beanList.size() - 1;//最后一个位置
                            if (lastPosition >= 0) {
                                if (FindViewGroup.LOAD_MORE.equals(beanList.get(lastPosition).getLayout_type())) {
                                    beanList.remove(lastPosition);
                                    adapter.notifyItemRemoved(lastPosition);
                                }
                            }
                            //添加更多数据
                            String layoutType = mainSubContentListBean.getLayout_type();
                            for (int i = 0; i < contentInfoList.size(); i++) {
                                ContentInfo contentInfo = contentInfoList.get(i);
                                contentInfo.setLayout_type(layoutType);
                                contentInfo.setIndex(i);
                                contentInfo.setParentResId(mainSubContentListBean.getRes_id());
                            }
                            beanList.addAll(contentInfoList);
                            //添加加载更多进度条
                            ContentInfo loadMoreInfo = new ContentInfo();
                            loadMoreInfo.setLayout_type(FindViewGroup.LOAD_MORE);
                            beanList.add(loadMoreInfo);
                            //更新UI
                            adapter.notifyItemRangeChanged(showCount, beanList.size());
                            showCount = showCount + contentInfoList.size();
                            return;
                        }
                    }
                }
                if (isLoadMore) {//是加载更多
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (isLoadMore) {//是加载更多
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.refreshView) {
            initData();
        }
    }

    //报数
    private void initReportBean(String resId) {
        if(!isReported){
            isReported = true;
            ReportFromBean bean = new ReportFromBean();
            bean.setFrompage(detailUrl);
            bean.setPagetype(ReportBusiness.PAGE_TYPE_TOPIC_LIST);
            bean.setTopicid(resId);
            adapter.setReportBean(bean);
        }
    }
}
