package com.ziyoushenghuo.entry;





import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_goods")
public class OrderGoods {

    @Id
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "goods_price")
    private double goods_price;

    @Column(name = "goodtitle")
    private String goodtitle;

    @Column(name = "goodposter")
    private String goodposter;

    @Column(name = "buysum",nullable = false)
    private int buysum;

    @Column(name = "goodid")
    private int goodId;
    @Column(name = "group_number")

    private int group_number;


    @Column(name = "skuid")
    private int skuid;


    @Column(name = "skuname")
    private String skuname;



    @Column(name = "goodsname")
    private String goodsname;


    public int getGroup_number() {
        return group_number;
    }

    public void setGroup_number(int group_number) {
        this.group_number = group_number;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(double goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoodtitle() {
        return goodtitle;
    }

    public void setGoodtitle(String goodtitle) {
        this.goodtitle = goodtitle;
    }

    public String getGoodposter() {
        return goodposter;
    }

    public void setGoodposter(String goodposter) {
        this.goodposter = goodposter;
    }

    public int getBuysum() {
        return buysum;
    }

    public void setBuysum(int buysum) {
        this.buysum = buysum;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }


    public int getSkuid() {
        return skuid;
    }

    public void setSkuid(int skuid) {
        this.skuid = skuid;
    }

    public String getSkuname() {
        return skuname;
    }

    public void setSkuname(String skuname) {
        this.skuname = skuname;
    }


    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }
}
