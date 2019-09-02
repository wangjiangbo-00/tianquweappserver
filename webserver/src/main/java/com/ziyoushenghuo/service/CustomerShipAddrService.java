package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerShipAddr;
import com.ziyoushenghuo.repository.CustomerRepository;
import com.ziyoushenghuo.repository.CustomerShipAddrRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerShipAddrService {

    @Autowired
    private CustomerShipAddrRepository customerShipAddrRepository;

    /**
     * 获取待验证用户信息
     */

    public List<CustomerShipAddr> GetByUserid(int userid){return customerShipAddrRepository.findByUid(userid);}

    public int CountByUserid(int userid){return customerShipAddrRepository.countByUid(userid);}

    public List<CustomerShipAddr> GetUserDefault(int userid){return customerShipAddrRepository.findByUidAndIsdefault(userid,1);}

    public CustomerShipAddr GetByid(int id){return customerShipAddrRepository.findById(id);}

    public void create(CustomerShipAddr customerShipAddr)
    {
        customerShipAddrRepository.save(customerShipAddr);
    }

    public void update(CustomerShipAddr customerShipAddr) {
        customerShipAddrRepository.save(customerShipAddr);
    }

    public void delete(CustomerShipAddr customerShipAddr) {
        customerShipAddrRepository.delete(customerShipAddr);
    }
}
