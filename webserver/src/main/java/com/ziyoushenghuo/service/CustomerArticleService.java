package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerArticle;
import com.ziyoushenghuo.entry.CustomerArticleShort;
import com.ziyoushenghuo.repository.CustomerArticleRepository;
import com.ziyoushenghuo.repository.CustomerArticleShortRepository;
import com.ziyoushenghuo.repository.CustomerRepository;
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

public class CustomerArticleService {

    @Autowired
    private CustomerArticleRepository customerArticleRepository;


    @Autowired
    private CustomerArticleShortRepository customerArticleShortRepository;

    /**
     * 获取待验证用户信息
     */

    public List<CustomerArticleShort> GetCustomerArticles(int cid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerArticleShortRepository.findByCid(cid,pageable);
    };
    public List<CustomerArticleShort> GetShopArticles(int shopid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerArticleShortRepository.findByShopid(shopid,pageable);
    };


    public List<CustomerArticleShort> GetArticlesByIds(List<Integer>ids){

        return customerArticleShortRepository.findAllByIdIn(ids);
    };

    public List<CustomerArticleShort> GetGoodsArticles(int goodsid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerArticleShortRepository.findByGoodsid(goodsid,pageable);
    };
    public CustomerArticle GetById(int id){return customerArticleRepository.findById(id);}
    public CustomerArticleShort GetShortById(int id){return customerArticleShortRepository.findById(id);}

    public void CreateOrUpdate(CustomerArticle customerArticle)
    {
        customerArticleRepository.save(customerArticle);
    }

    public void CreateOrUpdate(CustomerArticleShort customerArticleShort)
    {
        customerArticleShortRepository.save(customerArticleShort);
    }

    public void Delete(CustomerArticle customerArticle) {
        customerArticleRepository.delete(customerArticle);
    }

    public void Delete(CustomerArticleShort customerArticleShort) {
        customerArticleShortRepository.delete(customerArticleShort);
    }
}
