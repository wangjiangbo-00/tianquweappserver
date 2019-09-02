package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerShipAddr;
import com.ziyoushenghuo.entry.UserPropagateSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface UserPropagateSettingRepository extends JpaRepository<UserPropagateSetting,Long> {
    List<UserPropagateSetting> findByUid(int uid);
    List<UserPropagateSetting> findByUidAndIsdefault(int uid,int isdefault);
    UserPropagateSetting findById(int id);
    int countByUid(int uid);
}