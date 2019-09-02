package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface FreeOrderRepository extends JpaRepository<FreeOrder,Long> {


    FreeOrder findById(int  id);

    Page<FreeOrder> findAll(Pageable pageable);

    List<FreeOrder> findByUserid(int  userid, Sort sort);
}