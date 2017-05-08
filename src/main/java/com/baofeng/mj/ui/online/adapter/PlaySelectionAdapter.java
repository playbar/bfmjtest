package com.baofeng.mj.ui.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放选集adapter
 */

public class PlaySelectionAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mDatas = new ArrayList<String>();
    public int mCurrentPos = 0;
    private boolean isTvLeft = false;
    public PlaySelectionAdapter(Context context) {
        mContext = context;
    }
    public void setTextLeft(boolean isLeft){
        isTvLeft = isLeft;
    }

    @Override
    public int getCount() {
        if(mDatas!=null){
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mDatas!=null){
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mDatas!=null)
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
            if(isTvLeft) {
                RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.leftMargin = PixelsUtil.dip2px(20);
                params.rightMargin = PixelsUtil.dip2px(20);
                params.height = PixelsUtil.dip2px(40);
                viewHolder.tv_video_type.setLayoutParams(params);
                viewHolder.tv_video_type.setGravity(Gravity.CENTER_VERTICAL);
            }

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

        viewHolder.tv_video_type.setText(type);

    }


    class ViewHolder {
        private TextView tv_video_type;
    }
    public void setData(List<String> data){
        this.mDatas = data;
        notifyDataSetChanged();
    }
    public void setCurrentSelect(int pos){
        this.mCurrentPos = pos;
        notifyDataSetChanged();
    }

}
