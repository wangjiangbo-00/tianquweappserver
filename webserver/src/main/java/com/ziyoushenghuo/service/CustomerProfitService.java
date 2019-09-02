package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerProfitService {

    @Autowired
    private CustomerProfitRepository customerProfitRepository;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;


    @Autowired
    private OrderRepository orderRepository;

    private static Logger logger = LoggerFactory.getLogger(CustomerProfitService.class);
    /**
     * 获取待验证用户信息
     */
    public CustomerProfit getById(int id){
        return customerProfitRepository.findById(id);
    }
    public CustomerProfit getByOrderId(int orderid){
        return customerProfitRepository.findByOrderid(orderid);
    }


    public void Create(CustomerProfit customerProfit)
    {
        customerProfitRepository.save(customerProfit);
    }
    public void createOrUpdate(CustomerProfit  customerProfit){customerProfitRepository.save(customerProfit);}
    public void delete(CustomerProfit customerProfit){customerProfitRepository.delete(customerProfit);}


    public List<CustomerProfit> getOwnerRecentlyProfits(int cid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"time");

        Pageable pageable = new PageRequest(page,size,sort);
        List<CustomerProfit> customerProfits = customerProfitRepository.findByCid(cid,pageable);

        return  customerProfits;
    };

    public int getOwnerProfitsCountById(int  cid)
    {
        return  customerProfitRepository.countByCid(cid);
    }


    @Transactional(rollbackFor={ Exception.class})
    public void onOrderProfitSettle(Order order)
    {

    }
}
