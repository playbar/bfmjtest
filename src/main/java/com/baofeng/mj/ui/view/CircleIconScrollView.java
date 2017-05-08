package com.baofeng.mj.ui.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;

import java.util.List;

/**
 * 圆钮(圆小小) 布局 1行横向滚动
 * Created by muyu on 2016/5/4.
 */
public class CircleIconScrollView extends BaseView {
    private Fragment fragment;
    private View rootView;
    private LinearLayout linearLayout;

    public CircleIconScrollView(Context context, boolean hasTag) {
        super(context, hasTag);
    }

    public CircleIconScrollView(Context context, Fragment fragment, boolean hasTag) {
        super(context, hasTag);
        this.fragment = fragment;
    }

    public CircleIconScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(MainSubContentListBean<List<ContentInfo>> data){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_circle_icon_scroll,null);
        this.addView(rootView);
        linearLayout = (LinearLayout) findViewById(R.id.circle_icon_linear);
        linearLayout.removeAllViews();

        int count = data.getList().size();
        CircleIconItem item;
        for(int i= 0;i<count;i++){
            ContentInfo contentInfo=data.getList().get(i);
            contentInfo.setParentResId(data.getRes_id());
            contentInfo.setLayout_type(data.getLayout_type());
            if(fragment == null){
                item = new CircleIconItem(mContext, contentInfo);
            }else{
                item = new CircleIconItem(mContext,fragment, contentInfo);
            }
            item.setReportBean(getReportBean());
            linearLayout.addView(item);
        }
    }
}
