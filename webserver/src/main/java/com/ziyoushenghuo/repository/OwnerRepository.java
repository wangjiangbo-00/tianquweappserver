package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner,Long> {
    Owner findByUsername(String username);
    Owner   findByUsernameAndPassword(String username,String password);
    Owner findById(int id);
    Owner findByWeixin(String weixinid);
    Owner findByToken(String token);

}