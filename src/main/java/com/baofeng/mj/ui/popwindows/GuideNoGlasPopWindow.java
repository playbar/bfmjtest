package com.baofeng.mj.ui.popwindows;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.BaseActivity;
import com.baofeng.mj.ui.activity.ShopWebActivity;
import com.baofeng.mj.util.publicutil.GlassesUtils;

/** 引导页面没有眼镜PopWindow
 * Created by muyu on 2016/9/19.
 */
public class GuideNoGlasPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View contentView;
    private TextView goShopTV;
    private TextView skipTV;
    private TextView cancelTV;

    public GuideNoGlasPopWindow(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwindow_guide_no_glas, null);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);

        goShopTV = (TextView) contentView.findViewById(R.id.guide_noglas_shop_textview);
        goShopTV.setOnClickListener(this);
        skipTV = (TextView) contentView.findViewById(R.id.guide_noglas_skip_textview);
        skipTV.setOnClickListener(this);
        cancelTV = (TextView) contentView.findViewById(R.id.guide_noglas_cancel_textview);
        cancelTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.guide_noglas_shop_textview) {
            reportClick("tomall");
            mContext.startActivity(new Intent(mContext, ShopWebActivity.class));
        } else if(i == R.id.guide_noglas_skip_textview){
            GlassesUtils.setDefaultGlasses(false);
            mContext.startActivity(new Intent(mContext, MainActivityGroup.class));
            ((BaseActivity)mContext).finish();
            reportClick("toidle");
        } else if(i == R.id.guide_noglas_cancel_textview){
            dismiss();
            reportClick("exit");
        }
    }

    //click 报数
    private void reportClick(String buy_g_mode){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setPagetype("choose_glasses");
        bean.setBuy_g_mode(buy_g_mode);

        ReportBusiness.getInstance().reportClick(bean);
    }
}