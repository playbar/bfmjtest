package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentBaseBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentBean;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.ui.adapter.ChoicenessAdapter;
import com.baofeng.mj.ui.listeners.RecycleViewScrollDetector;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.FindViewGroup;
import com.baofeng.mj.util.viewutil.TransferData;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;

import java.util.ArrayList;
import java.util.List;

/**
 * type:9频道页
 * Created by muyu on 2016/6/12.
 */
public class ChoicenessActivity extends BaseLoadingActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {

    private PullToRefreshRecyle pullToRefreshRecyle;
    private RecyclerView recyclerView;
    private ChoicenessAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private RecycleViewScrollDetector viewScrollDetector;
    private List<ContentBaseBean> beans;
    private String detailUrl;

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
        getData();
        ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_choiceness_content;
    }

    @Override
    protected void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.removeDownLoadBusiness(downLoadBusiness);
        downLoadBusiness = null;
        apkInstallNotify = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initViews() {
        gridLayoutManager = new GridLayoutManager(this, 12, GridLayoutManager.VERTICAL, false);
        pullToRefreshRecyle = (PullToRefreshRecyle) findViewById(R.id.choice_list_pulltorefresh);
        pullToRefreshRecyle.setOnRefreshListener(this);
        recyclerView = pullToRefreshRecyle.getRefreshableView();
        viewScrollDetector = new RecycleViewScrollDetector(null, titleBgLayout, true, null);
        recyclerView.setOnScrollListener(viewScrollDetector);
        beans = new ArrayList<ContentBaseBean>();
        adapter = new ChoicenessAdapter(this, beans);
        adapter.setDownLoadBusiness(downLoadBusiness);
        recyclerView.setAdapter(adapter);

        adapter.setHasShadow(true);

        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                // return adapter.getItemViewType(position) != RecommendAdapter.item ? 3 : 1;
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
    }

    private void getData() {
        if (getIntent() != null) {
            if (getIntent().getStringExtra("next_title") != null) {
                setTitle(getIntent().getStringExtra("next_title") + "");
            }
            if (getIntent().getStringExtra("next_url") != null) {
                detailUrl = getIntent().getStringExtra("next_url");
            }
            type = getIntent().getIntExtra("next_type", 0);
            subType = getIntent().getIntExtra("next_subType", 0);
            initReportBean();
            requestData(detailUrl);
        }
    }

    private void requestData(String url) {

        new ChoicenessApi().getChoicenessInfo(this, url, new ApiCallBack<ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>>>() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>>> result) {
                if (result != null) {
                    if (result.getStatus() == 0) {
                        bindView(result.getData());
                    } else {
                        Toast.makeText(ChoicenessActivity.this, result.getStatus_msg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                hideContent();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
                pullToRefreshRecyle.onRefreshComplete();
                titleBackView.getNameTV().setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindView(MainSubContentBean<List<MainSubContentListBean<List<ContentInfo>>>> data) {
        adapter.setHasTag(true);
        List<ContentBaseBean> contentInfos = TransferData.getInstance().transToSmallModule(data);
        beans.clear();
        beans.addAll(contentInfos);
        adapter.notifyDataSetChanged();

        showContentView();
        dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refreshView) {
            requestData(detailUrl);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        titleBackView.getNameTV().setVisibility(View.GONE);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    //报数
    private void initReportBean() {
        ReportFromBean bean = new ReportFromBean();
        bean.setFrompage(detailUrl);
        adapter.setReportBean(bean);
    }
}
