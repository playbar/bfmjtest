package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ReportFromBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 横向滑动应用列表栏目
 * Created by yushaochen on 2017/3/2.
 */

public class HorizontalAppListView extends RelativeLayout{

    private Context mContext;
    private View rootView;
    private LinearLayout linearLayout;

    private List<ContentInfo> topListBeans = new ArrayList<ContentInfo>();//专题栏目

    private ReportFromBean reportBean;

    public HorizontalAppListView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public HorizontalAppListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private LinearLayout.LayoutParams itemImageLayoutParams;
    int px10;
    private void initView() {
        //按照16:9，依据当前手机的屏幕宽，设定高（产品要求出现1.5个item）
        int screenWidth = PixelsUtil.getWidthPixels()-PixelsUtil.dip2px(20);
        int imgWidth = PixelsUtil.dip2px(66.7f + 12f);
          px10 = (int)((screenWidth - imgWidth * 3.75) / 3);
//        int imgHeight =imgWidth;
//        itemImageLayoutParams = new LinearLayout.LayoutParams(imgWidth, imgHeight);
        rootView = LayoutInflater.from(mContext).inflate(R.layout.topic_list_view, null);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.circle_icon_linear);
        addView(rootView);
    }

    private void refreshView() {
        if(topListBeans==null||topListBeans.size()<=3){
            rootView.setVisibility(GONE);
            return;
        }

        rootView.setVisibility(VISIBLE);
        for(int x = 0; x < topListBeans.size(); x++) {
            LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.rect_land_item, null);
            ImageView image = (ImageView) item.findViewById(R.id.rect_four_image);
            TextView nameTv = (TextView) item.findViewById(R.id.rect_four_name);
            Button btn = (Button) item.findViewById(R.id.rect_four_btn);
            ImageView stickGameIV = (ImageView) item.findViewById(R.id.rect_four_stickgame);
//            image.setLayoutParams(itemImageLayoutParams);
            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if(x != topListBeans.size() - 1) {
                itemLayoutParams.setMargins(0,0,px10,0);
                item.setLayoutParams(itemLayoutParams);
            }else {
                itemLayoutParams.setMargins(0,0,0,0);
                item.setLayoutParams(itemLayoutParams);
            }
            GlideUtil.displayImage(mContext, new WeakReference<ImageView>(image),topListBeans.get(x).getPic_url(), R.drawable.img_default_4n);
            nameTv.setText(topListBeans.get(x).getTitle());
            if (downLoadBusiness != null) {
                AppExtraBean appExtra = topListBeans.get(x).getApp_extra();
                if (appExtra != null) {
                    btn.setTag(topListBeans.get(x));
                    //报数
                    ReportBusiness.getInstance().put(String.valueOf(topListBeans.get(x).getRes_id()),reportBean);
                    downLoadBusiness.addDownloadButton(btn, topListBeans.get(x), appExtra);
                    if(appExtra.getPlay_mode() != null){
                        List<String> playmodeList = appExtra.getPlay_mode();
                        for(String str: playmodeList){
                            if(str.equals("6")){ //体感游戏
                                stickGameIV.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
            item.setTag(x);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBusiness.getInstance().putHeader(topListBeans.get((int) v.getTag()), reportBean);
                    ResTypeUtil.onClickToActivity(mContext, topListBeans.get((int) v.getTag()));
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
    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    public void setReportBean(ReportFromBean reportBean,   DownLoadBusiness<ContentInfo> downLoadBusiness) {
        this.reportBean = reportBean;
        this.downLoadBusiness = downLoadBusiness;
    }
}
