package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface TeamFounderRepository extends JpaRepository<TeamFounder,Long> {

    @EntityGraph(value="group.all",type= EntityGraph.EntityGraphType.FETCH)
    TeamFounder findById(int id);


    @EntityGraph(value="group.all",type= EntityGraph.EntityGraphType.FETCH)
    List<TeamFounder> findByGoodsCoverAndGroupresultAndType(GoodsCover goodsCover, int result, int type);

    @EntityGraph(value="group.all",type= EntityGraph.EntityGraphType.FETCH)
    List<TeamFounder> findByTypeAndStatusIn(int type,List<Integer>statuses,  Pageable pageable);


    @Query(value="select people from TeamFounder teamfounder where teamfounder.id=?1")
    List<Integer> findPeopleById(int groupid);


    @EntityGraph(value="group.all",type= EntityGraph.EntityGraphType.FETCH)
    List<TeamFounder> findByTypeAndStatus(int type,int status,  Pageable pageable);


    @EntityGraph(value="group.all",type= EntityGraph.EntityGraphType.FETCH)
    List<TeamFounder> findByTypeAndShopCover(int type, ShopCover shopCover, Pageable pageable);

}