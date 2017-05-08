package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.CommentBean;
import com.baofeng.mj.bean.CommentListBean;
import com.baofeng.mj.ui.adapter.CommentListAdapter;

import java.util.List;

/**
 * Created by hanyang on 2016/5/10.
 * 评论列表页
 */
public class CommentListItem extends LinearLayout {
    private Context mContext;
    private View rootView;
    private GridView gridView;
    private CommentListAdapter commentListAdapter;
    private List<CommentBean> listBean;

    public CommentListItem(Context context, List<CommentBean> beans) {
        super(context);
        this.mContext = context;
        this.listBean = beans;
        initView();
    }

    public CommentListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_comment, null);
        this.addView(rootView);
        gridView = (GridView) rootView.findViewById(R.id.view_comment_list_grid_view);
        commentListAdapter = new CommentListAdapter(mContext, listBean);
        gridView.setAdapter(commentListAdapter);
    }
}
