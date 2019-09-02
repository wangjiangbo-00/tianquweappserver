package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.CustomerAccountRecord;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.ShopAccountRecord;
import com.ziyoushenghuo.repository.CustomerAccountRecordRepository;
import com.ziyoushenghuo.repository.ShopAccountRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerAccountRecordService {

    @Autowired
    private CustomerAccountRecordRepository customerAccountRecordRepository;

    /**
     * 获取待验证用户信息
     */
    public CustomerAccountRecord getById(int id){
        return customerAccountRecordRepository.findById(id);
    }

    public List<CustomerAccountRecord> getByCid(int cid){
        return customerAccountRecordRepository.findByCid(cid);
    }


    public void createOrUpdate(CustomerAccountRecord  customerAccountRecord){customerAccountRecordRepository.save(customerAccountRecord);}
    public void delete(CustomerAccountRecord customerAccountRecord){customerAccountRecordRepository.delete(customerAccountRecord);}


    public List<CustomerAccountRecord> getRecentlyRecords(int cid, List<Integer>types,int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);


        List<CustomerAccountRecord> customerAccountRecords = customerAccountRecordRepository.findByCidAndTypeIn(cid,types,pageable);

        return  customerAccountRecords;
    };

    public List<CustomerAccountRecord> getRecentlyAllRecords(int cid,int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<CustomerAccountRecord> customerAccountRecords = customerAccountRecordRepository.findByCid(cid,pageable);

        return  customerAccountRecords;
    };

    public int getRecordsCountByCid(int  cid)
    {
        return  customerAccountRecordRepository.countByCid(cid);
    }
}
