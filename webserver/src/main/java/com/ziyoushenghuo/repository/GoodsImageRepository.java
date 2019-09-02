package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.GoodImage;

import com.ziyoushenghuo.entry.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GoodsImageRepository extends JpaRepository<GoodImage,Long> {

    GoodImage findById(int id);


}