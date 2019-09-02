package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Goods;

import com.ziyoushenghuo.entry.ShopExpressFee;
import com.ziyoushenghuo.repository.GoodsRepository;


import com.ziyoushenghuo.repository.ShopExpressFeeRepository;
import com.ziyoushenghuo.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class ShopExpressFeeService {

    @Autowired
    private ShopExpressFeeRepository shopExpressFeeRepository;

    /**
     * 获取待验证用户信息
     */
    public ShopExpressFee getById(int id){
        return shopExpressFeeRepository.findById(id);
    }

    public List<ShopExpressFee> GetAllByShopid(int shopid){return shopExpressFeeRepository.findByShopid(shopid);}

}
