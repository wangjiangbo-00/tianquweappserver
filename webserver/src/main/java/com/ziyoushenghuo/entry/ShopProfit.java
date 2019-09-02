package com.ziyoushenghuo.entry;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_shop_account_proceeds_records")
public class ShopProfit {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "shop_id",nullable = true)
    private int shopid;

    @Column(name = "money",nullable = true)
    private double money;

    @Column(name = "create_time")
    private Date time;

    @Column(name = "remark")
    private String remark;

    @Column(name = "order_id",nullable = true)
    private Long orderid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
