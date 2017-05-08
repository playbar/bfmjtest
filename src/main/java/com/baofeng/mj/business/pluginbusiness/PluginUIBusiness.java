package com.baofeng.mj.business.pluginbusiness;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PluginDownloadInfo;
import com.baofeng.mj.bean.PluginRequestBean;
import com.baofeng.mj.business.accountbusiness.AppUpdateBusiness;
import com.baofeng.mj.business.downloadbusiness.DownloadResBusiness;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.baofeng.mj.ui.dialog.LoginForDownloadDialog;
import com.baofeng.mj.ui.dialog.MobileNetworkDialog;
import com.baofeng.mj.ui.dialog.PluginLoadingDialog;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;
import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2017/3/16.
 */
public class PluginUIBusiness implements Handler.Callback,PluginDownloadBusiness.IPluginDownloadListener{
    private static PluginUIBusiness mInstance;
    String openningPluginId;
    String roomId;
    String activiyName;
    Handler mHandler = new Handler(this);
    private PluginLoadingDialog loadingDialog;
    private Activity activiy;
    private MobileNetworkDialog mobileNetworkDialog;
    public static PluginUIBusiness getmInstance(){
        if(mInstance==null){
            mInstance = new PluginUIBusiness();
        }
        return mInstance;
    }
    private PluginUIBusiness(){
        PluginOperateBusiness.getInstance();
       PluginDownloadBusiness.getmInstance().onBindListener(this);
    }
    /**
     * 判断查找插件超时，10s后仍然没有可用插件 关闭提示框
     */
    Timer timer;
    private void startTimeoutTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(loadingDialog!=null&&loadingDialog.isShowing()&&activiy!=null){
                    activiy.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activiy,"没找到可用的插件,请稍后重试！",Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                            clearData();
                        }
                    });
                }
            }
        },15*1000);
    }


    /**
     * stop timer
     */
    private void stopTimeoutTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }


    /**
     * 提示登录再下载
     */
    private void showLoginForDownloadDialog(final Activity activity) {
        if(activity==null||activity.isFinishing())
            return;
        new LoginForDownloadDialog(activity).showDialog("登录提示","还未登录哦!", new LoginForDownloadDialog.MyDialogInterface() {
            @Override
            public void dialogCallBack() {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        });
    }
    private void showLoadingDialog(Activity activity){
        showLoadingDialog(activity,R.string.plugin_loading_tip);
    }

    /**
     * 显示正在打开插件提示框
     */
    private void showLoadingDialog(final Activity activity,int resContent){
        if(activity==null||activity.isFinishing())
            return;
        if(loadingDialog!=null&&loadingDialog.isShowing())
            return;
        loadingDialog = new PluginLoadingDialog(activity);
        loadingDialog.setContent(resContent);
        loadingDialog.show();
    }

    /**
     *  关闭提示框
     */
    private void closeLoadingDialog(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }


    /**
     * 清除数据
     */
    private void clearData(){
        openningPluginId = "";
        roomId="";
        activiyName="";
    }
    @Override
    public boolean handleMessage(Message msg) {
        PluginDownloadInfo item = PluginOperateBusiness.getInstance().getPluginById(openningPluginId);
        if(item!=null&&!TextUtils.isEmpty(roomId)) {
            startPlugin(item, roomId, activiyName);
        }
        return false;
    }

    /**
     * 检测是否需要更新app
     */
    private void checkUpgradAPP(){
        activiy.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( SettingSpBusiness.getInstance().getNeedUpdate()) { //app可以升级
                    AppUpdateBusiness mBussiness = new AppUpdateBusiness();
                    mBussiness.getAppUpdateDialog(activiy).showUpdateDialog(activiy, activiy.getResources().getString(R.string.plugin_updateApp_tip));
                }else{
                    Toast.makeText(activiy, "您当前的魔镜版本过低，无法打开直播间!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 开始打开插件
     * @param item
     * @param roomId
     * @param activityname
     */
    public synchronized void startPlugin(final PluginDownloadInfo item,final String roomId,final String activityname){
        if(activiy==null||activiy.isFinishing()||activiy.isDestroyed()){
            clearData();
            return;
        }
        //关闭loading
        if(loadingDialog!=null&&loadingDialog.isShowing()&&activiy!=null) {
            activiy.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeLoadingDialog();
                }
            });
        }

        /**app版本低于当前插件适配的版本提示用户升级*/
        if(item.getPlugin_upgrade()!=1&&ApkUtil.compareVersion(ApkUtil.getVersionNameSuffix(),item.getVersion_name())<0){
            checkUpgradAPP();
            clearData();
            return;
        }
        /**打开时判断网络处理*/
        if(HandleNetWorkException(item,activityname)) {
            return;
        }
        doOpenPlugin(item,roomId,activityname);
        clearData();

    }
    /**
     * 打开直播判断网络
     * */
    private boolean HandleNetWorkException(final PluginDownloadInfo item,final String activityname){
        //无网络时提示
        if(!NetworkUtil.isNetworkConnected(activiy) ){
            activiy.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activiy,"网络不可用！",Toast.LENGTH_SHORT).show();
                }
            });
            clearData();
            return true;
        }
        //移动网络 且 grps开关未打开
        if(!NetworkUtil.canPlayAndDownload()){
            if(mobileNetworkDialog==null||!mobileNetworkDialog.isShowing()) {
                mobileNetworkDialog = new MobileNetworkDialog(activiy, new MobileNetworkDialog.UnLockCallBack() {
                    @Override
                    public void onConfirm() {
                        doOpenPlugin(item,roomId,activityname);
                        clearData();
                    }

                    @Override
                    public void onCancel() {
                        clearData();
                    }
                });
            }
            mobileNetworkDialog.show();

            return true;
        }

        return false;
    }

    /**
     * 打开插件
     * @param item
     * @param roomId
     * @param activityname
     */
    private void doOpenPlugin(PluginDownloadInfo item,String roomId,String activityname){

        try {

            //小花秀
            if(item.getPlugin_id().equals("1")){
                openPluginXHX(item,roomId);
            }else if(!TextUtils.isEmpty(activityname)) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(item.getApk_name(), activityname));
                intent.putExtra("userId", UserSpBusiness.getInstance().getUid());
                intent.putExtra("roomId", roomId);
                String glasses_params = SettingSpBusiness.getInstance().getGlassesIds();
                intent.putExtra("glasses_params", glasses_params);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activiy.startActivity(intent);
            }else {
                PackageManager pm = BaseApplication.getInstance().getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(item.getApk_name());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activiy.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //调起小花秀竖屏播放
    public void openPluginXHX(PluginDownloadInfo item, String roomId){
        Intent intent = new Intent();
//        intent.setComponent(new ComponentName(item.getApk_name(), "com.rgbvr.show.activities.StartActivity")); //小花秀
        intent.setComponent(new ComponentName(item.getApk_name(), "com.rgbvr.show.activities.ThirdPartAuthActivity")); //小花秀
        intent.putExtra("userId",  UserSpBusiness.getInstance().getUid());
        intent.putExtra("roomId", roomId);
        String glasses_params = SettingSpBusiness.getInstance().getGlassesIds();
        intent.putExtra("glasses_params", glasses_params);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activiy.startActivity(intent);
    }



    /**
     * 外部接口  打开插件
     * @param pluginID  插件ID（来源ID）
     * @param roomId  直播房间ID
     * @return
     */
    public synchronized void openPlugin(final Activity context, String pluginID, final String roomId, final String activityName){

        /**用户未登录时引导登录*/
        if(!UserSpBusiness.getInstance().isUserLogin()){
            showLoginForDownloadDialog(context);
            return ;
        }
        /**插件服务未连接*/
        if(!PluginManager.getInstance().isConnected()){
            PluginOperateBusiness.getInstance().bindService();
            Toast.makeText(context,"插件服务未连接，请稍后重试",Toast.LENGTH_SHORT).show();
            return ;
        }
        this.openningPluginId = pluginID;
        this.roomId = roomId;
        this.activiy = context;
        this.activiyName = activityName;

        final PluginDownloadInfo item = PluginOperateBusiness.getInstance().getPluginById(pluginID);

        if(item!=null&&item.getStatus()==PluginDownloadInfo.Installed){
            /**已安装 并且能使用 直接启动插件*/
            startPlugin(item,roomId,activityName);
            return;
        }
        if (item!=null&&item.getStatus()!=MjDownloadStatus.COMPLETE&&(PluginOperateBusiness.getInstance().checkPluginInstalled(item.getApk_name()))){
            /**下载升级中 使用已安装的打开 启动插件*/
            startPlugin(item,roomId,activityName);
            return;
        }

        if(item!=null&&item.getStatus()== MjDownloadStatus.COMPLETE&&!TextUtils.isEmpty(item.getPlugin_name())) {
            if(item.getPlugin_upgrade()==1){
                showLoadingDialog(activiy,R.string.plugin_upgrade_tip);
            }else {
                showLoadingDialog(context);
            }
            item.setPath(DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_plugin) + "/" + item.getPlugin_name());
            File file = new File(item.getPath());
            if (file.exists()) {
                ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
                    @Override
                    public void doWork() {
                        PluginOperateBusiness.getInstance().doInstall(item);
                        if (!TextUtils.isEmpty(openningPluginId) && item.getPlugin_id().equals(openningPluginId)) {
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                });

            }
            return;
        }

        //无网络时提示
        if(!NetworkUtil.isNetworkConnected(activiy) ){
            activiy.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activiy,"网络不可用！",Toast.LENGTH_SHORT).show();
                }
            });
            clearData();
            return;
        }
        //移动网络 且 grps开关未打开
        if(!NetworkUtil.canPlayAndDownload()){
            if(mobileNetworkDialog==null||!mobileNetworkDialog.isShowing()) {
                mobileNetworkDialog = new MobileNetworkDialog(activiy, new MobileNetworkDialog.UnLockCallBack() {
                    @Override
                    public void onConfirm() {
                        checkDownloadPlugin();
                    }

                    @Override
                    public void onCancel() {
                        clearData();
                    }
                });
            }
            mobileNetworkDialog.show();


            return;
        }

        checkDownloadPlugin();

    }

    /**
     * 当本地没有可用插件时 请求服务器检测是否有可下载插件
     */
    public void  checkDownloadPlugin(){
        showLoadingDialog(activiy,R.string.plugin_download_wait_tip);
        PluginDownloadBusiness.getmInstance().requestPluginData(new IqueryPluginListener() {
            @Override
            public void callback(final PluginRequestBean result) {
                activiy.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(result==null){//查询失败
                            Toast.makeText(activiy,"没有找到可用插件！",Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                            clearData();
                        }else if(!result.isStatus()||result.getData()==null||result.getData().size()<=0) { //无可用插件
                            if( SettingSpBusiness.getInstance().getNeedUpdate()) { //app可以升级
                                AppUpdateBusiness mBussiness = new AppUpdateBusiness();
                                mBussiness.getAppUpdateDialog(activiy).showUpdateDialog(activiy, activiy.getResources().getString(R.string.plugin_updateApp_tip));
                            }else {
                                Toast.makeText(activiy,"没有找到可用插件！",Toast.LENGTH_SHORT).show();
                            }
                            closeLoadingDialog();
                            clearData();
                        }
                    }
                });

            }
        });
    }


    /**
     * 安装本地下载好的插件包
     * @param pluginID
     */
    private void localInstall(String pluginID){
        final PluginDownloadInfo item = PluginOperateBusiness.getInstance().getPluginById(pluginID);
        if(isRuningProcess(item.getApk_name())&&item.getPlugin_upgrade()==1){ //下载完成后检测到是升级 并且插件正在运行中 先不安装
            return;
        }
        item.setPath(DownloadResBusiness.getDownloadResFolder(ResTypeUtil.res_type_plugin) + "/" + item.getPlugin_name());
        File file = new File(item.getPath());
        if (file.exists()) {
            ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
                @Override
                public void doWork() {
                    PluginOperateBusiness.getInstance().doInstall(item);
                    if (!TextUtils.isEmpty(openningPluginId) && item.getPlugin_id().equals(openningPluginId)) {
                        mHandler.sendEmptyMessage(0);
                    }
                }
            });

        }
    }

    //下载状态监听
    @Override
    public void onDownloadStatusChange(int state, double progress, String pluginId) {
        updateDownLoading(state,progress,pluginId);
        if(state==MjDownloadStatus.COMPLETE) {
            localInstall(pluginId);
        }
    }

    /**
     * 更新显示插件下载速度
     * @param state
     * @param progress
     * @param pluginId
     */
    private void updateDownLoading(final int state, final double progress , String pluginId){
        //当前要打开的插件ID和正在下载的插件ID相同 显示下载中和下载百分比

        if(!TextUtils.isEmpty(openningPluginId)&&openningPluginId.equals(pluginId)){
            if(activiy!=null&&loadingDialog!=null&&loadingDialog.isShowing()){
               activiy.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       String tip = "";
                       switch (state){
                           case MjDownloadStatus.DEFAULT:
                           case MjDownloadStatus.CONNECTING:
                           case MjDownloadStatus.WAITING:
                               tip = activiy.getResources().getString(R.string.plugin_download_wait_tip);
                               loadingDialog.setContent(tip);
                               break;
                           case MjDownloadStatus.DOWNLOADING:
                                 tip = activiy.getResources().getString(R.string.plugin_downloading_tip);
                               loadingDialog.setContent(tip+" "+ ((int) (progress * 100))+"%");
                               break;
                           case MjDownloadStatus.ERROR:
                           case MjDownloadStatus.ABORT:
                               break;
                           case MjDownloadStatus.COMPLETE:
                               tip = activiy.getResources().getString(R.string.plugin_loading_tip);
                               loadingDialog.setContent(tip);
                               break;

                       }

                   }
               });
            }
        }
    }

    public interface IqueryPluginListener{
        void callback(PluginRequestBean result);
    }

    public void destory(){
        PluginDownloadBusiness.getmInstance().unBindListener(this);
        PluginOperateBusiness.getInstance().unBindService();
        clearData();
    }

    /**
     * 判断插件是否在运行中  如果在运行中先不升级包 避免出现使用中更新崩溃问题
     * @param packageName
     * @return
     */
    public boolean isRuningProcess(String packageName){
        if(TextUtils.isEmpty(packageName))
            return false ;
        ActivityManager am = (ActivityManager)BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的应用
          List<ActivityManager.RunningAppProcessInfo> runServiceList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runServiceInfo : runServiceList){
            String[] pkgList = runServiceInfo.pkgList;
            for (String pkgName : pkgList) {
                if(packageName.equals(pkgName)){
                    return  true;
                }
            }
        }

        return  false;
    }
}
