package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface PromotionDiscountGoodsRepository extends JpaRepository<PromotionDiscountGoods,Long> {


    List<PromotionDiscountGoods> findAllByPidAndStatusIn(int pid,List<Integer> status);
    List<PromotionDiscountGoods> findAllByDiscountid(int discound);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update PromotionDiscountGoods pdg set pdg.status=?1 where pdg.discountid=?2")
    int updateAllStatusByDiscountId(int status,int discountid);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update PromotionDiscountGoods pdg set pdg.status=?1 where pdg.pid=?2")
    int updateAllStatusByPid(int status,int pid);

    int deleteAllByDiscountid(int discountid);
    int deleteAllByPid(int pid);

    PromotionDiscountGoods findById(int id);


    PromotionDiscountGoods findByGoodsCover(GoodsCover goodsCover);


    @Query(value = "select new PromotionDiscountGoods(pg.id,pg.discount,pg.starttime,pg.endtime,pg.status) from PromotionDiscountGoods pg where pg.goodsCover = :goods ")
    PromotionDiscountGoods findGoodsDiscount(@Param("goods") GoodsCover goodsCover);



}