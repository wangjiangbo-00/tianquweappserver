package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.Shop;
import com.ziyoushenghuo.entry.TeamFollower;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface TeamFollowerRepository extends JpaRepository<TeamFollower,Long> {
    List<TeamFollower> findByTeamid(int teamid);

    List<TeamFollower> findByFoundid(int foundid);


    List<TeamFollower> findByFollowUserid(int userid);

    TeamFollower findById(int  id);

}