package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerArticle;
import com.ziyoushenghuo.entry.CustomerArticleShort;
import com.ziyoushenghuo.entry.CustomerRecommendImg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerRecommendImgRepository extends JpaRepository<CustomerRecommendImg,Long> {

    CustomerRecommendImg findById(int id);

    List<CustomerRecommendImg> findAllByCid(int userid);

    CustomerRecommendImg findByCidAndTidAndRefid(int userid,int tid,int refid);


    CustomerRecommendImg findByCidAndTid(int userid,int tid);
}