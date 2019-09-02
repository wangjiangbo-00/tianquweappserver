package com.ziyoushenghuo.repository;


import com.ziyoushenghuo.entry.ExpressCompany;
import com.ziyoushenghuo.entry.Goods;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface ExpressCompanyRepository extends JpaRepository<ExpressCompany,Long> {

    ExpressCompany findById(int id);

    List<ExpressCompany> findAll();

}