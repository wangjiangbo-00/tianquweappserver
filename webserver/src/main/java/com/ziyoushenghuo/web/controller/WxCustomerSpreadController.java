package com.ziyoushenghuo.web.controller;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.*;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.weixinoss.WxOssUtils;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;




/**
 * Created by RXJ on 2017/4/7.
 */
@Controller
@RequestMapping({"/ziyoutechan/customer"})
public class WxCustomerSpreadController {

    private final int SESSION_TIME = 30 * 365;


    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerSpreadController.class);


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAddrRepository orderAddrRepository;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GiftActivityRepository giftActivityRepository;


    @Autowired
    private UserPropagateSettingRepository userPropagateSettingRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CustomerRecommendImgRepository customerRecommendImgRepository;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    OrderShopRepository orderShopRepository;

    @Autowired
    OrderExtraRepository orderExtraRepository;


    @Autowired
    WeixinTemplate weixinTemplate;

    @RequestMapping({"/createrecommendimg"})
    @ResponseBody
    @Deprecated
    Object creacreaterecommendimgteqr(String session, int templeteid) {

        //templeteid 默认为0，此时采用系统默认的图片，模板暂时都放置到文件中

        //
        ResponseContainer responseContainer = new ResponseContainer();


        Customer customer = customerService.GetByToken(session);


        if (customer != null) {

            int userid = customer.getId();

            if (customer.getIsrecommender() == 0) {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);

                return responseContainer;
            }

            if (customer.getHeadpic() == null || customer.getHeadpic().isEmpty()) {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);

                return responseContainer;
            }


            CustomerRecommendImg customerRecommendImg = customerRecommendImgRepository.findByCidAndTid(userid, templeteid);

            if (customerRecommendImg != null) {
                //need delete and delete qiniu

                QiniuHelper.delete(QiniuHelper.GetKey(customerRecommendImg.getPic()));

                customerRecommendImgRepository.delete(customerRecommendImg);
            }

            InputStream inputStream = null;
            BufferedImage image_userqr = null;

            try {
                if (customer.getQrpic() == null || customer.getQrpic().isEmpty()) {
                    //need get qrpic first


                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                    inputStream =null;//= WeixinHelper.getuseractivityqr(customer,type,refid, access_token);
                    //inputStream = WeixinHelper.getuserqr(userid, access_token);
                    if (inputStream == null) {
                        // deal error
                        responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                        responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);

                        return responseContainer;

                    } else {
                        image_userqr = ImageHandleHelper.resize(inputStream, 150, false);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ImageIO.write(image_userqr, "png", out);
                        byte[] imageraws = out.toByteArray();

                        String hash = QiniuHelper.upload(imageraws);

                        if (!hash.isEmpty()) {
                            String fullqr = QiniuHelper.QINIU_PREX + hash;

                            customer.setQrpic(fullqr);

                            customerService.update(customer);
                        }
                    }


                } else {
                    URL userqrpic = new URL(customer.getQrpic());
                    image_userqr = ImageIO.read(userqrpic);

                }

                if (image_userqr == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);

                    return responseContainer;
                }

                Properties props = System.getProperties();
                String osName = props.getProperty("os.name");
                FileInputStream is = null;

                String basepic = "";
                if (osName.contains("Windows")) {
                    basepic = "D:\\images\\base.png";
                } else {

                    basepic = "/home/zhenxintc/wxappserver/projimages/recommendbase/base.png";

                }

                BufferedImage image_afterqr = ImageHandleHelper.overlapImage(basepic, image_userqr, 460, 600);


                URL userheadpic = new URL(customer.getHeadpic());
                BufferedImage image_uh = ImageIO.read(userheadpic);

                image_uh = ImageHandleHelper.resize(image_uh, 35, true);

                BufferedImage image_last = ImageHandleHelper.overlapImage(image_afterqr, image_uh, 30, 720);


                ByteArrayOutputStream out_last = new ByteArrayOutputStream();
                ImageIO.write(image_last, "png", out_last);
                byte[] imagelast = out_last.toByteArray();

                String hash_ri = QiniuHelper.upload(imagelast);
                //解析上传成功的结果
                if (!hash_ri.isEmpty()) {
                    String rdpic = QiniuHelper.QINIU_PREX + hash_ri;

                    CustomerRecommendImg customerRecommendImg1 = new CustomerRecommendImg();
                    customerRecommendImg1.setCid(userid);
                    customerRecommendImg1.setPic(rdpic);
                    customerRecommendImg1.setTid(templeteid);

                    customerRecommendImgRepository.save(customerRecommendImg1);

                    responseContainer.setMessage(customerRecommendImg1);

                }

            } catch (Exception e) {
                responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {

                    }
                }

            }


        }


        return responseContainer;
    }


    @RequestMapping({"/getrecommendpic"})
    @ResponseBody
    @Deprecated
    ResponseContainer getrecommendpic(String session, int templeteid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                CustomerRecommendImg customerRecommendImg = customerRecommendImgRepository.findByCidAndTid(userid, templeteid);
                responseContainer.setMessage(customerRecommendImg);
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


    @RequestMapping({"/getuseractivityqr"})
    @ResponseBody
    ResponseContainer getuseractivityqr(String session, int type,int refid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                CustomerRecommendImg customerRecommendImg = customerRecommendImgRepository.findByCidAndTidAndRefid(userid, type,refid);

                if(customerRecommendImg!=null)
                {
                    responseContainer.setMessage(customerRecommendImg);
                }
                else
                {
                    InputStream inputStream = null;
                    BufferedImage image_userqr = null;
                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);

                    HttpResponse response = WeixinHelper.getuseractivityqr(customer,type,refid, access_token);

                    long datalen = response.getEntity().getContentLength();
                    if (datalen < 256) {
                        // deal error
                        JsonParser parser = new JsonParser();  //创建JSON解析器

                        String responsestr = EntityUtils.toString(response.getEntity());
                        JsonObject object = (JsonObject) parser.parse(responsestr);


                        if (object != null) {
                            String errcode = object.get("errcode").getAsString();
                            String errmsg = object.get("errmsg").getAsString();

                            logger.warn("generate qr code return err with errcode = " + errcode + " msg = " + errmsg);


                        }

                        responseContainer.setReturncode(ResponeCodeConstant.USER_SHARE_MSG_GENERATE_ERR);
                        responseContainer.setErrormsg(ResponeCodeConstant.USER_SHARE_MSG_GENERATE_ERR_STR);

                        return responseContainer;

                    } else {
                        inputStream = response.getEntity().getContent();

                        String key = "tqxj_img_user/" + "u"+ customer.getId() +"_t" + type + "_r" + refid + "_" + new Date().getTime() ;

                        WxOssUtils.WxOssResult wxOssResult = WxOssUtils.uploadStream(inputStream,key,(int)datalen);

                        if (wxOssResult.isBret()) {
                            String fullqr = WxOssUtils.WXOSS_PREX + key;

                            customerRecommendImg = new CustomerRecommendImg();
                            customerRecommendImg.setCid(userid);
                            customerRecommendImg.setPic(fullqr);
                            customerRecommendImg.setTid(type);
                            customerRecommendImg.setRefid(refid);
                            customerRecommendImgRepository.save(customerRecommendImg);

                            responseContainer.setMessage(customerRecommendImg);


                        }
                    }

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


    @RequestMapping({"/getuserqr"})
    @ResponseBody
    ResponseContainer getuserqr(String session) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                if (customer.getQrpic() == null || customer.getQrpic().isEmpty()) {
                    //need get qrpic first
                    InputStream inputStream = null;
                    BufferedImage image_userqr = null;
                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);

                    HttpResponse response = WeixinHelper.getuserqr(userid, access_token);
                    long datalen = response.getEntity().getContentLength();
                    if (datalen < 256) {
                        // deal error
                        JsonParser parser = new JsonParser();  //创建JSON解析器

                        String responsestr = EntityUtils.toString(response.getEntity());
                        JsonObject object = (JsonObject) parser.parse(responsestr);


                        if (object != null) {
                            String errcode = object.get("errcode").getAsString();
                            String errmsg = object.get("errmsg").getAsString();

                            logger.warn("generate qr code return err with errcode = " + errcode + " msg = " + errmsg);


                        }

                        responseContainer.setReturncode(ResponeCodeConstant.USER_SHARE_MSG_GENERATE_ERR);
                        responseContainer.setErrormsg(ResponeCodeConstant.USER_SHARE_MSG_GENERATE_ERR_STR);

                        return responseContainer;

                    } else {
                        inputStream = response.getEntity().getContent();

                        //image_userqr = ImageIO.read(inputStream);
                        //ByteArrayOutputStream out = new ByteArrayOutputStream();
                        //ImageIO.write(image_userqr, "png", out);
                        //byte[] imageraws = out.toByteArray();

                        String key = "tqxj_img_user/" + "u"+ customer.getId() +"_" + new Date().getTime() ;

                        WxOssUtils.WxOssResult wxOssResult = WxOssUtils.uploadStream(inputStream,key,(int)datalen);

                        if (wxOssResult.isBret()) {
                            String fullqr = WxOssUtils.WXOSS_PREX + key;

                            customer.setQrpic(fullqr);

                            customerService.update(customer);

                            responseContainer.setMessage(customer);
                        }
                    }



                } else {

                    responseContainer.setMessage(customer);
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


    @RequestMapping({"/getuserlotterys"})
    @ResponseBody
    ResponseContainer getuserlotterys(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Sort sort = new Sort(Sort.Direction.DESC, "id");

                Pageable pageable = new PageRequest(offset, size, sort);

                List<GiftActivity>giftActivities = giftActivityRepository.findAllByGiftfromAndOwnerid(1,userid, pageable);

                responseContainer.setLists(giftActivities);

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


    @RequestMapping({"/adduserlottery"})
    @ResponseBody
    ResponseContainer adduserlottery(String session, GiftActivity giftActivity) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                giftActivity.setOwnerid(userid);
                giftActivity.setModifytime(new Date());
                giftActivity.setStart_time(new Date());
                giftActivity.setCreatetime(new Date());
                giftActivity.setOwnerpic(customer.getHeadpic());
                giftActivity.setOwnername(customer.getNickname());
                giftActivity.setGiftfrom(1);
                giftActivityRepository.save(giftActivity);



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


    @RequestMapping({"/updateuserlottery"})
    @ResponseBody
    ResponseContainer updateuserlottery(String session,  GiftActivity usergiftActivity) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(usergiftActivity.getId());

                if(giftActivity!=null)
                {
                    giftActivity.setDays(usergiftActivity.getDays());
                    giftActivity.setEndtime(usergiftActivity.getEndtime());
                    giftActivity.setGift_name(usergiftActivity.getGift_name());
                    giftActivity.setGoods_name(usergiftActivity.getGoods_name());
                    giftActivity.setGoods_picture(usergiftActivity.getGoods_picture());
                    giftActivity.setMode(usergiftActivity.getMode());
                    giftActivity.setCount(usergiftActivity.getCount());
                    giftActivity.setNum(usergiftActivity.getNum());
                    giftActivity.setModifytime(new Date());
                    giftActivity.setRemarks(usergiftActivity.getRemarks());
                    giftActivity.setUser_propagate(usergiftActivity.getUser_propagate());

                    giftActivityRepository.save(giftActivity);
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


    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {

        //转换日期
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }


    @RequestMapping({"/deleteuserlottery"})
    @ResponseBody
    ResponseContainer deleteuserlottery(String session, int lotteryid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);
                if (giftActivity.getOwnerid() == userid) {
                    giftActivityRepository.delete(giftActivity);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                    responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
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


    @RequestMapping({"/setuserlotteryapply"})
    @ResponseBody
    ResponseContainer setuserlotterypublic(String session, int lotteryid, int apply) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);

                if(giftActivity!=null)
                {
                    if(apply == 1 && giftActivity.getStatus() == GiftActivity.GIFT_STATUS_VOID)
                    {
                        giftActivity.setStatus(GiftActivity.GIFT_STATUS_APPLY);
                        giftActivityRepository.save(giftActivity);
                    }
                    else if(apply == 0 && giftActivity.getStatus() == GiftActivity.GIFT_STATUS_APPLY)
                    {
                        giftActivity.setStatus(GiftActivity.GIFT_STATUS_VOID);
                        giftActivityRepository.save(giftActivity);
                    }
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


    @RequestMapping({"/getpropagatesettings"})
    @ResponseBody
    ResponseContainer getpropagatesettings(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<UserPropagateSetting> userPropagateSettings = userPropagateSettingRepository.findByUid(userid);

                responseContainer.setLists(userPropagateSettings);

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


    @RequestMapping({"/getdefaultpropagatesetting"})
    @ResponseBody
    ResponseContainer getdefaultpropagatesetting(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<UserPropagateSetting> userPropagateSettings = userPropagateSettingRepository.findByUidAndIsdefault(userid,1);

                if (userPropagateSettings != null && !userPropagateSettings.isEmpty()) {
                    responseContainer.setMessage(userPropagateSettings.get(0));
                } else {
                    responseContainer.setMessage(null);
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


    @RequestMapping({"/addpropagatesetting"})
    @ResponseBody
    ResponseContainer addpropagatesetting(String session, UserPropagateSetting userPropagateSetting) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                userPropagateSetting.setUid(userid);

                int count = userPropagateSettingRepository.countByUid(userid);

                if (count == 0) {
                    userPropagateSetting.setIsdefault(1);
                }

                userPropagateSettingRepository.save(userPropagateSetting);

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





    @RequestMapping({"/getpropagatesetting"})
    @ResponseBody
    ResponseContainer getpropagatesetting(String session, int settingid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                UserPropagateSetting userPropagateSetting = userPropagateSettingRepository.findById(settingid);

                if (userPropagateSetting.getUid() == userid) {
                    responseContainer.setMessage(userPropagateSetting);
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


    @RequestMapping({"/editpropagatesetting"})
    @ResponseBody
    ResponseContainer editpropagatesetting(String session, UserPropagateSetting userPropagateSetting) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                UserPropagateSetting userPropagateSettingold = userPropagateSettingRepository.findById(userPropagateSetting.getId());

                if (userPropagateSettingold.getUid() == userid) {
                    userPropagateSettingold.setIntroduction(userPropagateSetting.getIntroduction());
                    userPropagateSettingold.setName(userPropagateSetting.getName());
                    userPropagateSettingold.setSpreadvalue(userPropagateSetting.getSpreadvalue());
                    userPropagateSettingold.setType(userPropagateSetting.getType());
                    userPropagateSettingRepository.save(userPropagateSettingold);
                    responseContainer.setMessage(userPropagateSettingold);
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


    @RequestMapping({"/deletepropagatesetting"})
    @ResponseBody
    ResponseContainer deletepropagatesetting(String session, int settingid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);


            if (customer != null) {
                int userid = customer.getId();

                UserPropagateSetting userPropagateSetting = userPropagateSettingRepository.findById(settingid);
                if (userPropagateSetting.getUid() == userid) {
                    boolean bautosetdefault = false;
                    if (userPropagateSetting.getIsdefault() == CustomerShipAddr.ADDR_DEFAULT) {
                        bautosetdefault = true;
                    }
                    userPropagateSettingRepository.delete(userPropagateSetting);

                    if (bautosetdefault) {
                        List<UserPropagateSetting> userPropagateSettings = userPropagateSettingRepository.findByUid(userid);
                        if (!userPropagateSettings.isEmpty()) {
                            userPropagateSettings.get(0).setIsdefault(CustomerShipAddr.ADDR_DEFAULT);
                            userPropagateSettingRepository.save(userPropagateSettings.get(0));
                        }
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


    @RequestMapping({"/setpropagatesettingdefault"})
    @ResponseBody
    ResponseContainer setpropagatesettingdefault(String session, int settingid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                UserPropagateSetting userPropagateSetting = userPropagateSettingRepository.findById(settingid);

                List<UserPropagateSetting> userPropagateSettings = userPropagateSettingRepository.findByUidAndIsdefault(userid,1);
                if (userPropagateSettings != null) {
                    for (UserPropagateSetting userPropagateSetting1 : userPropagateSettings) {
                        userPropagateSetting1.setIsdefault(0);
                        userPropagateSettingRepository.save(userPropagateSetting1);
                    }

                }
                if (userPropagateSettings == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                } else {
                    if (userPropagateSetting.getUid() == userid) {
                        userPropagateSetting.setIsdefault(1);

                        userPropagateSettingRepository.save(userPropagateSetting);

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }
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



}
