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

/** 首页椭圆RadioButton
 * Created by muyu on 2016/3/2.
 */
public class OvalRadioButton extends FrameLayout implements ITabItem {
    TextView textView;
    private Context mContext;
    public OvalRadioButton(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public OvalRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OvalRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    protected void init(AttributeSet attrs) {//'android.content.res.Resources android.content.Context.getResources()' on a null object reference
        View view = View.inflate(mContext, R.layout.view_oval_radiobtn, this);
        textView = (TextView) view.findViewById(R.id.textView);
    }

    @Override
    public View getTabItemView() {
        return this;
    }
}
