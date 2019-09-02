package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.RefundProcess;

import javax.persistence.Column;
import java.util.Date;

public class OrderCoverInfo extends  BasicMessage{

    private long id;
    private int buysum;
    private double ordermoney;
    private int groupOrderId;
    private Date createtime;

    private String goodtitle;
    private String goodposter;
    private String skuname;
    private int orderStatus;
    private int payStatus;
    private int finishReason;
    private int refundProcessStatus;
    private int goodId;
    private double goods_price;
    private int  ordertype;
    private int givenstatus;

    private double preshippfee;

    public void SetByOrder(Order order)
    {
        id = order.getId();
         buysum = order.getOrderGoods().getBuysum();
         ordermoney = order.getOrdermoney();
         groupOrderId = order.getGroupOrderId();
         createtime = order.getCreatetime();
         goodtitle = order.getOrderGoods().getGoodtitle();
         goodposter = order.getOrderGoods().getGoodposter();
         orderStatus = order.getOrderStatus();
         payStatus = order.getPayStatus();
        orderStatus = order.getOrderStatus();
        payStatus = order.getPayStatus();
        ordertype = order.getOrdertype();
        givenstatus = order.getGivenstatus();
        goodId = order.getOrderGoods().getGoodId();
        goods_price = order.getOrderGoods().getGoods_price();
        refundProcessStatus = order.getRefundProcessStatus();
        finishReason = order.getFinishreason();

        skuname = order.getOrderGoods().getSkuname();

        preshippfee = order.getPreshippfee();

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBuysum() {
        return buysum;
    }

    public void setBuysum(int buysum) {
        this.buysum = buysum;
    }

    public double getOrdermoney() {
        return ordermoney;
    }

    public void setOrdermoney(double ordermoney) {
        this.ordermoney = ordermoney;
    }

    public int getGroupOrderId() {
        return groupOrderId;
    }

    public void setGroupOrderId(int groupOrderId) {
        this.groupOrderId = groupOrderId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getGoodtitle() {
        return goodtitle;
    }

    public void setGoodtitle(String goodtitle) {
        this.goodtitle = goodtitle;
    }

    public String getGoodposter() {
        return goodposter;
    }

    public void setGoodposter(String goodposter) {
        this.goodposter = goodposter;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public int getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(int finishReason) {
        this.finishReason = finishReason;
    }

    public int getRefundProcessStatus() {
        return refundProcessStatus;
    }

    public void setRefundProcessStatus(int refundProcessStatus) {
        this.refundProcessStatus = refundProcessStatus;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public double getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(double goods_price) {
        this.goods_price = goods_price;
    }

    public int getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(int ordertype) {
        this.ordertype = ordertype;
    }

    public int getGivenstatus() {
        return givenstatus;
    }

    public void setGivenstatus(int givenstatus) {
        this.givenstatus = givenstatus;
    }

    public double getPreshippfee() {
        return preshippfee;
    }

    public void setPreshippfee(double preshippfee) {
        this.preshippfee = preshippfee;
    }


    public String getSkuname() {
        return skuname;
    }

    public void setSkuname(String skuname) {
        this.skuname = skuname;
    }
}
