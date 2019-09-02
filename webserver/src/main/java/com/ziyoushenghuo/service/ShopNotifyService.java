package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Owner;
import com.ziyoushenghuo.entry.ShopNotify;
import com.ziyoushenghuo.repository.OwnerRepository;
import com.ziyoushenghuo.repository.ShopNotifyRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class ShopNotifyService {

    @Autowired
    private ShopNotifyRepository shopNotifyRepository;

    /**
     * 获取待验证用户信息
     */
    public ShopNotify getShopNotifyById(int id){
        return shopNotifyRepository.findById(id);
    }


    public List<ShopNotify> gethopNotifyByShopIOd(int shopid){
        return shopNotifyRepository.findByShopId(shopid);
    }

    public void Create(ShopNotify  shopNotify)
    {
        shopNotifyRepository.save(shopNotify);
    }
    public void UpdateOrder(ShopNotify  order){shopNotifyRepository.save(order);}
}
