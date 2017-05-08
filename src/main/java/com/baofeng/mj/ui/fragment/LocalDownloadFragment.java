package com.baofeng.mj.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.GameDetailBean;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.ResponseBaseBean;
import com.baofeng.mj.business.brbusiness.ApkInstallReceiver;
import com.baofeng.mj.business.brbusiness.DeleteDownloadingReceiver;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoSaveBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConfigUrl;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.LocalDownloadedAdapter;
import com.baofeng.mj.ui.adapter.LocalDownloadingAdapter;
import com.baofeng.mj.ui.dialog.DeleteDownloadDialog;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.threadutil.LocalDownloadProxy;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.bumptech.glide.Glide;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 本地下载fragment
 */
public class LocalDownloadFragment extends BaseViewPagerFragment{
    private static final String TAG = "LocalDownloadFragment";
    private static final String MENU_ID = "2";
    private ScrollView scrollView_parent;
    private LinearLayout ll_empty;
    private LinearLayout parent_downloading;
    private LinearLayout parent_downloaded;
    private TextView tv_downloading;
    private TextView tv_downloaded;
    private LinearLayout ll_downloading_more;
    private ImageView iv_downloading_more;
    private ListView lv_downloading;
    private ListView lv_downloaded;
    private LocalDownloadingAdapter downloadingAdapter;
    private LocalDownloadedAdapter downloadedAdapter;
    private List<DownloadItem> downloadingList = new ArrayList<DownloadItem>();
    private List<DownloadItem> downloadedList = new ArrayList<DownloadItem>();
    private boolean isShowMore = false;
    private int itemHeightForDownloading = 0;
    private int itemHeightForDownloaded = 0;
    private int downloadingSize;//正在下载的个数
    private int downloadedSize;//已下载的个数
    private DeleteDownloadDialog deleteDownloadDialog;//删除下载对话框
    private ApkInstallReceiver.ApkInstallNotify apkInstallNotify;
    private boolean destroyView;//true视图销毁，false视图没销毁
    private int movePosition;//移动的位置
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadingAdapter = new LocalDownloadingAdapter(getActivity(),this, downloadingList);
        downloadedAdapter = new LocalDownloadedAdapter(getActivity(),this, downloadedList);
        apkInstallNotify = new ApkInstallReceiver.ApkInstallNotify() {
            @Override
            public void installNotify(String packageName) {
                int tempDownloadedSize = downloadedList.size();
                for(int i = 0; i < tempDownloadedSize; i++){
                    String tempPackageName = downloadedList.get(i).getPackageName();
                    if(!TextUtils.isEmpty(tempPackageName) && tempPackageName.equals(packageName)){
                        downloadedAdapter.apkInstallNotify(lv_downloaded.getChildAt(i));
                        break;
                    }
                }
            }
        };
        ApkInstallReceiver.addApkInstallNotify(apkInstallNotify);
        initReportClickBean();
        BaseApplication.INSTANCE.setBaseFragment(this);
    }

    @Override
    public void onDestroy() {
        ApkInstallReceiver.removeApkInstallNotify(apkInstallNotify);
        BaseApplication.INSTANCE.setBaseFragment(null);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glide.with(this).onStart();//开启图片加载
        destroyView = false;
        if(rootView == null){
            rootView = inflater.inflate(R.layout.frag_local_download,container, false);
            scrollView_parent = (ScrollView) rootView.findViewById(R.id.scrollView_parent);
            ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
            parent_downloading = (LinearLayout) rootView.findViewById(R.id.parent_downloading);
            parent_downloaded = (LinearLayout) rootView.findViewById(R.id.parent_downloaded);
            tv_downloading = (TextView) rootView.findViewById(R.id.tv_downloading);
            tv_downloaded = (TextView) rootView.findViewById(R.id.tv_downloaded);
            ll_downloading_more = (LinearLayout) rootView.findViewById(R.id.ll_downloading_more);
            iv_downloading_more = (ImageView) rootView.findViewById(R.id.iv_downloading_more);
            lv_downloading = (ListView) rootView.findViewById(R.id.lv_downloading);
            lv_downloaded = (ListView) rootView.findViewById(R.id.lv_downloaded);
            lv_downloading.setAdapter(downloadingAdapter);
            lv_downloaded.setAdapter(downloadedAdapter);
            setClickListener();//设置监听
            getDownloadInfoData();//获取数据
            List<DownloadItem> list = new ArrayList<>();
            list.addAll( BaseApplication.INSTANCE.getDownloadingList()) ;
            if(!list.isEmpty()){
               for(int i = 0; i < list.size(); i++){
                   for(DownloadItem in : downloadedList){
                       if(list.get(i).getAid().equals(in.getAid())){
                            list.remove(i);
                           i--;
                       }
                   }

               }
                updateDownloading(list.size(),list);
            }

        }else{
            removeRootView();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Glide.with(this).onStop();//停止图片加载
        destroyView = true;
        //removeRootView();
        super.onDestroyView();
    }

    /**
     * 设置监听
     */
    private void setClickListener(){
        ll_downloading_more.setOnClickListener(new View.OnClickListener() {//查看更多
            @Override
            public void onClick(View v) {
                if (isShowMore) {
                    isShowMore = false;
                    iv_downloading_more.setImageResource(R.drawable.public_arrow_down);
                } else {
                    isShowMore = true;

                    iv_downloading_more.setImageResource(R.drawable.public_arrow_up);
                }
                List<DownloadItem> list = BaseApplication.INSTANCE.getDownloadingList();
                updateDownloading(list.size(),list);
            }
        });
        lv_downloading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DownloadItem downloadItem = downloadingList.get(position);
                if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用

                } else {//全景

                }
            }
        });
        lv_downloaded.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DownloadItem downloadItem = downloadedList.get(position);
                if (ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())) {//游戏或者应用
                    downloadedAdapter.gameOnClick(downloadItem);
                } else {
                    reportClick(downloadItem.getAid(), String.valueOf(downloadItem.getDownloadType()), downloadItem.getTitle());
//                    StartActivityHelper.playPanoramaWithDownloaded(getActivity(), downloadItem);
                    if (downloadedAdapter != null) {
                        downloadedAdapter.showPlayerChooseDialog(downloadItem);
                    }
                }
            }
        });
        lv_downloading.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showdeleteDownloadDialog(true, position);//显示删除下载对话框
                return true;
            }
        });
        lv_downloaded.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showdeleteDownloadDialog(false, position);//显示删除下载对话框
                return true;
            }
        });
    }

    /**
     * 显示删除下载对话框
     * @param isDownloading true正在下载的删除，false已下载的删除
     * @param position 点击位置
     */
    private void showdeleteDownloadDialog(final boolean isDownloading, final int position){
        if(deleteDownloadDialog == null){
            deleteDownloadDialog = new DeleteDownloadDialog(getActivity());
        }
        deleteDownloadDialog.showDialog("确定删除下载吗？", new DeleteDownloadDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {//删除下载
                if (isDownloading) {//删除正在下载的
                    if (position < downloadingList.size()) {
                        final DownloadItem downloadItem = downloadingList.get(position);
                        DownLoadBusiness.deleteDownload(downloadItem, new DownLoadBusiness.DeleteDownloadCallback() {
                            @Override
                            public void deleteCallback() {
                                downloadingList.remove(downloadItem);
                                downloadingAdapter.notifyDataSetChanged();
                                resetListViewHeightForDownloading();//重置listView高度
                                Intent intent = new Intent(DeleteDownloadingReceiver.ACTION_DELETE_DOWNLOADING);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("downloadItem", downloadItem);
                                intent.putExtras(mBundle);
                                getActivity().sendBroadcast(intent);
                            }
                        });
                    }
                } else {//删除已下载的
                    if (position < downloadedList.size()) {
                        final DownloadItem downloadItem = downloadedList.get(position);
                        DownLoadBusiness.deleteDownload(downloadItem, new DownLoadBusiness.DeleteDownloadCallback() {
                            @Override
                            public void deleteCallback() {
                                downloadedList.remove(downloadItem);
                                downloadedAdapter.notifyDataSetChanged();
                                resetListViewHeightForDownloaded();//重置listView高度
                                if(downloadedList.isEmpty() && downloadingList.isEmpty()){
                                    DownloadUtils.getInstance().clearCache();
                                }
                            }
                        });
                    }
                }
                showView();//显示view

            }
        });
    }

    /**
     * 获取数据
     */
    private void getDownloadInfoData() {
        List<DownloadItem> list = new ArrayList<>();
        list.addAll(DownloadUtils.getInstance().getAllDownLoadsByState(getActivity(),MjDownloadStatus.COMPLETE,true));
        if (list.size() > 0) {
            for(int i = 0;i < list.size();i++){
                loadData(list.get(i).getAid());
            }
            downloadedList.clear();
            downloadedList.addAll(list);
            DownloadUtils.getInstance().sortFinishTimeByDownLoadItems(downloadedList);
            downloadedAdapter.notifyDataSetChanged();
            resetListViewHeightForDownloaded();//重置listView高度
        }
        showView();//显示view
    }

    private void loadData(final String resId) {
        String detailUrl = "1/detail/"+resId+".js";
        new GameApi().getGameDetailInfo(getActivity(), ConfigUrl.getGameDetailUrl(getActivity(), detailUrl), new ApiCallBack<ResponseBaseBean<GameDetailBean>>() {

            @Override
            public void onSuccess(ResponseBaseBean<GameDetailBean> result) {
                super.onSuccess(result);
                if (result != null) {
                    if (result.getStatus() == 0) {
                        if (result.getData() != null) {
                            for (DownloadItem item : downloadedList){
                                if(item.getAid().equals(resId)){
                                    item.setApkUpdateVersionCode(result.getData().getVersioncode());
                                    downloadedAdapter.notifyDataSetChanged();
                                    resetListViewHeightForDownloaded();//重置listView高度
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 更新已下载
     */
    public void updateDownloaded(final DownloadItem downloadItem){
        LogHelper.e("infosss","title=="+downloadItem.getTitle()+"==type=="+downloadItem.getDownloadType());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!DownloadUtils.getInstance().isContainsDownLoad(downloadedList,downloadItem) && downloadItem.getDownloadState() == MjDownloadStatus.COMPLETE){
                    downloadedList.add(0, downloadItem);
                    DownloadUtils.getInstance().sortFinishTimeByDownLoadItems(downloadedList);
                    downloadedAdapter.notifyDataSetChanged();
                    resetListViewHeightForDownloaded();//重置listView高度
                }else {
                    if(downloadItem.getDownloadState() != MjDownloadStatus.COMPLETE && !downloadedList.isEmpty()){
                        for(int i = 0; i < downloadedList.size();i++){
                                if(downloadItem.getAid().equals(downloadedList.get(i).getAid())){
                                    downloadedList.remove(i);
                                    i--;
                                }
                        }
                        DownloadUtils.getInstance().sortFinishTimeByDownLoadItems(downloadedList);
                        downloadedAdapter.notifyDataSetChanged();
                        resetListViewHeightForDownloaded();//重置listView高度
                    }

                }

                showView();//显示view
            }
        });

    }



    /**
     * 更新正在下载
     */
    public void updateDownloading(final int downloadingSize,final List<DownloadItem> tempDownloadingList){
        LocalDownloadFragment.this.downloadingSize = downloadingSize;
        final List<DownloadItem> newDownloadingList = new ArrayList<DownloadItem>();
        newDownloadingList.clear();

        List<DownloadItem> loadingList = new ArrayList<>();
        loadingList.clear();
        loadingList.addAll(tempDownloadingList);

        if (isShowMore) {//显示全部
            newDownloadingList.addAll(tempDownloadingList);
        } else {//只显示前三个
          /*  if (downloadingSize >= 1) {
                newDownloadingList.add(tempDownloadingList.get(0));
            }
            if (downloadingSize >= 2) {
                newDownloadingList.add(tempDownloadingList.get(1));
            }
            if (downloadingSize >= 3) {
                newDownloadingList.add(tempDownloadingList.get(2));
            }*/
           int index = 0;
            for(DownloadItem in : loadingList){
                index++;
                if(index > 3){
                    continue;
                }
                newDownloadingList.add(in);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadingList.clear();
                downloadingList.addAll(newDownloadingList);
                showView();//显示view
                downloadingAdapter.notifyDataSetChanged();//更新正在下载
                resetListViewHeightForDownloading();//重置listView高度
                if(iv_downloading_more != null){
                    if (isShowMore) {//显示全部
                        iv_downloading_more.setImageResource(R.drawable.public_arrow_up);
                    } else {//只显示前三个
                        iv_downloading_more.setImageResource(R.drawable.public_arrow_down);
                    }
                    if (downloadingSize > 3) {//正在下载个数大于3个，显示箭头
                        ll_downloading_more.setVisibility(View.VISIBLE);
                    } else {//否则隐藏箭头
                        ll_downloading_more.setVisibility(View.GONE);
                    }
                }
            }
        });


        if(!downloadedList.isEmpty()){
            isHave(newDownloadingList,downloadedList);
        }

    }

    private void isHave(List<DownloadItem> list,List<DownloadItem> downloadedList){
        List<DownloadItem> loadingList = new ArrayList<>();
        loadingList.clear();
        loadingList.addAll(list);
        List<DownloadItem> loadedList = new ArrayList<>();
        loadedList.clear();
        loadedList.addAll(downloadedList);
        for(DownloadItem in : loadingList){
            for(DownloadItem item : loadedList){
                if(in.getAid().equals(item.getAid())){
                    updateDownloaded(in);
                }
            }
        }

    }



    /**
     * 显示view
     */
    private void showView(){
        if(destroyView){//视图销毁
            return;
        }
        downloadedSize = downloadedList.size();
//        int loadingSize = BaseApplication.INSTANCE.getDownloadingList().size();
        if(downloadingList.isEmpty()){
            downloadingSize = 0;
        }
        if(ll_empty != null){
            if(downloadingSize == 0 && downloadedSize == 0){
                ll_empty.setVisibility(View.VISIBLE);
                parent_downloading.setVisibility(View.GONE);
                parent_downloaded.setVisibility(View.GONE);
            }else{
                ll_empty.setVisibility(View.GONE);
                if(downloadingSize == 0){
                    parent_downloading.setVisibility(View.GONE);
                }else{
                    parent_downloading.setVisibility(View.VISIBLE);
                    tv_downloading.setText("正在下载(" + downloadingSize + ")");
                }
                if(downloadedSize == 0){
                    parent_downloaded.setVisibility(View.GONE);
                }else{
                    parent_downloaded.setVisibility(View.VISIBLE);
                    tv_downloaded.setText("已下载(" + downloadedSize + ")");
                }
            }
        }
    }

    /**
     * 重置listView高度
     */
    private void resetListViewHeightForDownloading() {
        if(lv_downloading != null){
            ListAdapter listAdapter = lv_downloading.getAdapter();
            if(listAdapter != null){
                int count = listAdapter.getCount();
                if(count > 0){
                    if(itemHeightForDownloading == 0){
                        View listItem = listAdapter.getView(0, null, lv_downloading);
                        listItem.measure(0, 0);
                        itemHeightForDownloading = listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = lv_downloading.getLayoutParams();
                    params.height = itemHeightForDownloading * count;
                    lv_downloading.setLayoutParams(params);
                }
            }
        }
    }

    /**
     * 重置listView高度
     */
    private void resetListViewHeightForDownloaded() {
        if(lv_downloaded != null){
            ListAdapter listAdapter = lv_downloaded.getAdapter();
            if(listAdapter != null){
                int count = listAdapter.getCount();
                if(count > 0){
                    if(itemHeightForDownloaded == 0){
                        View listItem = listAdapter.getView(0, null, lv_downloaded);
                        listItem.measure(0, 0);
                        itemHeightForDownloaded = listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = lv_downloaded.getLayoutParams();
                    params.height = itemHeightForDownloaded * count;
                    lv_downloaded.setLayoutParams(params);
                }
            }
        }
        if(scrollView_parent != null){
            scrollView_parent.smoothScrollTo(0, movePosition);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(scrollView_parent != null){
                scrollView_parent.smoothScrollTo(0, movePosition);
            }
        }
        if(scrollView_parent != null){
            movePosition = scrollView_parent.getScrollY();
        }
    }

    private void reportClick(String videoId, String typeId, String title) {
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("local");
        bean.setLocal_menu_id(MENU_ID);
        bean.setTitle(title);
        bean.setVideoid(videoId);
        bean.setTypeid(typeId);
        ReportBusiness.getInstance().reportClick(bean);
    }

    private void initReportClickBean(){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("play");
        bean.setTpos("1");
        bean.setPagetype("local");
        bean.setLocal_menu_id(MENU_ID);
        downloadingAdapter.setReportClickBean(bean);
        downloadedAdapter.setReportClickBean(bean);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDownloadInfoData();//获取数据
    }
}
