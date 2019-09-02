package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.CustomerWithdrew;
import com.ziyoushenghuo.entry.Withdrew;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerWithdrewRepository extends JpaRepository<CustomerWithdrew,Long> {

    CustomerWithdrew findById(int id);
    List<CustomerWithdrew> findByCid(int cid);

    List<CustomerWithdrew> findByCid(int cid, Pageable pageable);

    List<CustomerWithdrew> findByStatus(int status, Pageable pageable);
}