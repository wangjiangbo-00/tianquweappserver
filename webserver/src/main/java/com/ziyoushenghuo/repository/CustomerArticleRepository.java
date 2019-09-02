package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.CustomerArticle;
import com.ziyoushenghuo.entry.CustomerArticleShort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface CustomerArticleRepository extends JpaRepository<CustomerArticle,Long> {

    CustomerArticle findById(int id);
}