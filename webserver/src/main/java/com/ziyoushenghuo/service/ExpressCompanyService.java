package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.ExpressCompany;
import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.repository.ExpressCompanyRepository;
import com.ziyoushenghuo.repository.GoodsRepository;

import org.springframework.data.domain.Page;
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

public class ExpressCompanyService {

    @Autowired
    private ExpressCompanyRepository expressCompanyRepository;

    /**
     * 获取待验证用户信息
     */
    public ExpressCompany getById(int id){
        return expressCompanyRepository.findById(id);
    }

    public List<ExpressCompany> GetAll(){return expressCompanyRepository.findAll();}



    public void UpdateOrCreate(ExpressCompany  expressCompany){expressCompanyRepository.save(expressCompany);}
}
