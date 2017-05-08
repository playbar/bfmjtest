package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.CommentBean;
import com.baofeng.mj.bean.CommentListBean;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.PushTypeBusiness;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.CommentListItem;
import com.baofeng.mj.ui.view.PicBannerView;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.NumFormatUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.util.viewutil.ShowUi;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.mojing.dl.domain.DownloadItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 游戏详情页
 */
public class GameDetailActivity extends BaseLoadingActivity implements View.OnClickListener {
    private LinearLayout game_support_layout;
    private LinearLayout bannerLayout, comment_list_layout, play_feature_layout, comment_more;
    private Button write_comment, game_detail_btn;
    private TextView game_detail_name, game_detail_score, game_detail_download_no,
            game_detail_size, game_play_feture, game_detail_des, des_hide, comment_no, game_detail_head_control,
            game_detail_source, game_detail_type, comment_tag, more_tag, game_support_des;
    private ImageView game_start_play;
    private WeakReference<ImageView> game_detail_image, game_detail_act_img;
    private int defMaxLines = 3;
    private String title;
    private View feature_line, banner_line;
    private String app_id;
    private int pageNum = 1;
    private boolean isFirst;//是否第一次点击
    private GameDetailBean gameInfo;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private LinearLayout des_hide_layout;
    private View game_support_line;

    private boolean hasCacheData;//true有缓存数据，false没有
    private int resultDate;//服务器返回结果的时间戳
    private String detailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        detailUrl = getIntent().getStringExtra("next_url");
        String operateJson = getIntent().getStringExtra(PushTypeBusiness.OPERATE_JSON);
        if (TextUtils.isEmpty(detailUrl)) {
            return;
        }
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                game_detail_btn.setText("打开");
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.setBaseActivity(this);
        //loadCacheData();//加载缓存数据
        loadData();//请求网络数据
        ResTypeUtil.processOperateJson(this, operateJson);//处理operateJson
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_game_detail_content;
    }

    @Override
    protected void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.setBaseActivity(null);
        super.onDestroy();
    }

    /**
     * 初始化view
     */
    private void initView() {
        bannerLayout = (LinearLayout) findViewById(R.id.game_detail_banner_layout);
        game_detail_name = (TextView) findViewById(R.id.game_detail_name);
        game_detail_score = (TextView) findViewById(R.id.game_detail_score);
        game_detail_download_no = (TextView) findViewById(R.id.game_detail_download_no);
        game_detail_size = (TextView) findViewById(R.id.game_detail_size);
        game_detail_image = new WeakReference<ImageView>((ImageView) findViewById(R.id.game_detail_image));
        game_play_feture = (TextView) findViewById(R.id.game_play_feture);
        game_detail_des = (TextView) findViewById(R.id.game_detail_des);
        comment_no = (TextView) findViewById(R.id.comment_no);
        game_detail_head_control = (TextView) findViewById(R.id.game_detail_head_control);
        game_detail_type = (TextView) findViewById(R.id.game_detail_type);
        game_detail_source = (TextView) findViewById(R.id.game_detail_source);
        game_start_play = (ImageView) findViewById(R.id.game_start_play);
        game_start_play.setOnClickListener(this);
        title = getIntent().getStringExtra("title");
        setTitle(title);
        titleBgLayout.setVisibility(View.VISIBLE);
        des_hide = (TextView) findViewById(R.id.des_hide);
        game_detail_btn = (Button) findViewById(R.id.game_detail_btn);
        game_detail_btn.setOnClickListener(this);
        feature_line = (View) findViewById(R.id.feature_line);
        write_comment = (Button) findViewById(R.id.write_comment);
        write_comment.setText(LanguageValue.getInstance().getValue(this, "SID_WRITE_COMMENT"));
        write_comment.setOnClickListener(this);
        comment_list_layout = (LinearLayout) findViewById(R.id.comment_list_layout);
        play_feature_layout = (LinearLayout) findViewById(R.id.play_feature_layout);
        banner_line = (View) findViewById(R.id.banner_line);
        comment_more = (LinearLayout) findViewById(R.id.comment_more);
        comment_more.setOnClickListener(this);
        des_hide_layout = (LinearLayout) findViewById(R.id.des_hide_layout);
        des_hide_layout.setOnClickListener(this);
        game_support_des = (TextView) findViewById(R.id.game_support_des);
        comment_tag = (TextView) findViewById(R.id.comment_tag);
        comment_tag.setText(LanguageValue.getInstance().getValue(this, "SID_COMMENT"));
        more_tag = (TextView) findViewById(R.id.more_tag);
        more_tag.setText(LanguageValue.getInstance().getValue(this, "SID_CHECK_MORE"));
        game_support_layout = (LinearLayout) findViewById(R.id.game_support_layout);
        game_support_line = (View) findViewById(R.id.game_support_line);
        game_detail_act_img = new WeakReference<ImageView>((ImageView) findViewById(R.id.game_detail_act_img));
        game_detail_act_img.get().setOnClickListener(this);
    }

    /**
     * 加载缓存数据
     */
    private void loadCacheData() {
        new GameApi().getGameDetailInfo(ConfigUrl.getGameDetailUrl(this, detailUrl), new CacheCallBack<ResponseBaseBean<GameDetailBean>>() {
            @Override
            public void onCache(ResponseBaseBean<GameDetailBean> result) {
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
                loadData();//请求网络数据
            }
        });
    }

    /**
     * 加载游戏详情页数据
     */
    private void loadData() {
        new GameApi().getGameDetailInfo(this, ConfigUrl.getGameDetailUrl(this, detailUrl), new ApiCallBack<ResponseBaseBean<GameDetailBean>>() {
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
            public void onSuccess(ResponseBaseBean<GameDetailBean> result) {
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
                            Toast.makeText(GameDetailActivity.this, result.getStatus_msg(), Toast.LENGTH_SHORT).show();
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

    /**
     * 绑定数据
     *
     * @param gameInfo
     */
    private void bindView(GameDetailBean gameInfo) {
        this.gameInfo = gameInfo;
        ShowUi.setDownloadButtonText(game_detail_btn, gameInfo);
        int screenWidth = PixelsUtil.getWidthPixels();
        int width = screenWidth - PixelsUtil.dip2px(20);
        int height = (int) ((width / 2.000f) + PixelsUtil.dip2px(10));
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(PixelsUtil.px2dip(10), PixelsUtil.px2dip(10), PixelsUtil.px2dip(10), 0);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        game_detail_act_img.get().setLayoutParams(layoutParams);
        if (TextUtils.isEmpty(gameInfo.getActivity_pic()) || TextUtils.isEmpty(gameInfo.getActivity_url())) {
            game_detail_act_img.get().setVisibility(View.GONE);
        } else {
            game_detail_act_img.get().setVisibility(View.VISIBLE);
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(gameInfo.getActivity_pic(), game_detail_act_img, ImageLoaderUtils.getInstance().getImgOptionsBanner());
            GlideUtil.displayImage(this, game_detail_act_img, gameInfo.getActivity_pic(), R.drawable.img_default_banner, width, height);
        }
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(gameInfo.getIcon_url(), game_detail_image, ImageLoaderUtils.getInstance().getImgOptionsFour());
        GlideUtil.displayImage(this, game_detail_image, gameInfo.getIcon_url(), R.drawable.img_default_4n);
        game_detail_name.setText(gameInfo.getTitle());
        setTitle(gameInfo.getTitle());//U3D返回页面为空
        game_detail_score.setText(gameInfo.getScore() + "分");
        game_detail_download_no.setText(NumFormatUtil.formatCount(gameInfo.getDownload_count()) + "次");
        game_detail_size.setText(gameInfo.getSize());
        if (gameInfo.getPlay_mode().size() > 0) {
            String playMode = gameInfo.getPlay_mode().get(0);
            if (TextUtils.isEmpty(playMode)) {
                game_detail_head_control.setVisibility(View.GONE);
            } else {
                int mode = Integer.parseInt(playMode);
                if (mode == 0) {
                    game_detail_head_control.setVisibility(View.VISIBLE);
                    game_detail_head_control.setText("单手柄");
                } else if (mode == 1) {
                    game_detail_head_control.setVisibility(View.VISIBLE);
                    game_detail_head_control.setText("双手柄");
                } else if (mode == 3) {
                    game_detail_head_control.setVisibility(View.VISIBLE);
                    game_detail_head_control.setText("头控");
                } else if (mode == 4) {
                    game_detail_head_control.setVisibility(View.VISIBLE);
                    game_detail_head_control.setText("触控");
                } else if (mode == 5) {
                    game_detail_head_control.setVisibility(View.VISIBLE);
                    game_detail_head_control.setText("手势");
                } else {
                    game_detail_head_control.setVisibility(View.GONE);
                }
            }
        }
        if (TextUtils.isEmpty(gameInfo.getSource())) {
            game_detail_source.setText("未知");
        } else {
            game_detail_source.setText(gameInfo.getSource());
        }
        game_detail_type.setText(gameInfo.getTypename());
        if (TextUtils.isEmpty(gameInfo.getBrief())) {
            game_support_line.setVisibility(View.GONE);
            game_support_layout.setVisibility(View.GONE);
        } else {
            game_support_des.setText(gameInfo.getBrief());
            game_support_layout.setVisibility(View.VISIBLE);
            game_support_line.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(gameInfo.getPlay_feature())) {
            play_feature_layout.setVisibility(View.GONE);
            feature_line.setVisibility(View.GONE);
        } else {
            play_feature_layout.setVisibility(View.VISIBLE);
            feature_line.setVisibility(View.VISIBLE);
        }
        game_play_feture.setText(Html.fromHtml(gameInfo.getPlay_feature()));
        game_detail_des.setText(Html.fromHtml(gameInfo.getDesc()));
        game_detail_des.setHeight(game_detail_des.getLineHeight() * defMaxLines);
        game_detail_des.post(new Runnable() {
            @Override
            public void run() {
                des_hide.setVisibility(game_detail_des.getLineCount() > defMaxLines ? View.VISIBLE : View.GONE);
            }
        });
        des_hide_layout.setOnClickListener(new DesLisener());
        if (gameInfo.getBigimages().size() != 0) {
            bannerLayout.setVisibility(View.VISIBLE);
            banner_line.setVisibility(View.VISIBLE);
            PicBannerView picBannerView = new PicBannerView(this, gameInfo.getBigimages(), true);
            picBannerView.setLLPointGravity(Gravity.CENTER);
            bannerLayout.addView(picBannerView);
        } else {
            banner_line.setVisibility(View.GONE);
            bannerLayout.setVisibility(View.GONE);
        }
        app_id = gameInfo.getApp_id();
        showContentView();
        loadCommentData(gameInfo.getApp_id(), pageNum, false);
    }

    /**
     * 加载评论数据
     *
     * @param resId 资源id 1：页码
     *              4：加载数据条数
     *              "100"：游戏类型 100
     */
    private void loadCommentData(String resId, int pageNo, final boolean isMore) {
        String uid = UserSpBusiness.getInstance().getUid();
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        new GameApi().getGameCommentList(this, uid, pageNo, 20, resId, 100, new ApiCallBack<ResponseBaseBean<CommentListBean>>() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onSuccess(ResponseBaseBean<CommentListBean> result) {
                super.onSuccess(result);
                if (result == null || result.getStatus() != 1) {
                    Toast.makeText(GameDetailActivity.this, "无更多评论内容", Toast.LENGTH_SHORT).show();
                    comment_more.setVisibility(View.GONE);
                    return;
                } else {
                    bindComment(result.getData(), isMore);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.write_comment == id) {
            Intent intent = new Intent(this, CommentDetailActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("id", app_id);
            startActivity(intent);
        } else if (id == R.id.refreshView) {
            loadData();
        } else if (R.id.comment_more == id) {
            pageNum++;
            loadCommentData(app_id, pageNum, true);
        } else if (id == R.id.game_detail_btn) {
            DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(gameInfo, detailUrl);
            File file = DownloadResBusiness.getDownloadResFile(gameInfo);//下载的资源文件
            String packageName = gameInfo.getPackage_name();//游戏包名
            int versionCode = Integer.valueOf(gameInfo.getVersioncode());//游戏版本号
            int apkState = ApkUtil.checkApk(file, packageName, versionCode);
            if(apkState == ApkUtil.NEED_INSTALL && !getUninatllApkInfo(file.getAbsolutePath())){
                apkState = ApkUtil.NEED_DOWNLOAD;
            }
            //报数
            reportClick(apkState);
            switch (apkState) {//apk状态
                case ApkUtil.NEED_DOWNLOAD://下载apk
                    downloadHandler(false);
                    break;
                case ApkUtil.NEED_UPDATE://升级apk
                    downloadHandler(true);
                    break;
                case ApkUtil.NEED_INSTALL://安装apk
                    ApkUtil.installApk(this, file.getAbsolutePath());
                    break;
                case ApkUtil.CAN_PLAY://打开apk
                    ApkUtil.startPlayApk(this, packageName);
                    break;
                case ApkUtil.NEED_UNZIP://解压zip
//                    DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(gameInfo, detailUrl);
                    downloadItem.setAppFromType(ConstantKey.OBB);
                    UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                        @Override
                        public void notify(DownloadItem downloadItem, int unZipResult) {
                            if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                                File file = DownloadResBusiness.getDownloadResFile(downloadItem);//下载的资源文件
                                ApkUtil.installApk(GameDetailActivity.this, file.getAbsolutePath());//安装apk
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        } else if (R.id.game_start_play == id) {

        } else if (R.id.game_detail_act_img == id) {
            if (TextUtils.isEmpty(gameInfo.toString()) || TextUtils.isEmpty(gameInfo.getActivity_url())) {
                return;
            } else {
                Intent intent = new Intent(this, H5Activity.class);
                intent.putExtra("next_url", gameInfo.getActivity_url());
                intent.putExtra("next_title", gameInfo.getTitle());
                startActivity(intent);
            }
        }
    }

    /**
     * 下载处理
     */
    public void downloadHandler(boolean isUpate) {
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(gameInfo.getRes_id());// 获取下载中的DownloadItem
        if (downloadingItem != null) {// 如果是下载中
            if (MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()) {
                DemoUtils.pauseDownload(BaseApplication.INSTANCE, downloadingItem);//暂停下载
            } else {

                if(isUpate){
                    if(MjDownloadStatus.ABORT == downloadingItem.getDownloadState()){
                        DemoUtils.startDownload(BaseApplication.INSTANCE, downloadingItem);//继续下载
                    }else{
                        DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE,downloadingItem);
                    }
                }else {
                    DemoUtils.startDownload(BaseApplication.INSTANCE, downloadingItem);//继续下载
                }
            }
        } else if (UserSpBusiness.getInstance().notLoginForDownload()) {// 未登录时，超过下载限制
            DownLoadBusiness.showLoginForDownloadDialog(this);//提示登录再下载
        } else if (!NetworkUtil.networkEnable()) {//无网络
            DownLoadBusiness.showNetworkErrorDialog(this);
        } else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络下载
            DownLoadBusiness.showOpenGprsDialog(this);// 提示WiFi不可用，是否开启gprs网络下载
        } else {
            DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(gameInfo, detailUrl);
            DownLoadBusiness.downloadStart(downloadItem);//开始下载
        }
    }

    /**
     * 描述展开与收缩动画
     */
    private class DesLisener implements View.OnClickListener {

        boolean isExpand;  //是否翻转

        @Override
        public void onClick(View v) {
            isExpand = !isExpand;
            game_detail_des.clearAnimation();  //清除动画
            final int tempHight;
            final int startHight = game_detail_des.getHeight();  //起始高度
            int durationMillis = 200;

            if (isExpand) {
                /**
                 * 折叠效果，从长文折叠成短文
                 */
                des_hide.setBackgroundResource(R.drawable.public_arrow_down);
                tempHight = game_detail_des.getLineHeight() * game_detail_des.getLineCount() - startHight;  //为正值，长文减去短文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                des_hide.startAnimation(animation);
            } else {
                /**
                 * 展开效果，从短文展开成长文
                 */
                des_hide.setBackgroundResource(R.drawable.public_arrow_down);
                tempHight = game_detail_des.getLineHeight() * defMaxLines - startHight;//为负值，即短文减去长文的高度差
                //翻转icon的180度旋转动画
                RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(durationMillis);
                animation.setFillAfter(true);
                des_hide.startAnimation(animation);
            }
            Animation animation = new Animation() {
                //interpolatedTime 为当前动画帧对应的相对时间，值总在0-1之间
                protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
                    game_detail_des.setHeight((int) (startHight + tempHight * interpolatedTime));//原始长度+高度差*（从0到1的渐变）即表现为动画效果
                }
            };
            animation.setDuration(durationMillis);
            game_detail_des.startAnimation(animation);
        }
    }

    /**
     * 绑定评论数据
     *
     * @param listBean
     */
    private void bindComment(final CommentListBean listBean, boolean more) {
        comment_no.setText("(" + listBean.getPeople_count() + ")");
        if (!more) {
            comment_list_layout.removeAllViews();
        }
        int commentNo = listBean.getData_list().size();
        List<CommentBean> comments = listBean.getData_list();
        if (listBean != null && comments != null) {
            if ((Integer.parseInt(listBean.getPeople_count()) - (pageNum * 20)) > 0) {
                comment_more.setVisibility(View.VISIBLE);
                CommentListItem commentListItem = new CommentListItem(this, listBean.getData_list());
                comment_list_layout.addView(commentListItem);
            } else {
                CommentListItem commentListItem = new CommentListItem(this, listBean.getData_list());
                comment_list_layout.addView(commentListItem);
                comment_more.setVisibility(View.GONE);
            }
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        comment_list_layout.setLayoutParams(layoutParams);
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem) {
        if (gameInfo == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameInfo.getPackage_name().equals(downloadItem.getPackageName())) {
                    game_detail_btn.setText("安装");
                }
            }
        });
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize,List<DownloadItem> downloadItemList) {
        if (downloadingSize == 0 || gameInfo == null) {
            return;
        }
        final DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItemForGame(gameInfo.getPackage_name());
        if (downloadingItem != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int downloadState = downloadingItem.getDownloadState();
                    if (MjDownloadStatus.ABORT  == downloadState) {//已暂停
                        game_detail_btn.setText("暂停中");
                    }else if(downloadState == MjDownloadStatus.ERROR){
                        game_detail_btn.setText(getResources().getString(R.string.download_continue));
                    } else {
                        int progress = DownLoadBusiness.getDownloadProgress(downloadingItem);
                        game_detail_btn.setText(progress + "%");
                    }
                }
            });
        }
    }

    private void reportClick(int type) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(ReportBusiness.getInstance().getClickType(type));
        bean.setTpos("1");
        bean.setPagetype("detail");
        bean.setTitle(gameInfo.getTitle());
        bean.setGameid(gameInfo.getRes_id());
        ReportBusiness.getInstance().reportClick(bean);
    }

    private   static boolean getUninatllApkInfo( String filePath) {
        boolean result = false;
        try {
            PackageManager pm = BaseApplication.INSTANCE.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
