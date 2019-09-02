package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.CustomerWithdrew;
import com.ziyoushenghuo.entry.Withdrew;
import com.ziyoushenghuo.repository.CustomerWithdrewRepository;
import com.ziyoushenghuo.repository.WithdrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerWithdrewService {

    @Autowired
    private CustomerWithdrewRepository customerwithdrewRepository;

    /**
     * 获取待验证用户信息
     */
    public CustomerWithdrew getById(int id){
        return customerwithdrewRepository.findById(id);
    }
    public List<CustomerWithdrew>  getByCid(int cid){
        return customerwithdrewRepository.findByCid(cid);
    }

    public List<CustomerWithdrew>  getRecentsByCid(int cid, int page, int size){

        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerwithdrewRepository.findByCid(cid);
    }


    public List<CustomerWithdrew>  getRecentsByStatus(int status, int page, int size){

        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerwithdrewRepository.findByStatus(status,pageable);
    }

    public void createOrUpdate(CustomerWithdrew  withdrew){customerwithdrewRepository.save(withdrew);}

    public void delete(CustomerWithdrew withdrew){customerwithdrewRepository.delete(withdrew);}
}
