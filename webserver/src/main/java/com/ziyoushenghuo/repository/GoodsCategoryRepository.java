package com.ziyoushenghuo.repository;
import com.ziyoushenghuo.entry.GoodsCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WWJ on 2017/12/10.
 */
@Repository
public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory,Long> {
    List<GoodsCategory> findAll();
    List<GoodsCategory> findByVisible(int visible);
    GoodsCategory findById(int id);
}