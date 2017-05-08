package com.baofeng.mj.ui.viewholder;


import android.graphics.drawable.Animatable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by yushaochen on 2016/11/24.
 * 加载更多ViewHolder
 */
public class NewLoadMoreViewHolder extends RecyclerView.ViewHolder{
    private FrameLayout item_parent;
    private LinearLayout ll_loading;
    private ImageView iv_loading;
    private ImageView iv_image;
    private TextView tv_load;
    private TextView tv_no_more;
    private Animatable animationDrawable;

    public NewLoadMoreViewHolder(View itemView) {
        super(itemView);
        item_parent = (FrameLayout) itemView.findViewById(R.id.item_parent);
        ll_loading = (LinearLayout) itemView.findViewById(R.id.ll_loading);
        iv_loading = (ImageView) itemView.findViewById(R.id.iv_loading);
        iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
        tv_load = (TextView) itemView.findViewById(R.id.tv_load);
        tv_no_more = (TextView) itemView.findViewById(R.id.tv_no_more);
        item_parent.setLayoutParams(new FrameLayout.LayoutParams(PixelsUtil.getWidthPixels(), LinearLayout.LayoutParams.WRAP_CONTENT));
        animationDrawable = (Animatable) iv_loading.getBackground();

    }

    /**
     * 更新加载更多UI
     * @param isLoadMore true处理加载更多，false处理加载结果显示
     * @param flag 当isLoadMore为true时，flag = true 是加载中，flag = false 是加载失败；
     *             当isLoadMore为false时，flag = true 是没有更多资源可以加载，flag = false 是当前加载完成；
     */
    public void updateLoadMoreUI(boolean isLoadMore, boolean flag){
        item_parent.setVisibility(View.VISIBLE);
        if(isLoadMore){//加载更多
            tv_no_more.setVisibility(View.GONE);
            ll_loading.setVisibility(View.VISIBLE);
            if(flag){//加载中
                iv_image.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);
                if(!animationDrawable.isRunning()) {
                    animationDrawable.start();//开始动画
                }
                tv_load.setText("正在加载...");
            } else {//加载失败
                if(animationDrawable.isRunning()) {
                    animationDrawable.stop();//停止动画
                }
                iv_loading.setVisibility(View.GONE);
                iv_image.setVisibility(View.VISIBLE);
                tv_load.setText("加载失败,请重新加载");
            }
        }else{//加载结果显示
            ll_loading.setVisibility(View.GONE);
            if(animationDrawable.isRunning()) {
                animationDrawable.stop();//停止动画
            }
            tv_no_more.setVisibility(View.VISIBLE);
            if(flag){//没有资源
                tv_no_more.setText("- 暂无更多内容 -");
            }else{//加载完成
                tv_no_more.setText("- 加载完成 -");
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
