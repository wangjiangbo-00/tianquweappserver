package com.ziyoushenghuo.entry;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_wallet_pay")
public class WalletPay {

    public final static int WALLETPAY_TYPE_UNKNOWN = 0;
    public final static int WALLETPAY_TYPE_RECHARGE = 1;
    public final static int WALLETPAY_TYPE_PAYBILL = 2;
    public final static int WALLETPAY_TYPE_REFUND= 3;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "createtime")
    private Date createtime;

    @Column(name = "refid",nullable = true)
    private Long refid;

    @Column(name = "money_before",nullable = true)
    private double money_before;

    @Column(name = "money_after",nullable = true)
    private double money_after;

    @Column(name = "money",nullable = true)
    private double money;

    @Column(name = "type",nullable = true)
    private int type;

    @Column(name = "userid",nullable = true)
    private int userid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Long getRefid() {
        return refid;
    }

    public void setRefid(Long refid) {
        this.refid = refid;
    }

    public double getMoney_before() {
        return money_before;
    }

    public void setMoney_before(double money_before) {
        this.money_before = money_before;
    }

    public double getMoney_after() {
        return money_after;
    }

    public void setMoney_after(double money_after) {
        this.money_after = money_after;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
