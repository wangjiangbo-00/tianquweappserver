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
import com.ziyoushenghuo.common.ShipFeeUtils;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.*;

import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.*;
import com.ziyoushenghuo.service.*;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
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
public class WxCustomerOrderController {

    private final int SESSION_TIME = 30 * 365;


    private final String WEI_APPID = "wxdd41e6a63c02c5f0";
    private final String WEI_APPKEY = "31b051b9b9a347a3906502bf27f3bd2c";

    private final String SESSION_SPLIT = "__";
    private static Logger logger = LoggerFactory.getLogger(WxCustomerOrderController.class);


    @Autowired
    private OrderService orderService;


    @Autowired
    private FreeOrderRepository freeOrderRepository;


    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    @Autowired
    private CustomerService customerService;


    @Autowired
    private ShopSendBackAddrService shopSendBackAddrService;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private ShipFeeUtils shipFeeUtils;



    @Autowired
    private WeixinPayResultService weixinPayResultService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    private CustomerWithdrewService customerWithdrewService;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TeamFounderService teamFounderService;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private CustomerAccountRecordService customerAccountRecordService;

    @Autowired
    private CustomerShipAddrService customerShipAddrService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private OrderExpressService orderExpressService;


    @Autowired
    private OrderAddrRepository orderAddrRepository;

    @Autowired
    private OrderShopRepository orderShopRepository;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private OrderExpressRepository orderExpressRepository;

    @Autowired
    private OrderExtraRepository orderExtraRepository;

    @Autowired
    private DelayUnGroupSender delayUnGroupSender;

    @Autowired
    private PromotionDiscountGoodsRepository promotionDiscountGoodsRepository;


    @Autowired
    private DelayUnPaySender delayUnPaySender;


    @Autowired
    private DelayAfterReceiveSender afterReceiveSender;

    @Autowired
    private DelayRefundGiftShipFee delayRefundGiftShipFee;

    @RequestMapping({"/getgoodsgroups"})
    @ResponseBody
    ResponseContainer getgoodsgroups(String session, int goodsid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            List<TeamFounder> teamFounders = teamFounderService.GetByGoodsid(goodsid);
            if (teamFounders != null) {
                responseContainer.setLists(teamFounders);
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


    @RequestMapping({"/getcustomergroups"})
    @ResponseBody
    ResponseContainer getcustomergroups(String session) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                List<Order> orders = orderService.getUserAllGroupOrders(userid, 0, 10000);

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


    @RequestMapping({"/getgroupdetails"})
    @ResponseBody
    ResponseContainer getgroupdetails(String session, int groupid) {

        ResponseContainer responseContainer = new ResponseContainer();


        try {
            GroupDetailsMessage message = new GroupDetailsMessage();
            TeamFounder teamFounder = teamFounderService.GetById(groupid);
            if (teamFounder != null) {
                List<Order> orders = orderService.getByGroupId(groupid);


                Goods goods = goodsService.getById(teamFounder.getGoodsCover().getId());

                List<GroupDetailsMessage.Grouper> groupers = new ArrayList<GroupDetailsMessage.Grouper>();
                for (Order order : orders) {
                    if (order.getPayStatus() == Order.ORDER_PAY_STATUS_OK || order.getPayStatus() == Order.ORDER_PAY_STATUS_CONFORM) {
                        int userid = order.getBuyerid();

                        GroupDetailsMessage.Grouper grouper = new GroupDetailsMessage.Grouper();
                        Customer customer = customerService.GetById(userid);

                        if (customer != null) {
                            long jointime = order.getCreatetime().getTime();

                            grouper.setNickname(customer.getNickname());
                            grouper.setAvatar(customer.getHeadpic());
                            grouper.setUser_id(customer.getId());
                            grouper.setJoin_time(jointime);
                            groupers.add(grouper);
                        }


                    }

                }

                if (orders != null && !orders.isEmpty()) {
                    message.setOrder(orders.get(0));
                    message.setTeamFounder(teamFounder);

                    message.setUsers(groupers);
                    message.setGoods(goods);
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                }


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
            }
            responseContainer.setMessage(message);
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


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @RequestMapping({"/createorder"})
    @ResponseBody
    public ResponseContainer createorder(String session, int goods_id, int address_id, int groupbuy, int group_order_id,
                                         int expressfee, int is_given, int recommenderid, int buysum, int skuid, int goods_discount_id,String extra) {
        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);



            if (customer != null) {

                int shiptype = 0;


                if(extra!=null && !extra.isEmpty())
                {
                    JsonParser parser = new JsonParser();  //创建JSON解析器
                    JsonObject object = (JsonObject) parser.parse(extra);

                    if (object.has("shiptype")) {
                        shiptype = object.get("shiptype").getAsInt();

                    }
                }





                //保障 推广员 自己是自己的收益，避免前端问题
                if(customer.getIsrecommender() == 1)
                {
                    recommenderid = customer.getId();
                }
                int userid = customer.getId();
                Goods goods = goodsService.getById(goods_id);

                double discount = 1.0;
                boolean needdiscount = false;
                if (goods != null) {
                    if (goods_discount_id > 0) {
                        PromotionDiscountGoods promotionDiscountGoods = promotionDiscountGoodsRepository.findById(goods_discount_id);
                        if (promotionDiscountGoods != null) {
                            if (promotionDiscountGoods.getStatus() != PromotionDiscount.STATUS_START) {
                                responseContainer.setReturncode(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR);
                                responseContainer.setErrormsg(ResponeCodeConstant.ORDER_GIFT_STATUS_ERR_STR);

                                return responseContainer;
                            } else {
                                discount = promotionDiscountGoods.getDiscount() * 0.1; // 折扣计算时需要乘以0.1
                                needdiscount = true;
                            }
                        } else {
                            responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                            responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);

                            return responseContainer;
                        }


                    }

                    double goodprice = 0.0;

                    String skuname = "";

                    GoodsSku goodsSku = null;

                    if (skuid > 0) {
                        goodsSku = goodsSkuService.getById(skuid);

                        if (goodsSku != null) {
                            goodprice = groupbuy != 0 ? goodsSku.getGroup_price() : goodsSku.getPrice();
                            if (needdiscount) {
                                goodprice = goodprice * discount;
                            }


                            skuname = goodsSku.getSku_name();
                        } else {
                            responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                            responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);

                            return responseContainer;
                        }

                    } else {
                        goodprice = groupbuy != 0 ? goods.getGroup_price() : goods.getPrice();
                        if (needdiscount) {
                            goodprice = goodprice * discount;
                        }
                    }


                    Order order = new Order();

                    order.setDiscount_goods_id(goods_discount_id);
                    order.setDiscount(discount);
                    order.setOrder_price(goodprice);




                    order.setSharefrom(recommenderid);

                    CustomerShipAddr customerShipAddr = null;
                    if (is_given == 1) {
                        if (!shipFeeUtils.checkPreFee(goods, expressfee, buysum)) {
                            responseContainer.setReturncode(ResponeCodeConstant.ORDER_EXPRSS_FEE_FAIL);
                            responseContainer.setErrormsg(ResponeCodeConstant.ORDER_EXPRSS_FEE_FAIL_STR);
                            return responseContainer;
                        }
                        order.setOrdertype(Order.ORDER_TYPE_GIVER);
                        order.setGivenstatus(0);
                        order.setGiven_token(Md5Util.getMD5(new Date().toString() + userid));

                        order.setPreshippfee(expressfee);
                        order.setOrdermoney(goodprice * buysum + expressfee);
                    } else {

                        if(shiptype == 0)
                        {
                            customerShipAddr = customerShipAddrService.GetByid(address_id);
                            if (customerShipAddr == null || customerShipAddr.getUid() != userid) {
                                responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                                responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
                                return responseContainer;
                            }
                            if (!shipFeeUtils.checkExpressFee(goods, customerShipAddr, expressfee, buysum)) {
                                responseContainer.setReturncode(ResponeCodeConstant.ORDER_EXPRSS_FEE_FAIL);
                                responseContainer.setErrormsg(ResponeCodeConstant.ORDER_EXPRSS_FEE_FAIL_STR);
                                return responseContainer;
                            }


                            order.setOrdertype(Order.ORDER_TYPE_NORMAL);
                            order.setGiverid(0);
                            order.setShipping_money(expressfee);
                            order.setOrdermoney(goodprice * buysum + expressfee);
                        }
                        else
                        {

                            int shipfee = -goods.getSelfliftreturn();

                            order.setOrdertype(Order.ORDER_TYPE_NORMAL);
                            order.setGiverid(0);
                            order.setShipping_money(shipfee);
                            order.setOrdermoney(goodprice * buysum + shipfee);

                            order.setIsselflift(1);
                        }

                    }
                    StringBuilder sb = new StringBuilder("JW");
                    sb.append(userid);

                    sb.append(new Date().getTime());
                    sb.append("-");
                    sb.append(goods.getShop().getId());
                    sb.append(goods.getId());

                    order.setOuttradeno(sb.toString());

                    if (groupbuy != 0) {
                        order.setGroupbuy(groupbuy);
                        if (group_order_id != 0) {

                            TeamFounder teamFounder = teamFounderService.GetById(group_order_id);


                            if (teamFounder == null) {
                                responseContainer.setReturncode(ResponeCodeConstant.GROUP_NOT_FOUND);
                                responseContainer.setErrormsg(ResponeCodeConstant.GROUP_NOT_FOUND_STR);
                                return responseContainer;
                            }

                            if (teamFounder.getType() == TeamFounder.GROUP_TYPE_USER) {
                                if (teamFounder.getGroupresult() != TeamFounder.GROUP_STATUS_NORMAL) {
                                    responseContainer.setReturncode(ResponeCodeConstant.GROUP_NOT_VALID);
                                    responseContainer.setErrormsg(ResponeCodeConstant.GROUP_NOT_VALID_STR);
                                    return responseContainer;
                                }
                            } else {
                                if (teamFounder.getStatus() != TeamFounder.GROUP_STATUS_START) {
                                    responseContainer.setReturncode(ResponeCodeConstant.GROUP_NOT_VALID);
                                    responseContainer.setErrormsg(ResponeCodeConstant.GROUP_NOT_VALID_STR);
                                    return responseContainer;
                                }

                            }
                            //follower
                            order.setGroup_header(0);
                            order.setGroupOrderId(group_order_id);

                        } else {
                            order.setGroup_header(1);
                        }


                        order.setGroupOrderId(group_order_id);
                    } else {
                        order.setGroupbuy(0);

                        order.setGroupOrderId(0);
                    }


                    order.setBuyerid(userid);
                    order.setShopId(goods.getShop().getId());

                    order.setUsername(customer.getNickname());

                    order.setOrderStatus(Order.ORDER_STATUS_TOPAY);
                    order.setPayStatus(Order.ORDER_PAY_STATUS_NORMAL);
                    order.setProfitStatus(Order.ORDER_PROFIT_STATUS_NORMAL);


                    order.setCreatetime(new Date());

                    orderService.Create(order);

                    if (is_given == 0 && customerShipAddr != null) {
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
                    }

                    OrderGoods orderGoods = new OrderGoods();
                    orderGoods.setId(order.getId());
                    orderGoods.setGoodId(goods_id);
                    orderGoods.setGoodtitle(goods.getIntroduction());

                    orderGoods.setGoodsname(goods.getGoodsname());

                    if (goods.getPictureurl() != null) {
                        orderGoods.setGoodposter(goods.getPictureurl());
                    }

                    orderGoods.setBuysum(buysum);
                    orderGoods.setGroup_number(goods.getGroup_number());
                    orderGoods.setGoods_price(goodprice);

                    orderGoods.setSkuid(skuid);
                    orderGoods.setSkuname(skuname);

                    orderGoodsRepository.save(orderGoods);


                    OrderShop orderShop = new OrderShop();
                    orderShop.setShopId(goods.getShop().getId());
                    orderShop.setShopcontact(goods.getShop().getShopcontact());
                    orderShop.setId(order.getId());

                    orderShop.setShop_name(goods.getShop().getShopname());

                    orderShopRepository.save(orderShop);


                    OrderExtra orderExtra = new OrderExtra();
                    orderExtra.setBlessing_message("");
                    orderExtra.setBuyer_message("");
                    orderExtra.setId(order.getId());

                    orderExtra.setSendnickname(customer.getNickname());
                    orderExtra.setSendheadpic(customer.getHeadpic());

                    orderExtraRepository.save(orderExtra);

                    delayUnPaySender.send("" + order.getId());
                    responseContainer.setMessage(order);

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/getorders"})
    @ResponseBody
    ResponseContainer getUserOrders(String session, int orderstatus, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<Order> orders = new ArrayList<>();
                switch (orderstatus) {
                    case 0:
                        orders = orderService.getUserToPayOrders(userid, offset, size);
                        break;
                    case 1:
                        orders = orderService.getUserToShipOrders(userid, offset, size);
                        break;
                    case 2:
                        orders = orderService.getUserToReceiveOrders(userid, offset, size);
                        break;
                    case 3:
                        orders = orderService.getUserFinishOrders(userid, offset, size);
                        break;

                    case 4:
                        orders = orderService.getUserFinishOrders(userid, offset, size);
                        break;

                    case 5:
                        orders = orderService.getUserRefundOrders(userid, offset, size);
                        break;

                    case 7:
                        orders = orderService.getUserToGroupOrders(userid, offset, size);
                        break;

                    default:
                        orders = orderService.getUserAllOrders(userid, offset, size);
                        break;

                }

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


    @RequestMapping({"/getfreeorders"})
    @ResponseBody
    ResponseContainer getfreeorders(String session, int datastatus, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                List<FreeOrder> freeOrders = new ArrayList<>();

                switch (datastatus) {
                    case 0:
                    case -1:
                        String orderstr = (String) redisUtils.get(RedisKeyConstant.VALUE_GET_FREEORDERS);

                        if (orderstr != null) {
                            Type type = new TypeToken<List<FreeOrder>>() {
                            }.getType();
                            Gson gson = new Gson();

                            freeOrders = gson.fromJson(orderstr, type);

                        } else {
                            Sort sort = new Sort(Sort.Direction.DESC, "id");

                            Pageable pageable = new PageRequest(offset, size, sort);
                            Page<FreeOrder> freeOrderPage = freeOrderRepository.findAll(pageable);
                            if (freeOrderPage.hasContent()) {
                                freeOrders = freeOrderPage.getContent();

                                Gson gson = new Gson();
                                String jsonstr = gson.toJson(freeOrders);
                                redisUtils.set(RedisKeyConstant.VALUE_GET_FREEORDERS, jsonstr);
                            }


                        }
                        break;
                    case 1:
                        Sort sort = new Sort(Sort.Direction.DESC, "id");
                        freeOrders = freeOrderRepository.findByUserid(userid,sort);
                        break;


                }
                responseContainer.setLists(freeOrders);

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



    @RequestMapping({"/checkorderpay"})
    @ResponseBody
    ResponseContainer checkorderpay(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order != null) {
                    if (order.getBuyerid() == userid || order.getGiverid() == userid) {

                        if(order.getPayStatus() != Order.ORDER_PAY_STATUS_CONFORM)
                        {
                            WxPayUtils.Result  result =  WxPayUtils.queryorder(orderid);

                            if(result.getResult() ==  WxPayUtils.ORDER_RESULT_OK)
                            {
                                weixinPayResultService.OnOrderPaySuccess(order,result.getTransaction_id());
                                responseContainer.setExtramsg("ok");
                            }
                            else
                            {
                                responseContainer.setExtramsg("fail");
                            }
                        }

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/getorderrefunds"})
    @ResponseBody
    ResponseContainer getorderrefunds(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {


            List<Refund> refunds = new ArrayList<>();
            refunds =        refundRepository.findByOrderid((long)orderid);

            responseContainer.setLists(refunds);


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


    @RequestMapping({"/getorder"})
    @ResponseBody
    ResponseContainer getUserOrder(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order != null) {
                    if (order.getBuyerid() == userid || order.getGiverid() == userid) {
                        responseContainer.setMessage(order);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }
                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/getexpressinfo"})
    @ResponseBody
    ResponseContainer getexpressinfo(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                Order order = orderService.getOrderById(orderid);

                if (order.getBuyerid() == userid || order.getGiverid() == userid) {
                    OrderExpress orderExpress = orderExpressService.getByOrderId(orderid);

                    if (orderExpress != null) {
                        Date updatetime = orderExpress.getUpdatetime();

                        Date now = new Date();

                        boolean needupdate = false;

                        if(orderExpress.getShippingtype() == OrderExpress.ORDER_SHIP_TYPE_EXPRESS)
                        {
                            if (orderExpress.getMessage() == null && orderExpress.getErrmessage() == null) {
                                needupdate = true;
                            } else {


                                long skip = now.getTime() - updatetime.getTime();
                                if (now.getTime() - updatetime.getTime() > 6 * 60 * 60 * 1000) {
                                    needupdate = true;
                                }
                            }
                        }



                        JsonArray traces = null;
                        if (needupdate) {
                            String url = "http://www.kuaidi100.com/query?type=";
                            url += orderExpress.getCompanycode();
                            url += "&&postid=";
                            url += orderExpress.getExpressno();
                            url += "&&id=1";
                            url += "&&valicode=";
                            url += "&&temp=";
                            url += AliSmsUtils.getRandomIntStringByLength(4);
                            url += "&&sessionid=";
                            url += "&&tmp=";
                            url += AliSmsUtils.getRandomIntStringByLength(4);

                            if(orderExpress.getCompanyid() == 6)
                            {

                                OrderAddr orderAddr = orderAddrRepository.findById(orderExpress.getOrderid());

                                String phone = orderAddr.getReceiverMobile();
                                phone = phone.substring(phone.length()-4,phone.length());
                                url += "&&phone=";
                                url += phone;
                            }
                            ;
                            String result = WxPayUtils.httpRequest(url, "GET", "");

                            if (result != null) {
                                JsonObject returnData = new JsonParser().parse(result).getAsJsonObject();

                                String status = returnData.get("status").getAsString();
                                if (status.equals("200")) {
                                    traces = returnData.getAsJsonArray("data");

                                    String tracestr = traces.toString();

                                    orderExpress.setMessage(tracestr);
                                    orderExpress.setUpdatetime(new Date());
                                    orderExpressService.UpdateOrCreate(orderExpress);
                                } else {

                                    traces = returnData.getAsJsonArray("data");
                                    String errmsg = returnData.get("message").getAsString();
                                    logger.warn("query express from kuaidi100 fail with msg : " + result);
                                    orderExpress.setErrmessage(errmsg);
                                    orderExpress.setUpdatetime(new Date());
                                    orderExpressService.UpdateOrCreate(orderExpress);
                                }


                            } else {
                                responseContainer.setReturncode(ResponeCodeConstant.SESSION_NOTFOUND);
                                responseContainer.setErrormsg(ResponeCodeConstant.SESSION_NOTFOUND_STR);
                                return responseContainer;
                            }
                        } else {
                            if(orderExpress.getMessage()!=null)
                            {
                                traces = new JsonParser().parse(orderExpress.getMessage()).getAsJsonArray();
                            }
                            else
                            {
                                traces = null;
                            }

                        }
                        OrderExpressMessage expressMessage = new OrderExpressMessage();
                        expressMessage.setShipper(orderExpress.getExpresscompany());
                        expressMessage.setTracking_number(orderExpress.getExpressno());
                        List<OrderExpressMessage.ExpressTrace> expressTraces = new ArrayList<OrderExpressMessage.ExpressTrace>();
                        if (traces != null) {
                            for (JsonElement trace : traces) {
                                OrderExpressMessage.ExpressTrace expressTrace = new OrderExpressMessage.ExpressTrace();
                                String time = trace.getAsJsonObject().get("time").getAsString();
                                String context = trace.getAsJsonObject().get("context").getAsString();
                                expressTrace.setAddress("");
                                expressTrace.setRemark(context);
                                expressTrace.setTime(time);

                                expressTraces.add(expressTrace);
                            }

                        }

                        if (expressTraces.isEmpty()) {
                            OrderExpressMessage.ExpressTrace expressTrace = new OrderExpressMessage.ExpressTrace();
                            expressTrace.setAddress("");
                            expressTrace.setRemark("暂无物流信息");
                            expressTrace.setTime(new Date().toString());
                        }

                        expressMessage.setTraces(expressTraces);


                        expressMessage.setErrmsg(orderExpress.getErrmessage());
                        responseContainer.setMessage(expressMessage);

                    }

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

    @RequestMapping({"/cancelorder"})
    @ResponseBody
    ResponseContainer cancelorder(String session, int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                Order order = orderService.getOrderById(orderid);

                if (order.getBuyerid() == userid) {
                    orderService.DeleteAllInterrelated(order);

                    if (order.getGroupbuy() == 1) {
                        //todo 如果是团购订单做额外处理
                    }


                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/getordercount"})
    @ResponseBody
    ResponseContainer getordercount(String session) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();

                int unpay = orderService.getUnPayCount(userid);
                int ungroup = orderService.getUnGroupCount(userid);
                int unship = orderService.getUnShipCount(userid);
                int unreceive = orderService.getUnReceiveCount(userid);

                OrderCountMessage orderCountMessage = new OrderCountMessage();

                orderCountMessage.setUnpay(unpay);
                orderCountMessage.setUngroup(ungroup);
                orderCountMessage.setUnreceive(unreceive);
                orderCountMessage.setUnship(unship);
                responseContainer.setMessage(orderCountMessage);

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


    @RequestMapping({"/conformReceive"})
    @ResponseBody
    ResponseContainer conformReceive(String session, int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();


                Order order = orderService.getOrderById(orderid);
                if (order != null) {
                    if (order.getBuyerid() == userid || order.getGiverid() == userid) {

                        order.setOrderStatus(Order.ORDER_STATUS_TOCOMMENT);

                        order.setSign_time(new Date());
                        orderService.UpdateOrder(order);

                        String queuemsg = String.valueOf(order.getId());

                        afterReceiveSender.send(queuemsg);
                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/fixorderaddr"})
    @ResponseBody
    ResponseContainer fixorderaddr(String session, int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                Order order = orderService.getOrderById(orderid);
                if (order != null) {
                    if (order.getBuyerid() == userid) {

                        order.setFixaddr(1);
                        orderService.UpdateOrder(order);

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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


    @RequestMapping({"/addrefundflag"})
    @ResponseBody
    ResponseContainer addrefundflag(String session, int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            Customer customer = customerService.GetByToken(session);

            if (customer != null) {
                int userid = customer.getId();
                Order order = orderService.getOrderById(orderid);
                if (order != null) {
                    if (order.getBuyerid() == userid) {

                        order.setTryrefund(1);
                        orderService.UpdateOrder(order);

                    } else {
                        responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                        responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
                    }

                } else {
                    responseContainer.setReturncode(ResponeCodeConstant.MEG_NOT_FOUND);
                    responseContainer.setErrormsg(ResponeCodeConstant.MEG_NOT_FOUND_STR);
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
