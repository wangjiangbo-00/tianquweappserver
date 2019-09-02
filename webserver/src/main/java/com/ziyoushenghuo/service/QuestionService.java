package com.ziyoushenghuo.service;



import com.ziyoushenghuo.entry.Question;
import com.ziyoushenghuo.entry.QuestionCategory;


import com.ziyoushenghuo.repository.QuestionCategoryRepositor;
import com.ziyoushenghuo.repository.QuestionRepositor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class QuestionService {

    @Autowired
    private QuestionRepositor questionRepositor;


    @Autowired
    private QuestionCategoryRepositor questionCategoryRepositor;
    /**
     * 获取待验证用户信息
     */
    public Question getById(int id){
        return questionRepositor.findById(id);
    }

    public List<Question> getAll(){
        return questionRepositor.findByShow(1);
    }

    public void createOrUpdate(Question  question){questionRepositor.save(question);}

    public void delete(Question question){questionRepositor.delete(question);}

    public QuestionCategory getQuestionCategoryById(int id){
        return questionCategoryRepositor.findById(id);
    }

}
