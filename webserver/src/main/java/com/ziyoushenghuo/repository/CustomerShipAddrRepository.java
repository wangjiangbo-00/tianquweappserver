package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerShipAddr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerShipAddrRepository extends JpaRepository<CustomerShipAddr,Long> {
    List<CustomerShipAddr> findByUid(int uid);
    List<CustomerShipAddr> findByUidAndIsdefault(int uid,int isdefount);
    CustomerShipAddr findById(int id);
    int countByUid(int uid);
}