package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Refund;

import com.ziyoushenghuo.entry.RefundProcess;
import com.ziyoushenghuo.repository.RefundProcessRepository;
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

public class RefundProcessService {

    @Autowired
    private RefundProcessRepository refundProcessRepository;

    /**
     * 获取待验证用户信息
     */

    public RefundProcess getByOrderId(long orderid){
        return refundProcessRepository.findByOrderid(orderid);
    }

    public List<RefundProcess> getShopUnDeal(int shopid){
        return refundProcessRepository.findByShopidAndStatus(shopid,RefundProcess.REFUND_PROCESS_SELLER_NOREPLY);
    }


    public List<RefundProcess> getShopUnreceive(int shopid){
        return refundProcessRepository.findByShopidAndStatus(shopid,RefundProcess.REFUND_PROCESS_SELLER_RECEIVE);
    }

    public  List<RefundProcess> getUserRefundprocesses(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"createtime");

        Pageable pageable = new PageRequest(page,size,sort);
        return refundProcessRepository.findByReceiveid(userid,pageable);
    }


    public List<RefundProcess> getShopAll(int shopid , int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"createtime");

        Pageable pageable = new PageRequest(page,size,sort);
        return refundProcessRepository.findByShopid(shopid,pageable);
    }


    public List<RefundProcess> getadminneeddeal( int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"orderid");

        Pageable pageable = new PageRequest(page,size,sort);
        return refundProcessRepository.findByStatus(RefundProcess.REFUND_PROCESS_PLATFORM_IN,pageable);
    }



    public void Create(RefundProcess  refundProcess)
    {
        refundProcessRepository.save(refundProcess);
    }
    public void createOrUpdate(RefundProcess  refundProcess){refundProcessRepository.save(refundProcess);}

    public void delete(RefundProcess refundProcess){refundProcessRepository.delete(refundProcess);}


}
