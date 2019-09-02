package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "zytc_order_goods_express")
public class OrderExpress extends BasicMessage{


    public static final int ORDER_SHIP_TYPE_EXPRESS = 0;
    public static final int ORDER_SHIP_TYPE_SELFDELIVERY = 1;
    public static final int ORDER_SHIP_TYPE_USERFETCH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;



    @Column(name = "order_id")
    private Long orderid;

    @Column(name = "express_name")
    private String expressname;



    @Column(name = "shipping_type")
    private int shippingtype;

    @Column(name = "express_company_id")
    private int companyid;


    @Column(name = "express_company")
    private String expresscompany;

    @Column(name = "express_no")
    private String expressno;

    @Column(name = "shipping_time")
    private Date shippingtime;

    @Column(name = "uid")
    private int uid;

    @Column(name = "user_name")
    private String username;

    @Column(name = "memo")
    private String memo;

    @Column(name = "message")
    private String message;


    @Column(name = "errmessage")
    private String errmessage;

    @Column(name = "company_code")
    private String companycode;

    @Column(name = "update_time")
    private Date updatetime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getExpressname() {
        return expressname;
    }

    public void setExpressname(String expressname) {
        this.expressname = expressname;
    }

    public int getShippingtype() {
        return shippingtype;
    }

    public void setShippingtype(int shippingtype) {
        this.shippingtype = shippingtype;
    }

    public int getCompanyid() {
        return companyid;
    }

    public void setCompanyid(int companyid) {
        this.companyid = companyid;
    }

    public String getExpresscompany() {
        return expresscompany;
    }

    public void setExpresscompany(String expresscompany) {
        this.expresscompany = expresscompany;
    }

    public String getExpressno() {
        return expressno;
    }

    public void setExpressno(String expressno) {
        this.expressno = expressno;
    }

    public Date getShippingtime() {
        return shippingtime;
    }

    public void setShippingtime(Date shippingtime) {
        this.shippingtime = shippingtime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompanycode() {
        return companycode;
    }

    public void setCompanycode(String companycode) {
        this.companycode = companycode;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }


    public String getErrmessage() {
        return errmessage;
    }

    public void setErrmessage(String errmessage) {
        this.errmessage = errmessage;
    }
}
