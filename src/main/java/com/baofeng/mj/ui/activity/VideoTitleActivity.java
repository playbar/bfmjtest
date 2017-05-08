package com.baofeng.mj.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.bean.MainTabBean;
import com.baofeng.mj.ui.adapter.CirlceIconGridViewAdapter;
import com.baofeng.mj.ui.adapter.MainCirlceIconGridViewAdapter;

import java.util.List;

/**
 * Created by muyu on 2017/4/5.
 */
public class VideoTitleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private GridView titleGridView;
    private MainCirlceIconGridViewAdapter adapter;
    private MainTabBean<List<MainSubTabBean>> data;
    private ImageButton closeImageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        getIntentData();
        initView();
    }

    private void getIntentData(){
        if(getIntent() != null && getIntent().getSerializableExtra("pages") != null){
            data = (MainTabBean<List<MainSubTabBean>>) getIntent().getSerializableExtra("pages");
        }
    }

    private void initView(){
        titleGridView = (GridView) findViewById(R.id.title_view_grid);
        adapter = new MainCirlceIconGridViewAdapter(this, data.getPages());
        titleGridView.setAdapter(adapter);
        titleGridView.setOnItemClickListener(this);

        closeImageBtn = (ImageButton) findViewById(R.id.title_close);
        closeImageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.title_close){
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("position",position);
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }
}
