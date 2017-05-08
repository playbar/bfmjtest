package com.baofeng.mojing.sdk.login.widget;

import android.content.Context;

import com.baofeng.mjgl.pubblico.layout.SdkDoubleLoginActivity;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.baofeng.mojing.sdk.login.R;
import com.baofeng.mojing.sdk.login.adapter.SdkKeyboardSymbolAdapter;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant.GLOrientation;
import com.bfmj.viewcore.view.GLGridView;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.ArrayList;
import java.util.List;

public class SdkKeyboardSymbol extends GLLinearView {
    private Context mContext;
    private GLGridView mGridView;
    private List<String> mListEn = new ArrayList<String>();
    private List<String> mListZn = new ArrayList<String>();
    private boolean isEn = true;
    private SdkKeyboardSymbolAdapter mAdapter;
    private GLColor mKeyboardTextColor = new GLColor(0.56f, 0.57f, 0.67f);

    private GLLinearView linearView;
    private GLLinearView linearView2;
    private GLLinearView linearView3;
    private GLLinearView linearView4;

    private int mIndex = 0; //页索引

    public SdkKeyboardSymbol(Context context) {
        super(context);

        mContext = context;

        this.setOrientation(GLOrientation.VERTICAL);
        this.setLayoutParams(256, 218);
        this.setMargin(0, 10, 0, 0);

        initListEn();
        initListZn();
        createLine1();
        createLine2();

        this.setDepth(2.5f);
    }

    private void initListEn() {
        mListEn.add(".");
        mListEn.add(",");
        mListEn.add("?");
        mListEn.add("!");
        mListEn.add("|");
        mListEn.add("@");
        mListEn.add("...");
        mListEn.add(":");
        mListEn.add(";");
        mListEn.add("\"");
        mListEn.add("/");
        mListEn.add("_");
        mListEn.add("-");
        mListEn.add("+");
        mListEn.add("`");
        mListEn.add("()");

        mListEn.add("(");
        mListEn.add(")");
        mListEn.add("=");
        mListEn.add("\\");
        mListEn.add("~");
        mListEn.add("^");
        mListEn.add("#");
        mListEn.add("*");
        mListEn.add("%");
        mListEn.add("&");
        mListEn.add("|");
        mListEn.add("{}");
        mListEn.add("{");
        mListEn.add("}");
        mListEn.add("[]");
        mListEn.add("<>");

        mListEn.add("[");
        mListEn.add("]");
        mListEn.add("<");
        mListEn.add(">");
        mListEn.add("$");
    }

    private void initListZn() {
        mListZn.add("，");
        mListZn.add("。");
        mListZn.add("？");
        mListZn.add("！");
        mListZn.add("、");
        mListZn.add("@");
        mListZn.add("：");
        mListZn.add("·");
        mListZn.add("......");
        mListZn.add("“”");
        mListZn.add("“");
        mListZn.add("”");
        mListZn.add("：");
        mListZn.add("（）");
        mListZn.add("（");
        mListZn.add("）");

        mListZn.add("《》");
        mListZn.add("《");
        mListZn.add("》");
        mListZn.add("''");
        mListZn.add("'");
        mListZn.add("'");
        mListZn.add("_");
        mListZn.add("-");
        mListZn.add("%");
        mListZn.add("~");
        mListZn.add("--");
        mListZn.add("&");
        mListZn.add("#");
        mListZn.add("*");
        mListZn.add("{");
        mListZn.add("}");

        mListZn.add("【】");
        mListZn.add("【");
        mListZn.add("】");
        mListZn.add("￥");
    }

    /**
     * 创建第一行
     *
     * @return void
     * @author linzanxian  @Date 2015-10-12 下午2:54:22
     */
    private void createLine1() {
        linearView = new GLLinearView(mContext);
        linearView.setOrientation(GLOrientation.HORIZONTAL);
        linearView.setLayoutParams(256, 122);

        linearView2 = new GLLinearView(mContext);
        linearView2.setOrientation(GLOrientation.VERTICAL);
        linearView2.setBackground(R.drawable.sdk_gl_login_key_bg9);
        linearView2.setLayoutParams(47, 122);

        GLTextView textView;
        textView = new GLTextView(mContext);
        textView.setLayoutParams(37, 26);
        textView.setTextSize(16);
        textView.setText("中");
//        textView.setTextPadding(2);
        textView.setTextColor(mKeyboardTextColor);
        textView.setFocusable(true);
        textView.setFocusListener(focusListener);
        textView.setOnKeyListener(keyListener);
        textView.setId("zn");
        textView.setPadding(10, 2.5f, 0, 0);
        textView.setMargin(5, 32, 0, 0);
        linearView2.addView(textView);

        textView = new GLTextView(mContext);
        textView.setLayoutParams(37, 26);
        textView.setTextSize(16);
        textView.setText("En");
        textView.setTextPadding(0);
        textView.setTextColor(mKeyboardTextColor);
        textView.setFocusable(true);
        textView.setFocusListener(focusListener);
        textView.setOnKeyListener(keyListener);
        textView.setId("en");
        textView.setPadding(9, 2.5f, 0, 0);
        textView.setMargin(5, 4, 0, 0);
        linearView2.addView(textView);

//		mGridView = new GLGridView(mContext);
//		mGridView.setLayoutParams(207, 122);
//		mGridView.setNumColumns(4);
//		mGridView.setHorizontalSpacing(5);
//		mGridView.setNumRows(4);
//		mGridView.setMargin(5, 0, 0, 0);
//		mGridView.setBackground(R.drawable.sdk_gl_login_key_bg10);
//		mGridView.setFocusable(true);
//		
//		mAdapter = new SdkKeyboardSymbolAdapter(mContext, mListEn.subList(0, 16));
//		mGridView.setAdapter(mAdapter);

        linearView3 = new GLLinearView(mContext);
        linearView3.setLayoutParams(207, 122);
        linearView3.setBackground(R.drawable.sdk_gl_login_key_bg10);
        linearView3.setOrientation(GLOrientation.VERTICAL);
        linearView3.setMargin(2, 0, 0, 0);

        createRight();

        linearView.addView(linearView2);
        linearView.addView(linearView3);
//		linearView.addView(mGridView);


        this.addView(linearView);
    }

    /**
     * 创建右侧键盘
     *
     * @return void
     * @author linzanxian  @Date 2015-10-14 下午2:35:37
     */
    private void createRight() {
        //linearView3.removeAllView();

        createRightLine(1);
        createRightLine(2);
        createRightLine(3);
        createRightLine(4);
    }

    /**
     * 修改右侧键盘
     *
     * @return void
     * @author linzanxian  @Date 2015-10-14 下午4:05:46
     */
    private void updateRight() {
        updateRightLine(1);
        updateRightLine(2);
        updateRightLine(3);
        updateRightLine(4);
    }

    private void updateRightLine(int line) {
        GLLinearView linearView = (GLLinearView) linearView3.getView(line - 1);

        List<String> tempList = new ArrayList<String>();
        if (isEn) {
            tempList = mListEn;
        } else {
            tempList = mListZn;
        }

        int iNum = mIndex * 16 + (line - 1) * 4;
        int iCount = iNum + 4;
        if (iNum >= tempList.size()) {
            return;
        }
        if (iCount > tempList.size()) {
            iCount = tempList.size();
        }

        GLTextView textView;
        for (int i = iNum; i < iCount; i++) {
            textView = (GLTextView) linearView.getView(i % 4);
            textView.setText(tempList.get(i));
            textView.setId(tempList.get(i));
        }
    }

    /**
     * 添加右侧行
     *
     * @param line 行数 从1开始
     * @return void
     * @author linzanxian  @Date 2015-10-14 上午11:37:05
     */
    private void createRightLine(int line) {
        GLLinearView linearView = new GLLinearView(mContext);
        linearView.setLayoutParams(207, 30);
        linearView.setId("line" + line);

        List<String> tempList = new ArrayList<String>();
        if (isEn) {
            tempList = mListEn;
        } else {
            tempList = mListZn;
        }

        int iNum = mIndex * 16 + (line - 1) * 4;
        int iCount = iNum + 4;
        if (iNum >= tempList.size()) {
            return;
        }
        if (iCount > tempList.size()) {
            iCount = tempList.size();
        }

        GLTextView textView;
        for (int i = iNum; i < iCount; i++) {
            textView = createRightText(tempList.get(i), i);

            if (i % 4 != 0) {
                textView.setMargin(4, 0, 0, 0);
            } else {
                textView.setMargin(3, 0, 0, 0);
            }
            linearView.addView(textView);
        }

        linearView3.addView(linearView);
    }

    /**
     * 创建text item
     *
     * @return void
     * @author linzanxian  @Date 2015-10-14 上午11:40:35
     */
    private GLTextView createRightText(String value, int i) {
        GLTextView textView = new GLTextView(mContext);
        textView.setLayoutParams( 47, 30);
        textView.setTextSize(16);
        textView.setText(value);
        textView.setTextPadding(0);
        textView.setTextColor(mKeyboardTextColor);
        textView.setFocusable(true);
        textView.setFocusListener(focusListener);
        textView.setOnKeyListener(keyListener);
        textView.setId(value);
        textView.setPadding(20, 5, 0, 0);
        textView.setTag("h" + i);

        return textView;
    }

    /**
     * 创建第二行
     *
     * @return void
     * @author linzanxian  @Date 2015-10-12 下午2:54:38
     */
    private void createLine2() {
        linearView4 = new GLLinearView(mContext);
        linearView4.setOrientation(GLOrientation.HORIZONTAL);
        linearView4.setLayoutParams(47, 37);
        linearView4.setMargin(0, 5, 0, 0);

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
        linearView4.addView(imageView);

        imageView = new GLImageView(mContext);
        imageView.setLayoutParams(47, 37);
        imageView.setBackground(R.drawable.sdk_gl_login_key_bg7);
        imageView.setImage(R.drawable.sdk_gl_login_key_space_icon);
        imageView.setId("space");
        imageView.setPadding(15f, 15, 15f, 15);
        imageView.setFocusable(true);
        imageView.setFocusListener(focusListener);
        imageView.setOnKeyListener(keyListener);
        imageView.setMargin(3, 0, 0, 0);
        linearView4.addView(imageView);

        imageView = new GLImageView(mContext);
        imageView.setLayoutParams( 47, 37);
        imageView.setBackground(R.drawable.sdk_gl_login_key_bg7);
        imageView.setImage(R.drawable.sdk_gl_login_key_up_no_icon);
        imageView.setId("up");
        imageView.setPadding(13.5f, 11, 13.5f, 11);
        imageView.setFocusable(true);
        imageView.setFocusListener(focusListener);
        imageView.setOnKeyListener(keyListener);
        imageView.setMargin(3, 0, 0, 0);
        linearView4.addView(imageView);

        imageView = new GLImageView(mContext);
        imageView.setLayoutParams(47, 37);
        imageView.setBackground(R.drawable.sdk_gl_login_key_bg7);
        imageView.setImage(R.drawable.sdk_gl_login_key_down_yes_icon);
        imageView.setId("down");
        imageView.setPadding(13.5f, 11, 13.5f, 11);
        imageView.setFocusable(true);
        imageView.setFocusListener(focusListener);
        imageView.setOnKeyListener(keyListener);
        imageView.setMargin(3, 0, 0, 0);
        linearView4.addView(imageView);

        //删除
        imageView = new GLImageView(mContext);
        imageView.setLayoutParams(57, 37);
        imageView.setBackground(R.drawable.sdk_gl_login_key_bg6);
        imageView.setImage(R.drawable.sdk_gl_login_key_delete);
        imageView.setId("delete");
        imageView.setPadding(15f, 12, 15f, 12);
        imageView.setFocusable(true);
        imageView.setFocusListener(focusListener);
        imageView.setOnKeyListener(keyListener);
        imageView.setMargin(3, 0, 0, 0);
        linearView4.addView(imageView);

        this.addView(linearView4);
    }

    /**
     * 焦点监听处理
     */
    GLViewFocusListener focusListener = new GLViewFocusListener() {

        @Override
        public void onFocusChange(GLRectView view, boolean focusd) {
            String id = view.getId();

            if (id.equals("delete")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_key_select_bg6);
                } else {
                    view.setBackground(R.drawable.sdk_gl_login_key_bg6);
                }
            } else if (id.equals("en") || id.equals("zn")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_key_bg12);
                } else {
                    view.setBackground(new GLColor(0,0,0,0));
                }
            } else if (id.equals("back") || id.equals("space") || id.equals("up") || id.equals("down")) {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_key_select_bg7);
                } else {
                    view.setBackground(R.drawable.sdk_gl_login_key_bg7);
                }
            } else {
                if (focusd) {
                    view.setBackground(R.drawable.sdk_gl_login_key_bg11);
                } else {
                    view.setBackground(new GLColor(0,0,0,0));
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
                if (keycode != MojingKeyCode.KEYCODE_DPAD_CENTER) {
                    String id = view.getId();

                    String tag = "";
                    if (view instanceof GLTextView) {
                        GLTextView textView = (GLTextView) view;
                        tag = textView.getTag();
                    }

                    if (keycode == MojingKeyCode.KEYCODE_DPAD_UP) {
                        if (id.equals("space")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (id.equals("up")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (id.equals("down")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (id.equals("delete")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h12")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h13")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h14")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h15")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h8")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h9")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h10")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h11")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h4")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(0);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h5")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(0);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h6")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(0);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h7")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(0);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h0") || tag.equals("h1") || tag.equals("h2") || tag.equals("h3") || id.equals("zn")) {
                            return true;
                        }
                    } else if (keycode == MojingKeyCode.KEYCODE_DPAD_DOWN) {
                        if (tag.equals("h0")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h1")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h2")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h3")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(1);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h4")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h5")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h6")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h7")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(2);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h8")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(0);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h9")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(1);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h10")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(2);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h11")) {
                            GLLinearView linearView = (GLLinearView) linearView3.getView(3);

                            GLTextView textView = (GLTextView) linearView.getView(3);
                            textView.requestFocus();
                            return true;
                        } else if (tag.equals("h12")) {
                            GLImageView imageView = (GLImageView) linearView4.getView(1);
                            imageView.requestFocus();
                            return true;
                        } else if (tag.equals("h13")) {
                            GLImageView imageView = (GLImageView) linearView4.getView(2);
                            imageView.requestFocus();
                            return true;
                        } else if (tag.equals("h14")) {
                            GLImageView imageView = (GLImageView) linearView4.getView(3);
                            imageView.requestFocus();
                            return true;
                        } else if (tag.equals("h15")) {
                            GLImageView imageView = (GLImageView) linearView4.getView(4);
                            imageView.requestFocus();
                            return true;
                        }
                    }

                    return false;
                }

                String id = view.getId();

                if (id.equals("back")) {
                    ((SdkDoubleLoginActivity) mContext).createEnglistKeyboard(true);
                } else if (id.equals("up")) {
                    if (mIndex > 0) {
                        --mIndex;
                        updateRight();

                        GLImageView imageView;
                        imageView = (GLImageView) linearView4.getView("down");
                        imageView.setImage(R.drawable.sdk_gl_login_key_down_yes_icon);

                        imageView = (GLImageView) linearView4.getView("up");
                        if (mIndex > 0) {
                            imageView.setImage(R.drawable.sdk_gl_login_key_up_yes_icon);
                        } else {
                            imageView.setImage(R.drawable.sdk_gl_login_key_up_no_icon);
                        }

                        return true;
                    }
                } else if (id.equals("down")) {
                    GLImageView imageView;
                    imageView = (GLImageView) linearView4.getView("up");
                    imageView.setImage(R.drawable.sdk_gl_login_key_up_yes_icon);

                    List<String> tempList = new ArrayList<String>();
                    if (isEn) {
                        tempList = mListEn;
                    } else {
                        tempList = mListZn;
                    }
                    int total = (tempList.size() % 16 == 0) ? tempList.size() / 16 : (tempList.size() / 16 + 1);
                    if (mIndex >= (total - 1)) {
                        return false;
                    }

                    ++mIndex;
                    updateRight();

                    if (mIndex >= (total - 1)) {
                        imageView = (GLImageView) linearView4.getView("down");
                        imageView.setImage(R.drawable.sdk_gl_login_key_down_no_icon);


                    }
                } else if (id.equals("zn")) {
                    mIndex = 0;
                    isEn = false;

                    updateRight();

                    GLImageView imageView;
                    imageView = (GLImageView) linearView4.getView("up");
                    imageView.setImage(R.drawable.sdk_gl_login_key_up_no_icon);

                    imageView = (GLImageView) linearView4.getView("down");
                    imageView.setImage(R.drawable.sdk_gl_login_key_down_yes_icon);
                } else if (id.equals("en")) {
                    mIndex = 0;
                    isEn = true;

                    updateRight();

                    GLImageView imageView;
                    imageView = (GLImageView) linearView4.getView("up");
                    imageView.setImage(R.drawable.sdk_gl_login_key_up_no_icon);

                    imageView = (GLImageView) linearView4.getView("down");
                    imageView.setImage(R.drawable.sdk_gl_login_key_down_yes_icon);
                } else {
                    ((SdkDoubleLoginActivity) mContext).setText(id);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

    };

    /**
     * 默认焦点
     *
     * @return void
     * @author linzanxian  @Date 2015-10-12 下午4:49:06
     */
    public void setDefaultFocus() {
        if (linearView2 != null) {
            GLTextView textView = (GLTextView) linearView2.getView(1);
            textView.requestFocus();
        }
    }
}
