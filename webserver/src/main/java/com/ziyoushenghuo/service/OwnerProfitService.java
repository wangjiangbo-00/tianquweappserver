package com.ziyoushenghuo.service;





import com.ziyoushenghuo.entry.OwnerProfit;
import com.ziyoushenghuo.repository.OwnerProfitRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class OwnerProfitService {

    @Autowired
    private OwnerProfitRepository ownerProfitRepository;

    /**
     * 获取待验证用户信息
     */
    public OwnerProfit getById(int id){
        return ownerProfitRepository.findById(id);
    }
    public OwnerProfit getByOrderId(int orderid){
        return ownerProfitRepository.findByOrderid(orderid);
    }
    public List<OwnerProfit> getByShopid(int shopid){
        return ownerProfitRepository.findByShopid(shopid);
    }

    public void Create(OwnerProfit ownerProfit)
    {
        ownerProfitRepository.save(ownerProfit);
    }
    public void createOrUpdate(OwnerProfit  ownerProfit){ownerProfitRepository.save(ownerProfit);}
    public void delete(OwnerProfit ownerProfit){ownerProfitRepository.delete(ownerProfit);}


    public List<OwnerProfit> getOwnerRecentlyProfits(int shopid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<OwnerProfit> ownerProfits = ownerProfitRepository.findByShopid(shopid,pageable);

        return  ownerProfits;
    };

    public int getOwnerProfitsCountById(int  shopid)
    {
        return  ownerProfitRepository.countByShopid(shopid);
    }
}
