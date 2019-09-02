package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.GoodsClass;
import com.ziyoushenghuo.repository.GoodsClassRepository;
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

public class GoodsClassService {

    @Autowired
    private GoodsClassRepository goodsClassRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodsClass getById(int id){
        return goodsClassRepository.findById(id);
    }



    public void UpdateOrCreate(GoodsClass  goodsClass){goodsClassRepository.save(goodsClass);}
}
