package com.baofeng.mojing.sdk.login.widget;

import android.annotation.SuppressLint;
import android.content.Context;

import com.baofeng.mjgl.pubblico.layout.SdkDoubleLoginActivity;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.login.R;
import com.baofeng.mojing.sdk.login.utils.MD5Util;
import com.baofeng.mojing.sdk.login.utils.MJGLUtils;
import com.baofeng.mojing.sdk.login.utils.OkHttpUtil;
import com.baofeng.mojing.sdk.login.utils.URLConfig;
import com.bfmj.viewcore.view.BaseViewActivity;
import com.bfmj.viewcore.animation.GLAnimation;
import com.bfmj.viewcore.animation.GLTranslateAnimation;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant.GLOrientation;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;
import com.bfmj.viewcore.view.GLViewPage;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class SdkDoubleLoginViewPage extends GLViewPage {
    private GLLinearView mBgLinearView;
    private GLTextView mMsgTextView;
    private GLTextView mUserTextView;
    private GLTextView mPassTextView;
    private GLLinearView mKeyboardBgView;

    private Context mContext;
    private float mBgDepth = 3f;
    private float mLoginDepth = 2.5f;
    public boolean isKeyboard = false; //键盘是否打开

    private float mBgBigHeight = 481;
    private float mBgSmallHeight = 260;
    private float mLoginY = (mBgBigHeight - mBgSmallHeight) / 2;
    private boolean isUser = false;
    private boolean mIsUpper = false;

    private SdkKeyboardEnglish mSdkKeyboardEnglish;
    private SdkKeyboardNumber mSdkKeyboardNumber;
    private SdkKeyboardSymbol mSdkKeyboardSymbol;

    private String mUsername = "用户名";
    private String mPassword = "密码";

    private int mLength = 20; //输入框能输入的最大字符数
    private String apiChar = "0p9o8i7u";

    /**
     * 实例化
     *
     * @param context Context上下文
     * @author linzanxian  @Date 2015-10-10 下午3:37:48
     */
    public SdkDoubleLoginViewPage(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    protected GLRectView createView(GLExtraData data) {
        //背景
        GLRelativeView relativeView = new GLRelativeView(mContext);
        relativeView.setDepth(mBgDepth);
        relativeView.setLayoutParams(2400, 2400);
        relativeView.setBackground(R.drawable.sdk_gl_login_activity_bg);

        //登录框
        mBgLinearView = new GLLinearView(mContext);
        mBgLinearView.setOrientation(GLOrientation.VERTICAL);
        mBgLinearView.setDepth(mLoginDepth);
        mBgLinearView.setLayoutParams(282, mBgSmallHeight);
        mBgLinearView.setBackground(R.drawable.sdk_gl_login_bg);

        //返回键
        GLImageView backImageView = new GLImageView(mContext);
        backImageView.setDepth(mLoginDepth);
        backImageView.setLayoutParams(37, 37);
        backImageView.setMargin(15, 10, 0, 0);
        backImageView.setBackground(R.drawable.sdk_gl_login_back);
        backImageView.setId("back");
        backImageView.setFocusable(true);
        backImageView.setFocusListener(focusListener);
        backImageView.setOnKeyListener(keyListener);

        //登录提示在登录标题与用户框输入之间
        mMsgTextView = new GLTextView(mContext);
        mMsgTextView.setDepth(mLoginDepth);
        mMsgTextView.setLayoutParams(242, 16);
        mMsgTextView.setAlignment(GLTextView.ALIGN_CENTER);
        mMsgTextView.setMargin(20, 10, 0, 0);
        mMsgTextView.setTextSize(14);
        //mMsgTextView.setText("帐号或密码错误！");
        mMsgTextView.setVisible(false);
        mMsgTextView.setTextColor(new GLColor(0.67f, 0.11f, 0.56f));

        //用户名输入框
        mUserTextView = new GLTextView(mContext);
        mUserTextView.setDepth(mLoginDepth);
        mUserTextView.setLayoutParams( 242, 47);
        mUserTextView.setMargin(20, 5, 0, 0);
        mUserTextView.setTextSize(17);
        mUserTextView.setText("用户名");
        mUserTextView.setPadding(15, 15, 0, 0);
        mUserTextView.setTextColor(new GLColor(0.58f, 0.58f, 0.6f));
        mUserTextView.setBackground(R.drawable.sdk_gl_login_edit_bg);
        mUserTextView.setFocusable(true);
        mUserTextView.setFocusListener(focusListener);
        mUserTextView.setOnKeyListener(keyListener);
        mUserTextView.setId("user");

        //密码输入框
        mPassTextView = new GLTextView(mContext);
        mPassTextView.setDepth(mLoginDepth);
        mPassTextView.setLayoutParams(242, 47);
        mPassTextView.setMargin(20, 8, 0, 0);
        mPassTextView.setTextSize(17);
        mPassTextView.setText("密码");
        mPassTextView.setPadding(15, 15, 0, 0);
        mPassTextView.setTextColor(new GLColor(0.58f, 0.58f, 0.6f));
        mPassTextView.setBackground(R.drawable.sdk_gl_login_edit_bg);
        mPassTextView.setFocusable(true);
        mPassTextView.setFocusListener(focusListener);
        mPassTextView.setOnKeyListener(keyListener);
        mPassTextView.setId("pass");

        //登录按钮
        GLImageView buttonImageView = new GLImageView(mContext);
        buttonImageView.setDepth(mLoginDepth);
        buttonImageView.setLayoutParams( 242, 47);
        buttonImageView.setMargin(20, 14, 0, 0);
        buttonImageView.setBackground(R.drawable.sdk_gl_login_button);
        buttonImageView.setFocusable(true);
        buttonImageView.setFocusListener(focusListener);
        buttonImageView.setOnKeyListener(keyListener);
        buttonImageView.setId("button");

        //键盘
        mKeyboardBgView = new GLLinearView(mContext);
        mKeyboardBgView.setOrientation(GLOrientation.VERTICAL);
        mKeyboardBgView.setDepth(mLoginDepth);
        mKeyboardBgView.setLayoutParams( 256, 218);
        mKeyboardBgView.setMargin(13, 0, 0, 0);
        mKeyboardBgView.setVisible(false);

        mBgLinearView.addView(backImageView);
        mBgLinearView.addView(mMsgTextView);
        mBgLinearView.addView(mUserTextView);
        mBgLinearView.addView(mPassTextView);
        mBgLinearView.addView(buttonImageView);
        mBgLinearView.addView(mKeyboardBgView);

        relativeView.addViewCenter(mBgLinearView);
        mUserTextView.requestFocus();

        return relativeView;
    }

    /**
     * 焦点监听处理
     */
    GLViewFocusListener focusListener = new GLViewFocusListener() {

        @Override
        public void onFocusChange(GLRectView view, boolean focusd) {
            String id = view.getId();

            if (id.equals("back")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_back_select);
                } else {
                    view.setBackground(R.drawable.sdk_gl_login_back);
                }
            } else if (id.equals("user") || id.equals("pass")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_edit_select_bg);
                } else {
                    view.setBackground(R.drawable.sdk_gl_login_edit_bg);
                }
            } else if (id.equals("button")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_select_button);
                } else {
                    view.setBackground(R.drawable.sdk_gl_login_button);
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
            if (keycode != MojingKeyCode.KEYCODE_DPAD_CENTER) {
                return false;
            }
            String id = view.getId();

            if (id.equals("back")) {
                ((SdkDoubleLoginActivity) mContext).finish();
            }
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

            if (id.equals("back")) {
//                ((SdkDoubleLoginActivity) mContext).finish();
            } else if (id.equals("user") || id.equals("pass")) {
                GLTextView textView = (GLTextView) view;
                if (id.equals("user")) {
                    isUser = true;
                    if (textView.equals("用户名")) {
                        mUserTextView.setText("");
                        mUsername = "";
                    }
                } else {
                    isUser = false;
                    if (textView.equals("密码")) {
                        mPassTextView.setText("");
                        mPassword = "";
                    }
                }

                showAnimation(true);
            } else if (id.equals("button")) {
                if (mUsername.equals("用户名") || mUsername.equals("")
                        || mPassword.equals("密码") || mPassword.equals("")) {
                    MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                            new Runnable() {
                                @Override
                                public void run() {
                                    mMsgTextView.setText("输入的用户名或密码不能为空");
                                    mMsgTextView.setVisible(true);
                                }
                            });
                    return true;
                } else if (mUsername.length() < 4 || mUsername.length() > 30) {
                    MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                            new Runnable() {
                                @Override
                                public void run() {
                                    mMsgTextView.setText("请输入4-30位的用户名");
                                    mMsgTextView.setVisible(true);
                                }
                            });
                    return true;
                } else if (mPassword.length() < 6 || mPassword.length() > 32) {
                    MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                            new Runnable() {
                                @Override
                                public void run() {
                                    mMsgTextView.setText("请输入6-32位的密码");
                                    mMsgTextView.setVisible(true);
                                }
                            });
                    return true;
                }

                //登录操作

                String url = URLConfig.SDK_LOGIN_REPORT_URL;

                List<String> keysList = new ArrayList<String>();
                keysList.add("login_name");
                keysList.add("login_pwd");

                List<String> valuesList = new ArrayList<String>();
                valuesList.add(mUsername);
                valuesList.add(mPassword);

                //verify
                String verify = "";
                int size = valuesList.size();
                for (int i = 0; i < size; i++) {
                    verify += valuesList.get(i) + "&";
                }

                //Log.d("test2", verify);
                keysList.add("open_verify");
                valuesList.add(MD5Util.MD5(verify.substring(0, verify.length() - 1) + apiChar + getDate()));

                //openId
                String openId = "{";
                size = keysList.size();
                for (int i = 0; i < size; i++) {
                    openId += "\"" + keysList.get(i) + "\":\"" + valuesList.get(i) + "\",";
                }
                openId = openId.substring(0, openId.length() - 1) + "}";

                //参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("open_id", urlEncode(openId));

                OkHttpUtil.postAsyn(url, new OkHttpUtil.ResultCallback<JSONObject>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mMsgTextView.setText("网络异常，请稍候再试！");
                                        mMsgTextView.setVisible(true);
                                    }
                                });
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject, String url) {
                        if (jsonObject == null) {
                            MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            mMsgTextView.setText("网络异常，请稍候再试！");
                                            mMsgTextView.setVisible(true);
                                        }
                                    });

                            return;
                        }

                        try {
                            boolean result = jsonObject.getBoolean("status");
                            if (result) {

                                MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                mMsgTextView.setText("登录成功！");
                                                mMsgTextView.setVisible(true);
                                            }
                                        });

                                JSONObject object = jsonObject.getJSONObject("data");
                                String userNo = object.getString("user_no");

                                //回调u3d
                                ((SdkDoubleLoginActivity) mContext).onLoginCallback(userNo);
                            } else {

                                final String msg = jsonObject.getString("msg");
                                MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                mMsgTextView.setText(msg);
                                                mMsgTextView.setVisible(true);
                                            }
                                        });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, params);
            }
            return false;
        }
    };

    /**
     * 显示动画
     *
     * @param isShow 是否显示键盘
     * @return void
     * @author linzanxian  @Date 2015-10-10 下午3:38:42
     */
    private void showAnimation(boolean isShow) {
        if (isShow) { //显示键盘
            if (!isKeyboard) {
                isKeyboard = true;

//				mUserTextView.setFocusable(false);
//				mPassTextView.setFocusable(false);

                mKeyboardBgView.setVisible(true);
                mKeyboardBgView.requestFocus();
                createEnglistKeyboard(true);

                mBgLinearView.setHeight(mBgBigHeight);
                mBgLinearView.setBackground(R.drawable.sdk_gl_login_big_bg);

                GLAnimation animation = new GLTranslateAnimation(0, -mLoginY, 0);
                animation.setAnimView(mBgLinearView);
                animation.setDuration(300);
                mBgLinearView.startAnimation(animation);


//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							Thread.sleep(1000);
//						} catch (Exception e) {
//							mSdkKeyboardEnglish.setDefaultFocus();
//						}
//					}
//				});
            }
        } else { //隐藏键盘
            if (isKeyboard) {
                mKeyboardBgView.setVisible(false);
                mBgLinearView.setHeight(mBgSmallHeight);
                mBgLinearView.setBackground(R.drawable.sdk_gl_login_bg);

                GLAnimation animation = new GLTranslateAnimation(0, mLoginY, 0);
                animation.setAnimView(mBgLinearView);
                animation.setDuration(300);
                mBgLinearView.startAnimation(animation);

                isKeyboard = false;
            }
        }
    }

    /**
     * 创建英文键盘
     *
     * @param isUpper 是否大写
     * @return void
     * @author linzanxian  @Date 2015-10-10 下午5:11:14
     */
    public void createEnglistKeyboard(final boolean isUpper) {
        mIsUpper = isUpper;
        mKeyboardBgView.removeAllView();
        createKeyboardDown();

        mSdkKeyboardEnglish = new SdkKeyboardEnglish(mContext, isUpper);
        mKeyboardBgView.addView(mSdkKeyboardEnglish);

        mSdkKeyboardEnglish.setDefaultFocus();
    }

    /**
     * 创建英文键盘
     *
     * @return void
     * @author linzanxian  @Date 2015-10-10 下午5:11:14
     */
    public void createNumberKeyboard() {
        mKeyboardBgView.removeAllView();
        createKeyboardDown();

        mSdkKeyboardNumber = new SdkKeyboardNumber(mContext);
        mKeyboardBgView.addView(mSdkKeyboardNumber);

        mSdkKeyboardNumber.setDefaultFocus();
    }

    /**
     * 创建符号键盘
     *
     * @return void
     * @author linzanxian  @Date 2015-10-10 下午5:11:14
     */
    public void createSymbolKeyboard() {
        mKeyboardBgView.removeAllView();
        createKeyboardDown();

        mSdkKeyboardSymbol = new SdkKeyboardSymbol(mContext);
        mKeyboardBgView.addView(mSdkKeyboardSymbol);

        mSdkKeyboardSymbol.setDefaultFocus();
    }

    /**
     * 创建键盘键头
     *
     * @return void
     * @author linzanxian  @Date 2015-10-10 下午5:31:07
     */
    private void createKeyboardDown() {
        GLImageView downImageView = new GLImageView(mContext);
        downImageView.setDepth(mLoginDepth);
        downImageView.setLayoutParams(17, 16.5f);
        downImageView.setMargin(238, 26, 0, 0);
        downImageView.setBackground(R.drawable.sdk_gl_login_key_down);

        mKeyboardBgView.addView(downImageView);
    }

    /**
     * 设置文本
     *
     * @param text 文本
     * @return void
     * @author linzanxian  @Date 2015-10-12 下午5:04:11
     */
    public void setText(final String text) {
        MJGLUtils.exeGLQueueEvent((BaseViewActivity) mContext,
                new Runnable() {
                    @Override
                    public void run() {
                        if (text.equals("123")) { //数字键盘
                            createNumberKeyboard();
                        } else if (text.equals("fuhao")) { //符号键盘
                            createSymbolKeyboard();
                        } else if (text.equals("sure")) { //结束
                            showAnimation(false);

                            mUserTextView.setFocusable(true);
                            mPassTextView.setFocusable(true);
                            if (isUser) {
                                mUserTextView.requestFocus();
                            } else {
                                mPassTextView.requestFocus();
                            }
                        } else if (text.equals("upper")) { //大小写
                            if (mIsUpper) {
                                createEnglistKeyboard(false);
                            } else {
                                createEnglistKeyboard(true);
                            }
                        } else {
                            String addText = text;
                            if (text.equals("space")) { //空格
                                addText = " ";
                            }

                            if (isUser) {
                                String oldText = mUsername;
                                if (oldText.equals("用户名")) {
                                    oldText = "";
                                    mUsername = "";
                                }
                                if (text.equals("delete")) {
                                    if (!oldText.equals("")) {
                                        mUsername = oldText.substring(0, oldText.length() - 1);
                                        String showName = mUsername;
                                        if (oldText.length() > mLength) {
                                            showName = mUsername.substring(mUsername.length() - mLength, mUsername.length());
                                        }

                                        mUserTextView.setText(showName);
                                    }
                                } else {
                                    if (oldText.length() >= 30) {
                                        return;
                                    }

                                    mUsername = oldText + addText;
                                    String showName = mUsername;
                                    if (mUsername.length() > mLength) {
                                        showName = mUsername.substring(mUsername.length() - mLength, mUsername.length());
                                    }
                                    mUserTextView.setText(showName);
                                }
                            } else {
                                String oldText = mPassword;
                                if (oldText.equals("密码")) {
                                    oldText = "";
                                    mPassword = "";
                                }
                                if (text.equals("delete")) {
                                    if (!oldText.equals("")) {
                                        mPassword = oldText.substring(0, oldText.length() - 1);
                                        String showName = mPassword;
                                        if (oldText.length() > mLength) {
                                            showName = mPassword.substring(mPassword.length() - mLength, mPassword.length());
                                        }

                                        mPassTextView.setText(showName);
                                    }
                                } else {
                                    if (oldText.length() >= 32) {
                                        return;
                                    }

                                    mPassword = oldText + addText;
                                    String showName = mPassword;
                                    if (mPassword.length() > mLength) {
                                        showName = mPassword.substring(mPassword.length() - mLength, mPassword.length());
                                    }
                                    mPassTextView.setText(showName);
                                }
                            }
                        }
                    }
                }
        );

    }

    /**
     * 获取时间
     *
     * @return String
     * @author linzanxian  @Date 2015-10-13 下午4:47:23
     */
    @SuppressLint("SimpleDateFormat")
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date);
    }

    /**
     * urlencode
     *
     * @param str 字符串
     * @return String
     * @author linzanxian  @Date 2015-10-13 下午3:38:27
     */
    private String urlEncode(String str) {
        try {
            str = URLEncoder.encode(str, "utf-8");
            str = str.replaceAll("%3A", ":").replaceAll("%2F", "/")
                    .replaceAll("%3F", "?").replaceAll("%3D", "=")
                    .replaceAll("%26", "&").replaceAll("\\+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 获取是否大写
     *
     * @return
     */
    public boolean getIsUpper() {
        return mIsUpper;
    }
}
