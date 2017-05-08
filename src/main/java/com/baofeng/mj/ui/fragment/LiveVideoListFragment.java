package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.ui.activity.LiveVideoListActivity;
import com.baofeng.mj.ui.adapter.LiveVideoListAdapter;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.viewholder.LoadMoreViewHolder;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.LiveVideoListApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.util.ArrayList;

/**
 * Created by yushaochen on 2017/2/28.
 */

public class LiveVideoListFragment extends BaseViewPagerFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {

    private int selected_type;

    public static final int CONTENT_LIST = 0;
    public static final int LOAD_MORE_NUM = 1;

    private GridLayoutManager gridLayoutManager;

    private PullToRefreshRecyle pullToRefreshScrollView;

    private RecyclerView recyclerView;

    private LiveVideoListAdapter adapter;

    private EmptyView emptyView;

    private ArrayList<ContentInfo> contentListBeans = new ArrayList<ContentInfo>();//中部列表数据

    private int total;

    private LoadMoreViewHolder loadMoreViewHolder;

    private int scrolledDY;

    private boolean lock = false;

    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_live_video_list, container, false);
            initViews();
            setListener();
            handleUrl(0);
        }else{
            removeRootView();
        }
        return rootView;

    }

    private void initViews() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 12, GridLayoutManager.VERTICAL, false);
        emptyView = (EmptyView) rootView.findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);
        emptyView.getRefreshView().setOnClickListener(this);

        pullToRefreshScrollView = (PullToRefreshRecyle) rootView.findViewById(R.id.choiceness_pulltorefresh);
        pullToRefreshScrollView.setOnRefreshListener(this);
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case CONTENT_LIST:
                    case LOAD_MORE_NUM:
                        return 12;
                    default:
                        return -1;
                }

            }
        });

        adapter = new LiveVideoListAdapter(getActivity(), this);
        adapter.setPageSelectedType(selected_type);
        recyclerView.setAdapter(adapter);
    }

    private void setListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                loadMoreData();//列表页数据加载更多
                            }
                        }
                    }
                } else {//滑动中

                }
            }
        });
    }

    public void loadMoreData() {
        if(total <= contentListBeans.size()) {//已经是最后一页了
            if (loadMoreViewHolder != null) {
                loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
            }
        } else {//继续向服务器请求数据
            if (loadMoreViewHolder != null) {
                loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
            }
            handleUrl(contentListBeans.size());
        }
    }

    public void handleUrl(int start){
        if(lock) return;
        lock = true;
        String reqUrl = "";
        if(!TextUtils.isEmpty(url)) {
            if(selected_type == LiveVideoListActivity.SELECTED_TYPE_LATEST) {
                reqUrl = Common.getUrlHead(url) + "-" + "start" + start + "-" + "num" + ConfigConstant.pageCount12 + Common.getUrlTail(url);
            } else if(selected_type == LiveVideoListActivity.SELECTED_TYPE_HOTTEST) {
                reqUrl = Common.getUrlHead(url) + "-" + "start" + start + "-" + "num" + ConfigConstant.pageCount12 + "-sort2" + Common.getUrlTail(url);
            }
        }
        requestData(reqUrl, start);
    }

    private void requestData(final String reqUrl,final int start) {
        new LiveVideoListApi().getList(getContext(), reqUrl, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {

            @Override
            public void onStart() {
                super.onStart();
                if(start <= 0) {
                    showProgressDialog("正在加载...");
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.hideLoadMoreUI();
                    }
                }
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> result) {
                super.onSuccess(result);
                emptyView.setVisibility(View.GONE);
                if(null != result && result.getStatus() == 0) {
                    if(null != result.getData()) {
                       setListData(result.getData(),start);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
                pullToRefreshScrollView.onRefreshComplete();
                lock = false;
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(start <= 0) {
                    if (!NetworkUtil.networkEnable()) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                    }
                }
            }
        });
    }

    private void setListData(MainSubContentListBean<ArrayList<ContentInfo>> data, int start) {
        if(null != data.getList() && data.getList().size() > 0) {

            total = data.getTotal();//服务器端总数据

            if(start <= 0) {//取第一页数据
                contentListBeans.clear();
            }

            contentListBeans.addAll(data.getList());//添加数据

            adapter.setContentData(contentListBeans);

            adapter.notifyDataSetChanged();

            if(start > 0) {
                if (loadMoreViewHolder != null) {
                    loadMoreViewHolder.updateLoadMoreUI(false, true);//更新加载更多UI
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refreshView) {
            handleUrl(0);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        handleUrl(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    public void setType(int type) {
        selected_type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLoadMoreViewHolder(LoadMoreViewHolder loadMoreViewHolder) {
        this.loadMoreViewHolder = loadMoreViewHolder;
    }
}
