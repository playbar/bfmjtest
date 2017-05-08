package com.baofeng.mj.ui.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.downloadbusiness.DownLoadBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.viewutil.ShowUi;
import com.baofeng.mojing.sdk.download.entity.NativeCallbackInfo;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.bumptech.glide.Glide;
import com.mojing.dl.domain.DownloadItem;
import com.storm.smart.common.utils.LogHelper;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 本地下载
 */
public class LocalDownloadingAdapter extends BaseAdapter {
    private Activity context;
    private Fragment fragment;
    private List<DownloadItem> downloadList;
    private LayoutInflater layoutInflater;
    private long time  = 0;
    //报数
    private ReportClickBean reportClickBean;

    public LocalDownloadingAdapter(Activity context, Fragment fragment, List<DownloadItem> downloadList) {
        this.context = context;
        this.fragment = fragment;
        this.downloadList = downloadList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return downloadList == null ? 0 : downloadList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return downloadList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_local_downloading, null);
            viewHolder = new MViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MViewHolder) convertView.getTag();
        }

        final DownloadItem downloadItem = downloadList.get(position);
        //ImageLoader.getInstance().displayImage(downloadItem.getImageUrl(), viewHolder.iv_image);//图标
        Glide.with(fragment).load(downloadItem.getImageUrl()).dontAnimate().into(viewHolder.iv_image.get());

        int resType = downloadItem.getDownloadType();
        String title = downloadItem.getTitle();
        if(ResTypeUtil.res_type_roaming != resType && ResTypeUtil.isNotGameAndApp(resType)){//不是漫游，不是游戏，不是应用，加上后缀名
            title = title + FileCommonUtil.getFileSuffix(downloadItem.getHttpUrl());
        }
        ShowUi.showTitle(viewHolder.tv_title, title, 15);//名称
        viewHolder.downloading_stickgame.setVisibility(View.GONE);

        if(downloadItem.getPlay_mode() != null) {
            String playMode = downloadItem.getPlay_mode();
            if (playMode.contains("6")) {
                viewHolder.downloading_stickgame.setVisibility(View.VISIBLE);
            }
        }

        refreshProgress(viewHolder, downloadItem);//刷新进度

        if(position == downloadList.size() - 1){//最后一个，分割线隐藏
            viewHolder.view_divider.setVisibility(View.GONE);
        }else{
            viewHolder.view_divider.setVisibility(View.VISIBLE);
        }

        viewHolder.bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - time < 1000) {//1000毫秒点击间隔
                    return;
                }
                time = System.currentTimeMillis();
                if (downloadItem.getDownloadState() == MjDownloadStatus.DOWNLOADING) {
                    DemoUtils.pauseDownload(context, downloadItem);//暂停下载
                } else {
                    if (NetworkUtil.networkEnable()) {//网络可用
                        DemoUtils.startDownload(context, downloadItem);//开始下载
                        reportGameClick(downloadItem);
                    } else {//网络不可用
                        DownLoadBusiness.showNetworkErrorDialog(context);
                    }
                }
            }
        });
        return convertView;
    }

    /**
     * 刷新进度
     */
    private synchronized void refreshProgress(MViewHolder viewHolder, DownloadItem downloadItem){
//        long downloadedSize = downloadItem.getOffset();//已下载大小
        long totalSize = downloadItem.getTotalLen();//下载总大小
//        int downloadProgress = DownLoadBusiness.getDownloadProgress(downloadedSize, totalSize);//下载百分比
        int downloadProgress = downloadItem.getProgress();
        long downloadedSize = totalSize *  downloadProgress / 100;
        viewHolder.pb_downloading.setProgress(downloadProgress);//下载进度
        String strDownloadedSize = FileSizeUtil.formatFileSize(downloadedSize);
        String strTotalSize = FileSizeUtil.formatFileSize(totalSize);
        viewHolder.tv_size.setText(strDownloadedSize + "/" + strTotalSize);//下载大小

        if(downloadItem.getDownloadState() == MjDownloadStatus.ABORT
                || downloadItem.getDownloadState() == MjDownloadStatus.ERROR){//已暂停或者错误
            if(downloadItem.getDownloadState() == MjDownloadStatus.ABORT ){
                viewHolder.bt_download.setText("暂停中");
            }else {
                viewHolder.bt_download.setText(context.getResources().getString(R.string.download_continue));
            }
            viewHolder.bt_download.setBackgroundResource(R.drawable.corner_gray_button_bg);
            viewHolder.bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.disable_text_color));
        }else{
            viewHolder.bt_download.setText(downloadProgress + "%");
            viewHolder.bt_download.setBackgroundResource(R.drawable.corner_blue_bg);
            viewHolder.bt_download.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.btn_normal_color));
        }
    }

    public void setReportClickBean(ReportClickBean reportClickBean) {
        this.reportClickBean = reportClickBean;
    }

    private void reportGameClick(DownloadItem downloadItem) {
        if (reportClickBean != null) {
            ReportClickBean bean = new ReportClickBean();
            bean.setEtype(reportClickBean.getEtype());
            bean.setClicktype(reportClickBean.getClicktype());
            bean.setTpos(reportClickBean.getTpos());
            bean.setPagetype(reportClickBean.getPagetype());
            bean.setLocal_menu_id(reportClickBean.getLocal_menu_id());
            bean.setTitle(downloadItem.getTitle());
            bean.setClicktype("download");
            if(ResTypeUtil.isGameOrApp(downloadItem.getDownloadType())){
                bean.setGameid(downloadItem.getAid());
            }else{
                bean.setVideoid(downloadItem.getAid());
                bean.setTypeid(String.valueOf(downloadItem.getDownloadType()));
            }
            ReportBusiness.getInstance().reportClick(bean);
        }
    }

    public class MViewHolder {
        private WeakReference<ImageView> iv_image;
        private TextView tv_title;
        private ProgressBar pb_downloading;
        private TextView tv_size;
        private Button bt_download;
        private View view_divider;
        private ImageView downloading_stickgame;

        public MViewHolder(View view) {
            iv_image = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.iv_image));
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            pb_downloading = (ProgressBar) view.findViewById(R.id.pb_downloading);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            bt_download = (Button) view.findViewById(R.id.bt_download);
            view_divider = view.findViewById(R.id.view_divider);
            downloading_stickgame = (ImageView) view.findViewById(R.id.downloading_stickgame);
        }
    }
}