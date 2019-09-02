package com.ziyoushenghuo.settlement;


import com.ziyoushenghuo.entry.Order;


import com.ziyoushenghuo.entry.ShopAccount;
import com.ziyoushenghuo.entry.ShopProfit;
import com.ziyoushenghuo.rabbitmq.order.CheckAfterDelivery;
import com.ziyoushenghuo.repository.OrderRepository;

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

public class PlatformSettle {

    @Autowired
    private ShopProfitRepository shopProfitRepository;

    @Autowired
    private ShopAccountRepository shopAccountRepository;


    @Autowired
    private OrderRepository orderRepository;

    private static Logger logger = LoggerFactory.getLogger(PlatformSettle.class);
    /**
     * 获取待验证用户信息
     */
    public ShopProfit getById(int id){
        return shopProfitRepository.findById(id);
    }
    public ShopProfit getByOrderId(int orderid){
        return shopProfitRepository.findByOrderid(orderid);
    }


    public void Create(ShopProfit shopProfit)
    {
        shopProfitRepository.save(shopProfit);
    }
    public void createOrUpdate(ShopProfit  shopProfit){shopProfitRepository.save(shopProfit);}
    public void delete(ShopProfit shopProfit){shopProfitRepository.delete(shopProfit);}


    public List<ShopProfit> getOwnerRecentlyProfits(int shopid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"time");

        Pageable pageable = new PageRequest(page,size,sort);
        List<ShopProfit> shopProfits = shopProfitRepository.findByShopid(shopid,pageable);

        return  shopProfits;
    };

    public int getOwnerProfitsCountById(int  shopid)
    {
        return  shopProfitRepository.countByShopid(shopid);
    }


    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ,rollbackFor={ Exception.class})
    public void onOrderProfitSettle(Order order)
    {
        int shopid = order.getOrderShop().getShopId();
        double ordermoney = order.getOrdermoney();

        double realmoney = ordermoney*0.984;

        ShopAccount  shopAccount= shopAccountRepository.findByShopid(shopid);

        if(shopAccount!=null)
        {
            double money_before = shopAccount.getShop_total_money();
            double money_after = shopAccount.getShop_total_money()+realmoney;

            double salesmoney_before = shopAccount.getShop_profit();
            shopAccount.setShop_profit(shopAccount.getShop_profit()+ordermoney);
            shopAccount.setShop_total_money(money_after);
            shopAccount.setShop_proceeds(shopAccount.getShop_proceeds()+realmoney);

            ShopProfit shopProfit = new ShopProfit();

            shopProfit.setMoney(realmoney);
            shopProfit.setShopid(shopid);



            shopProfit.setRemark("");
            shopProfit.setTime(new Date());



            shopProfit.setOrderid(order.getId());

            shopProfitRepository.save(shopProfit);

            shopAccountRepository.save(shopAccount);

            order.setProfitStatus(Order.ORDER_PROFIT_STATUS_DONE);


            orderRepository.save(order);

        }
        else
        {

        }








    }
}
