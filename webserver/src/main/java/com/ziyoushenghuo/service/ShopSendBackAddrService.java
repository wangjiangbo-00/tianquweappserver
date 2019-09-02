package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.ShopSendBackAddr;
import com.ziyoushenghuo.repository.ShopRepository;
import com.ziyoushenghuo.repository.ShopSendBackAddrRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class ShopSendBackAddrService {

    @Autowired
    private ShopSendBackAddrRepository shopSendBackAddrRepository;

    /**
     * 获取待验证用户信息
     */

    public ShopSendBackAddr GetByShopid(int shopid){return shopSendBackAddrRepository.findByShopid(shopid);}

}
