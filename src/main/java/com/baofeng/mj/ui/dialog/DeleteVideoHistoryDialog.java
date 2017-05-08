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
 * 删除视频历史记录对话框
 */
public class DeleteVideoHistoryDialog {
    private Dialog dialog;//对话框
    private TextView tv_describe;
    private MyDialogInterface myDialogInterface;//回调接口

    public DeleteVideoHistoryDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_delete_video_history,null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(true);//true可以点击返回键取消对话框
        tv_describe = (TextView) view.findViewById(R.id.tv_describe);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_delete = (TextView) view.findViewById(R.id.tv_delete);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
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
    public void showDialog(String describe, MyDialogInterface myDialogInterface){
        if(dialog != null && !dialog.isShowing()){
            this.myDialogInterface = myDialogInterface;
            tv_describe.setText(describe);
            dialog.show();
        }
    }

    public interface MyDialogInterface {
        void dialogCallBack();
    }
}
