package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by liuchuanchi on 2016/6/14.
 * 提示WiFi不可用，是否打开gprs对话框
 */
public class OpenGprsDialog {
    public static final String title_download = "下载提示";
    public static final String title_play = "播放提示";
    public static final String tip_download = "WiFi不可用，是否开启2G/3G/4G流量下载？";
    public static final String tip_play = "WiFi不可用，是否开启2G/3G/4G流量播放？";
    private Dialog dialog;//对话框
    private TextView tv_title;
    private TextView tv_tip;
    private MyDialogInterface myDialogInterface;//回调接口

    public OpenGprsDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_open_gprs,null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(true);//true可以点击返回键取消对话框
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_open = (TextView) view.findViewById(R.id.tv_open);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDialogInterface != null) {
                    myDialogInterface.dialogCallBack();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(String title, String tip, MyDialogInterface myDialogInterface){
        if(dialog != null){
            tv_title.setText(title);//标题
            tv_tip.setText(tip);//提示
            this.myDialogInterface = myDialogInterface;
            if(!dialog.isShowing()){
                dialog.show();
            }
        }
    }

    public interface MyDialogInterface {
        void dialogCallBack();
    }
}
