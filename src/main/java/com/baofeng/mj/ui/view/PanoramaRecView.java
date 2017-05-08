package com.baofeng.mj.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.PanoramaDetailActivity;
import com.baofeng.mj.ui.adapter.PanoramaRecAdapter;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.ListViewUtil;

import java.util.List;

/**
 * 全景视频推荐View
 * Created by muyu on 2016/12/28.
 */
public class PanoramaRecView extends FrameLayout implements AdapterView.OnItemClickListener{

    private Context mContext;
    private View rootView;
    private InScrollListView panoramaListView;

    private PanoramaRecAdapter adapter;
    public PanoramaRecView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public PanoramaRecView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_panorama_rec, this);
        panoramaListView = (InScrollListView) rootView.findViewById(R.id.panorama_rec_list);
        panoramaListView.setOnItemClickListener(this);
        adapter = new PanoramaRecAdapter(mContext);
        panoramaListView.setAdapter(adapter);
        new ListViewUtil().setListViewHeightBasedOnChildren(panoramaListView);
    }

    public void bindView(List<ContentInfo> rec_list){
        adapter.setDate(rec_list);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        final ContentInfo contentInfo = (ContentInfo) adapterView.getAdapter().getItem(i);
        ((PanoramaDetailActivity)mContext).destroyView();
        ((Activity)mContext).finish();
        ResTypeUtil.onClickToActivity(mContext, contentInfo);
        reportClick(contentInfo,i);

    }

    private void reportClick(ContentInfo contentInfo, int i){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("jump");
        bean.setTpos("1");
        bean.setPagetype("detail");
        bean.setTitle(contentInfo.getTitle());
        bean.setVideoid(contentInfo.getRes_id());
        bean.setTypeid(String.valueOf(contentInfo.getType()));
        bean.setRecovideo(i == 0 ? "1" : "2");
        ReportBusiness.getInstance().reportClick(bean);
    }
}
