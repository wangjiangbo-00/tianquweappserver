package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.ShopAccount;
import com.ziyoushenghuo.entry.ShopPickupPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopPickupPointRepository extends JpaRepository<ShopPickupPoint,Long> {
    ShopPickupPoint findByShopid(int shopid);



}