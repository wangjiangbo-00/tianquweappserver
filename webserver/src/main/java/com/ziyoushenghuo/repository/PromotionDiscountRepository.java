package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.PromotionDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface PromotionDiscountRepository extends JpaRepository<PromotionDiscount,Long> {

    List<PromotionDiscount> findByPid(int pid);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update PromotionDiscount pd set pd.status=?1 where pd.pid=?2")
    int updateAllRelatedDiscount(int status,int pid);

    PromotionDiscount findById(int id);


    List<PromotionDiscount> findByLevelAndIsvisibleAndStatusIn(int level,int visible,List<Integer>statuses);
}