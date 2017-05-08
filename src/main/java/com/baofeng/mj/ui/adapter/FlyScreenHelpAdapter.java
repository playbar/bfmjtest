package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.FlyScreenHelpBean;

import java.util.List;

/**
 * Created by zhaominglei on 2016/6/6.
 * 飞屏帮助页列表
 */
public class FlyScreenHelpAdapter extends BaseAdapter {
    private Context mContext;
    private List<FlyScreenHelpBean> mList;

    public FlyScreenHelpAdapter(Context context) {
        this.mContext = context;
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
            view = layoutInflater.inflate(R.layout.fly_screen_help_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_fly_screen_question = (TextView) view.findViewById(R.id.tv_fly_screen_question);
            viewHolder.tv_fly_screen_answer = (TextView) view.findViewById(R.id.tv_fly_screen_answer);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, mList.get(position));
        return view;
    }

    public void fillData(ViewHolder viewHolder, FlyScreenHelpBean data) {
        viewHolder.tv_fly_screen_question.setText(data.getQuestion());
        viewHolder.tv_fly_screen_answer.setText(data.getAnswer());
    }

    public void setDataset(List<FlyScreenHelpBean> dataset) {
        this.mList = dataset;
        notifyDataSetChanged();
    }

    class ViewHolder {
        private TextView tv_fly_screen_question, tv_fly_screen_answer;
    }
}
