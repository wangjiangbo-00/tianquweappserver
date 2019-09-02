package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.repository.GoodImageRepository;
import com.ziyoushenghuo.repository.GoodsRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class GoodImageService {

    @Autowired
    private GoodImageRepository goodImageRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodImage getById(int id){
        return goodImageRepository.findById(id);
    }



    public void UpdateOrCreate(GoodImage  goodImage){goodImageRepository.save(goodImage);}
}
