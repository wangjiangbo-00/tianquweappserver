package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods_edit")
public class GoodsEdit extends BasicMessage {


    public static final int REVIEW_STATUS_NOMAL = 0;
    public static final int REVIEW_STATUS_OK = 1;
    public static final int REVIEW_STATUS_REFUSE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "goods_id",nullable = false)
    private int goodsid;

    @Column(name = "category_id",nullable = false)
    private int catid;



    @Column(name = "code",nullable = false)
    private String goodslicense;

    @Column(name = "goods_name",nullable = false)
    private String goodsname;


    @Column(name = "img_id_array",nullable = false)
    private String imgarr;


    @Column(name = "goods_weight",nullable = false)
    private int weight;


    @Column(name = "market_price",nullable = false)
    private double marketprice;

    @Column(name = "group_price",nullable = false)
    private double group_price;

    @Column(name = "price",nullable = false)
    private double price;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "introduction",nullable = false)
    private String introduction;


    @Column(name = "create_time",nullable = false)
    private Date createtime;



    @OneToOne
    @JoinColumn(name="picture")
    private  GoodImage image;


    @Column(name = "sort",nullable = false)
    private int sort;



    @Column(name = "opengroup",nullable = false)
    private int opengroup;

    @Column(name = "shop_id",nullable = false)
    private int shopid;

    @Column(name = "group_number",nullable = false)
    private int group_number;

    @Column(name = "shipping_fee_id",nullable = false)
    private int feeid;

    @Column(name = "edit_type",nullable = false)
    private int edittype;

    @Column(name = "submit_status",nullable = false)
    private int submitstatus;

    @Column(name = "review_status",nullable = false)
    private int reviewstatus;

    @Column(name = "refusemsg",nullable = true)
    private String refusemsg;

    public int getFeeid() {
        return feeid;
    }

    public void setFeeid(int feeid) {
        this.feeid = feeid;
    }

    @Transient
    private List<String> gallery;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getGoodslicense() {
        return goodslicense;
    }

    public void setGoodslicense(String goodslicense) {
        this.goodslicense = goodslicense;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(double marketprice) {
        this.marketprice = marketprice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public GoodImage getImage() {
        return image;
    }

    public void setImage(GoodImage image) {
        this.image = image;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }



    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }


    public String getImgarr() {
        return imgarr;
    }

    public void setImgarr(String imgarr) {
        this.imgarr = imgarr;
    }

    public double getGroup_price() {
        return group_price;
    }

    public void setGroup_price(double group_price) {
        this.group_price = group_price;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public int getOpengroup() {
        return opengroup;
    }

    public void setOpengroup(int opengroup) {
        this.opengroup = opengroup;
    }

    public int getGroup_number() {
        return group_number;
    }

    public void setGroup_number(int group_number) {
        this.group_number = group_number;
    }

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }




    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public int getEdittype() {
        return edittype;
    }

    public void setEdittype(int edittype) {
        this.edittype = edittype;
    }

    public int getSubmitstatus() {
        return submitstatus;
    }

    public void setSubmitstatus(int submitstatus) {
        this.submitstatus = submitstatus;
    }

    public int getReviewstatus() {
        return reviewstatus;
    }

    public void setReviewstatus(int reviewstatus) {
        this.reviewstatus = reviewstatus;
    }

    public String getRefusemsg() {
        return refusemsg;
    }

    public void setRefusemsg(String refusemsg) {
        this.refusemsg = refusemsg;
    }
}
