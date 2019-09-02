package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_shop_account_withdraw_records")
public class Withdrew extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "shop_id",nullable = false)
    private int shopid;

    @Column(name = "create_time",nullable = false)
    private Date create_time;

    @Column(name = "money",nullable = false)
    private double money;

    @Column(name = "remark",nullable = true)
    private String remark;

    @Column(name = "status",nullable = false)
    private int status;

    @Column(name = "shopname",nullable = true)
    private String shopname;

    @Column(name = "ownername",nullable = true)
    private String ownername;

    @Column(name = "ownerphone",nullable = true)
    private String ownerphone;

    @Column(name = "openid",nullable = true)
    private String openid;


    @Column(name = "wxpayid",nullable = true)
    private String wxpayid;


    @Column(name = "failstr",nullable = true)
    private String failstr;


    @Column(name = "errcode",nullable = true)
    private String errcode;

    public String getWxpayid() {
        return wxpayid;
    }

    public void setWxpayid(String wxpayid) {
        this.wxpayid = wxpayid;
    }

    public String getFailstr() {
        return failstr;
    }

    public void setFailstr(String failstr) {
        this.failstr = failstr;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public Date getWithdrewtime() {
        return withdrewtime;
    }

    public void setWithdrewtime(Date withdrewtime) {
        this.withdrewtime = withdrewtime;
    }

    @Column(name = "formid",nullable = true)
    private String formid;

    @Column(name = "withdrewtime",nullable = false)
    private Date withdrewtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnerphone() {
        return ownerphone;
    }

    public void setOwnerphone(String ownerphone) {
        this.ownerphone = ownerphone;
    }
}
