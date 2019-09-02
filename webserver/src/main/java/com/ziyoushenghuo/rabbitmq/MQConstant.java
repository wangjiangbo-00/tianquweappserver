package com.ziyoushenghuo.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public final class MQConstant {

    private MQConstant(){
    }

    //exchange name
    public static final String DEFAULT_EXCHANGE = "ZXTC";

    //DLX QUEUE
    public static final String DEAD_LETTER_QUEUE_UNGROUP_NAME = "zxtc.deadletter.ungroup.queue";
    public static final String DEAD_LETTER_QUEUE_UNPAY_NAME = "zxtc.deadletter.unpay.queue";
    public static final String DEAD_LETTER_QUEUE_AFTERDELIVERY_NAME = "zxtc.deadletter.afterdelivery.queue";
    public static final String DEAD_LETTER_QUEUE_CANCALREFUND_NAME = "zxtc.deadletter.cancalrefund.queue";
    public static final String DEAD_LETTER_QUEUE_REFUND_FAIL_NAME = "zxtc.deadletter.refundfail.queue";
    public static final String DEAD_LETTER_DO_REFUND_GIIF_SHIPFEE_NAME = "zxtc.deadletter.refundgiftshipfee.queue";
    public static final String DEAD_LETTER_CHECK_SENDGIFT_STATUS_NAME = "zxtc.deadletter.checkgiftstatus.queue";
    public static final String DEAD_LETTER_QUEUE_AUTOCLOSE_NAME = "zxtc.deadletter.autoclose.queue";
    public static final String DEAD_LETTER_QUEUE_AFTERRECEIVE_NAME = "zxtc.deadletter.checkafterreceive.queue";
    public static final String DEAD_LETTER_QUEUE_REFUND_AUTO_RECEIVE_NAME = "zxtc.deadletter.refundautoreceive.queue";
    public static final String DEAD_LETTER_QUEUE_REFUND_OK_SETTILE_NAME = "zxtc.deadletter.checkrefundoksettle.queue";
    public static final String DEAD_LETTER_QUEUE_REFUND_REPLY_WAIT_NAME = "zxtc.deadletter.checkrefundwait.queue";
    public static final String DEAD_LETTER_DO_REFUND_FREE_ORDER_NAME = "zxtc.deadletter.refundfreeorder.queue";
    // 消息处理队列
    public static final String ORDER_CHECK_UNPAY_NAME = "zxtc.order.checkunpay"; // test 1
    public static final String ORDER_CHECK_UNGROUP_NAME = "zxtc.order.checktogruop";// test 2
    public static final String ORDER_CHECK_CANCALREFUND_NAME = "zxtc.order.cancalrefund";// test 3
    public static final String ORDER_CHECK_AUTOCLOSE_NAME = "zxtc.order.checkautoclose";// test 4
    public static final String ORDER_CHECK_AFTERDELIVERY_NAME = "zxtc.order.checkafterdelivery";// test 5
    public static final String ORDER_REFUND_FAIL_NAME = "zxtc.order.refundfail";// test 6
    public static final String ORDER_CHECK_AFTERRECEIVE_NAME = "zxtc.order.checkafterreceive";// test 7
    public static final String ORDER_DO_REFUND_GIIF_SHIPFEE_NAME = "zxtc.order.refundgiftshipfee";// test 8
    public static final String ORDER_CHECK_SENDGIFT_STATUS_NAME = "zxtc.order.checkgiftstatus";// test 9
    public static final String ORDER_REFUND_OK_SETTILE_NAME = "zxtc.order.checkrefundoksettle";// test 11
    public static final String ORDER_REFUND_REPLY_WAIT_NAME = "zxtc.order.checkrefundwait";// test 12
    public static final String ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME = "zxtc.order.checkrefundautoreceive";// test 13
    public static final String ORDER_DO_REFUND_FREE_ORDER_NAME = "zxtc.order.refundfreeorder";// test 14




    public static final String SCHEDULE_GIFT_START = "zxtc.schedule.giftstart";
    public static final String SCHEDULE_GIFT_STOP = "zxtc.schedule.giftstop";
    public static final String SCHEDULE_GIFT_SHOW = "zxtc.schedule.giftshow";

    public static final String SCHEDULE_GROUP_START = "zxtc.schedule.groupstart";
    public static final String SCHEDULE_GROUP_STOP = "zxtc.schedule.groupstop";
    public static final String SCHEDULE_GROUP_SHOW = "zxtc.schedule.groupshow";

    public static final String SCHEDULE_DISCOUNT_START = "zxtc.schedule.discountstart";
    public static final String SCHEDULE_DISCOUNT_STOP = "zxtc.schedule.discountstop";
    public static final String SCHEDULE_DISCOUNT_SHOW = "zxtc.schedule.discountshow";

    // 消息时长定义
    public static final int ORDER_CHECK_UNPAY_TIME = 1000*60*31;
    public static final int ORDER_CHECK_UNGROUP_TIME = 1000*24*60*60 +1000*60*5;
    public static final  int ORDER_CHECK_AFTERDELIVERY_TIME = 1000*24*60*60*15;
    public static final  int ORDER_CHECK_DOCANCALREFUND_TIME = 1000*3; //商户设置退款后 3秒尝试结算
    public static final  int ORDER_CHECK_AFTERRECEIVE_TIME = 1000*24*60*60*7; //7天后尝试结算
    public static final int ORDER_CHECK_AUTOCLOSE_TIME = 1000*60*60*24;
    public static final  int ORDER_REFUND_FAIL_TIME = 1000*60*60*6;

    public static final int ORDER_DO_REFUND_GIIF_SHIPFEE_TIME = 1000*10;

    public static final int ORDER_DO_REFUND_FREE_ORDER_TIME = 1000*15;
    public static final  int ORDER_CHECK_SENDGIFT_STATUS_TIME = 1000*60*60*48;  //礼物48小时没有领取自动退款

    public static final  int ORDER_CHECK_CAN_REFUND_APPLY_TIME = 1000*60*60*24 * 3; //自动收货或者确认收货后3天内能申请退款
    public static final  int ORDER_REFUND_OK_SETTILE_TIME = 1000*60*2; // 退款确认后 30分钟开始结算
    public static final  int ORDER_REFUND_REPLY_WAIT_TIME = 1000*60*60*24 * 3; // 申请退款后3天内回复
    public static final int ORDER_CHECK_REFUND_AUTO_RECEIVE_TIME = 1000*24*60*60*7; //7天商家收货
}
