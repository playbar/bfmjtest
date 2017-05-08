package com.baofeng.mj.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**GridView，解决在Scroll中高度不对问题
 * Created by muyu on 2016/5/3.
 */
public class BaseGridView extends GridView {
    public BaseGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseGridView(Context context) {
        super(context);
    }

    public BaseGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}