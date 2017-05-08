package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VideoDetailBean;

import java.util.List;

/**
 * 视频选集 GridView 适配器
 * Created by muyu on 2016/5/6.
 */
public class AllSelectionContentAdapter extends BaseAdapter {
	private List<VideoDetailBean.AlbumsBean.VideosBean> mjList;
	private Context context;
	private int selectedIndex = -1;

	public AllSelectionContentAdapter(Context context, List<VideoDetailBean.AlbumsBean.VideosBean> mjList) {
		this.context = context;
		this.mjList = mjList;
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
		return mjList == null ? 0 : mjList.size();
	}

	@Override
	public Object getItem(int position) {
		return mjList == null ? null : mjList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (mjList == null || mjList.isEmpty()) ? 0 : mjList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		final MyGridViewItemViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(R.layout.cube_icon_underline_item, null);
			viewHolder = new MyGridViewItemViewHolder();
			viewHolder.ItemText = (RadioButton) view.findViewById(R.id.cube_icon_radiobtn);
			view.setTag(viewHolder);

		} else {
			view = convertView;
			viewHolder = (MyGridViewItemViewHolder) view.getTag();
		}

		viewHolder.ItemText.setClickable(false);

		VideoDetailBean.AlbumsBean.VideosBean videosBean = mjList.get(position);

		if(selectedIndex == position) {
			viewHolder.ItemText.setChecked(true);
			videosBean.setSeletedStatus(true);
		} else{
			viewHolder.ItemText.setChecked(false);
			videosBean.setSeletedStatus(false);
		}
		//update view holder
		updateViewHolder(viewHolder, (VideoDetailBean.AlbumsBean.VideosBean) getItem(position));
		return view;
	}

	private void updateViewHolder(final MyGridViewItemViewHolder viewHolder, VideoDetailBean.AlbumsBean.VideosBean data) {

		viewHolder.ItemText.setText(data.getSeq()+"");
	}

	public void update(List<VideoDetailBean.AlbumsBean.VideosBean> data) {
		this.mjList = data;
		notifyDataSetChanged();
	}

	public void add(List<VideoDetailBean.AlbumsBean.VideosBean> data) {
		if (this.mjList == null) {
			this.mjList = data;
		} else {
			mjList.clear();
			this.mjList.addAll(data);
		}
		notifyDataSetChanged();
	}

	public void update() {
		notifyDataSetChanged();
	}

	public boolean hasData() {
		return mjList != null;
	}

	class MyGridViewItemViewHolder {
		private RadioButton ItemText;
	}
}
