package com.baofeng.mj.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.UserInfoEditBusiness;

/**
 * Created by zhaominglei on 2016/5/21.
 * 支付成功提示框
 */
public class ChangeHeadPortraitDialog extends Dialog implements View.OnClickListener {

    private TextView fromAlbum;
    private TextView fromCamera;
    private Button cancel;
    private Activity context;

    public ChangeHeadPortraitDialog(Activity context) {
        super(context, R.style.alertdialog);
        this.context = context;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_change_head_portrait, null);
        setContentView(v);
        fromAlbum = (TextView) v.findViewById(R.id.tv_select_from_album);
        fromCamera = (TextView) v.findViewById(R.id.tv_select_from_camera);
        cancel = (Button) v.findViewById(R.id.cancel);
        fromAlbum.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.tv_select_from_album) {
            UserInfoEditBusiness.getInstance().selectFromAlbum(this.context);
        } else if (vid == R.id.tv_select_from_camera) {
            UserInfoEditBusiness.getInstance().selectFromCarema(this.context);
        } else if (vid == R.id.cancel) {

        }
        cancel();
    }
}
