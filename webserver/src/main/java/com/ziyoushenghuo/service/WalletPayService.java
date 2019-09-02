package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.repository.WalletPayRepository;
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

public class WalletPayService {

    @Autowired
    private WalletPayRepository walletPayRepository;

    /**
     * 获取待验证用户信息
     */

    public WalletPay GetByUserid(int userid){return walletPayRepository.findByUserid(userid);}
    public WalletPay GetById(int id){return walletPayRepository.findById(id);}
    public void createOrUpdate(WalletPay  walletPay){walletPayRepository.save(walletPay);}


    public List<WalletPay> getCustomerRecentlyChanges(int userid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"createtime");

        Pageable pageable = new PageRequest(page,size,sort);
        List<WalletPay> customerRecharges = walletPayRepository.findByUserid(userid,pageable);

        return  customerRecharges;
    };

    public int getCountByWeixin(int userid)
    {
        return  walletPayRepository.countByUserid(userid);
    }
}
