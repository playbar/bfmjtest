package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.activity.H5Activity;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.lang.ref.WeakReference;

/**
 * 第一次进入应用市场，提示
 * Created by muyu on 2017/4/11.
 */
public class GameTipDialog {
    private Context context;
    private Dialog dialog;//对话框
    private ImageView tipBgIV;
    private String mUrl;

    public GameTipDialog(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.game_tip_dialog,null);//生成布局文件
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        params.height = params.width;
        dialog = new Dialog(context, R.style.alertdialog);// 创建对话框
        dialog.setContentView(view, params);//设置布局文件
        dialog.setCancelable(false);//true可以点击返回键取消对话框
        dialog.setCanceledOnTouchOutside(false);
        tipBgIV = (ImageView) view.findViewById(R.id.game_tip_bg);
        tipBgIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingSpBusiness.getInstance().setGameTips(true);
                dialog.dismiss();
                Intent htmlIntent = new Intent(context, H5Activity.class);
                htmlIntent.putExtra("next_url", mUrl);
                context.startActivity(htmlIntent);
            }
        });
        ImageView closeIV = (ImageView) view.findViewById(R.id.game_tip_close);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingSpBusiness.getInstance().setGameTips(true);
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showDialog(String mPic, String mUrl){
        if(dialog != null && !dialog.isShowing()){
            this.mUrl = mUrl;
            GlideUtil.displayImage(context, new WeakReference<ImageView>(tipBgIV), mPic, -1);
            dialog.show();
        }
    }
}