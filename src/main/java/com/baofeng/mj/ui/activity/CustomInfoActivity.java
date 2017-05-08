package com.baofeng.mj.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.accountbusiness.UserInfoEditBusiness;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.publicutil.GlideUtil;

import java.lang.ref.WeakReference;

/**
 * 用户资料界面
 */
public class CustomInfoActivity extends BaseActivity implements View.OnClickListener{
    private AppTitleBackView appTitleLayout;
    private RelativeLayout mPortraitLayout;
    private RelativeLayout mNickNameLayout;
    private RelativeLayout mPhoneNumberLayout;
    private WeakReference<ImageView> mPortrait;
    private TextView mNickName;
    private TextView mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_detail_info);
        findView();
        initUserInfo();
        checkPermission();
    }

    /**
     * 空间寻址
     */
    private void findView() {
        appTitleLayout = (AppTitleBackView) findViewById(R.id.custom_detail_title_layout);
        appTitleLayout.getNameTV().setText("我的资料");
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        mPortraitLayout = (RelativeLayout) findViewById(R.id.rl_detail_user_info_portrait);
        mNickNameLayout = (RelativeLayout) findViewById(R.id.rl_detail_user_info_nick_name);
        mPhoneNumberLayout = (RelativeLayout) findViewById(R.id.rl_detail_user_info_phone_number);
        mPortrait = new WeakReference<ImageView>((ImageView) findViewById(R.id.photo));
        mNickName = (TextView) findViewById(R.id.tv_nick_name);
        mPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        mPortraitLayout.setOnClickListener(this);
        mNickNameLayout.setOnClickListener(this);
        mPhoneNumberLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int vid = v.getId();
        if (vid == R.id.rl_detail_user_info_portrait) {
            UserInfoEditBusiness.getInstance().changeHeadPortrait(this);
        } else if (vid == R.id.rl_detail_user_info_nick_name) {
            UserInfoEditBusiness.getInstance().startUserRenameActivity(this);
        } else if (vid == R.id.rl_detail_user_info_phone_number) {
            UserInfoEditBusiness.getInstance().startChangePhoneActivity(this);
        }
    }

    @Override
    protected void onResume() {
        initUserInfo();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserInfoEditBusiness.getInstance().onActivityResult(this,requestCode, resultCode, data);
    }

    private void initUserInfo(){
        if (UserSpBusiness.getInstance().isUserLogin()) {
            UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
            GlideUtil.displayImage(this, mPortrait, userInfo.getLogoUrl(), R.drawable.user_default_head_portrait);
            if (!TextUtils.isEmpty(userInfo.getNikename())) {
                mNickName.setText(userInfo.getNikename());
            }
            if (!TextUtils.isEmpty(userInfo.getMobile())) {
                mPhoneNumber.setText(userInfo.getMobile());
            }
        }
    }

    private void checkPermission(){
        CheckPermission.from(this)
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener(){

                    @Override
                    public void permissionGranted() {
                    }

                    @Override
                    public void permissionDenied() {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(CustomInfoActivity.this,
                                Manifest.permission.CAMERA)) {
                        } else {
                            Toast.makeText(CustomInfoActivity.this,R.string.camera_permission_denied,Toast.LENGTH_SHORT).show();
                        }
                    }
                }).check();
    }
}
