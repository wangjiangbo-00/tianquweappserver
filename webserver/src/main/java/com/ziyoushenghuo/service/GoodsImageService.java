package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
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

public class GoodsImageService {

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodImage getById(int id){
        return goodsImageRepository.findById(id);
    }



    public void UpdateOrCreate(GoodImage  goodImage){goodsImageRepository.save(goodImage);}
}
