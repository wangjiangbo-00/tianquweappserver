package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.repository.CustomerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 获取待验证用户信息
     */

    public Customer GetByWeixin(String weixin){return customerRepository.findByWeixin(weixin);}
    public Customer GetByIdPart(int id){return customerRepository.findByIdParamPart(id);}
    public Customer GetByToken(String token){return customerRepository.findByToken(token);}
    public Customer GetById(int id){return customerRepository.findById(id);}
    public Customer GetByPhone(String phone){return customerRepository.findByPhone(phone);}
    public Customer GetCoverById(int id){return customerRepository.findCoverById(id);}


    public List<Customer> GetMyRecommendPersons(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);


        return customerRepository.findRecommendPersons(userid,pageable);
    }


    public Customer GetNameIdById(int id){return customerRepository.findNameById(id);}
    public Customer GetOpenIdById(int id){return customerRepository.findOpenIdById(id);}

    public void create(Customer customer)
    {
        customerRepository.save(customer);
    }

    public void update(Customer customer) {
        customerRepository.save(customer);
    }


    public int updateUsernameById( int id ,String username){return customerRepository.updateUsernameByIdN(username,id);}
}
