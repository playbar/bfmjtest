package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.CustomInfoActivity;
import com.baofeng.mj.ui.activity.LoginActivity;
import com.baofeng.mj.ui.activity.RegisterActivity;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.MinifyImageUtil;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;

/**
 * 账户页面头像显示View
 * Created by muyu on 2016/5/11.
 */
public class AccountHead extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private View rootView;
    private FrameLayout accountLinear;
    private WeakReference<ImageView> portraitIV;
    private Button loginBtn;
    private Button regBtn;
    private RelativeLayout loginLinear;
    private LinearLayout logoutLinear;
    private TextView userName;
    private TextView modou;
    private TextView moCoin;
    private Bitmap overlay;


    public AccountHead(Context context) {
        this(context, null);
    }

    public AccountHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_account_head, this);
        accountLinear = (FrameLayout) rootView.findViewById(R.id.account_head_bg);
        portraitIV = new WeakReference<ImageView>((ImageView) rootView.findViewById(R.id.account_head_portrait));
        loginBtn = (Button) rootView.findViewById(R.id.login);
        regBtn = (Button) rootView.findViewById(R.id.reg);
        loginLinear = (RelativeLayout) rootView.findViewById(R.id.ll_account_login);
        logoutLinear = (LinearLayout) rootView.findViewById(R.id.ll_account_logout);
        userName = (TextView) rootView.findViewById(R.id.account_user_name);
        modou = (TextView) rootView.findViewById(R.id.tv_modou);
        moCoin = (TextView) rootView.findViewById(R.id.tv_mo_coin);
        portraitIV.get().setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.account_head_portrait) {
            if (UserSpBusiness.getInstance().isUserLogin()) {
                mContext.startActivity(new Intent(mContext, CustomInfoActivity.class));
            }
        } else if (i == R.id.login) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        } else if (i == R.id.reg) {
            mContext.startActivity(new Intent(mContext, RegisterActivity.class));
        }
    }

    public void initUserInfo(Fragment fragment) {
        if (UserSpBusiness.getInstance().isUserLogin()) {
            loginLinear.setVisibility(View.VISIBLE);
            logoutLinear.setVisibility(View.GONE);
            UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
            userName.setText(Common.getEllipsizeStr(userInfo.getNikename(), userInfo.getNikename().getBytes().length, 12));
            updateModouInfo();
            //ImageLoaderUtils.getInstance().getImageLoader().displayImage(userInfo.getLogoUrl(), portraitIV, ImageLoaderUtils.getInstance().getImgOptionsHeadPortrait());
            GlideUtil.displayImage(fragment, portraitIV, userInfo.getLogoUrl(), R.drawable.user_default_head_portrait);
            setGaussianBitmap();
        } else {
            logoutLinear.setVisibility(View.VISIBLE);
            loginLinear.setVisibility(View.GONE);
            portraitIV.get().setImageResource(R.drawable.user_default_head_portrait);
            accountLinear.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }


    private void setGaussianBitmap() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                String url=UserSpBusiness.getInstance().getUserInfo().getLogoUrl();
                /*Bitmap bitmap = ImageLoader.getInstance().loadImageSync(UserSpBusiness.getInstance().getUserInfo().getLogoUrl());
                if (bitmap != null) {
                    overlay = Bitmap.createScaledBitmap(bitmap, 100, 70, true);
                    MinifyImageUtil.getInstance().blur(mContext, overlay, accountLinear);
                }*/
                String logoUrl = UserSpBusiness.getInstance().getUserInfo().getLogoUrl();
                GlideUtil.loadBitmap(BaseApplication.INSTANCE, logoUrl, new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (bitmap != null) {
                            overlay = Bitmap.createScaledBitmap(bitmap, 100, 70, true);
                            MinifyImageUtil.getInstance().blur(mContext, overlay, accountLinear);
                        }
                    }
                });
            }
        }, 50);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onResume(Fragment fragment) {
        initUserInfo(fragment);
    }
    public void updateModouInfo(){
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        String sModouFormat = BaseApplication.INSTANCE.getResources().getString(R.string.modou);
        String finalModou = String.format(sModouFormat, Common.str2float(userInfo.getGift_modou()));
        modou.setText(finalModou);
        String sMoCoinFormat = BaseApplication.INSTANCE.getResources().getString(R.string.mo_coin);
        String finalMoCoin = String.format(sMoCoinFormat, Common.str2float(userInfo.getRecharge_modou()));
        moCoin.setText(finalMoCoin);
    }
}
