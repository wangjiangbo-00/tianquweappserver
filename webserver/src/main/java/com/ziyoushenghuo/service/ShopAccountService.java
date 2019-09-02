package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.ShopAccount;
import com.ziyoushenghuo.repository.ShopAccountRepository;
import com.ziyoushenghuo.repository.ShopRepository;
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

public class ShopAccountService {

    @Autowired
    private ShopAccountRepository shopAccountRepository;

    /**
     * 获取待验证用户信息
     */

    public ShopAccount GetByShopid(int shopid){return shopAccountRepository.findByShopid(shopid);}

    public void  UpdateOrCreate(ShopAccount shopAccount){shopAccountRepository.save(shopAccount);}


}
