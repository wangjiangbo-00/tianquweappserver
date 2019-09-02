package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.GoodImage;


import com.ziyoushenghuo.entry.GoodsCategory;
import com.ziyoushenghuo.repository.GoodsCategoryRepository;
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

public class GoodsCategoryService {

    @Autowired
    private GoodsCategoryRepository goodsCategoryRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodsCategory getById(int id){
        return goodsCategoryRepository.findById(id);
    }

    public List<GoodsCategory> getAll(){
        return goodsCategoryRepository.findByVisible(1);
    }

    public void UpdateOrCreate(GoodsCategory  goodsCategory){goodsCategoryRepository.save(goodsCategory);}
}
