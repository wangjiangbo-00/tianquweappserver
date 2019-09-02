package com.ziyoushenghuo.service;



import com.ziyoushenghuo.entry.OrderExpress;


import com.ziyoushenghuo.repository.OrderExpressRepository;
import com.ziyoushenghuo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class OrderExpressService {

    @Autowired
    private OrderExpressRepository orderExpressRepository;

    /**0
     * 获取待验证用户信息
     */
    public OrderExpress getById(int id){
        return orderExpressRepository.findById(id);
    }


    public OrderExpress getByOrderId(int orderid){
        return orderExpressRepository.findByOrderid(orderid);
    }

    public OrderExpress UpdateOrCreate(OrderExpress orderExpress){
        return orderExpressRepository.save(orderExpress);
    }
}
