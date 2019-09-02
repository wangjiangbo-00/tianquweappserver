package com.ziyoushenghuo.service;





import com.ziyoushenghuo.entry.OwnerProfit;
import com.ziyoushenghuo.entry.ShopAccountRecord;
import com.ziyoushenghuo.repository.OwnerProfitRepository;
import com.ziyoushenghuo.repository.ShopAccountRecordRepository;
import com.ziyoushenghuo.repository.ShopAccountRepository;
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

public class ShopAccountRecordService {

    @Autowired
    private ShopAccountRecordRepository shopAccountRecordRepository;

    /**
     * 获取待验证用户信息
     */
    public ShopAccountRecord getById(int id){
        return shopAccountRecordRepository.findById(id);
    }
    public ShopAccountRecord getByRefidAndType(int refid,int type){
        return shopAccountRecordRepository.findByRefidAndType(refid,type);
    }
    public List<ShopAccountRecord> getByShopid(int shopid){
        return shopAccountRecordRepository.findByShopid(shopid);
    }


    public void createOrUpdate(ShopAccountRecord  shopAccountRecord){shopAccountRecordRepository.save(shopAccountRecord);}
    public void delete(ShopAccountRecord shopAccountRecord){shopAccountRecordRepository.delete(shopAccountRecord);}


    public List<ShopAccountRecord> getOwnerRecentlyRecords(int shopid, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<ShopAccountRecord> shopAccountRecords = shopAccountRecordRepository.findByShopid(shopid,pageable);

        return  shopAccountRecords;
    };


    public List<ShopAccountRecord> getRecentlyRecords(int cid, List<Integer>types,int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);


        List<ShopAccountRecord> shopAccountRecords = shopAccountRecordRepository.findByShopidAndTypeIn(cid,types,pageable);

        return  shopAccountRecords;
    };

    public int getOwnerRecordsCountByShopid(int  shopid)
    {
        return  shopAccountRecordRepository.countByShopid(shopid);
    }
}
