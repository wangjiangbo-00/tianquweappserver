package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.GoodsAttribute;
import com.ziyoushenghuo.repository.GoodsAttributeRepository;
import com.ziyoushenghuo.repository.GoodsImageRepository;
import com.ziyoushenghuo.repository.GoodsRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class GoodsAttributeService {

    @Autowired
    private GoodsAttributeRepository goodsAttributeRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodsAttribute getById(int id){
        return goodsAttributeRepository.findById(id);
    }



    public void UpdateOrCreate(GoodsAttribute  goodsAttribute){goodsAttributeRepository.save(goodsAttribute);}
}
