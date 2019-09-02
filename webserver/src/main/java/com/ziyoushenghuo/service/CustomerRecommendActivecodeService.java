package com.ziyoushenghuo.service;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.repository.CustomerRecommendActivecodeRepository;
import com.ziyoushenghuo.repository.WalletPayRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class CustomerRecommendActivecodeService {

    @Autowired
    private CustomerRecommendActivecodeRepository customerRecommendActivecodeRepository;

    /**
     * 获取待验证用户信息
     */

    public CustomerRecommendActivecode GetByCode(String code)
    {
        return  customerRecommendActivecodeRepository.findByCode(code);
    }

    public List<CustomerRecommendActivecode> getUsedCodes( int page, int size){
        Sort sort = new Sort(Sort.Direction.ASC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return customerRecommendActivecodeRepository.findByStatus(0,pageable);
    }

    public void DeleteItem(CustomerRecommendActivecode customerRecommendActivecode)
    {
        customerRecommendActivecodeRepository.delete(customerRecommendActivecode);
    }

    public void deleteExpireCodes(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        //过去七天
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        Date weekAgo = c.getTime();

        customerRecommendActivecodeRepository.deleteAllByCreatetimeBefore(weekAgo);

    }

    public void addCodes(int n)
    {

        try
        {
            List<CustomerRecommendActivecode> customerRecommendActivecodeList = new ArrayList<>(n);
            for(int i =0;i<n;i++)
            {

                CustomerRecommendActivecode customerRecommendActivecode = new CustomerRecommendActivecode();
                customerRecommendActivecode.setCode(AppCommon.getRandowString(6));
                customerRecommendActivecode.setStatus(CustomerRecommendActivecode.CODE_STATUS_NORMAL);
                customerRecommendActivecode.setCreatetime(new Date());

                customerRecommendActivecodeList.add(customerRecommendActivecode);

            }

            customerRecommendActivecodeRepository.save(customerRecommendActivecodeList);
        }
        catch (Exception e)
        {

        }


    }

    public  void save(CustomerRecommendActivecode customerRecommendActivecode)
    {
        customerRecommendActivecodeRepository.save(customerRecommendActivecode);
    }



}
