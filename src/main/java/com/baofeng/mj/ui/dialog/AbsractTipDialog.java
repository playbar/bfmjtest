package com.baofeng.mj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.baofeng.mj.R;

/**
 * ClassName: AbsractYesOrNoDialog <br/>
 * @author qiguolong
 * @date: 2015-4-30 下午3:41:27 <br/>
 * @description: 用于显示tip的dialog
 */
public abstract class AbsractTipDialog extends Dialog {

	protected View v;

	public AbsractTipDialog(Context context) {
		super(context, R.style.pay_alert_style);
		initView();
	}

	public AbsractTipDialog(Context context, int id) {
		super(context, id);
		initView();
	}

	private void initView() {
		v = getShowView();
		setContentView(v);
		setCancelable(true);
		//
		Window window = getWindow();
		window.setAttributes(getWindowlayout(window));

	}

	protected abstract View getShowView();

	protected LayoutParams getWindowlayout(Window window) {

		LayoutParams la = window.getAttributes();
		la.y -= 50;
		return la;
	}
}
