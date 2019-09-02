package com.ziyoushenghuo.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.asyntask.ShopWithdrewTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.Md5Util;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundSettleSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.OwnerRepository;
import com.ziyoushenghuo.response.GoodsDetailsMessage;
import com.ziyoushenghuo.response.ResponeCodeConstant;
import com.ziyoushenghuo.response.ResponseContainer;
import com.ziyoushenghuo.response.WeixinAuthInfoMessage;
import com.ziyoushenghuo.service.*;

import com.ziyoushenghuo.weixinpay.WxPayConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/owner"})
public class OwnerAccountController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;



    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private GoodsImageService goodsImageService;


    @Autowired
    private ShopWithdrewTask shopWithdrewTask;

    @Autowired
    private OrderExpressService orderExpressService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private ShopAccountService shopAccountService;

    @Autowired
    private WithdrewService withdrewService;

    @Autowired
    private ShopAccountRecordService shopAccountRecordService;

    @Autowired
    private OwnerProfitService ownerProfitService;

    @Autowired
    private DelayAfterDeliverySender delayAfterDeliverySender;



    @Autowired
    DelayRefundSettleSender delayRefundSettleSender;


    @Autowired
    OwnerRepository ownerRepository;


    private static Logger logger = LoggerFactory.getLogger(OwnerAccountController.class);




    @RequestMapping({"/getaccount"})
    @ResponseBody
    ResponseContainer getaccount(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

                Owner owner = ownerRepository.findByToken(session);

                if (owner != null) {
                    int shopid = owner.getShopid();

                    ShopAccount shopAccount = shopAccountService.GetByShopid(shopid);
                    if (shopAccount != null) {
                        responseContainer.setMessage(shopAccount);
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                }

        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }



    @RequestMapping({"/getaccountrecords"})
    @ResponseBody
    ResponseContainer getaccountrecords(String session, int status, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();
                List<ShopAccountRecord> shopAccountRecords = null;
                if (status == 0) {


                    shopAccountRecords = shopAccountRecordService.getOwnerRecentlyRecords(shopid, offset, size);
                } else if(status == 1) {
                    List<Integer> integers = new ArrayList<>();

                    integers.add(CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);
                    integers.add(CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);
                    shopAccountRecords = shopAccountRecordService.getRecentlyRecords(shopid, integers, offset, size);
                }
                else if(status == 2) {
                    List<Integer> integers = new ArrayList<>();

                    integers.add(CustomerAccountRecord.OP_TYPE_WITHDREW);
                    shopAccountRecords = shopAccountRecordService.getRecentlyRecords(shopid, integers, offset, size);
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


    @RequestMapping({"/withdrew"})
    @ResponseBody
    ResponseContainer withdrew(String session, double money,String formid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

                Owner owner = ownerRepository.findByToken(session);

                if (owner != null) {
                    int shopid = owner.getShopid();

                    Shop shop = shopService.GetById(shopid);


                    ShopAccount shopAccount = shopAccountService.GetByShopid(shopid);


                    Withdrew withdrew = new Withdrew();
                    withdrew.setCreate_time(new Date());
                    withdrew.setMoney(money);
                    withdrew.setShopid(shopid);
                    withdrew.setStatus(0);
                    withdrew.setShopname(shop.getShopname());
                    withdrew.setOwnername(shop.getShop_company_name());
                    withdrew.setOwnerphone(owner.getPhone());
                    withdrew.setOpenid(owner.getWeixin());
                    withdrew.setFormid(formid);
                    withdrewService.createOrUpdate(withdrew);

                    shopAccount.setShop_total_money_lock(shopAccount.getShop_total_money_lock() + money);
                    shopAccountService.UpdateOrCreate(shopAccount);



                    double balance = shopAccount.getShop_total_money() - money;

                    shopAccount.setShop_total_money(balance);
                    shopAccount.setShop_total_money_lock(money);
                    shopAccountService.UpdateOrCreate(shopAccount);

                    ShopAccountRecord shopAccountRecord = new ShopAccountRecord();
                    shopAccountRecord.setBalance(balance);
                    shopAccountRecord.setRefid(withdrew.getId());
                    shopAccountRecord.setCreatetime(new Date());
                    shopAccountRecord.setMoney(money);
                    shopAccountRecord.setType(CustomerAccountRecord.OP_TYPE_WITHDREW);
                    shopAccountRecord.setRemark("提现（在途）");
                    shopAccountRecord.setStatus(0);
                    shopAccountRecord.setShopid(shopid);

                    shopAccountRecord.setRefid(withdrew.getId());

                    shopAccountRecordService.createOrUpdate(shopAccountRecord);

                    shopWithdrewTask.withdrew(withdrew,shopAccount,shopAccountRecord);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                }

        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/getwithdrews"})
    @ResponseBody
    ResponseContainer getwithdrews(String session, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                List<Withdrew> withdrews = withdrewService.getRecentsByShopid(shopid, page, size);

                responseContainer.setLists(withdrews);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }


        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }
        return responseContainer;
    }

    @RequestMapping({"/getprofits"})
    @ResponseBody
    ResponseContainer getprofits(String session, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                List<OwnerProfit> ownerProfits = ownerProfitService.getOwnerRecentlyProfits(shopid, page, size);

                responseContainer.setLists(ownerProfits);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }


        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }
        return responseContainer;
    }


    @RequestMapping({"/getwalletchanges"})
    @ResponseBody
    ResponseContainer getwalletchanges(String session, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                List<ShopAccountRecord> shopAccountRecords = shopAccountRecordService.getOwnerRecentlyRecords(shopid, page, size);

                responseContainer.setLists(shopAccountRecords);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }


        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }
        return responseContainer;
    }






    @RequestMapping({"/checkaccount"})
    @ResponseBody
    ResponseContainer checkaccount(String username, String passwd, String safecode) {

        ResponseContainer responseContainer = new ResponseContainer();

        Owner owner = ownerService.getOwnerByUser(username);

        if (owner != null) {
            String pwmd5 = StringUtils.lowerCase(Md5Util.getMD5(passwd));

            if (owner.getPassword().equals(pwmd5) && owner.getSecuritypwd().equals(safecode)) {


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.USER_NOT_CORRECT);
                responseContainer.setErrormsg(ResponeCodeConstant.USER_NOT_CORRECT_STR);
            }
        } else {
            responseContainer.setReturncode(ResponeCodeConstant.USER_NOT_CORRECT);
            responseContainer.setErrormsg(ResponeCodeConstant.USER_NOT_CORRECT_STR);
        }


        return responseContainer;
    }

    @RequestMapping({"/unbindaccount"})
    @ResponseBody
    ResponseContainer unbindaccount(String session) {

        ResponseContainer responseContainer = new ResponseContainer();


            Owner owner = ownerRepository.findByToken(session);


            if (owner != null) {

                owner.setWeixin("");
                owner.setToken("");
                ownerService.update(owner);
            }

         else {
            responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
            responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
        }

        return responseContainer;
    }


}