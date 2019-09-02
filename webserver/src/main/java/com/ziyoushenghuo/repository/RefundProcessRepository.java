package com.ziyoushenghuo.repository;

import java.util.List;

import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.RefundProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface RefundProcessRepository extends JpaRepository<RefundProcess,Long> {

    @EntityGraph(value = "refundprocess.all", type = EntityGraph.EntityGraphType.FETCH)
    List<RefundProcess> findByShopidAndStatus(int shopid, int status);

    @EntityGraph(value = "refundprocess.all", type = EntityGraph.EntityGraphType.FETCH)
    List<RefundProcess> findByReceiveid(int receiveid, Pageable pageable);

    @EntityGraph(value = "refundprocess.all", type = EntityGraph.EntityGraphType.FETCH)
    RefundProcess findByOrderid(long orderid);

    @EntityGraph(value = "refundprocess.all", type = EntityGraph.EntityGraphType.FETCH)
    List<RefundProcess> findByShopid(int shopid, Pageable pageable);

    @EntityGraph(value = "refundprocess.all", type = EntityGraph.EntityGraphType.FETCH)
    List<RefundProcess> findByStatus(int status,Pageable pageable);
}