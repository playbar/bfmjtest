package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.TaskListBean;
import com.baofeng.mj.ui.adapter.TaskListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyang on 2016/5/11.
 */
public class TaskListItem extends RelativeLayout {
    private Context mContext;
    private View rootView;
    private TaskListAdapter taskListAdapter;
    private GridView view_task_list_grid_view;
    private TaskListBean taskListBean;

    public TaskListItem(Context context, TaskListBean taskListBean) {
        super(context);
        this.mContext = context;
        this.taskListBean = taskListBean;
        initView();
    }

    public TaskListItem(Context context, AttributeSet attrs, TaskListBean taskListBean) {
        super(context, attrs);
        this.mContext = context;
        this.taskListBean = taskListBean;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_task_list, null);
        this.addView(rootView);
        view_task_list_grid_view = (GridView) rootView.findViewById(R.id.view_task_list_grid_view);
        taskListAdapter = new TaskListAdapter(mContext, taskListBean.getList());
        view_task_list_grid_view.setAdapter(taskListAdapter);
    }
}
