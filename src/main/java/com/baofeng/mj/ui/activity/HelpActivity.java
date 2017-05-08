package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.business.publicbusiness.ConstantKey;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.pubblico.activity.MainActivityGroup;
import com.baofeng.mj.sdk.gvr.vrcore.entity.GlassesNetBean;
import com.baofeng.mj.sdk.gvr.vrcore.utils.GlassesManager;
import com.baofeng.mj.ui.view.AppTitleBackView;

public class HelpActivity extends BaseActivity implements View.OnClickListener {
    public static final String SHOW = "show";
    public static final String IS_MJ = "ismj";

//    private ImageButton help_list_back;
//    private TextView help_list_title;

    private ViewStub normalLayout;
    private ViewStub fivegenerLayout;
    private boolean mIsShow;
    private RelativeLayout mBottomLayout;
    private boolean mIsMj;
    private AppTitleBackView mAppTitleLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mIsShow = getIntent().getBooleanExtra(SHOW,true);
        mIsMj = getIntent().getBooleanExtra(IS_MJ,false);
        initView();
    }

    private void initView() {
        mAppTitleLayout = (AppTitleBackView) findViewById(R.id.help_title_layout);
        mAppTitleLayout.getNameTV().setText(getResources().getString(R.string.handle_connect_explain));
        mAppTitleLayout.getNameTV().setTextColor(Color.WHITE);
        mAppTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        mAppTitleLayout.getBackImgBtn().setImageResource(R.drawable.nav_icon_back_white);

        normalLayout = (ViewStub) findViewById(R.id.help_normal_content);
        fivegenerLayout = (ViewStub) findViewById(R.id.help_fivegener_content);

        mBottomLayout = (RelativeLayout) findViewById(R.id.immediate_experience_layout);
        mBottomLayout.setOnClickListener(this);
        if(mIsShow){
            mBottomLayout.setVisibility(View.VISIBLE);
        }else {
            mBottomLayout.setVisibility(View.GONE);
        }

        if(mIsMj){
            fivegenerLayout.inflate();
        }else {
            normalLayout.inflate();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.help_list_back:
//                finish();
//                break;
            case R.id.immediate_experience_layout:
               startActivity(new Intent(this, MainActivityGroup.class));
               finish();
                break;
        }
    }
}
