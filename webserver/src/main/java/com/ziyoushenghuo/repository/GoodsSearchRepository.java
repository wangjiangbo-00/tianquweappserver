package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.Goods;

import com.ziyoushenghuo.entry.GoodsBasic;
import com.ziyoushenghuo.entry.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GoodsSearchRepository extends JpaRepository<GoodsBasic,Long> {

    List<GoodsBasic> findByStateAndShopstateAndType(int state, int shopstate,int type);
    List<GoodsBasic> findByShopidAndStateAndType(int shopid,int state,int type);
    Page<GoodsBasic> findByStateAndShopstateAndType(int state,int shopstate,int type,Pageable pageable);
    List<GoodsBasic> findByCatidAndStateAndShopstateAndType(int catid,int state,int shopstate,int type, Pageable pageable);

    List<GoodsBasic> findByGoodsnameLike(String name);
    List<GoodsBasic> findByState(int state);
    List<GoodsBasic> findByShopid(int shopid);

    GoodsBasic findById(int goodsid);
}