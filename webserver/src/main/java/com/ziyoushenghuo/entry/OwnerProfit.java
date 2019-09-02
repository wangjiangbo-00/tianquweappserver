package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_shop_account_proceeds_records")
public class OwnerProfit  extends BasicMessage {

    public final static int PROFIT_TYPE_UNKNOWN = 0;
    public final static int PROFIT_TYPE_CHARGE_ORDER = 1;
    public final static int PROFIT_TYPE_WITHDREW = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "create_time")
    private Date create_time;

    @Column(name = "shop_id",nullable = true)
    private int shopid;

    @Column(name = "money",nullable = true)
    private double money;


    @Column(name = "order_id",nullable = true)
    private int orderid;


    @Column(name = "remark",nullable = true)
    private String remark;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
