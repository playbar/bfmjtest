package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.HistoryInfo;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.publicutil.ResTypeUtil;
import com.baofeng.mj.util.publicutil.VideoTypeUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hanyang on 2016/5/13.
 */
public class ViewHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<HistoryInfo> historyList;
    private LayoutInflater layoutInflater;

    public ViewHistoryAdapter(Context context, List<HistoryInfo> historyList) {
        this.mContext = context;
        this.historyList = historyList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return historyList == null ? 0 : historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.history_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.video_history_img = new WeakReference<ImageView>((ImageView) convertView.findViewById(R.id.video_history_img));
            viewHolder.video_tag = (ImageView) convertView.findViewById(R.id.video_tag);
            viewHolder.video_name = (TextView) convertView.findViewById(R.id.video_name);
            viewHolder.video_state = (TextView) convertView.findViewById(R.id.video_state);
            viewHolder.video_last_set = (TextView) convertView.findViewById(R.id.video_last_set);
            viewHolder.history_line = convertView.findViewById(R.id.history_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HistoryInfo historyInfo = historyList.get(position);
        viewHolder.video_name.setText(historyInfo.getVideoTitle());
        //ImageLoaderUtils.getInstance().getImageLoader().displayImage(historyInfo.getVideoImg(), viewHolder.video_history_img, ImageLoaderUtils.getInstance().getImgOptionsThreeCross());//图标
        GlideUtil.displayImage(mContext, viewHolder.video_history_img, historyInfo.getVideoImg(), R.drawable.img_default_3n_cross);
        int playDuration = historyInfo.getPlayDuration();//当前播放时长
        int totalDuration = historyInfo.getTotalDuration();//视频总时长
        if(playDuration == 0){
            viewHolder.video_state.setText("");
        }else if(playDuration == totalDuration){
            viewHolder.video_state.setText("已看完");
        }else{
            viewHolder.video_state.setText("已看  " + getPlayProgress(playDuration, totalDuration) + "%");
        }

        int lastSetIndex = historyInfo.getLastSetIndex();//当前播放第几集
        if(lastSetIndex == 0){
            viewHolder.video_last_set.setText("");
        }else{
            viewHolder.video_last_set.setText("观看到" + (lastSetIndex + 1) + "集");
        }

        boolean videoIsVR = ResTypeUtil.isPanoramaVideo(historyInfo.getResType());
        boolean videoIs3D = VideoTypeUtil.videoIs3D(String.valueOf(historyInfo.getVideo3dType()));
        if(videoIsVR && videoIs3D){//VR，3D
            viewHolder.video_tag.setVisibility(View.VISIBLE);
            viewHolder.video_tag.setImageResource(R.drawable.video_vr_3d);
        }else if(videoIsVR){//VR
            viewHolder.video_tag.setVisibility(View.VISIBLE);
            viewHolder.video_tag.setImageResource(R.drawable.video_vr);
        }else if(videoIs3D){//3D
            viewHolder.video_tag.setVisibility(View.VISIBLE);
            viewHolder.video_tag.setImageResource(R.drawable.video_3d);
        }else{
            viewHolder.video_tag.setVisibility(View.GONE);
        }

        if (position == historyList.size() - 1) {
            viewHolder.history_line.setVisibility(View.GONE);
        } else {
            viewHolder.history_line.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    /**
     * 获取播放进度
     */
    private int getPlayProgress(int playDuration, int totalDuration) {
        if(totalDuration == 0){
            return 0;
        }
        float progress = (float) playDuration  * 100 / totalDuration;
        return (int) (progress > 100 ? 100 : progress);
    }

    private class ViewHolder {
        private WeakReference<ImageView> video_history_img;
        private ImageView video_tag;
        private TextView video_name, video_state, video_last_set;
        private View history_line;
    }
}
