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
import com.baofeng.mj.business.spbusiness.SettingSpBusiness;
import com.baofeng.mj.ui.adapter.DefinitionAdapter;

import java.util.List;

/**
 * 下载选择高清标清 PopWindow
 * Created by muyu on 2016/5/5.
 */
public class DefinitionPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View contentView;
    private List<PanoramaVideoAttrs> videoAttrses;
    private ListView videoListView;
    private DefinitionAdapter adapter;
    private TextView cancelTV;
    private OnItemClickCallback onItemClickCallback;

    public DefinitionPopWindow(Context context, PanoramaVideoBean panoramaVideoBean) {
        super(context);
        this.mContext = context;
        this.videoAttrses = panoramaVideoBean.getVideo_attrs();
        initView();
    }

    public DefinitionPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DefinitionPopWindow(Context context, AttributeSet attrs, int defStyle) {
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
        adapter = new DefinitionAdapter(mContext, videoAttrses);
        videoListView.setAdapter(adapter);
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickCallback != null) {
                    onItemClickCallback.onItemClick(videoAttrses.get(position));
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
        void onItemClick(PanoramaVideoAttrs panoramaVideoAttrs);
    }
}