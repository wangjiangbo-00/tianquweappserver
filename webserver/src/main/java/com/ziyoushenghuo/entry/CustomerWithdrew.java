package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_customer_account_withdraw_records")
public class CustomerWithdrew extends BasicMessage {


    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_OK= 1;
    public static final int STATUS_FAIL= 2;

    public static final int STATUS_CANCAL= 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "c_id",nullable = false)
    private int cid;

    @Column(name = "money",nullable = false)
    private double money;

    @Column(name = "status",nullable = false)
    private int status;

    @Column(name = "create_time",nullable = false)
    private Date create_time;

    @Column(name = "remark",nullable = true)
    private String remark;



    @Column(name = "openid",nullable = true)
    private String openid;

    @Column(name = "ownername",nullable = true)
    private String ownername;

    @Column(name = "wxpayid",nullable = true)
    private String wxpayid;


    @Column(name = "failstr",nullable = true)
    private String failstr;


    @Column(name = "errcode",nullable = true)
    private String errcode;


    @Column(name = "formid",nullable = true)
    private String formid;

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    @Column(name = "withdrewtime",nullable = false)
    private Date withdrewtime;

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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Date getWithdrewtime() {
        return withdrewtime;
    }

    public void setWithdrewtime(Date withdrewtime) {
        this.withdrewtime = withdrewtime;
    }
}
