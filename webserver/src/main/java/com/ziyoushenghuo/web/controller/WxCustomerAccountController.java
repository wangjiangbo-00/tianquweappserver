package com.ziyoushenghuo.web.controller;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.*;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.asyntask.CustomerWithdrewTask;
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
public class WxCustomerAccountController {

    private final int SESSION_TIME = 30 * 365;
    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerController.class);

    @Autowired
    private OwnerProfitService ownerProfitService;


    @Autowired
    private CustomerService customerService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CustomerAccountService customerAccountService;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private CustomerAccountRecordService customerAccountRecordService;

    @Autowired
    private CustomerWithdrewService customerWithdrewService;

    @Autowired
    private CustomerRecommendActivecodeService customerRecommendActivecodeService;


    @Autowired
    private CustomerWithdrewTask customerWithdrewTask;


    @RequestMapping({"/getaccount"})
    @ResponseBody
    ResponseContainer getaccount(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                // Owner owner = ownerService.getOwnerById(userid);
                if (!"".equals(String.valueOf(userid))) {
                    //int shopid = owner.getShopid();

                    CustomerAccount customerAccount = customerAccountService.GetByCid(userid);
                    if (customerAccount != null) {
                        responseContainer.setMessage(customerAccount);
                    }
                    else
                    {
                        customerAccount = new CustomerAccount();

                        customerAccount.setCid(userid);
                        customerAccount.setMoney(0);
                        customerAccount.setProceeds(0);
                        customerAccount.setProfit(0);
                        customerAccount.setMoney_lock(0);
                        customerAccount.setConsume(0);
                        customerAccount.setOrdercount(0);
                        customerAccount.setDayincome(0);
                        customerAccount.setWithdraw(0);

                        customerAccountService.UpdateOrCreate(customerAccount);

                        responseContainer.setMessage(customerAccount);
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping({"/withdrew"})
    @ResponseBody
    ResponseContainer withdrew(String session, double money,String formid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null && customer.getIsrecommender() == 1) {
                int userid = customer.getId();
                CustomerAccount customerAccount = customerAccountService.GetByCid(userid);

                if (customerAccount != null) {

                    if (customerAccount.getMoney() >= money) {
                        CustomerWithdrew withdrew = new CustomerWithdrew();
                        withdrew.setCreate_time(new Date());
                        withdrew.setMoney(money);
                        withdrew.setCid(userid);
                        withdrew.setStatus(0);
                        withdrew.setOpenid(customer.getWeixin());
                        withdrew.setOwnername(customer.getNickname());
                        withdrew.setFormid(formid);
                        customerWithdrewService.createOrUpdate(withdrew);

                        double balance = customerAccount.getMoney() - money;

                        customerAccount.setMoney_lock(money);
                        customerAccount.setMoney(balance);
                        customerAccountService.UpdateOrCreate(customerAccount);

                        CustomerAccountRecord customerAccountRecord = new CustomerAccountRecord();
                        customerAccountRecord.setBalance(balance);
                        customerAccountRecord.setCid(userid);
                        customerAccountRecord.setCreatetime(new Date());
                        customerAccountRecord.setMoney(money);
                        customerAccountRecord.setType(CustomerAccountRecord.OP_TYPE_WITHDREW);
                        customerAccountRecord.setRemark("提现（在途）");
                        customerAccountRecord.setStatus(0);
                        customerAccountRecord.setRefid(withdrew.getId());

                        customerAccountRecordService.createOrUpdate(customerAccountRecord);

                        customerWithdrewTask.withdrew(withdrew,customerAccount,customerAccountRecord);

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.WITHDREW_CHECKFAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.WITHDREW_CHECKFAIL_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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


    @Transactional(rollbackFor = Exception.class)
    @RequestMapping({"/applyrecommender"})
    @ResponseBody
    ResponseContainer applyrecommender(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int cid = customer.getId();

                CustomerAccount customerAccount = customerAccountService.GetByCid(cid);

                if (customerAccount != null) {

                    if (customerAccount.getConsume() >= CustomerAccount.RECOMMEDN_ALLPY_CONSUME) {
                        customer.setIsrecommender(1);
                        customerService.update(customer);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.WITHDREW_CHECKFAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.WITHDREW_CHECKFAIL_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
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


    @Transactional(rollbackFor = Exception.class)
    @RequestMapping({"/applyrecommenderwithactivecode"})
    @ResponseBody
    ResponseContainer applyrecommenderwithactivecode(String session, String code) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int cid = customer.getId();



                CustomerRecommendActivecode customerRecommendActivecode = customerRecommendActivecodeService.GetByCode(code);

                if (customerRecommendActivecode != null && customerRecommendActivecode.getStatus()!=CustomerRecommendActivecode.CODE_STATUS_USED) {

                    customerRecommendActivecode.setStatus(CustomerRecommendActivecode.CODE_STATUS_USED);
                    customerRecommendActivecodeService.save(customerRecommendActivecode);
                    customer.setIsrecommender(1);
                    customerService.update(customer);


                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.RECOMMEND_CODE_NOT_VALID);
                    responseContainer.setErrormsg(ResponeCodeConstant.RECOMMEND_CODE_NOT_VALID_STR);
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


    @RequestMapping({"/getwithdrews"})
    @ResponseBody
    ResponseContainer getwithdrews(String session, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<CustomerWithdrew> withdrews = customerWithdrewService.getRecentsByCid(userid, page, size);
                responseContainer.setLists(withdrews);
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

    @RequestMapping({"/generatecodes"})
    @ResponseBody
    ResponseContainer generatecodes(String session, int num) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                customerRecommendActivecodeService.addCodes(num);
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

    @RequestMapping({"/getrecommenderwithactivecodes"})
    @ResponseBody
    ResponseContainer getrecommenderwithactivecodes(String session, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<CustomerRecommendActivecode> customerRecommendActivecodeList = customerRecommendActivecodeService.getUsedCodes(page, size);

                responseContainer.setLists(customerRecommendActivecodeList);

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




    @RequestMapping({"/getaccountrecords"})
    @ResponseBody
    ResponseContainer getaccountrecords(String session, int status, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<CustomerAccountRecord> shopAccountRecords = null;
                if (status == 0) {


                    shopAccountRecords = customerAccountRecordService.getRecentlyAllRecords(userid, offset, size);
                } else if(status == 1) {
                    List<Integer> integers = new ArrayList<>();

                    integers.add(CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);
                    integers.add(CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);
                    shopAccountRecords = customerAccountRecordService.getRecentlyRecords(userid, integers, offset, size);
                }
                else if(status == 2) {
                    List<Integer> integers = new ArrayList<>();

                    integers.add(CustomerAccountRecord.OP_TYPE_WITHDREW);
                    shopAccountRecords = customerAccountRecordService.getRecentlyRecords(userid, integers, offset, size);
                }


                responseContainer.setLists(shopAccountRecords);


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


}
