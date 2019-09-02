package com.ziyoushenghuo.repository;

import java.util.List;


import com.ziyoushenghuo.entry.ShopProfit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopProfitRepository extends JpaRepository<ShopProfit,Long> {

    List<ShopProfit> findByShopid(int shopid);

    ShopProfit findById(int id);
    ShopProfit findByOrderid(int orderid);

    int countByShopid(int shopid);

    List<ShopProfit> findByShopid(int shopid, Pageable pageable);

}