package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods_sku")

public class GoodsSku extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id", nullable = false)
    private int id;

    @Column(name = "goods_id",nullable = false)
    private int goodsid;

    @Column(name = "sku_name",nullable = false)
    private String sku_name;

    @Column(name = "attr_value_items",nullable = false)
    private String attr_value_items;

    @Column(name = "attr_value_items_format",nullable = false)
    private String attr_value_items_format;



    @Column(name = "market_price",nullable = false)
    private double marketprice;

    @Column(name = "group_price",nullable = false)
    private double group_price;

    @Column(name = "price",nullable = false)
    private double price;

    @Column(name = "stock",nullable = false)
    private int stock;



    @Column(name = "create_time",nullable = false)
    private Date createtime;

    @Column(name = "update_time",nullable = false)
    private Date update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public String getSku_name() {
        return sku_name;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
    }

    public String getAttr_value_items() {
        return attr_value_items;
    }

    public void setAttr_value_items(String attr_value_items) {
        this.attr_value_items = attr_value_items;
    }

    public String getAttr_value_items_format() {
        return attr_value_items_format;
    }

    public void setAttr_value_items_format(String attr_value_items_format) {
        this.attr_value_items_format = attr_value_items_format;
    }

    public double getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(double marketprice) {
        this.marketprice = marketprice;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
