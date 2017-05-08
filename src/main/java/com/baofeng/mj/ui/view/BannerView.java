package com.baofeng.mj.ui.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.ShowDetail;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by muyu on 2016/5/3.
 */
public class BannerView extends BaseView {
    private static final int DELAY = 5000; //5秒轮播
    private ViewPager viewPager;
    private LinearLayout indicators;
    private BannerPagerAdapter adapter;
    private int lastPointIndex = 0;
    private boolean isRunning = false;
    private LinearLayout ll_point_layout;
    private List<ContentInfo> contentInfos;
    private boolean hasShadow;
    private Handler handler;
    private Context mContext;
    private FrameLayout.LayoutParams params;
    private MainSubContentListBean<List<ContentInfo>> originData;
    private int width;
    private int height;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public BannerView(Context context, boolean hasTag, boolean hasShadow) {
        super(context, hasTag);
        this.hasShadow = hasShadow;
        this.mContext = context;
    }

    private void initLayoutParam(){
        width = PixelsUtil.getWidthPixels();
        height = (int) (width / 1.774f);
        params = new FrameLayout.LayoutParams(width, height);
    }

    @Override
    public void initView(MainSubContentListBean<List<ContentInfo>> data) {
        this.originData = data;
        contentInfos = data.getList();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner, this);
        ImageView topShadow = (ImageView) view.findViewById(R.id.shorty_two_shadow_top);
        if (hasShadow) {
            topShadow.setVisibility(View.VISIBLE);
        } else {
            topShadow.setVisibility(View.GONE);
        }
        initLayoutParam();
        setLayoutParams(params);
        ll_point_layout = (LinearLayout) findViewById(R.id.ll_point_layout);
        ll_point_layout.setGravity(Gravity.RIGHT);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        indicators = (LinearLayout) findViewById(R.id.ll_point_group);
        initViewPager();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // 执行滑动到下一个页面
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                if (isRunning) {
                    // 在发一个handler延时
                    handler.sendEmptyMessageDelayed(0, DELAY);
                }

            }
        };
        handler.sendEmptyMessageDelayed(0, DELAY);
        initPoint(contentInfos);
    }

    private void initViewPager() {
        adapter = new BannerPagerAdapter(mContext);
        viewPager.setAdapter(adapter);
        int size = contentInfos.size() <= 0 ? 1 : contentInfos.size();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2
                - (Integer.MAX_VALUE / 2 % size));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                position %= contentInfos.size();
                if (indicators.getChildAt(position) != null) {
                    indicators.getChildAt(position).setEnabled(true);
                }
                if (indicators.getChildAt(lastPointIndex) != null) {
                    indicators.getChildAt(lastPointIndex).setEnabled(false);
                }
                lastPointIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 0:
                        handler.removeMessages(0);
                        handler.sendEmptyMessageDelayed(0, DELAY);
                        break;
                }
            }
        });
    }

    public void setLLPointGravity(int gravity) {
        ll_point_layout.setGravity(gravity);
    }

    private void initPoint(List<ContentInfo> imageList) {
        indicators.removeAllViews();
        for (int i = 0; i < imageList.size(); i++) {
            // 添加指示点
            ImageView point = new ImageView(mContext);
            point.setBackgroundResource(R.drawable.banner_circle_indicator_bg);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置边左界
            params.leftMargin = 12;
            point.setLayoutParams(params);
            indicators.addView(point);
            if (i == lastPointIndex) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
        }
    }

    class BannerPagerAdapter extends PagerAdapter {

        private Context adapterContext;

        public BannerPagerAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public int getCount() {
            return contentInfos.size() > 1 ? Integer.MAX_VALUE : contentInfos
                    .size();
        }

        /**
         * 实例化指定位置的view
         */
        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_banner_item, null);
            TextView title = (TextView) itemView.findViewById(R.id.banner_title);
            WeakReference<ImageView> bannerBg = new WeakReference<ImageView>((ImageView) itemView.findViewById(R.id.banner_header_img));
            ImageView headwear = (ImageView) itemView.findViewById(R.id.banner_headware);
            bannerBg.get().setLayoutParams(params);
            int bannercount = position % contentInfos.size();
            final ContentInfo item = contentInfos.get(bannercount);
            title.setText(item.getTitle());
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(item.getPic_url(), bannerBg, ImageLoaderUtils.getInstance().getImgOptionsBanner());
            GlideUtil.displayImage(mContext, bannerBg, item.getPic_url(), R.drawable.img_default_banner, width, height);
            ShowDetail.showVideoLabel(item.getHeadwear(), headwear);

            if (item.getUrl() != null && !"".equals(item.getUrl())) {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setLayout_type(originData.getLayout_type());
                        item.setParentResId(originData.getRes_id());
                        ReportBusiness.getInstance().putHeader(item, getReportBean());
                        onClickToActivity(item);
                    }
                });
            }
            container.addView(itemView);
            return itemView;
        }

        /**
         * 判断view和object之间的关系
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
//        unbindDrawables(this);
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
