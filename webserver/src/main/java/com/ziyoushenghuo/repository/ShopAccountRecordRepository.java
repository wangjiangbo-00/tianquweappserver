package com.ziyoushenghuo.repository;

import java.util.List;

import com.ziyoushenghuo.entry.OwnerProfit;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.ShopAccountRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopAccountRecordRepository extends JpaRepository<ShopAccountRecord,Long> {


    List<ShopAccountRecord> findByShopid(int shopid);
    ShopAccountRecord findById(int id);
    ShopAccountRecord findByRefidAndType(int refid,int type);
    int countByShopid(int shopid);
    List<ShopAccountRecord> findByShopid(int ownerid, Pageable pageable);
    List<ShopAccountRecord> findByShopidAndTypeIn(int ownerid,List<Integer>types, Pageable pageable);

}