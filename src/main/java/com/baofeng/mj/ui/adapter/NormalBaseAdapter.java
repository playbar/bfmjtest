package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * ClassName: NorlmalBaseAdapter <br/>
 * @author qiguolong
 * @param <T>
 * @date: 2015-8-31 下午6:20:10 <br/>
 * @description:
 */
public abstract class NormalBaseAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List dataList;
	protected int layid;

	public NormalBaseAdapter(List<T> datas, Context context, int LayoutId) {
		mContext = context;
		dataList = datas;
		layid = LayoutId;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			convertView = layoutInflater.inflate(layid, null);
			convertView.setTag(initViewHolder(convertView));
		} else {

		}
		handleViewholder(convertView.getTag(), position, convertView);
		return convertView;
	}

	/**
	 * @author qiguolong @Date 2015-9-2 上午10:18:09
	 * @description:{初始化viwholder
	 * @param convertView
	 * @return
	 */
	protected abstract Object initViewHolder(View convertView);

	/**
	 * @author qiguolong @Date 2015-9-2 上午10:18:59
	 * @description:{处理viewholder
	 * @param viewholder
	 * @param pos
	 * @param convertView
	 */
	protected abstract void handleViewholder(Object viewholder, int pos,
			View convertView);

	/**
	 * @return the dataList
	 */
	public List<T> getDataList() {
		return dataList;
	}

	/**
	 * @param dataList the dataList to set
	 */
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public T getData(int pos) {
		return (T) dataList.get(pos);
	}

	protected View getView(View conview, int id) {
		return conview.findViewById(id);
	}

	protected TextView getTextView(View conview, int id) {
		return (TextView) getView(conview, id);
	}

	protected ImageView getImageView(View conview, int id) {
		return (ImageView) getView(conview, id);
	}
}
