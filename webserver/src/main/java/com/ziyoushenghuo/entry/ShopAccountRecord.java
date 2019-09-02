package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_shop_account_records")
public class ShopAccountRecord extends BasicMessage {

    public static final int OP_TYPE_PROFIT = 1; //推广或者自己消费

    public static final int OP_TYPE_WITHDREW = 3; // 提现


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "shop_id", nullable = false)
    private int shopid;


    @Column(name = "money",nullable = false)
    private double money; //当前余额

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "refid", nullable = false)
    private long refid;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "balance",nullable = false)
    private double balance; //当前余额

    @Column(name = "create_time")
    private Date createtime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "remark",nullable = false)
    private String remark; //当前余额

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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRefid() {
        return refid;
    }

    public void setRefid(long refid) {
        this.refid = refid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
