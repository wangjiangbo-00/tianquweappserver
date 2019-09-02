package com.ziyoushenghuo.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.Md5Util;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundSettleSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.GoodsSearchRepository;
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
public class OwnerController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;


    @Autowired
    private GoodsSearchRepository goodsSearchRepository;

    @Autowired
    private GoodsEditService goodsEditService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private GoodsImageService goodsImageService;


    @Autowired
    private OrderService orderService;

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
    private RefundProcessService refundProcessService;

    @Autowired
    DelayRefundSettleSender delayRefundSettleSender;


    @Autowired
    OwnerRepository ownerRepository;


    private static Logger logger = LoggerFactory.getLogger(OwnerController.class);

    @RequestMapping({"/getshopgoods"})
    @ResponseBody
    ResponseContainer getshopgoods(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                int shopid = owner.getShopid();
                List<GoodsBasic> goods = goodsService.GetAllByShopid(shopid);
                responseContainer.setLists(goods);

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

    @RequestMapping({"/setUserPersonal"})
    @ResponseBody
    ResponseContainer setUserPersonal(String session, String nickname, String headpic) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                owner.setHeadpic(headpic);
                if (nickname != null && !"".equals(nickname)) {
                    nickname = nickname.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
                    logger.warn("nickname:" + nickname);
                }

                owner.setNikename(nickname);

                ownerService.create(owner);
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


    @RequestMapping({"/getshopavailablegoods"})
    @ResponseBody
    ResponseContainer getshopavailablegoods(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                int shopid = owner.getShopid();
                List<GoodsBasic> goods = goodsService.GetAllByShopid(shopid);
                responseContainer.setLists(goods);

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




    @RequestMapping({"/getshopdetail"})
    @ResponseBody
    ResponseContainer getshopdetail(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

                if (owner != null) {
                    int shopid = owner.getShopid();

                    String shopsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_SHOP_DETAILS, String.valueOf(shopid));

                    if (shopsdetails != null) {
                        Type type = new TypeToken<Shop>() {
                        }.getType();
                        Gson gson = new Gson();

                        Shop shop = gson.fromJson(shopsdetails, type);

                        responseContainer.setMessage(shop);

                    } else {
                        Shop shop = shopService.GetById(shopid);
                        if (shop != null) {


                            Gson gson = new Gson();
                            String jsonstr = gson.toJson(shop);
                            redisUtils.hmSet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, String.valueOf(shopid), jsonstr);
                            responseContainer.setMessage(shop);


                        }
                    }

                }


             else {
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

    @RequestMapping({"/onlogin"})
    @ResponseBody
    ResponseContainer onlogin(String username, String code) {
        ResponseContainer responseContainer = new ResponseContainer();

        StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?appid=");
        sb.append(WxPayConfig.owner_appid);
        sb.append("&secret=");
        sb.append(WxPayConfig.OWNER_APP_SECRET);
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
                    String sessionkey = object.get("session_key").getAsString();

                    Owner owner = ownerService.getOwnerByUser(username);
                    Date date = new Date();

                    String dateStr = new SimpleDateFormat(AppCommon.COMMON_DATE_FORMAT).format(date);

                    String md5Str = Md5Util.getMD5(openid + sessionkey + dateStr);


                    if (owner != null) {

                        if (owner.getWeixin() == null || owner.getWeixin().isEmpty() || owner.getWeixin().equals(openid)) {

                            owner.setToken(md5Str);
                            owner.setWeixin(openid);
                            ownerService.create(owner);
                        } else {
                            responseContainer.setReturncode(ResponeCodeConstant.OWNER_REPEAT_BIND);
                            responseContainer.setErrormsg(ResponeCodeConstant.OWNER_REPEAT_BIND_STR);
                        }
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.USER_NOT_CORRECT);
                        responseContainer.setErrormsg(ResponeCodeConstant.USER_NOT_CORRECT_STR);
                    }


                    responseContainer.setExtramsg(md5Str);


                } else if (object.has("errcode")) {
                    int errcode = object.get("errcode").getAsInt();
                    String errmsg = object.get("errmsg").getAsString();

                    responseContainer.setReturncode(errcode);
                    responseContainer.setErrormsg(errmsg);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.WEIXIN_ONLOGIN_RETRUN_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.WEIXIN_ONLOGIN_RETRUN_FAIL_STR);
                }


            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }
        return responseContainer;
    }


    @RequestMapping({"/getuserinfo"})
    @ResponseBody
    ResponseContainer getuserinfo(String session) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                responseContainer.setMessage(owner);
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


    @RequestMapping({"/getshopinfo"})
    @ResponseBody
    ResponseContainer getshopinfo(String session) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);




            if (owner != null) {

                int shopid = owner.getShopid();

                Shop shop = shopService.GetById(shopid);
                responseContainer.setMessage(shop);
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















    @RequestMapping({"/setgoodsstock"})
    @ResponseBody
    ResponseContainer setGoodsStock(String session, int goodsid,int stock,String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Goods goods = goodsService.getById(goodsid);

                goods.setStock(stock);
                goods.setFormid(formid);
                goodsService.UpdateOrCreate(goods);

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


    @RequestMapping({"/setgoodsstate"})
    @ResponseBody
    ResponseContainer setgoodsstate(String session, int goodsid,int action,String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Goods goods = goodsService.getById(goodsid);

                if(action == 0)
                {
                    if(goods.getState() == Goods.GOODS_STATE_NORMAL)
                    {
                        goods.setState(Goods.GOODS_STATE_APPLY);
                    }
                    else if(goods.getState() == Goods.GOODS_STATE_FORBIT)
                    {
                        goods.setState(Goods.GOODS_STATE_FORBIT_APPLY);
                    }

                }
                else if(action == 1)
                {
                    goods.setState(Goods.GOODS_STATE_NORMAL);
                }
                else if(action == 2)
                {
                    if(goods.getState() == Goods.GOODS_STATE_APPLY)
                    {
                        goods.setState(Goods.GOODS_STATE_NORMAL);
                    }
                    else if(goods.getState() == Goods.GOODS_STATE_FORBIT_APPLY)
                    {
                        goods.setState(Goods.GOODS_STATE_FORBIT);
                    }
                }



                goodsService.UpdateOrCreate(goods);

                List<Object> items = new ArrayList<>();
                items.add(String.valueOf(goods.getId()));
                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, items.toArray());


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

    @RequestMapping({"/getgoodsdetails"})
    @ResponseBody
    ResponseContainer getgoodsdetails(String session, int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            String goodsdetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, String.valueOf(goodsid));
            GoodsDetailsMessage goodsDetailsMessage = null;
            if (goodsdetails != null) {
                Type type = new TypeToken<GoodsDetailsMessage>() {
                }.getType();
                Gson gson = new Gson();

                goodsDetailsMessage = gson.fromJson(goodsdetails, type);

                responseContainer.setMessage(goodsDetailsMessage);

            } else {

                Goods goods = goodsService.getById(goodsid);
                if (goods != null) {
                    goodsDetailsMessage = new GoodsDetailsMessage();
                    List<String> gallery = new ArrayList<String>();
                    if (goods != null) {
                        String imagesStr = goods.getImgarr();

                        String[] imagearr = imagesStr.split(",");


                        for (String imgid : imagearr) {
                            GoodImage goodImage = goodsImageService.getById(Integer.valueOf(imgid));
                            if (goodImage != null) {
                                gallery.add(goodImage.getImageurl());
                            }
                        }
                        goodsDetailsMessage.setGallery(gallery);
                        goodsDetailsMessage.setGoods(goods);

                    }

                    List<GoodsSku> goodsSkus = null;

                    goodsDetailsMessage.setGoodsSkuList(goodsSkus);
                    //GsonBuilder b = new GsonBuilder();

                    // b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                    //Gson gson = b.create();

                    Gson gson = new Gson();


                    String jsonstr = gson.toJson(goodsDetailsMessage);
                    redisUtils.hmSet(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, String.valueOf(goodsid), jsonstr);
                    responseContainer.setMessage(goodsDetailsMessage);


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






}