package com.ziyoushenghuo.repository;

import java.util.List;

import com.ziyoushenghuo.entry.OwnerProfit;
import com.ziyoushenghuo.entry.Refund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface OwnerProfitRepository extends JpaRepository<OwnerProfit,Long> {


    List<OwnerProfit> findByShopid(int shopid);
    OwnerProfit findById(int id);
    OwnerProfit findByOrderid(int orderid);

    int countByShopid(int shopid);

    List<OwnerProfit> findByShopid(int shopid, Pageable pageable);

}