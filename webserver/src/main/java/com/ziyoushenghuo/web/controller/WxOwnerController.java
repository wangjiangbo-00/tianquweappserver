package com.ziyoushenghuo.web.controller;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ziyoushenghuo.common.Md5Util;

/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/weixinserver/owner"})
public class WxOwnerController {

    private final int SESSION_WEIXINID_DAY = 3600;

    private final int SESSION_CHECK_MIN = 10;

    private final int DEVICEID_NOTFOUND = 1000;
    private final String DEVICEID_NOTFOUND_STR = "ID ERR OR NOT ADD IN SYSTEM";

    private final int SESSION_NOTFOUND = 1001;
    private final String SESSION_NOTFOUND_STR = "no login info found";


    private final int USER_CHECK_FAIL = 1006;
    private final String USER_CHECK_FAIL_STR = "用户名密码检查失败";


    private final int CHARGE_HAS_STARTED = 1002;
    private final String CHARGE_HAS_STARTED_STR = "你已经在该机启动充电";

    private final int DEVICE_BE_USED = 1003;
    private final String DEVICE_BE_USED_STR = "设备正在被他人使用";


    private final int NO_CHARGEPROCESS = 1004;
    private final String NO_CHARGEPROCESS_STR = "没有找到可停止的充电过程";

    private final int NOT_RIGHT_PROCESS = 1005;
    private final String NOT_RIGHT_PROCESS_STR = "不是处于可停止充电状态";

    private final int WEIXIN_PAY_FAIL = 5000;
    private final String WEIXIN_PAY_FAIL_STR = "";

    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxOwnerController.class);


    @Autowired
    private OrderService orderService;


    @Autowired
    private CustomerService customerService;


    @Autowired
    private OwnerService ownerService;


    @Autowired
    private WalletPayService walletPayService;


    @Autowired
    private RegionService regionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping({"/setphone"})
    @ResponseBody
    ResponseContainer setphone(String weixinid, String phone) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];
            Customer customer = customerService.GetByWeixin(openid);
            if (customer == null) {

            } else {
                responseContainer.setReturncode(0);
                responseContainer.setErrormsg("");
                customer.setPhone(phone);
                customerService.update(customer);
            }
        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }

        return responseContainer;
    }


    @RequestMapping({"/setuserinfo"})
    @ResponseBody
    ResponseContainer setuserinfo(String weixinid, String phone, String email, String sex, String addr, String birthday, String realname) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];
            Owner owner = ownerService.getOwnerByWeixin(openid);
            if (owner == null) {

            } else {
                responseContainer.setReturncode(0);
                responseContainer.setErrormsg("");

                ownerService.update(owner);
            }
        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }

        return responseContainer;
    }

    @RequestMapping({"/getbindphone"})
    @ResponseBody
    ResponseContainer getbindphone(String weixinid) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];
            Customer customer = customerService.GetByWeixin(openid);
            if (customer == null) {
                WeixinUserinfoMessage weixinUserinfoMessage = new WeixinUserinfoMessage();

                weixinUserinfoMessage.setPhone("");
                weixinUserinfoMessage.setBalance(0);
                responseContainer.setMessage(weixinUserinfoMessage);
            } else {
                responseContainer.setReturncode(0);
                responseContainer.setErrormsg("");

                WeixinUserinfoMessage weixinUserinfoMessage = new WeixinUserinfoMessage();

                weixinUserinfoMessage.setPhone(customer.getPhone());
                responseContainer.setMessage(weixinUserinfoMessage);

            }
        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }


        return responseContainer;
    }


    @RequestMapping({"/getuserbalance"})
    @ResponseBody
    ResponseContainer getuserbalance(String weixinid) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];
            Owner owner = ownerService.getOwnerByWeixin(openid);
            if (owner == null) {
                WeixinUserinfoMessage weixinUserinfoMessage = new WeixinUserinfoMessage();

                weixinUserinfoMessage.setPhone("");
                weixinUserinfoMessage.setBalance(0);
                responseContainer.setMessage(weixinUserinfoMessage);
            } else {
                responseContainer.setReturncode(0);
                responseContainer.setErrormsg("");

                WeixinUserinfoMessage weixinUserinfoMessage = new WeixinUserinfoMessage();


                responseContainer.setMessage(weixinUserinfoMessage);

            }
        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }


        return responseContainer;
    }


    @RequestMapping({"/checkuser"})
    @ResponseBody
    ResponseContainer checkuser(String username, String passwd) {

        ResponseContainer responseContainer = new ResponseContainer();

        Owner owner = ownerService.getOwnerByUser(username);


        if (owner != null && owner.getPassword().equals(passwd)) {
            Date date = new Date();

            String dateStr = new SimpleDateFormat(AppCommon.COMMON_DATE_FORMAT).format(date);

            String md5Str = Md5Util.getMD5(username + dateStr);

            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String idkey = String.valueOf(owner.getId());
            operations.set(md5Str, idkey, SESSION_CHECK_MIN, TimeUnit.MINUTES);

            WeixinAuthInfoMessage weixinAuthInfoMessage = new WeixinAuthInfoMessage();
            weixinAuthInfoMessage.setSession(md5Str);

            responseContainer.setMessage(weixinAuthInfoMessage);
        } else {
            responseContainer.setReturncode(USER_CHECK_FAIL);
            responseContainer.setErrormsg(USER_CHECK_FAIL_STR);
        }


        return responseContainer;
    }


    @RequestMapping({"/getprofits"})
    @ResponseBody
    ResponseContainer getprofits(String weixinid, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {


            responseContainer.setMessage(null);

        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }


    @RequestMapping({"/getwalletchanges"})
    @ResponseBody
    ResponseContainer getwalletchanges(String weixinid, int page, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];

            Owner owner = ownerService.getOwnerByWeixin(openid);


        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }

    @RequestMapping({"/getuserinfo"})
    @ResponseBody
    ResponseContainer getuserinfo(String weixinid) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String ownerid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];

            Owner owner = ownerService.getOwnerByWeixin(ownerid);

            if (owner != null) {
                responseContainer.setMessage(owner);
            } else {

            }


        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }


    @RequestMapping({"/addaddr"})
    @ResponseBody
    ResponseContainer addaddr(String weixinid, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];


        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }

    @RequestMapping({"/editaddr"})
    @ResponseBody
    ResponseContainer editaddr(String weixinid, CustomerShipAddr shipAddr) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];


            Owner owner = ownerService.getOwnerByWeixin(openid);


        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }


    @RequestMapping({"/deleteaddr"})
    @ResponseBody
    ResponseContainer editregion(String weixinid, int addrid) {

        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(weixinid);

        if (sessionvalue != null) {
            String openid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];


            Owner owner = ownerService.getOwnerByWeixin(openid);


        } else {
            responseContainer.setReturncode(SESSION_NOTFOUND);
            responseContainer.setErrormsg(SESSION_NOTFOUND_STR);
        }
        return responseContainer;
    }


    @RequestMapping({"/onlogin"})
    @ResponseBody
    ResponseContainer onlogin(String code, String binduser) {
        ResponseContainer responseContainer = new ResponseContainer();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(binduser);

        if (sessionvalue != null) {
            String ownerid = StringUtils.splitByWholeSeparator(sessionvalue, SESSION_SPLIT)[0];
            Owner owner = ownerService.getOwnerById(Integer.valueOf(ownerid));

            if (owner != null && owner.getWeixin() != null) {
                StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?appid=");
                sb.append(WEI_APPID);
                sb.append("&secret=");
                sb.append(WEI_APPKEY);
                sb.append("&js_code=");
                sb.append(code);
                sb.append("&grant_type=authorization_code");

                String url = sb.toString();
                try {
                    ///tcpserver/reportstartresult
                    String respone = HttpUtils.getInstance().sendHttpGet(url);

                    if (respone != null) {

                        JsonParser parser = new JsonParser();  //创建JSON解析器
                        JsonObject object = (JsonObject) parser.parse(respone);

                        if (object.has("openid")) {
                            String openid = object.get("openid").getAsString();


                            owner.setWeixin(openid);

                            ownerService.update(owner);
                            String sessionkey = object.get("session_key").getAsString();

                            Date date = new Date();

                            String dateStr = new SimpleDateFormat(AppCommon.COMMON_DATE_FORMAT).format(date);

                            String md5Str = Md5Util.getMD5(openid + sessionkey + dateStr);


                            boolean hasKey = redisTemplate.hasKey(md5Str);
                            if (hasKey) {
                                //hash crash not deal now
                            } else {
                                String idkey = openid + SESSION_SPLIT + sessionkey + SESSION_SPLIT + sessionkey;
                                operations.set(md5Str, idkey, SESSION_WEIXINID_DAY, TimeUnit.DAYS);
                            }

                            responseContainer.setReturncode(0);
                            responseContainer.setErrormsg("");
                            WeixinAuthInfoMessage weixinAuthInfoMessage = new WeixinAuthInfoMessage();

                            weixinAuthInfoMessage.setSession(md5Str);
                            responseContainer.setMessage(weixinAuthInfoMessage);

                        } else if (object.has("errcode")) {
                            int errcode = object.get("errcode").getAsInt();
                            String errmsg = object.get("errmsg").getAsString();

                            responseContainer.setReturncode(errcode);
                            responseContainer.setErrormsg(errmsg);
                        }


                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
        }

        return responseContainer;
    }


}
