package com.baofeng.mj.ui.popwindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.SelectDetailBean;
import com.baofeng.mj.bean.SelectListBean;
import com.baofeng.mj.ui.adapter.AppListAdapter;
import com.baofeng.mj.ui.view.HomeSelectItemView;

/**
 * 首页2D 电影电视剧筛选Pop
 * Created by muyu on 2017/3/9.
 */
public class TDCategoryPopWindow extends PopupWindow {
    private Context mContext;
    private View contentView;
    private SelectListBean<SelectListBean<SelectDetailBean>> mHomeSubTab2dBeans;
    private LinearLayout categoryLayout;
    private AppListAdapter adapter;

    public TDCategoryPopWindow(Context context, SelectListBean<SelectListBean<SelectDetailBean>> homeSubTab2dBeans, AppListAdapter appListAdapter) {
        super(context);
        this.mContext = context;
        this.mHomeSubTab2dBeans = homeSubTab2dBeans;
        this.adapter = appListAdapter;
        initView();
    }

    private int resId;
    private String keyname;

    private int typeHeight;

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwindow_2d_category, null);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        categoryLayout = (LinearLayout) contentView.findViewById(R.id.td_category_layout);

        for (int i = 0; i < mHomeSubTab2dBeans.getList().size(); i++) {
            for (int j = 0; j < mHomeSubTab2dBeans.getList().size(); j++) {
                mHomeSubTab2dBeans.getList().get(j).setLayout_type("select_type");
                HomeSelectItemView selectItemView = new HomeSelectItemView(mContext, mHomeSubTab2dBeans.getList().get(j), adapter.getSelectChange());
                categoryLayout.addView(selectItemView, j);
            }
            categoryLayout.measure(0, 0);
            typeHeight = categoryLayout.getMeasuredHeight();
            break;
        }

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
