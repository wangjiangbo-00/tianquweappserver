package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.ShopSendBackAddr;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopSendBackAddrRepository extends JpaRepository<ShopSendBackAddr,Long> {


    ShopSendBackAddr findByShopid(int  shopid);

}