package com.baofeng.mj.ui.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hanyang on 2016/5/22.
 * 用于实现普通图片banner
 */
public class PicBannerView extends FrameLayout {
    private static final int DELAY = 5000; //5秒轮播
    private ViewPager viewPager;
    private LinearLayout indicators;
    private BannerPagerAdapter adapter;
    private int lastPointIndex = 0;
    private boolean isRunning = false;
    private LinearLayout ll_point_layout;
    private boolean isGame;
    private Context mContext;
    private Fragment fragment;
    private List<String> picUrls;
    private Handler handler;
    private FrameLayout.LayoutParams params;

    public PicBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicBannerView(Context context) {
        super(context);
    }

    public PicBannerView(Context context, List<String> picUrls, boolean isGame) {
        super(context);
        this.mContext = context;
        this.isGame = isGame;
        this.picUrls = picUrls;
        initView();
    }

    public PicBannerView(Context context, Fragment fragment, List<String> picUrls, boolean isGame) {
        super(context);
        this.mContext = context;
        this.fragment = fragment;
        this.isGame = isGame;
        this.picUrls = picUrls;
        initView();
    }

    private void initLayoutParam(){
        int screenWidth = PixelsUtil.getWidthPixels();
        int width;
        if (isGame) {
            width = screenWidth - PixelsUtil.dip2px(20);
        } else {
            width = screenWidth;
        }
        int height = (int) (width / 1.774f);
        params = new FrameLayout.LayoutParams(width, height);
    }

    public void initView() {
        initLayoutParam();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner, null);
        ImageView topShadow = (ImageView) view.findViewById(R.id.shorty_two_shadow_top);
        view.setLayoutParams(params);
        topShadow.setVisibility(View.VISIBLE);

        this.addView(view);
        ll_point_layout = (LinearLayout) view.findViewById(R.id.ll_point_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        indicators = (LinearLayout) view.findViewById(R.id.ll_point_group);
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
        initPoint(picUrls);
    }

    private void initViewPager() {
        adapter = new BannerPagerAdapter(mContext);
        viewPager.setAdapter(adapter);
        int size = picUrls.size() <= 0 ? 1 : picUrls.size();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2
                - (Integer.MAX_VALUE / 2 % size));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                position %= picUrls.size();
                indicators.getChildAt(position).setEnabled(true);
                indicators.getChildAt(lastPointIndex).setEnabled(false);
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

    private void initPoint(List<String> imageList) {
        indicators.removeAllViews();
        if (imageList.size() == 1) {
            return;
        }
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
            if (picUrls == null) {
                return 0;
            }
            return picUrls.size() > 1 ? Integer.MAX_VALUE : picUrls
                    .size();
        }



        /**
         * 实例化指定位置的view
         */
        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner_item, null);
            TextView title = (TextView) view.findViewById(R.id.banner_title);
            title.setVisibility(GONE);
            WeakReference<ImageView> bannerBg = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.banner_header_img));
            int bannercount = position % picUrls.size();
            bannerBg.get().setLayoutParams(params);
            view.setLayoutParams(params);
            container.addView(view);
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(picUrls.get(bannercount), bannerBg, ImageLoaderUtils.getInstance().getImgOptionsBanner());
            if(fragment == null){
                GlideUtil.displayImage(mContext, bannerBg, picUrls.get(bannercount), R.drawable.img_default_banner, params.width, params.height);
            }else{
                GlideUtil.displayImage(fragment, bannerBg, picUrls.get(bannercount), R.drawable.img_default_banner, params.width, params.height);
            }
            return view;
        }

        @Override
        /**
         * 判断view和object之间的关系
         */
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
