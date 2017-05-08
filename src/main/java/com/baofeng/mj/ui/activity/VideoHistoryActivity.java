package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.business.historybusiness.HistoryBusiness;
import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.adapter.ViewHistoryAdapter;
import com.baofeng.mj.ui.dialog.DeleteVideoHistoryDialog;
import com.baofeng.mj.ui.dialog.RequestFailureDialog;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.util.entityutil.CreateHistoryUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.VideoApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.publicutil.SubTypeUtil;
import com.baofeng.mj.util.threadutil.HistoryProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的播放记录
 */
public class VideoHistoryActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private LinearLayout ll_empty;
    private AppTitleBackView appTitleLayout;
    private PullToRefreshListView pullToRefreshListView;
    private ViewHistoryAdapter viewHistoryAdapter;
    private List<HistoryInfo> historyList;
    private DeleteVideoHistoryDialog deleteVideoHistoryDialog;
    private boolean canShowView;
    private int page = ConfigConstant.pageStart;
    private CustomProgressView progressView;
    private RequestFailureDialog requestFailureDialog;//请求失败对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        subActivityName = "VideoHistoryActivity";
        requestFailureDialog = new RequestFailureDialog(this);
        setContentView(R.layout.activity_video_history);
        canShowView = true;
        appTitleLayout = (AppTitleBackView) findViewById(R.id.video_history_title_layout);
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        appTitleLayout.getNameTV().setText("我的播放记录");
        appTitleLayout.getAppTitleRight().setText("清除");
        appTitleLayout.getAppTitleRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAllDialog();//弹出删除所有对话框
            }
        });
        progressView = (CustomProgressView) findViewById(R.id.history_loading);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                if(PullToRefreshBase.Mode.PULL_FROM_START == refreshView.getCurrentMode()) {//下拉刷新
                    page = ConfigConstant.pageStart;
                }else {//加载更多
                    page++;
                }
                queryCinemaHistory();//从文件读所有的历史信息
            }
        });
        historyList = new ArrayList<HistoryInfo>();
        viewHistoryAdapter = new ViewHistoryAdapter(this, historyList);
        ListView actualListView = pullToRefreshListView.getRefreshableView();
        actualListView.setOnItemLongClickListener(this);
        actualListView.setAdapter(viewHistoryAdapter);
//        showProgressDialog("正在加载...");
        queryCinemaHistory();//从文件读所有的历史信息
        type = ResTypeUtil.res_type_native;
        subType = SubTypeUtil.native_history_record;
//        initHierarchy();
    }

    /**
     * 查询在线历史记录
     */
    private void queryCinemaHistory(){
        if(UserSpBusiness.getInstance().isUserLogin()){//已登录
            new VideoApi().queryCinemaHistory(page, ConfigConstant.pageCount20, new ApiCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    super.onSuccess(result);
                    if (TextUtils.isEmpty(result)) {
                        requestFailure();//请求失败
                    } else {
                        requestSuccess(result, true);//请求成功
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    requestFailure();//请求失败
                }

                @Override
                public void onFinish() {
                    super.onFinish();
//                    dismissProgressDialog();
                    if(canShowView){
                        progressView.setVisibility(View.GONE);
                        pullToRefreshListView.onRefreshComplete();
                    }
                }
            });
        }else{//未登录
            HistoryProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                @Override
                public void run() {
                    final String result = HistoryBusiness.readAllFromHistory(page, ConfigConstant.pageCount20, 0, 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestSuccess(result, false);//请求成功
//                            dismissProgressDialog();
                            if(canShowView){
                                progressView.setVisibility(View.GONE);
                                pullToRefreshListView.onRefreshComplete();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 请求成功
     */
    private void requestSuccess(String result, boolean isUserLogin){
        try {
            JSONObject joResult = new JSONObject(result);
            if("1".equals(joResult.getString("status"))){
                List<HistoryInfo> tempHistoryInfo = new ArrayList<HistoryInfo>();
                JSONArray jaResult = joResult.getJSONArray("data");
                if(jaResult != null && jaResult.length() > 0){
                    if(isUserLogin){//已经登录
                        for(int i = 0; i < jaResult.length(); i++){
                            tempHistoryInfo.add(CreateHistoryUtil.netJsonToHistoryInfo(jaResult.getJSONObject(i)));
                        }

                        JSONArray localJson = CreateHistoryUtil.netJsonToLocalJson(jaResult);
                        HistoryBusiness.saveCinemaHistoryToLocal(localJson);//保存在线历史到本地
                    }else{//未登录
                        for(int i = 0; i < jaResult.length(); i++){
                            tempHistoryInfo.add(CreateHistoryUtil.localJsonToHistoryInfo(jaResult.getJSONObject(i)));
                        }
                    }
                }
                if(canShowView){
                    if(page == ConfigConstant.pageStart){
                        historyList.clear();
                    }else{
                        if(tempHistoryInfo.size() == 0){
                            Toast.makeText(VideoHistoryActivity.this, "没有更多！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    historyList.addAll(tempHistoryInfo);
                    viewHistoryAdapter.notifyDataSetChanged();
                    showView();
                }
            }else{
                Toast.makeText(VideoHistoryActivity.this, joResult.getString("status_msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求失败
     */
    private void requestFailure(){
        if(canShowView){
            if(page > ConfigConstant.pageStart){//说明是加载更多
                page--;
            }
            progressView.setVisibility(View.GONE);

            if(UserSpBusiness.getInstance().isUserLogin()) {//已登录
                if(!NetworkUtil.networkEnable()){
                    networkError();//网络不可用
                    return;
                }
            }
            requestFailureDialog.showDialog("服务器异常，请刷新重试！", new RequestFailureDialog.MyDialogInterface() {
                @Override
                public void dialogCallBack(boolean againRequest) {
                    if (againRequest) {
                        queryCinemaHistory();//从文件读所有的历史信息
                    } else {
                        finish();//结束当前界面
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryInfo historyInfo = historyList.get(position - 1);
        int resType = historyInfo.getResType();
        String detailUrl = historyInfo.getDetailUrl();
        int lastSetIndex = historyInfo.getLastSetIndex();
        if(ResTypeUtil.isPanoramaVideo(resType)){//全景视频
            StartActivityHelper.startPanoramaGoUnity(this, resType, detailUrl, detailUrl, "", "", StartActivityHelper.online_resource_from_history);
        }else{//在线视频
            StartActivityHelper.startVideoGoUnity(this, detailUrl, detailUrl, "", String.valueOf(lastSetIndex + 1), "");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDeleteDialog(position);//弹出删除对话框
        return true;
    }

    /**
     * 弹出删除对话框
     * @param position 点击位置
     */
    private void showDeleteDialog(final int position){
        if(UserSpBusiness.getInstance().isUserLogin()) {//已登录
            if(!NetworkUtil.networkEnable()){
                networkError();//网络不可用
                return;
            }
        }
        final HistoryInfo historyInfo = historyList.get(position - 1);
        if(deleteVideoHistoryDialog == null){
            deleteVideoHistoryDialog = new DeleteVideoHistoryDialog(this);
        }
        deleteVideoHistoryDialog.showDialog("确定删除播放记录吗？", new DeleteVideoHistoryDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                HistoryBusiness.deleteSingleCinemaHistory(historyInfo.getVideoId(), new HistoryBusiness.DeleteHistoryCallback() {
                    @Override
                    public void callback(boolean status) {
                        if (status) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (canShowView) {
                                        historyList.remove(historyInfo);
                                        viewHistoryAdapter.notifyDataSetChanged();
                                        showView();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    /**
     * 弹出删除所有对话框
     */
    private void showDeleteAllDialog(){
        if(UserSpBusiness.getInstance().isUserLogin()) {//已登录
            if(!NetworkUtil.networkEnable()){
                networkError();//网络不可用
                return;
            }
        }
        if(deleteVideoHistoryDialog == null){
            deleteVideoHistoryDialog = new DeleteVideoHistoryDialog(this);
        }
        deleteVideoHistoryDialog.showDialog("确定删除所有播放记录吗？", new DeleteVideoHistoryDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                HistoryBusiness.deleteAllCinemaHistory(new HistoryBusiness.DeleteHistoryCallback() {
                    @Override
                    public void callback(boolean status) {
                        if (status) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (canShowView) {
                                        historyList.clear();
                                        viewHistoryAdapter.notifyDataSetChanged();
                                        showView();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void showView(){
        progressView.setVisibility(View.GONE);
        if(historyList.size() == 0){
            ll_empty.setVisibility(View.VISIBLE);//显示空页面
            pullToRefreshListView.setVisibility(View.GONE);//隐藏内容
            appTitleLayout.getAppTitleRight().setVisibility(View.GONE);//隐藏清除按钮
        }else{
            ll_empty.setVisibility(View.GONE);//隐藏空页面
            pullToRefreshListView.setVisibility(View.VISIBLE);//显示内容
            appTitleLayout.getAppTitleRight().setVisibility(View.VISIBLE);//显示清除按钮
        }
    }

    @Override
    protected void onDestroy() {
        canShowView = false;
        super.onDestroy();
    }

    /**
     * 网络错误
     */
    private void networkError(){
        requestFailureDialog.showDialog("当前网络不可用，请检查网络是否连接！", new RequestFailureDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack(boolean againRequest) {
                finish();//结束当前界面
            }
        });
    }
}
