package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Created by liuchuanchi on 2016/6/16.
 * 解压对话框
 */
public class UnZipDialog {
    public static final String START_UNZIP = "正在解压zip包，请不要退出...";
    public static final String NOT_FOUND = "找不到zip下载包";
    public static final String UNZIP_ERROR = "zip包解析异常";
    public static final String STROGE_LESS = "解压出错，请检查磁盘容量";
    public static final String FORMAT_ERROR = "zip包格式错误";
    private ProgressDialog loadingDialog;
    private Activity ac;

    public UnZipDialog(final Activity activity){
        this.ac = activity;
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 显示解压对话框
     * @param message
     */
    public void showUnZipDialog(String message, Handler handler){
        showUnZipDialog(message);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissUnZipDialog();
            }
        }, 1500);//延迟1.5秒，隐藏解压对话框
    }

    /**
     * 显示解压对话框
     * @param message
     */
    public void showUnZipDialog(String message){
        if(loadingDialog != null){
            loadingDialog.setMessage(message);
            if(!loadingDialog.isShowing()){
                loadingDialog.show();
            }
        }
    }

    /**
     * 隐藏解压对话框
     */
    public void dismissUnZipDialog(){
        if(loadingDialog != null){
            if(isValidContext()) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        }
    }

    private boolean isValidContext (){

        if(ac==null){
            return false;
        }
        if (ac.isDestroyed() || ac.isFinishing()){
            return false;
        }else{
            return true;
        }
    }
}
