package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.FeedbackCommitResult;
import com.baofeng.mj.bean.ProblemTypeBean;
import com.baofeng.mj.bean.ReportPVBean;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.adapter.ProblemSelectAdapter;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.BaseGridView;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.HelpAndFeedbackApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;

import java.util.ArrayList;

/**
 * 意见反馈页面
 * Created by yushaochen on 2016/12/28.
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private AppTitleBackView appTitleLayout;//页面标题

    private ScrollView p_scrollView;

    private String[] problemNames = new String[]{"在线播放","本地视频","游戏","头控选择","遥控器","飞屏","充值购买","观影体验","界面功能","其他"};

    private ArrayList<ProblemTypeBean> problemTypeBeans = new ArrayList();//问题类型集合

    private BaseGridView problemGridView;//问题类型显示gridview

    private ProblemSelectAdapter problemSelectAdapter;//问题类型显示适配器

    private EditText content;//反馈问题内容输入框

    private EditText p_phone_num;//联系电话输入框

    private Button p_commit_btn;//意见反馈提交按钮

    private RelativeLayout feedback_commit_result_lay;//意见反馈提交结果显示view

    private static final int MAX_LENGH = 4;//反馈问题内容最少字符个数限制

    private long pre_time = System.currentTimeMillis();

    private String mFrom = "";//跳转页面的来源
    private  Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Intent intent = getIntent();
        if(intent!=null){
            mFrom = intent.getStringExtra("from");
        }
        initView();
        initData();
        initListener();
    }

    private void initView() {
        appTitleLayout = (AppTitleBackView) findViewById(R.id.feedback_title_layout);
        appTitleLayout.getNameTV().setText("意见反馈");
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        appTitleLayout.getAppTitleRight().setText("关闭");
        appTitleLayout.getAppTitleRight().setVisibility(View.INVISIBLE);

        p_scrollView = (ScrollView) findViewById(R.id.p_scrollView);

        problemGridView = (BaseGridView) findViewById(R.id.feedback_problem_gridview);

        content = (EditText) findViewById(R.id.content);

        p_phone_num = (EditText) findViewById(R.id.p_phone_num);

        p_commit_btn = (Button) findViewById(R.id.p_commit_btn);

        feedback_commit_result_lay = (RelativeLayout) findViewById(R.id.feedback_commit_result_lay);
        feedback_commit_result_lay.setVisibility(View.GONE);
    }

    private void initData() {
        problemTypeBeans.clear();
        for(String name : problemNames) {
            ProblemTypeBean problemTypeBean = new ProblemTypeBean();
            problemTypeBean.setName(name);
            problemTypeBean.setSelected(false);
            problemTypeBeans.add(problemTypeBean);
        }

        problemSelectAdapter = new ProblemSelectAdapter(getApplicationContext());
        problemGridView.setAdapter(problemSelectAdapter);
        /*add by whf 20170222  添加从镜片选择页跳转来时默认其他为选择状态*/
        if(!TextUtils.isEmpty(mFrom)&&mFrom.equals("glasses_view")){
            if(problemTypeBeans!=null&&problemTypeBeans.size()>0){
                problemTypeBeans.get(problemTypeBeans.size()-1).setSelected(true);
            }
            String conentStr = getResources().getString(R.string.glasses_feedback_content);
            content.setText(conentStr);
            content.setSelection(conentStr.length());
            content.setFocusable(true);
            content.setFocusableInTouchMode(true);
            content.requestFocus();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showKeyBoard();
                }
            },500);
        }
        problemSelectAdapter.setData(problemTypeBeans);
    }



    private void initListener() {
        appTitleLayout.getBackImgBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    hiddenKeyBoard(content);
                    hiddenKeyBoard(p_phone_num);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },300);

            }
        });
        appTitleLayout.getAppTitleRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(200);
                finish();
            }
        });
        problemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(R.id.problem_name_text);
//                if(problemTypeBeans.get(position).isSelected()) {
//                    textView.setTextColor(getResources().getColor(R.color.problem_text_no_selected));
//                    textView.setBackgroundResource(R.drawable.help_feedback_line);
//                    problemTypeBeans.get(position).setSelected(false);
//                } else {
//                    textView.setTextColor(getResources().getColor(R.color.white));
//                    textView.setBackgroundResource(R.color.problem_text_bg);
//                    problemTypeBeans.get(position).setSelected(true);
//                }

                if(problemTypeBeans.get(position).isSelected()) {
                    problemTypeBeans.get(position).setSelected(false);
                } else {
                    for(ProblemTypeBean problemTypeBean : problemTypeBeans){
                        problemTypeBean.setSelected(false);
                    }
                    problemTypeBeans.get(position).setSelected(true);
                }
                problemSelectAdapter.notifyDataSetChanged();
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkContent()) {
                    p_commit_btn.setTextColor(getResources().getColor(R.color.problem_commit_btn_bg));
                    p_commit_btn.setBackgroundResource(R.drawable.feedback_commit_btn_bg);
                } else {
                    p_commit_btn.setTextColor(getResources().getColor(R.color.problem_no_commit_btn_bg));
                    p_commit_btn.setBackgroundResource(R.drawable.feedback_no_commit_btn_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        p_commit_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.p_commit_btn == id) {
            if(!checkContent()) {
                //Toast.makeText(this,"内容至少四个字哦",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!checkPhoneNum()) {
                return;
            }
            if(!checkNetwork()) {
                return;
            }

            hiddenKeyBoard(content);
            hiddenKeyBoard(p_phone_num);

            String p_type = "";
            for(ProblemTypeBean problemTypeBean : problemTypeBeans){
                if(problemTypeBean.isSelected()) {
                    p_type = problemTypeBean.getName();
                }
            }

            //防止快速点击提交多次
            long nowTime = System.currentTimeMillis();
            if(nowTime - pre_time >2000) {
                pre_time = nowTime;
            } else {
                return;
            }
            //提交反馈
            commit(content.getText().toString().trim(), p_phone_num.getText().toString().trim(), p_type);
        }
    }

    /**
     * 请求服务器
     * @param p_content
     * @param phone_num
     * @param p_type
     */
    private void commit(String p_content, String phone_num, String p_type) {
        new HelpAndFeedbackApi().feedbackCommit(this, p_content, phone_num, p_type, new ApiCallBack<FeedbackCommitResult>() {
            @Override
            public void onSuccess(FeedbackCommitResult result) {
                if(null != result) {
                    String code = result.getCode();
                    if("0".equals(code)) {
                        commitResult(true);
                    } else {
                        commitResult(false);
                    }
                } else {
                    commitResult(false);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                commitResult(false);
            }
        });
    }

    /**
     * 隐藏某个输入框当前调出的输入法
     * @param editText
     */
    private void hiddenKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void showKeyBoard(){
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(content,InputMethodManager.SHOW_FORCED);
    }
    private boolean isKeyBoardShowing(){
        return  (getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 提交结果显示
     * @param result 提交成功 true 提交失败 false
     */
    public void commitResult(boolean result) {
        if(result) {
            appTitleLayout.getAppTitleRight().setVisibility(View.VISIBLE);
            feedback_commit_result_lay.setVisibility(View.VISIBLE);
            p_scrollView.setVisibility(View.INVISIBLE);
            /*add by whf 20170222  如果是从眼镜选择页跳转来的 反馈成功后隐藏返回键 只显示关闭按钮*/
            if(!TextUtils.isEmpty(mFrom)&&mFrom.equals("glasses_view")){
                appTitleLayout.getBackImgBtn().setVisibility(View.INVISIBLE);
            }
        } else {
            appTitleLayout.getAppTitleRight().setVisibility(View.INVISIBLE);
            feedback_commit_result_lay.setVisibility(View.GONE);
            p_scrollView.setVisibility(View.VISIBLE);
            Toast.makeText(this,"服务器连接异常,请重新点击提交",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否可以提交反馈内容
     * @return
     */
    private boolean checkContent() {
        String ct = content.getText().toString().trim();
        if(!TextUtils.isEmpty(ct)) {
            int num = ct.length();
            if(num >= MAX_LENGH) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 联系号码校验，产品要求非必须提交，所以可以为空
     * @return
     */
    private boolean checkPhoneNum() {
        String num = p_phone_num.getText().toString().trim();
        if (!TextUtils.isEmpty(num) && !Common.isMobile(num)) {
            Toast.makeText(this,"请填写正确的手机号",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检测网络
     * @return
     */
    private boolean checkNetwork() {
        if(!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this,"当前网络已断开",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportPV();
    }

    /**
     * pv报数
     */
    private void reportPV(){
        ReportPVBean bean=new ReportPVBean();
        bean.setEtype("pv");
        bean.setTpos("1");
        bean.setPagetype("feedback");
        ReportBusiness.getInstance().reportPV(bean);
    }
}
