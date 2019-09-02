package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.TeamActivity;
import com.ziyoushenghuo.repository.ShopRepository;
import com.ziyoushenghuo.repository.TeamActivityRepository;
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

public class TeamActivityService {

    @Autowired
    private TeamActivityRepository teamActivityRepository;

    /**
     * 获取待验证用户信息
     */

    public TeamActivity GetByGoodsId(int goodid){return teamActivityRepository.findByGoodsid(goodid);}
    public TeamActivity GetById(int id){return teamActivityRepository.findById(id);}



}
