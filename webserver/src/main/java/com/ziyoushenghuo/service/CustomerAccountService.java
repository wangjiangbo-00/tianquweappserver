package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.CustomerAccount;
import com.ziyoushenghuo.repository.CustomerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerAccountService {

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    /**
     * 获取待验证用户信息
     */

    public CustomerAccount GetByCid(int cid){return customerAccountRepository.findByCid(cid);}

    public void  UpdateOrCreate(CustomerAccount customerAccount){customerAccountRepository.save(customerAccount);}


}
