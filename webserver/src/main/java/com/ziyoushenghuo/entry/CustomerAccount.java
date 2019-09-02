package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;

@Entity
@Table(name = "zytc_customer_account")
public class CustomerAccount extends BasicMessage {

    public static final int RECOMMEDN_ALLPY_CONSUME = 298;

    @Id

    @Column(name = "c_id", nullable = false)
    private int cid;

    @Column(name = "profit",nullable = false)
    private double profit; // 销售额

    @Column(name = "money",nullable = false)
    private double money; //当前余额

    @Column(name = "proceeds",nullable = false)
    private double proceeds; //去除微信和平台扣费后的金额

    @Column(name = "dayincome",nullable = false)
    private double dayincome; //去除微信和平台扣费后的金额

    @Column(name = "withdraw",nullable = false)
    private double withdraw;

    @Column(name = "money_lock",nullable = true)
    private double money_lock;

    @Column(name = "consume",nullable = true)
    private double consume;

    @Column(name = "ordercount", nullable = false)
    private int ordercount;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getProceeds() {
        return proceeds;
    }

    public void setProceeds(double proceeds) {
        this.proceeds = proceeds;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

    public double getMoney_lock() {
        return money_lock;
    }

    public void setMoney_lock(double money_lock) {
        this.money_lock = money_lock;
    }


    public double getDayincome() {
        return dayincome;
    }

    public void setDayincome(double dayincome) {
        this.dayincome = dayincome;
    }

    public double getConsume() {
        return consume;
    }

    public void setConsume(double consume) {
        this.consume = consume;
    }

    public int getOrdercount() {
        return ordercount;
    }

    public void setOrdercount(int ordercount) {
        this.ordercount = ordercount;
    }
}
