package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.Goods;

import com.ziyoushenghuo.entry.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods,Long> {

    @EntityGraph(value="goods.all",type= EntityGraph.EntityGraphType.FETCH)
    Goods findById(int id);

    List<Goods> findByShop(Shop shop);

    List<Goods> findByCatidAndStateAndShopstate(int catid,int state,int shopstate, Pageable pageable);

    Page<Goods> findByStateAndShopstate(int state,int shopstate,Pageable pageable);

    int countByCatidAndStateAndShopstate(int catid,int state,int shopstate);
}