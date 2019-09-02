package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.repository.ShopRepository;
import com.ziyoushenghuo.repository.TeamFounderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class TeamFounderService {

    @Autowired
    private TeamFounderRepository teamFounderRepository;

    /**
     * 获取待验证用户信息
     */

    public List<TeamFounder> GetByGoodsid(int goodsid){
        GoodsCover goodsCover = new GoodsCover();
        goodsCover.setId(goodsid);
        return teamFounderRepository.findByGoodsCoverAndGroupresultAndType(goodsCover,0,TeamFounder.GROUP_TYPE_USER);
    }
    public TeamFounder GetById(int id){return teamFounderRepository.findById(id);}
    public List<TeamFounder> GetPlatGroups(List<Integer>integers,Sort sort,int offset,int size){

        Pageable pageable = new PageRequest(offset,size,sort);

        return teamFounderRepository.findByTypeAndStatusIn(TeamFounder.GROUP_TYPE_PLATFORM,integers, pageable);
    }

    public void  CreateOrUpdate(TeamFounder teamFounder){
        teamFounderRepository.save(teamFounder);
    }


    public List<Integer> findPeopleById(int groupid){
        return teamFounderRepository.findPeopleById(groupid);
    }

}
