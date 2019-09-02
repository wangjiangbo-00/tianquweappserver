package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_free_orders")
public class FreeOrder extends BasicMessage {


    //暂时不用
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "orderid",nullable = true)
    private int orderid;


    @Column(name = "userid",nullable = true)
    private int userid;


    @Column(name = "goodsid",nullable = true)
    private int goodsid;

    @Column(name = "goodsname")
    private String goodsname;

    @Column(name = "money",nullable = true)
    private double money;

    @Column(name = "num",nullable = true)
    private int num;


    @Column(name = "goodspic")
    private String goodspic;

    @Column(name = "create_time")
    private Date time;


    @Column(name = "nikename")
    private String nikename;

    @Column(name = "headpic")
    private String headpic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGoodspic() {
        return goodspic;
    }

    public void setGoodspic(String goodspic) {
        this.goodspic = goodspic;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }
}
