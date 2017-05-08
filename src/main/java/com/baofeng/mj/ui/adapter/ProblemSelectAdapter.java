package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ProblemTypeBean;

import java.util.ArrayList;

/**
 * 意见反馈页面问题类型选择适配器
 * Created by yushaochen on 2016/12/28.
 */

public class ProblemSelectAdapter extends BaseAdapter{

    private Context mContext;

    private ArrayList<ProblemTypeBean> problemTypeBeans = new ArrayList();

    public ProblemSelectAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<ProblemTypeBean> beans) {
        if(beans == null) {
            return;
        }
        problemTypeBeans.clear();
        problemTypeBeans.addAll(beans);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return problemTypeBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return problemTypeBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.problem_name_text,null);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.problem_name_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameText.setText(problemTypeBeans.get(position).getName());

        if(problemTypeBeans.get(position).isSelected()) {
            viewHolder.nameText.setTextColor(mContext.getResources().getColor(R.color.white));
            viewHolder.nameText.setBackgroundResource(R.color.problem_text_bg);
        } else {
            viewHolder.nameText.setTextColor(mContext.getResources().getColor(R.color.problem_text_no_selected));
            viewHolder.nameText.setBackgroundResource(R.drawable.help_feedback_line);
        }

        return convertView;
    }

    class ViewHolder {
        TextView nameText;
    }
}
