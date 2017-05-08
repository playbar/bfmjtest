package com.baofeng.mj.ui.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.baofeng.mj.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 清晰度选择adapter
 */

public class PlayDefinitionAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mHDtypes = new ArrayList<String>();
    private int mCurrentPos = 0;
    public PlayDefinitionAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mHDtypes!=null){
            return mHDtypes.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mHDtypes!=null){
            return mHDtypes.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mHDtypes!=null)
            return position;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.popwindow_player_hd_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_video_type = (TextView) view.findViewById(R.id.definition_item_tv);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, ((String) getItem(position)));
        if(position==mCurrentPos){
            viewHolder.tv_video_type.setTextColor(mContext.getResources().getColor(R.color.auxiliary_blue_color));
            }else {
            viewHolder.tv_video_type.setTextColor(mContext.getResources().getColor(R.color.prompt_color));
            }
        return view;
    }

    private void fillData(ViewHolder viewHolder, String type) {

        if(TextUtils.isEmpty(type)){
            return;
        }
        if(!(type.endsWith("k")||type.endsWith("K"))) {
            viewHolder.tv_video_type.setText(type+"P");
        }else {
            viewHolder.tv_video_type.setText(type);
        }


    }


    class ViewHolder {
        private TextView tv_video_type;
    }
    public void setData(List<String> data){
        this.mHDtypes = data;
        notifyDataSetChanged();
    }
    public void setCurrentSelect(int pos){
        this.mCurrentPos = pos;
        notifyDataSetChanged();
    }

}
