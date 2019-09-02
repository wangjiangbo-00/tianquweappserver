package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.CustomerAccountRecord;
import com.ziyoushenghuo.entry.ShopAccountRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerAccountRecordRepository extends JpaRepository<CustomerAccountRecord,Long> {

    List<CustomerAccountRecord> findByCid(int cid);
    CustomerAccountRecord findById(int id);
    CustomerAccountRecord findByRefidAndCidAndType(long refid, int userid,int type);

    List<CustomerAccountRecord> findByRefidAndTypeIn(long cid, List<Integer>types);
    int countByCid(int cid);
    List<CustomerAccountRecord> findByCidAndTypeIn(int cid, List<Integer>types,Pageable pageable);
    List<CustomerAccountRecord> findByCid(int cid,Pageable pageable);
}