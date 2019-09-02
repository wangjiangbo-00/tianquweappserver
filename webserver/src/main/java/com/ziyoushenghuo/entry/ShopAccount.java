package com.ziyoushenghuo.entry;





import com.fasterxml.jackson.annotation.JsonInclude;
import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_shop_account")
public class ShopAccount extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private int shopid;

    @Column(name = "shop_profit",nullable = false)
    private double shop_profit; // 销售额

    @Column(name = "shop_total_money",nullable = false)
    private double shop_total_money; //当前余额

    @Column(name = "shop_total_money_lock",nullable = false)
    private double shop_total_money_lock; //提现过程金额

    @Column(name = "shop_proceeds",nullable = false)
    private double shop_proceeds; //去除微信和平台扣费后的金额

    @Column(name = "shop_platform_commission",nullable = false)
    private double shop_platform_commission;

    @Column(name = "shop_withdraw",nullable = false)
    private double shop_withdraw;

    @Column(name = "shop_weixin_commission",nullable = false)
    private double shop_weixin_commission;


    @Column(name = "dayincome",nullable = false)
    private double dayincome;

    @Column(name = "dayordercount", nullable = false)
    private int dayordercount;

    @Column(name = "monthincome",nullable = false)
    private double monthincome;

    @Column(name = "monthordercount", nullable = false)
    private int monthordercount;



    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public double getShop_profit() {
        return shop_profit;
    }

    public void setShop_profit(double shop_profit) {
        this.shop_profit = shop_profit;
    }

    public double getShop_total_money() {
        return shop_total_money;
    }

    public void setShop_total_money(double shop_total_money) {
        this.shop_total_money = shop_total_money;
    }

    public double getShop_proceeds() {
        return shop_proceeds;
    }

    public void setShop_proceeds(double shop_proceeds) {
        this.shop_proceeds = shop_proceeds;
    }

    public double getShop_platform_commission() {
        return shop_platform_commission;
    }

    public void setShop_platform_commission(double shop_platform_commission) {
        this.shop_platform_commission = shop_platform_commission;
    }

    public double getShop_withdraw() {
        return shop_withdraw;
    }

    public void setShop_withdraw(double shop_withdraw) {
        this.shop_withdraw = shop_withdraw;
    }

    public double getShop_weixin_commission() {
        return shop_weixin_commission;
    }

    public void setShop_weixin_commission(double shop_weixin_commission) {
        this.shop_weixin_commission = shop_weixin_commission;
    }

    public double getShop_total_money_lock() {
        return shop_total_money_lock;
    }

    public void setShop_total_money_lock(double shop_total_money_lock) {
        this.shop_total_money_lock = shop_total_money_lock;
    }

    public double getDayincome() {
        return dayincome;
    }

    public void setDayincome(double dayincome) {
        this.dayincome = dayincome;
    }

    public int getDayordercount() {
        return dayordercount;
    }

    public void setDayordercount(int dayordercount) {
        this.dayordercount = dayordercount;
    }

    public double getMonthincome() {
        return monthincome;
    }

    public void setMonthincome(double monthincome) {
        this.monthincome = monthincome;
    }

    public int getMonthordercount() {
        return monthordercount;
    }

    public void setMonthordercount(int monthordercount) {
        this.monthordercount = monthordercount;
    }
}
