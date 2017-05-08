package com.baofeng.mj.ui.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.CirlceIconGridViewAdapter;

import java.util.List;

/**
 * 圆钮(圆小小) 布局 2行，一行5个，填充10条数据
 * Created by muyu on 2016/4/29.
 */
public class CircleIconView extends BaseView {
    private Fragment fragment;
    private View rootView;
    private GridView gridView;
    private CirlceIconGridViewAdapter adapter;

    public CircleIconView(Context context, boolean hasTag) {
        super(context, hasTag);
    }

    public CircleIconView(Context context, Fragment fragment, boolean hasTag) {
        super(context, hasTag);
        this.fragment = fragment;
    }

    public CircleIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(MainSubContentListBean<List<ContentInfo>> bean){
        this.data = bean;
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_circle_icon,null);
        this.addView(rootView);
        gridView = (GridView) rootView.findViewById(R.id.view_grid);
        if(fragment == null){
            adapter = new CirlceIconGridViewAdapter(mContext, bean.getList());
        }else{
            adapter = new CirlceIconGridViewAdapter(mContext,fragment, bean.getList());
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContentInfo item = data.getList().get(position);
        item.setLayout_type(data.getLayout_type());
        item.setParentResId(data.getRes_id());
        ReportBusiness.getInstance().putHeader(item, getReportBean());
        onClickToActivity(item);
    }
}
