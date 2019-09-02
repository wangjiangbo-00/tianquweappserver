package com.ziyoushenghuo.settlement;


import com.ziyoushenghuo.entry.*;


import com.ziyoushenghuo.rabbitmq.order.CheckAfterDelivery;
import com.ziyoushenghuo.repository.OrderRepository;

import com.ziyoushenghuo.repository.ShopAccountRepository;
import com.ziyoushenghuo.repository.ShopProfitRepository;
import com.ziyoushenghuo.service.CustomerAccountService;
import com.ziyoushenghuo.service.CustomerService;
import com.ziyoushenghuo.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class SettleService {

    @Autowired
    private CustomerSettle customerSettle;

    @Autowired
    private ShopSettle shopSettle;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private RefundService refundService;


    @Autowired
    private OrderRepository orderRepository;

    private static Logger logger = LoggerFactory.getLogger(SettleService.class);
    /**
     * 获取待验证用户信息
     */

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public void onOrderProfitSettle(Order order)
    {

        if(order.getProfitStatus() == Order.ORDER_PROFIT_STATUS_NORMAL)
        {

            if(order.getOrdertype() != Order.ORDER_TYPE_LOTTERY)
            {
                try
                {
                    double ordermoney = order.getOrdermoney();
                    double shipmoney = order.getShipping_money();

                    double shoprate = 0;
                    double realmoney = 0;


                    realmoney = ordermoney;
                    boolean needcustomerprofit = false;

                    int recommender = 0;
                    int benefiter = 0;
                    Customer customer = null;
                    if(order.getIsfree() == 1)
                    {
                        shoprate = SettleConfig.FREE_ORDER_RETRUN_RATE;


                    }
                    else
                    {
                        shoprate = 1 - SettleConfig.WEIXIN_PAY_RATE - SettleConfig.PLATFORM_SERVICE_RATE;

                        if(order.getGroupbuy() != 2 && order.getRefundProcessStatus() == 0)
                        {
                            customer = customerService.GetById(order.getBuyerid());

                            if(customer!=null)
                            {
                                if(customer.getIsrecommender() == 1)
                                {
                                    //本身就是推荐者 自己获取收益，sharefrom 无效
                                    benefiter = customer.getId();
                                    shoprate -= SettleConfig.CUSTOMER_SHARE_RATE;
                                    needcustomerprofit = true;
                                }
                                else
                                {
                                    if(order.getSharefrom() != 0)
                                    {
                                        benefiter = order.getSharefrom();
                                        shoprate -= SettleConfig.CUSTOMER_SHARE_RATE;
                                        needcustomerprofit = true;
                                    }

                                }

                                if(customer.getRecommender() != 0)
                                {
                                    shoprate -= SettleConfig.CUSTOMER_RECOMMENDER_RATE;
                                    needcustomerprofit = true;
                                    recommender = customer.getRecommender();
                                }

                            }
                        }






                    }


                    String settleinfo = "订单金额:" + order.getOrdermoney();


                    List<Refund> refunds = refundService.getByOrderId(order.getId().intValue());

                    for(Refund refund:refunds)
                    {
                        settleinfo += "- " + refund.getReason() + " :" + refund.getMoney();

                        realmoney -= refund.getMoney();

                    }

                    shopSettle.onOrderProfitSettle(order,shoprate,realmoney);

                    if(needcustomerprofit)
                    {
                        customerSettle.onOrderProfitSettle(order,recommender,benefiter,realmoney);
                    }


                    order.setProfitStatus(Order.ORDER_PROFIT_STATUS_DONE);


                    if(customer!=null)
                    {

                        CustomerAccount customerAccount = customerAccountService.GetByCid(customer.getId());

                        if(customerAccount!=null)
                        {
                            customerAccount.setOrdercount(customerAccount.getOrdercount()+1);
                            customerAccount.setConsume(customerAccount.getConsume()+realmoney);
                        }
                        else
                        {
                            customerAccount = new CustomerAccount();
                            customerAccount.setCid(customer.getId());
                            customerAccount.setMoney(0);
                            customerAccount.setProceeds(0);
                            customerAccount.setProfit(0);
                            customerAccount.setMoney_lock(0);
                            customerAccount.setConsume(realmoney);
                            customerAccount.setOrdercount(1);
                            customerAccount.setDayincome(0);
                            customerAccount.setWithdraw(0);
                        }
                        customerAccountService.UpdateOrCreate(customerAccount);
                    }

                    orderRepository.save(order);
                }
                catch (Exception e)
                {
                    logger.warn(e.getMessage());
                }

            }
            else
            {
                logger.info("lottery no need to settle");
            }



        }
    }
}
