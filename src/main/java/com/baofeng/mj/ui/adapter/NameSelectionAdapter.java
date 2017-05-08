package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.VideoDetailBean;

import java.util.ArrayList;
import java.util.List;

/**视频名字显示adapter
 * Created by muyu on 2016/5/31.
 */
public class NameSelectionAdapter extends BaseAdapter {
    private Context mContext;
    private VideoDetailBean.AlbumsBean albumsBean;
    private List<VideoDetailBean.AlbumsBean.VideosBean> videosBeans;
    private int selectedIndex;

    public NameSelectionAdapter(Context context, VideoDetailBean.AlbumsBean albumsBean) {
        this.mContext = context;
        this.albumsBean = albumsBean;
        videosBeans = albumsBean.getVideos();
    }

    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }

    @Override
    public int getCount() {
        return videosBeans == null ? 0 : videosBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return videosBeans == null ? null : videosBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (videosBeans == null || videosBeans.isEmpty()) ? 0 : videosBeans.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.name_selection_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name_selection_item_textview = (RadioButton) view.findViewById(R.id.name_selection_item_textview);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        VideoDetailBean.AlbumsBean.VideosBean videosBean = videosBeans.get(position);
        if(selectedIndex == position) {
            viewHolder.name_selection_item_textview.setChecked(true);
            videosBean.setSeletedStatus(true);
        }else{
            viewHolder.name_selection_item_textview.setChecked(false);
            videosBean.setSeletedStatus(false);
        }

        fillData(viewHolder, (VideoDetailBean.AlbumsBean.VideosBean) getItem(position));
        return view;
    }
    public void fillData(ViewHolder viewHolder, VideoDetailBean.AlbumsBean.VideosBean data) {
        viewHolder.name_selection_item_textview.setText(data.getTitle());
    }
    class ViewHolder {
        private RadioButton name_selection_item_textview;
    }
}
