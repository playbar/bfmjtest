package com.baofeng.mj.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baofeng.mj.R;

/**选集方块按钮
 * Created by muyu on 2016/5/9.
 */
public class CubeIconItem extends FrameLayout {

    private Context mContext;
    private View rootView;
    private TextView nameTV;
    public CubeIconItem(Context context, String data) {
        super(context);
        this.mContext = context;
        initView(data);
    }

    protected void initView(String data){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.cube_icon_item,null);
        this.addView(rootView);
//        nameTV = (TextView) findViewById(R.id.cube_icon_num_tv);
        nameTV.setText(data);
    }
}
