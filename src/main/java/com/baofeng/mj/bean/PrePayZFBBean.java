package com.baofeng.mj.bean;

/**
 * Created by zhaominglei on 2016/5/20.
 * 支付宝预支付类
 */
public class PrePayZFBBean {
    public String service;
    // 签约合作者身份ID
    public String partner;
    // 参数编码， 固定值
    public String _input_charset;
    public String sign_type;
    public String notify_url;
    // 商户网站唯一订单号
    public String out_trade_no;
    // 商品名称
    public String subject;
    // 支付类型， 固定值
    public String payment_type;
    // 签约卖家支付宝账号
    public String seller_id;
    // 商品金额
    public String total_fee;
    // 商品详情
    public String body;
    public String sign;


}
