package com.baofeng.mj.bean;

import android.text.TextUtils;

/**
 * Created by zhaominglei on 2016/5/20.
 * 支付宝 支付返回结果
 */
public class PayZFBBean {
    /**
     * "{9000}", "支付成功")
     * ("{4000}", "系统异常")
     * ("{4001}", "订单参数错误")
     * ("{6001}", "您已取消了本次订单的支付")
     * ("{6002}", "网络连接异常")
     */
    private String resultStatus;
    private String result;
    private String memo;

    public PayZFBBean(String rawResult) {

        if (TextUtils.isEmpty(rawResult))
            return;

        String[] resultParams = rawResult.split(";");
        for (String resultParam : resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus");
            }
            if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result");
            }
            if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo");
            }
        }
    }

    @Override
    public String toString() {
        return "resultStatus={" + resultStatus + "};memo={" + memo
                + "};result={" + result + "}";
    }

    private String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(),
                content.lastIndexOf("}"));
    }

    /**
     * @return the resultStatus
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

}
