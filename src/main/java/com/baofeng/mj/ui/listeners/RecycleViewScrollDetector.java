package com.baofeng.mj.ui.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * PullToRefresh滑动Title渐变效果
 * Created by yum on 16/7/12.
 */
public class RecycleViewScrollDetector extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private RelativeLayout mTitleLayout;
    private boolean hasTitle;
    private int totalDy;
    private ScrollStateCallback scrollStateCallback;
    private ScrollCallback scrollCallback;
    private int scrolledDY;

    public RecycleViewScrollDetector(LinearLayoutManager layoutManager, RelativeLayout titleLayout, boolean hasTitle, ScrollStateCallback scrollStateCallback){
        this.layoutManager = layoutManager;
        this.mTitleLayout = titleLayout;
        this.hasTitle = hasTitle;
        this.scrollStateCallback = scrollStateCallback;
    }

    public int getTotalDy() {
        return totalDy;
    }

    public void setTotalDy(int totalDy) {
        this.totalDy = totalDy;
    }

    public void addScrollCallback(ScrollCallback scrollCallback){
        this.scrollCallback = scrollCallback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        scrolledDY = dy;
        if(hasTitle){//有title
            mTitleLayout.setVisibility(View.VISIBLE);
            totalDy = totalDy + dy;
            if (totalDy < 5) {
                mTitleLayout.setAlpha(0);
            }
            if (totalDy > 0) {
                mTitleLayout.setAlpha(totalDy / PixelsUtil.dip2px(200));
            }
            if(scrollCallback != null){
                scrollCallback.onScrolled(recyclerView, dx, dy);
            }
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(RecyclerView.SCROLL_STATE_DRAGGING == newState){//滑动拖拽

        }else if(RecyclerView.SCROLL_STATE_IDLE == newState){//滑动停止
            if(layoutManager != null){
                //20170110 whf 添加异常捕捉 java.lang.NullPointerException: Attempt to invoke virtual method 'int android.support.v7.widget.OrientationHelper.getStartAfterPadding()
                try {
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    if(lastPosition >= itemCount - 1){//滑动到底部
                        if(scrollStateCallback != null){
                            scrollStateCallback.scrollToEnd(scrolledDY);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }else{//滑动中

        }
    }

    /**
     * 滑动回调
     */
    public interface ScrollCallback{
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    /**
     * 滑动状态回调
     */
    public interface ScrollStateCallback{
        void scrollToEnd(int scrolledDY);//滑动到底部
    }
}