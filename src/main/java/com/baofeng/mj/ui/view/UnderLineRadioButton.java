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
import com.baofeng.mj.bean.ContentInfo;

/** 带下划线的RadioButton
 * Created by muyu on 2016/8/22.
 */
public class UnderLineRadioButton extends FrameLayout implements ITabItem {
    ImageView imageView;
    TextView textView;
    private Context mContext;
    public UnderLineRadioButton(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs, int defStyle) {
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

    protected void init(AttributeSet attrs) {//'android.content.res.Resources android.content.Context.getResources()' on a null object reference
        View view = View.inflate(mContext, R.layout.view_underline_radiobtn, this);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textView = (TextView) view.findViewById(R.id.textView);

        if (attrs == null)
            return;

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.UnderLineRadioButton);
        if (a.hasValue(R.styleable.UnderLineRadioButton_underLineImageDrawable)) {
            Drawable drawable = a.getDrawable(R.styleable.UnderLineRadioButton_underLineImageDrawable);
            imageView.setImageDrawable(drawable);
        }

        if (a.hasValue(R.styleable.UnderLineRadioButton_underLineImageBackground)) {
            Drawable drawable = a.getDrawable(R.styleable.UnderLineRadioButton_underLineImageBackground);
            imageView.setBackgroundDrawable(drawable);
        }

        if (a.hasValue(R.styleable.UnderLineRadioButton_underLineText)) {
            String text = a.getString(R.styleable.UnderLineRadioButton_underLineText);
            textView.setText(text);
        }
        if (a.hasValue(R.styleable.UnderLineRadioButton_underLineTextColor)) {
            ColorStateList color = a.getColorStateList(R.styleable.UnderLineRadioButton_underLineTextColor);
            textView.setTextColor(color);
        }
    }

    @Override
    public View getTabItemView() {
        return this;
    }
}
