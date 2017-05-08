package com.baofeng.mj.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;

/**
 * Created by muyu on 2016/12/29.
 */
public class MoreTextView extends LinearLayout{
    protected TextView contentView;
    private boolean isExpand;
    private ImageButton expandView;

    protected int textColor;
    protected float textSize;
    protected int maxLine;
    protected String text;

    public int defaultTextColor = Color.BLACK;
    public int defaultTextSize = 12;
    public int defaultLine = 1;

    public MoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize();
        initWithAttrs(context, attrs);
    }

    protected void initWithAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MoreTextStyle);
        int textColor = a.getColor(R.styleable.MoreTextStyle_moreTextColor,
                defaultTextColor);
        textSize = a.getDimensionPixelSize(R.styleable.MoreTextStyle_moreTextSize, defaultTextSize);
        maxLine = a.getInt(R.styleable.MoreTextStyle_moreMaxLine, defaultLine);
        text = a.getString(R.styleable.MoreTextStyle_moreText);
        bindTextView(textColor, textSize, maxLine, text);
        a.recycle();
    }

    protected void initalize() {
        setOrientation(VERTICAL);
        setGravity(Gravity.RIGHT);
        contentView = new TextView(getContext());
        addView(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    protected void bindTextView(int color,float size,final int line,final String text){
        contentView.setTextColor(color);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        contentView.setText(text);
        contentView.getPaint().setTextSize(textSize);

//        setLineSpacing()原型为public void setLineSpacing(float add, float mult);
//        参数add表示要增加的间距数值，对应android:lineSpacingExtra参数。
//        参数mult表示要增加的间距倍数，对应android:lineSpacingMultiplier参数。
        contentView.setLineSpacing(15, 1);
//        contentView.setHeight(contentView.getLineHeight() * line);
 }

    public void bindListener(final ImageButton expandView) {
        this.expandView = expandView;

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMoreTextView();
            }
        });
    }

    public void clickMoreTextView(){
        isExpand = !isExpand;
        contentView.clearAnimation();
//        final int deltaValue;
//        final int startValue = contentView.getHeight();
        int durationMillis = 200;
        if (isExpand) {
            contentView.setSingleLine(false);
//            deltaValue = contentView.getLineHeight() * contentView.getLineCount() - startValue;
            RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(durationMillis);
            animation.setFillAfter(true);
            expandView.startAnimation(animation);

        } else {
            setSingleLine();
//            deltaValue = contentView.getLineHeight() * maxLine - startValue;
            RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(durationMillis);
            animation.setFillAfter(true);
            expandView.startAnimation(animation);
        }

//        Animation animation = new Animation() {
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                contentView.setHeight((int) (startValue + deltaValue * interpolatedTime));
//            }
//
//        };
//        animation.setDuration(durationMillis);
//        contentView.startAnimation(animation);
    }

    private void setSingleLine(){
//        Layout layout = contentView.getLayout();
//        int start = layout.getLineStart(0);
//        int end = layout.getLineEnd(0);
//        contentView.setText(text.substring(start, end - 2) + "......");
        contentView.setSingleLine(true);
        contentView.setEllipsize(TextUtils.TruncateAt.END);
    }

    public TextView getTextView(){
        return contentView;
    }

    public void setText(String charSequence){
        this.text = charSequence;
        contentView.setText(charSequence);
        setSingleLine();
    }
}

