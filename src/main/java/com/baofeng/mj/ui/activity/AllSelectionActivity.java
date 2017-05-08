package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.adapter.AllSelectionAdapter;
import com.baofeng.mj.ui.view.BaseGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选集页面
 * Created by muyu on 2016/5/6.
 */
public class AllSelectionActivity extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private static final int PAGE_SIZE = 30;
    private AllSelectionAdapter adapter;

    private TextView titleTV;
    private RadioGroup orderRadioGroup;
    private RadioButton positiveBtn;
    private RadioButton negativeBtn;
    private RadioGroup selectionRadioGroup;
    private ViewPager selectionViewPager;
    private String detailUrl;
    private ArrayList<VideoDetailBean.AlbumsBean.VideosBean> videosBeanList;
    private int tabNum;
    private ImageView backImg;
    private int select_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(null);
        setContentView(R.layout.activity_all_selections);
        initView();
        initData();
    }

    private void initView(){
        titleTV = (TextView) findViewById(R.id.all_selections_title);
        orderRadioGroup = (RadioGroup) findViewById(R.id.all_selections_order_radiogroup);
        positiveBtn = (RadioButton) findViewById(R.id.all_selections_positive_sequence);
        positiveBtn.setOnClickListener(this);
        negativeBtn = (RadioButton) findViewById(R.id.all_selections_negative_sequence);
        negativeBtn.setOnClickListener(this);
        selectionRadioGroup = (RadioGroup) findViewById(R.id.all_selections_radiogroup);
        selectionViewPager = (ViewPager) findViewById(R.id.all_selections_viewpager);
        backImg = (ImageView) findViewById(R.id.all_selection_back);
        backImg.setOnClickListener(this);

    }

    private void initData(){
        VideoDetailBean.AlbumsBean videoDetailBean = null;
        if(getIntent() != null){
//            videoDetailBean = (VideoDetailBean.AlbumsBean) getIntent().getSerializableExtra("videoBean");
            videoDetailBean = VideoDetailActivity.videoBean.getAlbums().get(0);
            detailUrl = getIntent().getStringExtra("detailUrl");
//            contents = getIntent().getStringExtra("contents");
            contents = VideoDetailActivity.videoBean.getLandscape_url().getContents();
//            nav = getIntent().getStringExtra("nav");
            nav = VideoDetailActivity.videoBean.getLandscape_url().getNav();
            select_index = getIntent().getIntExtra("select_index", 0);
        }
        videosBeanList = (ArrayList)videoDetailBean.getVideos();
        float count = videosBeanList.size();
        titleTV.setText("更新至"+ (int)count+"集");
        tabNum = (int) Math.ceil(count / PAGE_SIZE);
        for(int i = 0;i<tabNum;i++){
            final int ii = i;
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.selection_tab_radiobtn, null);
            String show = ( i * PAGE_SIZE + 1 )+"-"+(i+1)*PAGE_SIZE;
            if (i == tabNum - 1){
                show = ( i * PAGE_SIZE + 1)+"-" + (int)count;
            }
            radioButton.setText(show);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectionViewPager.setCurrentItem(ii);
                }
            });
            selectionRadioGroup.addView(radioButton);
        }

        adapter = new AllSelectionAdapter(getSupportFragmentManager(), videoDetailBean, detailUrl, contents, nav, select_index+1);
        selectionViewPager.setAdapter(adapter);
        selectionViewPager.setOnPageChangeListener(this);
        positiveBtn.setChecked(true);
        selectionRadioGroup.check(selectionRadioGroup.getChildAt(0).getId());

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        selectionRadioGroup.check(selectionRadioGroup.getChildAt(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.all_selections_positive_sequence){
            showPositive();
        } else if(id == R.id.all_selections_negative_sequence){
            showNegative();
        } else if(id == R.id.all_selection_back){
            finish();
//            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        }
    }

    private void showPositive(){
        Collections.sort(videosBeanList);
        adapter.setMjList(videosBeanList);

        int count = selectionRadioGroup.getChildCount();//3
        for(int i=0;i<count;i++) {
            String show = (i * PAGE_SIZE + 1) + "-" + (i + 1) * PAGE_SIZE;
            if (i == tabNum - 1) {
                show = (i * PAGE_SIZE + 1) + "-" + videosBeanList.size();
            }
            RadioButton button = (RadioButton) selectionRadioGroup.getChildAt(i);
            button.setText(show);
        }
    }

    private void showNegative(){
        Collections.sort(videosBeanList, Collections.reverseOrder());
        adapter.setMjList(videosBeanList);
        int count = selectionRadioGroup.getChildCount();//3
        for(int i = count-1 ; i >= 0 ;i--){
            int newNum = Math.abs(i-count+1);
            String show = "";
            show = (videosBeanList.size() - (newNum)*PAGE_SIZE)+"-"+(videosBeanList.size() - (newNum +1)*PAGE_SIZE + 1);
            if(i==0){
                show =(videosBeanList.size() - (newNum)*PAGE_SIZE)+"-"+1;
            }
            RadioButton button = (RadioButton) selectionRadioGroup.getChildAt(newNum);
            button.setText(show);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }



}
