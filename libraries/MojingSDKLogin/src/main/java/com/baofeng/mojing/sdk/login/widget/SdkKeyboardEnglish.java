package com.baofeng.mojing.sdk.login.widget;

import android.content.Context;

import com.baofeng.mjgl.pubblico.layout.SdkDoubleLoginActivity;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.login.R;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant.GLOrientation;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLTextView;

public class SdkKeyboardEnglish extends GLLinearView {
	private Context mContext;
	private GLLinearView line1;
	private GLLinearView line2;
	private GLLinearView line3;
	private GLLinearView line4;

	private GLColor mKeyboardTextColor = new GLColor(0.89f, 0.89f, 0.9f);

	public SdkKeyboardEnglish(Context context, boolean isUpper) {
		super(context);
		mContext = context;

		this.setLayoutParams(256, 218);
		this.setOrientation(GLOrientation.VERTICAL);

		//第一行
		createEnglistKeyboardLine1(isUpper);

		//第二行
		createEnglistKeyboardLine2(isUpper);

		//第三行
		createEnglistKeyboardLine3(isUpper);

		//第四行
		createEnglistKeyboardLine4(isUpper);

		this.setDepth(2.5f);
	}

	/**
	 * 创建英文键盘第一行
	 * @author linzanxian  @Date 2015-10-10 下午6:36:30
	 * @param isUpper 是否大写
	 * @return void
	 */
	private void createEnglistKeyboardLine1(boolean isUpper) {
		//第一行
		String[] keys = new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
		line1 = new GLLinearView(mContext);
		line1.setOrientation(GLOrientation.HORIZONTAL);
		line1.setLayoutParams(256, 37);
		line1.setMargin(0, 10, 0, 0);
		line1.setId("line1");

		GLTextView textView;
		int len = keys.length;
		for (int i = 0; i < len; i++) {
			textView = new GLTextView(mContext);
			createEnglistTextView(textView, isUpper, keys[i]);

			if (i != 0) {
				textView.setMargin(3, 0, 0, 0);
			}

			line1.addView(textView);
		}

		this.addView(line1);
	}

	/**
	 * 创建英文键盘第二行
	 * @author linzanxian  @Date 2015-10-10 下午6:36:30
	 * @param isUpper 是否大写
	 * @return void
	 */
	private void createEnglistKeyboardLine2(boolean isUpper) {
		String[] keys = new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"};
		line2 = new GLLinearView(mContext);
		line2.setOrientation(GLOrientation.HORIZONTAL);
		line2.setLayoutParams(230, 37);
		line2.setMargin(13, 3, 0, 0);

		GLTextView textView;
		int len = keys.length;
		for (int i = 0; i < len; i++) {
			textView = new GLTextView(mContext);
			createEnglistTextView(textView, isUpper, keys[i]);

			if (i != 0) {
				textView.setMargin(3, 0, 0, 0);
			}

			line2.addView(textView);
		}

		this.addView(line2);
	}

	/**
	 * 创建英文键盘第三行
	 * @author linzanxian  @Date 2015-10-10 下午6:36:30
	 * @param isUpper 是否大写
	 * @return void
	 */
	private void createEnglistKeyboardLine3(boolean isUpper) {
		String[] keys = new String[]{"Z", "X", "C", "V", "B", "N", "M"};
		line3 = new GLLinearView(mContext);
		line3.setOrientation(GLOrientation.HORIZONTAL);
		line3.setLayoutParams(230, 37);
		line3.setMargin(0, 3, 0, 0);

		//上键头
		GLImageView upImageView = new GLImageView(mContext);
		upImageView.setLayoutParams( 36, 37);
		upImageView.setBackground(R.drawable.sdk_gl_login_key_bg4);
		upImageView.setImage(R.drawable.sdk_gl_login_key_up);
		upImageView.setId("upper");
		upImageView.setPadding(10, 10, 10, 10);
		upImageView.setFocusable(true);
		upImageView.setFocusListener(focusListener);
		upImageView.setOnKeyListener(keyListener);

		line3.addView(upImageView);

		GLTextView textView;
		int len = keys.length;
		for (int i = 0; i < len; i++) {
			textView = new GLTextView(mContext);
			createEnglistTextView(textView, isUpper, keys[i]);

			textView.setMargin(3, 0, 0, 0);

			line3.addView(textView);
		}

		GLImageView deleteImageView = new GLImageView(mContext);
		deleteImageView.setLayoutParams( 36, 37);
		deleteImageView.setMargin(3, 0, 0, 0);
		deleteImageView.setBackground(R.drawable.sdk_gl_login_key_bg2);
		deleteImageView.setImage(R.drawable.sdk_gl_login_key_delete);
		deleteImageView.setId("delete");
		deleteImageView.setPadding(9, 13.5f, 9, 9.5f);
		deleteImageView.setFocusable(true);
		deleteImageView.setFocusListener(focusListener);
		deleteImageView.setOnKeyListener(keyListener);

		line3.addView(deleteImageView);

		this.addView(line3);
	}

	/**
	 * 创建英文键盘第四行
	 * @author linzanxian  @Date 2015-10-10 下午6:36:30
	 * @param isUpper 是否大写
	 * @return void
	 */
	private void createEnglistKeyboardLine4(boolean isUpper) {
		line4 = new GLLinearView(mContext);
		line4.setOrientation(GLOrientation.HORIZONTAL);
		line4.setLayoutParams(230, 37);
		line4.setMargin(0, 3, 0, 0);

		//123
		GLTextView textView;
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 36, 37);
		textView.setTextSize(16);
		textView.setText("123");
		textView.setTextPadding(2);
		textView.setPadding(7, 10, 0, 0);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg4);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("123");

		line4.addView(textView);

		//逗号
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 23, 37);
		textView.setTextSize(16);
		textView.setText(",");
		textView.setPadding(10, 5, 0, 0);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg1);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId(",");
		textView.setMargin(3, 0, 0, 0);

		line4.addView(textView);

		//空格
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 75, 37);
		textView.setAlignment(GLTextView.ALIGN_CENTER);
		textView.setTextSize(16);
		textView.setText("空格");
		textView.setPadding(5, 10, 0, 10);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg5);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("space");
		textView.setMargin(3, 0, 0, 0);

		line4.addView(textView);

		//点号
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 23, 37);
		textView.setTextSize(16);
		textView.setText(".");
		textView.setPadding(10, 5, 0, 0);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg1);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId(".");
		textView.setMargin(3, 0, 0, 0);

		line4.addView(textView);

		//#+=
		textView = new GLTextView(mContext);
		textView.setLayoutParams(36, 37);
		textView.setTextSize(16);
		textView.setText("#+=");
		textView.setTextPadding(2);
		textView.setPadding(5, 10, 0, 0);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg4);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("fuhao");
		textView.setMargin(2, 0, 0, 0);

		line4.addView(textView);

		//确定
		textView = new GLTextView(mContext);
		textView.setLayoutParams(49, 37);
		textView.setAlignment(GLTextView.ALIGN_CENTER);
		textView.setTextSize(16);
		textView.setText("确定");
		textView.setPadding(3, 10, 0, 10);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg3);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("sure");
		textView.setMargin(3, 0, 0, 0);

		line4.addView(textView);

		this.addView(line4);
	}

	/**
	 * 创建英文textview
	 * @author linzanxian  @Date 2015-10-10 下午6:09:03
	 * @param textView GLTextView
	 * @param isUpper 是否大写
	 * @param key 键值
	 * @return void
	 */
	private void createEnglistTextView(GLTextView textView, boolean isUpper, String key) {
		textView.setLayoutParams(23, 37);
		//textView.setAlignment(GLTextView.ALIGN_CENTER);
		textView.setTextSize(16);
		textView.setText(isUpper ? key.toUpperCase() : key.toLowerCase());

		textView.setTextPadding(2);
		if (key.equals("W") || key.equals("M")) {
			textView.setPadding(4, 10, 0, 0);
		} else if (key.equals("I")) {
			textView.setPadding(8, 10, 0, 0);
		} else {
			textView.setPadding(6, 10, 0, 0);
		}


		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg1);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId(isUpper ? key.toUpperCase() : key.toLowerCase());
	}

	/**
	 * 焦点监听处理
	 */
	GLViewFocusListener focusListener = new GLViewFocusListener() {

		@Override
		public void onFocusChange(GLRectView view, boolean focusd) {
			String id = view.getId();

			if (id.equals("upper")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg4);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg4);
				}
			} else if (id.equals("delete")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg2);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg2);
				}
			} else if (id.equals("space")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg5);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg5);
				}
			} else if (id.equals("123") || id.equals("fuhao")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg4);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg4);
				}
			} else if (id.equals("sure")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg3);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg3);
				}
			} else {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg1);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg1);
				}
			}
		}
	};

	/**
	 * 事件监听处理
	 */
	GLOnKeyListener keyListener = new GLOnKeyListener() {

		@Override
		public boolean onKeyUp(GLRectView view, int keycode) {
			return false;
		}

		@Override
		public boolean onKeyLongPress(GLRectView arg0, int arg1) {
			return false;
		}

		@Override
		public boolean onKeyDown(GLRectView view, int keycode) {
			try {
				GLTextView textView;
				GLImageView imageView;
				if (keycode == MojingKeyCode.KEYCODE_DPAD_UP) {
					GLRectView view2 = view.getParent();
					String idString = view2.getId();
					if (idString.equals("line1")) {
						return true;
					}

					String id = view.getId();
					if (id.equals("123")) {
						imageView = (GLImageView) line3.getView(0);
						imageView.requestFocus();
						return true;
					} else if(id.equals(",")) {
						textView = (GLTextView) line3.getView(1);
						textView.requestFocus();
						return true;
					}  else if(id.equals("space")) {
						textView = (GLTextView) line3.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.equals(".")) {
						textView = (GLTextView) line3.getView(5);
						textView.requestFocus();
						return true;
					} else if(id.equals("fuhao")) {
						textView = (GLTextView) line3.getView(6);
						textView.requestFocus();
						return true;
					} else if(id.equals("sure")) {
						imageView = (GLImageView) line3.getView(8);
						imageView.requestFocus();
						return true;
					} else if(id.equals("upper")) {
						textView = (GLTextView) line2.getView(0);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("z")) {
						textView = (GLTextView) line2.getView(1);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("x")) {
						textView = (GLTextView) line2.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("c")) {
						textView = (GLTextView) line2.getView(3);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("v")) {
						textView = (GLTextView) line2.getView(4);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("b")) {
						textView = (GLTextView) line2.getView(5);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("n")) {
						textView = (GLTextView) line2.getView(6);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("m")) {
						textView = (GLTextView) line2.getView(7);
						textView.requestFocus();
						return true;
					} else if(id.equals("delete")) {
						textView = (GLTextView) line2.getView(8);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("a")) {
						textView = (GLTextView) line1.getView(0);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("s")) {
						textView = (GLTextView) line1.getView(1);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("d")) {
						textView = (GLTextView) line1.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("f")) {
						textView = (GLTextView) line1.getView(3);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("g")) {
						textView = (GLTextView) line1.getView(4);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("h")) {
						textView = (GLTextView) line1.getView(5);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("j")) {
						textView = (GLTextView) line1.getView(6);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("k")) {
						textView = (GLTextView) line1.getView(7);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("l")) {
						textView = (GLTextView) line1.getView(8);
						textView.requestFocus();
						return true;
					}
				} else if (keycode == MojingKeyCode.KEYCODE_DPAD_DOWN) {
					String id = view.getId();

					if(id.toLowerCase().equals("q")) {
						textView = (GLTextView) line2.getView(0);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("w")) {
						textView = (GLTextView) line2.getView(0);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("e")) {
						textView = (GLTextView) line2.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("r")) {
						textView = (GLTextView) line2.getView(3);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("t")) {
						textView = (GLTextView) line2.getView(4);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("y")) {
						textView = (GLTextView) line2.getView(5);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("u")) {
						textView = (GLTextView) line2.getView(6);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("i")) {
						textView = (GLTextView) line2.getView(7);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("o")) {
						textView = (GLTextView) line2.getView(8);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("p")) {
						textView = (GLTextView) line2.getView(8);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("a")) {
						imageView = (GLImageView) line3.getView(0);
						imageView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("s")) {
						textView = (GLTextView) line3.getView(1);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("d")) {
						textView = (GLTextView) line3.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("f")) {
						textView = (GLTextView) line3.getView(3);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("g")) {
						textView = (GLTextView) line3.getView(4);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("h")) {
						textView = (GLTextView) line3.getView(5);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("j")) {
						textView = (GLTextView) line3.getView(6);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("k")) {
						textView = (GLTextView) line3.getView(7);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("l")) {
						imageView = (GLImageView) line3.getView(8);
						imageView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("upper")) {
						textView = (GLTextView) line4.getView(0);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("z")) {
						textView = (GLTextView) line4.getView(1);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("x") || id.toLowerCase().equals("c") || id.toLowerCase().equals("v")) {
						textView = (GLTextView) line4.getView(2);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("b")) {
						textView = (GLTextView) line4.getView(3);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("n") || id.toLowerCase().equals("m")) {
						textView = (GLTextView) line4.getView(4);
						textView.requestFocus();
						return true;
					} else if(id.toLowerCase().equals("delete")) {
						textView = (GLTextView) line4.getView(5);
						textView.requestFocus();
						return true;
					}
				} else if(keycode == MojingKeyCode.KEYCODE_DPAD_CENTER) {
					((SdkDoubleLoginActivity) mContext).setText(view.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

	/**
	 * 默认焦点
	 * @author linzanxian  @Date 2015-10-12 下午4:49:06
	 * @return void
	 */
	public void setDefaultFocus() {
		if (line1 != null) {
			GLTextView textView = (GLTextView) line1.getView(0);
			textView.requestFocus();
		}
	}
}
