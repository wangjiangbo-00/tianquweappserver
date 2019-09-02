package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount,Long> {
    CustomerAccount findByCid(int cid);

}