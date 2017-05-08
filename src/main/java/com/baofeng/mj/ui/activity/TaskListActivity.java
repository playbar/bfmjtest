package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.TaskListBean;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.AppTitleBackView;
import com.baofeng.mj.ui.view.TaskListItem;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;

/**
 * 任务列表
 */
public class TaskListActivity extends BaseActivity {
    private AppTitleBackView appTitleLayout;
    private LinearLayout task_list_layout;
    private LinearLayout no_task;
    private TextView no_task_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        appTitleLayout = (AppTitleBackView) findViewById(R.id.task_title_layout);
        appTitleLayout.getNameTV().setText(LanguageValue.getInstance().getValue(this, "SID_TASK"));
        appTitleLayout.getInvrImgBtn().setVisibility(View.GONE);
        task_list_layout = (LinearLayout) findViewById(R.id.task_list_layout);
        no_task = (LinearLayout) findViewById(R.id.no_task);
        no_task_tag=(TextView)findViewById(R.id.no_task_tag);
        no_task_tag.setText(LanguageValue.getInstance().getValue(this, "SID_NO_TASK"));
        getTaskList();
    }

    /**
     * 获取任务列表
     */
    private void getTaskList() {
        String userId = UserSpBusiness.getInstance().getUserInfo().getUid();
        String versionCode = ApkUtil.getVersionNameSuffix();
        new UserInfoApi()
                .getTaskList(this, userId, "mjapk", versionCode, new ApiCallBack<String>() {
                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                    }

                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        if (TextUtils.isEmpty(result)) {
                            Toast.makeText(TaskListActivity.this, "获取任务失败", Toast.LENGTH_SHORT).show();
                        } else {
                            TaskListBean json = JSON.parseObject(result, new TypeReference<TaskListBean>() {
                            });
                            task_list_layout.removeAllViews();
                            if (json.isStatus()) {
                                if (json.getList() != null) {
                                    if (json.getList().size() == 0) {
                                        task_list_layout.setVisibility(View.GONE);
                                        no_task.setVisibility(View.VISIBLE);
                                        return;
                                    } else {
                                        task_list_layout.setVisibility(View.VISIBLE);
                                        no_task.setVisibility(View.GONE);
                                        TaskListItem taskListItem = new TaskListItem(TaskListActivity.this, json);
                                        task_list_layout.addView(taskListItem);
                                    }
                                } else {
                                    task_list_layout.setVisibility(View.GONE);
                                    no_task.setVisibility(View.VISIBLE);
                                    return;
                                }
                            } else {
                                Toast.makeText(TaskListActivity.this, "获取任务失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
