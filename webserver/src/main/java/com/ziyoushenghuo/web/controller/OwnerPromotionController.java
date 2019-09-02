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
@RequestMapping({"/ziyoutechan/owner"})
public class OwnerPromotionController {

    private final int SESSION_TIME = 30 * 365;


    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(OwnerPromotionController.class);


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAddrRepository orderAddrRepository;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TeamFounderRepository teamFounderRepository;


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
    GoodsSearchRepository goodsSearchRepository;


    @Autowired
    OrderShopRepository orderShopRepository;

    @Autowired
    OrderExtraRepository orderExtraRepository;


    @Autowired
    WeixinTemplate weixinTemplate;


    @Autowired
    OwnerRepository ownerRepository;


    @Autowired
    private TeamFounderService teamFounderService;



    @RequestMapping({"/getsupergroups"})
    @ResponseBody
    ResponseContainer getsupergroups(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                Sort sort = new Sort(Sort.Direction.DESC, "id");

                Pageable pageable = new PageRequest(offset, size, sort);

                ShopCover shopCover = new ShopCover();
                shopCover.setId(shopid);

                List<TeamFounder>teamFounders = teamFounderRepository.findByTypeAndShopCover(2,shopCover, pageable);

                responseContainer.setLists(teamFounders);

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

    @RequestMapping({"/getlotterys"})
    @ResponseBody
    ResponseContainer getlotterys(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                Sort sort = new Sort(Sort.Direction.DESC, "id");

                Pageable pageable = new PageRequest(offset, size, sort);

                List<GiftActivity>giftActivities = giftActivityRepository.findAllByGiftfromAndOwnerid(0,shopid, pageable);

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


    @RequestMapping({"/getplatformgroupdetails"})
    @ResponseBody
    ResponseContainer getplatformgroupdetails(String session, int groupid) {

        ResponseContainer responseContainer = new ResponseContainer();



        try {

            Owner owner = ownerRepository.findByToken(session);



            SuperGroupDetailsMessage superGroupDetailsMessage = new SuperGroupDetailsMessage();



                if (owner != null) {
                String lotterydetails = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS, String.valueOf(groupid));


                    TeamFounder teamFounder = teamFounderService.GetById(groupid);
                    if (teamFounder != null) {

                        superGroupDetailsMessage.setTeamFounder(teamFounder);
                        superGroupDetailsMessage.setPeople(teamFounder.getPeople());
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                        responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    }



                superGroupDetailsMessage.setOrderid(0);

            } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                }

            responseContainer.setMessage(superGroupDetailsMessage);

            } catch (Exception e) {
                responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
                responseContainer.setErrormsg(e.getMessage().substring(0, len));
                logger.warn(e.getMessage().substring(0, len));
            }





        return responseContainer;
    }


    @RequestMapping({"/setsupergroupapply"})
    @ResponseBody
    ResponseContainer setsupergroupapply(String session, int groupid, int apply) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                TeamFounder teamFounder = teamFounderRepository.findById(groupid);

                if(teamFounder!=null)
                {
                    if(apply == 1 && teamFounder.getStatus() == GiftActivity.GIFT_STATUS_VOID)
                    {
                        teamFounder.setStatus(GiftActivity.GIFT_STATUS_APPLY);
                        teamFounderRepository.save(teamFounder);
                    }
                    else if(apply == 0 && teamFounder.getStatus() == GiftActivity.GIFT_STATUS_APPLY)
                    {
                        teamFounder.setStatus(GiftActivity.GIFT_STATUS_VOID);
                        teamFounderRepository.save(teamFounder);
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



    @RequestMapping({"/getlotterydetails"})
    @ResponseBody
    ResponseContainer getlotterydetails(String session, int lotteryid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {


            GiftActivityDetailsMessage giftActivityDetailsMessage = new GiftActivityDetailsMessage();
            GiftActivity giftActivity = null;

            giftActivity = giftActivityRepository.findById(lotteryid);
            if (giftActivity != null) {

                giftActivityDetailsMessage.setGiftActivity(giftActivity);
                responseContainer.setMessage(giftActivityDetailsMessage);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                return responseContainer;
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
    ResponseContainer adduserlottery(String session, GiftActivity giftActivity,int goodsdid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                Shop shop = shopRepository.findById(shopid);

                GoodsBasic goodsBasic = goodsSearchRepository.findById(goodsdid);

                GoodsCover goodsCover = new GoodsCover();
                goodsCover.setId(goodsdid);


                giftActivity.setGoods_name(goodsBasic.getIntroduction());
                giftActivity.setGoods_picture(goodsBasic.getBannerurl());
                giftActivity.setGift_name(goodsBasic.getGoodsname());
                giftActivity.setGoodsCover(goodsCover);
                giftActivity.setOwnerid(shopid);
                giftActivity.setModifytime(new Date());
                giftActivity.setStart_time(new Date());
                giftActivity.setCreatetime(new Date());
                giftActivity.setOwnerpic(shop.getShoplogo());
                giftActivity.setOwnername("田趣小集");
                giftActivity.setGiftfrom(0);
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
    ResponseContainer updateuserlottery(String session,  GiftActivity usergiftActivity,int goodsdid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                GiftActivity giftActivity = giftActivityRepository.findById(usergiftActivity.getId());

                if(giftActivity!=null)
                {

                    GoodsBasic goodsBasic = goodsSearchRepository.findById(goodsdid);

                    GoodsCover goodsCover = new GoodsCover();
                    goodsCover.setId(goodsdid);


                    giftActivity.setGoods_name(goodsBasic.getIntroduction());
                    giftActivity.setGoods_picture(goodsBasic.getBannerurl());
                    giftActivity.setGift_name(goodsBasic.getGoodsname());
                    giftActivity.setGoodsCover(goodsCover);
                    giftActivity.setOwnerid(shopid);
                    giftActivity.setDays(usergiftActivity.getDays());
                    giftActivity.setEndtime(usergiftActivity.getEndtime());

                    giftActivity.setGoods_picture(usergiftActivity.getGoods_picture());
                    giftActivity.setMode(usergiftActivity.getMode());
                    giftActivity.setCount(usergiftActivity.getCount());
                    giftActivity.setNum(usergiftActivity.getNum());
                    giftActivity.setModifytime(new Date());
                    giftActivity.setRemarks(usergiftActivity.getRemarks());
                    //giftActivity.setUser_propagate(usergiftActivity.getUser_propagate());

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


    @RequestMapping({"/updatesupergroup"})
    @ResponseBody
    ResponseContainer updatesupergroup(String session,  TeamFounder teamFounder,int goodsdid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                TeamFounder teamFounderForUpdate = teamFounderRepository.findById(teamFounder.getId());

                if(teamFounderForUpdate!=null)
                {

                    GoodsCover goodsCover = new GoodsCover();
                    goodsCover.setId(goodsdid);

                    teamFounderForUpdate.setStarttime(teamFounder.getStarttime());
                    teamFounderForUpdate.setExpiretime(teamFounder.getExpiretime());
                    teamFounderForUpdate.setGoodsCover(goodsCover);
                    teamFounderForUpdate.setStage_format(teamFounder.getStage_format());


                    teamFounderRepository.save(teamFounderForUpdate);
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




    @RequestMapping({"/addsupergroup"})
    @ResponseBody
    ResponseContainer addsupergroup(String session, TeamFounder teamFounder ,int goodsdid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                int shopid = owner.getShopid();

                ShopCover shopCover = new ShopCover();
                shopCover.setId(shopid);


                GoodsCover goodsCover = new GoodsCover();
                goodsCover.setId(goodsdid);


                teamFounder.setShopCover(shopCover);


                teamFounder.setGoodsCover(goodsCover);


                teamFounder.setCreatetime(new Date());

                teamFounder.setStatus(0);
                teamFounder.setType(TeamFounder.GROUP_TYPE_PLATFORM);




                teamFounderRepository.save(teamFounder);



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
            Owner owner = ownerRepository.findByToken(session);


            if (owner != null) {
                int shopid = owner.getShopid();

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);
                if (giftActivity.getOwnerid() == shopid) {
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
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

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





}
