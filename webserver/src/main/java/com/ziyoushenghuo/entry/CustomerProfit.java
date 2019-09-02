package com.ziyoushenghuo.entry;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_customer_account_proceeds_records")
public class CustomerProfit {


    //暂时不用
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "c_id",nullable = true)
    private int cid;

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

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
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
}
