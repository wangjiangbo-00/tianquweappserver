package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_refund_process")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "refundprocess.all",
                attributeNodes = {//attributeNodes 来定义需要懒加载的属性
                         @NamedAttributeNode("orderGoods")

                }
        )
})
public class RefundProcess extends BasicMessage{

    public static final int REFUND_PROCESS_NOMAL = 0;
    public static final int REFUND_PROCESS_SELLER_NOREPLY = 1;
    public static final int REFUND_PROCESS_SELLER_ACCEPT= 2;
    public static final int REFUND_PROCESS_SELLER_REFUSE = 3; //
    public static final int REFUND_PROCESS_SELLER_OVERTIME = 4; //
    public static final int REFUND_PROCESS_SELLER_RECEIVE= 5; //
    public static final int REFUND_PROCESS_STATUS_REFUNDING= 6; //
    public static final int REFUND_PROCESS_REFUND_DONE= 7; //
    public static final int REFUND_PROCESS_PLATFORM_IN= 8; //



    public static final int REFUND_PROCESS_TYPE_ONLYMONEY= 0; //
    public static final int REFUND_PROCESS_TYPE_NEEDSENDBACK= 1; //


    public static final int REFUND_SENDBACK_NOMAL = 0;
    public static final int REFUND_SENDBACK_TORECEIVE = 1;
    public static final int REFUND_SENDBACK_RECEIVED= 2;
    public static final int REFUND_SENDBACK_OVERTIME= 3;


    public static final int REFUND_REASON_GOODS_BAD = 0;
    public static final int REFUND_REASON_WRONG_GOODS = 1;
    public static final int REFUND_REASON_GOODS_BREAK= 2;


    @Id
    @Column(name = "orderid",nullable = true)
    private long orderid;




    @Column(name = "applymsg",nullable = true)
    private String applymsg;

    @Column(name = "applypics",nullable = true)
    private String applypics;

    @Column(name = "receiveid",nullable = true)
    private int receiveid;


    @Column(name = "buyid",nullable = true)
    private int buyid;

    @Column(name = "applyreason",nullable = true)
    private int applyreason;


    @Column(name = "applymode",nullable = true)
    private int applymode;

    @Column(name = "status",nullable = true)
    private int status;



    @Column(name = "shopid",nullable = true)
    private int shopid;

    @Column(name = "refundmoney",nullable = true)
    private double refundmoney;


    @Column(name = "ordermoney",nullable = true)
    private double ordermoney;

    @Column(name = "sendbackstatus",nullable = true)
    private int sendbackstatus;


    @Column(name = "sellermsg",nullable = true)
    private String sellermsg;


    @Column(name = "customerphone",nullable = true)
    private String customerphone;


    @Column(name = "shopphone",nullable = true)
    private String shopphone;


    @Column(name = "sendbackcompany",nullable = true)
    private String sendbackcompany;

    @Column(name = "sendbackcode",nullable = true)
    private String sendbackcode;

    @Column(name = "createtime",nullable = true)
    private Date createtime;

    @Column(name = "refundtime",nullable = true)
    private Date refundtime;


    @Column(name = "sendbacktime",nullable = true)
    private Date sendbacktime;



    @Column(name = "needjudge",nullable = true)
    private int needjudge;

    @Column(name = "refundid",nullable = true)
    private int refundid;


    @Column(name = "hasreply",nullable = true)
    private int hasreply;


    @OneToOne
    @JoinColumn(name="orderid")
    private  OrderGoods orderGoods;


    public int getHasreply() {
        return hasreply;
    }

    public void setHasreply(int hasreply) {
        this.hasreply = hasreply;
    }


    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }

    public String getApplymsg() {
        return applymsg;
    }

    public void setApplymsg(String applymsg) {
        this.applymsg = applymsg;
    }

    public String getApplypics() {
        return applypics;
    }

    public void setApplypics(String applypics) {
        this.applypics = applypics;
    }

    public int getReceiveid() {
        return receiveid;
    }

    public void setReceiveid(int receiveid) {
        this.receiveid = receiveid;
    }


    public int getApplyreason() {
        return applyreason;
    }

    public void setApplyreason(int applyreason) {
        this.applyreason = applyreason;
    }

    public int getApplymode() {
        return applymode;
    }

    public void setApplymode(int applymode) {
        this.applymode = applymode;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    public double getRefundmoney() {
        return refundmoney;
    }

    public void setRefundmoney(double refundmoney) {
        this.refundmoney = refundmoney;
    }

    public int getSendbackstatus() {
        return sendbackstatus;
    }

    public void setSendbackstatus(int sendbackstatus) {
        this.sendbackstatus = sendbackstatus;
    }

    public String getSellermsg() {
        return sellermsg;
    }

    public void setSellermsg(String sellermsg) {
        this.sellermsg = sellermsg;
    }

    public String getSendbackcompany() {
        return sendbackcompany;
    }

    public void setSendbackcompany(String sendbackcompany) {
        this.sendbackcompany = sendbackcompany;
    }

    public String getSendbackcode() {
        return sendbackcode;
    }

    public void setSendbackcode(String sendbackcode) {
        this.sendbackcode = sendbackcode;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getRefundtime() {
        return refundtime;
    }

    public void setRefundtime(Date refundtime) {
        this.refundtime = refundtime;
    }

    public Date getSendbacktime() {
        return sendbacktime;
    }

    public void setSendbacktime(Date sendbacktime) {
        this.sendbacktime = sendbacktime;
    }

    

    public int getNeedjudge() {
        return needjudge;
    }

    public void setNeedjudge(int needjudge) {
        this.needjudge = needjudge;
    }

    public int getRefundid() {
        return refundid;
    }

    public void setRefundid(int refundid) {
        this.refundid = refundid;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public int getBuyid() {
        return buyid;
    }

    public void setBuyid(int buyid) {
        this.buyid = buyid;
    }

    public double getOrdermoney() {
        return ordermoney;
    }

    public void setOrdermoney(double ordermoney) {
        this.ordermoney = ordermoney;
    }

    public String getShopphone() {
        return shopphone;
    }

    public void setShopphone(String shopphone) {
        this.shopphone = shopphone;
    }


    public OrderGoods getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(OrderGoods orderGoods) {
        this.orderGoods = orderGoods;
    }
}
