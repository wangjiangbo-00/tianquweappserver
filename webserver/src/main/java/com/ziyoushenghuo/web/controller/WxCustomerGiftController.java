package com.ziyoushenghuo.web.controller;


import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ziyoushenghuo.HttpUtils.HttpUtils;
import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.HibernateProxyTypeAdapter;
import com.ziyoushenghuo.common.ShipFeeUtils;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.settlement.CustomerSettle;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class WxCustomerGiftController {

    private final int SESSION_TIME = 30 * 365;


    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerGiftController.class);


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
    private GiftParticipateRepository giftParticipateRepository;


    @Autowired
    private GiftWinnerRepository giftWinnerRepository;

    @Autowired
    private PromotionDiscountRepository promotionDiscountRepository;


    @Autowired
    private PromotionDiscountGoodsRepository promotionDiscountGoodsRepository;


    @Autowired
    private AmqpTemplate rabbitTemplate;


    @Autowired
    private ShipFeeUtils shipFeeUtils;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private CustomerSettle customerSettle;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TeamFounderService teamFounderService;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private CustomerShipAddrService customerShipAddrService;


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private OrderExpressService orderExpressService;


    @Autowired
    private DelayUnGroupSender delayUnGroupSender;

    @Autowired
    private DelayUnPaySender delayUnPaySender;


    @Autowired
    private DelayAfterReceiveSender afterReceiveSender;

    @Autowired
    private DelayRefundGiftShipFee delayRefundGiftShipFee;


    @Autowired
    private DelayRefundFreeOrder delayRefundFreeOrder;


    @Autowired
    ShopRepository shopRepository;


    @Autowired
    OrderShopRepository orderShopRepository;

    @Autowired
    OrderExtraRepository orderExtraRepository;


    @Autowired
    WeixinTemplate weixinTemplate;


    @RequestMapping({"/getreceivegiftorders"})
    @ResponseBody
    ResponseContainer getReceiveGiftOrders(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<Order> orders = new ArrayList<>();
                orders = orderService.getReceiveGiftOrders(userid, offset, size);


                List<OrderCoverInfo> orderCoverInfoList = new ArrayList<>(orders.size());


                for (Order order : orders) {
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


    @RequestMapping({"/getsendgiftorders"})
    @ResponseBody
    ResponseContainer getSendGiftOrders(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<Order> orders = new ArrayList<>();

                orders = orderService.getSendGiftOrders(userid, offset, size);

                List<OrderCoverInfo> orderCoverInfoList = new ArrayList<>(orders.size());


                for (Order order : orders) {
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


    @RequestMapping({"/getlotteryorders"})
    @ResponseBody
    ResponseContainer getlotteryorders(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<Order> orders = new ArrayList<>();

                orders = orderService.getLotteryOrders(userid, offset, size);

                List<OrderCoverInfo> orderCoverInfoList = new ArrayList<>(orders.size());


                for (Order order : orders) {
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


    @RequestMapping({"/getuserparticipates"})
    @ResponseBody
    ResponseContainer getuserparticipates(String session, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<GiftParticipate> giftParticipates = new ArrayList<>();

                Sort sort = new Sort(Sort.Direction.DESC,"id");

                Pageable pageable = new PageRequest(offset,size,sort);

                giftParticipates = giftParticipateRepository.findAllByUserid(userid,pageable);

                responseContainer.setLists(giftParticipates);


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






    @RequestMapping({"/getorderwithtoken"})
    @ResponseBody
    ResponseContainer getUserOrder(int orderid, String token) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            Order order = orderService.getOrderById(orderid);

            if (order != null) {
                if (order.getGiven_token().equals(token)) {
                    responseContainer.setMessage(order);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                    responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                }
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

    @RequestMapping({"/bindgiftaddr"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer bindgiftaddr(String session, int orderid, int address_id, String token) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    return responseContainer;
                }

                if (order.getGiven_token().equals(token)) {
                    if (order.getGivenstatus() == Order.ORDER_GIVEN_STATUS_NORMAL) {

                        order.setGivenstatus(Order.ORDER_GIVEN_STATUS_DONE);
                        order.setOrderStatus(Order.ORDER_STATUS_TOSHIP);

                        order.setGiverid(userid);

                        CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(address_id);

                        if (customerShipAddr != null) {
                            OrderAddr orderAddr = new OrderAddr();
                            orderAddr.setId(order.getId());
                            orderAddr.setReceiverMobile(customerShipAddr.getMobile());
                            orderAddr.setReceiverName(customerShipAddr.getConsigner());
                            orderAddr.setReceiverAddress(customerShipAddr.getAddress());
                            orderAddr.setReceiverCity(customerShipAddr.getCity());
                            orderAddr.setReceiverDistrict(customerShipAddr.getDistrict());
                            orderAddr.setReceiverProvince(customerShipAddr.getProvince());
                            orderAddr.setProvincename(customerShipAddr.getProvincename());

                            orderAddr.setCityname(customerShipAddr.getCityname());
                            orderAddr.setDistrictname(customerShipAddr.getDistrictname());

                            orderAddrRepository.save(orderAddr);

                            OrderExtra orderExtra = orderExtraRepository.findById(orderid);

                            if (orderExtra != null) {
                                orderExtra.setRecievenickname(customer.getNickname());
                                orderExtra.setRecieveheadpic(customer.getHeadpic());

                                orderExtra.setGivertime(new Date());

                                orderExtraRepository.save(orderExtra);
                            }


                            if (order.getOrdertype() == Order.ORDER_TYPE_GIVER) {
                                Goods goods = goodsService.getById(order.getOrderGoods().getGoodId());
                                int shipfee = shipFeeUtils.getShipFee(goods, customerShipAddr.getProvince());

                                order.setShipping_money(shipfee);
                                if (order.getPreshippfee() != 0 && order.getPreshippfee() != shipfee) {
                                    // 需要延迟退款
                                    delayRefundGiftShipFee.send("" + orderid);
                                }


                                Customer customerBuyer = customerService.GetOpenIdById(order.getBuyerid());
                                String chancestr = (String) redisUtils.get(RedisKeyConstant.VALUE_GET_ORDER_FREE_CHANCE);
                                String solutionstr = (String) redisUtils.get(RedisKeyConstant.VALUE_GET_ORDER_FREE_SOLUTION);

                                boolean bfree = false;


                                if (chancestr != null && !chancestr.isEmpty() && solutionstr != null && !solutionstr.isEmpty()) {
                                    int chance = Integer.valueOf(chancestr);
                                    int solution = Integer.valueOf(solutionstr);

                                    if (chance > AppCommon.MIN_FREE_CHANCE) {
                                        Random random = new Random();

                                        int mynum = random.nextInt(chance);

                                        if (mynum == solution) {
                                            delayRefundFreeOrder.send("" + orderid);
                                            String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);

                                            bfree = true;
                                            weixinTemplate.sendTemplateGiftGivenFree(access_token, order, customerBuyer, customer.getNickname(), true);


                                            if(order.getGroupbuy()!=2)
                                            {
                                                customerSettle.onOrderCancalSettle(order,Refund.REFUND_TYPE_FREE_ORDER);
                                            }
                                        }

                                    }
                                }

                                if(!bfree)
                                {
                                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                                    weixinTemplate.sendTemplateGiftGiven(access_token, order, customerBuyer, customer.getNickname(), true);

                                    //todo add pre costomersettle

                                    if(order.getGroupbuy()!=2)
                                    {
                                        customerSettle.onOrderPreSettle(order);
                                    }


                                }




                            }

                        }

                        orderService.UpdateOrder(order);


                    } else if (order.getGivenstatus() == Order.ORDER_GIVEN_STATUS_DONE) {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_OVER_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_OVER_FAIL_STR);
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


    @RequestMapping({"/binduserlotteryaddr"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer binduserlotteryaddr(String session, int lotteryid, int address_id) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);

                if (giftActivity == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    return responseContainer;
                }

                String result = giftActivity.getResult();



                if (result != null && !result.isEmpty()) {
                    JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();

                    boolean update = false;

                    for(JsonElement jsonElement:jsonArray)
                    {
                        JsonObject jsonObject = (JsonObject)jsonElement;

                        if(jsonObject!=null)
                        {
                            int prize_userid = jsonObject.get("uid").getAsInt();

                            if(userid == prize_userid)
                            {

                                CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(address_id);
                                if(customerShipAddr!=null)
                                {

                                    String addrstr = customerShipAddr.getConsigner() + "," + customerShipAddr.getMobile() + "," +  customerShipAddr.getProvincename() + customerShipAddr.getCityname() + customerShipAddr.getDistrictname() + customerShipAddr.getAddress();
                                    jsonObject.addProperty("addr", addrstr);
                                    jsonObject.addProperty("addrbind", 1);
                                    update = true;
                                }

                                break;
                            }
                        }
                    }

                    if(update)
                    {
                        result = jsonArray.toString();
                        giftActivity.setResult(result);

                        giftActivityRepository.save(giftActivity);
                    }


                }
                else
                {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    return responseContainer;
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

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                return responseContainer;
            }


            String key = RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + lotteryid;

            List<String> personas = (List<String>) (List) redisUtils.lRange(key, 0, 6);

            giftActivityDetailsMessage.setPersonas(personas);

            String peoplestr = (String) redisUtils.hmGet(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PEOPLE, String.valueOf(lotteryid));

            if (peoplestr != null) {
                giftActivityDetailsMessage.setPeople(Integer.valueOf(peoplestr));
            } else {
                int peoplenum = 0;

                if (giftActivity.getStatus() == GiftActivity.GIFT_STATUS_CAN_SHOW) {
                    peoplenum = giftParticipateRepository.countByGiftidAndStatus(lotteryid, GiftParticipate.PARTICIPATE_ACTION_APPOINTMENT);
                } else {
                    peoplenum = giftParticipateRepository.countByGiftidAndStatus(lotteryid, GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                }

                redisUtils.hmSet(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PEOPLE, String.valueOf(lotteryid), String.valueOf(peoplenum));

                giftActivityDetailsMessage.setPeople(peoplenum);
            }

            responseContainer.setMessage(giftActivityDetailsMessage);

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


    @RequestMapping({"/getprizetoken"})
    @ResponseBody
    ResponseContainer getprizetoken(String session, int lotteryid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);
                if (giftActivity != null) {
                    String result = giftActivity.getResult();

                    if (result == null || result.isEmpty()) {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);

                        return responseContainer;
                    }

                    JsonParser parser = new JsonParser();  //创建JSON解析器
                    JsonArray resultjson = parser.parse(result).getAsJsonArray();


                    String extra = "";
                    if (resultjson.size() > 0) {
                        boolean bfind = false;

                        for (JsonElement jsonElement : resultjson) {
                            JsonObject prize = (JsonObject) jsonElement;

                            int prize_userid = prize.get("uid").getAsInt();

                            if (prize_userid == userid) {

                                int oid = prize.get("oid").getAsInt();
                                String token = prize.get("token").getAsString();

                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("oid", oid);
                                jsonObject.addProperty("token", token);
                                extra = jsonObject.toString();
                                responseContainer.setExtramsg(extra);
                                bfind = true;
                                break;
                            }
                        }

                        if (!bfind) {
                            responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                            responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);

                            return responseContainer;
                        }
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);

                        return responseContainer;
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


    @RequestMapping({"/getparticipatedetails"})
    @ResponseBody
    ResponseContainer getparticipatedetails(String session, int lotteryid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {

            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                GiftParticipate giftParticipate = giftParticipateRepository.findByUseridAndGiftid(userid, lotteryid);


                responseContainer.setMessage(giftParticipate);

            }
            else
            {
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


    @RequestMapping({"/getpromotionshows"})
    @ResponseBody
    ResponseContainer getpromotionshows(String session) {

        ResponseContainer responseContainer = new ResponseContainer();



        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_GET_PROMITION_SHOWS;



            String redisstr = (String) redisUtils.get(key);


            PlatformActivityShowsMessage platformActivityShowsMessage = null;

            if (redisstr == null) {

                platformActivityShowsMessage = new PlatformActivityShowsMessage();
                List<Integer> integers = new ArrayList<>();


                integers.add(GiftActivity.GIFT_STATUS_START);
                integers.add(GiftActivity.GIFT_STATUS_CAN_SHOW);

                List<Sort.Order> orders = new ArrayList<>();
                orders.add(new Sort.Order(Sort.Direction.DESC, "status"));
                orders.add(new Sort.Order(Sort.Direction.DESC, "endtime"));

                Sort sort = new Sort(Sort.Direction.DESC, "id");

                Pageable pageable = new PageRequest(0, 5, sort);


                List<GiftActivity>giftActivities = giftActivityRepository.findAllByStatusInAndGiftfrom(integers,0, pageable);

                List<Sort.Order> orders1 = new ArrayList<>();
                orders1.add(new Sort.Order(Sort.Direction.DESC,"status"));
                orders1.add(new Sort.Order(Sort.Direction.DESC,"expiretime"));

                List<TeamFounder>teamFounders = teamFounderService.GetPlatGroups(integers,new Sort(orders1),0,5);


                List<PromotionDiscount> promotionDiscounts = promotionDiscountRepository.findByLevelAndIsvisibleAndStatusIn(0,1,integers);

                List<PlatformActivityShowsMessage.PlatformDiscount> platformDiscounts = new ArrayList<>();

                if(promotionDiscounts!=null && !promotionDiscounts.isEmpty())
                {
                    for(PromotionDiscount promotionDiscount:promotionDiscounts)
                    {

                        List<Integer> statuses = new ArrayList<Integer>();

                        statuses.add(PromotionDiscount.STATUS_START);
                        statuses.add(PromotionDiscount.STATUS_CAN_SHOW);
                        List<PromotionDiscountGoods>promotionDiscountGoods = promotionDiscountGoodsRepository.findAllByPidAndStatusIn(promotionDiscount.getId(),statuses);

                        if(promotionDiscountGoods!=null && !promotionDiscountGoods.isEmpty())
                        {
                            PlatformActivityShowsMessage.PlatformDiscount platformDiscount = new  PlatformActivityShowsMessage.PlatformDiscount();
                            platformDiscount.setPromotionDiscount(promotionDiscount);
                            platformDiscount.setPromotionDiscountGoods(promotionDiscountGoods);
                            platformDiscounts.add(platformDiscount);

                        }

                    }
                }

                platformActivityShowsMessage.setGiftActivities(giftActivities);
                platformActivityShowsMessage.setPlatformDiscounts(platformDiscounts);
                platformActivityShowsMessage.setTeamFounders(teamFounders);



                Gson gson = new Gson();
                String jsonstr = gson.toJson(platformActivityShowsMessage);
                redisUtils.set(RedisKeyConstant.VALUE_GET_PROMITION_SHOWS,  jsonstr);






            } else {
                Type type = new TypeToken<PlatformActivityShowsMessage>() {
                }.getType();
                Gson gson = new Gson();

                platformActivityShowsMessage = gson.fromJson(redisstr, type);

            }

            responseContainer.setMessage(platformActivityShowsMessage);

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



    @RequestMapping({"/getplatformlotterys"})
    @ResponseBody
    ResponseContainer getplatformlotterys(String session, int datastatus, int offset, int size) {

        ResponseContainer responseContainer = new ResponseContainer();

        GiftActivitysMessage giftActivitysMessage = new GiftActivitysMessage();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String key = RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY;

            String indexstr = String.valueOf(datastatus) + "_" + String.valueOf(offset);

            String redisstr = (String) redisUtils.hmGet(key, String.valueOf(indexstr));
            List<GiftActivity> giftActivities = new ArrayList<GiftActivity>();

            if (redisstr == null) {


                List<Integer> integers = new ArrayList<>();

                if (datastatus == 0) {
                    integers.add(GiftActivity.GIFT_STATUS_START);
                    integers.add(GiftActivity.GIFT_STATUS_CAN_SHOW);

                } else if (datastatus == 1) {
                    integers.add(GiftActivity.GIFT_STATUS_END);
                    integers.add(GiftActivity.GIFT_STATUS_CLOSE);
                }


                List<Sort.Order> orders = new ArrayList<>();
                orders.add(new Sort.Order(Sort.Direction.DESC, "status"));
                orders.add(new Sort.Order(Sort.Direction.DESC, "endtime"));


                Sort sort = new Sort(Sort.Direction.DESC, "id");

                Pageable pageable = new PageRequest(offset, size, sort);


                giftActivities = giftActivityRepository.findAllByStatusInAndGiftfrom(integers,0, pageable);


                Gson gson = new Gson();
                String jsonstr = gson.toJson(giftActivities);
                redisUtils.hmSet(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY, String.valueOf(indexstr), jsonstr);

            } else {
                Type type = new TypeToken<ArrayList<GiftActivity>>() {
                }.getType();
                Gson gson = new Gson();

                giftActivities = gson.fromJson(redisstr, type);

            }

            giftActivitysMessage.setGiftActivities(giftActivities);

            Customer customer = customerService.GetByToken(session);
            List<GiftParticipate> giftParticipates = null;
            if (customer != null) {
                List<Integer> gids = new ArrayList<>();

                if (giftActivities != null && !giftActivities.isEmpty()) {
                    for (GiftActivity giftActivity : giftActivities) {
                        gids.add(giftActivity.getId());
                    }

                    giftParticipates = giftParticipateRepository.findAllByGiftidInAndUserid(gids, customer.getId());


                }
            }

            giftActivitysMessage.setGiftParticipates(giftParticipates);


            responseContainer.setMessage(giftActivitysMessage);

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

    @RequestMapping({"/uncoverlottery"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer uncoverlottery(String session, int lotteryid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(lotteryid);

                if (giftActivity == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (giftActivity.getStatus() != GiftActivity.GIFT_STATUS_START) {
                    // todo check cheat
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);
                    return responseContainer;
                }


                int mode = giftActivity.getMode();

                if(mode == GiftActivity.GIFT_MODE_OWNER_OPEN)
                {
                    rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GIFT_STOP, "" + lotteryid);
                }



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




    @RequestMapping({"/participategift"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer participategift(String session, int giftid, String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(giftid);

                if (giftActivity == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (giftActivity.getStatus() != GiftActivity.GIFT_STATUS_START) {
                    // todo check cheat
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);
                    return responseContainer;
                }

                if (giftActivity.getCount() >= giftActivity.getNum()) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALL_SEND);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALL_SEND_STR);
                    return responseContainer;
                }

                GiftParticipate giftParticipate = giftParticipateRepository.findByUseridAndGiftid(customer.getId(), giftid);


                if (giftParticipate != null && giftParticipate.getStatus() == GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN_STR);
                    return responseContainer;
                }

                String params = giftActivity.getParms();


                int mode = giftActivity.getMode();
                LotteryParticipateMessage lotteryParticipateMessage = new LotteryParticipateMessage();

                int participate = giftActivity.getParticipate();

                redisUtils.hmIncrement(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PEOPLE, String.valueOf(giftid), 1);

                if (customer.getHeadpic() != null && !customer.getHeadpic().isEmpty()) {

                    int size = redisUtils.lSize(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid);

                    if (size > 100) {
                        redisUtils.rightPop(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid);

                    }
                }
                redisUtils.lPush(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid, customer.getHeadpic());
                giftActivity.setParticipate(participate + 1);


                if (mode == GiftActivity.GIFT_MODE_TIME_DUE || mode == GiftActivity.GIFT_MODE_OWNER_OPEN ) {

                    if (giftParticipate == null) {
                        giftParticipate = new GiftParticipate();
                    }


                    giftParticipate.setFormid(formid);
                    giftParticipate.setGiftid(giftid);
                    giftParticipate.setUserid(userid);
                    giftParticipate.setResult(0);
                    giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);

                    giftParticipate.setGiftname(giftActivity.getGift_name());
                    giftParticipate.setParticipatetime(new Date());

                    giftParticipateRepository.save(giftParticipate);

                    lotteryParticipateMessage.setResult(0);
                    lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_TIME_DUE);

                }
                else if (mode == GiftActivity.GIFT_MODE_PEOPLE_COUNT ) {
                    if (giftParticipate == null) {
                        giftParticipate = new GiftParticipate();
                    }
                    giftParticipate.setFormid(formid);
                    giftParticipate.setGiftid(giftid);
                    giftParticipate.setUserid(userid);
                    giftParticipate.setResult(0);
                    giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                    giftParticipate.setGiftname(giftActivity.getGift_name());
                    giftParticipate.setParticipatetime(new Date());
                    giftParticipateRepository.save(giftParticipate);

                    lotteryParticipateMessage.setResult(0);
                    lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_PEOPLE_COUNT);
                    JsonObject jsonObject = new JsonParser().parse(params).getAsJsonObject();


                    int peoplecount = jsonObject.get("num").getAsInt();




                    if(participate+1 >= peoplecount)
                    {
                        rabbitTemplate.convertAndSend(MQConstant.DEFAULT_EXCHANGE, MQConstant.SCHEDULE_GIFT_STOP, "" + giftid);
                    }





                }
                else if (mode == GiftActivity.GIFT_MODE_ASSIST ) {
                    //用户点击参与按钮
                    if (giftParticipate == null) {
                        giftParticipate = new GiftParticipate();
                        giftParticipate.setResult(0);
                    }
                    giftParticipate.setFormid(formid);
                    giftParticipate.setGiftid(giftid);
                    giftParticipate.setUserid(userid);

                    giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                    giftParticipate.setGiftname(giftActivity.getGift_name());
                    giftParticipate.setParticipatetime(new Date());
                    giftParticipateRepository.save(giftParticipate);

                    lotteryParticipateMessage.setResult(0);
                    lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_PEOPLE_COUNT);


                }
                else if (mode == GiftActivity.GIFT_MODE_RANDOM) {
                    JsonObject jsonObject = new JsonParser().parse(params).getAsJsonObject();


                    int chance = jsonObject.get("chance").getAsInt();

                    int solution = jsonObject.get("solution").getAsInt();


                    Random random = new Random();

                    int mynum = random.nextInt(chance);

                    if (mynum == solution) {
                        //中奖了
                        if (giftParticipate == null) {
                            giftParticipate = new GiftParticipate();
                        }

                        giftParticipate.setFormid(formid);
                        giftParticipate.setGiftid(giftid);
                        giftParticipate.setUserid(userid);
                        giftParticipate.setResult(GiftParticipate.PARTICIPATE_RESULT_SUCCESS);
                        giftParticipate.setMynumber(mynum);
                        giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                        giftParticipate.setGiftname(giftActivity.getGift_name());
                        giftParticipate.setParticipatetime(new Date());
                        giftParticipateRepository.save(giftParticipate);

                        lotteryParticipateMessage.setResult(1);
                        lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_RANDOM);


                        giftActivity.setCount(giftActivity.getCount() + 1);

                        if (giftActivity.getCount() >= giftActivity.getNum()) {
                            giftActivity.setStatus(GiftActivity.GIFT_STATUS_END);
                        }

                        GiftWinner giftWinner = new GiftWinner();

                        giftWinner.setGiftid(giftid);
                        giftWinner.setUserid(userid);
                        giftWinner.setGiftname(giftActivity.getGift_name());
                        giftWinner.setWintime(new Date());

                        giftWinnerRepository.save(giftWinner);

                        String result = giftActivity.getResult();

                        JsonArray jsonArray = null;

                        if (result != null && !result.isEmpty()) {
                            jsonArray = new JsonParser().parse(result).getAsJsonArray();
                        } else {
                            jsonArray = new JsonArray();
                        }

                        JsonObject jsonResult = new JsonObject();
                        jsonResult.addProperty("uid", customer.getId());
                        jsonResult.addProperty("name", customer.getNickname());
                        jsonResult.addProperty("pic", customer.getHeadpic());
                        if(giftActivity.getGiftfrom() == 0)
                        {
                            Order order = new Order();
                            StringBuilder sb = new StringBuilder("JW");
                            sb.append(customer.getId());

                            sb.append(new Date().getTime());
                            sb.append("-");
                            sb.append(0);
                            sb.append(giftActivity.getGoodsCover().getId());

                            order.setOuttradeno(sb.toString());
                            order.setBuyerid(0);
                            order.setShopId(giftActivity.getOwnerid());

                            order.setUsername(customer.getNickname());

                            order.setOrderStatus(Order.ORDER_STATUS_TOPAY);
                            order.setPayStatus(Order.ORDER_PAY_STATUS_NORMAL);
                            order.setProfitStatus(Order.ORDER_PROFIT_STATUS_NORMAL);


                            order.setOrdertype(Order.ORDER_TYPE_LOTTERY);
                            order.setCreatetime(new Date());

                            String token = Md5Util.getMD5(new Date().toString() + customer.getId());

                            order.setGiven_token(token);

                            orderService.Create(order);


                            OrderGoods orderGoods = new OrderGoods();
                            orderGoods.setId(order.getId());
                            orderGoods.setGoodId(giftActivity.getGoodsCover().getId());
                            orderGoods.setGoodtitle(giftActivity.getGoodsCover().getIntroduction());

                            orderGoods.setGoodsname(giftActivity.getGoodsCover().getGoods_name());
                            orderGoods.setGoodposter(giftActivity.getGoodsCover().getPictureurl());


                            orderGoods.setBuysum(1);
                            orderGoods.setGroup_number(1);
                            orderGoods.setGoods_price(giftActivity.getGoodsCover().getGroup_price());

                            orderGoods.setSkuid(0);
                            orderGoods.setSkuname("");

                            orderGoodsRepository.save(orderGoods);

                            Shop shop = shopRepository.findById(giftActivity.getOwnerid());


                            if (shop != null) {
                                OrderShop orderShop = new OrderShop();

                                orderShop.setShopId(shop.getId());
                                orderShop.setShopcontact(shop.getShopcontact());
                                orderShop.setId(order.getId());

                                orderShopRepository.save(orderShop);
                            }


                            OrderExtra orderExtra = new OrderExtra();
                            orderExtra.setBlessing_message("大吉大利，恭喜发财");
                            orderExtra.setBuyer_message("");
                            orderExtra.setId(order.getId());


                            if (shop != null) {
                                orderExtra.setSendnickname(shop.getShopname());
                                orderExtra.setSendheadpic(shop.getShoplogo());
                            } else {
                                orderExtra.setSendnickname("田趣小集");
                                orderExtra.setSendheadpic("");
                            }


                            orderExtraRepository.save(orderExtra);

                            jsonResult.addProperty("oid", order.getId());
                            jsonResult.addProperty("token", order.getGiven_token());
                        }
                        else
                        {
                            jsonResult.addProperty("oid","0");
                            jsonResult.addProperty("token","0");
                        }

                        jsonArray.add(jsonResult);

                        result = jsonArray.toString();

                        giftActivity.setResult(result);

                        List<Object> items = new ArrayList<>();
                        items.add(String.valueOf(giftid));
                        redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                        redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS, items.toArray());
                    } else {
                        if (giftParticipate == null) {
                            giftParticipate = new GiftParticipate();
                        }

                        giftParticipate.setFormid(formid);
                        giftParticipate.setGiftid(giftid);
                        giftParticipate.setUserid(userid);
                        giftParticipate.setResult(0);
                        giftParticipate.setMynumber(mynum);
                        giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                        lotteryParticipateMessage.setResult(1);
                        lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_RANDOM);
                        giftParticipate.setGiftname(giftActivity.getGift_name());
                        giftParticipate.setParticipatetime(new Date());
                        giftParticipateRepository.save(giftParticipate);
                    }

                }
                responseContainer.setMessage(lotteryParticipateMessage);



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


    @RequestMapping({"/giftrelay"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseContainer giftrelay(String session, int giftid, int relayuserid,String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);
            Customer customerrelay = customerService.GetById(relayuserid);
            if (customer != null && customerrelay!=null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(giftid);

                if (giftActivity == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (giftActivity.getStatus() != GiftActivity.GIFT_STATUS_START) {
                    // todo check cheat
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);
                    return responseContainer;
                }

                if (giftActivity.getCount() >= giftActivity.getNum()) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALL_SEND);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALL_SEND_STR);
                    return responseContainer;
                }

                GiftParticipate giftParticipate = giftParticipateRepository.findByUseridAndGiftid(customer.getId(), giftid);


                GiftParticipate giftParticipaterelay = giftParticipateRepository.findByUseridAndGiftid(relayuserid, giftid);


                if (giftParticipate != null  && giftParticipate.getRelaystatus() == GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN_STR);
                    return responseContainer;
                }

                if (giftParticipaterelay == null  || giftParticipaterelay.getResult() !=0) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_RELAY_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_RELAY_STATUS_ERR_STR);
                    return responseContainer;
                }



                String params = giftActivity.getParms();


                int mode = giftActivity.getMode();
                LotteryParticipateMessage lotteryParticipateMessage = new LotteryParticipateMessage();




                 if (mode == GiftActivity.GIFT_MODE_ASSIST) {

                     if (giftParticipate == null) {
                         giftParticipate = new GiftParticipate();
                         giftParticipate.setResult(0);
                         giftParticipate.setGiftid(giftid);
                         giftParticipate.setUserid(userid);
                         giftParticipate.setFormid(formid);
                         giftParticipate.setStatus(0);

                     }



                     giftParticipate.setRelayuserid(relayuserid);
                     giftParticipate.setRelayname(customerrelay.getNickname());
                     giftParticipate.setRelaystatus(GiftParticipate.PARTICIPATE_ACTION_ALREADY_IN);
                     giftParticipate.setGiftname(giftActivity.getGift_name());
                     giftParticipate.setParticipatetime(new Date());

                     JsonObject jsonObject = new JsonParser().parse(params).getAsJsonObject();
                     int chance = jsonObject.get("chance").getAsInt();

                     int solution = jsonObject.get("solution").getAsInt();


                     Random random = new Random();

                     int mynum = random.nextInt(chance);

                     giftParticipate.setMynumber(mynum);
                     giftParticipateRepository.save(giftParticipate);





                    if (mynum == solution) {
                        //中奖了

                        giftParticipaterelay.setResult(GiftParticipate.PARTICIPATE_RESULT_SUCCESS);
                        giftParticipaterelay.setRelaycount(giftParticipaterelay.getRelaycount() + 1);

                        giftParticipaterelay.setGiftname(giftActivity.getGift_name());
                        giftParticipaterelay.setParticipatetime(new Date());

                        giftParticipateRepository.save(giftParticipaterelay);



                        lotteryParticipateMessage.setResult(1);
                        lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_ASSIST);


                        giftActivity.setCount(giftActivity.getCount() + 1);

                        if (giftActivity.getCount() >= giftActivity.getNum()) {
                            giftActivity.setStatus(GiftActivity.GIFT_STATUS_END);
                        }

                        GiftWinner giftWinner = new GiftWinner();

                        giftWinner.setGiftid(giftid);
                        giftWinner.setUserid(relayuserid);
                        giftWinner.setGiftname(giftActivity.getGift_name());
                        giftWinner.setWintime(new Date());

                        giftWinnerRepository.save(giftWinner);

                        String result = giftActivity.getResult();

                        JsonArray jsonArray = null;

                        if (result != null && !result.isEmpty()) {
                            jsonArray = new JsonParser().parse(result).getAsJsonArray();
                        } else {
                            jsonArray = new JsonArray();
                        }

                        JsonObject jsonResult = new JsonObject();
                        jsonResult.addProperty("uid", customerrelay.getId());
                        jsonResult.addProperty("name", customerrelay.getNickname());
                        jsonResult.addProperty("pic", customerrelay.getHeadpic());
                        if(giftActivity.getGiftfrom() == 0)
                        {
                            Order order = new Order();
                            StringBuilder sb = new StringBuilder("JW");
                            sb.append(customer.getId());

                            sb.append(new Date().getTime());
                            sb.append("-");
                            sb.append(0);
                            sb.append(giftActivity.getGoodsCover().getId());

                            order.setOuttradeno(sb.toString());
                            order.setBuyerid(0);
                            order.setShopId(giftActivity.getOwnerid());

                            order.setUsername(customer.getNickname());

                            order.setOrderStatus(Order.ORDER_STATUS_TOPAY);
                            order.setPayStatus(Order.ORDER_PAY_STATUS_NORMAL);
                            order.setProfitStatus(Order.ORDER_PROFIT_STATUS_NORMAL);


                            order.setOrdertype(Order.ORDER_TYPE_LOTTERY);
                            order.setCreatetime(new Date());

                            String token = Md5Util.getMD5(new Date().toString() + customer.getId());

                            order.setGiven_token(token);

                            orderService.Create(order);


                            OrderGoods orderGoods = new OrderGoods();
                            orderGoods.setId(order.getId());
                            orderGoods.setGoodId(giftActivity.getGoodsCover().getId());
                            orderGoods.setGoodtitle(giftActivity.getGoodsCover().getIntroduction());

                            orderGoods.setGoodsname(giftActivity.getGoodsCover().getGoods_name());
                            orderGoods.setGoodposter(giftActivity.getGoodsCover().getPictureurl());


                            orderGoods.setBuysum(1);
                            orderGoods.setGroup_number(1);
                            orderGoods.setGoods_price(giftActivity.getGoodsCover().getGroup_price());

                            orderGoods.setSkuid(0);
                            orderGoods.setSkuname("");

                            orderGoodsRepository.save(orderGoods);

                            Shop shop = shopRepository.findById(giftActivity.getOwnerid());


                            if (shop != null) {
                                OrderShop orderShop = new OrderShop();

                                orderShop.setShopId(shop.getId());
                                orderShop.setShopcontact(shop.getShopcontact());
                                orderShop.setId(order.getId());

                                orderShopRepository.save(orderShop);
                            }


                            OrderExtra orderExtra = new OrderExtra();
                            orderExtra.setBlessing_message("大吉大利，恭喜发财");
                            orderExtra.setBuyer_message("");
                            orderExtra.setId(order.getId());


                            if (shop != null) {
                                orderExtra.setSendnickname(shop.getShopname());
                                orderExtra.setSendheadpic(shop.getShoplogo());
                            } else {
                                orderExtra.setSendnickname("田趣小集");
                                orderExtra.setSendheadpic("");
                            }


                            orderExtraRepository.save(orderExtra);

                            jsonResult.addProperty("oid", order.getId());
                            jsonResult.addProperty("token", order.getGiven_token());
                        }
                        else
                        {
                            jsonResult.addProperty("oid","0");
                            jsonResult.addProperty("token","0");
                        }

                        jsonArray.add(jsonResult);

                        result = jsonArray.toString();

                        giftActivity.setResult(result);

                        String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                        weixinTemplate.sendLotteryResultNotify(access_token,giftActivity.getGift_name(),customerrelay,Integer.valueOf(giftid),giftParticipaterelay.getFormid(),true);

                        List<Object> items = new ArrayList<>();
                        items.add(String.valueOf(giftid));
                        redisUtils.remove(RedisKeyConstant.VALUE_GET_PLATFORM_LOTTERY);
                        redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_LOTTERY_DETAILS, items.toArray());
                    } else {


                        if(giftParticipaterelay == null)
                        {
                            giftParticipaterelay = new GiftParticipate();
                            giftParticipaterelay.setStatus(0);
                            giftParticipaterelay.setGiftid(giftid);
                            giftParticipaterelay.setUserid(relayuserid);
                            giftParticipaterelay.setRelaycount(0);
                        }


                        giftParticipaterelay.setRelaycount(giftParticipaterelay.getRelaycount() + 1);
                        giftParticipaterelay.setGiftname(giftActivity.getGift_name());

                        giftParticipateRepository.save(giftParticipaterelay);

                        lotteryParticipateMessage.setResult(0);
                        lotteryParticipateMessage.setType(GiftActivity.GIFT_MODE_ASSIST);
                    }

                }
                responseContainer.setMessage(lotteryParticipateMessage);



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


    @RequestMapping({"/appointment"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    ResponseContainer appointment(String session, int giftid, String formid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                GiftActivity giftActivity = giftActivityRepository.findById(giftid);

                if (giftActivity == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                    return responseContainer;
                }

                if (giftActivity.getStatus() != GiftActivity.GIFT_STATUS_CAN_SHOW) {
                    // todo check cheat
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);
                    return responseContainer;
                }

                if (giftActivity.getCount() >= giftActivity.getNum()) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALL_SEND);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALL_SEND_STR);
                    return responseContainer;
                }

                GiftParticipate giftParticipate = giftParticipateRepository.findByUseridAndGiftid(customer.getId(), giftid);


                if (giftParticipate != null) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_ALREADY_IN_STR);

                    return responseContainer;
                }


                if (giftParticipate == null) {
                    giftParticipate = new GiftParticipate();
                }

                giftParticipate.setFormid(formid);
                giftParticipate.setGiftid(giftid);
                giftParticipate.setUserid(userid);
                giftParticipate.setResult(0);
                giftParticipate.setStatus(GiftParticipate.PARTICIPATE_ACTION_APPOINTMENT);

                giftParticipate.setGiftname(giftActivity.getGift_name());
                giftParticipate.setParticipatetime(new Date());


                redisUtils.hmIncrement(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PEOPLE, String.valueOf(giftid), 1);

                if (customer.getHeadpic() != null && !customer.getHeadpic().isEmpty()) {
                    int size = redisUtils.lSize(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid);
                    if (size > 100) {
                        redisUtils.rightPop(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid);

                    }
                    redisUtils.lPush(RedisKeyConstant.VALUE_GIFT_ACTIVITY_PERSONAS_PREX + giftid, customer.getHeadpic());
                }


                giftParticipateRepository.save(giftParticipate);

                giftActivity.setAppointment(giftActivity.getAppointment() + 1);
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


    @RequestMapping({"/bindprizeaddr"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    ResponseContainer bindprizeaddr(String session, int orderid, int address_id) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order == null) {
                    responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                    responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    return responseContainer;
                }

                if (true) {
                    if (order.getGivenstatus() == Order.ORDER_GIVEN_STATUS_NORMAL) {

                        order.setGivenstatus(Order.ORDER_GIVEN_STATUS_DONE);
                        order.setOrderStatus(Order.ORDER_STATUS_TOSHIP);

                        order.setGiverid(userid);

                        CustomerShipAddr customerShipAddr = customerShipAddrService.GetByid(address_id);

                        if (customerShipAddr != null) {
                            OrderAddr orderAddr = new OrderAddr();
                            orderAddr.setId(order.getId());
                            orderAddr.setReceiverMobile(customerShipAddr.getMobile());
                            orderAddr.setReceiverName(customerShipAddr.getConsigner());
                            orderAddr.setReceiverAddress(customerShipAddr.getAddress());
                            orderAddr.setReceiverCity(customerShipAddr.getCity());
                            orderAddr.setReceiverDistrict(customerShipAddr.getDistrict());
                            orderAddr.setReceiverProvince(customerShipAddr.getProvince());
                            orderAddr.setProvincename(customerShipAddr.getProvincename());

                            orderAddr.setCityname(customerShipAddr.getCityname());
                            orderAddr.setDistrictname(customerShipAddr.getDistrictname());

                            orderAddrRepository.save(orderAddr);


                            Goods goods = goodsService.getById(order.getOrderGoods().getGoodId());
                            int shipfee = shipFeeUtils.getShipFee(goods, customerShipAddr.getProvince());

                            order.setShipping_money(shipfee);


                            OrderExtra orderExtra = orderExtraRepository.findById(orderid);

                            orderExtra.setRecievenickname(customer.getNickname());
                            orderExtra.setRecieveheadpic(customer.getHeadpic());

                            orderExtraRepository.save(orderExtra);

                        }

                        orderService.UpdateOrder(order);


                    } else if (order.getGivenstatus() == Order.ORDER_GIVEN_STATUS_DONE) {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_BIND_FAIL_STR);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_OVER_FAIL);
                        responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_OVER_FAIL_STR);
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/changeblessmessage"})
    @ResponseBody
    ResponseContainer changeblessmessage(String session, int orderid, String message) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                OrderExtra orderExtra = orderExtraRepository.findById((long) orderid);

                if (orderExtra != null) {
                    orderExtra.setBlessing_message(message);
                    orderExtraRepository.save(orderExtra);
                } else {
                    orderExtra = new OrderExtra();
                    orderExtra.setId(orderid);
                    orderExtra.setBlessing_message(message);
                    orderExtra.setBuyer_message("");
                    orderExtraRepository.save(orderExtra);
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


}
