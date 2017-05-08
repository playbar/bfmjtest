package com.baofeng.mj.ui.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import java.lang.ref.WeakReference;

/**
 * Created by muyu on 2016/5/4.
 */
public class
CircleIconItem extends FrameLayout {

    private Context mContext;
    private Fragment fragment;
    private View rootView;
    private TextView nameTV;
    private WeakReference<ImageView> iconIV;
    private RelativeLayout.LayoutParams params;
    private ReportFromBean reportBean;

    public CircleIconItem(Context context, ContentInfo data) {
        super(context);
        this.mContext = context;
        initView(data);
    }

    public CircleIconItem(Context context, Fragment fragment, ContentInfo data) {
        super(context);
        this.mContext = context;
        this.fragment = fragment;
        initView(data);
    }

    private void initLayoutParam(){
        int screenWidth = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(10);;
        int width = (int)(screenWidth / 4.5);
        params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
    }

    protected void initView(final ContentInfo data){
        initLayoutParam();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.circle_icon_scroll_item, null);
        this.addView(rootView);
        nameTV = (TextView) findViewById(R.id.iconitem_scroll_name_tv);
        iconIV = new WeakReference<ImageView>((ImageView) findViewById(R.id.iconitem_scroll_icon_iv));
        nameTV.setText(data.getTitle());
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(data.getPic_url(), iconIV, ImageLoaderUtils.getInstance().getImgOptionsClassification());
        if(fragment == null){
            GlideUtil.displayImage(mContext, iconIV, data.getPic_url(), R.drawable.img_default_icon_classification);
        }else{
            GlideUtil.displayImage(fragment, iconIV, data.getPic_url(), R.drawable.img_default_icon_classification);
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportBusiness.getInstance().putHeader(data, reportBean);
                ResTypeUtil.onClickToActivity(mContext, data);
            }
        });
    }

    public ReportFromBean getReportBean() {
        return reportBean;
    }

    public void setReportBean(ReportFromBean reportBean) {
        this.reportBean = reportBean;
    }
}
