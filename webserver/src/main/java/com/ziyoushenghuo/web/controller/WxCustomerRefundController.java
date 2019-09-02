package com.ziyoushenghuo.web.controller;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.*;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ziyoushenghuo.common.Md5Util;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/customer"})
public class WxCustomerRefundController {

    private final int SESSION_TIME = 30 * 365;


    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerRefundController.class);


    @Autowired
    private OrderService orderService;


    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private ShopSendBackAddrService shopSendBackAddrService;

    @Autowired
    private ShopExpressFeeService shopExpressFeeService;


    @Autowired
    private DelayRefundProcessAutoReceiveSender delayRefundReceiveSender;

    @Autowired
    private RefundService refundService;

    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    private DelayRefundProcessApplyWaitSender delayRefundProcessApplyWaitSender;




    @RequestMapping({"/getuserrefundprocesses"})
    @ResponseBody
    ResponseContainer getuserrefundprocesses(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<RefundProcess> refundProcessList = refundProcessService.getUserRefundprocesses(userid, offset, size);

                responseContainer.setLists(refundProcessList);
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


    @RequestMapping({"/getrefundprocess"})
    @ResponseBody
    ResponseContainer getrefundprocess(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                responseContainer.setMessage(refundProcess);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
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


    @RequestMapping({"/setrefundexpressinfo"})
    @ResponseBody
    ResponseContainer setrefundexpressinfo(String session, int orderid, String companyname, String code) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                refundProcess.setSendbackcompany(companyname);
                refundProcess.setSendbackcode(code);
                refundProcess.setCreatetime(new Date());

                if (refundProcess.getStatus() != RefundProcess.REFUND_PROCESS_SELLER_RECEIVE) {
                    refundProcess.setSendbackstatus(RefundProcess.REFUND_SENDBACK_TORECEIVE);

                    delayRefundReceiveSender.send("" + refundProcess.getOrderid());
                }

                refundProcessService.createOrUpdate(refundProcess);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
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


    @RequestMapping({"/refundapply"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    ResponseContainer refundapply(String session, int orderid, int refundmode, int reason, double money, String refundinfo, String pics, String contact) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {


            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (order.getRefundProcessStatus() == 0 && money <= (order.getOrdertype() == 1 ? order.getOrdermoney() - (order.getPreshippfee() - order.getShipping_money()) : order.getOrdermoney())) {
                    RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                    if (refundProcess == null) {
                        refundProcess = new RefundProcess();
                        refundProcess.setApplymsg(refundinfo);
                        refundProcess.setOrderid(orderid);
                        refundProcess.setApplypics(pics);
                        refundProcess.setApplyreason(reason);
                        refundProcess.setApplymode(refundmode);
                        refundProcess.setRefundmoney(money);
                        refundProcess.setCreatetime(new Date());
                        refundProcess.setShopid(order.getOrderShop().getShopId());
                        refundProcess.setCustomerphone(contact);

                        refundProcess.setShopphone(order.getOrderShop().getShopcontact());

                        refundProcess.setOrdermoney(order.getOrdermoney());


                        if (order.getOrdertype() == 1) {
                            refundProcess.setReceiveid(order.getGiverid());
                        } else {
                            refundProcess.setReceiveid(order.getBuyerid());
                        }


                        refundProcess.setBuyid(order.getBuyerid());
                        refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_NOREPLY);

                        refundProcessService.createOrUpdate(refundProcess);


                        order.setRefundProcessStatus(RefundProcess.REFUND_PROCESS_SELLER_NOREPLY);
                        order.setOrderStatus(Order.ORDER_STATUS_REFUND);
                        orderService.UpdateOrder(order);

                        delayRefundProcessApplyWaitSender.send(""+orderid);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.PARAM_ERROR);
                        responseContainer.setErrormsg(ResponeCodeConstant.PARAM_ERROR_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.PARAM_ERROR);
                    responseContainer.setErrormsg(ResponeCodeConstant.PARAM_ERROR_STR);
                }
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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


    @RequestMapping({"/editrefundapply"})
    @ResponseBody
    ResponseContainer editrefundapply(String session, int orderid, int refundmode, int reason, double money, String refundinfo, String pics, String contact) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Order order = orderService.getOrderById(orderid);

            if (order == null) {
                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                return responseContainer;
            }

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
            if (refundProcess != null) {

                refundProcess.setApplymsg(refundinfo);

                refundProcess.setApplypics(pics);
                refundProcess.setApplyreason(reason);
                refundProcess.setApplymode(refundmode);
                refundProcess.setRefundmoney(money);
                refundProcess.setCustomerphone(contact);
                refundProcess.setCreatetime(new Date());

                if (refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_REFUSE) {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_NOREPLY);
                }


                refundProcess.setCustomerphone(contact);

                refundProcessService.createOrUpdate(refundProcess);

                order.setRefundProcessStatus(RefundProcess.REFUND_PROCESS_SELLER_NOREPLY);

                orderService.UpdateOrder(order);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/deleterefundapply"})
    @ResponseBody
    ResponseContainer deleterefundapply(String session, int orderid) {


        //暂时不用
        ResponseContainer responseContainer = new ResponseContainer();

        try {


            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                if (refundProcess != null && refundProcess.getReceiveid() == userid) {
                    refundProcessService.delete(refundProcess);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL_STR);
                }
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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


    @RequestMapping({"/applyplatformin"})
    @ResponseBody
    ResponseContainer applyplatformin(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {


            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                if (refundProcess != null && refundProcess.getReceiveid() == userid) {
                    if (refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_REFUSE) {
                        refundProcess.setStatus(RefundProcess.REFUND_PROCESS_PLATFORM_IN);
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL_STR);
                }
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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

    @RequestMapping({"/getshopsendbackaddr"})
    @ResponseBody
    ResponseContainer getshopsendbackaddr(String session, int shopid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {

            ShopSendBackAddr shopSendBackAddr = shopSendBackAddrService.GetByShopid(shopid);
            if (shopSendBackAddr != null) {
                responseContainer.setMessage(shopSendBackAddr);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL);
                responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_TOKEN_FAIL_STR);
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


}
