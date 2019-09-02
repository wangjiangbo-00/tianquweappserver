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
import com.ziyoushenghuo.asyntask.CustomerWithdrewTask;
import com.ziyoushenghuo.asyntask.ShopWithdrewTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.Md5Util;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.rabbitmq.delay.DelayCancalRefundSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.OrderCoverInfo;
import com.ziyoushenghuo.response.ResponeCodeConstant;
import com.ziyoushenghuo.response.ResponseContainer;
import com.ziyoushenghuo.response.WeixinAuthInfoMessage;
import com.ziyoushenghuo.service.*;

import com.ziyoushenghuo.weixinpay.WxPayConfig;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
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
@RequestMapping({"/ziyoutechan/admin"})
public class AdminController {
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
    private CustomerWithdrewService customerWithdrewService;

    @Autowired
    private ShopAccountRecordService shopAccountRecordService;

    @Autowired
    private OwnerProfitService ownerProfitService;

    @Autowired
    private ShopProfitService shopProfitService;

    @Autowired
    private GiftActivityRepository giftActivityRepository;

    @Autowired
    private TeamFounderRepository teamFounderRepository;


    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    private CustomerAccountRecordRepository customerAccountRecordRepository;


    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    OwnerRepository ownerRepository;


    @Autowired
    private DelayCancalRefundSender delayCancalRefundSender;


    @Autowired
    CustomerWithdrewTask customerWithdrewTask;


    @Autowired
    ShopWithdrewTask shopWithdrewTask;

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);


    @RequestMapping({"/getshopedits"})
    @ResponseBody
    ResponseContainer getshopedits(String session, int submit, int review) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            List<GoodsEdit> goodsEdits = null;

            if (review == 0) {
                goodsEdits = goodsEditService.GetAllUnReview();
            } else if (review == 1) {
                goodsEdits = goodsEditService.GetAllReviewOk();
            } else if (review == 2) {
                goodsEdits = goodsEditService.GetAllReviewFail();
            }

            responseContainer.setLists(goodsEdits);


        } catch (Exception e) {

            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/review"})
    @ResponseBody
    ResponseContainer review(int editid, int review, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            GoodsEdit goodsEdit = goodsEditService.getById(editid);

            if (goodsEdit != null) {
                if (GoodsEdit.REVIEW_STATUS_OK == review) {
                    Goods goods = goodsService.getById(goodsEdit.getGoodsid());

                    boolean bchange = false;//goods.IsCoverChanged(goodsEdit);

                    if (goods != null) {
                        int oldcat = goods.getCatid();
                        int newcat = goodsEdit.getCatid();


                        goods.CoverWithEdit(goodsEdit);


                        goods.setState(Goods.GOODS_STATE_OK);


                        goodsService.UpdateOrCreate(goods);


                        if (true) {
                            List<Object> api_get_recommend_goods = new ArrayList<>();
                            api_get_recommend_goods.add(String.valueOf(oldcat));
                            api_get_recommend_goods.add(String.valueOf(0));
                            redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS, api_get_recommend_goods.toArray());

                            List<Object> api_get_shop_goods = new ArrayList<>();
                            api_get_shop_goods.add(String.valueOf(goods.getShop().getId()));
                            redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_SHOP_GOODS, api_get_shop_goods.toArray());

                            redisUtils.remove(RedisKeyConstant.VALUE_SYS_SEARCH_GOODS);


                        } else {
                            if (oldcat != newcat) {
                                List<Object> api_get_recommend_goods = new ArrayList<>();
                                api_get_recommend_goods.add(String.valueOf(oldcat));
                                api_get_recommend_goods.add(String.valueOf(newcat));
                                if (bchange) {
                                    api_get_recommend_goods.add(String.valueOf(0));
                                }
                                redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS, api_get_recommend_goods.toArray());

                                if (bchange) {
                                    List<Object> api_get_shop_goods = new ArrayList<>();
                                    api_get_shop_goods.add(String.valueOf(goods.getShop().getId()));
                                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_SHOP_GOODS, api_get_shop_goods.toArray());
                                }

                            } else {
                                if (bchange) {
                                    List<Object> api_get_recommend_goods = new ArrayList<>();
                                    api_get_recommend_goods.add(String.valueOf(oldcat));
                                    api_get_recommend_goods.add(String.valueOf(0));
                                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS, api_get_recommend_goods.toArray());

                                    List<Object> api_get_shop_goods = new ArrayList<>();
                                    api_get_shop_goods.add(String.valueOf(goods.getShop().getId()));
                                    redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_SHOP_GOODS, api_get_shop_goods.toArray());
                                }
                            }
                            List<Object> api_get_goodsdetail = new ArrayList<>();
                            api_get_goodsdetail.add(String.valueOf(goods.getId()));
                            redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_GOODS_DETAILS, api_get_goodsdetail.toArray());
                        }

                        if (bchange) {
                            redisUtils.remove(RedisKeyConstant.VALUE_SYS_SEARCH_GOODS);
                        }


                    }

                    goodsEditService.Delete(goodsEdit);
                } else {
                    goodsEdit.setReviewstatus(review);
                    goodsEdit.setRefusemsg(refusemsg);

                    goodsEditService.UpdateOrCreate(goodsEdit);
                }

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }

    @RequestMapping({"/setgoodstate"})
    @ResponseBody
    ResponseContainer setgoodstate(String session, int goodsid,int state, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Goods goods = goodsService.getById(goodsid);

            if (goods != null) {
                goods.setState(state);
                goods.setExtra(refusemsg);
                goodsService.UpdateOrCreate(goods);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/getgoodsonlineapply"})
    @ResponseBody
    ResponseContainer getgoodsonlineapply(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);


            if (owner != null && owner.getIsmember() == 1) {
                List<GoodsBasic> goods = goodsService.GetGoodsOnlineApply();
                responseContainer.setLists(goods);
            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/refuselottery"})
    @ResponseBody
    ResponseContainer refuselottery(String session, int lotteryid, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);

            if (giftActivity != null) {
                giftActivity.setStatus(GiftActivity.GIFT_STATUS_APPLY_FAIL);
                giftActivity.setRemarks(refusemsg);
                // 写入json到
                giftActivityRepository.save(giftActivity);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }



    @RequestMapping({"/agreelottery"})
    @ResponseBody
    ResponseContainer agreelottery(String session, int lotteryid, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);

            if (giftActivity != null) {
                String url = "https://www.weiruikj.cn/ziyoutechan/schedule/giftop?";
                url+= "id=" + lotteryid;

                url+= "&&action=" + 1;

                String respone = WxPayUtils.httpRequest(url, "GET", "");

                logger.warn(respone);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/getlotterysonlineapply"})
    @ResponseBody
    ResponseContainer getlotterysonlineapply(String session, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);


            if (owner != null && owner.getIsmember() == 1) {

                Sort sort = new Sort(Sort.Direction.DESC,"id");

                Pageable pageable = new PageRequest(offset,size,sort);
                List<GiftActivity> giftActivities = giftActivityRepository.findAllByStatus(GiftActivity.GIFT_STATUS_APPLY,pageable);
                responseContainer.setLists(giftActivities);
            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/refusesupergroup"})
    @ResponseBody
    ResponseContainer refusesupergroup(String session, int id, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            TeamFounder teamFounder = teamFounderRepository.findById(id);

            if (teamFounder != null) {
                teamFounder.setStatus(GiftActivity.GIFT_STATUS_APPLY_FAIL);

                // 写入json到
                teamFounderRepository.save(teamFounder);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }



    @RequestMapping({"/agreesupergroup"})
    @ResponseBody
    ResponseContainer agreesupergroup(String session, int id, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            TeamFounder teamFounder = teamFounderRepository.findById(id);

            if (teamFounder != null) {
                String url = "https://www.weiruikj.cn/ziyoutechan/schedule/groupop?";
                url+= "id=" + id;

                url+= "&&action=" + 1;

                String respone = WxPayUtils.httpRequest(url, "GET", "");

                logger.warn(respone);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/agreeonlineapply"})
    @ResponseBody
    ResponseContainer agreeonlineapply(String session, int goodsid, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            GoodsBasic goodsBasic = goodsSearchRepository.findById(goodsid);

            if (goodsBasic != null) {

                goodsBasic.setState(Goods.GOODS_STATE_OK);

                goodsSearchRepository.save(goodsBasic);
            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }

    @RequestMapping({"/disagreeonlineapply"})
    @ResponseBody
    ResponseContainer disagreeonlineapply(String session, int goodsid, String refusemsg) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Goods goods = goodsService.getById(goodsid);

            if (goods != null) {

                if(goods.getState() == Goods.GOODS_STATE_APPLY)
                {
                    goods.setState(Goods.GOODS_STATE_REFUSE);
                }
                else if(goods.getState() == Goods.GOODS_STATE_FORBIT_APPLY)
                {
                    goods.setState(Goods.GOODS_STATE_FORBIT_REFUSE);
                }


                goods.setExtra(refusemsg);



                goodsService.UpdateOrCreate(goods);
            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/getsupergroupsonlineapply"})
    @ResponseBody
    ResponseContainer getsupergroupsonlineapply(String session, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);


            if (owner != null && owner.getIsmember() == 1) {

                Sort sort = new Sort(Sort.Direction.DESC,"id");

                Pageable pageable = new PageRequest(offset,size,sort);
                List<TeamFounder> teamFounders = teamFounderRepository.findByTypeAndStatus(TeamFounder.GROUP_TYPE_PLATFORM,GiftActivity.GIFT_STATUS_APPLY,pageable);
                responseContainer.setLists(teamFounders);
            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/getuserrefundprocesses"})
    @ResponseBody
    ResponseContainer getuserrefundprocesses(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                List<RefundProcess> refundProcessList = refundProcessService.getadminneeddeal(offset, size);

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


    @RequestMapping({"/clearallcache"})
    @ResponseBody
    ResponseContainer clearallcache(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);


            if (owner != null && owner.getIsmember() == 1) {


                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_GROUP);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_ACTIVITIES);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_BANNERS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_DISCOUNT_GOODS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PROMITION_SHOWS);


                redisUtils.remove(RedisKeyConstant.VALUE_SYS_SEARCH_GOODS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_RECOMMEND_SHOPS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_RECOMMEND_GOODS);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_GOODS);


                redisUtils.remove(RedisKeyConstant.VALUE_GET_CATEGORYS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_REGIONS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_QUESTIONS);
                redisUtils.remove(RedisKeyConstant.VALUE_SYS_SHOP_PROVINCES);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_FEELIST);
                redisUtils.remove(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFROMGROUP_DETAILS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_SHOP_DETAILS);
                redisUtils.remove(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS);

                redisUtils.remove(RedisKeyConstant.VALUE_GET_FREEORDERS);

            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;


    }


    @RequestMapping({"/getexceptionwithdrews"})
    @ResponseBody
    ResponseContainer getexceptionwithdrews(String session, int withdrewtype,int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                if(withdrewtype == 0)
                {



                    List<CustomerWithdrew> customerWithdrews = customerWithdrewService.getRecentsByStatus(2, offset, size);

                    responseContainer.setLists(customerWithdrews);
                }
                else
                {
                    List<Withdrew> withdrews = withdrewService.getRecentsByStatus(2, offset, size);

                    responseContainer.setLists(withdrews);
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



    @RequestMapping({"/rewithdrew"})
    @ResponseBody
    ResponseContainer rewithdrew(String session, int withdrewid,int fromtype) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                if(fromtype == 0)
                {

                    CustomerWithdrew customerWithdrew = customerWithdrewService.getById(withdrewid);

                    if(customerWithdrew!=null)
                    {
                        List<Integer> integers = new ArrayList<>();
                        integers.add(CustomerAccountRecord.OP_TYPE_WITHDREW);
                        List<CustomerAccountRecord>    customerAccountRecords  = customerAccountRecordRepository.findByRefidAndTypeIn(customerWithdrew.getId(),integers);

                        if(customerAccountRecords!=null)
                        {
                            CustomerAccountRecord customerAccountRecord = customerAccountRecords.get(0);

                            CustomerAccount customerAccount = customerAccountRepository.findByCid(customerWithdrew.getCid());

                            customerWithdrewTask.withdrew(customerWithdrew,customerAccount,customerAccountRecord);

                        }
                    }



                }
                else
                {

                    Withdrew withdrew =  withdrewService.getById(withdrewid);

                    ShopAccountRecord shopAccountRecord = shopAccountRecordService.getByRefidAndType(withdrew.getId(),CustomerAccountRecord.OP_TYPE_WITHDREW);

                    ShopAccount shopAccount = shopAccountService.GetByShopid(withdrew.getShopid());

                    shopWithdrewTask.withdrew(withdrew,shopAccount,shopAccountRecord);

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



    @RequestMapping({"/cancalwithdrew"})
    @ResponseBody
    ResponseContainer cancalwithdrew(String session, int withdrewid,int fromtype) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                if(fromtype == 0)
                {

                    CustomerWithdrew customerWithdrew = customerWithdrewService.getById(withdrewid);

                    if(customerWithdrew!=null)
                    {
                        List<Integer> integers = new ArrayList<>();
                        integers.add(CustomerAccountRecord.OP_TYPE_WITHDREW);
                        List<CustomerAccountRecord>    customerAccountRecords  = customerAccountRecordRepository.findByRefidAndTypeIn(customerWithdrew.getId(),integers);

                        if(customerAccountRecords!=null)
                        {
                            CustomerAccountRecord customerAccountRecord = customerAccountRecords.get(0);

                            customerAccountRecord.setExtra("提现撤回");

                            customerAccountRecordRepository.save(customerAccountRecord);

                            CustomerAccount customerAccount = customerAccountRepository.findByCid(customerWithdrew.getCid());

                            double lockmoney = customerAccount.getMoney_lock();

                            customerAccount.setMoney_lock(0);
                            customerAccount.setMoney(customerAccount.getMoney() + lockmoney);

                            customerAccountRepository.save(customerAccount);

                            customerWithdrew.setStatus(CustomerWithdrew.STATUS_CANCAL);
                            customerWithdrewService.createOrUpdate(customerWithdrew);






                        }
                    }



                }
                else
                {

                    Withdrew withdrew =  withdrewService.getById(withdrewid);

                    withdrew.setRemark("提现撤回");

                    withdrewService.createOrUpdate(withdrew);

                    ShopAccountRecord shopAccountRecord = shopAccountRecordService.getByRefidAndType(withdrew.getId(),CustomerAccountRecord.OP_TYPE_WITHDREW);


                    shopAccountRecord.setRemark("提现撤回");
                    shopAccountRecordService.createOrUpdate(shopAccountRecord);
                    ShopAccount shopAccount = shopAccountService.GetByShopid(withdrew.getShopid());

                    double lockmoney = shopAccount.getShop_total_money_lock();

                    shopAccount.setShop_total_money_lock(0);
                    shopAccount.setShop_total_money(shopAccount.getShop_total_money() + lockmoney);


                    withdrew.setStatus(CustomerWithdrew.STATUS_CANCAL);

                    withdrewService.createOrUpdate(withdrew);





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


    @RequestMapping({"/searchgoodsbyname"})
    @ResponseBody
    ResponseContainer searchgoodsbyname(String session, String keyword) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {


                List<GoodsBasic> withdrews = goodsService.searchByName(keyword);

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


    @RequestMapping({"/searchorder"})
    @ResponseBody
    ResponseContainer searchorder(String session, String keyword) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {

                Order order = null;
                boolean bnum = AppCommon.isNumeric(keyword);

                if(bnum)
                {
                    long orderid =  Long.parseLong(keyword);
                    order = orderService.getOrderById(orderid);

                }
                else
                {
                    order = orderService.getOrderByOuttradeno(keyword);
                }
                List<OrderCoverInfo> orderCoverInfoList = new ArrayList<>(1);
                if(order!=null)
                {
                    OrderCoverInfo orderCoverInfo = new OrderCoverInfo();
                    orderCoverInfo.SetByOrder(order);
                    orderCoverInfoList.add(orderCoverInfo);
                }


                responseContainer.setLists(orderCoverInfoList);


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


    @RequestMapping({"/admincancalorder"})
    @ResponseBody
    ResponseContainer admincancalorder(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Order order = orderService.getOrderById(orderid);
                if (order!=null) {
                    String queuemsg = String .valueOf(order.getId()) ;

                    delayCancalRefundSender.send(queuemsg);
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/resetorder"})
    @ResponseBody
    ResponseContainer resetorder(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                if (refundProcess!=null) {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_NOMAL);

                    refundProcessService.createOrUpdate(refundProcess);
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/getorder"})
    @ResponseBody
    ResponseContainer getUserOrder(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Order order = orderService.getOrderById(orderid);
                if (order!=null) {
                    responseContainer.setMessage(order);
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
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String sessionvalue = operations.get(session);

            if (sessionvalue != null) {
                int userid = Integer.valueOf(sessionvalue);
                userid = 296;
                Owner owner = ownerService.getOwnerById(userid);

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


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
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
        sb.append(WxPayConfig.appid);
        sb.append("&secret=");
        sb.append(WxPayConfig.APP_SECRET);
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


                    ValueOperations<String, String> operations = redisTemplate.opsForValue();

                    boolean hasKey = redisTemplate.hasKey(md5Str);
                    if (hasKey) {
                        //hash crash not deal now
                        responseContainer.setReturncode(ResponeCodeConstant.WEIXIN_GENERATE_TOKEN_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.WEIXIN_GENERATE_TOKEN_FAIL_STR);
                    } else {
                        if (owner != null) {

                            if (owner.getToken() != null) {
                                //redisTemplate.delete(customer.getToken());
                                owner.setToken(md5Str);
                                owner.setWeixin(openid);
                                ownerService.create(owner);
                            } else {
                                owner.setToken(md5Str);
                                owner.setWeixin(openid);
                                ownerService.create(owner);
                            }
                        } else {

                        }
                        String idkey = String.valueOf(owner.getId());
                        operations.set(md5Str, idkey, RedisKeyConstant.EXPIRE_SESSION_TIME, TimeUnit.DAYS);
                        WeixinAuthInfoMessage weixinAuthInfoMessage = new WeixinAuthInfoMessage();

                        weixinAuthInfoMessage.setSession(md5Str);
                        responseContainer.setMessage(weixinAuthInfoMessage);
                    }


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
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
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

            List<Withdrew> withdrews = withdrewService.getRecentsByStatus(0, page, size);

            responseContainer.setLists(withdrews);

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
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
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String sessionvalue = operations.get(session);

            if (sessionvalue != null) {

                int userid = Integer.valueOf(sessionvalue);
                Owner owner = ownerService.getOwnerById(userid);

                if (owner != null) {
                    int shopid = owner.getShopid();

                    List<OwnerProfit> ownerProfits = ownerProfitService.getOwnerRecentlyProfits(shopid, page, size);

                    responseContainer.setLists(ownerProfits);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                }


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
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
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String sessionvalue = operations.get(session);

            if (sessionvalue != null) {

                int userid = Integer.valueOf(sessionvalue);
                Owner owner = ownerService.getOwnerById(userid);

                if (owner != null) {
                    int shopid = owner.getShopid();

                    List<ShopAccountRecord> shopAccountRecords = shopAccountRecordService.getOwnerRecentlyRecords(shopid, page, size);

                    responseContainer.setLists(shopAccountRecords);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                }


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
            }
        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }
        return responseContainer;
    }


    @RequestMapping({"/getcloseapplyorders"})
    @ResponseBody
    ResponseContainer getCloseApplyOrders(String session) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {

            List<Order> orders = new ArrayList<>();

            orders = null;

            responseContainer.setLists(orders);

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/getautocloseorders"})
    @ResponseBody
    ResponseContainer getAutoCloseOrders(String session) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {


            List<Order> orders = new ArrayList<>();

            orders = null;

            responseContainer.setLists(orders);


        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/geteditdetails"})
    @ResponseBody
    ResponseContainer geteditdetails(String session, int id) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            GoodsEdit goodsEdit = goodsEditService.getById(id);
            if (goodsEdit != null) {
                List<String> gallery = new ArrayList<String>();
                if (goodsEdit != null) {
                    String imagesStr = goodsEdit.getImgarr();

                    String[] imagearr = imagesStr.split(",");


                    for (String imgid : imagearr) {
                        GoodImage goodImage = goodsImageService.getById(Integer.valueOf(imgid));
                        if (goodImage != null) {
                            gallery.add(goodImage.getImageurl());
                        }
                    }

                    goodsEdit.setGallery(gallery);
                }


                responseContainer.setMessage(goodsEdit);


            }

        } catch (Exception e) {
            int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();
            logger.warn(e.getMessage().substring(0, len));
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

                if (owner.getToken() == null || owner.getToken().equals("")) {


                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.OWNER_REPEAT_BIND);
                    responseContainer.setErrormsg(ResponeCodeConstant.OWNER_REPEAT_BIND_STR);
                }

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

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String sessionvalue = operations.get(session);

        if (sessionvalue != null) {
            int ownerid = Integer.valueOf(sessionvalue);

            Owner owner = ownerService.getOwnerById(ownerid);
            if (owner != null) {

                owner.setWeixin("");
                owner.setToken("");
                ownerService.update(owner);
            }

        } else {
            responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
            responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
        }

        return responseContainer;
    }


}