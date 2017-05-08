package com.baofeng.mj.ui.popwindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.ui.adapter.DefinitionAdapter;
import com.baofeng.mj.ui.adapter.VideoDefinitionAdapter;

import java.util.List;

/**
 * 下载选择高清标清 PopWindow
 * Created by muyu on 2017/2/9.
 */
public class VideoDefinitionPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View contentView;
    private  List<VideoDetailBean.AlbumsBean> albums;
    private ListView videoListView;
    private VideoDefinitionAdapter adapter;
    private TextView cancelTV;
    private OnItemClickCallback onItemClickCallback;

    public VideoDefinitionPopWindow(Context context, VideoDetailBean videoDetailBean) {
        super(context);
        this.mContext = context;
        this.albums = videoDetailBean.getAlbums();
        initView();
    }

    public VideoDefinitionPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public VideoDefinitionPopWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwindow_definition, null);
        setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        videoListView = (ListView) contentView.findViewById(R.id.pop_definition_listview);
        cancelTV = (TextView) contentView.findViewById(R.id.definition_cancel_textview);
        cancelTV.setOnClickListener(this);
        adapter = new VideoDefinitionAdapter(mContext, albums);
        videoListView.setAdapter(adapter);
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickCallback != null) {
                    onItemClickCallback.onItemClick(albums.get(position));
                }
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.definition_cancel_textview) {
            dismiss();
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(VideoDetailBean.AlbumsBean album);
    }
}