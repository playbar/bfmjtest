package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VRModelBean;

import java.util.List;

/**
 * Created by zhaominglei on 2016/6/19.
 */
public class PlayStrategyAdapter extends BaseAdapter {

    private Context mContext;
    private List<VRModelBean> mVideoTypes;
    private VRModelBean mCurrentPlayStategy;

    public PlayStrategyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mVideoTypes == null ? 0 : mVideoTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoTypes == null ? null : mVideoTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.play_strategy_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_video_type = (TextView) view.findViewById(R.id.tv_video_type);
            viewHolder.divider = (View) view.findViewById(R.id.tv_video_type_divider);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, ((VRModelBean) getItem(position)));
        return view;
    }

    private void fillData(ViewHolder viewHolder, VRModelBean type) {
        viewHolder.tv_video_type.setTextColor(type.getName().equals(mCurrentPlayStategy.getName()) ?
                mContext.getResources().getColor(R.color.selected_text_color) :
                mContext.getResources().getColor(R.color.normal_text_color));

        viewHolder.tv_video_type.setText(type.getName());
        viewHolder.divider.setVisibility(type.isShowDivider() ? View.VISIBLE : View.GONE);
    }

    public void setCurrentPlayStrategy(VRModelBean strategy) {
        this.mCurrentPlayStategy = strategy;
    }

    public void setDataSet(List<VRModelBean> dataSet) {
        if (dataSet != null && dataSet.size() > 0) {
            mVideoTypes = dataSet;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        private TextView tv_video_type;
        private View divider;
    }
}
