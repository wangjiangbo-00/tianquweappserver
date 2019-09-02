package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GiftActivityRepository extends JpaRepository<GiftActivity,Long> {

    GiftActivity findById(int  id);

    List<GiftActivity> findAllByStatusInAndGiftfrom(List<Integer>statuses, int giftfrom,  Pageable pageable);

    List<GiftActivity> findAllByGiftfromAndOwnerid(int from,int ownerid,   Pageable pageable);

    List<GiftActivity> findAllByStatus( int status, Pageable pageable);


}