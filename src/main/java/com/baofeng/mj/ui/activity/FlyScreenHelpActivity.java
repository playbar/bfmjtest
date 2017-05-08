package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.FlyScreenHelpBean;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.ui.adapter.FlyScreenHelpAdapter;
import com.baofeng.mj.ui.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class FlyScreenHelpActivity extends BaseActivity implements OnClickListener {

    private TitleBar mTitleBar;
    private ListView mHelpList;
    private FlyScreenHelpAdapter mFlyScreenHelpAdapter;
    private RelativeLayout mFlyScreenGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_screen_help);
        findViewByIds();
        init();
    }

    private void findViewByIds() {
        mTitleBar = (TitleBar) findViewById(R.id.rl_title_bar);
        mTitleBar.setTitleBarTitle(getResources().getString(R.string.fly_screen_help));
        mHelpList = (ListView) findViewById(R.id.lv_fly_screen_help_list);
        mFlyScreenGuide = (RelativeLayout) findViewById(R.id.rl_fly_screen_guide);
    }

    private void init() {
        mTitleBar.setOnClickListener(this);
        mFlyScreenGuide.setOnClickListener(this);
        mFlyScreenHelpAdapter = new FlyScreenHelpAdapter(this);
        mHelpList.setAdapter(mFlyScreenHelpAdapter);
        String[] questions = getResources().getStringArray(R.array.fly_screen_questions);
        String[] answers = getResources().getStringArray(R.array.fly_screen_answers);
        List<FlyScreenHelpBean> flyScreenHelpBeans = new ArrayList<FlyScreenHelpBean>();
        for (int i = 0; i < questions.length; i++) {
            FlyScreenHelpBean item = new FlyScreenHelpBean();
            item.setQuestion(questions[i]);
            item.setAnswer(answers[i]);
            flyScreenHelpBeans.add(item);
        }
        mFlyScreenHelpAdapter.setDataset(flyScreenHelpBeans);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back) {
            finish();
        } else if (vid == R.id.rl_fly_screen_guide) {
            FlyScreenBusiness.getInstance().setSkipGuide(false);
            finish();
        }
    }
}
