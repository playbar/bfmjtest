package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.CancleAlbumBusiness;

/**
 * Created by hanyang on 2016/6/22.
 * 取消订阅对话框
 */
public class CancleAlbumDialog {
    private Dialog subCancleDialog;//取消对话框
    private CancleAlbumBusiness cancleAlbumBusiness;//取消订阅接口回调

    /**
     * 创建取消对话框
     *
     * @param context
     */
    public void createCancleDialog(final Context context, final CancleAlbumBusiness cancleCallBack, final int position, final String albumId) {
        subCancleDialog = new Dialog(context, R.style.cancle_sub_style);
        View view = LayoutInflater.from(context).inflate(R.layout.view_sub_cancle_dialog, null);
        subCancleDialog.setContentView(view);
        subCancleDialog.setCancelable(true);
        final TextView cancle_btn = (TextView) view.findViewById(R.id.cancle_btn);
        TextView ok_btn = (TextView) view.findViewById(R.id.ok_btn);
        //取消按钮点击事件
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        //确定按钮点击事件
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                cancleCallBack.cancleCallBack(position, albumId);
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(Context context, CancleAlbumBusiness cancleCallBack, int position, String albumId) {
        if (subCancleDialog == null) {
            createCancleDialog(context, cancleCallBack, position, albumId);
        }
        if (!subCancleDialog.isShowing()) {
            subCancleDialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismissDialog() {
        if (subCancleDialog != null && subCancleDialog.isShowing()) {
            subCancleDialog.dismiss();
        }
    }
}
