package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.GoodsBasic;
import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.repository.GoodsRepository;


import com.ziyoushenghuo.repository.GoodsSearchRepository;
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

public class GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;



    @Autowired
    private GoodsSearchRepository goodsSearchRepository;

    /**
     * 获取待验证用户信息
     */
    public Goods getById(int id){
        return goodsRepository.findById(id);
    }
    public List<GoodsBasic> getRecommendGoodsByCat(int cat, int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<GoodsBasic> goods = goodsSearchRepository.findByCatidAndStateAndShopstateAndType(cat,1,1,0,pageable);

        return  goods;
    };





    public List<GoodsBasic> getRecommendGoods( int page, int size)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        Page<GoodsBasic> goods = goodsSearchRepository.findByStateAndShopstateAndType(1,1,0,pageable);

        List<GoodsBasic> goodsList = goods.getContent();

        return  goodsList;
    };




    public List<GoodsBasic> getSearchGoods()
    {
        List<GoodsBasic> goodsList = goodsSearchRepository.findByStateAndShopstateAndType(1,1,0);
        return  goodsList;
    };

    public List<GoodsBasic> GetAllByShopid(int shopid){

        return goodsSearchRepository.findByShopid(shopid);
    }


    public List<GoodsBasic> GetGoodsOnlineApply(){

        return goodsSearchRepository.findByState(Goods.GOODS_STATE_APPLY);
    }




    public List<GoodsBasic> searchByName(String name)
    {
        name = name + "%";
        List<GoodsBasic> goodsList = goodsSearchRepository.findByGoodsnameLike(name);
        return  goodsList;
    };


    public void UpdateOrCreate(Goods  goods){goodsRepository.save(goods);}
}
