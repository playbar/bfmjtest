package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.RechargeBean;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.ui.adapter.RechargeRecordAdapter;
import com.baofeng.mj.ui.view.TitleBar;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;

import java.util.List;

/**
 * 充值记录界面
 */
public class RechargeRecordActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_no_recharge;
    private ListView lv_recharge_record;
    private ImageView iv_no_recharge;
    private TextView tv_no_recharge;
    private TitleBar titleBar;
    private RechargeRecordAdapter rechargeRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
        findViewByIds();
        init();
    }

    private void findViewByIds() {
        rl_no_recharge = (RelativeLayout) findViewById(R.id.rl_no_recharge);
        iv_no_recharge = (ImageView) findViewById(R.id.iv_no_recharge);
        tv_no_recharge = (TextView) findViewById(R.id.tv_no_recharge);
        lv_recharge_record = (ListView) findViewById(R.id.lv_recharge_record);
        titleBar = (TitleBar) findViewById(R.id.rl_recharge_record_title);
    }

    private void init() {
        rechargeRecordAdapter = new RechargeRecordAdapter(this);
        lv_recharge_record.setAdapter(rechargeRecordAdapter);
        titleBar.setTitleBarTitle(LanguageValue.getInstance().getValue(this, "SID_RECHARGE_LOG"));
        titleBar.setOnClickListener(this);
        titleBar.getRightTv().setVisibility(View.GONE);
        if (NetworkUtil.isNetworkConnected(this)) {
            rl_no_recharge.setVisibility(View.GONE);
            lv_recharge_record.setVisibility(View.VISIBLE);
        } else {
            wifiDisconnect();
        }
        queryRechargeRecord();
    }

    private void queryRechargeRecord() {
        new UserInfoApi().queryRechargeRecord(new ApiCallBack<Response<List<RechargeBean>>>() {
            @Override
            public void onSuccess(Response<List<RechargeBean>> result) {
                super.onSuccess(result);
                List<RechargeBean> data = result.data;
                if (data == null || data.size() <= 0) {
                    rl_no_recharge.setVisibility(View.VISIBLE);
                    lv_recharge_record.setVisibility(View.GONE);
                    iv_no_recharge.setImageResource(R.drawable.no_recharge_record);
                    tv_no_recharge.setText(getResources().getString(R.string.no_recharge_record));
                } else {
                    rechargeRecordAdapter.setDatas(data);
                    rl_no_recharge.setVisibility(View.GONE);
                    lv_recharge_record.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (!NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE)) {
                    lv_recharge_record.setVisibility(View.GONE);
                    wifiDisconnect();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissProgressDialog();
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("正在加载...");
            }

            @Override
            public void onCache(Response<List<RechargeBean>> result) {
                super.onCache(result);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

    private void wifiDisconnect() {
        iv_no_recharge.setImageResource(R.drawable.wifi_disconnect);
        tv_no_recharge.setText(getResources().getString(R.string.network_exception));
        rl_no_recharge.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back) {
            finish();
        }
    }
}
