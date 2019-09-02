package com.ziyoushenghuo.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import com.ziyoushenghuo.entry.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by RXJ on 2017/4/5.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {


    @Override
    List<Order> findAll();



    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    Order findById(long id);
    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    Order findByOuttradeno(String outno);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndOrderStatusAndOrdertype(int buyid,int status,int type, Pageable pageable);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndOrderStatus(int buyid,int status, Pageable pageable);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndOrderStatusIn(int buyid,List<Integer>statuses, Pageable pageable);

    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndOrderStatusInAndOrdertype(int buyid,List<Integer>statuses, int type,Pageable pageable);

    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndOrderStatusInAndOrdertypeIn(int buyid,List<Integer>statuses, List<Integer>types,Pageable pageable);

    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyeridAndGroupbuy(int buyid ,int groupbuy, Pageable pageable);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByBuyerid(int buyid, Pageable pageable);
    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByGroupOrderId(int groupid);


    int countByBuyeridAndOrderStatus(int buyid,int status);
    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByShopIdAndOrderStatus(int shopid,int status, Pageable pageable);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByGiveridAndOrderStatusIn(int giverid,List<Integer>statuses, Pageable pageable);


    @Query(value="select id from Order order where order.groupOrderId=?1 and order.buyerid=?2")
    List<Long> findOrderIdByGroupUser(int groupid,int userid);


    @EntityGraph(value="order.all",type= EntityGraph.EntityGraphType.FETCH)
    List<Order> findByShopIdAndOrderStatusIn(int shopid,List<Integer>statuses, Pageable pageable);




}