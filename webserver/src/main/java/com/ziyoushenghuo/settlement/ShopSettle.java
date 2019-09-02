package com.ziyoushenghuo.settlement;


import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;


import com.ziyoushenghuo.entry.ShopAccount;
import com.ziyoushenghuo.entry.ShopAccountRecord;
import com.ziyoushenghuo.entry.ShopProfit;
import com.ziyoushenghuo.rabbitmq.order.CheckAfterDelivery;
import com.ziyoushenghuo.repository.OrderRepository;

import com.ziyoushenghuo.repository.ShopAccountRecordRepository;
import com.ziyoushenghuo.repository.ShopAccountRepository;
import com.ziyoushenghuo.repository.ShopProfitRepository;
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

public class ShopSettle {

    @Autowired
    private ShopProfitRepository shopProfitRepository;

    @Autowired
    private ShopAccountRepository shopAccountRepository;

    @Autowired
    private ShopAccountRecordRepository shopAccountRecordRepository;


    @Autowired
    private OrderRepository orderRepository;

    private static Logger logger = LoggerFactory.getLogger(ShopSettle.class);
    /**
     * 获取待验证用户信息
     */


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public void onOrderProfitSettle(Order order,double rate,double ordermoney)
    {
        int shopid = order.getOrderShop().getShopId();

        double realmoney = ordermoney*rate;

        try {
            ShopAccount  shopAccount= shopAccountRepository.findByShopid(shopid);

            if(shopAccount!=null)
            {
                double money_before = shopAccount.getShop_total_money();
                double money_after = shopAccount.getShop_total_money()+realmoney;

                double salesmoney_before = shopAccount.getShop_profit();
                shopAccount.setShop_profit(shopAccount.getShop_profit()+ordermoney);
                shopAccount.setShop_total_money(money_after);
                shopAccount.setShop_proceeds(shopAccount.getShop_proceeds()+realmoney);

                ShopAccountRecord shopAccountRecord = new ShopAccountRecord();

                shopAccountRecord.setMoney(realmoney);
                shopAccountRecord.setShopid(shopid);

                shopAccountRecord.setRemark("");
                shopAccountRecord.setType(ShopAccountRecord.OP_TYPE_PROFIT);
                shopAccountRecord.setCreatetime(new Date());

                shopAccountRecord.setRefid(order.getId());
                shopAccountRecord.setBalance(money_after);


                shopAccountRecordRepository.save(shopAccountRecord);

                shopAccountRepository.save(shopAccount);

                order.setProfitStatus(Order.ORDER_PROFIT_STATUS_DONE);


                orderRepository.save(order);

            }
            else
            {

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



    }
}
