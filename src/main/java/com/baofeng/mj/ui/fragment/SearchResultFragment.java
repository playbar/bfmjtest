package com.baofeng.mj.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.SearchResultBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.ui.adapter.SearchResultAdapter;
import com.baofeng.mj.ui.listeners.RecycleViewScrollDetector;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.SearchApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunshine on 16/9/21.
 * 搜索结果集列表页
 */
public class SearchResultFragment extends BaseFragment {
    private View rootView;
    private PullToRefreshRecyle search_result_recyle;
    private RecyclerView recyclerView;
    private SearchResultAdapter searchResultAdapter;
    private LinearLayoutManager linearLayoutManager;
    private MainSubContentListBean<List<ContentInfo>> mData;
    private Context mContext;
    private int showCount = 0;
    private String mChannel;
    private String mKey;
    private List<ContentInfo> contentInfos = new ArrayList<ContentInfo>();

    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private RecycleViewScrollDetector viewScrollDetector;
    private LoadMoreViewHolder loadMoreViewHolder;
    private boolean isLoadingMore;//true正在加载，false不是

    public void setLoadMoreViewHolder(LoadMoreViewHolder loadMoreViewHolder){
        this.loadMoreViewHolder = loadMoreViewHolder;
    }

    public SearchResultFragment initThis(Context context, String key, String channel, MainSubContentListBean<List<ContentInfo>> data) {
        this.mContext = context;
        this.mData = data;
        this.mChannel = channel;
        this.mKey = key;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downLoadBusiness = new MyDownLoadBusiness(getActivity());
        BaseApplication.INSTANCE.addDownLoadBusiness(downLoadBusiness);
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                downLoadBusiness.apkInstallNotify(packageName);//apk安装完成
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
    }

    @Override
    public void onDestroy() {
        BaseApplication.INSTANCE.removeDownLoadBusiness(downLoadBusiness);
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        downLoadBusiness = null;
        apkInstallNotify = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.search_result_layout, null);
        find(rootView);
        init();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Glide.with(this).onStop();//停止图片加载
        super.onDestroyView();
    }

    /**
     * 控件寻址
     */
    private void find(View view) {
        search_result_recyle = (PullToRefreshRecyle) view.findViewById(R.id.search_result_recyle);
        search_result_recyle.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView = search_result_recyle.getRefreshableView();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        viewScrollDetector = new RecycleViewScrollDetector(linearLayoutManager, null, false, new RecycleViewScrollDetector.ScrollStateCallback() {
            @Override
            public void scrollToEnd(int scrolledDY) {//滑动到底部
                if(scrolledDY > 0){//上拉
                    loadMoreData();//加载更多数据
                }
            }
        });
        recyclerView.setOnScrollListener(viewScrollDetector);
    }

    /***
     * 初始化
     */
    private void init() {
        if(mData == null || mData.getList() == null){
            return;
        }
        for (int i = 0; i < mData.getList().size(); i++) {
            if (mData.getObject_type() == 1) {
                mData.getList().get(i).setIndex(1);
            } else if (mData.getObject_type() == 2) {
                mData.getList().get(i).setIndex(2);
            }
        }
        contentInfos.clear();
        if (mData.getHas_more() == 0) {
            ContentInfo emptyInfo = new ContentInfo();
            emptyInfo.setIndex(0);
            contentInfos.add(emptyInfo);
            ContentInfo titleInfo = new ContentInfo();
            titleInfo.setIndex(3);
            titleInfo.setTitle("热门推荐");
            contentInfos.add(titleInfo);
        }
        showCount = showCount + mData.getList().size();
        contentInfos.addAll(mData.getList());

        //添加加载更多进度条
        ContentInfo contentInfo = new ContentInfo();
        contentInfo.setIndex(4);
        contentInfos.add(contentInfo);

        searchResultAdapter = new SearchResultAdapter(mContext, this, mData, contentInfos, downLoadBusiness, mKey);
        recyclerView.setAdapter(searchResultAdapter);
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (!NetworkUtil.networkEnable()) {
            Toast.makeText(getActivity(), "网络已断开，请检查网络设置!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isLoadingMore){
            return;
        }
        isLoadingMore = true;//正在加载
        String url = ConfigUrl.getSearchListUrl(getUrlTail(mKey, mChannel, mData.getObject_type(), showCount, ConfigConstant.pageCount12));
        new SearchApi().getSearchResult(mContext, url, new ApiCallBack<SearchResultBean>() {
            @Override
            public void onStart() {
                super.onStart();
                if(loadMoreViewHolder != null){
                    loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoadingMore = false;//正在加载完成
            }

            @Override
            public void onSuccess(SearchResultBean result) {
                super.onSuccess(result);
                if (result != null && result.getData() != null) {
                    List<MainSubContentListBean<List<ContentInfo>>> list = result.getData().getList();
                    if (list != null && list.size() > 0) {
                        MainSubContentListBean<List<ContentInfo>> mainSubContentListBean = list.get(0);
                        int objectType = mainSubContentListBean.getObject_type();
                        List<ContentInfo> contentInfoList = mainSubContentListBean.getList();
                        if(contentInfoList != null && contentInfoList.size() > 0){
                            //移除加载更多进度条
                            int lastPosition = contentInfos.size() - 1;//最后一个位置
                            if(lastPosition >= 0){
                                if(contentInfos.get(lastPosition).getIndex() == 4){
                                    contentInfos.remove(lastPosition);
                                    searchResultAdapter.notifyItemRemoved(lastPosition);
                                }
                            }
                            //添加更多数据
                            for (int i = 0; i < contentInfoList.size(); i++) {
                                if(objectType == 1) {
                                    contentInfoList.get(i).setIndex(1);
                                }else if(objectType == 2) {
                                    contentInfoList.get(i).setIndex(2);
                                }
                            }
                            contentInfos.addAll(contentInfoList);
                            //添加加载更多进度条
                            ContentInfo contentInfo = new ContentInfo();
                            contentInfo.setIndex(4);
                            contentInfos.add(contentInfo);
                            //更新UI
                            searchResultAdapter.notifyItemRangeChanged(showCount,contentInfos.size());
                            showCount = showCount + contentInfoList.size();
                            return;
                        }
                    }
                }
                if(loadMoreViewHolder != null){
                    loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(loadMoreViewHolder != null){
                    loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                }
            }
        });
    }

    /**
     * 获取分页地址的尾部
     *
     * @param key
     * @param channel
     * @param type
     * @param startNum
     * @param pageNum
     * @return
     */
    private String getUrlTail(String key, String channel, int type, int startNum, int pageNum) {
        return key + "-" + "channel" + channel + "-" + "type" + type + "-" + "start" + startNum + "-" + "num" + pageNum + ".js";
    }

    //报数
//    private void initReportBean() {
//        ReportFromBean bean = new ReportFromBean();
//        bean.setFrompage(detailUrl);
//        searchResultAdapter.setReportBean(bean);
//    }
}
