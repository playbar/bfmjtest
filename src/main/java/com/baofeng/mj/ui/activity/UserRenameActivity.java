package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.accountbusiness.UserInfoEditBusiness;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.view.TitleBar;

public class UserRenameActivity extends BaseActivity implements View.OnClickListener, UserInfoEditBusiness.IUserInfoEditCallback {

    private TitleBar titleBar;
    private EditText nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rename);
        findViewByIds();
        init();
    }

    private void findViewByIds() {
        titleBar = (TitleBar) findViewById(R.id.rl_title_bar);
        nickName = (EditText) findViewById(R.id.et_nick_name);
    }

    private void init() {
        titleBar.getRightBtn().setVisibility(View.GONE);
        titleBar.setTitleBarTitle(getResources().getString(R.string.change_nick_name));
        titleBar.getRightTv().setText(getResources().getString(R.string.save));
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getNikename())) {
            this.nickName.setText(userInfo.getNikename());
            this.nickName.setSelection(userInfo.getNikename().length());
        }
        titleBar.setOnClickListener(this);
        addTextChangedListener();
        UserInfoEditBusiness.getInstance().setUserInfoEditCallback(this);
    }

    private void addTextChangedListener() {
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back) {
            finish();
        } else if (vid == R.id.tv_right) {
            UserInfoEditBusiness.getInstance().updateNickName(nickName.getText().toString().trim());
        }
    }

    @Override
    public void onUserInfoEditCallback(int code, Object obj) {
        if (code == UserInfoEditBusiness.getInstance().NICK_NAME_NULL) {
            Toast.makeText(this, getResources().getString(R.string.input_nick_name), Toast.LENGTH_SHORT).show();
        } else if (code == UserInfoEditBusiness.getInstance().NICK_NAME_TOO_LONG) {
            Toast.makeText(this, getResources().getString(R.string.nick_name_too_long), Toast.LENGTH_SHORT).show();

        } else if (code == UserInfoEditBusiness.getInstance().NICK_NAME_TOO_SHORT) {
            Toast.makeText(this, getResources().getString(R.string.nick_name_too_short), Toast.LENGTH_SHORT).show();

        } else if (code == UserInfoEditBusiness.getInstance().NICK_NAME_UPDATE_FAIL) {
            if (obj != null && obj instanceof String) {
                Toast.makeText(this, (String) obj, Toast.LENGTH_SHORT).show();
            }
        } else if (code == UserInfoEditBusiness.getInstance().NET_EXCEPTION) {
            Toast.makeText(this, getResources().getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
        } else if (code == UserInfoEditBusiness.getInstance().NICK_NAME_INVALID) {
            Toast.makeText(this, getResources().getString(R.string.input_nick_name_invalid), Toast.LENGTH_SHORT).show();
        } else if (code == UserInfoEditBusiness.getInstance().NICK_NAME_UPDATE_SUCCESS) {
            Toast.makeText(this, getResources().getString(R.string.nick_name_success), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
