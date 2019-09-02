package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods_attribute")

public class GoodsAttribute extends BasicMessage {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attr_id", nullable = false)
    private int id;

    @Column(name = "goods_id",nullable = false)
    private int goodsid;

    @Column(name = "shop_id",nullable = false)
    private int shopid;

    @Column(name = "class_id",nullable = false)
    private int classid;

    @Column(name = "attr_value",nullable = false)
    private String attr_value;

    @Column(name = "attr_value_name",nullable = false)
    private String attr_value_name;



    @Column(name = "sort",nullable = false)
    private int sort;

    @Column(name = "create_time",nullable = false)
    private Date createtime;

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

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getAttr_value_name() {
        return attr_value_name;
    }

    public void setAttr_value_name(String attr_value_name) {
        this.attr_value_name = attr_value_name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
