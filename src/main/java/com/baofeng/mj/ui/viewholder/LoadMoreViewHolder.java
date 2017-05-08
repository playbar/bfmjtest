package com.baofeng.mj.ui.viewholder;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by liuchuanchi on 2016/11/24.
 * 加载更多ViewHolder
 */
public class LoadMoreViewHolder extends RecyclerView.ViewHolder{
    private FrameLayout item_parent;
    private LinearLayout ll_loading;
    private ImageView iv_loading;
    private TextView tv_no_more;
    private TextView tv_load_failure;
    private AnimationDrawable animationDrawable;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        item_parent = (FrameLayout) itemView.findViewById(R.id.item_parent);
        ll_loading = (LinearLayout) itemView.findViewById(R.id.ll_loading);
        iv_loading = (ImageView) itemView.findViewById(R.id.iv_loading);
        tv_no_more = (TextView) itemView.findViewById(R.id.tv_no_more);
        tv_load_failure = (TextView) itemView.findViewById(R.id.tv_load_failure);
        item_parent.setLayoutParams(new FrameLayout.LayoutParams(PixelsUtil.getWidthPixels(), LinearLayout.LayoutParams.WRAP_CONTENT));
        animationDrawable = (AnimationDrawable) iv_loading.getBackground();
    }

    /**
     * 更新加载更多UI
     * @param isLoadMore true加载更多，false不是
     * @param noMore true没有更多，false加载更多失败
     */
    public void updateLoadMoreUI(boolean isLoadMore, boolean noMore){
        item_parent.setVisibility(View.VISIBLE);
        if(isLoadMore){//加载更多
            ll_loading.setVisibility(View.VISIBLE);
            animationDrawable.start();//开始动画
            tv_no_more.setVisibility(View.GONE);
            tv_load_failure.setVisibility(View.GONE);
        }else{//不是加载更多
            ll_loading.setVisibility(View.GONE);
            animationDrawable.stop();//停止动画
            if(noMore){//没有更多
                tv_no_more.setVisibility(View.VISIBLE);
                tv_load_failure.setVisibility(View.GONE);
            }else{//加载更多失败
                tv_no_more.setVisibility(View.GONE);
                tv_load_failure.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 隐藏加载更多UI
     */
    public void hideLoadMoreUI(){
        item_parent.setVisibility(View.GONE);
    }
}
