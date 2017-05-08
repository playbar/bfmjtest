package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResInfoBusiness;
import com.baofeng.mj.business.downloadbusinessnew.DownloadUtils;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.MediaGlActivity;
import com.baofeng.mj.ui.online.view.PlayerTypeChoseDialog;
import com.baofeng.mj.util.entityutil.DownloadItemUtil;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileSizeUtil;
import com.baofeng.mj.util.fileutil.UnZipUtil;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.DemoUtils;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;
import com.baofeng.mj.util.viewutil.ShowUi;
import com.baofeng.mj.util.viewutil.StartActivityHelper;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.bumptech.glide.Glide;
import com.mojing.dl.domain.DownloadItem;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 本地下载
 */
public class LocalDownloadedAdapter extends BaseAdapter {
    private Context context;
    private Fragment fragment;
    private List<DownloadItem> downloadList;
    private LayoutInflater layoutInflater;
    private int main_tab_text_normal;
    private int title_color;
    //报数
    private ReportClickBean reportClickBean;

    public LocalDownloadedAdapter(Context context, Fragment fragment, List<DownloadItem> downloadList) {
        this.context = context;
        this.fragment = fragment;
        this.downloadList = downloadList;
        layoutInflater = LayoutInflater.from(context);
        main_tab_text_normal = context.getResources().getColor(R.color.main_tab_text_normal);
        title_color = context.getResources().getColor(R.color.title_color);
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
            convertView = layoutInflater.inflate(R.layout.adapter_local_downloaded, null);
            viewHolder = new MViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MViewHolder) convertView.getTag();
        }

        final DownloadItem downloadItem = downloadList.get(position);
        long totalSize = downloadItem.getTotalLen();//下载总大小
        String strTotalSize = FileSizeUtil.formatFileSize(totalSize);
        viewHolder.tv_size.setText(strTotalSize);//下载大小
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(downloadItem.getImageUrl(), viewHolder.iv_image, ImageLoaderUtils.getInstance().getImgOptionsWhite());//图标
        Glide.with(fragment).load(downloadItem.getImageUrl()).crossFade().skipMemoryCache(true).into(viewHolder.iv_image.get());

        int resType = downloadItem.getDownloadType();
        String title = downloadItem.getTitle();
        if(ResTypeUtil.res_type_roaming != resType && ResTypeUtil.isNotGameAndApp(resType)){//不是漫游，不是游戏，不是应用，加上后缀名
            title = title + FileCommonUtil.getFileSuffix(downloadItem.getHttpUrl());
        }
        ShowUi.showTitle(viewHolder.tv_title, title, 12);//名称
        viewHolder.downloaded_stickgame.setVisibility(View.GONE);
        if(downloadItem.getPlay_mode() != null) {
            String playMode = downloadItem.getPlay_mode();
            if (playMode.contains("6")) {
                viewHolder.downloaded_stickgame.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.bt_download.setBackgroundResource(R.drawable.corner_downloading_bg);//默认背景
        viewHolder.bt_download.setTextColor(main_tab_text_normal);//默认字体
//        File file = DownloadResBusiness.getDownloadResFileNoEx(downloadItem.getDownloadType(), downloadItem.getTitle());
//        if(file != null){
//            file.renameTo(DownloadResBusiness.getDownloadResFileHasEx(downloadItem.getDownloadType(), downloadItem.getTitle(), downloadItem.getHttpUrl()));
//        }
//        file = DownloadResBusiness.getDownloadResFileHasEx(downloadItem.getDownloadType(), downloadItem.getTitle(), downloadItem.getHttpUrl());
        File file = DownloadResBusiness.getDownloadResFile(downloadItem);
        if(ResTypeUtil.isGameOrApp(resType)) {//游戏或者应用
//            final File file = DownloadResBusiness.getDownloadResFile(downloadItem);
            final String packageName = downloadItem.getPackageName();
//            final int versionCode = Integer.valueOf(downloadItem.getApkVersionCode());
            int versionCode = Integer.valueOf(downloadItem.getApkVersionCode());
            try {
                versionCode = Integer.valueOf(downloadItem.getApkUpdateVersionCode());
            }catch (Exception e){

            }
            int apkState = ApkUtil.checkApk(file, packageName, versionCode);
            switch (apkState){
                case ApkUtil.NEED_INSTALL://安装apk
                    viewHolder.bt_download.setText("安装");
                    break;
                case ApkUtil.CAN_PLAY://打开apk
                    viewHolder.bt_download.setText("打开");
                    viewHolder.bt_download.setBackgroundResource(R.drawable.corner_game_downloaded_bg);//改变背景
                    viewHolder.bt_download.setTextColor(title_color);//改变字体
                    break;
                case ApkUtil.NEED_UNZIP://解压zip
                    viewHolder.bt_download.setText("安装");
                    break;
                case ApkUtil.NEED_UPDATE:
                    viewHolder.bt_download.setText("升级");
                    break;
                default:
                    viewHolder.bt_download.setText("");
                    break;
            }
            viewHolder.bt_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameOnClick(downloadItem);//游戏点击
                }
            });
        }else{//不是游戏
//            final File file = DownloadResBusiness.getDownloadResFile(downloadItem);
            if( resType == ResTypeUtil.res_type_roaming){
                file = DownloadResBusiness.getDownloadResFile(downloadItem.getDownloadType(),downloadItem.getAid(),downloadItem.getTitle(),downloadItem.getHttpUrl());
            }
            if( file.exists()){
                viewHolder.bt_download.setText("播放");
            }else{//下载
                viewHolder.bt_download.setText("");
            }
            viewHolder.bt_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportPanoramaClick(downloadItem);
//                    StartActivityHelper.playPanoramaWithDownloaded(context, downloadItem);
                    showPlayerChooseDialog(downloadItem);
                }
            });
        }
        if(position == downloadList.size() - 1){//最后一个，分割线隐藏
            viewHolder.view_divider.setVisibility(View.GONE);
        }else{
            viewHolder.view_divider.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    /**
     * 播放模式选择
     */
    public void showPlayerChooseDialog(final DownloadItem downloadItem){
        if(downloadItem==null)
            return;
        int resType = downloadItem.getDownloadType();//资源类型
        if(resType!=ResTypeUtil.res_type_video&&resType!=ResTypeUtil.res_type_movie){
            goToVRPlayer(downloadItem);
            return;
        }

        int playerMode = SettingSpBusiness.getInstance().getPlayerMode();
        if(playerMode==0){//极简模式
            goToSimplePlay(downloadItem);
        }else if(playerMode==1){//沉浸模式
            goToVRPlayer(downloadItem);
        }else {
            PlayerTypeChoseDialog dialog = new PlayerTypeChoseDialog(context);
            dialog.setGoUnityParams(fragment.getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (R.id.player_choose_dialog_simple_layout == v.getId()){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goToSimplePlay(downloadItem);
                            }
                        },80);


                    }else if(R.id.player_choose_dialog_vr_layout == v.getId()){
                        goToVRPlayer(downloadItem);

                    }
                }
            });
            dialog.show();
        }
    }

    /**
     * 极简模式播放
     */
    private void goToSimplePlay(DownloadItem downloadItem){
        File file = DownloadResBusiness.getDownloadResFile(downloadItem);
        final String name = downloadItem.getTitle();
       final String resourcePath = file.getAbsolutePath();
        int dimension = downloadItem.getVideo_dimension();
        int is_panoram = downloadItem.getIs_panorama();
        int mVideoType = VideoTypeUtil.getVideoType(is_panoram, dimension);
        Intent intent = new Intent(context, MediaGlActivity.class);
        intent.putExtra("videoPath",resourcePath);
        intent.putExtra("videoName",name);
        intent.putExtra("videoType", String.valueOf(mVideoType));//视频类型
        fragment.getActivity().startActivity(intent);



    }

    /**
     * 沉浸模式播放
     */
    private void goToVRPlayer(DownloadItem downloadItem){
        StartActivityHelper.playPanoramaWithDownloaded(context, downloadItem);
    }


    /**
     * 游戏点击
     */

    public void gameOnClick(DownloadItem downloadItem){
        final File file = DownloadResBusiness.getDownloadResFile(downloadItem);
        int versionCode = Integer.valueOf(downloadItem.getApkVersionCode());
        try {
            versionCode = Integer.valueOf(downloadItem.getApkUpdateVersionCode());
        }catch (Exception e){

        }
        int apkState = ApkUtil.checkApk(file, downloadItem.getPackageName(), versionCode);
        reportGameClick(apkState,downloadItem);
        switch (apkState) {
            case ApkUtil.NEED_INSTALL://安装apk
                ApkUtil.installApk(context, file.getAbsolutePath());//安装apk
                break;
            case ApkUtil.CAN_PLAY://打开apk
                ApkUtil.startPlayApk(context, downloadItem.getPackageName());
                break;
            case ApkUtil.NEED_UNZIP://解压zip
                downloadItem.setAppFromType(ConstantKey.OBB);
                UnZipUtil.unZip(downloadItem, new UnZipUtil.UnZipNotify() {
                    @Override
                    public void notify(DownloadItem downloadItem, int unZipResult) {
                        if (UnZipUtil.UNZIP_SUCCESS == unZipResult) {//解压成功
                            File file = DownloadResBusiness.getDownloadResFile(downloadItem);
                            ApkUtil.installApk(context, file.getAbsolutePath());//安装apk
                        }
                    }
                });
                break;
            case ApkUtil.NEED_UPDATE://升级
//                DemoUtils.startDownload(BaseApplication.INSTANCE, downloadItem);//开始下载
//                DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE, downloadItem);
                if(MjDownloadStatus.ABORT == downloadItem.getDownloadState()){
                    DemoUtils.startDownload(BaseApplication.INSTANCE, downloadItem);//继续下载
                }else{
                    DownloadUtils.getInstance().updateApk(BaseApplication.INSTANCE,downloadItem);
                }
                String baseInfoPath = DownloadResInfoBusiness.getDownloadResInfoFilePath(ResTypeUtil.res_type_downloading, downloadItem.getTitle(),downloadItem.getAid());
                JSONObject json = DownloadItemUtil.createJSONObject(downloadItem);//创建资源信息json
                FileCommonUtil.writeFileString(json.toString(), baseInfoPath);//资源信息保存到正在下载文件夹

            default:
                break;
        }
    }

    /**
     * apk安装完成回调
     */
    public void apkInstallNotify(View view){
        if(view != null){
            MViewHolder holder = (MViewHolder) view.getTag();
            holder.bt_download.setText("打开");
            holder.bt_download.setBackgroundResource(R.drawable.corner_game_downloaded_bg);//改变背景
            holder.bt_download.setTextColor(title_color);//改变字体
        }
    }

    public void setReportClickBean(ReportClickBean reportClickBean) {
        this.reportClickBean = reportClickBean;
    }

    private void reportGameClick(int apkState, DownloadItem downloadItem) {
        if (reportClickBean != null) {
            ReportClickBean bean = new ReportClickBean();
            bean.setEtype(reportClickBean.getEtype());
            bean.setClicktype(reportClickBean.getClicktype());
            bean.setTpos(reportClickBean.getTpos());
            bean.setPagetype(reportClickBean.getPagetype());
            bean.setLocal_menu_id(reportClickBean.getLocal_menu_id());

            bean.setTitle(downloadItem.getTitle());
            bean.setGameid(downloadItem.getAid());
            bean.setClicktype(ReportBusiness.getInstance().getClickType(apkState));
            ReportBusiness.getInstance().reportClick(bean);
        }
    }

    private void reportPanoramaClick(DownloadItem downloadItem) {
        if (reportClickBean != null) {
            ReportClickBean bean = new ReportClickBean();
            bean.setEtype(reportClickBean.getEtype());
            bean.setClicktype(reportClickBean.getClicktype());
            bean.setTpos(reportClickBean.getTpos());
            bean.setPagetype(reportClickBean.getPagetype());
            bean.setLocal_menu_id(reportClickBean.getLocal_menu_id());
            bean.setTitle(downloadItem.getTitle());
            bean.setVideoid(downloadItem.getAid());
            bean.setTypeid(String.valueOf(downloadItem.getDownloadType()));
            ReportBusiness.getInstance().reportClick(bean);
        }
    }

    public class MViewHolder {
        private WeakReference<ImageView> iv_image;
        private TextView tv_title;
        private TextView tv_size;
        private Button bt_download;
        private View view_divider;
        private ImageView downloaded_stickgame;

        public MViewHolder(View view) {
            iv_image = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.iv_image));
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            bt_download = (Button) view.findViewById(R.id.bt_download);
            view_divider = view.findViewById(R.id.view_divider);
            downloaded_stickgame = (ImageView) view.findViewById(R.id.downloaded_stickgame);
        }
    }
}