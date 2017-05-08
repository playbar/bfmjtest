package com.baofeng.mj.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.accountbusiness.UserInfoEditBusiness;
import com.baofeng.mj.ui.view.ClipBorderImageView;
import com.baofeng.mj.ui.view.ClipZoomImageView;
import com.baofeng.mj.ui.view.TitleBar;

public class ImageCropActivity extends Activity implements View.OnClickListener, UserInfoEditBusiness.IUserInfoEditCallback {

    private String path;

    private ClipZoomImageView iv_clip_zoom_img;
    private ClipBorderImageView iv_clip_border_img;
    private TitleBar rl_title_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(UserInfoEditBusiness.getInstance().IMAGE_PATH)) {
            path = intent.getStringExtra(UserInfoEditBusiness.getInstance().IMAGE_PATH);
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        findViewByIds();
        init();
    }

    private void findViewByIds() {
        rl_title_bar = (TitleBar) findViewById(R.id.rl_title_bar);
        iv_clip_zoom_img = (ClipZoomImageView) findViewById(R.id.iv_clip_zoom_img);
        iv_clip_border_img = (ClipBorderImageView) findViewById(R.id.iv_clip_border_img);
    }

    private void init() {
        rl_title_bar.getRightBtn().setVisibility(View.GONE);
        rl_title_bar.getRightTv().setText("使用");
        rl_title_bar.setOnClickListener(this);
        UserInfoEditBusiness.getInstance().setUserInfoEditCallback(this);
        iv_clip_zoom_img.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = UserInfoEditBusiness.getInstance().createClipZoomImg(path);
                if(bitmap!=null){
                    iv_clip_zoom_img.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back) {
            finish();
        } else if (vid == R.id.tv_right) {
            UserInfoEditBusiness.getInstance().updateHeadPortrait(iv_clip_zoom_img.clip());
        }
    }

    @Override
    public void onUserInfoEditCallback(int code, Object obj) {
        if (code == UserInfoEditBusiness.getInstance().HEAD_PORTRAIT_UPDATE_SUCCESS) {
            Toast.makeText(this, "头像上传成功！", Toast.LENGTH_LONG).show();
        } else if (code == UserInfoEditBusiness.getInstance().HEAD_PORTRAIT_UPDATE_FAIL) {
            Toast.makeText(this, "头像上传失败！", Toast.LENGTH_LONG).show();
        } else if (code == UserInfoEditBusiness.getInstance().NET_EXCEPTION) {
            Toast.makeText(this, "网络异常，头像上传失败！", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
