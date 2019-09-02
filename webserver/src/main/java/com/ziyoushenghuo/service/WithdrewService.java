package com.ziyoushenghuo.service;



import com.ziyoushenghuo.entry.Withdrew;


import com.ziyoushenghuo.repository.WithdrewRepository;
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

public class WithdrewService {

    @Autowired
    private WithdrewRepository withdrewRepository;

    /**
     * 获取待验证用户信息
     */
    public Withdrew getById(int id){
        return withdrewRepository.findById(id);
    }
    public List<Withdrew>  getByShopid(int shopid){
        return withdrewRepository.findByShopid(shopid);
    }

    public List<Withdrew>  getRecentsByShopid(int shopid, int page, int size){

        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return withdrewRepository.findByShopid(shopid);
    }


    public List<Withdrew>  getRecentsByStatus(int status, int page, int size){

        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return withdrewRepository.findByStatus(status,pageable);
    }

    public void createOrUpdate(Withdrew  withdrew){withdrewRepository.save(withdrew);}

    public void delete(Withdrew withdrew){withdrewRepository.delete(withdrew);}
}
