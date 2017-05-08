package com.baofeng.mojing.sdk.login.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import com.baofeng.mjgl.pubblico.layout.SdkDoubleLoginActivity;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.login.R;
import com.baofeng.mojing.sdk.login.utils.MJGLUtils;
import com.bfmj.viewcore.view.BaseViewActivity;
import com.bfmj.viewcore.adapter.GLBaseAdapter;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.GLGroupView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.List;

public class SdkKeyboardSymbolAdapter extends GLBaseAdapter {

	private List<String> mList;
	private Context mContext;
	
	public SdkKeyboardSymbolAdapter(Context context){
		mContext = context;
	}
	
	public SdkKeyboardSymbolAdapter(Context context, List<String> mVideoInfos){
		mList = mVideoInfos;
		mContext = context;
	}
	
	public void setListData(List<String> list) {
		mList = list;
	}
	
	@Override
	public void addIndex(int index, GLRectView view) {
		
	}

	@Override
	public void removeIndex(int index) {
		mList.remove(index);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public GLRectView getGLView(int position, GLRectView convertView,
								GLGroupView parent) {
		if (position >= getCount()) {
			return null;
		}
		
		GLTextView textView = (GLTextView)convertView;
		if(textView == null){
			textView = new GLTextView(mContext);
			textView.setLayoutParams(47, 30);
			textView.setTextSize(16);
			textView.setText(mList.get(position));
			textView.setTextPadding(2);
			textView.setTextColor(new GLColor(0.89f, 0.89f, 0.9f));
			//textView.setBackground(R.drawable.sdk_gl_login_key_bg11);
			textView.setFocusable(true);
			textView.setFocusListener(focusListener);
			textView.setOnKeyListener(keyListener);
			textView.setId(mList.get(position));
			//textView.setPadding(35, 10, 0, 0);
		}
		textView.setText(mList.get(position));
		textView.setId(mList.get(position));
		
		if (position == 0) {
			textView.requestFocus();
		}
		
		return textView;
	}
	
	/**
	 * 焦点监听处理
	 */
	GLViewFocusListener focusListener = new GLViewFocusListener() {
		
		@Override
		public void onFocusChange(final GLRectView view, boolean focusd) {
			if (focusd) {
				MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
					new Runnable() {
						@Override
						public void run() {
							view.setBackground(R.drawable.sdk_gl_login_key_bg11);
						}
					});
				
			} else {
				
				MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
					new Runnable() {
						@Override
						public void run() {
							Bitmap bitmap = null;
							view.setBackground(bitmap);
						}
					});
			}
		}
	};
	
	/**
	 * 事件监听处理
	 */
	GLOnKeyListener keyListener = new GLOnKeyListener() {
		
		@Override
		public boolean onKeyUp(GLRectView arg0, int arg1) {
			return false;
		}
		
		@Override
		public boolean onKeyLongPress(GLRectView arg0, int arg1) {
			return false;
		}
		
		@Override
		public boolean onKeyDown(GLRectView view, int keycode) {
			if (keycode != MojingKeyCode.KEYCODE_DPAD_CENTER) {
				return false;
			}
			
			String id = view.getId();
			((SdkDoubleLoginActivity) mContext).setText(id);
			return false;
		}
	};
}
