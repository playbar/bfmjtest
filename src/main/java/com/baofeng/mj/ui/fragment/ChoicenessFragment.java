package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.brbusiness.DeleteDownloadingReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.NoNetWorkActivity;
import com.baofeng.mj.ui.adapter.ChoicenessAdapter;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.TransferData;
import com.baofeng.mj.utils.ACache;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;
import com.mojing.dl.domain.DownloadItem;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐页面Fragment
 * Created by muyu on 2016/3/28.
 */
public class ChoicenessFragment extends BaseViewPagerFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {
    private LinearLayout contentLayout;
    private PullToRefreshRecyle pullToRefreshScrollView;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> fragData;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private DeleteDownloadingReceiver.DeleteDownloadingNotify deleteDownloadingNotify;
    private DeleteDownloadingReceiver deleteDownloadingReceiver;
    private RelativeLayout noNetWorkInnerLayout;
    private ChoicenessAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private List<ContentBaseBean> beans;
    private CustomProgressView loadingView; //在fragment中显示loading
    private int currentTab;  //一级tab位置
    private int subCurrentTab; //二级tab位置
    private static final int START_DOWNLOAD = 1;
    private String nextUrl;
    private int resId;

    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(!isVisibleToUser){
            mVideoPlayerManager.stopAnyPlayback();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTab = getArguments().getInt("currentTab");
            subCurrentTab = getArguments().getInt("subCurrentTab");
            nextUrl = getArguments().getString("next_url");
            resId = getArguments().getInt("res_id");

        }
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                if(downLoadBusiness != null){
                    downLoadBusiness.apkInstallNotify(packageName);//apk安装完成
                }
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);

        deleteDownloadingReceiver = new DeleteDownloadingReceiver();
        getActivity().registerReceiver(deleteDownloadingReceiver, new IntentFilter(DeleteDownloadingReceiver.ACTION_DELETE_DOWNLOADING));
        deleteDownloadingNotify = new DeleteDownloadingReceiver.DeleteDownloadingNotify() {
            @Override
            public void deleteNotify(DownloadItem downloadItem) {
                if(downLoadBusiness != null){
                    downLoadBusiness.deleteDownloading(downloadItem);//删除正在下载
                }
            }
        };
        deleteDownloadingReceiver.addDeleteDownloadingNotify(deleteDownloadingNotify);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_choiceness, container, false);
            initViews();
            getData();
        }else{
            removeRootView();
            if(adapter != null){
                /**
                 * 刷新下载进度需要此行代码，否则会出现的问题：
                 * 当前界面某个资源下载进度50%，离开当前界面，
                 * 当资源下载完成后再回到当前界面，下载进度还是50%
                 * (以后替换新的下载库，这个问题可以解)。
                 */
                adapter.notifyDataSetChanged();
            }
        }
        if(downLoadBusiness != null){
            BaseApplication.INSTANCE.addDownLoadBusiness(downLoadBusiness);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Glide.with(this).onStop();//停止图片加载
        if(downLoadBusiness != null){
            BaseApplication.INSTANCE.removeDownLoadBusiness(downLoadBusiness);
        }
//        removeRootView();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        showNetWork();
    }

    private void initViews() {
        downLoadBusiness = new MyDownLoadBusiness(getActivity());
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
        adapter = new ChoicenessAdapter(getActivity(), this, beans, mVideoPlayerManager);
        adapter.setDownLoadBusiness(downLoadBusiness);
        adapter.setHasShadow(false);
        recyclerView.setAdapter(adapter);
        noNetWorkInnerLayout = (RelativeLayout) rootView.findViewById(R.id.video_no_network_tab_layout);
        noNetWorkInnerLayout.setOnClickListener(this);
        loadingView = (CustomProgressView) rootView.findViewById(R.id.choiceness_loading);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mVideoPlayerManager != null) {
            mVideoPlayerManager.resetMediaPlayer();
        }
    }

    private void getData() {
        boolean loaded = SettingSpBusiness.getInstance().getTabClickStatus(currentTab, subCurrentTab, 0);  //是否请求过数据
        if (loaded) {
            initCache();
        } else {
            requestData();
        }
        initReportBean(nextUrl, resId);
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
            requestData();
        }
    }

    private void requestData() {
        new ChoicenessApi().getMainSubTabInfo(getActivity(), nextUrl, new ApiCallBack<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> result) {
                if (result != null && result.getStatus() == 0) {
                    showOrHideEmptyView(false);//隐藏空页面
                    if (result.getData().getList().size() > 0) { //设置已请求过数据
                        SettingSpBusiness.getInstance().setTabClickStatus(currentTab, subCurrentTab, 0, true);
                    }
                    bindView(result.getData());
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
    private void showOrHideEmptyView(boolean show){
        if(show){//显示空页面
            if(beans == null || beans.size() == 0){
                emptyView.setVisibility(View.VISIBLE);
            }
        }else{//隐藏空页面
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
            requestData();
            showNetWork();
        } else if (i == R.id.video_no_network_tab_layout) {
            Intent intent = new Intent(getActivity(), NoNetWorkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        requestData();
        showNetWork();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    @Override
    public void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        deleteDownloadingReceiver.removeDeleteDownloadingNotify(deleteDownloadingNotify);
        getActivity().unregisterReceiver(deleteDownloadingReceiver);
        //downLoadBusiness = null;
        apkInstallNotify = null;
        deleteDownloadingNotify = null;
        super.onDestroy();
    }


    private void initReportBean(String url, int resId) {
        ReportFromBean bean = new ReportFromBean();
        bean.setFrompage(url);
        //pagetype无法动态判断,暂时固定写在在客户端
//        if (resId == 443512) {
//            bean.setPagetype("recommend");
//        } else if (resId == 443519
//                || resId == 443520
//                || resId == 443521
//                || resId == 443522
//                || resId == 443523) {
//            bean.setPagetype("appgame");
//        }
        adapter.setReportBean(bean);
    }
}
