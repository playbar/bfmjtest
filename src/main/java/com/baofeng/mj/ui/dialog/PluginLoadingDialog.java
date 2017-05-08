package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

/**
 * Created by wanghongfang on 2017/3/24.
 */
public class PluginLoadingDialog extends Dialog {
    private Context activity;
    TextView tv;
    public PluginLoadingDialog(Context context) {
        super(context, R.style.alertdialog);
        activity = context;
        initView();

    }

    private void initView(){
        View view = LayoutInflater.from(activity).inflate(R.layout.app_no_update_dialog, null);
          tv = (TextView) view.findViewById(R.id.content);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText("提示");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        setContentView(view, params);
        setCancelable(false);
        TextView update_dialog_ok = (TextView) view.findViewById(R.id.update_dialog_ok);
        update_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setContent(int contentRes){
        tv.setText(activity.getResources().getString(contentRes));
    }

    public void setContent(String content){
        tv.setText(content);
    }
}
