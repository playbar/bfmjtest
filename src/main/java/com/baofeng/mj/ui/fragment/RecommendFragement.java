package com.baofeng.mj.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubContentListBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.brbusiness.DeleteDownloadingReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.MyDownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.ui.activity.NoNetWorkActivity;
import com.baofeng.mj.ui.adapter.RecommendAdapter;
import com.baofeng.mj.ui.view.AppTitleView;
import com.baofeng.mj.ui.view.EmptyView;
import com.baofeng.mj.ui.viewholder.NewLoadMoreViewHolder;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.CacheCallBack;
import com.baofeng.mj.util.netutil.RecommendApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyle;
import com.mojing.dl.domain.DownloadItem;

import java.util.ArrayList;

//import com.baofeng.mj.ui.view.PanoramImagePlayerView;

/**
 * Created by yushaochen on 2017/1/18.
 */

public class RecommendFragement extends BaseViewPagerFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<RecyclerView> {

    private LinearLayout contentLayout;
    private PullToRefreshRecyle pullToRefreshScrollView;
    private RecyclerView recyclerView;
    private AppTitleView titleIconLayout;
    private EmptyView emptyView;
    private RelativeLayout noNetWorkLayout;
    private RecommendAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private DownLoadBusiness<ContentInfo> downLoadBusiness;
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private DeleteDownloadingReceiver.DeleteDownloadingNotify deleteDownloadingNotify;

    private ArrayList<ContentInfo> contentListBeans = new ArrayList<ContentInfo>();//中部列表数据

    public static final int TOP_LIST = 1;
    public static final int CONTENT_LIST = 2;
    public static final int LOAD_MORE_LIST = 3;
    public static final int APP_OR_GAME = 4;

    public static boolean refreshFlag = false;

    private int scrolledDY;

    private NewLoadMoreViewHolder loadMoreViewHolder;

    private boolean lock = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                if(downLoadBusiness != null){
                    downLoadBusiness.apkInstallNotify(packageName);//apk安装完成
                }
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);

        deleteDownloadingNotify = new DeleteDownloadingReceiver.DeleteDownloadingNotify() {
            @Override
            public void deleteNotify(DownloadItem downloadItem) {
                if(downLoadBusiness != null){
                    downLoadBusiness.deleteDownloading(downloadItem);//删除正在下载
                }
            }
        };
        DeleteDownloadingReceiver.addDeleteDownloadingNotify(deleteDownloadingNotify);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_recommend, container, false);
            initViews();
            setListener();
            requestData(false);
        }else{
            removeRootView();
            if(adapter != null){
                /**
                 * 刷新下载进度需要此行代码，否则会出现的问题：
                 * 当前界面某个资源下载进度50%，离开当前界面，
                 * 当资源下载完成后再回到当前界面，下载进度还是50%
                 * (以后替换新的下载库，这个问题可以解)。
                 */
                adapter.notifyDataSetChanged();
            }
        }
        if(downLoadBusiness != null){
            BaseApplication.INSTANCE.addDownLoadBusiness(downLoadBusiness);
        }
        return rootView;

    }

    private void initViews() {
        downLoadBusiness = new MyDownLoadBusiness(getActivity());
        gridLayoutManager = new GridLayoutManager(getActivity(), 12, GridLayoutManager.VERTICAL, false);
        contentLayout = (LinearLayout) rootView.findViewById(R.id.choiceness_content_layout);
        emptyView = (EmptyView) rootView.findViewById(R.id.choiceness_empty_view);
        emptyView.setVisibility(View.INVISIBLE);
        emptyView.getRefreshView().setOnClickListener(this);
        pullToRefreshScrollView = (PullToRefreshRecyle) rootView.findViewById(R.id.choiceness_pulltorefresh);
        pullToRefreshScrollView.setOnRefreshListener(this);
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case TOP_LIST:
                    case CONTENT_LIST:
                    case LOAD_MORE_LIST:
                        return 12;
                    case APP_OR_GAME:
                        return 12;
                    default:
                        return -1;
                }

            }
        });

        adapter = new RecommendAdapter(getActivity(), this);
        adapter.setDownLoadBusiness(downLoadBusiness);
        recyclerView.setAdapter(adapter);

        titleIconLayout = (AppTitleView) rootView.findViewById(R.id.video_title_icon_layout);
        noNetWorkLayout = (RelativeLayout) rootView.findViewById(R.id.choiceness_no_network_tab_layout);
        noNetWorkLayout.setVisibility(View.INVISIBLE);
        noNetWorkLayout.setOnClickListener(this);
        //测试全景图片显示
        //testLoadPanoImage();
    }

    private void testLoadPanoImage() {
//        test_image = (PanoramImagePlayerView) rootView.findViewById(R.id.test_image);
//        test_image.setVisibility(View.VISIBLE);
//        test_image.setDoubleScreen(false);//是否双屏显示
//        test_image.setGyroscopeEnable(true);//是否打开陀螺仪
//        test_image.setIsScreenTouch(true);//是否支持触屏
//        GlideUtil.loadBitmap2(getContext(),"http://pano.chinavr.net/360html/360/kmnet/201205/fxh.jpg",resourceReady);

        //test_image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.aatest));
    }

//    private PanoramImagePlayerView test_image;
//
//    private SimpleTarget<Bitmap> resourceReady = new SimpleTarget<Bitmap>() {
//        @Override
//        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//            if(null != resource) {
//                test_image.setImageBitmap(resource);
//            }
//        }
//    };
//
    @Override
    public void onResume() {
        super.onResume();
//        if(null != test_image) {
//            test_image.resumeView();
//        }

        ((MainActivityGroup) getActivity()).registerMyOnTouchListener(myOnTouchListener);

        if(BaseApplication.mainClickPosition!=-1){
            recyclerView.scrollToPosition(BaseApplication.mainClickPosition);
            BaseApplication.mainClickPosition = -1;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(null != test_image) {
//            test_image.pauseView();
//        }
        ((MainActivityGroup) getActivity()).unregisterMyOnTouchListener(myOnTouchListener);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(null != test_image) {
//            test_image.destroyView();
//        }
//    }

    private void requestData(final boolean isMore) {
        //检测网络
        showNetWork();

        if(lock) return;
        lock = true;

        if(!isMore) {
            adapter.isRefreshTop(true);//下拉时执行顶部数据刷新
        } else {
            adapter.isRefreshTop(false);//上拉时不执行顶部数据刷新
        }
//        resetRecyclerView();
        //没有数据时，先加载缓存
        if(contentListBeans.size() == 0) {
            new RecommendApi().getRecommendContentReqCache(new CacheCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {
                @Override
                public void onCache(ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> result) {
                    if(result != null && result.getStatus() == 0) {
                        if(null != result.getData()) {
                            MainSubContentListBean<ArrayList<ContentInfo>> data = result.getData();
                            if(null != data.getList() && data.getList().size() > 0) {
                                ArrayList<ContentInfo> list = data.getList();
                                contentListBeans.clear();
                                contentListBeans.addAll(list);
                                adapter.setContentData(contentListBeans);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }

        //防止横屏返回竖屏,更新列表数据
        if(refreshFlag) {
            refreshFlag = false;
            return;
        }

        new RecommendApi().recommendContentReq(getActivity(), new ApiCallBack<ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>>>() {

            @Override
            public void onStart() {
                super.onStart();
                if(isMore) {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(true, true);//更新加载更多UI
                    }
                }
            }

            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<ArrayList<ContentInfo>>> result) {
                if(result != null && result.getStatus() == 0) {
                    if(!isMore) {
                        showOrHideEmptyView(false);
                    } else {
                        if (loadMoreViewHolder != null) {
                            loadMoreViewHolder.updateLoadMoreUI(false, false);//更新加载更多UI
                        }
                    }
                    if(null != result.getData()) {
                        MainSubContentListBean<ArrayList<ContentInfo>> data = result.getData();
                        if(null != data.getList() && data.getList().size() > 0) {
                            ArrayList<ContentInfo> list = data.getList();
                            if(!isMore) {
                                contentListBeans.clear();
                            }
                            contentListBeans.addAll(list);
                            adapter.setContentData(contentListBeans);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(!isMore) {
                    showOrHideEmptyView(true);
                } else {
                    if (loadMoreViewHolder != null) {
                        loadMoreViewHolder.updateLoadMoreUI(true, false);//更新加载更多UI
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                adapter.notifyDataSetChanged();
                pullToRefreshScrollView.onRefreshComplete();
                lock = false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refreshView) {
            requestData(false);
        } else if (i == R.id.choiceness_no_network_tab_layout) {
            Intent intent = new Intent(getActivity(), NoNetWorkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        requestData(false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    private void showNetWork() {
        if (!NetworkUtil.networkEnable()) {
            noNetWorkLayout.setVisibility(View.VISIBLE);
        } else {
            noNetWorkLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示或隐藏空页面
     */
    private void showOrHideEmptyView(boolean show){
        if(show){//显示空页面
            if(contentListBeans == null || contentListBeans.size() == 0){
                emptyView.setVisibility(View.VISIBLE);
            }
        }else{//隐藏空页面
            emptyView.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledDY = dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {//滑动拖拽

                } else if (RecyclerView.SCROLL_STATE_IDLE == newState) {//滑动停止
                    if (gridLayoutManager != null) {
                        int lastPosition = gridLayoutManager.findLastVisibleItemPosition();
                        int itemCount = gridLayoutManager.getItemCount();
                        if (lastPosition >= itemCount - 1) {//滑动到底部
                            if(scrolledDY > 0){
                                requestData(true);//列表页数据加载更多
                            }
                        }
                    }
                } else {//滑动中

                }
            }
        });
        //重置控制title隐藏和显示的参数
        reset();
        //注册touch事件监听
        initListener();
    }

    private MainActivityGroup.MyOnTouchListener myOnTouchListener;

    private void initListener() {
        myOnTouchListener = new MainActivityGroup.MyOnTouchListener() {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                return dispathTouchEvent(ev);
            }
        };
    }

    private boolean dispathTouchEvent(MotionEvent event) {
        if (mIsAnim) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastY = y;
                lastX = x;

                return false;
            case MotionEvent.ACTION_MOVE:

                float dY = Math.abs(y - lastY);
                float dX = Math.abs(x - lastX);
                boolean down = y > lastY ? true : false;
                lastY = y;
                lastX = x;
                isUp = dX < 8 && dY > 8 && !mIsTitleHide && !down ;
                isDown = dX < 8 && dY > 8 && mIsTitleHide && down;
                if (isUp) {//上滑，隐藏title
                    showHideTitle(true);
                    showHideList(true);
                } else if (isDown) {//下滑，显示title
                    showHideTitle(false);
                } else {
                    return false;
                }
                mIsTitleHide = !mIsTitleHide;
                mIsAnim = true;
                break;
            default:
                return false;
        }
        return false;
    }

    public void setLoadMoreViewHolder(NewLoadMoreViewHolder loadMoreViewHolder) {
        this.loadMoreViewHolder = loadMoreViewHolder;
    }

    private boolean mIsTitleHide = false;
    private boolean mIsAnim = false;
    private float lastX = 0;
    private float lastY = 0;
    private boolean isDown = false;
    private boolean isUp = false;

    private void showHideTitle(boolean tag) {
        if (tag) {//上移隐藏
            mAnimatorTitle = ObjectAnimator.ofFloat(titleIconLayout, "translationY", titleIconLayout.getTranslationY(), - getResources().getDimension(R.dimen.public_main_title_height));
        } else {//下移显示
            mAnimatorTitle = ObjectAnimator.ofFloat(titleIconLayout, "translationY", titleIconLayout.getTranslationY(), 0);
        }
        mAnimatorTitle.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorTitle.setDuration(200);
        mAnimatorTitle.start();
        mAnimatorTitle.addListener(animatorListener);
    }

    private void showHideList(boolean tag) {
        if (tag) {//上移
            mAnimatorContent = ObjectAnimator.ofFloat(pullToRefreshScrollView, "translationY", pullToRefreshScrollView.getTranslationY(),0);
        } else {//下移
            mAnimatorContent = ObjectAnimator.ofFloat(pullToRefreshScrollView, "translationY", pullToRefreshScrollView.getTranslationY(), getResources().getDimension(R.dimen.public_main_title_height));
        }
        mAnimatorContent.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorContent.setDuration(200);
        mAnimatorContent.start();
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener(){

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(isDown){//下滑
                showHideList(false);
            }
            mIsAnim = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator mAnimatorTitle;
    private Animator mAnimatorContent;

    /**
     * 双击左下角tab列表恢复到初始状态
     */
    public void resetRecyclerView() {
        //列表从第一个显示
        recyclerView.scrollToPosition(0);
        //恢复列表和title的位置
        reset();

        //刷新列表
        requestData(false);
    }


    /**
     * 重置控制title隐藏和显示的参数
     */
    public void reset(){
        mIsTitleHide = false;
        mIsAnim = false;
        lastX = 0;
        lastY = 0;
        isDown = false;
        isUp = false;
        mAnimatorTitle = ObjectAnimator.ofFloat(titleIconLayout, "translationY", titleIconLayout.getTranslationY(), 0);
        mAnimatorTitle.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorTitle.setDuration(200);
        mAnimatorTitle.start();
        mAnimatorContent = ObjectAnimator.ofFloat(pullToRefreshScrollView, "translationY", pullToRefreshScrollView.getTranslationY(), getResources().getDimension(R.dimen.public_main_title_height));
        mAnimatorContent.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorContent.setDuration(200);
        mAnimatorContent.start();
    }
}
