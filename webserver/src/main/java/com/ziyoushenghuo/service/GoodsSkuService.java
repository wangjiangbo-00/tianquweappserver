package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.GoodsSku;
import com.ziyoushenghuo.repository.GoodsImageRepository;
import com.ziyoushenghuo.repository.GoodsRepository;

import com.ziyoushenghuo.repository.GoodsSkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class GoodsSkuService {

    @Autowired
    private GoodsSkuRepository goodsSkuRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodsSku getById(int id){
        return goodsSkuRepository.findById(id);
    }

    public List<GoodsSku> getByGoodsid(int goodsid){
        return goodsSkuRepository.findAllByGoodsid(goodsid);
    }

    public void UpdateOrCreate(GoodsSku  goodsSku){goodsSkuRepository.save(goodsSku);}
}
