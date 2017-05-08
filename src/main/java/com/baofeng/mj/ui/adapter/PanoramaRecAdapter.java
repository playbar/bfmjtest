package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;
import com.baofeng.mj.util.publicutil.GlideUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 全景视频推荐View adapter
 * Created by muyu on 2016/12/28.
 */
public class PanoramaRecAdapter extends BaseAdapter{

    private Context mContext;
    private List<ContentInfo> mRecList;
    private LayoutInflater layoutInflater;

    public PanoramaRecAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setDate(List<ContentInfo> recList){
        this.mRecList = recList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRecList == null ? 0 : mRecList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.panorama_rec_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.panorama_rec_img = new WeakReference<ImageView>((ImageView) convertView.findViewById(R.id.panorama_rec_img));
            viewHolder.panorama_rec_name = (TextView) convertView.findViewById(R.id.panorama_rec_name);
            viewHolder.panorama_rec_subname = (TextView) convertView.findViewById(R.id.panorama_rec_subname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GlideUtil.displayImage(mContext, viewHolder.panorama_rec_img, mRecList.get(position).getPic_url(),R.drawable.img_default_2n_cross);
        viewHolder.panorama_rec_name.setText(mRecList.get(position).getTitle());
        viewHolder.panorama_rec_subname.setText(mRecList.get(position).getSubtitle());
        return convertView;
    }

    private class ViewHolder {
        private WeakReference<ImageView> panorama_rec_img;
        private TextView panorama_rec_name, panorama_rec_subname;
    }
}
