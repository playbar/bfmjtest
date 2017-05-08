package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.bean.MainSubTabBean;
import com.baofeng.mj.util.publicutil.GlideUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * GridView 适配器
 * Created by muyu on 2017/4/5.
 */
public class MainCirlceIconGridViewAdapter extends BaseAdapter {
	private List<MainSubTabBean> mjList;
	private Context context;
	private Fragment fragment;

	public MainCirlceIconGridViewAdapter(Context context,List<MainSubTabBean> mjList) {
		this.context = context;
		this.mjList = mjList;
	}

	public MainCirlceIconGridViewAdapter(Context context, Fragment fragment, List<MainSubTabBean> mjList) {
		this.context = context;
		this.fragment = fragment;
		this.mjList = mjList;
	}

	@Override
	public int getCount() {
		return mjList == null ? 0 : mjList.size();
	}

	@Override
	public MainSubTabBean getItem(int position) {
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
			view = layoutInflater.inflate(R.layout.circle_icon_item, null);
			viewHolder = new MyGridViewItemViewHolder(); 
			viewHolder.ItemImage = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.iconitem_icon_iv));
			viewHolder.ItemText = (TextView) view.findViewById(R.id.iconitem_name_tv);
			view.setTag(viewHolder);

		} else {
			view = convertView;
			viewHolder = (MyGridViewItemViewHolder) view.getTag();
		}

		//update view holder
		updateViewHolder(viewHolder, (MainSubTabBean) getItem(position));
		return view;
	}

	private void updateViewHolder(final MyGridViewItemViewHolder viewHolder, MainSubTabBean data) {
		viewHolder.ItemText.setText(data.getTitle());
		//ImageLoaderUtils.getInstance().getImageLoader().displayImage(data.getPic_url(),viewHolder.ItemImage, ImageLoaderUtils.getInstance().getImgOptionsClassification());
		if(fragment == null){
			GlideUtil.displayImage(context, viewHolder.ItemImage, data.getIcon(), R.drawable.img_default_icon_classification);
		}else{
			GlideUtil.displayImage(fragment, viewHolder.ItemImage, data.getIcon(), R.drawable.img_default_icon_classification);
		}
	}

	public void update(List<MainSubTabBean> data) {
		this.mjList = data;
		notifyDataSetChanged();
	}

	public void add(List<MainSubTabBean> data) {
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
		private WeakReference<ImageView> ItemImage;
		private TextView ItemText;
	}
}
