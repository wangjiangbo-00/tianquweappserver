package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.*;
import org.springframework.data.domain.Pageable;
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
public interface GiftWinnerRepository extends JpaRepository<GiftWinner,Long> {

    GiftWinner findById(int  id);

    List<GiftWinner> findByUserid(int userid);
}