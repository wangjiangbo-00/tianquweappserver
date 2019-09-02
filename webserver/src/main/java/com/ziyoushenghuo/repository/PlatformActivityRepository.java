package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.PlatformActivity;
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
public interface PlatformActivityRepository extends JpaRepository<PlatformActivity,Long> {

    PlatformActivity findById(int id);


    PlatformActivity findByDiscountid(int id);

    List<PlatformActivity> findAllByIsvisible(int visible);
}