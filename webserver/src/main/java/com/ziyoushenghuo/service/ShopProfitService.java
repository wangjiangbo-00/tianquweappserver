package com.ziyoushenghuo.service;


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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class ShopProfitService {

    @Autowired
    private ShopProfitRepository shopProfitRepository;

    @Autowired
    private ShopAccountRepository shopAccountRepository;


    @Autowired
    private OrderRepository orderRepository;

    private static Logger logger = LoggerFactory.getLogger(ShopProfitService.class);
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


    @Transactional(rollbackFor = Exception.class)
    public void onOrderProfitSettle(Order order)
    {
        int shopid = order.getOrderShop().getShopId();
        double ordermoney = order.getOrdermoney();

        double realmoney = ordermoney*0.984;










    }
}
