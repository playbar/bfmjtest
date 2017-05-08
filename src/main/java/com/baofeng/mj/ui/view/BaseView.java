package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.ui.activity.AppListActivity;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用主要模块View父类
 * Created by muyu on 2016/5/16.
 */
public abstract class BaseView extends FrameLayout implements AdapterView.OnItemClickListener {
    public Context mContext;
    public MainSubContentListBean<List<ContentInfo>> data;
    public int type;
    public boolean isRank = false;
    public List<String> picUrls;
    public boolean hasTag;
    public DownLoadBusiness<ContentInfo> downLoadBusiness;
    //报数
    private ReportFromBean reportBean;

    public BaseView(Context context, boolean hasTag) {
        super(context);
        this.mContext = context;
        this.hasTag = hasTag;
    }

    public BaseView(Context context, boolean hasTag, DownLoadBusiness<ContentInfo> downLoadBusiness) {
        super(context);
        this.mContext = context;
        this.hasTag = hasTag;
        this.downLoadBusiness = downLoadBusiness;
    }

    public BaseView(Context context, int type, boolean hasTag) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.hasTag = hasTag;
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        requestData();
    }

    public BaseView(Context context, boolean hasTag, DownLoadBusiness<ContentInfo> downLoadBusiness, boolean isRank) {
        super(context);
        this.mContext = context;
        this.hasTag = hasTag;
        this.downLoadBusiness = downLoadBusiness;
        this.isRank = isRank;
    }

    /**
     * 用于实现单张图片的bannerView
     *
     * @param context
     * @param picUrls 图片地址
     */
    public BaseView(Context context, List<String> picUrls) {
        super(context);
        this.mContext = context;
        this.picUrls = picUrls;
    }

    public void requestData() {
        data = new MainSubContentListBean<List<ContentInfo>>();
        List<ContentInfo> list = new ArrayList<ContentInfo>();
        ContentInfo info;
        for (int i = 0; i < 4; i++) {
            info = new ContentInfo();
            info.setTitle("Test data");
            list.add(info);
        }
        data.setList(list);
    }

    public void initView(MainSubContentListBean<List<ContentInfo>> bean) {
        this.data = bean;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        onClickToActivity(data.getList().get(position));
    }

    public void onClickToActivity(ContentInfo contentInfo) {
        ResTypeUtil.onClickToActivity(mContext, contentInfo);
    }


    public void moreClick() {
        //跳转更多页面
        Intent categoryIntent = new Intent(mContext, AppListActivity.class);
        categoryIntent.putExtra("next_type", ResTypeUtil.res_type_banner);
        categoryIntent.putExtra("next_subType", 0);
        categoryIntent.putExtra("next_title", data.getTitle());
        categoryIntent.putExtra("next_url", data.getUrl());
        mContext.startActivity(categoryIntent);
    }

    public ReportFromBean getReportBean() {
        return reportBean;
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }

}
