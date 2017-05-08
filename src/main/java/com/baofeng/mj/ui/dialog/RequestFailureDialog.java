package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by liuchuanchi on 2016/6/14.
 * 请求失败对话框
 */
public class RequestFailureDialog{
    private Dialog dialog;//对话框
    private TextView tv_description;
    private MyDialogInterface myDialogInterface;//回调接口

    public RequestFailureDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_request_failure,null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(true);//true可以点击返回键取消对话框
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (myDialogInterface != null) {
                        myDialogInterface.dialogCallBack(false);
                    }
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        tv_description = (TextView) view.findViewById(R.id.tv_description);
        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDialogInterface != null) {
                    myDialogInterface.dialogCallBack(true);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(String description, MyDialogInterface myDialogInterface){
        if(dialog != null && !dialog.isShowing()){
            this.myDialogInterface = myDialogInterface;
            tv_description.setText(description);
            dialog.show();
        }
    }

    public interface MyDialogInterface {
        void dialogCallBack(boolean againRequest);
    }
}
