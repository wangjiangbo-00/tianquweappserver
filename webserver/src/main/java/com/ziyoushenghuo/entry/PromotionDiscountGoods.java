package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_promotion_discount_goods")

public class PromotionDiscountGoods extends BasicMessage{


    public static final int STATUS_VOID = 0;
    public static final int STATUS_APPLY = 1;
    public static final int STATUS_APPLY_OK = 2;
    public static final int STATUS_APPLY_FAIL = 3;
    public static final int STATUS_CAN_SHOW = 4;
    public static final int STATUS_START = 5;
    public static final int STATUS_END = 6; //等待收礼者填写
    public static final int STATUS_CLOSE = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_goods_id", nullable = false)
    private int id;

    @Column(name = "discount_id")
    private int discountid;


    @OneToOne
    @JoinColumn(name="goods_id")
    private  GoodsCover goodsCover;


    @Column(name = "goods_name")
    private String goods_name;



    @Column(name = "start_time")
    private Date starttime;


    @Column(name = "end_time")
    private Date endtime;

    @Column(name = "status")
    private int status;


    @Column(name = "discount")
    private double discount;

    @Column(name = "goods_picture")
    private int goods_picture;

    @Column(name = "pid")
    private int pid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscountid() {
        return discountid;
    }

    public void setDiscountid(int discountid) {
        this.discountid = discountid;
    }


    public GoodsCover getGoodsCover() {
        return goodsCover;
    }

    public void setGoodsCover(GoodsCover goodsCover) {
        this.goodsCover = goodsCover;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getGoods_picture() {
        return goods_picture;
    }

    public void setGoods_picture(int goods_picture) {
        this.goods_picture = goods_picture;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }


    public PromotionDiscountGoods(){};


    public PromotionDiscountGoods(int id,double discount,Date starttime,Date endtime,int status){
        this.id = id;
        this.discount = discount;
        this.starttime = starttime;
        this.endtime = endtime;
        this.status = status;

    };
}
