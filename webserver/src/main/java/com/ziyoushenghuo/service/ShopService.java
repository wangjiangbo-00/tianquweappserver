package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Shop;
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

public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    /**
     * 获取待验证用户信息
     */

    public Shop GetByOwnerid(int ownid){return shopRepository.findByOwnerid(ownid);}
    public Shop GetById(int id){return shopRepository.findById(id);}

    public List<Shop> GetAll(){return shopRepository.findByShopstate(1);}
    public List<Shop> GetShopProvinces(){return shopRepository.findByShopstate(1);}

    public List<Shop> GetShopCitys(int provinceid){return shopRepository.findByProvinceidAndShopstate(provinceid,1);}
}
