package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_customer_article")
public class CustomerArticleShort extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "cid", nullable = false)
    private int cid;

    @Column(name = "goodsid",nullable = false)
    private int goodsid;

    @Column(name = "shopid",nullable = false)
    private int shopid;

    @Column(name = "type",nullable = false)
    private int type;


    @Column(name = "modifytime")
    private Date modifytime;

    @Column(name = "ispublic",nullable = false)
    private int ispublic;

    @Column(name = "title",nullable = false)
    private String title;


    @Column(name = "coverpic",nullable = false)
    private String coverpic;



    @Column(name = "isban",nullable = false)
    private int isban;


    @Column(name = "collect",nullable = false)
    private int collect;

    @Column(name = "readcount",nullable = false)
    private int readcount;


    @Column(name = "nikename",nullable = false)
    private String nikename;

    @Column(name = "headpic",nullable = false)
    private String headpic;


    @Column(name = "bgstyle",nullable = false)
    private int bgstyle;


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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public int getIspublic() {
        return ispublic;
    }

    public void setIspublic(int ispublic) {
        this.ispublic = ispublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverpic() {
        return coverpic;
    }

    public void setCoverpic(String coverpic) {
        this.coverpic = coverpic;
    }

    public int getIsban() {
        return isban;
    }

    public void setIsban(int isban) {
        this.isban = isban;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getReadcount() {
        return readcount;
    }

    public void setReadcount(int readcount) {
        this.readcount = readcount;
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

    public int getBgstyle() {
        return bgstyle;
    }

    public void setBgstyle(int bgstyle) {
        this.bgstyle = bgstyle;
    }
}
