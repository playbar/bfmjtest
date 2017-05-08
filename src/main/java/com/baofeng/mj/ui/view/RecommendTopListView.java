package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.RecommendApi;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushaochen on 2017/1/18.
 */

public class RecommendTopListView extends RelativeLayout{

    private Context mContext;
    private View rootView;
    private LinearLayout linearLayout;

    private List<ContentInfo> topListBeans = new ArrayList<ContentInfo>();//顶部列表数据

    private int[] imageIds = new int[]{R.drawable.classification_vr,
            R.drawable.classification_girl,
            R.drawable.classification_3d,
            R.drawable.classification_jingsong,
            R.drawable.classification_jixina,
            R.drawable.classification_2d};

    public RecommendTopListView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public RecommendTopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.recommend_top_view, null);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.circle_icon_linear);

        addView(rootView);
    }

    private void refreshView() {
        linearLayout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PixelsUtil.dip2px(150),PixelsUtil.dip2px(140));
        for(int x = 0; x < topListBeans.size(); x++) {
            LinearLayout recommend_top_view_item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.recommend_top_view_item, null);
            recommend_top_view_item.setLayoutParams(layoutParams);
            View rightView = recommend_top_view_item.findViewById(R.id.right_view);
            if(x != topListBeans.size() - 1) {
                rightView.setVisibility(View.VISIBLE);
            } else {
                rightView.setVisibility(View.GONE);
            }
            TextView textView = (TextView) recommend_top_view_item.findViewById(R.id.top_text);
            textView.setText("#"+topListBeans.get(x).getTitle());
            ImageView top_image = (ImageView) recommend_top_view_item.findViewById(R.id.top_image);
            GlideUtil.displayImage(mContext, new WeakReference<ImageView>(top_image),imageIds[x],R.drawable.recommend_content_default_bg);
            recommend_top_view_item.setTag(x);
            recommend_top_view_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ResTypeUtil.onClickToActivity(mContext, topListBeans.get((int)v.getTag()));
                }
            });
            linearLayout.addView(recommend_top_view_item);
        }
    }

    public void requestData(boolean mIsRefreshTop) {
        if(!mIsRefreshTop) {
            return;
        }
//        System.out.println("!!!!!!!!!!!!!-----------requestData");
        if(topListBeans.size() == 0) {
            new RecommendApi().getRecommendTopReqCache(new CacheCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
                @Override
                public void onCache(ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> result) {
//                    System.out.println("!!!!!!!!!!!!!-----------onCache");
                    if(null != result && null != result.getData()) {
                        MainSubContentListBean<ArrayList<ContentInfo>> data = result.getData();
                        if(null != data) {
                            ArrayList<ContentInfo> list = data.getList();
                            if(null != list && list.size() > 0) {
                                topListBeans.clear();
                                for(int x = 0; x < imageIds.length; x++) {
                                    topListBeans.add(list.get(x));
                                }
                                refreshView();
                            }
                        }
                    }
                }
            });
        }
        new RecommendApi().recommendTopReq(mContext, new ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> result) {
//                System.out.println("!!!!!!!!!!!-----------onSuccess");
                if (result != null && result.getStatus() == 0) {
                    MainSubContentListBean<ArrayList<ContentInfo>> data = result.getData();
                    if(null != data) {
                        ArrayList<ContentInfo> list = data.getList();
                        if(null != list && list.size() > 0) {
                            topListBeans.clear();
                            for(int x = 0; x < imageIds.length; x++) {
                                topListBeans.add(list.get(x));
                            }
                            refreshView();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
//                System.out.println("!!!!!!!!!!!-----------onFailure");
            }

        });
    }

}
