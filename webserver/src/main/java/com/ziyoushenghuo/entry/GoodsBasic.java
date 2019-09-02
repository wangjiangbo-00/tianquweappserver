package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods")
public class GoodsBasic extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id", nullable = false)
    private int id;

    @Column(name = "goods_name",nullable = false)
    private String goodsname;

    @Column(name = "group_price",nullable = false)
    private double group_price;

    @Column(name = "price",nullable = false)
    private double price;


    @Column(name = "pictureurl",nullable = true)
    private String pictureurl;

    @Column(name = "bannerurl",nullable = true)
    private String bannerurl;

    @Column(name = "type",nullable = false)
    private int type;



    @Column(name = "group_number",nullable = false)
    private int group_number;


    @Column(name = "shop_state",nullable = false)
    private int shopstate;

    @Column(name = "state",nullable = false)
    private int state;

    @Column(name = "category_id",nullable = false)
    private int catid;

    @Column(name = "introduction",nullable = false)
    private String introduction;


    @Column(name = "shop_id",nullable = false)
    private int shopid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public double getGroup_price() {
        return group_price;
    }

    public void setGroup_price(double group_price) {
        this.group_price = group_price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    public int getGroup_number() {
        return group_number;
    }

    public void setGroup_number(int group_number) {
        this.group_number = group_number;
    }


    public int getShopstate() {
        return shopstate;
    }

    public void setShopstate(int shopstate) {
        this.shopstate = shopstate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getBannerurl() {
        return bannerurl;
    }

    public void setBannerurl(String bannerurl) {
        this.bannerurl = bannerurl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
