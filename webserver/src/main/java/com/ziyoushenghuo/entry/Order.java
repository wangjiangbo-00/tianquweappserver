package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_order")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "order.all",
                attributeNodes = {//attributeNodes 来定义需要懒加载的属性
                        @NamedAttributeNode("orderExpress"), @NamedAttributeNode("orderGoods"), @NamedAttributeNode("orderShop"),
                        @NamedAttributeNode("orderExtra"), @NamedAttributeNode("orderAddr")
                }
        ),
        @NamedEntityGraph(name = "order.goods",
                attributeNodes = {
                        @NamedAttributeNode("orderGoods")//无延伸

                }
        )
})


public class  Order extends BasicMessage{


    //订单状态0待付款1待发货2待收货3待评价4已完成5已关闭
    public static final int ORDER_PAY_STATUS_NORMAL = 0;
    public static final int ORDER_PAY_STATUS_OK = 1;
    public static final int ORDER_PAY_STATUS_CONFORM = 2;


    public static final int ORDER_TYPE_NORMAL = 0;
    public static final int ORDER_TYPE_GIVER = 1;
    public static final int ORDER_TYPE_LOTTERY = 2;

    public static final int ORDER_STATUS_TOPAY = 0;

    public static final int ORDER_STATUS_TOSHIP= 1;
    public static final int ORDER_STATUS_TORECEIVE= 2;
    public static final int ORDER_STATUS_TOCOMMENT = 3; //
    public static final int ORDER_STATUS_FINISH = 4; // 订单处于中止状态，包括完成了评价 礼物送出超时，团购成团失败，前端需要根据标志展示更丰富的信息

    public static final int ORDER_STATUS_TOGIFT = 5; //
    public static final int ORDER_STATUS_REFUND= 6; //

    public static final int ORDER_STATUS_TOGROUP = 7; //用户等待团购成功
    public static final int ORDER_STATUS_UNPAY = 8; // 超过支付时间，用户取消订单，或者重新购买 24小时自动删除

    public static final int ORDER_FINISH_REASON_NORMAL = 0;
    public static final int ORDER_FINISH_REASON_GROUP_FAIL = 1;
    public static final int ORDER_FINISH_REASON_GIFT_OVER = 2;
    public static final int ORDER_FINISH_REASON_USER_GIVEUP= 4;

    public static final int ORDER_PROFIT_STATUS_NORMAL = 0;
    public static final int ORDER_PROFIT_STATUS_DONE = 1;

    public static final int ORDER_GIVEN_STATUS_NORMAL = 0;
    public static final int ORDER_GIVEN_STATUS_DONE = 1;
    public static final int ORDER_GIVEN_STATUS_OVER = 2;

    public static final String ORDER_WEIXIN_PREX = "wr_tqxj_";
    public static final String ORDER_REFUND_PREX = "wr_tqxj_refund_";
    public static final String ORDER_SHOP_WITHDREW_PREX = "wrswd";
    public static final String ORDER_CUSTOMER_WITHDREW_PREX = "wrcwd";

    public static final String ORDER_CUSTOMER_WITHDREW_DESC = "提现推广奖励";

    public static final String ORDER_SHOP_WITHDREW_DESC = "商户收益提现";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "out_trade_no",nullable = false)
    private String outtradeno;

    @Column(name = "buyer_id",nullable = false)
    private int buyerid;

    @Column(name = "user_name",nullable = false)
    private String username;


    @Column(name = "shop_id",nullable = false)
    private int shopId;


    @Column(name = "order_money",nullable = false)
    private double ordermoney;


    @Column(name = "order_price",nullable = false)
    private double order_price;



    @Column(name = "refund_money",nullable = false)
    private double refund_money;

    @Column(name = "group_buy")
    private int groupbuy;

    @Column(name = "group_order_id")
    private int groupOrderId;

    @Column(name = "group_header")
    private int group_header;

    @Column(name = "is_selflift")
    private int isselflift;

    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "pay_time")
    private Date paytime;

    @Column(name = "sign_time")
    private Date sign_time;

    @Column(name = "shipping_time")
    private Date shipping_time;

    @Column(name = "order_status")
    private int orderStatus;

    @Column(name = "pay_status")
    private int payStatus;

    @Column(name = "refund_process_status")
    private int refundProcessStatus;



    @Column(name = "profit_status")
    private int profitStatus;

    @Column(name = "sharefrom")
    private int sharefrom;



    @Column(name = "shipping_money")
    private int shipping_money;

    @Column(name = "fixaddr")
    private int fixaddr;

    @Column(name = "tryrefund")
    private int tryrefund;


    @Column(name = "transaction_id")
    private String transaction_id;


    @Column(name = "prepay_id")
    private String prepay_id;

    @Column(name = "form_id")
    private String form_id;


    @Column(name = "ordertype")
    private int ordertype;


    @Column(name = "finish_reason")
    private int finishreason;

    @Column(name = "giver_id")
    private int giverid;

    @Column(name = "given_token")
    private String given_token;

    @Column(name = "preshippfee")
    private int preshippfee;

    @Column(name = "given_status")
    private int givenstatus;

    @Column(name = "canrefund")
    private int canrefund;


    @Column(name = "isfree")
    private int isfree;

    @Column(name = "refund_flag")
    private int refundFlag;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private  OrderExpress orderExpress;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private  OrderGoods orderGoods;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private  OrderShop orderShop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private  OrderExtra orderExtra;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private  OrderAddr orderAddr;



    @Column(name = "discount_goods_id")
    private int discount_goods_id;
    @Column(name = "discount",nullable = false)
    private double discount;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOuttradeno() {
        return outtradeno;
    }

    public void setOuttradeno(String outtradeno) {
        this.outtradeno = outtradeno;
    }

    public int getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(int buyerid) {
        this.buyerid = buyerid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public double getOrdermoney() {
        return ordermoney;
    }

    public void setOrdermoney(double ordermoney) {
        this.ordermoney = ordermoney;
    }

    public double getRefund_money() {
        return refund_money;
    }

    public void setRefund_money(double refund_money) {
        this.refund_money = refund_money;
    }

    public int getGroupbuy() {
        return groupbuy;
    }

    public void setGroupbuy(int groupbuy) {
        this.groupbuy = groupbuy;
    }

    public int getGroupOrderId() {
        return groupOrderId;
    }

    public void setGroupOrderId(int groupOrderId) {
        this.groupOrderId = groupOrderId;
    }

    public int getGroup_header() {
        return group_header;
    }

    public void setGroup_header(int group_header) {
        this.group_header = group_header;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getPaytime() {
        return paytime;
    }

    public void setPaytime(Date paytime) {
        this.paytime = paytime;
    }

    public Date getSign_time() {
        return sign_time;
    }

    public void setSign_time(Date sign_time) {
        this.sign_time = sign_time;
    }

    public Date getShipping_time() {
        return shipping_time;
    }

    public void setShipping_time(Date shipping_time) {
        this.shipping_time = shipping_time;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public int getRefundProcessStatus() {
        return refundProcessStatus;
    }

    public void setRefundProcessStatus(int refundProcessStatus) {
        this.refundProcessStatus = refundProcessStatus;
    }

    public int getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(int refundFlag) {
        this.refundFlag = refundFlag;
    }




    public int getFixaddr() {
        return fixaddr;
    }

    public void setFixaddr(int fixaddr) {
        this.fixaddr = fixaddr;
    }

    public int getTryrefund() {
        return tryrefund;
    }

    public void setTryrefund(int tryrefund) {
        this.tryrefund = tryrefund;
    }

    public int getProfitStatus() {
        return profitStatus;
    }

    public void setProfitStatus(int profitStatus) {
        this.profitStatus = profitStatus;
    }

    public int getSharefrom() {
        return sharefrom;
    }

    public void setSharefrom(int sharefrom) {
        this.sharefrom = sharefrom;
    }



    public int getShipping_money() {
        return shipping_money;
    }

    public void setShipping_money(int shipping_money) {
        this.shipping_money = shipping_money;
    }



    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public int getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(int ordertype) {
        this.ordertype = ordertype;
    }

    public int getGiverid() {
        return giverid;
    }

    public void setGiverid(int giverid) {
        this.giverid = giverid;
    }

    public String getGiven_token() {
        return given_token;
    }

    public void setGiven_token(String given_token) {
        this.given_token = given_token;
    }

    public int getPreshippfee() {
        return preshippfee;
    }

    public void setPreshippfee(int preshippfee) {
        this.preshippfee = preshippfee;
    }

    public int getGivenstatus() {
        return givenstatus;
    }

    public void setGivenstatus(int givenstatus) {
        this.givenstatus = givenstatus;
    }



    public int getFinishreason() {
        return finishreason;
    }

    public void setFinishreason(int finishreason) {
        this.finishreason = finishreason;
    }

    public OrderExpress getOrderExpress() {
        return orderExpress;
    }

    public void setOrderExpress(OrderExpress orderExpress) {
        this.orderExpress = orderExpress;
    }

    public OrderGoods getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(OrderGoods orderGoods) {
        this.orderGoods = orderGoods;
    }

    public OrderShop getOrderShop() {
        return orderShop;
    }

    public void setOrderShop(OrderShop orderShop) {
        this.orderShop = orderShop;
    }

    public OrderExtra getOrderExtra() {
        return orderExtra;
    }

    public void setOrderExtra(OrderExtra orderExtra) {
        this.orderExtra = orderExtra;
    }

    public OrderAddr getOrderAddr() {
        return orderAddr;
    }

    public void setOrderAddr(OrderAddr orderAddr) {
        this.orderAddr = orderAddr;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }


    public int getIsfree() {
        return isfree;
    }

    public void setIsfree(int isfree) {
        this.isfree = isfree;
    }

    public double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(double order_price) {
        this.order_price = order_price;
    }

    public int getDiscount_goods_id() {
        return discount_goods_id;
    }

    public void setDiscount_goods_id(int discount_goods_id) {
        this.discount_goods_id = discount_goods_id;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }


    public int getIsselflift() {
        return isselflift;
    }

    public void setIsselflift(int isselflift) {
        this.isselflift = isselflift;
    }

    public int getCanrefund() {
        return canrefund;
    }

    public void setCanrefund(int canrefund) {
        this.canrefund = canrefund;
    }
}
