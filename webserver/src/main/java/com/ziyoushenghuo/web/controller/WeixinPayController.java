package com.ziyoushenghuo.web.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.weixinpay.WxPayConfig;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import com.ziyoushenghuo.weixintemplete.TemplateUtils;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/pay"})
public class WeixinPayController {

    private final int SESSION_TIME = 30;
    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WeixinPayController.class);


    @Autowired
    private OrderService orderService;


    @Autowired
    private WeixinTemplate weixinTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OwnerService ownerService;


    @Autowired
    private WalletPayService walletPayService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private WeixinPayResultService weixinPayResultService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @RequestMapping({"/prepay"})
    @ResponseBody
    ResponseContainer prepay(int orderid, String session, HttpServletRequest request) {
        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                Order order = orderService.getOrderById(orderid);

                if (order != null && order.getPayStatus() == Order.ORDER_PAY_STATUS_NORMAL) {
                    WeixinPayMessage weixinPayMessage = new WeixinPayMessage();
                    weixinPayMessage.setAction(WeixinPayMessage.ACTION_PAY_NEXT);
                    boolean needpay = true;
                    double moneyleft2pay = order.getOrdermoney();

                    double money_before = customer.getBalance();
                    double money_after = 0;
                    double money_pay = 0;
                    String openid = customer.getWeixin();
                    if ( false) {
                        // todo use balnce
                        if (customer.getBalance() > order.getOrdermoney()) {
                            //no need pay next


                            orderService.UpdateOrder(order);
                            boolean dosettle = weixinPayResultService.OnOrderPaySuccess(order, "");

                            weixinPayMessage.setAction(WeixinPayMessage.ACTION_NO_NEED_PAY);

                            needpay = false;

                        } else {
                            moneyleft2pay = order.getOrdermoney() - customer.getBalance();




                            orderService.UpdateOrder(order);

                        }


                    } else {
                        moneyleft2pay = order.getOrdermoney();




                        //orderService.UpdateOrder(order);
                    }

                    if (needpay) {

                        int moneyleft2pay_i = new Double(moneyleft2pay*100).intValue();

                        //生成的随机字符串
                        String nonce_str = WxPayUtils.getRandomStringByLength(32);
                        //商品名称
                        String body = order.getOrderGoods().getGoodtitle();
                        //获取客户端的ip地址
                        String spbill_create_ip = "10.10.10.100";

                        String orderStr = Order.ORDER_WEIXIN_PREX + String.valueOf(orderid);

                        //组装参数，用户生成统一下单接口的签名
                        Map<String, String> packageParams = new HashMap<String, String>();
                        packageParams.put("appid", WxPayConfig.appid);
                        packageParams.put("mch_id", WxPayConfig.mch_id);
                        packageParams.put("nonce_str", nonce_str);
                        packageParams.put("body", body);
                        packageParams.put("out_trade_no", orderStr);//商户订单号
                        packageParams.put("total_fee", String.valueOf(moneyleft2pay_i));
                        //packageParams.put("total_fee", String.valueOf(10));//测试使用0.01
                        packageParams.put("spbill_create_ip", spbill_create_ip);
                        packageParams.put("notify_url", WxPayConfig.pay_notify_url);//支付成功后的回调地址
                        packageParams.put("trade_type", WxPayConfig.TRADETYPE);//支付方式
                        packageParams.put("openid", openid);

                        String prestr = WxPayUtils.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

                        //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
                        String mysign = WxPayUtils.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();

                        //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
                        String xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                                + "<body><![CDATA[" + body + "]]></body>"
                                + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                                + "<nonce_str>" + nonce_str + "</nonce_str>"
                                + "<notify_url>" + WxPayConfig.pay_notify_url + "</notify_url>"
                                + "<openid>" + openid + "</openid>"
                                + "<out_trade_no>" + orderStr + "</out_trade_no>"
                                + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                                 + "<total_fee>" + String.valueOf(moneyleft2pay_i) + "</total_fee>"
                                //+ "<total_fee>" + String.valueOf(10) + "</total_fee>"
                                + "<trade_type>" + WxPayConfig.TRADETYPE + "</trade_type>"
                                + "<sign>" + mysign + "</sign>"
                                + "</xml>";

                        logger.warn("weixinserver/prepay request data" + xml);

                        //调用统一下单接口，并接受返回的结果
                        String result = WxPayUtils.httpRequest(WxPayConfig.pay_url, "POST", xml);

                        logger.warn("weixinserver/prepay receive message" + result);

                        // 将解析结果存储在HashMap中
                        Map map = WxPayUtils.doXMLParse(result);

                        String return_code = (String) map.get("return_code");//返回状态码 comm
                        String result_code = (String) map.get("result_code");//返回状态码 comm

                        Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
                        if (return_code.equals("SUCCESS") && result_code.equals("SUCCESS")) {

                            String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                            Long timeStamp = System.currentTimeMillis() / 1000;

                            //拼接签名需要的参数
                            String stringSignTemp = "appId=" + WxPayConfig.appid + "&nonceStr=" + nonce_str + "&package=prepay_id="
                                    + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                            //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                            String paySign = WxPayUtils.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();
                            weixinPayMessage.setAppid(WxPayConfig.appid);
                            weixinPayMessage.setNonceStr(nonce_str);
                            weixinPayMessage.setPrepay_id(prepay_id);
                            weixinPayMessage.setTimeStamp(String.valueOf(timeStamp));
                            weixinPayMessage.setPaySign(paySign);
                            weixinPayMessage.setSignType(WxPayConfig.SIGNTYPE);


                            order.setPrepay_id(prepay_id);
                            orderService.UpdateOrder(order);


                        } else {
                            responseContainer.setReturncode(ResponeCodeConstant.WEIXIN_PAY_FAIL);
                            responseContainer.setErrormsg(result_code);
                        }


                    }


                    responseContainer.setMessage(weixinPayMessage);


                } else {
                    // order null or error status
                    responseContainer.setReturncode(ResponeCodeConstant.WEIXIN_PAY_REPEAT);
                    responseContainer.setErrormsg(ResponeCodeConstant.WEIXIN_PAY_REPEAT_STR);
                }

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            responseContainer.setErrormsg(e.getMessage().substring(0, len));
            logger.warn(e.getMessage().substring(0, len));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }


        return responseContainer;
    }

    @RequestMapping({"/payok"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer payok(int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Order order = orderService.getOrderById(orderid);
            if (order != null) {
                if (order.getPayStatus() == Order.ORDER_PAY_STATUS_NORMAL) {
                    order.setPayStatus(Order.ORDER_PAY_STATUS_OK);
                    orderService.UpdateOrder(order);
                }


            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            } else {
                responseContainer.setErrormsg(e.toString());
                logger.warn(e.toString());
            }
        }


        return responseContainer;
    }

    @RequestMapping({"/paynotify"})
    @ResponseBody
    String paynotify(HttpServletRequest request) {
        String resXml = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //sb为微信返回的xml
            String notityXml = sb.toString();

            logger.warn("weixinserver/paynotify receive message" + notityXml);



            Map map = WxPayUtils.doXMLParse(notityXml);
            if(map!=null)
            {
            String returnCode = (String) map.get("return_code");

            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            if ("SUCCESS".equals(returnCode)) {
                //验证签名是否正确

                if (WxPayUtils.verify(WxPayUtils.createLinkString(map), (String) map.get("sign"), WxPayConfig.key, "utf-8")) {
                    /**此处添加自己的业务逻辑代码start**/

                    String out_trade_no = (String) map.get("out_trade_no");
                    String transaction_id = (String) map.get("transaction_id");
                    String total_fee = (String) map.get("total_fee");



                    if (out_trade_no.contains(Order.ORDER_WEIXIN_PREX)) {
                        //charge_order
                        String order_str = out_trade_no.substring(Order.ORDER_WEIXIN_PREX.length(), out_trade_no.length());

                        int order_id = Integer.parseInt(order_str);

                        Order order = orderService.getOrderById(order_id);
                        if (order != null) {
                            boolean dosettle = weixinPayResultService.OnOrderPaySuccess(order, transaction_id);
                            if (dosettle) {

                            } else {
                                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                                        + "<return_msg><![CDATA[NO_ORDER_FOUND]]></return_msg>" + "</xml> ";
                            }
                        } else {
                            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                                    + "<return_msg><![CDATA[NO_ORDER_FOUND]]></return_msg>" + "</xml> ";
                        }


                    }

                } else {
                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[VERIFY_FAIL]]></return_msg>" + "</xml> ";
                }
            } else {

                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[RETRUNCODE_ERR]]></return_msg>" + "</xml> ";
            }

            }
            logger.warn("weixinserver/paynotify response" + resXml);
        } catch (Exception e) {

            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

            logger.warn(e.getMessage().substring(0, len));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return resXml;
    }


    @RequestMapping({"/refundnotify"})
    @ResponseBody
    String refundnotify(HttpServletRequest request) {
        String resXml = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //sb为微信返回的xml
            String notityXml = sb.toString();

            logger.warn("weixinserver/refundnotify receive message" + notityXml);

            Map map = WxPayUtils.doXMLParse(notityXml);

            String returnCode = (String) map.get("return_code");

            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            if ("SUCCESS".equals(returnCode)) {


                String req_info = (String) map.get("req_info");

                if (req_info != null) {

                    String decryptData = WxPayUtils.decryptData(req_info);

                    Map decryptmap = WxPayUtils.doXMLParse(decryptData);
                    String refund_status = (String) decryptmap.get("refund_status");
                    if (refund_status.equals("SUCCESS")) {
                        /**此处添加自己的业务逻辑代码start**/

                        String out_trade_no = (String) decryptmap.get("out_trade_no");
                        String out_refund_no = (String) decryptmap.get("out_refund_no");
                        String transaction_id = (String) decryptmap.get("transaction_id");
                        String refund_id = (String) decryptmap.get("refund_id");
                        String total_fee = (String) decryptmap.get("total_fee");
                        String refund_fee = (String) decryptmap.get("refund_fee");
                        if (out_trade_no.contains("zdw_recharge_") && out_refund_no.contains("zdw_refund_")) {
                            //other order type
                            String order_str = out_trade_no.substring(13, out_trade_no.length());

                            int order_id = Integer.parseInt(order_str);


                            String refund_str = out_refund_no.substring(11, out_refund_no.length());

                            int refundid = Integer.parseInt(refund_str);


                            Refund refund = refundService.getById(refundid);


                            if (!true) {
                                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                                        + "<return_msg><![CDATA[SETTLE_FAIL]]></return_msg>" + "</xml> ";
                            }
                        }
                    } else {
                        resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                                + "<return_msg><![CDATA[VERIFY_FAIL]]></return_msg>" + "</xml> ";
                    }

                }


                //验证签名是否正确


            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[RETRUNCODE_ERR]]></return_msg>" + "</xml> ";
            }


            logger.warn("weixinserver/refundnotify response" + resXml);
        } catch (Exception e) {
            logger.warn("weixinserver/refundnotify exception" + e.toString());
        }

        return resXml;
    }


}
