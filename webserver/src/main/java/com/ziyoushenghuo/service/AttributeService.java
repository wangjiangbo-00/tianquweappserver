package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Attribute;
import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.repository.AttributeRepository;
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

public class AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    /**
     * 获取待验证用户信息
     */
    public Attribute getById(int id){
        return attributeRepository.findById(id);
    }



    public void UpdateOrCreate(Attribute  attribute){attributeRepository.save(attribute);}
}
