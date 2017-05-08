package com.baofeng.mj.ui.activity;

import android.os.Bundle;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;

/**
 * 我的账户页面
 * Created by muyu on 2016/5/11.
 */
public class AccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportPV();
    }

    private void reportPV(){
        ReportPVBean bean=new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("personal");
        ReportBusiness.getInstance().reportPV(bean);
    }
}
