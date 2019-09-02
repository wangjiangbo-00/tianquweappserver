package com.ziyoushenghuo.web.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.HttpUtils;
import com.ziyoushenghuo.common.ImageHandleHelper;
import com.ziyoushenghuo.common.QiniuHelper;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.express.KdniaoSubscribeAPI;
import com.ziyoushenghuo.express.KdniaoTrackQueryAPI;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.*;

import com.ziyoushenghuo.settlement.CustomerSettle;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import com.ziyoushenghuo.weixintoken.WxTokenUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan"})
public class TestController {


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private CustomerSettle customerSettle;


    @Autowired
    private DelayUnGroupSender delaySender;

    @Autowired
    private RefundService refundService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    private CustomerWithdrewService customerWithdrewService;

    private static Logger logger = LoggerFactory.getLogger(HomeController.class);


    @RequestMapping({"/testrefund"})
    @ResponseBody
    Object testrefund(String refundid) {


        try {
            logger.warn("deal with refund order  with refundid id = " + refundid);
            int rid = Integer.valueOf(refundid);

            Refund refund = refundService.getById(rid);


            if (refund != null) {
                if (refund.getStatus() == Refund.REFUND_STATUS_REFUND_FAIL) {
                    // do refund
                    long orderid = refund.getOrderid();

                    Order order = orderService.getOrderById(orderid);
                    if (order != null) {
                        WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                        if (refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK) {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                            refund.setRefundid(refundResult.getRefundid());
                            refund.setRefundtime(new Date());
                            refundService.createOrUpdate(refund);
                        } else {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                            refund.setFailstr(refundResult.getErrdes());
                            refundService.createOrUpdate(refund);

                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(), refund.getType(),Refund.REFUND_STATUS_REFUND_FAIL));
                            order.setFinishreason(Order.ORDER_FINISH_REASON_GIFT_OVER);

                            order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                            // failed again todo add expection
                        }
                        if (refund.getType() == Refund.REFUND_TYPE_GROUPFAIL) {
                            //order.setGroupfailRefundStatus(refund.getStatus());

                        } else if (refund.getType() == Refund.REFUND_TYPE_PRE_SHIP_FREE) {


                        } else if (refund.getType() == Refund.REFUND_TYPE_USER_REFUND) {

                            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                            if (refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK) {
                                refundProcess.setStatus(RefundProcess.REFUND_PROCESS_REFUND_DONE);
                                refundProcess.setRefundtime(new Date());
                                refundProcessService.createOrUpdate(refundProcess);
                            }


                        } else if (refund.getType() == Refund.REFUND_TYPE_GIFT_OVER_TIME) {
                            //order.setGiftRefundStatus(refund.getStatus());

                        }


                        orderService.UpdateOrder(order);
                    }

                }


            } else {
                logger.warn("deal with refund order  with refundid id = " + refundid + ", and no refund found");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return "";
    }



    @RequestMapping({"/testwithdrew"})
    @ResponseBody
    Object testwithdrew(String id) {


        try {
            logger.warn("deal with testwithdrew order  with  id = " + id);
            int rid = Integer.valueOf(id);

            CustomerWithdrew customerWithdrew = customerWithdrewService.getById(rid);


            if (customerWithdrew != null) {

                    // do refund
                    WxPayUtils.RefundResult refundResult = WxPayUtils.customerwithdrew(customerWithdrew);






            } else {
                logger.warn("deal with refund order  with refundid id = " + rid + ", and no refund found");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return "";
    }


    @RequestMapping({"/testdelqn"})
    @ResponseBody
    Object testdelqn(int id) {

        String url = "http://p4wgvxk6d.bkt.clouddn.com/FrEkeKxy-w5-151g9SVXTi07SRx3";

        String key = QiniuHelper.GetKey(url);


        QiniuHelper.delete(key);


        return String.valueOf("");
    }

    @RequestMapping({"/test"})
    @ResponseBody
    Object test() {

        try {
            //ImageHandleHelper.resize(new File("D:\\images\\1.png"),new File("D:\\images\\6.png"),150, 1f);
            //ImageHandleHelper.resize(new File("D:\\images\\1.png"),new File("D:\\images\\7.png"),150, 0.7f);
            //ImageHandleHelper.resize(new File("D:\\images\\132.jpg"), new File("D:\\images\\8.png"), 35, 1f, true);
            //ImageHandleHelper.overlapImage("D:\\images\\base.png", "D:\\images\\8.png", "D:\\images\\test.png", 30, 720);

            //ImageHandleHelper.drawStringForImage("D:\\images\\test.png", "高品质特产平台,好吃，送礼就来-田趣小集", new Color(156, 156, 156), (float) 0.5,
                    //"D:\\images\\test.png", 70, 738);
        } catch (Exception e) {

        }


        //
        /*
        Properties props=System.getProperties();
        String osName = props.getProperty("os.name");




        /*
        KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
        try {
            String result = api.getOrderTracesByJson("YTO", "809789257631");
            System.out.print(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
ImageHandleHelper.drawStringForImage("D:\\proj\\customerapp\\images\\makephone.png","red", Color.red,(float) 0.5,"D:\\proj\\customerapp\\images\\test1.png");
        WxTokenUtils.gettoken();

        try {
            //AliSmsUtils.sendNotify("zxtc","退款余额不足");

        }catch (Exception e)
        {
            logger.warn(e.getMessage());
        }

        */


        /*

         */


        return String.valueOf("i am ok");
    }


    @RequestMapping({"/testqiniu"})
    @ResponseBody
    Object testqiniu() {

        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
        String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
        String bucket = "ziyoutechan";
        String key = "tmp/wxdd41e6a63c02c5f0.o6zAJs7ovNRM1nCfIURMX8URcbI8.1OvQGXTyYWdt2c935ad759beb651378127edfbd47d4b.png";
//过期天数，该文件10天后删除
        int days = 10;
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {

            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return String.valueOf("");
    }


    @RequestMapping({"/testqiniuupload"})
    @ResponseBody
    Object testqiniuupload() {
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "goJrpGbTHF2ZRHc3yVJ8kIPqqSHx7M_UzMiIphtR";
        String secretKey = "22HLpgHF1gKCBrhHmpelsRuMSbd6BYnwxEgq1yOS";
        String bucket = "ziyoutechan";

//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "D:\\images\\5.png";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }


        return new ResponseEntity(true, HttpStatus.OK);
    }


    @RequestMapping({"/testpromition"})
    @ResponseBody
    Object testpromition(int type, int action, int id) {

        if (type == 1) {
            if (action == 1) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GIFT_START, "" + id);
            } else if (action == 2) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GIFT_STOP, "" + id);
            } else if (action == 3) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GIFT_SHOW, "" + id);
            }
        } else if (type == 2) {
            if (action == 1) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GROUP_START, "" + id);
            } else if (action == 2) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GROUP_STOP, "" + id);
            } else if (action == 3) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GROUP_SHOW, "" + id);
            }
        } else if (type == 3) {
            if (action == 1) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_DISCOUNT_START, "" + id);
            } else if (action == 2) {
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_DISCOUNT_STOP, "" + id);
            }

        }


        return String.valueOf("");
    }


    @RequestMapping({"/testorder"})
    @ResponseBody
    Object testorder(int orderid, int action) {


        switch (action) {
            case 1:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_UNPAY_NAME, "" + orderid);
                break;
            case 2:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_UNGROUP_NAME, "" + orderid);
                break;
            case 3:
                //rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_DOREFUND_NAME, "" + orderid);
                break;
            case 4:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_AUTOCLOSE_NAME, "" + orderid);
                break;
            case 5:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_AFTERDELIVERY_NAME, "" + orderid);
                break;
            case 6:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_REFUND_FAIL_NAME, "" + orderid);
                break;
            case 7:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_AFTERRECEIVE_NAME, "" + orderid);
                break;
            case 8:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_DO_REFUND_GIIF_SHIPFEE_NAME, "" + orderid);
                break;
            case 9:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_SENDGIFT_STATUS_NAME, "" + orderid);
                break;
            case 10:
                //rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_CAN_REFUND_APPLY_NAME, "" + orderid);
                break;
            case 11:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_REFUND_OK_SETTILE_NAME, "" + orderid);
                break;
            case 12:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_REFUND_REPLY_WAIT_NAME, "" + orderid);
                break;

            case 13:
                rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME, "" + orderid);
                break;
        }


        return String.valueOf("");
    }


    @RequestMapping({"/testpreorder"})
    @ResponseBody
    Object testpreorder(int orderid) {





        try {


            Order order = orderService.getOrderById(orderid);


            customerSettle.onOrderPreSettle(order);


        } catch (Exception e) {
            logger.warn(e.getMessage());

        } finally {

        }


        return String.valueOf("");
    }

}
