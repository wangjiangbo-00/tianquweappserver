package com.ziyoushenghuo.repository;

import com.ziyoushenghuo.entry.Customer;
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
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findByWeixin(String weixin);
    Customer findByPhone(String phone);

    Customer findByToken(String token);

    @Query(value = "select b from Customer b where b.id = :id ")
    Customer findByIdParam(@Param("id") int id);


    @Query(value = "select * from zytc_customers b where b.id=?1", nativeQuery = true)
    Customer findByIdParamN( int id);


    @Query(value = "select new Customer(b.id,b.nickname) from Customer b where b.id = :id ")
    Customer findByIdParamPart(@Param("id") int id);


    @Query(value = "select new Customer(b.id,b.configflag) from Customer b where b.token = :token ")
    Customer findFlagByToken(@Param("token") String token);


    @Query(value = "select new Customer(b.id,b.nickname,b.headpic,b.weixin) from Customer b where b.id in (:ids) ")
    List<Customer> findUserPicByIds(@Param("ids") List<Integer> ids);



    @Query(value = "select new Customer(b.id,b.nickname,b.headpic,b.updatetime) from Customer b where b.recommender = :userid and b.headpic <> NULL ")
    List<Customer> findRecommendPersons(@Param("userid") int userid, Pageable pageable);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update Customer zc set zc.nickname=?1 where zc.id=?2")
    int updateUsernameById(String username,int id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update zytc_customers  set nickname=?1 where id=?2", nativeQuery = true)
    int updateUsernameByIdN(String username,int id);

    Customer findById(int id);

    @Query(value = "select new Customer(b.id,b.nickname,b.headpic) from Customer b where b.id = :id ")
    Customer findCoverById(@Param("id") int id);


    @Query(value = "select new Customer(b.id,b.nickname) from Customer b where b.id = :id ")
    Customer findNameById(@Param("id") int id);


    @Query(value = "select new Customer(b.id,b.isrecommender,b.recommender) from Customer b where b.id = :id ")
    Customer findRecommendinfoById(@Param("id") int id);


    @Query(value = "select new Customer(b.id,b.nickname,b.headpic,b.weixin) from Customer b where b.id = :id ")
    Customer findCoverAndOpenidById(@Param("id") int id);


    @Query(value = "select new Customer(b.weixin) from Customer b where b.id = :id ")
    Customer findOpenIdById(@Param("id") int id);
}