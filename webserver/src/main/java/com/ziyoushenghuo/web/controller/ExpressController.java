package com.ziyoushenghuo.web.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.weixinpay.WxPayConfig;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/express"})
public class ExpressController {

    //暂时不做

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WeixinPayController.class);


    @Autowired
    private OrderService orderService;


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


    @RequestMapping({"/kdniaonotify"})
    @ResponseBody
    String kdniaonotify(HttpServletRequest request) {
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

            logger.warn("weixinserver/kdniaonotify receive message" + notityXml);


            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";


            logger.warn("weixinserver/paynotify response" + resXml);
        } catch (Exception e) {
            logger.warn("weixinserver/paynotify exception" + e.toString());
        }

        return resXml;
    }


}
