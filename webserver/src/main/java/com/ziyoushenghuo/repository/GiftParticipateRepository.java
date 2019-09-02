package com.ziyoushenghuo.repository;



import com.ziyoushenghuo.entry.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface GiftParticipateRepository extends JpaRepository<GiftParticipate,Long> {

    GiftParticipate findById(int  id);

    @Query(value="select id from GiftParticipate gp where gp.giftid=?1 and gp.status =?2")
    List<Integer> findAllGiftIds(int giftid,int status);

    List<GiftParticipate> findAllByGiftidAndStatus(int id,int status);

    GiftParticipate findByUseridAndGiftid(int userid,int giftid);
    List<GiftParticipate> findAllByIdIn(List<Integer> ids);


    void deleteInBatch(Iterable<GiftParticipate> iterable);

    List<GiftParticipate> findAllByUserid(int userid, Pageable pageable);



    List<GiftParticipate> findAllByGiftidInAndUserid(List<Integer> ids,int userid);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update GiftParticipate gp set gp.result = :result where gp.id in (:ids)")
    int updatePrizeWinners(@Param("result") int result ,@Param("ids") List<Integer> ids);

    int countByGiftid(int giftid);

    int countByGiftidAndStatus(int giftid,int status);
}