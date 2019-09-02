package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Owner;
import com.ziyoushenghuo.entry.ShopNotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopNotifyRepository extends JpaRepository<ShopNotify,Long> {


    ShopNotify findById(int id);

    ShopNotify findByOrderid(int orderid);


    List<ShopNotify> findByShopId(int shopid);
}