package com.baofeng.mj.business.pluginbusiness;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.baofeng.mj.bean.PluginDownloadInfo;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.business.sqlitebusiness.SqliteManager;
import com.baofeng.mj.ui.online.utils.ThreadProxy;
import com.baofeng.mj.util.threadutil.SingleThreadProxy;
import com.baofeng.mj.util.threadutil.SqliteProxy;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;


/**
 * Created by wanghongfang on 2017/3/1.
 * 插件操作管理类  安装，卸载，打开,搜索已下载文件等
 */
public class PluginOperateBusiness implements ServiceConnection  {
    private static PluginOperateBusiness mInstance;
    private PluginOperateBusiness(){
        bindService();
    }


    public static PluginOperateBusiness getInstance(){
        if(mInstance==null)
            mInstance = new PluginOperateBusiness();
        return mInstance;
    }



    /**
     * 根据来源类型 找到使用的插件
     * @param pluginID  插件id
     * @return
     */
    public PluginDownloadInfo getPluginById(final String pluginID){

        return SqliteProxy.getInstance().addProxyExecute(new SqliteProxy.ProxyExecute<PluginDownloadInfo>() {
            @Override
            public PluginDownloadInfo execute() {
                PluginDownloadInfo info = SqliteManager.getInstance().getFromLocalPluginInfo(pluginID);
                SqliteManager.getInstance().closeSQLiteDatabase();
                return info;
            }
        });
    }

   //调起小花秀的横屏直播
    public void openPluginXHXLand(PluginDownloadInfo item){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(item.getApk_name(), "com.rgbvr.show.activities.StartActivity")); //小花秀
        String uid = UserSpBusiness.getInstance().getUid();
        intent.putExtra("userId", "3903700880078406");
        intent.putExtra("roomId", "10000000480014");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.getInstance().startActivity(intent);
    }



    /**
     *  绑定插件服务
     */
    public void bindService(){
        if(!PluginManager.getInstance().isConnected()) {
            PluginManager.getInstance().addServiceConnection(this);
        }
    }

    /**
     * 解绑插件服务
     */
    public void unBindService(){
        PluginManager.getInstance().removeServiceConnection(this);
    }

    /**
     * 获取本地的插件
     */

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    public boolean checkPluginInstalled(String packageName){
        boolean isInstalled = false;
        try {
           isInstalled = PluginManager.getInstance().getPackageInfo(packageName,0) !=null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return isInstalled ;
    }

    /**
     * 安装
     * @param item
     */
    public synchronized int doInstall(final PluginDownloadInfo item ) {
        int result = 0;
        try {
            int rePlace = 0;  //第一次安装：0,  覆盖安装： PackageManagerCompat.INSTALL_REPLACE_EXISTING
            //已安装的 需要覆盖安装（版本升级问题）
            if (PluginManager.getInstance().getPackageInfo(item.getApk_name(), 0) != null){
                rePlace = PackageManagerCompat.INSTALL_REPLACE_EXISTING;
            }
            //返回值： PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION， 没权限
            //          PackageManagerCompat.INSTALL_FAILED_ALREADY_EXISTS   已经安装了
            //          PackageManagerCompat.INSTALL_SUCCEEDED;       安装成功
            //          PackageManagerCompat.INSTALL_FAILED_INTERNAL_ERROR  安装失败
            result = PluginManager.getInstance().installPackage(item.getPath(),rePlace);
            //安装成功后删除本地的安装包
            if(result== PackageManagerCompat.INSTALL_SUCCEEDED||result==PackageManagerCompat.INSTALL_FAILED_ALREADY_EXISTS){
                item.setStatus(PluginDownloadInfo.Installed);
                deleteLoaclAPK(item.getPath());
            }else {
                item.setStatus(PluginDownloadInfo.InstallFailed);
            }
            SqliteProxy.getInstance().addProxyRunnable(new SingleThreadProxy.ProxyRunnable() {
                @Override
                public void run() {
                    item.setPlugin_upgrade(0);
                    SqliteManager.getInstance().addPlugin(item);
                    SqliteManager.getInstance().closeSQLiteDatabase();
                }
            });


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;

    }
    /**
     * 删除插件包
     */
    private void deleteLoaclAPK(String path){
        if(!TextUtils.isEmpty(path)){
            File file = new File(path);
            if(file!=null&&file.exists()){
                file.delete();
            }
        }

    }

    /**
     *  卸载
     * @param item
     */
    public synchronized void doUninstall(final String item){
        ThreadProxy.getInstance().addRun(new ThreadProxy.IHandleThreadWork() {
            @Override
            public void doWork() {
                try {
                    PluginManager.getInstance().deletePackage(item, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }



}
