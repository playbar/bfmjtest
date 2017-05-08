package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ModouResponse;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.SimpleUserInfo;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.utils.StringUtils;

import java.util.List;

/**
 * 兑换礼券
 */
public class ExchangeFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private EditText excharge_no, excharge_tel;
    private Button exchange_confirm_btn;
    private String mPhoneNumber;
    private Toast mToast;
    private boolean isGiftCodeValid;
    private boolean isMobileValid;
    private SimpleUserInfo simpleUserInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhoneNumber = UserSpBusiness.getInstance().getMobile();
        rootView = inflater.inflate(R.layout.excharge_view, null);
        excharge_tel = (EditText) rootView.findViewById(R.id.excharge_tel);
        excharge_no = (EditText) rootView.findViewById(R.id.excharge_no);
        exchange_confirm_btn = (Button) rootView.findViewById(R.id.exchange_confirm_btn);
        initExchargeView();
        return rootView;
    }

    private void initExchargeView() {
        exchange_confirm_btn.setOnClickListener(this);
        if (!StringUtils.isEmpty(mPhoneNumber)) {
            excharge_tel.setText(mPhoneNumber);
            excharge_tel.setSelection(mPhoneNumber.length());
        }
        addPhoneNumberTextChangedListener(excharge_tel);
        addEditListener(excharge_no);
    }

    private void addEditListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String giftcode = s.toString().trim();
                if (giftcode.length() == 16) {
                    checkExchangeCode(giftcode, false);
                }else{
                    isGiftCodeValid=false;
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
                    checkPhoneNumber(phoneNumber, false);
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
        if (R.id.exchange_confirm_btn == id) {
            exchangeOnclick(excharge_no.getText().toString().trim(),
                    excharge_tel.getText().toString().trim());
        }
    }

    public void exchangeOnclick(String exchangeCode, String phoneNumber) {
        if (!isMobileValid) {
            checkPhoneNumber(phoneNumber, true);
        } else if (!isGiftCodeValid) {
            checkExchangeCode(exchangeCode, true);
        } else {
            exchangeCode(exchangeCode, phoneNumber);
        }
    }

    public void checkPhoneNumber(String phoneNumber, final boolean isCallback) {
        if (Common.isMobile(phoneNumber)) {
            new UserInfoApi().queryUserInfoByTel(phoneNumber, new ApiCallBack<Response<List<SimpleUserInfo>>>() {
                @Override
                public void onSuccess(Response<List<SimpleUserInfo>> result) {
                    super.onSuccess(result);
                    List<SimpleUserInfo> userInfo = result.data;
                    boolean status = result.status;
                    if (status && (userInfo != null && userInfo.size() > 0)) {
                        simpleUserInfo = userInfo.get(0);
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

    public void checkExchangeCode(String code, final boolean isCallback) {
        if (TextUtils.isEmpty(code)) {
            isGiftCodeValid = false;
            if (isCallback) {
                showToast("请输入兑换码");
            }
            return;
        }
        new UserInfoApi().queryExchangeByCode(code, new ApiCallBack<ModouResponse>() {
                    @Override
                    public void onSuccess(ModouResponse result) {
                        super.onSuccess(result);
                        if (result != null) {
                            int status = result.status;
                            String msg = result.msg;
                            if (status == 1) {
                                isGiftCodeValid = true;
                            } else {
                                isGiftCodeValid = false;
                                if (isCallback) {
                                    showToast(msg);
                                }
                            }
                        } else {
                            isGiftCodeValid = false;
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        isGiftCodeValid = false;
                        if (!NetworkUtil.isNetworkConnected(BaseApplication.INSTANCE)) {
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
                    public void onCache(ModouResponse result) {
                        super.onCache(result);
                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                    }
                }
        );
    }

    private void exchangeCode(String exchangeCode, String phoneNumber) {
        new UserInfoApi().exChangeGiftModou(exchangeCode, simpleUserInfo.user_no, phoneNumber, new ApiCallBack<ModouResponse>() {
            @Override
            public void onSuccess(ModouResponse result) {
                super.onSuccess(result);
                if (result.status == 0) {
                    //兑换失败
                } else if (result.status == 1) {
                    //兑换成功
                }
                if (!TextUtils.isEmpty(result.msg)) {
                    showToast(result.msg);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
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
            public void onCache(ModouResponse result) {
                super.onCache(result);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPhoneNumber(excharge_tel.getText().toString().trim(), false);
        checkExchangeCode(excharge_no.getText().toString().trim(), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
