package com.baofeng.mj.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.permissionbusiness.CheckPermission;
import com.baofeng.mj.business.permissionbusiness.PermissionListener;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.ChargeActivity;
import com.baofeng.mj.ui.activity.CodeScanActivity;
import com.baofeng.mj.ui.activity.ControlSettingActivity;
import com.baofeng.mj.ui.activity.HelpFeedBackActivity;
import com.baofeng.mj.ui.activity.SettingActivity;
import com.baofeng.mj.ui.activity.ShopWebActivity;
import com.baofeng.mj.ui.activity.SubscribeActivity;
import com.baofeng.mj.ui.activity.TaskListActivity;
import com.baofeng.mj.ui.activity.VideoHistoryActivity;
import com.baofeng.mj.ui.activity.VrSettingActivity;
import com.baofeng.mj.ui.activity.WebExperienceReportActivity;
import com.baofeng.mj.ui.view.AccountHead;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.handmark.pulltorefresh.library.ObservableScrollView;
import com.handmark.pulltorefresh.library.ScrollViewListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by muyu on 2016/10/13.
 */
public class AccountFragment extends BaseFragment implements View.OnClickListener{
    private AccountHead accountHead;
    private RelativeLayout playHistoryRelative;
    private RelativeLayout subscribeRelative;
    private RelativeLayout myMissionRelative;
    private RelativeLayout modouChargeRelative;
    private RelativeLayout modouShopRelative;
    private RelativeLayout settingRelative;
    private RelativeLayout scanRelative;
    private RelativeLayout helpRelative;
    private RelativeLayout experienceRelative;
    private AppTitleBackView titleBar;
    private RelativeLayout account_my_device_relative;
    private ObservableScrollView sv_main_content;
    private View reportDivider;
    private ImageView shadow;
    private Handler mHandler = new Handler();
    private TextView has_content, my_task, my_sub;//客服信息小红点

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.frag_account, null);
        initView();
        initLanguageText();
        if (PermissionUtil.isOverMarshmallow() && !PermissionUtil.hasSelfPermissions(getActivity(), Manifest.permission.CAMERA)) {
            checkPermission();
        }
        checkIfHasGift();//检查是否有礼券发放活动
        return rootView;
    }

    private void initLanguageText() {
        TextView historyTV = (TextView) rootView.findViewById(R.id.account_record_history_pre);
        historyTV.setText(LanguageValue.getInstance().getValue(getActivity(), "SID_MY_PLAY_LOG"));//我的播放记录
    }

    private void initView() {
        accountHead = (AccountHead) rootView.findViewById(R.id.account_head_linear);
        sv_main_content = (ObservableScrollView) rootView.findViewById(R.id.sv_main_content);
        titleBar = (AppTitleBackView) rootView.findViewById(R.id.account_title_back);
        titleBar.getInvrImgBtn().setVisibility(View.GONE);
        shadow = (ImageView) rootView.findViewById(R.id.iv_account_shadow);
        playHistoryRelative = (RelativeLayout) rootView.findViewById(R.id.account_play_history_relative);
        subscribeRelative = (RelativeLayout) rootView.findViewById(R.id.account_my_subscribe_relative);
        myMissionRelative = (RelativeLayout) rootView.findViewById(R.id.account_my_mission_relative);
        modouChargeRelative = (RelativeLayout) rootView.findViewById(R.id.account_modou_recharge_relative);
        modouShopRelative = (RelativeLayout) rootView.findViewById(R.id.account_modou_shop_relative);
        settingRelative = (RelativeLayout) rootView.findViewById(R.id.account_setting_relative);
        scanRelative = (RelativeLayout) rootView.findViewById(R.id.account_scan_relative);
        helpRelative = (RelativeLayout) rootView.findViewById(R.id.account_help_relative);
        account_my_device_relative=(RelativeLayout)rootView.findViewById(R.id.account_my_device_relative);
        account_my_device_relative.setOnClickListener(this);
        reportDivider = (View) rootView.findViewById(R.id.v_experience_report_divider);
        experienceRelative = (RelativeLayout) rootView.findViewById(R.id.account_experience_relative);
        my_task = (TextView) rootView.findViewById(R.id.my_task);
        my_task.setText(LanguageValue.getInstance().getValue(getActivity(), "SID_MY_TASK"));
        my_sub = (TextView) rootView.findViewById(R.id.my_sub);
        my_sub.setText(LanguageValue.getInstance().getValue(getActivity(), "SID_MY_SUBSCRIBE"));
        has_content = (TextView) rootView.findViewById(R.id.has_content);
        if (SettingSpBusiness.getInstance().getHasContent()) {
            has_content.setVisibility(View.VISIBLE);
        } else {
            has_content.setVisibility(View.GONE);
        }
        playHistoryRelative.setOnClickListener(this);
        subscribeRelative.setOnClickListener(this);
        myMissionRelative.setOnClickListener(this);
        modouChargeRelative.setOnClickListener(this);
        modouShopRelative.setOnClickListener(this);
        settingRelative.setOnClickListener(this);
        scanRelative.setOnClickListener(this);
        helpRelative.setOnClickListener(this);
        experienceRelative.setOnClickListener(this);
        sv_main_content.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        sv_main_content.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollView.setScrollViewListener(new ScrollViewListener() {
                    @Override
                    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                        int height = PixelsUtil.px2dip(200);
                        if (y >= 0 && y <= height) {
                            int alpha = 255 - y * 255 / height;
                            shadow.getBackground().setAlpha(alpha);
                        } else {
                            if (y > height) {
                                shadow.getBackground().setAlpha(0);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.account_play_history_relative) {
            startActivity(new Intent(getActivity(), VideoHistoryActivity.class));
        } else if (i == R.id.account_my_subscribe_relative) {
            if (UserSpBusiness.getInstance().isUserLogin()) {
                startActivity(new Intent(getActivity(), SubscribeActivity.class));
            } else {
                Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.account_my_mission_relative) {
            if (UserSpBusiness.getInstance().isUserLogin()) {
                startActivity(new Intent(getActivity(), TaskListActivity.class));
            } else {
                Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.account_modou_recharge_relative) {
            if (isLogin()) {
                startActivity(new Intent(getActivity(), ChargeActivity.class));
            }
        } else if (i == R.id.account_modou_shop_relative) {
            startActivity(new Intent(getActivity(), ShopWebActivity.class));
        } else if (i == R.id.account_setting_relative) {
            startActivity(new Intent(getActivity(), SettingActivity.class));
        } else if (i == R.id.account_scan_relative) {
            startActivity(new Intent(getActivity(), CodeScanActivity.class));
        } else if (i == R.id.account_help_relative) {
            SettingSpBusiness.getInstance().setHasContent(false);
//            startActivity(new Intent(this, HelpListActivity.class));
            //ChatUitl.getInstance(getActivity()).startChat(UserSpBusiness.getInstance().getNickName(),UserSpBusiness.getInstance().getUid());

            startActivity(new Intent(getActivity(), HelpFeedBackActivity.class));

        } else if (i == R.id.account_experience_relative) {
//            if (isLogin()) {
            startActivity(new Intent(getActivity(), WebExperienceReportActivity.class));
//            }
        }else if(i==R.id.account_my_device_relative){
            Intent intent=new Intent(getActivity(),VrSettingActivity.class);
//            intent.putExtra("from",1);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        accountHead.onResume(this);
        //延迟两秒后 再次刷新信息
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                accountHead.updateModouInfo();
            }
        }, 2000);
        super.onResume();
        if (SettingSpBusiness.getInstance().getHasContent()) {
            has_content.setVisibility(View.VISIBLE);
        } else {
            has_content.setVisibility(View.GONE);
        }
        reportPV();
    }

    private void reportPV(){
        ReportPVBean bean=new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("personal");
        ReportBusiness.getInstance().reportPV(bean);
    }

    private boolean isWifiConnected() {
        if (NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE)) {
            return true;
        } else {
            Toast.makeText(getActivity(), R.string.network_exception, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isLogin() {
        if (UserSpBusiness.getInstance().isUserLogin()) {
            return true;
        } else {
            Toast.makeText(getActivity(), R.string.please_login, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setExperienceReportVisibility(int visibility) {
        reportDivider.setVisibility(visibility);
        experienceRelative.setVisibility(visibility);
    }

    /**
     * 检查是否有礼券发放活动
     */
    private void checkIfHasGift() {
        if (!UserSpBusiness.getInstance().isUserLogin()){
            return;//未登录，返回
        }
        new UserInfoApi().getIfHasGift(new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject joresult = new JSONObject(result);
                        if (joresult.getBoolean("status")) { // 成功
                            int time = joresult.getInt("time");
                            int hadget = joresult.getInt("hadget");
                            if (hadget == 0 && time > UserSpBusiness.getInstance().getGiftTime()) {
                                UserSpBusiness.getInstance().setHasNewGift(true);
                            } else {
                                UserSpBusiness.getInstance().setHasNewGift(false);
                            }
                            UserSpBusiness.getInstance().setGiftTime(time);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
        });
    }

    private void checkPermission(){
        CheckPermission.from(getActivity())
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener(){

                    @Override
                    public void permissionGranted() {
                    }

                    @Override
                    public void permissionDenied() {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.CAMERA)) {
                        } else {
                            Toast.makeText(getActivity(),R.string.camera_permission_denied,Toast.LENGTH_SHORT).show();
                        }
                    }
                }).check();
    }

}
