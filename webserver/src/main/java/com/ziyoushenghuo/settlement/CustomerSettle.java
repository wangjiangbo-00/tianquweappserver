package com.ziyoushenghuo.settlement;


import com.sun.tools.classfile.Opcode;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;


import com.ziyoushenghuo.rabbitmq.order.CheckAfterDelivery;
import com.ziyoushenghuo.repository.*;

import com.ziyoushenghuo.service.CustomerService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/* 这个位置处理结算信息，包括推广员和店铺的结算
  todo 平台账目
  @author 王江波
  @version V1.0
*/
@Service

public class CustomerSettle {

    @Autowired
    private CustomerAccountRepository customerAccountRepository;


    @Autowired
    private CustomerAccountRecordRepository customerAccountRecordRepository;

    @Autowired
    private CustomerProfitRepository customerProfitRepository;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static Logger logger = LoggerFactory.getLogger(CustomerSettle.class);
    /**
     * 获取待验证用户信息
     */

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public boolean onOrderProfitSettle(Order order,int recommender,int sharefrom,double ordermoney)
    {
        boolean bsettle = false;

        try {
            if(recommender!=0)
            {
                CustomerAccount customerAccount= customerAccountRepository.findByCid(recommender);
                double realmoney = ordermoney* SettleConfig.CUSTOMER_RECOMMENDER_RATE;

                if(customerAccount!=null)
                {
                    double money_before = customerAccount.getMoney();
                    double money_after = customerAccount.getMoney()+realmoney;

                    double salesmoney_before = customerAccount.getProfit();
                    customerAccount.setProfit(customerAccount.getProfit()+ordermoney);
                    customerAccount.setMoney(money_after);
                    customerAccount.setProceeds(customerAccount.getProceeds()+realmoney);

                    CustomerAccountRecord customerAccountRecord  = customerAccountRecordRepository.findByRefidAndCidAndType(order.getId(),recommender,CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);

                    if(customerAccountRecord != null)
                    {
                        customerAccountRecord.setMoney(realmoney);
                        customerAccountRecord.setBalance(money_after);
                        customerAccountRecord.setType(CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);
                        customerAccountRecord.setCreatetime(new Date());
                        customerAccountRecord.setCid(recommender);
                        customerAccountRecord.setRefid(order.getId());
                        customerAccountRecord.setStatus(CustomerAccountRecord.OP_PROFIT_STATUS_OK);
                        customerAccountRecordRepository.save(customerAccountRecord);
                        customerAccountRepository.save(customerAccount);
                    }

                }
                else
                {

                }
            }

            if(sharefrom!=0)
            {
                CustomerAccount customerAccount= customerAccountRepository.findByCid(sharefrom);
                double realmoney = ordermoney* SettleConfig.CUSTOMER_SHARE_RATE;

                if(customerAccount!=null)
                {
                    double money_before = customerAccount.getMoney();
                    double money_after = customerAccount.getMoney()+realmoney;

                    double salesmoney_before = customerAccount.getProfit();
                    customerAccount.setProfit(customerAccount.getProfit()+ordermoney);
                    customerAccount.setMoney(money_after);
                    customerAccount.setProceeds(customerAccount.getProceeds()+realmoney);

                    CustomerAccountRecord customerAccountRecord  = customerAccountRecordRepository.findByRefidAndCidAndType(order.getId(),sharefrom,CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);

                    if(customerAccountRecord != null)
                    {
                        customerAccountRecord.setMoney(realmoney);
                        customerAccountRecord.setBalance(money_after);
                        customerAccountRecord.setType(CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);
                        customerAccountRecord.setCreatetime(new Date());
                        customerAccountRecord.setCid(sharefrom);
                        customerAccountRecord.setRefid(order.getId());

                        customerAccountRecord.setStatus(CustomerAccountRecord.OP_PROFIT_STATUS_OK);
                        customerAccountRecordRepository.save(customerAccountRecord);
                    }




                    customerAccountRepository.save(customerAccount);


                    bsettle =  true;

                }
                else
                {

                }
            }
        }catch (Exception e)
        {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

        return  bsettle;
    }



    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public boolean onOrderPreSettle(Order order)
    {
        boolean bsettle = false;


        double ordermoney = order.getOrdermoney();


        int buyid = order.getBuyerid();

        Customer customerbuyer = customerRepository.findById(buyid);

        int sharefrom = order.getSharefrom();
        int recommender = customerbuyer.getRecommender();

        try {
            if(recommender!=0)
            {
                CustomerAccount customerAccount= customerAccountRepository.findByCid(recommender);
                double realmoney = ordermoney* SettleConfig.CUSTOMER_RECOMMENDER_RATE;

                if(customerAccount!=null)
                {
                    double money_before = customerAccount.getMoney();
                    double money_after = customerAccount.getMoney()+realmoney;


                    CustomerAccountRecord customerAccountRecord  = customerAccountRecordRepository.findByRefidAndCidAndType(order.getId(),recommender,CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);

                    if(customerAccountRecord == null)
                    {

                        String desp =   customerbuyer.getNickname() + "购买" + order.getOrderGoods().getGoodtitle() + ",订单金额 " + order.getOrdermoney() + ",获得推荐人消费分成" + String.format("%1.2f", realmoney) + "元" ;


                        customerAccountRecord = new CustomerAccountRecord();
                        customerAccountRecord.setRemark(desp);
                        customerAccountRecord.setMoney(realmoney);
                        customerAccountRecord.setBalance(money_after); //这个位置不处理余额
                        customerAccountRecord.setType(CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);
                        customerAccountRecord.setCreatetime(new Date());
                        customerAccountRecord.setCid(recommender);
                        customerAccountRecord.setRefid(order.getId());
                        customerAccountRecord.setStatus(0);
                        customerAccountRecordRepository.save(customerAccountRecord);
                    }
                    else
                    {
                        logger.warn("pre profit discount record has exists");

                    }


                }

            }

            if(sharefrom!=0)
            {
                CustomerAccount customerAccount= customerAccountRepository.findByCid(sharefrom);
                double realmoney = ordermoney* SettleConfig.CUSTOMER_SHARE_RATE;

                if(customerAccount!=null)
                {
                    double money_before = customerAccount.getMoney();
                    double money_after = customerAccount.getMoney()+realmoney;



                    CustomerAccountRecord customerAccountRecord  = customerAccountRecordRepository.findByRefidAndCidAndType(order.getId(),sharefrom,CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);

                    if(customerAccountRecord == null) {

                        String desp = "";

                        if(sharefrom == customerbuyer.getId())
                        {
                            desp = customerbuyer.getNickname() +  "购买" + order.getOrderGoods().getGoodsname() + ",订单金额 " + order.getOrdermoney() + ",获得消费返现" + String.format("%1.2f", realmoney)  + "元" ;
                        }
                        else
                        {
                            desp = customerbuyer.getNickname() + "购买" + order.getOrderGoods().getGoodsname() + ",订单金额 " + order.getOrdermoney() + ",获得推广返利" + String.format("%1.2f", realmoney)  + "元" ;
                        }


                        customerAccountRecord = new CustomerAccountRecord();
                        customerAccountRecord.setRemark(desp);
                        customerAccountRecord.setMoney(realmoney);
                        customerAccountRecord.setBalance(money_after);
                        customerAccountRecord.setType(CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);
                        customerAccountRecord.setCreatetime(new Date());
                        customerAccountRecord.setCid(sharefrom);
                        customerAccountRecord.setRefid(order.getId());
                        customerAccountRecord.setStatus(0);
                        customerAccountRecordRepository.save(customerAccountRecord);
                    }
                    else
                    {
                        logger.warn("pre profit discount record has exists");
                    }

                    bsettle =  true;

                }

            }
        }catch (Exception e)
        {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

        return  bsettle;
    }



    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public boolean onOrderCancalSettle(Order order,int reason)
    {
        boolean bsettle = false;

        try {
        List<Integer> integers = new ArrayList<>();

        integers.add(CustomerAccountRecord.OP_TYPE_RECOMMEND_PROFIT);
        integers.add(CustomerAccountRecord.OP_TYPE_JUNIOR_CONSUME);

        List<CustomerAccountRecord>    customerAccountRecords  = customerAccountRecordRepository.findByRefidAndTypeIn(order.getId(),integers);

                    if(customerAccountRecords != null && !customerAccountRecords.isEmpty())
                    {


                        String cancalstr = "取消返利";

                        if(reason == Refund.REFUND_TYPE_GIVEUP_ORDER)
                        {
                            cancalstr = "用户替换货";
                        }
                        else if(reason == Refund.REFUND_TYPE_GIVEUP_ORDER)
                        {
                            cancalstr = "用户取消订单";
                        }
                        else if(reason == Refund.REFUND_TYPE_FREE_ORDER)
                        {
                            cancalstr = "用户免单，取消返利";
                        }
                        for(CustomerAccountRecord customerAccountRecord:customerAccountRecords) {

                            customerAccountRecord.setExtra(cancalstr);
                            //这个位置不处理余额

                            customerAccountRecord.setStatus(CustomerAccountRecord.OP_PROFIT_STATUS_NONEED);
                            customerAccountRecordRepository.save(customerAccountRecord);
                        }

                    }
                    else
                    {
                        logger.warn("pre profit discount record has exists");

                    }









        }catch (Exception e)
        {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

        return  bsettle;
    }
}
