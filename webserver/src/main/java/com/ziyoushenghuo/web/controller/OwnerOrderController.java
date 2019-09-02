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
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.common.Md5Util;
import com.ziyoushenghuo.entry.*;

import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayCancalRefundSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundSettleSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.*;
import com.ziyoushenghuo.response.*;
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
public class OwnerOrderController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;

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
    private OrderAddrRepository orderAddrRepository;


    @Autowired
    private OrderExtraRepository orderExtraRepository;

    @Autowired
    private DelayAfterDeliverySender delayAfterDeliverySender;

    @Autowired
    private RefundProcessService refundProcessService;

    @Autowired
    DelayRefundSettleSender delayRefundSettleSender;


    @Autowired
    private DelayCancalRefundSender delayCancalRefundSender;


    @Autowired
    private RefundRepository refundRepository;


    @Autowired
    OwnerRepository ownerRepository;


    @Autowired
    CustomerShipAddrRepository customerShipAddrRepository;


    private static Logger logger = LoggerFactory.getLogger(OwnerOrderController.class);



    @RequestMapping({"/goodsdelivey"})
    @ResponseBody
    ResponseContainer goodsDelivey(String session, int orderid, int companyid, String shipcode) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {

            if (session != null) {
                Owner owner = ownerRepository.findByToken(session);
                if (owner!=null) {
                    ExpressCompany expressCompany = expressCompanyService.getById(companyid);
                    Order order = orderService.getOrderById(orderid);

                    int buyid = order.getBuyerid();


                    if (order != null && expressCompany != null) {
                        OrderExpress orderExpress = new OrderExpress();
                        orderExpress.setUpdatetime(new Date());
                        orderExpress.setCompanyid(companyid);
                        orderExpress.setCompanycode(expressCompany.getKdncode());
                        orderExpress.setExpresscompany(expressCompany.getCompany_name());

                        orderExpress.setExpressno(shipcode);
                        orderExpress.setOrderid(order.getId());
                        orderExpress.setShippingtime(new Date());
                        orderExpress.setUid(buyid);
                        orderExpress.setUsername(order.getUsername());

                        orderExpress.setShippingtype(expressCompany.getShiptype());

                        orderExpressService.UpdateOrCreate(orderExpress);

                        order.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);


                        orderService.UpdateOrder(order);


                        delayAfterDeliverySender.send(String.valueOf(order.getId()));
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
                int shopid = owner.getShopid();
                List<RefundProcess> refundProcessList = refundProcessService.getShopAll(shopid, offset, size);

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



    @RequestMapping({"/fixorderaddr"})
    @ResponseBody
    ResponseContainer fixorderaddr(String session, int orderid, int addrid) {


        ResponseContainer responseContainer = new ResponseContainer();

        try {

            if (session != null) {
                Owner owner = ownerRepository.findByToken(session);

                if (owner != null) {
                    CustomerShipAddr customerShipAddr = customerShipAddrRepository.findById(addrid);
                    Order order = orderService.getOrderById(orderid);

                    int buyid = order.getBuyerid();


                    if (order != null && customerShipAddr != null) {
                        order.setFixaddr(2);
                        OrderAddr orderAddr = orderAddrRepository.findById(orderid);
                        if(orderAddr!=null)
                        {

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

                            orderService.UpdateOrder(order);

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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }

    @RequestMapping({"/getorders"})
    @ResponseBody
    ResponseContainer getUserOrders(String session, int orderstatus, int offset, int size) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                List<Order> orders = new ArrayList<>();
                switch (orderstatus) {
                    case 0:
                        orders = null;
                        break;
                    case 1:
                        orders = orderService.getShopToShipOrders(owner.getShopid(), offset, size);
                        break;
                    case 2:
                        orders = orderService.getShopShipingOrders(owner.getShopid(), offset, size);
                        break;
                    case 3:
                        orders = orderService.getShopFinishOrders(owner.getShopid(), offset, size);
                        break;

                    case 4:

                        break;

                    default:
                        orders = orderService.getShopOrders(owner.getShopid(), offset, size);
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/getrefundorders"})
    @ResponseBody
    ResponseContainer getRefundOrders(String session, int orderstatus) {


        ResponseContainer responseContainer = new ResponseContainer();
        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                List<RefundProcess> refundProcesses = new ArrayList<>();
                switch (orderstatus) {
                    case 1:
                        refundProcesses = refundProcessService.getShopUnDeal(owner.getShopid());
                        break;


                    case 5:
                        refundProcesses = refundProcessService.getShopUnreceive(owner.getShopid());
                        break;

                    default:

                        break;

                }


                responseContainer.setLists(refundProcesses);
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

    @RequestMapping({"/agreeusercancalorder"})
    @ResponseBody
    ResponseContainer agreeusercancalorder(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Order order = orderService.getOrderById(orderid);
                if (order.getOrderShop().getShopId() == owner.getShopid()) {
                    logger.warn("deal with order user cancal order with orderid = " + order.getId());
                    if(order.getOrderStatus() == Order.ORDER_STATUS_TOSHIP || order.getOrderStatus() == Order.ORDER_STATUS_TOGIFT )
                    {

                        order.setTryrefund(2);
                        orderService.UpdateOrder(order);

                        String queuemsg = String .valueOf(order.getId()) ;

                        delayCancalRefundSender.send(queuemsg);
                    }
                    else
                    {
                        logger.warn("deal with order user cancal order with orderid =  " + order.getId() + "status not right = " + order.getOrderStatus());
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }



    @RequestMapping({"/refuseusercancalorder"})
    @ResponseBody
    ResponseContainer refuseusercancalorder(String session, int orderid,String refusemsg) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Order order = orderService.getOrderById(orderid);
                if (order.getOrderShop().getShopId() == owner.getShopid()) {
                    logger.warn("deal with order user cancal order with orderid = " + order.getId());
                    if(order.getOrderStatus() == Order.ORDER_STATUS_TOSHIP || order.getOrderStatus() == Order.ORDER_STATUS_TOGIFT )
                    {

                        order.setTryrefund(2);
                        orderService.UpdateOrder(order);

                        OrderExtra orderExtra = orderExtraRepository.findById(orderid);

                        if(orderExtra!=null)
                        {
                            orderExtra.setRefusemsg(refusemsg);
                            orderExtraRepository.save(orderExtra);
                        }


                    }
                    else
                    {
                        logger.warn("deal with order user cancal order with orderid =  " + order.getId() + "status not right = " + order.getOrderStatus());
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
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }



    @RequestMapping({"/getorderaddrs"})
    @ResponseBody
    ResponseContainer getUserOrderAddrs(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {


            Owner owner = ownerRepository.findByToken(session);

            if (owner != null) {
                Order order = orderService.getOrderById(orderid);
                if (order.getOrderShop().getShopId() == owner.getShopid()) {

                    int userid = order.getBuyerid();

                    if(order.getOrdertype() != Order.ORDER_TYPE_NORMAL)
                    {
                        userid = order.getGiverid();
                    }

                    List<CustomerShipAddr> customerShipAddrs = customerShipAddrRepository.findByUid(userid);

                    responseContainer.setLists(customerShipAddrs);
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
                    if (order.getOrderShop().getShopId() == owner.getShopid()) {
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




    @RequestMapping({"/closeorder"})
    @ResponseBody
    ResponseContainer closeorder(String session, int orderid) {

        ResponseContainer responseContainer = new ResponseContainer();

        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();

            String sessionvalue = operations.get(session);

            if (sessionvalue != null) {
                int ownerid = Integer.valueOf(sessionvalue);

                Order order = orderService.getOrderById(orderid);
                Owner owner = ownerService.getOwnerById(ownerid);

                if (order != null && order.getOrderShop().getShopId() == owner.getShopid()) {
                    if (order.getOrderStatus() == Order.ORDER_STATUS_TORECEIVE) {
                        //order.setShippingStatus(Order.ORDER_SHIP_STATUS_CLOSEAPPLY);
                        orderService.UpdateOrder(order);
                    }
                }
            }


        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }

        return responseContainer;
    }




    @RequestMapping({"/recevierefundgoods"})
    @ResponseBody
    ResponseContainer recevierefundgoods(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                refundProcess.setSendbackstatus(RefundProcess.REFUND_SENDBACK_RECEIVED);
                refundProcess.setStatus(RefundProcess.REFUND_PROCESS_STATUS_REFUNDING);

                refundProcessService.createOrUpdate(refundProcess);

                delayRefundSettleSender.send("" + orderid);
            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/acceptrefundapply"})
    @ResponseBody
    ResponseContainer acceptrefundapply(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                if (refundProcess.getApplymode() == RefundProcess.REFUND_PROCESS_TYPE_ONLYMONEY) {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_ACCEPT);
                    delayRefundSettleSender.send("" + orderid);
                    // start timer to refund
                } else if (refundProcess.getApplymode() == RefundProcess.REFUND_PROCESS_TYPE_NEEDSENDBACK) {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_RECEIVE);
                }


                refundProcessService.createOrUpdate(refundProcess);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
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


    @RequestMapping({"/receiverefundgoods"})
    @ResponseBody
    ResponseContainer receiverefundgoods(String session, int orderid) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                if (refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_RECEIVE) {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_STATUS_REFUNDING);
                    refundProcess.setSendbackstatus(RefundProcess.REFUND_SENDBACK_RECEIVED);
                    delayRefundSettleSender.send("" + orderid);

                }

                refundProcessService.createOrUpdate(refundProcess);


            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }


    @RequestMapping({"/refuserefundapply"})
    @ResponseBody
    ResponseContainer refuserefundapply(String session, int orderid, String reason) {


        ResponseContainer responseContainer = new ResponseContainer();


        try {

            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);

            if (refundProcess != null) {
                refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_REFUSE);
                refundProcess.setSellermsg(reason);

                refundProcessService.createOrUpdate(refundProcess);

            } else {
                responseContainer.setReturncode(ResponeCodeConstant.NOT_PERMIT_INFO);
                responseContainer.setErrormsg(ResponeCodeConstant.NOT_PERMIT_INFO_STR);
            }
        } catch (Exception e) {
            responseContainer.setReturncode(ResponeCodeConstant.EXCEPTION_OCCUR);
            responseContainer.setErrormsg(e.getMessage().substring(0, 30));
            logger.warn(e.getMessage());
        }


        return responseContainer;
    }

}