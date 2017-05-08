package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.baofeng.mj.R;

/** 首页标签View
 * Created by muyu on 2016/3/28.
 */
public class MainTabItemView extends FrameLayout implements ITabItem {
    ImageView imageView;
    TextView textView;
    TextView tv_red_point;

    public MainTabItemView(Context context) {
        super(context);
        init(null);
    }

    public MainTabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MainTabItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    /**
     * 显示小红点
     * @param downloadingSize 正在下载的个数
     */
    public void showRedPoint(int downloadingSize) {
        if(downloadingSize == 0){
            tv_red_point.setVisibility(View.GONE);
        }else{
            tv_red_point.setVisibility(View.VISIBLE);
            if(downloadingSize <= 99){
                tv_red_point.setText(String.valueOf(downloadingSize));
            }else{
                tv_red_point.setText("99+");
            }
        }
    }

    protected void init(AttributeSet attrs) {
        View view = View.inflate(getContext(), R.layout.layout_main_tab_item, this);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textView = (TextView) view.findViewById(R.id.textView);
        tv_red_point = (TextView) view.findViewById(R.id.tv_red_point);

        if (attrs == null)
            return;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MainTabItem);
        if (a.hasValue(R.styleable.MainTabItem_imageDrawable)) {
            Drawable drawable = a.getDrawable(R.styleable.MainTabItem_imageDrawable);
            imageView.setImageDrawable(drawable);
        }

        if (a.hasValue(R.styleable.MainTabItem_imageBackground)) {
            Drawable drawable = a.getDrawable(R.styleable.MainTabItem_imageBackground);
            imageView.setBackgroundDrawable(drawable);
        }

        if (a.hasValue(R.styleable.MainTabItem_text)) {
            String text = a.getString(R.styleable.MainTabItem_text);
            textView.setText(text);
        }
        if (a.hasValue(R.styleable.MainTabItem_textColor)) {
            ColorStateList color = a.getColorStateList(R.styleable.MainTabItem_textColor);
            textView.setTextColor(color);
        }
    }

    @Override
    public View getTabItemView() {
        return this;
    }
}