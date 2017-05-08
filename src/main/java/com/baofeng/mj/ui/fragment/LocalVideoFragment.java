package com.baofeng.mj.ui.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.LocalVideoBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.brbusiness.ExternalStorageReceiver;
import com.baofeng.mj.business.localbusiness.LocalVideoBusiness;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.FileBrowseActivity;
import com.baofeng.mj.ui.activity.MediaGlActivity;
import com.baofeng.mj.ui.adapter.LocalVideoAdapter;
import com.baofeng.mj.ui.dialog.DeleteLocalVideoDialog;
import com.baofeng.mj.ui.listeners.AbsListViewScrollDetector;
import com.baofeng.mj.ui.online.view.PlayerTypeChoseDialog;
import com.baofeng.mj.ui.popwindows.LocalVideoSortPop;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.publicutil.ComparatorLong;
import com.baofeng.mj.util.publicutil.ComparatorString;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.util.threadutil.LocalVideoProxy;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * 本地视频Fragment
 */
public class LocalVideoFragment extends BaseViewPagerFragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String MENU_ID = "1";
    private LinearLayout ll_empty;
    private TextView tv_browser_local;
    private LinearLayout ll_video_title;
    private TextView tv_video_count;
    private LinearLayout ll_menu;
    private LinearLayout ll_file_browse;
    private PullToRefreshListView pullToRefreshListView;
    private ListView actualListView;
    private LocalVideoAdapter adapter;
    private List<LocalVideoBean> videoList = new ArrayList<LocalVideoBean>();
    private LocalVideoSortPop localVideoSortPop;//排序pop
    private DeleteLocalVideoDialog deleteLocalVideoDialog;
    private boolean onItemClick;
    private boolean isOnLoadData;//true正在加载数据，false没有加载数据
    private boolean isFirstLoadData = true;//true第一次加载数据，false不是第一次加载
    private ExternalStorageReceiver.ExternalStorageNotify externalStorageNotify;
    private CustomProgressView progressView;
    private AbsListViewScrollDetector absListViewScrollDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new LocalVideoAdapter(getActivity(), this, videoList);
        checkPermission();
        externalStorageNotify = new ExternalStorageReceiver.ExternalStorageNotify() {
            @Override
            public void externalStorageState(boolean status) {
                getLocalVideoData(); //刷新数据
            }
        };
        BaseApplication.INSTANCE.addExternalStorageNotify(externalStorageNotify);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_local_video, container, false);
            ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
            tv_browser_local = (TextView) rootView.findViewById(R.id.tv_browser_local);
            ll_video_title = (LinearLayout) rootView.findViewById(R.id.ll_video_title);
            tv_video_count = (TextView) rootView.findViewById(R.id.tv_video_count);
            ll_menu = (LinearLayout) rootView.findViewById(R.id.ll_menu);
            ll_file_browse = (LinearLayout) rootView.findViewById(R.id.ll_file_browse);
            pullToRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pullToRefreshListView);
            pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            pullToRefreshListView.setOnItemClickListener(this);
            pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    if (isOnLoadData) {//正在加载数据
                        pullToRefreshListView.onRefreshComplete();
                        return;//直接返回
                    }
                    getLocalVideoData(); //刷新数据
                }
            });

            progressView = (CustomProgressView) rootView.findViewById(R.id.local_video_loading);
            actualListView = pullToRefreshListView.getRefreshableView();
            absListViewScrollDetector = new AbsListViewScrollDetector();
            actualListView.setOnScrollListener(absListViewScrollDetector);
            actualListView.setOnItemLongClickListener(this);
            actualListView.setAdapter(adapter);
            ll_menu.setOnClickListener(this);
            ll_file_browse.setOnClickListener(this);
            tv_browser_local.setOnClickListener(this);
        }else{
            removeRootView();
        }
        getLocalVideoData(); //刷新数据
        if(absListViewScrollDetector != null){
            absListViewScrollDetector.addFragment(this);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Glide.with(this).onStop();//停止图片加载
        if(absListViewScrollDetector != null){
            absListViewScrollDetector.removeFragment();
        }
        //removeRootView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        BaseApplication.INSTANCE.removeExternalStorageNotify();
        super.onDestroy();
    }

    /**
     * 获取数据
     */
    private void getLocalVideoData() {
        if (isOnLoadData) {//正在加载数据
            return;//直接返回
        }
        isOnLoadData = true;//正在加载数据
        if(isFirstLoadData){//第一次加载数据
            isFirstLoadData = false;
            progressView.setVisibility(View.VISIBLE);//显示加载框
        }
        LocalVideoProxy.getInstance().addProxyRunnable(new LocalVideoProxy.ProxyRunnable() {
            @Override
            public void run() {
                int sortRule = SettingSpBusiness.getInstance().getLocalVideoSort();
                LocalVideoBusiness.getInstance().searchLocalVideo(sortRule, new LocalVideoBusiness.LocalVideoDataCallback() {
                    @Override
                    public void callback(final TreeMap localVideoMap) {
                        if(getActivity() == null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {//更新UI
                            @Override
                            public void run() {
                                isOnLoadData = false;//完成加载数据
                                if (progressView != null) {
                                    progressView.setVisibility(View.GONE);//隐藏加载框
                                }
                                if (pullToRefreshListView != null) {
                                    pullToRefreshListView.onRefreshComplete();
                                }
                                if (localVideoMap == null || localVideoMap.size() == 0) {
                                    showEmptyView();//显示空页面
                                } else {
                                    showContentView();//显示内容页面
                                    refresh(localVideoMap);//刷新
                                }
                            }
                        });
                    }
                },false);
            }
        });
    }

    /**
     * 显示空页面
     */
    private void showEmptyView() {
        if(ll_video_title != null){
            ll_video_title.setVisibility(View.GONE);
        }
        if(pullToRefreshListView != null){
            pullToRefreshListView.setVisibility(View.GONE);
        }
        if(ll_empty != null){
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示内容页面
     */
    private void showContentView() {
        if(ll_video_title != null){
            ll_video_title.setVisibility(View.VISIBLE);
        }
        if(pullToRefreshListView != null){
            pullToRefreshListView.setVisibility(View.VISIBLE);
        }
        if(ll_empty != null){
            ll_empty.setVisibility(View.GONE);
        }
    }

    /**
     * 显示视频数量
     */
    private void showVideoCount() {
        tv_video_count.setText("视频(" + videoList.size() + ")");
    }

    /**
     * 刷新
     */
    private void refresh(TreeMap treeMap) {
        if(adapter != null){
            videoList.clear();
            Iterator iterator = treeMap.values().iterator();
            while (iterator.hasNext()) {
                videoList.add((LocalVideoBean) iterator.next());
            }
            adapter.notifyDataSetChanged();
            actualListView.setSelection(0);
            showVideoCount();//显示视频数量
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ll_menu) {//弹出排序pop
            showPop();
        } else if (viewId == R.id.ll_file_browse) {//文件浏览
            startActivity(new Intent(getActivity(), FileBrowseActivity.class));
        } else if (viewId == R.id.tv_browser_local) {//文件浏览
            startActivity(new Intent(getActivity(), FileBrowseActivity.class));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClick) {
            return;//点击过，直接返回
        }
        onItemClick = true;//置为true，不让点击
        LocalVideoBean localVideoBean = videoList.get(position - 1);
//        progressView.setVisibility(View.VISIBLE);
//        StartActivityHelper.playVideoWithLocal(getActivity(), localVideoBean.name, localVideoBean.path, new StartActivityHelper.GotoPlayCallback() {
//            @Override
//            public void callback() {//去播放的回调
//                if(getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressView.setVisibility(View.GONE);
//                            onItemClick = false;
//                        }
//                    });
//                }
//            }
//        });//播放本地视频
        showPlayerChooseDialog(localVideoBean);
        reportClick(localVideoBean.name);
    }



    /**
     * 播放模式选择
     */
    public void showPlayerChooseDialog(final LocalVideoBean videoBean){
        int playerMode = SettingSpBusiness.getInstance().getPlayerMode();
        if(playerMode==0){//极简模式
          goToSimplePlay(videoBean);
        }else if(playerMode==1){//沉浸模式
          goToVRPlayer(videoBean);
        }else {
            PlayerTypeChoseDialog dialog = new PlayerTypeChoseDialog(getContext());
            dialog.setGoUnityParams(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (R.id.player_choose_dialog_simple_layout == v.getId()){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goToSimplePlay(videoBean);
                            }
                        },80);


                    } else if (R.id.player_choose_dialog_vr_layout == v.getId()) {
                        goToVRPlayer(videoBean);

                    } else if (R.id.close_img == v.getId()) {
                        onItemClick = false;
                    }
                }
            }, new PlayerTypeChoseDialog.onBackPressListener() {
                @Override
                public void back() {
                    onItemClick = false;
                }
            });
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onItemClick = false;
                }
            });
        }
    }

    /**
     * 极简模式播放
     */
    private void goToSimplePlay(final LocalVideoBean videoBean){

        VideoTypeUtil.getVideoType(videoBean.path, new VideoTypeUtil.VideoTypeCallback() {
            @Override
            public void result(int videoType) {
                Intent intent = new Intent(getActivity(), MediaGlActivity.class);
                intent.putExtra("videoPath",videoBean.path);
                intent.putExtra("videoName",videoBean.name);
                intent.putExtra("videoType", String.valueOf(videoType));//视频类型
                getActivity().startActivity(intent);
            }
        });

        progressView.setVisibility(View.GONE);
        onItemClick = false;
    }

    /**
     * 沉浸模式播放
     */
    private void goToVRPlayer(LocalVideoBean videoBean){
        StartActivityHelper.playVideoWithLocal(getActivity(), videoBean.name, videoBean.path, new StartActivityHelper.GotoPlayCallback() {
            @Override
            public void callback() {//去播放的回调
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressView.setVisibility(View.GONE);
                            onItemClick = false;
                        }
                    });
                }
            }
        });//播放本地视频
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDeleteDialog(position);//弹出删除对话框
        return true;
    }

    /**
     * 弹出排序pop
     */
    private void showPop() {
        if (videoList.size() == 0) {
            return;
        }
        if (localVideoSortPop == null) {
            localVideoSortPop = new LocalVideoSortPop(getActivity());
        }
        localVideoSortPop.showPop(ll_video_title, new LocalVideoSortPop.LocalVideoSortCallback() {
            @Override
            public void select(int sortRule) {//排序规则，0添加时间从新到旧，1文件名，2文件大小
                TreeMap treeMap = null;
                if (FileCommonUtil.ruleFileLastModify == sortRule) {//文件添加时间排序
                    treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
                    for (LocalVideoBean localVideoBean : videoList) {
                        treeMap.put(localVideoBean.lastModify, localVideoBean);
                    }
                } else if (FileCommonUtil.ruleFileName == sortRule) {//文件名排序
                    treeMap = new TreeMap<String, LocalVideoBean>(new ComparatorString());
                    for (LocalVideoBean localVideoBean : videoList) {
                        if (treeMap.containsKey(localVideoBean.name)) {//当前key（文件名）已存在
//                            treeMap.put(localVideoBean.name + System.currentTimeMillis(), localVideoBean);
                            treeMap.put(localVideoBean.name + localVideoBean.path, localVideoBean);
                        } else {
                            treeMap.put(localVideoBean.name, localVideoBean);
                        }
                    }
                } else {//文件大小排序
                    treeMap = new TreeMap<Long, LocalVideoBean>(new ComparatorLong());
                    for (LocalVideoBean localVideoBean : videoList) {
                        treeMap.put(localVideoBean.length, localVideoBean);
                    }
                }
                refresh(treeMap);//刷新
            }
        });
    }

    /**
     * 弹出删除对话框
     *
     * @param position 点击位置
     */
    private void showDeleteDialog(final int position) {
        final LocalVideoBean localVideoBean = videoList.get(position - 1);
        if (deleteLocalVideoDialog == null) {
            deleteLocalVideoDialog = new DeleteLocalVideoDialog(getActivity());
        }
        deleteLocalVideoDialog.showDialog(localVideoBean, new DeleteLocalVideoDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                boolean deleteResult = FileCommonUtil.deleteFile(localVideoBean.path);//删除本地视频文件
                if (deleteResult) {//删除文件成功
                    videoList.remove(position - 1);
                    adapter.notifyDataSetChanged();
                    if (videoList.size() == 0) {
                        showEmptyView();//显示空页面
                    }
                    showVideoCount();//显示视频数量
                } else {//删除文件失败
                    Toast.makeText(getActivity(), "当前视频无法删除！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            CheckPermission.from(getActivity())
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setPermissionListener(new PermissionListener() {

                        @Override
                        public void permissionGranted() {
                        }

                        @Override
                        public void permissionDenied() {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            } else {
                                Toast.makeText(getActivity(), R.string.storage_permission_denied, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).check();
        }
    }

    private void reportClick(String title){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("local");
        bean.setLocal_menu_id(MENU_ID);
        bean.setTitle(title);
        ReportBusiness.getInstance().reportClick(bean);
    }
}
