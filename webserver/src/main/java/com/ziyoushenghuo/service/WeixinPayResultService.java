package com.ziyoushenghuo.service;


import com.ziyoushenghuo.asyntask.SuperGroupEndTask;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayCheckGiftOvertime;

import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.OrderExtraRepository;
import com.ziyoushenghuo.repository.ShopRepository;
import com.ziyoushenghuo.repository.WalletPayRepository;
import com.ziyoushenghuo.settlement.CustomerSettle;
import com.ziyoushenghuo.web.controller.WeixinPayController;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/* 这里处理微信支付成功后订单的处理逻辑，
  todo 将这种类型服务跟封装的jpa分开
  @author 王江波
  @version V1.0
*/
@Service
public class WeixinPayResultService {

    private static Logger logger = LoggerFactory.getLogger(WeixinPayResultService.class);

    @Autowired
    private WalletPayRepository walletPayRepository;


    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WalletPayService walletPayService;


    @Autowired
    private TeamFounderService teamFounderService;

    @Autowired
    private OwnerService ownerService;


    @Autowired
    private ShopRepository shopRepository;


    @Autowired
    private DelayCheckGiftOvertime delayCheckGiftStatus;


    @Autowired
    private GoodsService goodsService;


    @Autowired
    private RefundService refundService;


    @Autowired
    private ShopNotifyService shopNotifyService;


    @Autowired
    private DelayUnGroupSender delayUnGroupSender;

    @Autowired
    SuperGroupEndTask superGroupEndTask;


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OrderExtraRepository orderExtraRepository;


    @Autowired
    private DelayAfterDeliverySender delayAfterDeliverySender;

    @Autowired
    private CustomerSettle customerSettle;


    /**
     * 获取待验证用户信息
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean OnOrderPaySuccess(Order order, String transaction_id) {
        boolean bret = false;
        if (order != null) {

            if (order.getPayStatus() == Order.ORDER_PAY_STATUS_NORMAL
                    || order.getPayStatus() == Order.ORDER_PAY_STATUS_OK)

            {
                try {
                    order.setPayStatus(Order.ORDER_PAY_STATUS_CONFORM);
                    int groupbuy = order.getGroupbuy();
                    int isHeader = order.getGroup_header();
                    int groupid = order.getGroupOrderId();

                    Goods goods = goodsService.getById(order.getOrderGoods().getGoodId());

                    if(goods==null)
                    {
                        logger.warn("goods not exists");
                        return  false;

                    }

                    goods.setSell_count(goods.getSell_count() + 1);

                    if(goods.getStock() > 1)
                    {
                        goods.setStock(goods.getStock() -1);
                    }
                    else
                    {
                        goods.setStock(0);
                        List<Object> items = new ArrayList<>();
                        items.add(String.valueOf(goods.getId()));
                        redisUtils.hmDelete(RedisKeyConstant.VALUE_GET_GOODS_DETAILS_V1, items.toArray());
                    }


                    goodsService.UpdateOrCreate(goods);


                    Shop shop = shopRepository.findById(goods.getShop().getId());
                    if(shop!=null)
                    {
                        shop.setShopsales(shop.getShopsales()+1);

                        shopRepository.save(shop);
                    }
                    Customer customer = customerService.GetById(order.getBuyerid());

                    /*
                    if(order.getWalletpart()>0)
                    {

                        if(customer!=null)
                        {
                            double money_after = customer.getBalance()-order.getWalletpart()<0?0:customer.getBalance()-order.getWalletpart();

                            double money_pay = order.getWalletpart();

                            WalletPay walletPay = new WalletPay();

                            walletPay.setMoney_after(money_after);
                            walletPay.setType(WalletPay.WALLETPAY_TYPE_PAYBILL);
                            walletPay.setRefid(order.getId());
                            walletPay.setMoney(money_pay);
                            walletPay.setMoney_before(customer.getBalance());
                            walletPay.setUserid(order.getBuyerid());

                            try {
                                walletPay.setCreatetime(new Date());
                            }catch (Exception e)
                            {

                            }
                            walletPayService.createOrUpdate(walletPay);
                            customer.setBalance(money_after);
                            customerService.update(customer);
                        }
                    }
*/
                    if (groupbuy>0 ) {
                        if (isHeader == 1 && groupid == 0) {
                            // create group
                            TeamFounder teamFounder = new TeamFounder();
                            teamFounder.setOwnerid(order.getBuyerid());
                            teamFounder.setPeople(1);
                            teamFounder.setRequirenum(goods.getGroup_number());

                            GoodsCover goodsCover = new GoodsCover();
                            goodsCover.setId(order.getOrderGoods().getGoodId());
                            teamFounder.setGoodsCover(goodsCover);
                            teamFounder.setHeadpic(customer.getHeadpic());
                            teamFounder.setNickname(customer.getNickname());
                            teamFounder.setCreatetime(new Date());

                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.HOUR, 24);
                            teamFounder.setExpiretime(c.getTime());
                            teamFounder.setGroupresult(0);
                            teamFounder.setType(TeamFounder.GROUP_TYPE_USER);


                            teamFounderService.CreateOrUpdate(teamFounder);

                            int id = teamFounder.getId();


                            String queuemsg = "" + id;

                            delayUnGroupSender.send(queuemsg);

                            order.setGroupOrderId(teamFounder.getId());
                            order.setOrderStatus(Order.ORDER_STATUS_TOGROUP);

                        } else {
                            TeamFounder teamFounder = teamFounderService.GetById(groupid);
                            if (teamFounder != null) {

                                if (teamFounder.getType() == TeamFounder.GROUP_TYPE_USER) {
                                    if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_NORMAL) {

                                        int people = teamFounder.getPeople() + 1;
                                        teamFounder.setPeople(people);

                                        if (teamFounder.getType() == TeamFounder.GROUP_TYPE_PLATFORM) {
                                            redisUtils.hmSet(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE, String.valueOf(groupid), String.valueOf(people));

                                        }


                                        if (teamFounder.getPeople() >= teamFounder.getRequirenum()) {
                                            // group success
                                            teamFounder.setGroupresult(TeamFounder.GROUP_STATUS_OK);

                                            List<Order> orders = orderService.getByGroupId(groupid);
                                            for (Order order1 : orders) {
                                                if (order1.getPayStatus() != 0) {
                                                    if (order1.getOrdertype() ==Order.ORDER_TYPE_GIVER) {
                                                        order1.setOrderStatus(Order.ORDER_STATUS_TOGIFT);
                                                        order1.setGivenstatus(Order.ORDER_GIVEN_STATUS_NORMAL);
                                                        delayCheckGiftStatus.send("" + order1.getId());
                                                    } else {

                                                        if(order1.getIsselflift() == 0)
                                                        {
                                                            order1.setOrderStatus(Order.ORDER_STATUS_TOSHIP);
                                                        }
                                                        else
                                                        {
                                                            String queuemsg = String .valueOf(order1.getId()) ;

                                                            delayAfterDeliverySender.send(queuemsg);
                                                            order1.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);
                                                        }


                                                        customerSettle.onOrderPreSettle(order);

                                                        //todo pre customer settlement
                                                    }
                                                    orderService.UpdateOrder(order1);
                                                }

                                            }


                                        } else {
                                            order.setOrderStatus(Order.ORDER_STATUS_TOGROUP);
                                        }
                                        teamFounderService.CreateOrUpdate(teamFounder);
                                    } else if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_OK) {
                                        if (order.getOrdertype() == Order.ORDER_TYPE_GIVER) {
                                            order.setOrderStatus(Order.ORDER_STATUS_TOGIFT);
                                            order.setGivenstatus(Order.ORDER_GIVEN_STATUS_NORMAL);
                                            delayCheckGiftStatus.send("" + order.getId());
                                        } else {
                                            if(order.getIsselflift() == 0)
                                            {
                                                order.setOrderStatus(Order.ORDER_STATUS_TOSHIP);
                                            }
                                            else
                                            {
                                                String queuemsg = String .valueOf(order.getId()) ;

                                                delayAfterDeliverySender.send(queuemsg);
                                                order.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);
                                            }
                                            customerSettle.onOrderPreSettle(order);
                                            //todo pre customer settlement
                                        }


                                    } else if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_FAIL) {
                                        //避免出现在最后时间抢支付未成功的情况，直接退款 todo


                                    }
                                } else if (teamFounder.getType() == TeamFounder.GROUP_TYPE_PLATFORM) {
                                    //这里不进行退款，统一在结束时刻处理订单退款


                                    if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_NORMAL) {
                                        int people = teamFounder.getPeople() + 1;
                                        teamFounder.setPeople(people);

                                        if (teamFounder.getType() == TeamFounder.GROUP_TYPE_PLATFORM) {
                                            redisUtils.hmSet(RedisKeyConstant.VALUE_SUPERGROUP_PEOPLE, String.valueOf(groupid), String.valueOf(people));
                                        }
                                        if (teamFounder.getPeople() >= teamFounder.getRequirenum()) {
                                            // group success
                                            teamFounder.setGroupresult(TeamFounder.GROUP_STATUS_OK);

                                        }

                                        order.setOrderStatus(Order.ORDER_STATUS_TOGROUP);

                                        teamFounderService.CreateOrUpdate(teamFounder);

                                    } else if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_OK) {
                                        order.setOrderStatus(Order.ORDER_STATUS_TOGROUP);


                                    } else if (teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_FAIL) {
                                        //避免出现在最后时间抢支付未成功的情况，直接退款 todo


                                    }

                                }


                            }
                        }


                        order.setPaytime(new Date());
                        order.setTransaction_id(transaction_id);

                        orderService.UpdateOrder(order);
                    } else {

                        order.setPayStatus(Order.ORDER_PAY_STATUS_CONFORM);
                        order.setPaytime(new Date());
                        if (order.getOrdertype() == Order.ORDER_TYPE_GIVER) {
                            order.setOrderStatus(Order.ORDER_STATUS_TOGIFT);
                            order.setGivenstatus(Order.ORDER_GIVEN_STATUS_NORMAL);
                            delayCheckGiftStatus.send("" + order.getId());
                        } else {
                            if(order.getIsselflift() == 0)
                            {
                                order.setOrderStatus(Order.ORDER_STATUS_TOSHIP);
                            }
                            else
                            {
                                String queuemsg = String .valueOf(order.getId()) ;

                                delayAfterDeliverySender.send(queuemsg);
                                order.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);
                            }

                            customerSettle.onOrderPreSettle(order);
                            //todo pre customer settlement
                        }
                        order.setTransaction_id(transaction_id);

                        orderService.UpdateOrder(order);
                    }


                    bret = true;
                } catch (Exception e) {
                    //todo add exception log
                    logger.warn(e.getMessage().substring(0, 256));
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }

            }


        }
        return bret;
    }


}
