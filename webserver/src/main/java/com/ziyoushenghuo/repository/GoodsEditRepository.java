package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.Goods;

import com.ziyoushenghuo.entry.GoodsEdit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GoodsEditRepository extends JpaRepository<GoodsEdit,Long> {

    GoodsEdit findById(int id);

    List<GoodsEdit> findByShopidAndSubmitstatusAndReviewstatus(int id,int submit,int review);

    List<GoodsEdit> findByShopidAndSubmitstatus(int id,int submit);

    List<GoodsEdit> findByShopidAndSubmitstatusAndReviewstatusIsNot(int id,int submit,int review_no);

    List<GoodsEdit> findBySubmitstatusAndReviewstatus(int submit,int review);

    Page<GoodsEdit> findAll(Pageable pageable);

    int countByCatid(int catid);
}