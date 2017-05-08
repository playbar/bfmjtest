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

import com.baofeng.mj.R;
import com.baofeng.mj.bean.VRModelBean;
import com.baofeng.mj.ui.adapter.PlayStrategyAdapter;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.util.List;

/**
 *
 */
public class PlayStrategyPopupWindow extends PopupWindow {

    private Context mContext;

    private ListView videoTypeList;

    private PlayStrategyAdapter mPlayStrategyAdapter;

    private List<VRModelBean> mVideoTypes;

    private OnItemClickListener mOnItemClickListener;

    public PlayStrategyPopupWindow(Context context, List<VRModelBean> videoTypes) {
        super(context);
        this.mContext = context;
        this.mVideoTypes = videoTypes;
        initView();
    }

    public PlayStrategyPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popup_window_play_strategy, null);
        setContentView(contentView);
        //setAnimationStyle(R.style.AnimationFade);
        setWidth(PixelsUtil.dip2px(120f));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        videoTypeList = (ListView) contentView.findViewById(R.id.video_type_list);
        mPlayStrategyAdapter = new PlayStrategyAdapter(mContext);
        videoTypeList.setAdapter(mPlayStrategyAdapter);
        mPlayStrategyAdapter.setDataSet(mVideoTypes);
        videoTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                    dismiss();
                }
            }
        });

    }

    public void setCurrentPlayStrategy(VRModelBean strategy) {
        mPlayStrategyAdapter.setCurrentPlayStrategy(strategy);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
}
