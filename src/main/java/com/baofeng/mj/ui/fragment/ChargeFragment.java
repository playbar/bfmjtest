package com.baofeng.mj.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.UserPayActivity;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;
import com.baofeng.mj.utils.StringUtils;

import java.util.List;

/**
 * Created by hanyang on 2016/5/11.
 * 魔豆充值
 */
public class ChargeFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private EditText charge_no, charge_tel;
    private TextView charge_count, buy_no, charge_mobile_tag;
    private Button charge_btn;
    private String mPhoneNumber;
    private Toast mToast;
    //魔豆充值号码是否合法
    private boolean isMobileValid = false;
    private boolean isModouValid = false;
    public final int MODOU_MAX_ONCE = 10000;
    public final float MODOU_MIN_ONCE = 0.1f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhoneNumber = UserSpBusiness.getInstance().getMobile();
        rootView = inflater.inflate(R.layout.charge_view, null);
        charge_tel = (EditText) rootView.findViewById(R.id.charge_tel);
        charge_no = (EditText) rootView.findViewById(R.id.charge_no);
        charge_count = (TextView) rootView.findViewById(R.id.charge_count);
        charge_btn = (Button) rootView.findViewById(R.id.charge_btn);
        buy_no = (TextView) rootView.findViewById(R.id.buy_no);
        charge_mobile_tag = (TextView) rootView.findViewById(R.id.charge_mobile_tag);
        charge_mobile_tag.setText(LanguageValue.getInstance().getValue(getContext(), "SID_PHONE_NUM"));
        initChargeView();
        return rootView;
    }

    private void initChargeView() {
        buy_no.setText(LanguageValue.getInstance().getValue(getContext(), "SID_BUY_NUM"));
        charge_btn.setOnClickListener(this);
        if (!StringUtils.isEmpty(mPhoneNumber)) {
            charge_tel.setText(mPhoneNumber);
            charge_tel.setSelection(mPhoneNumber.length());
        }
        addEditListener(charge_no);
        addPhoneNumberTextChangedListener(charge_tel);
    }

    private void addEditListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String modouCount = s.toString();
                if (!TextUtils.isEmpty(modouCount) && Common.isNumeric(modouCount)) {
                    String decimalFormat = Common.getDecimalFormatStr(Float.parseFloat(modouCount) / 10, "0.00");
                    charge_count.setText(decimalFormat);
                    checkMoDou(modouCount, false);
                } else {
                    charge_count.setText("0.00");
                    isModouValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void addPhoneNumberTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString();
                if (phoneNumber.length() == 11) {
                    checkPhoneNumber(phoneNumber, true);
                } else {
                    isMobileValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.charge_btn == id) {
            // 判断是否是整数或者是携带一位或者两位的小数
            chargeOnClick(getContext(),
                    charge_tel.getText().toString().trim(),
                    charge_no.getText().toString().trim());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPhoneNumber(charge_tel.getText().toString().trim(), false);
        checkMoDou(charge_no.getText().toString().trim(), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void checkPhoneNumber(String phoneNumber, final boolean isCallback) {
        if (Common.isMobile(phoneNumber)) {
            new UserInfoApi().queryUserInfoByTel(phoneNumber, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
                @Override
                public void onSuccess(Response<List<SimpleUserInfo>> result) {
                    super.onSuccess(result);
                    if (result.status) {
                        isMobileValid = true;
                    } else {
                        isMobileValid = false;
                        if (isCallback) {
                            showToast("此手机号未注册,无法充值");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    isMobileValid = false;
                    if ((!NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE)) && isCallback) {
                        String hint = BaseApplication.INSTANCE.getResources().getString(R.string.network_exception);
                        showToast(hint);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onCache(Response<List<SimpleUserInfo>> result) {
                    super.onCache(result);
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                }
            });
        } else {
            isMobileValid = false;
            String hint = "";
            if (StringUtils.isEmpty(phoneNumber)) {
                hint = "请输入手机号";
            } else {
                hint = "手机号输入不正确";
            }
            if (isCallback) {
                showToast(hint);
            }
        }
    }

    private void checkMoDou(String str, boolean isCallback) {
        if (StringUtils.isEmpty(str) || !Common.isNumeric(str)) {
            isModouValid = false;
            if (isCallback) {
                showToast("请填写魔币数量");
            }
            return;
        }
        float count = Float.parseFloat(str);
        if (count > MODOU_MAX_ONCE) {
            if (isCallback) {
                showToast("每次最多充值10000魔币");
            }
            isModouValid = false;
        } else if (count < MODOU_MIN_ONCE || (count) % 1 != 0) {
            if (isCallback) {
                showToast("最少只能买1个魔币，且只能输入整数");
            }
            isModouValid = false;
        } else {
            isModouValid = true;
        }
    }

    public void chargeOnClick(Context context, String phoneNumber, String modou) {
        if (!isMobileValid) {
            checkPhoneNumber(phoneNumber, true);
        } else if (!isModouValid) {
            checkMoDou(modou, true);
        } else {
            startUserPayAcitivity(context, phoneNumber, modou);
        }
    }

    private void startUserPayAcitivity(Context context, String phoneNumber, String modou) {
        String money = Common.getDecimalFormatStr(Float.parseFloat(modou) / 10, "0.00");
        Intent in = new Intent(context, UserPayActivity.class);
        in.putExtra("modouNum", modou);
        in.putExtra("money", money);
        in.putExtra("mobile", phoneNumber);
        context.startActivity(in);
    }
}
