package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.CustomProgressView;
import com.baofeng.mj.ui.view.EmptyView;

/**页面内有loading、空页面、Title的父页面
 * Created by muyu on 2016/8/17.
 */
public abstract class BaseLoadingActivity extends BaseActivity implements View.OnClickListener{
    protected ViewGroup contentView;
    protected EmptyView emptyView;
    protected RelativeLayout titleBgLayout;
    protected AppTitleBackView titleBackView;
    protected CustomProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseview);
        findViews();
    }

    public void setTitle(String title){
        titleBackView.getNameTV().setText(title);
    }

    private void findViews(){
        contentView = (ViewGroup) findViewById(R.id.base_contentview);
        emptyView = (EmptyView) findViewById(R.id.base_empty_view);
        emptyView.getRefreshView().setOnClickListener(this);

        titleBgLayout = (RelativeLayout) findViewById(R.id.base_title_layout);
        titleBackView = (AppTitleBackView) findViewById(R.id.base_title);

        progressView = (CustomProgressView) findViewById(R.id.base_loading);
        contentView.addView(View.inflate(this, getContentView(), null)); //填充具体内容布局
    }

    /**
     * 显示子类页面
     */
    public void showContentView(){
        contentView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    public void hideTopLine(){
        titleBackView.hideTopLine();
    }

    public void setVRResWhite(){
        titleBackView.setVRResWhite();
    }
    /**
     * 隐藏子类页面
     */
    public void hideContent(){
        contentView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }
    /**
     * ProgressView 隐藏
     */
    public void dismissProgressDialog(){
        progressView.setVisibility(View.GONE);
    }

    /**
     * ProgressView 显示
     */
    public void showProgressDialog(){
        progressView.setVisibility(View.VISIBLE);
    }

    /**
     * ProgressView 显示指定文字
     * @param message
     */
    public void showProgressDialog(String message) {
        progressView.setVisibility(View.VISIBLE);
        progressView.setMessage(message);
    }

    @Override
    public void onClick(View view) {
    }

    protected abstract int getContentView();

}
