package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.CustomerProfit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerProfitRepository extends JpaRepository<CustomerProfit,Long> {

    List<CustomerProfit> findByCid(int cid);

    CustomerProfit findById(int id);

    CustomerProfit findByOrderid(int orderid);

    int countByCid(int cid);

    List<CustomerProfit> findByCid(int cid, Pageable pageable);

}