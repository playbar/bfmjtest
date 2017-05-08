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

import java.util.ArrayList;
import java.util.List;


public class SdkKeyboardNumber extends GLLinearView {
	private Context mContext;
	private GLColor mKeyboardTextColor = new GLColor(0.89f, 0.89f, 0.9f);
	private float mHeight = 155;
	private float mPadding = 5;
	private GLTextView textView1;
	private int mSignIndex = 0;
	private List<String> mSignList1 = new ArrayList<String>();
//	private List<String> mSignList2 = new ArrayList<String>();
//	private List<String> mSignList3 = new ArrayList<String>();

	private GLLinearView linearView;
	private GLLinearView linearView1;
	private GLLinearView linearView2;
	private GLLinearView linearView3;
	private GLLinearView linearView4;
	private GLLinearView linearView5;

	public SdkKeyboardNumber(Context context) {
		super(context);
		mContext = context;

		this.setOrientation(GLOrientation.HORIZONTAL);
		this.setLayoutParams(256, 218);
		this.setMargin(0, 10, 0, 0);

		initSignList();
		createRow1();
		createRow2();
		createRow3();
		createRow4();
		createRow5();

		this.setDepth(2.5f);
	}

	private void initSignList() {
		mSignList1.clear();
//		mSignList2.clear();
//		mSignList3.clear();

		mSignList1.add("+");
		mSignList1.add("-");
		mSignList1.add("/");
		mSignList1.add("%");

//		mSignList2.add(":");
//		mSignList2.add("<");
//		mSignList2.add(">");
//		mSignList2.add("^");
//		
//		mSignList3.add("+");
//		mSignList3.add("-");
//		mSignList3.add("/");
//		mSignList3.add("%");
	}

	/**
	 * 创建第一列
	 * @author linzanxian  @Date 2015-10-12 上午11:10:23
	 * @return void
	 */
	public void createRow1() {
		linearView = new GLLinearView(mContext);
		linearView.setOrientation(GLOrientation.VERTICAL);
		linearView.setLayoutParams(47, mHeight);

		linearView1 = new GLLinearView(mContext);
		linearView1.setOrientation(GLOrientation.VERTICAL);
		linearView1.setLayoutParams(47, 122);
		linearView1.setBackground(R.drawable.sdk_gl_login_key_bg8);

		GLTextView textView;

		//+
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 47, 30);
		textView.setTextSize(16);
		textView.setText("+");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		//textView.setBackground(R.drawable.sdk_gl_login_key_bg11);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("h1");
		textView.setPadding(20, 5, 0, 0);
		linearView1.addView(textView);

		//-
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 47, 30);
		textView.setTextSize(16);
		textView.setText("-");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		//textView.setBackground(R.drawable.sdk_gl_login_key_bg11);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("h2");
		textView.setPadding(20, 5, 0, 0);
		linearView1.addView(textView);

		///
		textView = new GLTextView(mContext);
		textView.setLayoutParams(47, 30);
		textView.setTextSize(16);
		textView.setText("/");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		//textView.setBackground(R.drawable.sdk_gl_login_key_bg11);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("h3");
		textView.setPadding(20, 5, 0, 0);
		linearView1.addView(textView);

		//%
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 47, 30);
		textView.setTextSize(16);
		textView.setText("%");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		//textView.setBackground(R.drawable.sdk_gl_login_key_bg11);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("h4");
		textView.setPadding(20, 5, 0, 0);
		linearView1.addView(textView);

		//返回键
		GLImageView imageView = new GLImageView(mContext);
		imageView.setLayoutParams(47, 37);
		imageView.setBackground(R.drawable.sdk_gl_login_key_bg7);
		imageView.setImage(R.drawable.sdk_gl_login_key_back_icon);
		imageView.setId("back");
		imageView.setPadding(13.5f, 11, 13.5f, 11);
		imageView.setFocusable(true);
		imageView.setFocusListener(focusListener);
		imageView.setOnKeyListener(keyListener);
		imageView.setMargin(0, mPadding, 0, 0);

		linearView.addView(linearView1);
		linearView.addView(imageView);

		this.addView(linearView);
	}

	/**
	 * 创建第二列
	 * @author linzanxian  @Date 2015-10-12 上午11:10:23
	 * @return void
	 */
	public void createRow2() {
		linearView2 = new GLLinearView(mContext);
		linearView2.setOrientation(GLOrientation.VERTICAL);
		linearView2.setLayoutParams(47, mHeight);
		linearView2.setMargin(mPadding-2, 0, 0, 0);

		GLTextView textView;

		textView1 = createText("1");
		linearView2.addView(textView1);

		textView = createText("4");
		textView.setMargin(0, mPadding, 0, 0);
		linearView2.addView(textView);

		textView = createText("7");
		textView.setMargin(0, mPadding, 0, 0);
		linearView2.addView(textView);

		//空格
		GLImageView imageView = new GLImageView(mContext);
		imageView.setLayoutParams( 47, 37);
		imageView.setBackground(R.drawable.sdk_gl_login_key_bg7);
		imageView.setImage(R.drawable.sdk_gl_login_key_space_icon);
		imageView.setId("space");
		imageView.setPadding(15f, 12, 15f, 12);
		imageView.setFocusable(true);
		imageView.setFocusListener(focusListener);
		imageView.setOnKeyListener(keyListener);
		imageView.setMargin(0, mPadding, 0, 0);
		linearView2.addView(imageView);

		this.addView(linearView2);
	}

	/**
	 * 创建第三列
	 * @author linzanxian  @Date 2015-10-12 上午11:10:23
	 * @return void
	 */
	public void createRow3() {
		linearView3 = new GLLinearView(mContext);
		linearView3.setOrientation(GLOrientation.VERTICAL);
		linearView3.setLayoutParams(47, mHeight);
		linearView3.setMargin(mPadding-2, 0, 0, 0);

		GLTextView textView;

		textView = createText("2");
		linearView3.addView(textView);

		textView = createText("5");
		textView.setMargin(0, mPadding, 0, 0);
		linearView3.addView(textView);

		textView = createText("8");
		textView.setMargin(0, mPadding, 0, 0);
		linearView3.addView(textView);

		textView = createText("0");
		textView.setMargin(0, mPadding, 0, 0);
		linearView3.addView(textView);

		this.addView(linearView3);
	}

	/**
	 * 创建第四列
	 * @author linzanxian  @Date 2015-10-12 上午11:10:23
	 * @return void
	 */
	public void createRow4() {
		linearView4 = new GLLinearView(mContext);
		linearView4.setOrientation(GLOrientation.VERTICAL);
		linearView4.setLayoutParams(47, mHeight);
		linearView4.setMargin(mPadding-2, 0, 0, 0);

		GLTextView textView;

		textView = createText("3");
		linearView4.addView(textView);

		textView = createText("6");
		textView.setMargin(0, mPadding, 0, 0);
		linearView4.addView(textView);

		textView = createText("9");
		textView.setMargin(0, mPadding, 0, 0);
		linearView4.addView(textView);

		textView = createText(".");
		textView.setMargin(0, mPadding, 0, 0);
		linearView4.addView(textView);

		this.addView(linearView4);
	}

	/**
	 * 创建第五列
	 * @author linzanxian  @Date 2015-10-12 上午11:10:23
	 * @return void
	 */
	public void createRow5() {
		linearView5 = new GLLinearView(mContext);
		linearView5.setOrientation(GLOrientation.VERTICAL);
		linearView5.setLayoutParams(57, mHeight);
		linearView5.setMargin(mPadding-2, 0, 0, 0);

		//删除
		GLImageView imageView = new GLImageView(mContext);
		imageView.setLayoutParams( 57, 37);
		imageView.setBackground(R.drawable.sdk_gl_login_key_bg6);
		imageView.setImage(R.drawable.sdk_gl_login_key_delete);
		imageView.setId("delete");
		imageView.setPadding(15f, 12, 15f, 12);
		imageView.setFocusable(true);
		imageView.setFocusListener(focusListener);
		imageView.setOnKeyListener(keyListener);
		linearView5.addView(imageView);

		GLTextView textView;

		//*
		textView = new GLTextView(mContext);
		textView.setLayoutParams(57, 37);
		textView.setTextSize(16);
		textView.setText("*");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg13);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("*");
		textView.setMargin(0, mPadding, 0, 0);
		textView.setPadding(25, 10, 0, 0);
		linearView5.addView(textView);

		//#
		textView = new GLTextView(mContext);
		textView.setLayoutParams( 57, 37);
		textView.setTextSize(16);
		textView.setText("#");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg13);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("#");
		textView.setMargin(0, mPadding, 0, 0);
		textView.setPadding(25, 10, 0, 0);
		linearView5.addView(textView);

		//完成
		textView = new GLTextView(mContext);
		textView.setLayoutParams(57, 37);
		textView.setTextSize(16);
		textView.setText("完成");
		textView.setTextPadding(2);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg6);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId("sure");
		textView.setMargin(0, mPadding, 0, 0);
		textView.setPadding(12, 10, 0, 0);
		linearView5.addView(textView);

		this.addView(linearView5);
	}

	private GLTextView createText(String value) {
		GLTextView textView = new GLTextView(mContext);
		textView.setLayoutParams(47, 37);
		textView.setTextSize(16);
		textView.setText(value);
		textView.setTextPadding(0);
		textView.setTextColor(mKeyboardTextColor);
		textView.setBackground(R.drawable.sdk_gl_login_key_bg7);
		textView.setFocusable(true);
		textView.setFocusListener(focusListener);
		textView.setOnKeyListener(keyListener);
		textView.setId(value);

		if (value.equals(".")) {
			textView.setPadding(20, 5, 0, 0);
		} else {
			textView.setPadding(20, 10, 0, 0);
		}

		return textView;
	}

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
				if (keycode != MojingKeyCode.KEYCODE_DPAD_CENTER) {
					String id = view.getId();
					GLTextView textView;
					GLImageView imageView;
					if (keycode == MojingKeyCode.KEYCODE_DPAD_UP) {

						if (id.equals("h1") || id.equals("1") || id.equals("2") || id.equals("3") || id.equals("delete")) {
							return true;
						}
					} else if(keycode == MojingKeyCode.KEYCODE_DPAD_LEFT) {
						if(id.equals("*")) {
							textView = (GLTextView) linearView4.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("#")) {
							textView = (GLTextView) linearView4.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("sure")) {
							textView = (GLTextView) linearView4.getView(3);
							textView.requestFocus();
							return true;
						} else if(id.equals("6")) {
							textView = (GLTextView) linearView3.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("9")) {
							textView = (GLTextView) linearView3.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals(".")) {
							textView = (GLTextView) linearView3.getView(3);
							textView.requestFocus();
							return true;
						} else if(id.equals("5")) {
							textView = (GLTextView) linearView2.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("8")) {
							textView = (GLTextView) linearView2.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("0")) {
							imageView = (GLImageView) linearView2.getView(3);
							imageView.requestFocus();
							return true;
						} else if(id.equals("4")) {
							textView = (GLTextView) linearView1.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("7")) {
							textView = (GLTextView) linearView1.getView(3);
							textView.requestFocus();
							return true;
						} else if(id.equals("space")) {
							imageView = (GLImageView) linearView.getView(1);
							imageView.requestFocus();
							return true;
						}
					} else if(keycode == MojingKeyCode.KEYCODE_DPAD_RIGHT) {
						if (id.equals("h2") || id.equals("h3")) {
							textView = (GLTextView) linearView2.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("h4")) {
							textView = (GLTextView) linearView2.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("back")) {
							imageView = (GLImageView) linearView2.getView(3);
							imageView.requestFocus();
							return true;
						} else if(id.equals("4")) {
							textView = (GLTextView) linearView3.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("7")) {
							textView = (GLTextView) linearView3.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("space")) {
							textView = (GLTextView) linearView3.getView(3);
							textView.requestFocus();
							return true;
						} else if(id.equals("5")) {
							textView = (GLTextView) linearView4.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("8")) {
							textView = (GLTextView) linearView4.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals("0")) {
							textView = (GLTextView) linearView4.getView(3);
							textView.requestFocus();
							return true;
						} else if(id.equals("6")) {
							textView = (GLTextView) linearView5.getView(1);
							textView.requestFocus();
							return true;
						} else if(id.equals("9")) {
							textView = (GLTextView) linearView5.getView(2);
							textView.requestFocus();
							return true;
						} else if(id.equals(".")) {
							textView = (GLTextView) linearView5.getView(3);
							textView.requestFocus();
							return true;
						}
					}

					return false;
				}

				String id = view.getId();

				if (id.equals("back")) {
					((SdkDoubleLoginActivity) mContext).createEnglistKeyboard(true);
				} else {
					if(id.equals("h1")) {
						if (mSignIndex == 0) {
							id = mSignList1.get(0);
						}
					} else if(id.equals("h2")) {
						if (mSignIndex == 0) {
							id = mSignList1.get(1);
						}
					} else if(id.equals("h3")) {
						if (mSignIndex == 0) {
							id = mSignList1.get(2);
						}
					} else if(id.equals("h4")) {
						if (mSignIndex == 0) {
							id = mSignList1.get(3);
						}
					}

					((SdkDoubleLoginActivity) mContext).setText(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

	/**
	 * 焦点监听处理
	 */
	GLViewFocusListener focusListener = new GLViewFocusListener() {

		@Override
		public void onFocusChange(GLRectView view, boolean focusd) {
			String id = view.getId();

			if (id.equals("delete") || id.equals("cancel")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg6);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg6);
				}
			} else if (id.equals("h1") || id.equals("h2") || id.equals("h3") || id.equals("h4")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_bg11);
				} else {
					view.setBackground(new GLColor(0,0,0,0));
				}
			} else if (id.equals("*") || id.equals("#")) {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg13);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg13);
				}
			} else {
				if (focusd) {
					view.setBackground(R.drawable.sdk_gl_login_key_select_bg7);
				} else {
					view.setBackground(R.drawable.sdk_gl_login_key_bg7);
				}
			}
		}
	};

	/**
	 * 默认焦点
	 * @author linzanxian  @Date 2015-10-12 下午4:49:06
	 * @return void
	 */
	public void setDefaultFocus() {
		if (textView1 != null) {
			textView1.requestFocus();
		}
	}
}
