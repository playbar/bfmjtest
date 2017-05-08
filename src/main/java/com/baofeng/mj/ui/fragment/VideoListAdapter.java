
package com.baofeng.mj.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;

/**
 * 视频教程中ListView的适配器
 * 
 * @author muyu
 */
public class VideoListAdapter extends BaseAdapter {

    private Context mContext;
    private String[] videoTitles;
    private LayoutInflater mInflater;
    private int[] drawablePic;
    private int itemid;
    

    public VideoListAdapter(Context context, int itemid, String[] videoTitles, int[] drawablePic) {
        mContext = context;
        this.itemid = itemid;
        this.videoTitles = videoTitles;
        this.drawablePic = drawablePic;
    }

    @Override
    public int getCount() {
        return videoTitles.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        mInflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_video_list, null, false);
            holder = new ViewHolder();
            holder.item_video_textview = (TextView) convertView.findViewById(R.id.item_video_textview);
            holder.item_video_bg_grey = (ImageView) convertView.findViewById(R.id.item_video_bg_grey);
            holder.item_video_image = (ImageView) convertView.findViewById(R.id.item_video_bg_image);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_video_textview.setText(videoTitles[position]);
        if(itemid == 1){  //三代有蒙版
            holder.item_video_bg_grey.setBackgroundResource(R.drawable.grey_darkgrey_bg_selector);
        } else {
            holder.item_video_bg_grey.setBackgroundResource(R.drawable.transparent_darkgrey_bg_selector);
        }
        holder.item_video_image.setBackgroundResource(drawablePic[position]);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return videoTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public ImageView item_video_image;
        public ImageView item_video_bg_grey;
        public TextView item_video_textview;
    }
}
