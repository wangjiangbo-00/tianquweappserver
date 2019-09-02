package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.WalletPay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface WalletPayRepository extends JpaRepository<WalletPay,Long> {
    WalletPay findByUserid(int userid);

    WalletPay findById(int  id);

    int countByUserid(int userid);

    List<WalletPay> findByUserid(int userid, Pageable pageable);

}