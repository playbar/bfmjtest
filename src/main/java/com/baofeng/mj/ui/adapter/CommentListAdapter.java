package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.CommentBean;

import java.util.List;

/**
 * Created by hanyang on 2016/5/10.
 */
public class CommentListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommentBean> mList;

    public CommentListAdapter(Context context, List<CommentBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mList == null || mList.isEmpty()) ? 0 : mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.comment_list, null);
            viewHolder = new ViewHolder();
            viewHolder.comment_name = (TextView) view.findViewById(R.id.comment_name);
            viewHolder.comment_score = (RatingBar) view.findViewById(R.id.comment_score);
            viewHolder.comment_content = (TextView) view.findViewById(R.id.comment_content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, (CommentBean) getItem(position));
        return view;
    }

    public void fillData(ViewHolder viewHolder, CommentBean bean) {
        viewHolder.comment_name.setText(bean.getNickname());
        viewHolder.comment_score.setRating(Float.valueOf(bean.getScore()));
        viewHolder.comment_content.setText(bean.getContent());
    }

    class ViewHolder {
        private TextView comment_name, comment_content;
        private RatingBar comment_score;
    }
}
