package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Refund;

import com.ziyoushenghuo.repository.RefundRepository;
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

public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    /**
     * 获取待验证用户信息
     */
    public Refund getById(int id){
        return refundRepository.findById(id);
    }
    public List<Refund> getByOrderId(int orderid){
        return refundRepository.findByOrderid(orderid);
    }

    public boolean checkOrderRefundExist(long orderid,int type){
        return refundRepository.countByOrderidAndType(orderid,type)>0;
    }

    public List<Refund> getByUserid(int userid){
        return refundRepository.findByUserid(userid);
    }

    public void Create(Refund  refund)
    {
        refundRepository.save(refund);
    }
    public void createOrUpdate(Refund  refund){refundRepository.save(refund);}

    public void delete(Refund refund){refundRepository.delete(refund);}

    public List<Refund> getCustomerRecentlyRefunds(int userid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"createtime");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Refund> refunds = refundRepository.findByUserid(userid,pageable);

        return  refunds;
    };

    public int getCountByUserid(int userid)
    {
        return  refundRepository.countByUserid(userid);
    }
}
