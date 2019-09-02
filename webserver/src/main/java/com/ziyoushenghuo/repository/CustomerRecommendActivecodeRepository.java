package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.CustomerRecommendActivecode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerRecommendActivecodeRepository extends JpaRepository<CustomerRecommendActivecode,Long> {

    CustomerRecommendActivecode findByCode(String code);



    @Transactional
    @Modifying(clearAutomatically = true)

    int deleteAllByCreatetimeBefore(Date date);

    CustomerRecommendActivecode findById(int id);

    List<CustomerRecommendActivecode> findByStatus( int status,Pageable pageable);


}