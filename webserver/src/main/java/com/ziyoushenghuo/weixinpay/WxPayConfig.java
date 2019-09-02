package com.ziyoushenghuo.weixinpay;


public class WxPayConfig {

    //小程序appid
    public static final String appid = "wxacdc9acc9937a90b";
    //微信支付的商户id
    public static final String mch_id = "1514558261";
    //微信支付的商户密钥
    public static final String key = "TIANQUXIAOJI1988tianquxiaoji0209";

    public static final String APP_SECRET = "c7b4060be033f0106e7de6c1f9fa6f5c";


    public static final String companypaykey = "TIANQUXIAOJI1988tianquxiaoji0209";
    //支付成功后的服务器回调url
    public static final String pay_notify_url = "https://www.weiruikj.cn/ziyoutechan/pay/paynotify";
    public static final String refund_notify_url = "https://www.weiruikj.cn/ziyoutechan/pay/refundnotify";
    //签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    //交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String query_url = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static final String refund_url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String refund_query = "https://api.mch.weixin.qq.com/pay/refundquery";
    public static final String closeorder_url = "https://api.mch.weixin.qq.com/pay/closeorder";
    public static final String withdrew_url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/paywwsptrans2pocket";
    public static final String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";


    public static final String cpay_url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    public static final int pay_rate = 6;


    public static final String owner_appid = "wx7e187f85097231ba";
    public static final String OWNER_APP_SECRET = "3527e91e49d6c0622dad4c48ce2ac061";



}
