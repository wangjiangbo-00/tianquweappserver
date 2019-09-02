package com.ziyoushenghuo.entry;





import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order_refund")
public class Refund {


    public static final int REFUND_STATUS_NORMAL = 0;
    public static final int REFUND_STATUS_REFUND_OK= 1;
    public static final int REFUND_STATUS_REFUND_FAIL= 2;


    public static final int REFUND_TYPE_GROUPFAIL = 1;
    public static final int REFUND_TYPE_PRE_SHIP_FREE= 2;
    public static final int REFUND_TYPE_USER_REFUND = 3;
    public static final int REFUND_TYPE_GIFT_OVER_TIME = 4;
    public static final int REFUND_TYPE_GIVEUP_ORDER = 5;
    public static final int REFUND_TYPE_PLATFORM_GROUP_DISCOUNT = 6;
    public static final int REFUND_TYPE_FREE_ORDER = 7;
    public static final String[] REFUND_REASONS = {
            "退款",
            "成团失败自动退款",
            "退回预付运费",
            "商品售后",
            "礼物领取超时",
            "取消订单",
            "退还团购折扣",
            "送礼免单"
    };



    public static final String REFUND_DEFAULT_ERROR_CODE= "接口调用失败";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "userid",nullable = true)
    private int userid;

    @Column(name = "money",nullable = true)
    private double money;

    @Column(name = "orderid",nullable = true)
    private Long orderid;


    @Column(name = "status",nullable = true)
    private int status;


    @Column(name = "type",nullable = true)
    private int type;

    @Column(name = "reason",nullable = true)
    private String reason;

    @Column(name = "failstr",nullable = true)
    private String failstr;

    @Column(name = "refundid",nullable = true)
    private String refundid;

    @Column(name = "createtime",nullable = true)
    private Date createtime;

    @Column(name = "refundtime",nullable = true)
    private Date refundtime;



    public String getFailstr() {
        return failstr;
    }

    public void setFailstr(String failstr) {
        this.failstr = failstr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefundid() {
        return refundid;
    }

    public void setRefundid(String refundid) {
        this.refundid = refundid;
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


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public static int GetNewRefundFlag(int oldflag,int type,int result)
    {

        int sp = (type-1)*2;
        int fp = (type-1)*2 + 1 ;
        if(result == 1)
        {
            oldflag = oldflag & (~(1 << fp));
            oldflag = oldflag | (1 << sp);
        }
        else if(result == 2)
        {
            oldflag = oldflag & (~(1 << sp));
            oldflag = oldflag | (1 << fp);
        }
        return oldflag;
    }



}
