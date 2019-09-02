package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_customer_account_records")
public class CustomerAccountRecord extends BasicMessage {

    public static final int OP_TYPE_RECOMMEND_PROFIT = 1; //推广或者自己消费
    public static final int OP_TYPE_JUNIOR_CONSUME = 2; //下线分成
    public static final int OP_TYPE_WITHDREW = 3; // 提现


    public static final int OP_PROFIT_STATUS_NORMAL = 0; //推广或者自己消费
    public static final int OP_PROFIT_STATUS_OK = 1; // 收益成功
    public static final int OP_PROFIT_STATUS_NONEED = 2; // 收益退出 ，原因在extra

    public static final String OP_TYPE_RECOMMEND_PROFIT_STR = "分享返现"; //推广或者自己消费
    public static final String OP_TYPE_JUNIOR_CONSUME_STR = "消费分成"; //下线分成
    public static final String OP_TYPE_WITHDREW_STR = "提现"; // 提现
    public static final String OP_TYPE_WITHDREW_UNFINISHED_STR = "提现（在途）"; // 提现


    public static final String OP_TYPE_PROFIT_QUIT_STR = ""; // 提现
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "c_id", nullable = false)
    private int cid;

    @Column(name = "money",nullable = false)
    private double money; //当前余额

    @Column(name = "type", nullable = false)
    private int type;


    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "refid", nullable = false)
    private long refid;

    @Column(name = "balance",nullable = false)
    private double balance; //店铺可用余额

    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "remark",nullable = false)
    private String remark; //描述


    @Column(name = "extra",nullable = false)
    private String extra; //描述

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
