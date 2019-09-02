package com.ziyoushenghuo.repository;

import java.util.List;

import com.ziyoushenghuo.entry.Refund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund,Long> {


    @Override
    List<Refund> findAll();

    List<Refund> findByUserid(int userid);
    Refund findById(int id);
    List<Refund> findByOrderid(long orderid);

    int countByOrderidAndType(long orderid,int type);

    int countByUserid(int userid);

    List<Refund> findByUserid(int userid, Pageable pageable);

}