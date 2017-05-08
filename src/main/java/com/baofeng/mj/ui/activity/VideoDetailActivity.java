package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.dialog.UnLockDialog;
import com.baofeng.mj.ui.online.view.VideoPlayerPreView;
import com.baofeng.mj.ui.popwindows.NameSelectionPopWindow;
import com.baofeng.mj.ui.popwindows.VideoDefinitionPopWindow;
import com.baofeng.mj.ui.view.CubeIconScrollView;
import com.baofeng.mj.ui.view.VideoDetialNameGrid;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.VideoApi;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.handmark.pulltorefresh.library.ObservableScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshMyScrollView;
import com.handmark.pulltorefresh.library.ScrollViewListener;
import com.iflytek.thirdparty.E;
import com.mojing.dl.domain.DownloadItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 视频详情页面
 * Created by muyu on 2016/5/6.
 */
public class VideoDetailActivity extends BasePlayerActivity implements View.OnClickListener, ScrollViewListener, UnLockDialog.UnLockCallBack {
    private PullToRefreshMyScrollView contentLayout;
    private ObservableScrollView scrollView;
    private  WeakReference<ImageView> topImageBg;
    private WeakReference<ImageView> topImage;
    private TextView nameTV;
    private TextView gradeTV;
    private TextView actorTV;
    private TextView directorTV;
    private TextView dateTV;
    private TextView typeTV;
    private TextView areaTV;
    private TextView showAllTV;
    private TextView briefTV;
    //    private String url;
    private CubeIconScrollView cubeIconScrollView;
    private VideoDetialNameGrid nameGrid;
    private RelativeLayout fullScreenPlayer;
    private LinearLayout playLayout;
    private RelativeLayout xj_layout;
    private View detail_line;
    private Button mVrPlayBtn;
    private Button downLoadBtn;
    private int categoryType;

    private ImageView startPlayIV;
    public static VideoDetailBean videoBean;

    private VideoPlayerPreView PlayerpreView;

    private boolean hasCacheData;//true有缓存数据，false没有
    private int resultDate;//服务器返回结果的时间戳

    private int result;//高清测试结果
    private UnLockDialog unLockDialog;
    private VideoDetailBean.AlbumsBean mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.INSTANCE.setBaseActivity(this);
        subActivityName = "VideoDetailActivity";
        detailUrl = getIntent().getStringExtra("next_url");
        type = getIntent().getIntExtra("next_type", 0);
        subType = getIntent().getIntExtra("next_subType", 0);
        result = SettingSpBusiness.getInstance().getHigh();
        initView();
        //loadCacheData();//加载缓存数据
        loadData();//请求网络数据
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_detail_content;
    }

    private void initView() {
        contentLayout = (PullToRefreshMyScrollView) findViewById(R.id.video_content_layout);
        scrollView = contentLayout.getRefreshableView();
        scrollView.setScrollViewListener(this);
//        startPlayIV = (ImageView) findViewById(R.id.video_start_play);
//        startPlayIV.setOnClickListener(this);
        topImageBg = new WeakReference<ImageView>((ImageView) findViewById(R.id.video_detail_image));
//        topImage = new WeakReference<ImageView>((ImageView) findViewById(R.id.video_portrait_image));
        nameTV = (TextView) findViewById(R.id.video_detail_name_textview);
        gradeTV = (TextView) findViewById(R.id.video_detail_grade_textview);
        actorTV = (TextView) findViewById(R.id.video_detail_actor_textview);
        directorTV = (TextView) findViewById(R.id.video_detail_director_textview);
        dateTV = (TextView) findViewById(R.id.video_detail_date_textview);
        typeTV = (TextView) findViewById(R.id.video_detail_type_textview);
        areaTV = (TextView) findViewById(R.id.video_detail_area_textview);
        showAllTV = (TextView) findViewById(R.id.video_show_all);
        briefTV = (TextView) findViewById(R.id.video_detail_brief_textview);
        showAllTV.setOnClickListener(this);
        cubeIconScrollView = (CubeIconScrollView) findViewById(R.id.video_detail_cubeIcon);
        nameGrid = (VideoDetialNameGrid) findViewById(R.id.video_detail_name_grid);
        fullScreenPlayer = (RelativeLayout)findViewById(R.id.fullscreen_videpplayer);
        xj_layout = (RelativeLayout) findViewById(R.id.video_detail_xj_layout);
        playLayout = (LinearLayout) findViewById(R.id.video_detail_play_layout);
        detail_line =(View) findViewById(R.id.video_detail_line);
        mVrPlayBtn = (Button)findViewById(R.id.video_detail_play_btn);
        mVrPlayBtn.setOnClickListener(this);
        downLoadBtn = (Button) findViewById(R.id.video_detail_download_btn);
        downLoadBtn.setOnClickListener(this);
        int screenWidth = PixelsUtil.getWidthPixels();
        int width = screenWidth;
        int height =  (int) (width * (9.0f/16));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        fullScreenPlayer.setLayoutParams(params);
        topImageBg.get().setLayoutParams(params);
        PlayerpreView  = new VideoPlayerPreView(this);
        super.mPlayerView = PlayerpreView;
        fullScreenPlayer.addView(PlayerpreView);
        PlayerpreView.setActivityViews(this, fullScreenPlayer, titleBackView);
        fullScreenPlayer.setVisibility(View.VISIBLE);
    }

    public void  reSetPos(){
        if(scrollView!=null){
            scrollView.scrollTo(0,0);
        }
    }

    /**
     * 加载缓存数据
     */
    private void loadCacheData(){
        new VideoApi().getVideoDetailInfo(detailUrl, new CacheCallBack<ResponseBaseBean<VideoDetailBean>>() {
            @Override
            public void onCache(ResponseBaseBean<VideoDetailBean> result) {
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
                loadData();//请求网络数据
            }
        });
    }

    private void loadData() {
        new VideoApi().getVideoDetailInfo(this, detailUrl, new ApiCallBack<ResponseBaseBean<VideoDetailBean>>() {

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
            public void onSuccess(ResponseBaseBean<VideoDetailBean> result) {
                super.onSuccess(result);
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
                            if (resultDate < result.getDate()) {
                                bindView(result.getData());
                            }
                        }
                    } else {
                        if (!hasCacheData) {//没有缓存数据
                            Toast.makeText(VideoDetailActivity.this, result.getStatus_msg(), Toast.LENGTH_SHORT).show();
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

    private void bindView(final VideoDetailBean videobean) {
        contents = videobean.getLandscape_url().getContents();
        nav = videobean.getLandscape_url().getNav();
        initHierarchy();
        this.videoBean = videobean;
//        int screenWidth = PixelsUtil.getWidthPixels();
//        int imgWidth = screenWidth - PixelsUtil.dip2px(186.7f);
//        int width = screenWidth;
//        int height =  (int) (width * (9.0f/16));
//        final FrameLayout.LayoutParams topLayoutParams = new FrameLayout.LayoutParams(imgWidth, imgHeight);
//        topImage.get().setLayoutParams(topLayoutParams);
        //ImageLoader.getInstance().displayImage(videoBean.getPic(), topImage, ImageLoaderUtils.getInstance().getImgOptionsThreeVertical());
//        GlideUtil.displayImage(this, topImage, videoBean.getPic(), R.drawable.img_default_3n_vertical, imgWidth, imgHeight);
//        PlayerpreView  = new VideoPlayerPreView(this);
//        super.mPlayerView = PlayerpreView;
//        fullScreenPlayer.addView(PlayerpreView);
//        PlayerpreView.setActivityViews(this,fullScreenPlayer,titleBackView);
        PlayerpreView.setData(videoBean,0);
        nameTV.setText(videoBean.getTitle());
        gradeTV.setText(videoBean.getScore() + "分");
        actorTV.setText(videoBean.getActors());
        directorTV.setText(videoBean.getDirectors());
        dateTV.setText(videoBean.getYear());
        typeTV.setText(videoBean.getCate());
        areaTV.setText(videoBean.getArea());
        briefTV.setText(videoBean.getDescription());
        int count = videoBean.getMaxseq();//videoBean.getAlbums().get(0).getVideos().size();
        categoryType = videoBean.getCategory_type();
        switch (categoryType) {
            //显示title
            case 1:
                if(videoBean.getIs_3d()==1){//3D 视频
                    cubeIconScrollView.setVisibility(View.GONE);
                    nameGrid.setVisibility(View.GONE);
                    playLayout.setVisibility(View.VISIBLE);
                    mVrPlayBtn.setVisibility(View.VISIBLE);
                    detail_line.setVisibility(View.GONE);
                    xj_layout.setVisibility(View.GONE);
                    if(videoBean.getAlbums().get(0).getVideos().get(0).getDownload_url() != null
                            && !"".equals(videoBean.getAlbums().get(0).getVideos().get(0).getDownload_url())){
                        downLoadBtn.setVisibility(View.VISIBLE);
                        showDownloadBtn();
                    }
                    break;
                }
            case 4:
                cubeIconScrollView.setVisibility(View.GONE);
                nameGrid.setVisibility(View.VISIBLE);
                nameGrid.initData(videoBean, detailUrl);
                if (count <= 10) {
                    showAllTV.setVisibility(View.GONE);
                } else {
                    showAllTV.setVisibility(View.VISIBLE);
                    showAllTV.setText("更多");
                }

                break;
            //显示编号
            case 2:
            case 3:
            case 5:
                cubeIconScrollView.setVisibility(View.VISIBLE);
                nameGrid.setVisibility(View.GONE);
                cubeIconScrollView.initData(videoBean, detailUrl);
                if (count <= 10) {
                    showAllTV.setVisibility(View.GONE);
                } else {
                    showAllTV.setVisibility(View.VISIBLE);
                    showAllTV.setText("更新至" + count + "集");
                }
                break;
        }
//        RelativeLayout.LayoutParams bgLayoutParams=new RelativeLayout.LayoutParams(screenWidth,imgHeight+PixelsUtil.dip2px(68));
//        topImageBg.setLayoutParams(bgLayoutParams);
//        GlideUtil.loadBitmap(BaseApplication.INSTANCE, videoBean.getPic(), new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                if (bitmap != null) {
//                    Bitmap overlay = Bitmap.createScaledBitmap(bitmap, 100, 70, true);
//                    MinifyImageUtil.getInstance().blur(VideoDetailActivity.this, overlay, topImageBg);
//                }
//            }
//        });
        showContentView();
        scrollView.smoothScrollBy(0,0);
    }

    public void showDownloadBtn(){
        File file = DownloadResBusiness.getDownloadResFile(videoBean);
        if(file != null && file.exists()) {//资源存在
            downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_downloaded_btn_bg));
            downLoadBtn.setText(getResources().getString(R.string.downloaded));
            downLoadBtn.setTextColor(getResources().getColor(R.color.prompt_color));
            downLoadBtn.setVisibility(View.VISIBLE);
            downLoadBtn.setClickable(false);
//            playBtn.setVisibility(View.VISIBLE);//显示播放按钮
        }
        showDownLoadProgress(videoBean.getId()+"");
    }
    private int isLoading(){
        DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(videoBean.getRes_id());// 获取下载中的DownloadItem
        if (downloadingItem != null) {
//            if(MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()){// 如果是下载中
            return downloadingItem.getDownloadState();
//            }
        }

        return 0;
    }
    long time = 0;
    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.video_detail_play_btn) {//3D 视频选择VR播放
            reportClick("play");
            StartActivityHelper.startVideoGoUnity(this, detailUrl, contents, nav, 0 + "", ReportBusiness.PAGE_TYPE_DETAIL);
        } else if (i == R.id.video_detail_download_btn){
            reportClick("download");
            DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(videoBean.getId()+"");// 获取下载中的DownloadItem
            if (downloadingItem != null) {// 如果是下载中
                if(System.currentTimeMillis()-time<1000){//1000毫秒点击间隔
                    return;
                }
                time = System.currentTimeMillis();
                if(MjDownloadStatus.DOWNLOADING == downloadingItem.getDownloadState()){
                    DemoUtils.pauseDownload(BaseApplication.INSTANCE, downloadingItem);//暂停下载
                }else{
                    DemoUtils.startDownload(BaseApplication.INSTANCE, downloadingItem);//继续下载
                }
            }else if (UserSpBusiness.getInstance().notLoginForDownload()) {// 未登录时，超过下载限制
                DownLoadBusiness.showLoginForDownloadDialog(this);//提示登录再下载
            }
//            else if (needBuy(context, info)) {// 需要购买
//                toBuy(context, info);//开始购买
//            }
            else if(!NetworkUtil.networkEnable()){//无网络
                DownLoadBusiness.showNetworkErrorDialog(this);
            }else if (!NetworkUtil.canPlayAndDownload()) {// WiFi不可用，不允许gprs网络下载
                DownLoadBusiness.showOpenGprsDialog(this);// 提示WiFi不可用，是否开启gprs网络下载
            }else {
                showSelectDefinitionDialog();//显示选择清晰度对话框
            }
        } else if (i == R.id.refreshView) {
            loadData();
        } else if (i == R.id.video_show_all) {
            switch (categoryType) {
                case 1:
                case 4:
                    NameSelectionPopWindow popWindow = new NameSelectionPopWindow(this, videoBean, detailUrl);
                    popWindow.showAtLocation(nameTV, Gravity.CENTER, 0, 0);
                    popWindow.setCurrentIndex(mIndex);
                    break;
                //显示编号
                case 2:
                case 3:
                case 5:
                    Bundle bundle = new Bundle();
                    bundle.putString("detailUrl", detailUrl);
//                    bundle.putString("centents",videoBean.getLandscape_url().getContents());
//                    bundle.putString("nav",videoBean.getLandscape_url().getNav());
//                    bundle.putSerializable("videoBean", videoBean.getAlbums().get(0));
                    bundle.putInt("select_index",mIndex);
                    Intent intent = new Intent(this, AllSelectionActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
            }
        }
    }

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
    /**
     * 显示选择清晰度对话框
     */
    private void showSelectDefinitionDialog(){
        VideoDefinitionPopWindow popWindow = new VideoDefinitionPopWindow(this, videoBean);
        popWindow.setOnItemClickCallback(new VideoDefinitionPopWindow.OnItemClickCallback() {

            @Override
            public void onItemClick(VideoDetailBean.AlbumsBean album) {
                if (2 != result && "4k".equals(album.getHdtype())) {
                    mAlbum = album;
                    if(unLockDialog == null){
                        unLockDialog = new UnLockDialog(VideoDetailActivity.this, VideoDetailActivity.this);
                    }
                    unLockDialog.show();
//                    reportClick("unlocked", "dwd_claritychg");
                    return;
                }
                startDownload(album,0);
            }
        });
        popWindow.showAtLocation(nameTV, Gravity.CENTER, 0, 0);
    }

    private void startDownload(VideoDetailBean.AlbumsBean albumsBean, int is4k){ //下载4k为1，其他为0
        String downloadUrl = albumsBean.getVideos().get(0).getDownload_url();//某个清晰度的资源下载地址
        String size = albumsBean.getVideos().get(0).getSize();//某个清晰度的资源大小
        DownloadItem downloadItem = DownloadItemUtil.createDownloadItem(videoBean, downloadUrl, size, is4k, detailUrl);
        DownLoadBusiness.downloadStart(downloadItem);
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem){
        if (videoBean == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (downloadItem.getAid().equals(videoBean.getId() + "")) {
                    downLoadBtn.setBackground(getResources().getDrawable(R.drawable.corner_downloaded_btn_bg));
                    downLoadBtn.setText(getResources().getString(R.string.downloaded));
                    downLoadBtn.setTextColor(getResources().getColor(R.color.prompt_color));
                    downLoadBtn.setVisibility(View.VISIBLE);//下载完成，显示为已下载,并且不可点击
                    downLoadBtn.setClickable(false);
//                    playBtn.setVisibility(View.VISIBLE);//下载完成，显示播放按钮
                }
            }
        });
    }

    /**
     * 更新正在下载
     */
    public void updateDownloading(int downloadingSize,List<DownloadItem> downloadItemList){
        if(downloadingSize == 0 || videoBean == null){
            return;
        }
        showDownLoadProgress(videoBean.getId()+"");
    }

    private void showDownLoadProgress(String resId){
        final DownloadItem downloadingItem = BaseApplication.INSTANCE.getDownloadItem(resId);
        if(downloadingItem != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int downloadState = downloadingItem.getDownloadState();
                    if (MjDownloadStatus.ABORT == downloadState) {//已暂停
                        downLoadBtn.setText("暂停中");
                    } else if(MjDownloadStatus.COMPLETE == downloadState){
                        updateDownloaded(downloadingItem);
                    } else if(downloadState == MjDownloadStatus.ERROR){
                        downLoadBtn.setText(getResources().getString(R.string.download_continue));
                    }else {
                        int progress = DownLoadBusiness.getDownloadProgress(downloadingItem);
                        downLoadBtn.setText(progress + "%");
                    }
                }
            });
        }
    }


    private void reportClick(String clickType){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype(clickType);
        bean.setTpos("1");
        bean.setPagetype(ReportBusiness.PAGE_TYPE_DETAIL);
        bean.setTitle(videoBean.getTitle());
        bean.setMovieid(String.valueOf(videoBean.getId()));
        bean.setMovietypeid(String.valueOf(videoBean.getCategory_type()));
        ReportBusiness.getInstance().reportClick(bean);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerpreView!=null){
            PlayerpreView.onResume();
        }

//        startActivity(new Intent(this, PlayOnlineGeneralActivity.class));
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(PlayerpreView!=null){
            PlayerpreView.onPause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.INSTANCE.setBaseActivity(null);
        if(PlayerpreView!=null){
            PlayerpreView.onDestroy();
        }
        try {
            videoBean.getAlbums().clear();
            videoBean = null;
            System.gc();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        if(PlayerpreView!=null){
            PlayerpreView.changeSurface();
        }
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(PlayerpreView!=null&&PlayerpreView.currentMode== VideoPlayerPreView.PlayerScreenMode.fullscreen){
                PlayerpreView.changePlayerScreen(VideoPlayerPreView.PlayerScreenMode.half_screen,false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private int mIndex = 0;//当前选择播放的集数

    /**
     * 刷新当前选择的播放集数
     * @param mIndex
     */
    public void setCurrentIndex(int mIndex){
        if(mIndex<0)
            return;
        this.mIndex = mIndex;
        if(cubeIconScrollView!=null){
            cubeIconScrollView.setCurrentIndex(mIndex);
        }
        if(nameGrid!=null){
            nameGrid.setCurrentIndex(mIndex);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onConfirm() {
        startDownload(mAlbum,1);
    }

    @Override
    public void onCancel() {

    }
}