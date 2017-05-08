package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.paybusiness.PayBusiness;
import com.baofeng.mj.business.paybusiness.PayConfigBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.publicbusiness.RequestResponseCode;
import com.baofeng.mj.business.publicbusiness.ResponseCodeUtil;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.dialog.PayTipDialog;
import com.baofeng.mj.ui.dialog.RequestFailureDialog;
import com.baofeng.mj.ui.dialog.UnLockDialog;
import com.baofeng.mj.ui.online.view.PanoramPlayerPreView;
import com.baofeng.mj.ui.popwindows.DefinitionPopWindow;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.MoreTextView;
import com.baofeng.mj.ui.view.PanoramaRecView;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.ChoicenessApi;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.handmark.pulltorefresh.library.ObservableScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshMyScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.ScrollViewListener;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 全景详情页面
 * Created by muyu on 2016/5/5.
 */
public class PanoramaDetailActivity extends BasePlayerActivity implements View.OnClickListener,ScrollViewListener, UnLockDialog.UnLockCallBack {
    private PullToRefreshMyScrollView contentLayout;
    private ObservableScrollView scrollView;
    private TextView nameTV;
    private TextView gradeTV;
    private TextView sizeTV;
    private TextView fromTV;
    private MoreTextView briefTV;
    private Button playBtn;
    private Button downLoadBtn;
    //    private String url;
    private LinearLayout topicView;
    private RelativeLayout fullScreenPlayer;//全屏播放
    private PanoramaVideoBean info;
    private LinearLayout sizeLayout;
    private LinearLayout freeLayout;
    private LinearLayout payLayout;
    private TextView payCountTV;
    private TextView resTypeTV;
    private RequestFailureDialog requestFailureDialog;//请求失败对话框
    private PayTipDialog payTipDialog;//购买提示对话框
    private int result;//高清测试结果
    private UnLockDialog unLockDialog;
    private PanoramaVideoAttrs mPanoramaVideoAttrs;

    private boolean hasCacheData;//true有缓存数据，false没有
    private int resultDate;//服务器返回结果的时间戳

    private PanoramPlayerPreView PlayerpreView;//播放预览View
    private PanoramaRecView recView;

    private ImageButton briefMoreBtn; //简介显示更多
    private MoreTextView moreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isfinish = false;
        BaseApplication.INSTANCE.setBaseActivity(this);
        subActivityName = "PanoramaDetailActivity";
        detailUrl = getIntent().getStringExtra("next_url");
        type = getIntent().getIntExtra("next_type", 0);
        subType = getIntent().getIntExtra("next_subType", 0);
        payTipDialog = new PayTipDialog(this);
        requestFailureDialog = new RequestFailureDialog(this);
        result = SettingSpBusiness.getInstance().getHigh();
        initView();
        //loadCacheData();//加载缓存数据
        initData();//请求网络数据
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_panorama_detail_content;
    }

    @Override
    protected void onDestroy() {
        if (!isfinish) {
            destroyView();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (PlayerpreView != null) {
            PlayerpreView.changeSurface();
        }
        super.finish();
        BaseApplication.INSTANCE.setBaseActivity(null);
    }

    boolean isfinish = false;

    public void destroyView() {
        if (isfinish)
            return;
        isfinish = true;
        if (PlayerpreView != null) {
            PlayerpreView.onPause();
        }
        if (PlayerpreView != null) {
            PlayerpreView.onDestroy();
        }
    }

    private void initView() {
        resTypeTV = (TextView) findViewById(R.id.panorama_detail_size_textview_pre);
        contentLayout = (PullToRefreshMyScrollView) findViewById(R.id.panorama_content_layout);
        scrollView = contentLayout.getRefreshableView();
        scrollView.setScrollViewListener(this);
        topicView = (LinearLayout) findViewById(R.id.panorama_detail_layout);
        fullScreenPlayer = (RelativeLayout) findViewById(R.id.fullscreen_player);

        nameTV = (TextView) findViewById(R.id.panorama_detail_name_textview);
        gradeTV = (TextView) findViewById(R.id.panorama_detail_grade_textview);
        sizeTV = (TextView) findViewById(R.id.panorama_detail_size_textview);
        fromTV = (TextView) findViewById(R.id.panorama_detail_from_textview);
        briefTV = (MoreTextView) findViewById(R.id.panorama_detail_brief_textview);
        playBtn = (Button) findViewById(R.id.panorama_detail_play_btn);
        playBtn.setOnClickListener(this);
        downLoadBtn = (Button) findViewById(R.id.panorama_detail_download_btn);
        downLoadBtn.setOnClickListener(this);

        sizeLayout = (LinearLayout) findViewById(R.id.panorama_detail_size_layout);//视频大小
        //是否付费判断
        freeLayout = (LinearLayout) findViewById(R.id.panorama_detail_play_layout);
        payLayout = (LinearLayout) findViewById(R.id.panorama_detail_pay_layout);
        payCountTV = (TextView) findViewById(R.id.panorama_detail_pay_count);

        payLayout.setOnClickListener(this);
        briefMoreBtn = (ImageButton) findViewById(R.id.panorama_brief_more);
        briefMoreBtn.setOnClickListener(this);
        moreTextView = (MoreTextView) findViewById(R.id.panorama_detail_brief_textview);
        moreTextView.bindListener(briefMoreBtn);

        if (type == ResTypeUtil.res_type_video) {//视频
            int width = PixelsUtil.getWidthPixels();
            int height = (int) (width / 10f * 9f);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            topicView.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            fullScreenPlayer.setLayoutParams(params);

            fullScreenPlayer.setVisibility(View.VISIBLE);
            PlayerpreView =  new PanoramPlayerPreView(this);
            super.mPlayerView = PlayerpreView;
            fullScreenPlayer.addView(PlayerpreView);
            PlayerpreView.setActivityViews(this, fullScreenPlayer, titleBackView);

            recView = (PanoramaRecView) findViewById(R.id.panorama_detail_rec);
            recView.setVisibility(View.VISIBLE);

        } else {
            int screenWidth = PixelsUtil.getWidthPixels();
            int width = screenWidth;
            int height = (int) (width / 1.777f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            topicView.setLayoutParams(layoutParams);
            topicView.removeAllViews();
            titleBackView.getNameTV().setVisibility(View.GONE);
            titleBackView.setVisibility(View.VISIBLE);

        }

    }

    public void reSetPos() {
        if (scrollView != null) {
            scrollView.scrollTo(0, 0);
        }
    }

    /**
     * 加载缓存数据
     */
    private void loadCacheData() {
        new ChoicenessApi().getPanoramaDetailInfo(detailUrl, new CacheCallBack<ResponseBaseBean<PanoramaVideoBean>>() {
            @Override
            public void onCache(ResponseBaseBean<PanoramaVideoBean> result) {
                super.onCache(result);
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
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

    private void initData() {
        new ChoicenessApi().getPanoramaDetailInfo(this, detailUrl, new ApiCallBack<ResponseBaseBean<PanoramaVideoBean>>() {
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
            public void onSuccess(ResponseBaseBean<PanoramaVideoBean> result) {
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
                            Toast.makeText(PanoramaDetailActivity.this, result.getStatus_msg(), Toast.LENGTH_SHORT).show();
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

    private void bindView(PanoramaVideoBean data) {
        info = data;
        type = data.getType();
        if(data.getLandscape_url() != null) {
            contents = data.getLandscape_url().getContents();
            nav = data.getLandscape_url().getNav();
        }else {
            contents = "";
            nav = "";
        }
        initHierarchy();
        nameTV.setText(data.getTitle());
        gradeTV.setText(String.valueOf(data.getScore()));
        fromTV.setText(data.getSource());
        briefTV.setText("简介:  " + data.getDesc());
        sizeTV.setText(data.getSize());

        if (type == ResTypeUtil.res_type_video) {//视频
            PlayerpreView.setData(data);
        } else {

            if (data.getScreenshot() != null) {
                PicBannerView picBannerView = new PicBannerView(this, data.getScreenshot(), false);
                picBannerView.setLLPointGravity(Gravity.CENTER);
                topicView.addView(picBannerView);
            }
        }

        if (data.getType() == 4) { //如果Type为4，不显示视频大小
            sizeLayout.setVisibility(View.GONE);
            recView.bindView(data.getRec_list());
            scrollView.smoothScrollBy(0, 0);
        }

        downloadBtnShowOrNot();//下载按钮是否显示
        showContentView();

        if (0 == info.getPayment_type()) {//免费
            payBtnNotShow();//隐藏支付按钮
        } else {
            if (UserSpBusiness.getInstance().isUserLogin()) {//已登录
                checkIfPayed();//检查是否购买过
            } else {//未登录
                payBtnShow();//显示支付按钮
            }
        }
    }


    /**
     * 检查是否购买过
     */
    private void checkIfPayed() {
        new PayBusiness().checkIfPayed(this, info.getRes_id(), String.valueOf(info.getType()), new ApiCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                dismissProgressDialog();
                if (TextUtils.isEmpty(result)) {
                    showRequestFailureDialog();//显示请求失败对话框
                } else {
                    try {
                        JSONObject joResult = new JSONObject(result);
                        if (0 == joResult.getInt("status")) {//已购买
                            payBtnNotShow();//隐藏支付按钮
                        } else if (1 == joResult.getInt("status")) {//未购买
                            payBtnShow();//显示支付按钮
                        } else {
                            showRequestFailureDialog();//显示请求失败对话框
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                dismissProgressDialog();
                showRequestFailureDialog();//显示请求失败对话框
            }
        });
    }

    /**
     * 显示请求失败对话框
     */
    private void showRequestFailureDialog() {
        requestFailureDialog.showDialog("服务器异常，请刷新重试！", new RequestFailureDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack(boolean againRequest) {
                if (againRequest) {
                    checkIfPayed();//再一次检查是否购买过
                } else {
                    finish();//结束当前界面
                }
            }
        });
    }

    /**
     * 隐藏支付按钮
     */
    private void payBtnNotShow() {
        freeLayout.setVisibility(View.VISIBLE);
        payLayout.setVisibility(View.GONE);
    }

    /**
     * 显示支付按钮
     */
    private void payBtnShow() {
        freeLayout.setVisibility(View.GONE);
        payLayout.setVisibility(View.VISIBLE);
        if (1 == info.getPayment_type()) {//魔豆
            payCountTV.setText("(" + info.getPayment_count() + "魔豆)");
        } else if (2 == info.getPayment_type()) {//魔币
            payCountTV.setText("(" + info.getPayment_count() + "魔币)");
        }
    }

    /**
     * 下载按钮是否显示
     */
    private void downloadBtnShowOrNot() {
        LogHelper.e("infos", "status==" + isLoading() + "==type==" + type);
        if (ResTypeUtil.res_type_video == type) { //全景视频
            resTypeTV.setText("全景视频");
            File file = DownloadResBusiness.getDownloadResFile(info);
            if (file != null && file.exists()) {//资源存在
                LogHelper.e("infos", "exists==" + file.getAbsolutePath());
                downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_downloaded_btn_bg));
                downLoadBtn.setText(getResources().getString(R.string.downloaded));
                downLoadBtn.setTextColor(getResources().getColor(R.color.prompt_color));
                downLoadBtn.setVisibility(View.VISIBLE);
                downLoadBtn.setClickable(false);
                playBtn.setVisibility(View.VISIBLE);//显示播放按钮
            } else {//资源不存在
                downLoadBtn.setClickable(true);
                downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_blue_bg));
                downLoadBtn.setTextColor(getResources().getColor(R.color.btn_normal_color));
                int operation_type = info.getOperation_type();
                if (1 == operation_type) {//只在线
                    downLoadBtn.setVisibility(View.GONE);//隐藏下载按钮
                    playBtn.setVisibility(View.VISIBLE);//显示播放按钮
                } else if (2 == operation_type) {//只下载
                    if (isLoading() == MjDownloadStatus.DOWNLOADING) {
                        setDownloadBtnProgress();
                    } else if (isLoading() == MjDownloadStatus.ABORT) {
                        downLoadBtn.setText(getResources().getString(R.string.pause));
                    } else {
                        downLoadBtn.setText(getResources().getString(R.string.download));
                    }
                    downLoadBtn.setVisibility(View.VISIBLE);//显示下载按钮
                    playBtn.setVisibility(View.GONE);//隐藏播放按钮
                } else {//两者都
                    if (isLoading() == MjDownloadStatus.DOWNLOADING) {
                        setDownloadBtnProgress();
                    } else if (isLoading() == MjDownloadStatus.ABORT) {
                        downLoadBtn.setText(getResources().getString(R.string.pause));
                    } else {
                        downLoadBtn.setText(getResources().getString(R.string.download));
                    }
                    downLoadBtn.setVisibility(View.VISIBLE);//显示下载按钮
                    playBtn.setVisibility(View.VISIBLE);//显示播放按钮
                }
            }
        } else if (ResTypeUtil.res_type_roaming == type) {//全景漫游
            resTypeTV.setText("全景漫游");
            File file = DownloadResBusiness.getDownloadResFile(info);
            if (file != null && file.exists() ) {//资源存在
                downLoadBtn.setVisibility(View.GONE);//隐藏下载按钮
                playBtn.setVisibility(View.VISIBLE);//显示播放按钮
            } else {//资源不存在
                if (isLoading() == MjDownloadStatus.DOWNLOADING) {
                    setDownloadBtnProgress();
                } else if (isLoading() == MjDownloadStatus.ABORT) {
                    downLoadBtn.setText(getResources().getString(R.string.pause));
                } else {
                    downLoadBtn.setText(getResources().getString(R.string.download));
                }
                downLoadBtn.setVisibility(View.VISIBLE);//显示下载按钮
                downLoadBtn.setClickable(true);
                downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_blue_bg));
                downLoadBtn.setTextColor(getResources().getColor(R.color.btn_normal_color));
                playBtn.setVisibility(View.GONE);//隐藏播放按钮
            }
        } else {//全景图片
            resTypeTV.setText("全景图片");
            downLoadBtn.setVisibility(View.GONE);//隐藏下载按钮
            playBtn.setVisibility(View.VISIBLE);//显示播放按钮
        }
    }

    long time = 0;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refreshView) {//刷新
            initData();
        } else if (i == R.id.panorama_detail_pay_layout) {//支付
            reportClick("pay");
            if (UserSpBusiness.getInstance().isUserLogin()) {//已登录
                showGotoPayDialog();//显示去支付对话框
            } else {//未登录
                startActivityForResult(new Intent(this, LoginActivity.class), RequestResponseCode.REQUEST_CODE_LOGIN);
            }
        } else if (i == R.id.panorama_detail_play_btn) {//播放
            reportClick("play");
            if (type != 4) {//视频
                doVRPlay();
            } else {
                if (PlayerpreView != null) {
                    PlayerpreView.showPlayerChooseDialog();
                }
            }
        } else if (i == R.id.panorama_detail_download_btn) {//下载
            reportClick("download");
            DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(info.getRes_id());// 获取下载中的DownloadItem
            if (downloadingItem != null) {// 如果是下载中
                if(System.currentTimeMillis()-time<1000){//1000毫秒点击间隔
                    return;
                }
                time = System.currentTimeMillis();
                if (MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()) {
                    DemoUtils.pauseDownload(BaseApplication.INSTANCE, downloadingItem);//暂停下载
                } else {
                    DemoUtils.startDownload(BaseApplication.INSTANCE, downloadingItem);//继续下载
                }
            } else if (UserSpBusiness.getInstance().notLoginForDownload()) {// 未登录时，超过下载限制
                DownLoadBusiness.showLoginForDownloadDialog(this);//提示登录再下载
            }
//            else if (needBuy(context, info)) {// 需要购买
//                toBuy(context, info);//开始购买
//            }
            else if (!NetworkUtil.networkEnable()) {//无网络
                DownLoadBusiness.showNetworkErrorDialog(this);
            } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络下载
                DownLoadBusiness.showOpenGprsDialog(this);// 提示WiFi不可用，是否开启gprs网络下载
            } else if (ResTypeUtil.res_type_video == info.getType()) {// 全景视频
                showSelectDefinitionDialog();//显示选择清晰度对话框
            } else {
                if(System.currentTimeMillis()-time<1000){//1000毫秒点击间隔
                    return;
                }
                time = System.currentTimeMillis();
                DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(info, detailUrl);
                DownLoadBusiness.downloadStart(downloadItem);//开始下载
            }
        } else if (i == R.id.panorama_brief_more) {
            moreTextView.clickMoreTextView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RequestResponseCode.RESPONSE_CODE_LOGIN == resultCode) {//登录返回
            checkIfPayed();//检查是否购买过
        }
    }

    /**
     * 显示去支付对话框
     */
    private void showGotoPayDialog() {
        String description = "购买该资源需要支付";
        if (1 == info.getPayment_type()) {//魔豆
            description = description + info.getPayment_count() + "魔豆";
        } else if (2 == info.getPayment_type()) {//魔币
            description = description + info.getPayment_count() + "魔币";
        }
        String leftText = "取消";
        String rightText = "确认购买";
        payTipDialog.showDialog(description, leftText, rightText, new PayTipDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {//确认购买
                gotoPay();//去购买
            }
        });
    }

    /**
     * 去购买
     */
    private void gotoPay() {
        showProgressDialog("正在购买...");
        String payUrl = PayConfigBusiness.createPayUrl(info);
        new PayBusiness().gotoPay(PanoramaDetailActivity.this, payUrl, new PayBusiness.PayCallBack() {
            @Override
            public void callBack(String code, String data) {
                dismissProgressDialog();
                if (ResponseCodeUtil.SUCCESS.equals(code)) {//支付成功
                    payBtnNotShow();//隐藏支付按钮
                } else {//支付失败
                    String description = ResponseCodeUtil.getFailureReason(code);//获取失败原因
                    if (ResponseCodeUtil.modouOrMobiNotEnough(code)) {//魔豆或者魔币不足
                        String leftText = "取消";
                        String rightText = "去充值";
                        payTipDialog.showDialog(description, leftText, rightText, new PayTipDialog.MyDialogInterface() {
                            @Override
                            public void dialogCallBack() {//去充值
                                Intent intent = new Intent(PanoramaDetailActivity.this, ChargeActivity.class);
                                if (info.getPayment_type() == 1) {//魔豆
                                    intent.putExtra("curTab", 1);
                                } else {//魔币
                                    intent.putExtra("curTab", 0);
                                }
                                startActivity(intent);
                            }
                        });
                    } else if (ResponseCodeUtil.ifHasPayed(code)) {//资源已订购
                        requestFailureDialog.showDialog(description, new RequestFailureDialog.MyDialogInterface() {
                            @Override
                            public void dialogCallBack(boolean againRequest) {
                                payBtnNotShow();//隐藏支付按钮
                            }
                        });
                    } else {//其他
                        requestFailureDialog.showDialog(description, null);
                    }
                }
            }
        });
    }

    @Override
    public void onConfirm() {
        reportClick("IDOK", "popup");
        startDownload(mPanoramaVideoAttrs, 1);
    }

    @Override
    public void onCancel() {

    }

    /**
     * 显示选择清晰度对话框
     */
    private void showSelectDefinitionDialog() {
        DefinitionPopWindow popWindow = new DefinitionPopWindow(PanoramaDetailActivity.this, info);
        popWindow.setOnItemClickCallback(new DefinitionPopWindow.OnItemClickCallback() {
            @Override
            public void onItemClick(PanoramaVideoAttrs panoramaVideoAttrs) {
                if (2 != result && "4k".equals(panoramaVideoAttrs.getDefinition_name())) {
                    mPanoramaVideoAttrs = panoramaVideoAttrs;
                    if (unLockDialog == null) {
                        unLockDialog = new UnLockDialog(PanoramaDetailActivity.this, PanoramaDetailActivity.this);
                    }
                    unLockDialog.show();
                    reportClick("unlocked", "dwd_claritychg");
                    return;
                }
                startDownload(panoramaVideoAttrs, 0);
            }
        });
        popWindow.showAtLocation(nameTV, Gravity.CENTER, 0, 0);
    }

    private void startDownload(PanoramaVideoAttrs panoramaVideoAttrs, int is4k) { //下载4k为1，其他为0
        String downloadUrl = panoramaVideoAttrs.getDownload_url();//某个清晰度的资源下载地址
        String size = panoramaVideoAttrs.getSize();//某个清晰度的资源大小
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(info, downloadUrl, size, is4k, detailUrl);
        DownLoadBusiness.downloadStart(downloadItem);
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem) {
        if (info == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (downloadItem.getAid().equals(info.getRes_id())) {
                    downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_downloaded_btn_bg));
                    downLoadBtn.setText(getResources().getString(R.string.downloaded));
                    downLoadBtn.setTextColor(getResources().getColor(R.color.prompt_color));
                    downLoadBtn.setVisibility(View.VISIBLE);//下载完成，显示为已下载,并且不可点击
                    downLoadBtn.setClickable(false);
                    playBtn.setVisibility(View.VISIBLE);//下载完成，显示播放按钮
                }
            }
        });
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize, List<DownloadItem> downloadItemList) {
        if (downloadingSize == 0 || info == null) {
            return;
        }

        final DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(info.getRes_id());
        if (downloadingItem != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int downloadState = downloadingItem.getDownloadState();
                    if (MjDownloadStatus.ABORT == downloadState) {//已暂停
                        downLoadBtn.setText("暂停中");
                    } else if (MjDownloadStatus.COMPLETE == downloadState) {

                    }else if(downloadState == MjDownloadStatus.ERROR){
                        downLoadBtn.setText(getResources().getString(R.string.download_continue));
                    }else {
                        int progress = DownLoadBusiness.getDownloadProgress(downloadingItem);
                        downLoadBtn.setText(progress + "%");
                    }
                }
            });
        }
    }


//    @Override
//    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
////        titleBgLayout.setVisibility(View.VISIBLE);
//        final float heigh = PixelsUtil.dip2px(200);
//        if (y < 5) {
//            titleBgLayout.setAlpha(0);
//        }
//        if (y > 0) {
//            float alpha = y / heigh;
//            titleBgLayout.setAlpha(alpha);
//        }
//    }

    /**
     * 视频的下载地址是否存在
     *
     * @return true存在，false不存在
     */
    private boolean videoDownloadUrlExist(PanoramaVideoBean panoramaVideoBean) {
        List<PanoramaVideoAttrs> video_attrs = panoramaVideoBean.getVideo_attrs();
        if (video_attrs != null && video_attrs.size() > 0) {
            for (PanoramaVideoAttrs panoramaVideoAttrs : video_attrs) {
                if (panoramaVideoAttrs != null && !TextUtils.isEmpty(panoramaVideoAttrs.getDownload_url())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 视频的播放地址是否存在
     *
     * @return true存在，false不存在
     */
    private boolean videoPlayUrlExist(PanoramaVideoBean panoramaVideoBean) {
        List<PanoramaVideoAttrs> video_attrs = panoramaVideoBean.getVideo_attrs();
        if (video_attrs != null && video_attrs.size() > 0) {
            for (PanoramaVideoAttrs panoramaVideoAttrs : video_attrs) {
                if (panoramaVideoAttrs != null && !TextUtils.isEmpty(panoramaVideoAttrs.getPlay_url())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void reportClick(String clickType, String pageType) {
        ReportClickBean bean = new ReportClickBean();
        bean.setClicktype(clickType);
        bean.setPagetype(pageType);
        reportClick(bean);
    }

    private void reportClick(String clickType) {
        ReportClickBean bean = new ReportClickBean();
        bean.setClicktype(clickType);
        bean.setPagetype(ReportBusiness.PAGE_TYPE_DETAIL);
        bean.setTitle(this.info.getTitle());
        bean.setVideoid(this.info.getRes_id());
        bean.setTypeid(String.valueOf(this.info.getType()));
        reportClick(bean);
    }

    private void reportClick(ReportClickBean bean) {
        bean.setEtype("click");
        bean.setTpos("1");
        ReportBusiness.getInstance().reportClick(bean);
    }

    private Handler mHandler;
    @Override
    protected void onResume() {
        super.onResume();
        if (PlayerpreView != null) {
            PlayerpreView.onResume();
        }
        if (null != info && type != 0) {
            downloadBtnShowOrNot();
        }

        if(mHandler==null){
            mHandler = new Handler();
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (PlayerpreView != null) {
            PlayerpreView.onPause();
        }
    }


    public void doVRPlay() {
        //跳转Unity播放
        File file = DownloadResBusiness.getDownloadResFile(info);
        if (StartActivityHelper.needPlayWithDownload(file, info.getType())) {//走已下载播放
            String resInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(info.getType(),info.getTitle(),info.getRes_id());
            String json = FileCommonUtil.readFileString(new File(resInfoPath));
            if(!TextUtils.isEmpty(json)) {
                StartActivityHelper.playPanoramaWithDownloaded(PanoramaDetailActivity.this, DownloadItemUtil.createDownloadItem(json));
            }else {
                //走在线播放
                StartActivityHelper.startPanoramaGoUnity(PanoramaDetailActivity.this, type, detailUrl, contents, nav, ReportBusiness.PAGE_TYPE_DETAIL, StartActivityHelper.online_resource_from_default);
            }
        } else {//走在线播放
            StartActivityHelper.startPanoramaGoUnity(PanoramaDetailActivity.this, type, detailUrl, contents, nav, ReportBusiness.PAGE_TYPE_DETAIL, StartActivityHelper.online_resource_from_default);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (PlayerpreView != null && PlayerpreView.currentMode == PanoramPlayerPreView.PlayerScreenMode.fullscreen) {
                PlayerpreView.changePlayerScreen(PanoramPlayerPreView.PlayerScreenMode.half_screen, false);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private int isLoading() {
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(info.getRes_id());// 获取下载中的DownloadItem
        if (downloadingItem != null) {
//            if(MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()){// 如果是下载中
            return downloadingItem.getDownloadState();
//            }
        }

        return 0;
    }

   /* private int isComplete() {
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(DownloadUtils.getInstance().getAllDownLoadsByState(this, MjDownloadStatus.COMPLETE, true));
        for (DownloadItem in : list) {
            if (in.getAid().equals(info.getRes_id())) {
                return in.getDownloadState();
            }
        }
        return 0;
    }*/
   @Override
   public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//        titleBgLayout.setVisibility(View.VISIBLE);
       final float heigh = PixelsUtil.dip2px(200);
       if (y < 5) {
           titleBgLayout.setAlpha(0);
       }
       if (y > 0) {
           float alpha = y / heigh;
           titleBgLayout.setAlpha(alpha);
       }
   }

    private void setDownloadBtnProgress() {
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(info.getRes_id());
        int progress = DownLoadBusiness.getDownloadProgress(downloadingItem);
        downLoadBtn.setText(progress + "%");
    }
}
