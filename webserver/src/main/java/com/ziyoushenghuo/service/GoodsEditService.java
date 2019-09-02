package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.GoodsEdit;
import com.ziyoushenghuo.repository.GoodsEditRepository;
import com.ziyoushenghuo.repository.GoodsRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class GoodsEditService {

    @Autowired
    private GoodsEditRepository goodsEditRepository;

    /**
     * 获取待验证用户信息
     */
    public GoodsEdit getById(int id){
        return goodsEditRepository.findById(id);
    }

    public List<GoodsEdit> GetAllUnSubmit(int shopid){return goodsEditRepository.findByShopidAndSubmitstatus(shopid,0);}
    public List<GoodsEdit> GetAllUnReview(int shopid){return goodsEditRepository.findByShopidAndSubmitstatusAndReviewstatus(shopid,1,0);}
    public List<GoodsEdit> GetAllReviewed(int shopid){return goodsEditRepository.findByShopidAndSubmitstatusAndReviewstatusIsNot(shopid,1,0);}



    public List<GoodsEdit> GetAllUnReview(){return goodsEditRepository.findBySubmitstatusAndReviewstatus(1,0);}
    public List<GoodsEdit> GetAllReviewOk(){return goodsEditRepository.findBySubmitstatusAndReviewstatus(1,1);}
    public List<GoodsEdit> GetAllReviewFail(){return goodsEditRepository.findBySubmitstatusAndReviewstatus(1,2);}

    public void UpdateOrCreate(GoodsEdit  goodsEdit){goodsEditRepository.save(goodsEdit);}
    public void Delete(GoodsEdit  goodsEdit){goodsEditRepository.delete(goodsEdit);}
}
