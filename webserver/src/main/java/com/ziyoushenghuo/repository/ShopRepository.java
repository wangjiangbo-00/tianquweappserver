package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
    Shop findByOwnerid(int ownid);

    Shop findById(int  id);

    List<Shop> findByShopstate(int state);


    List<Shop> findDistinctTopByShopstate(int state);


    List<Shop> findByProvinceidAndShopstate(int provinceid,int state);


}