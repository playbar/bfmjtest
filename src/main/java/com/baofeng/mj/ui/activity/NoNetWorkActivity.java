package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;

/**网络无连接页面
 * Created by muyu on 2016/6/20.
 */
public class NoNetWorkActivity extends BaseActivity implements View.OnClickListener{

    private AppTitleBackView backView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);
        initView();
    }

    private void initView(){
        backView = (AppTitleBackView) findViewById(R.id.network_title_layout);
        backView.getInvrImgBtn().setVisibility(View.GONE);
        backView.getNameTV().setText("网络无连接");
        findViewById(R.id.no_network_help).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.no_network_help){
            //ChatUitl.getInstance(this).startChat(UserSpBusiness.getInstance().getNickName(),UserSpBusiness.getInstance().getUid());
            startActivity(new Intent(this, FeedbackActivity.class));
        }
    }
}
