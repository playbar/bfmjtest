package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.LiveBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.handmark.pulltorefresh.library.ObservableScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshMyScrollView;
import com.handmark.pulltorefresh.library.ScrollViewListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 直播详情页面
 * Created by muyu on 2016/5/22.
 */
public class LiveDetailActivity extends BaseLoadingActivity implements View.OnClickListener, ScrollViewListener {
    private PullToRefreshMyScrollView contentLayout;
    private ObservableScrollView scrollView;
    private TextView nameTV;
    private TextView gradeTV;
    private TextView durationTV;
    private TextView beginTV;
    private TextView briefTV;
    private Button playBtn;
//    private String url;
    private LinearLayout topicView;
    private LiveBean info;
    private LinearLayout freeLayout;
    private LinearLayout payLayout;
    private TextView payCountTV;
    private TextView refreshView;
    private boolean hasCacheData;//true有缓存数据，false没有
    private int resultDate;//服务器返回结果的时间戳

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subActivityName = "LiveDetailActivity";
        detailUrl = getIntent().getStringExtra("next_url");
        type = getIntent().getIntExtra("next_type", 0);
        subType = getIntent().getIntExtra("next_subType",0);
        titleBackView.setPageType(ReportBusiness.PAGE_TYPE_DETAIL);
        initView();
        //loadCacheData();//加载缓存数据
        initData();//请求网络数据
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_live_detail_content;
    }

    private void initView(){
        //2017-04XX样式变更
        titleBackView.hideTopLine();
        titleBackView.setVRResWhite();
        //2017-04XX样式变更
        contentLayout = (PullToRefreshMyScrollView) findViewById(R.id.live_content_layout);
        scrollView = contentLayout.getRefreshableView();
        scrollView.setScrollViewListener(this);
        refreshView = (TextView) emptyView.getRefreshView();
        refreshView.setOnClickListener(this);
        topicView = (LinearLayout) findViewById(R.id.live_detail_layout);
        int screenWidth = PixelsUtil.getWidthPixels();
        int width = screenWidth;
        int height = (int) (width / 1.777f);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(width,height);
        topicView.setLayoutParams(layoutParams);
        nameTV = (TextView) findViewById(R.id.live_detail_name_textview);
        gradeTV = (TextView) findViewById(R.id.live_detail_grade_textview);
        durationTV = (TextView) findViewById(R.id.live_detail_duration_textview);
        beginTV = (TextView) findViewById(R.id.live_detail_begin_textview);
        briefTV = (TextView) findViewById(R.id.live_detail_brief_textview);
        playBtn = (Button) findViewById(R.id.live_detail_play_btn);
        playBtn.setOnClickListener(this);
        //是否付费判断
        freeLayout = (LinearLayout) findViewById(R.id.live_detail_play_layout);
        payLayout = (LinearLayout) findViewById(R.id.live_detail_pay_layout);
        payCountTV = (TextView) findViewById(R.id.live_detail_pay_count);

        payLayout.setOnClickListener(this);
    }

    /**
     * 加载缓存数据
     */
    private void loadCacheData() {
        new ChoicenessApi().getLiveDetailInfo(detailUrl, new CacheCallBack<ResponseBaseBean<LiveBean>>() {
            @Override
            public void onCache(ResponseBaseBean<LiveBean> result) {
                super.onCache(result);
                Log.i("CacheLogic", "onCache");
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
                            Log.i("CacheLogic", "onCache bindView");
                            hasCacheData = true;
                            resultDate = result.getDate();
                            bindView(result.getData());
                        }
                    }
                }
                initData();//请求网络数据
            }
        });
    }

    private void initData(){
        new ChoicenessApi().getLiveDetailInfo(this, detailUrl, new ApiCallBack<ResponseBaseBean<LiveBean>>() {
            @Override
            public void onStart() {
                super.onStart();
                if (!hasCacheData) {//没有缓存数据
                    showProgressDialog("正在加载...");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!hasCacheData) {//没有缓存数据
                    dismissProgressDialog();
                }
            }

            @Override
            public void onSuccess(ResponseBaseBean<LiveBean> result) {
                super.onSuccess(result);
                Log.i("CacheLogic", "onSuccess");
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
                            if (resultDate < result.getDate()) {
                                Log.i("CacheLogic", "onSuccess bindView");
                                bindView(result.getData());
                            }
                        }
                    } else {
                        if (!hasCacheData) {//没有缓存数据
                            Toast.makeText(LiveDetailActivity.this, result.getStatus_msg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!hasCacheData) {//没有缓存数据
                    hideContent();
                }
            }
        });
    }

    private void bindView(LiveBean data){
        info=data;
        type = data.getType();
        subType = data.getSubType();
        contents = data.getLandscape_url().getContents();
        nav = data.getLandscape_url().getNav();
        initHierarchy();
        this.info = data;
        topicView.removeAllViews();
        if(data.getThumb_pic_url() != null) {
            PicBannerView picBannerView = new PicBannerView(this, data.getThumb_pic_url(), false);
            picBannerView.setLLPointGravity(Gravity.CENTER);
            topicView.addView(picBannerView);
        }
        int dateTime = (int)(data.getLive_end_time() - data.getLive_start_time());  //7200秒
        durationTV.setText(dateTime/60 + "分钟");
        beginTV.setText(getTimeFormat(new Date(data.getLive_start_time() * 1000L)));
        nameTV.setText(data.getTitle());
        gradeTV.setText(data.getScore()+"分");
        briefTV.setText(data.getDesc());

        int paymentType = data.getIs_pay(); // 0 免费 1 付费
        if(paymentType == 0){
            freeLayout.setVisibility(View.VISIBLE);
            payLayout.setVisibility(View.GONE);
        }else {
            freeLayout.setVisibility(View.GONE);
            payLayout.setVisibility(View.VISIBLE);
            payCountTV.setText(data.getPayment_count()+"魔豆");
        }
        
//        playBtn.setText("已结束");
//        playBtn.setOnClickListener(null);
        if(currentTime().getTime() - data.getLive_start_time() * 1000 <= 0){
            playBtn.setText("等待直播开始");
            playBtn.setEnabled(false);
        } else if(currentTime().getTime() - data.getLive_end_time() * 1000 >= 0){
            playBtn.setText("直播已结束");
            playBtn.setEnabled(false);
            if(data.getReview_id() != 0){
                playBtn.setText("观看直播回放");
                playBtn.setEnabled(true);
            }
        }

        showContentView();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.live_detail_pay_layout){
            Toast.makeText(this,"付费去",Toast.LENGTH_SHORT).show();
        } else if(i == R.id.refreshView){
            initData();
        } else if(i == R.id.live_detail_play_btn){
            reportClick();
            StartActivityHelper.startLiveGoUnity(this, detailUrl, contents, nav, ReportBusiness.PAGE_TYPE_DETAIL);
        }
    }

    private Date currentTime(){
        return new Date();
    }

    private String getTimeFormat(Date date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        titleBgLayout.setVisibility(View.VISIBLE);
        final float heigh = PixelsUtil.dip2px(200);
        if (y < 5) {
            titleBgLayout.setAlpha(0);
        }
        if (y > 0) {
            float alpha = y / heigh;
            titleBgLayout.setAlpha(alpha);
        }
    }

    private void reportClick(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("detail");
        bean.setTitle(info.getTitle());
        bean.setVideoid(String.valueOf(info.getRes_id()));
        bean.setTypeid(String.valueOf(info.getType()));
        ReportBusiness.getInstance().reportClick(bean);
    }
}
