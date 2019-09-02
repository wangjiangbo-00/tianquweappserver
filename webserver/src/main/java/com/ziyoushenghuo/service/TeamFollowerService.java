package com.ziyoushenghuo.service;

import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.TeamFollower;
import com.ziyoushenghuo.repository.ShopRepository;
import com.ziyoushenghuo.repository.TeamFollowerRepository;
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

public class TeamFollowerService {

    @Autowired
    private TeamFollowerRepository teamFollowerRepository;

    /**
     * 获取待验证用户信息
     */

    public List<TeamFollower> GetAllByTeamId(int teamid){return teamFollowerRepository.findByTeamid(teamid);}

    public List<TeamFollower> GetAllByFoundId(int foundid){return teamFollowerRepository.findByFoundid(foundid);}

    public List<TeamFollower> GetAllByFollowUserid(int userid){return teamFollowerRepository.findByFollowUserid(userid);}
    public TeamFollower GetById(int id){return teamFollowerRepository.findById(id);}



}
