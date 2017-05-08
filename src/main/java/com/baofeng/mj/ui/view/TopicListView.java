package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 专题栏目view
 * Created by yushaochen on 2017/3/2.
 */

public class TopicListView extends RelativeLayout{

    private Context mContext;
    private View rootView;
    private LinearLayout linearLayout;

    private List<ContentInfo> topListBeans = new ArrayList<ContentInfo>();//专题栏目

    private ReportFromBean reportBean;
    
    public TopicListView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TopicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private RelativeLayout.LayoutParams itemImageLayoutParams;

    private void initView() {
        //按照16:9，依据当前手机的屏幕宽，设定高（产品要求出现1.5个item）
        int screenWidth = PixelsUtil.getWidthPixels();
        int px10 = PixelsUtil.dip2px(10);
        int imgWidth = (int)((screenWidth - px10 * 3) / 1.75f);
        int imgHeight = (int) (imgWidth / 1.78f);
        itemImageLayoutParams = new RelativeLayout.LayoutParams(imgWidth, imgHeight);

        rootView = LayoutInflater.from(mContext).inflate(R.layout.topic_list_view, null);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.circle_icon_linear);
        addView(rootView);
    }

    private void refreshView() {
        for(int x = 0; x < topListBeans.size(); x++) {
            RelativeLayout item = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.horizontal_one_point_five_item, null);
            View rightView = item.findViewById(R.id.right_view);
            if(x != topListBeans.size() - 1) {
                rightView.setVisibility(View.VISIBLE);
            } else {
                rightView.setVisibility(View.GONE);
            }

            ImageView image = (ImageView) item.findViewById(R.id.image);
            image.setLayoutParams(itemImageLayoutParams);
            GlideUtil.displayImage(mContext, new WeakReference<ImageView>(image),topListBeans.get(x).getPic_url(),R.drawable.img_default_2n_cross);
            item.setTag(x);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBusiness.getInstance().putHeader(topListBeans.get((int)v.getTag()), reportBean);
                    ResTypeUtil.onClickToActivity(mContext, topListBeans.get((int)v.getTag()));
                }
            });
            linearLayout.addView(item);
        }
    }

    public void setData(MainSubContentListBean<List<ContentInfo>> data) {
        linearLayout.removeAllViews();
        topListBeans.clear();
        if(null != data) {
            List<ContentInfo> list = data.getList();
            if(null != list && list.size() > 1) {
                topListBeans.addAll(list);
            }
        }
        refreshView();
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }
}
