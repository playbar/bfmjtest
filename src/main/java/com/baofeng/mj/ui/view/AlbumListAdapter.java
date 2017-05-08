package com.baofeng.mj.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.SubBean;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hanyang on 2016/6/21.
 * 订阅专辑列表
 */
public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private List<SubBean> mList;
    private int imgWidth;
    private int imgHeight;

    public AlbumListAdapter(Context context, List<SubBean> list) {
        this.context = context;
        this.mList = list;
        imgWidth = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(20);
        imgHeight = (int) (imgWidth / 1.777f);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mList == null || mList.isEmpty()) ? 0 : mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.view_album_item, null);
            viewHolder = new ViewHolder();
            viewHolder.album_name = (TextView) view.findViewById(R.id.album_name);
            viewHolder.album_image = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.album_image));
            viewHolder.album_des = (TextView) view.findViewById(R.id.album_des);
            ViewGroup.LayoutParams layoutParams = viewHolder.album_image.get().getLayoutParams();
            layoutParams.width = imgWidth;
            layoutParams.height = imgHeight;
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, (SubBean) getItem(position));
        return view;
    }

    /**
     * 填充数据
     *
     * @param viewHolder
     * @param data
     */
    private void fillData(ViewHolder viewHolder, SubBean data) {
        //ImageLoader.getInstance().displayImage(data.getAlbum_thumbnail(), viewHolder.album_image, defaultDisplayImageOptions);
        GlideUtil.displayImage(context, viewHolder.album_image, data.getAlbum_thumbnail(), R.drawable.img_default_1n_cross);
        viewHolder.album_name.setText(data.getAlbum_name());
        viewHolder.album_des.setText(data.getAlbum_description());
    }

    class ViewHolder {
        private WeakReference<ImageView> album_image;
        private TextView album_name, album_des;
    }
}
