package com.baofeng.mj.util.viewutil;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ContentInfo;

/**
 * 关于页面View的一些细节展示
 * Created by muyu on 2016/5/24.
 */
public class ShowDetail {

    /**
     * 是否需要付费
     * @param payView
     * @param data
     */
    public static void showPayCount(TextView payView, ContentInfo data) {
        switch (data.getPayment_type()) {
            case 1:
                payView.setVisibility(View.VISIBLE);
                payView.setText(data.getPayment_count() + "魔豆");
                break;
            case 2:
                payView.setVisibility(View.VISIBLE);
                payView.setText(data.getPayment_count() + "魔币");
                break;
            default:
                payView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 游戏标签显示
     * @param type
     * @param gamelabel
     * @param mContext
     */
    public static void showGameLabel(int type, TextView gamelabel, Context mContext) {
        switch (type){
            case 1:
                gamelabel.setVisibility(View.VISIBLE);
                gamelabel.setBackground(mContext.getResources().getDrawable(R.drawable.label_activity));
                break;
            case 2:
                gamelabel.setVisibility(View.VISIBLE);
                gamelabel.setBackground(mContext.getResources().getDrawable(R.drawable.label_first));
                break;
            case 3:
                gamelabel.setVisibility(View.VISIBLE);
                gamelabel.setBackground(mContext.getResources().getDrawable(R.drawable.label_sole));
                break;
            default:
                gamelabel.setVisibility(View.GONE);
                gamelabel.setBackground(null);
                break;
        }
    }

    /**
     * 视频类型角标显示
     * @param headwear
     * @param ivHeadwear
     */
    public static void showVideoLabel(int headwear, ImageView ivHeadwear) {//6 专题 7 直播8 巨幕9 游戏 10 软件
        switch (headwear) {
            case 1:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.video_new);
                break;
            case 2:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.video_hot);
                break;
            case 3:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.video_vr);
                break;
            case 4:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.video_3d);
                break;
            case 5:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.video_vr_3d);
                break;
            case 6:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.public_label_zhuanti);
                break;
            case 7:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.public_label_zhibo);
                break;
            case 8:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.public_label_jumu);
                break;
            case 9:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.public_label_youxi);
                break;
            case 10:
                ivHeadwear.setVisibility(View.VISIBLE);
                ivHeadwear.setImageResource(R.drawable.public_label_ruanjian);
                break;
            default:
                ivHeadwear.setVisibility(View.GONE);
                break;
        }
    }

    public static void hideVideoLabel(int headwear, ImageView ivHeadwear){
        ivHeadwear.setVisibility(View.GONE);
    }
}
