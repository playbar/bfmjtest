package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SearchSpBusiness;
import com.baofeng.mj.util.publicutil.ApkUtil;

/**
 * Created by hanyang on 16/10/11.
 * 游戏打开对话框
 */

public class GameOpenDialog {
    private Dialog gameOpenDialog;//取消对话框

    /**
     * 创建取消对话框
     *
     * @param context
     */
    public void createCancleDialog(final Context context, final String packAgeName, final String res_id) {
        gameOpenDialog = new Dialog(context, R.style.cancle_sub_style);
        View view = LayoutInflater.from(context).inflate(R.layout.view_open_game_warn_dialog, null);
        gameOpenDialog.setContentView(view);
        gameOpenDialog.setCancelable(true);
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
                SearchSpBusiness.getInstance().setGameOpenState(res_id);
                ApkUtil.startPlayApk(context, packAgeName);
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(Context context,String packAgeName,String res_id) {
        if (gameOpenDialog == null) {
            createCancleDialog(context,packAgeName,res_id);
        }
        if (!gameOpenDialog.isShowing()) {
            gameOpenDialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismissDialog() {
        if (gameOpenDialog != null && gameOpenDialog.isShowing()) {
            gameOpenDialog.dismiss();
        }
    }
}
