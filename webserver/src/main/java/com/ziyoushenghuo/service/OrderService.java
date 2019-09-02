package com.ziyoushenghuo.service;


import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.OrderAddr;
import com.ziyoushenghuo.entry.OrderExtra;
import com.ziyoushenghuo.repository.*;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RXJ on 2016/9/9.
 * 基本用户
 */
@Service

public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderExtraRepository orderExtraRepository;

    @Autowired
    private OrderExpressRepository orderExpressRepository;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private OrderShopRepository orderShopRepository;

    @Autowired
    private OrderAddrRepository orderAddrRepository;


    /**
     * 获取待验证用户信息
     */
    public Order getOrderById(long id){
        return orderRepository.findById(id);
    }

    public Order getOrderByOuttradeno(String outno){
        return orderRepository.findByOuttradeno(outno);
    }


    public List<Order> getUserToPayOrders(int userid, int page, int size){

        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);

        List<Integer> integers = new ArrayList<>();
        integers.add(Order.ORDER_STATUS_TOPAY);
        integers.add(Order.ORDER_STATUS_UNPAY);
        return orderRepository.findByBuyeridAndOrderStatusIn(userid, integers,pageable);
    }


    public List<Order> getShopFinishOrders(int shopid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Integer> integers = new ArrayList<>();

        integers.add(Order.ORDER_STATUS_TOCOMMENT);

        integers.add(Order.ORDER_STATUS_FINISH);
        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByShopIdAndOrderStatusIn(shopid, integers,pageable);
    }

    public List<Order> getReceiveGiftOrders(int userid , int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Integer> integers = new ArrayList<>();
        integers.add(Order.ORDER_STATUS_TOSHIP);
        integers.add(Order.ORDER_STATUS_TORECEIVE);
        integers.add(Order.ORDER_STATUS_TOCOMMENT);
        integers.add(Order.ORDER_STATUS_FINISH);
        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByGiveridAndOrderStatusIn(userid, integers,pageable);
    }

    public List<Order> getSendGiftOrders(int userid , int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Integer> integers = new ArrayList<>();
        integers.add(Order.ORDER_STATUS_TOSHIP);
        integers.add(Order.ORDER_STATUS_TORECEIVE);
        integers.add(Order.ORDER_STATUS_TOCOMMENT);
        integers.add(Order.ORDER_STATUS_TOGIFT);
        integers.add(Order.ORDER_STATUS_FINISH);
        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByBuyeridAndOrderStatusInAndOrdertype(userid, integers,1,pageable);
    }


    public List<Order> getLotteryOrders(int userid , int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Integer> integers = new ArrayList<>();
        integers.add(Order.ORDER_STATUS_TOSHIP);
        integers.add(Order.ORDER_STATUS_TORECEIVE);
        integers.add(Order.ORDER_STATUS_TOCOMMENT);

        integers.add(Order.ORDER_STATUS_FINISH);
        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByBuyeridAndOrderStatusInAndOrdertype(userid, integers,Order.ORDER_TYPE_LOTTERY,pageable);
    }








    public List<Order> getUserAllOrders(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);

        List<Order> orders =  orderRepository.findByBuyerid(userid,pageable);

        return  orders;
    }

    public List<Order> getUserAllGroupOrders(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndGroupbuy(userid,1,pageable);
    }

    public List<Order> getShopToShipOrders(int shopid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByShopIdAndOrderStatus(shopid, Order.ORDER_STATUS_TOSHIP,pageable);
    }

    public List<Order> getShopShipingOrders(int shopid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByShopIdAndOrderStatus(shopid, Order.ORDER_STATUS_TORECEIVE,pageable);
    }

    public List<Order> getUserToShipOrders(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndOrderStatusAndOrdertype(userid, Order.ORDER_STATUS_TOSHIP,0,pageable);
    }


    public List<Order> getUserToReceiveOrders(int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndOrderStatusAndOrdertype(userid, Order.ORDER_STATUS_TORECEIVE,0,pageable);
    }

    public List<Order> getUserFinishOrders(int userid, int page, int size){
        // tocomment and refund
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        List<Integer> integers = new ArrayList<>();

        integers.add(Order.ORDER_STATUS_TOCOMMENT);
        integers.add(Order.ORDER_STATUS_FINISH);

        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByBuyeridAndOrderStatusInAndOrdertype(userid, integers,0,pageable);
    }


    public List<Order> getShopOrders(int shopid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);

        List<Integer> integers = new ArrayList<>();
        integers.add(Order.ORDER_STATUS_TOSHIP);
        integers.add(Order.ORDER_STATUS_TORECEIVE);
        integers.add(Order.ORDER_STATUS_TOCOMMENT);

        integers.add(Order.ORDER_STATUS_FINISH);
        integers.add(Order.ORDER_STATUS_REFUND);
        return orderRepository.findByShopIdAndOrderStatusIn(shopid, integers,pageable);
    }

    public List<Order> getByGroupId(int groupid){
        return orderRepository.findByGroupOrderId(groupid);
    }


    public List<Order> getUserToCommentOrders (int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndOrderStatusAndOrdertype(userid, Order.ORDER_STATUS_FINISH,0,pageable);
    }

    public List<Order> getUserRefundOrders (int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndOrderStatusAndOrdertype(userid, Order.ORDER_STATUS_REFUND,0,pageable);
    }

    public List<Order> getUserToGroupOrders (int userid, int page, int size){
        Sort sort = new Sort(Sort.Direction.DESC,"id");

        Pageable pageable = new PageRequest(page,size,sort);
        return orderRepository.findByBuyeridAndOrderStatus(userid, Order.ORDER_STATUS_TOGROUP,pageable);
    }

    public  int getUnPayCount(int userid)
    {
        return orderRepository.countByBuyeridAndOrderStatus(userid,Order.ORDER_STATUS_TOPAY);
    }

    public  int getUnGroupCount(int userid)
    {
        return orderRepository.countByBuyeridAndOrderStatus(userid,Order.ORDER_STATUS_TOGROUP);
    }

    public  int getUnShipCount(int userid)
    {
        return orderRepository.countByBuyeridAndOrderStatus(userid,Order.ORDER_STATUS_TOSHIP);
    }


    public  int getUnReceiveCount(int userid)
    {
        return orderRepository.countByBuyeridAndOrderStatus(userid,Order.ORDER_STATUS_TORECEIVE);
    }


    public void Create(Order  order)
    {
        orderRepository.save(order);
    }
    public void UpdateOrder(Order  order){orderRepository.save(order);}




    public void Delete(Order  order)
    {
        orderRepository.delete(order);
    }


    public void DeleteAllInterrelated(Order  order)
    {
        try
        {
            orderRepository.delete(order);
        }
        catch (Exception e)
        {

        }

        try
        {
            orderAddrRepository.delete((long)order.getId());
        }
        catch (Exception e)
        {

        }

        try
        {
            orderExtraRepository.delete((long)order.getId());
        }
        catch (Exception e)
        {

        }

        try
        {
            orderShopRepository.delete((long)order.getId());
        }
        catch (Exception e)
        {

        }

        try
        {
            orderGoodsRepository.delete((long)order.getId());
        }
        catch (Exception e)
        {

        }

        try
        {
            orderExpressRepository.delete((long)order.getId());
        }
        catch (Exception e)
        {

        }

    }


    public List<Long> findOrderIdByGroupUser(int groupid,int userid){
        return  orderRepository.findOrderIdByGroupUser(groupid,userid);
    }



}
