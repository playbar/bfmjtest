package com.baofeng.mj.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.baofeng.mj.R;

/**
 * Created by zhaominglei on 2016/6/15.
 * 带清空
 */
public class ClearableEditText extends EditText {
    public String defaultValue = "";
    final Drawable imageClear = getResources().getDrawable(R.drawable.clear_btn_normal); // X image


    public ClearableEditText(Context context) {
        super(context);
        initView();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    private void initView() {

        imageClear.setBounds(0, 0, imageClear.getIntrinsicWidth(), imageClear.getIntrinsicHeight());
        manageClearButton();

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ClearableEditText et = ClearableEditText.this;

                if (et.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > et.getWidth() - et.getPaddingRight() - imageClear.getIntrinsicWidth()) {
                    et.setText("");
                    ClearableEditText.this.removeClearButton();
                }
                return false;
            }
        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ClearableEditText.this.manageClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    void manageClearButton() {
        if (this.getText().toString().equals("")){
            removeClearButton();
        }
        else
            addClearButton();
    }

    void addClearButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1],
                imageClear,
                this.getCompoundDrawables()[3]);
    }

    void removeClearButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1],
                null,
                this.getCompoundDrawables()[3]);
    }
}
