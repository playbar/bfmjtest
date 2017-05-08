package com.baofeng.mj.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mojing.MojingSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕尺寸选择
 */
public class ResolSettingActivity extends BaseActivity implements View.OnClickListener {
    private AppTitleBackView res_setting_title;
    private RelativeLayout tag_1_btn, tag_2_btn, tag_3_btn, tag_4_btn, tag_5_btn, tag_6_btn,
            tag_7_btn, tag_8_btn, tag_9_btn, tag_10_btn, tag_11_btn, tag_12_btn, tag_13_btn, tag_14_btn;
    private TextView tag_1_img, tag_2_img, tag_3_img, tag_4_img, tag_5_img, tag_6_img,
            tag_7_img, tag_8_img, tag_9_img, tag_10_img, tag_11_img, tag_12_img, tag_13_img, tag_14_img;
    private String resol;
    private List<TextView> views = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_setting);
        initView();
    }

    private void initView() {
        if (getIntent() != null) {
            resol = getIntent().getStringExtra("size");
        }
        res_setting_title = (AppTitleBackView) findViewById(R.id.res_setting_title);
        res_setting_title.getNameTV().setText("手机屏幕尺寸选择");
        res_setting_title.getInvrImgBtn().setVisibility(View.GONE);

        tag_1_btn = (RelativeLayout) findViewById(R.id.tag_1_btn);
        tag_1_img = (TextView) findViewById(R.id.tag_1_img);
        setDefault(tag_1_img, "7.0");
        views.add(tag_1_img);
        tag_1_btn.setOnClickListener(this);

        tag_2_btn = (RelativeLayout) findViewById(R.id.tag_2_btn);
        tag_2_img = (TextView) findViewById(R.id.tag_2_img);
        setDefault(tag_2_img, "6.0");
        views.add(tag_2_img);
        tag_2_btn.setOnClickListener(this);

        tag_3_btn = (RelativeLayout) findViewById(R.id.tag_3_btn);
        tag_3_img = (TextView) findViewById(R.id.tag_3_img);
        setDefault(tag_3_img, "5.5");
        views.add(tag_3_img);
        tag_3_btn.setOnClickListener(this);

        tag_4_btn = (RelativeLayout) findViewById(R.id.tag_4_btn);
        tag_4_img = (TextView) findViewById(R.id.tag_4_img);
        setDefault(tag_4_img, "5.3");
        views.add(tag_4_img);
        tag_4_btn.setOnClickListener(this);

        tag_5_btn = (RelativeLayout) findViewById(R.id.tag_5_btn);
        tag_5_img = (TextView) findViewById(R.id.tag_5_img);
        setDefault(tag_5_img, "5.2");
        views.add(tag_5_img);
        tag_5_btn.setOnClickListener(this);

        tag_6_btn = (RelativeLayout) findViewById(R.id.tag_6_btn);
        tag_6_img = (TextView) findViewById(R.id.tag_6_img);
        setDefault(tag_6_img, "5.1");
        views.add(tag_6_img);
        tag_6_btn.setOnClickListener(this);

        tag_7_btn = (RelativeLayout) findViewById(R.id.tag_7_btn);
        tag_7_img = (TextView) findViewById(R.id.tag_7_img);
        setDefault(tag_7_img, "5.0");
        views.add(tag_7_img);
        tag_7_btn.setOnClickListener(this);

        tag_8_btn = (RelativeLayout) findViewById(R.id.tag_8_btn);
        tag_8_img = (TextView) findViewById(R.id.tag_8_img);
        setDefault(tag_8_img, "4.95");
        views.add(tag_8_img);
        tag_8_btn.setOnClickListener(this);

        tag_9_btn = (RelativeLayout) findViewById(R.id.tag_9_btn);
        tag_9_img = (TextView) findViewById(R.id.tag_9_img);
        setDefault(tag_9_img, "4.8");
        views.add(tag_9_img);
        tag_9_btn.setOnClickListener(this);

        tag_10_btn = (RelativeLayout) findViewById(R.id.tag_10_btn);
        tag_10_img = (TextView) findViewById(R.id.tag_10_img);
        setDefault(tag_10_img, "4.7");
        views.add(tag_10_img);
        tag_10_btn.setOnClickListener(this);

        tag_11_btn = (RelativeLayout) findViewById(R.id.tag_11_btn);
        tag_11_img = (TextView) findViewById(R.id.tag_11_img);
        setDefault(tag_11_img, "5.9");
        views.add(tag_11_img);
        tag_11_btn.setOnClickListener(this);

        tag_12_btn = (RelativeLayout) findViewById(R.id.tag_12_btn);
        tag_12_img = (TextView) findViewById(R.id.tag_12_img);
        setDefault(tag_12_img, "5.8");
        views.add(tag_12_img);
        tag_12_btn.setOnClickListener(this);

        tag_13_btn = (RelativeLayout) findViewById(R.id.tag_13_btn);
        tag_13_img = (TextView) findViewById(R.id.tag_13_img);
        setDefault(tag_13_img, "5.7");
        views.add(tag_13_img);
        tag_13_btn.setOnClickListener(this);

        tag_14_btn = (RelativeLayout) findViewById(R.id.tag_14_btn);
        tag_14_img = (TextView) findViewById(R.id.tag_14_img);
        setDefault(tag_14_img, "5.6");
        views.add(tag_14_img);
        tag_14_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tag_1_btn == id) {
            clickSet(tag_1_img, "7.0");
        } else if (R.id.tag_2_btn == id) {
            clickSet(tag_2_img, "6.0");
        } else if (R.id.tag_3_btn == id) {
            clickSet(tag_3_img, "5.5");
        } else if (R.id.tag_4_btn == id) {
            clickSet(tag_4_img, "5.3");
        } else if (R.id.tag_5_btn == id) {
            clickSet(tag_5_img, "5.2");
        } else if (R.id.tag_6_btn == id) {
            clickSet(tag_6_img, "5.1");
        } else if (R.id.tag_7_btn == id) {
            clickSet(tag_7_img, "5.0");
        } else if (R.id.tag_8_btn == id) {
            clickSet(tag_8_img, "4.95");
        } else if (R.id.tag_9_btn == id) {
            clickSet(tag_9_img, "4.8");
        } else if (R.id.tag_10_btn == id) {
            clickSet(tag_10_img, "4.7");
        } else if (R.id.tag_11_btn == id) {
            clickSet(tag_11_img, "5.9");
        } else if (R.id.tag_12_btn == id) {
            clickSet(tag_12_img, "5.8");
        } else if (R.id.tag_13_btn == id) {
            clickSet(tag_13_img, "5.7");
        } else if (R.id.tag_14_btn == id) {
            clickSet(tag_14_img, "5.6");
        }
    }

    /**
     * 保存用户选择的分辨率
     *
     * @param resSet
     */
    private void setSp(String resSet) {
        SharedPreferences sharedPreferences = getSharedPreferences("size", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("size", resSet);
        editor.commit();
    }

    /**
     * 用户选择尺寸点击事件
     *
     * @param v
     * @param resolu
     */
    private void clickSet(View v, String resolu) {
        if (getResources().getDrawable(R.drawable.path_selected_icon).equals(v.getBackground())) {
            finish();
        } else {
            v.setBackground(getResources().getDrawable(R.drawable.path_selected_icon));
            setDefaulBg(v);
            setSp(resolu);
            MojingSDK mojingSDK = new MojingSDK();
            try {
                JSONObject json = new JSONObject();
                json.put("ClassName", "UserSettingProfile");
                json.put("EnableScreenSize", 1);
                json.put("ScreenSize", Float.valueOf(resolu));
                mojingSDK.SetUserSettings(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    /**
     * 设置勾选与否
     *
     * @param v
     * @param resoluti
     */
    private void setDefault(TextView v, String resoluti) {
        if (resoluti.equals(resol)) {
            v.setBackground(getResources().getDrawable(R.drawable.path_selected_icon));
        } else {
            v.setBackground(null);
        }
    }

    /**
     * 设置背景
     *
     * @param view
     */
    private void setDefaulBg(View view) {
        int id = view.getId();
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i).getId() == id) {
                views.get(i).setBackground(getResources().getDrawable(R.drawable.path_selected_icon));
            } else {
                views.get(i).setBackground(null);
            }
        }
    }
}
