package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.Question;
import com.ziyoushenghuo.entry.QuestionCategory;
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
public interface QuestionCategoryRepositor extends JpaRepository<QuestionCategory,Long> {


    QuestionCategory findById(int  id);

    List<QuestionCategory> findAll();

}