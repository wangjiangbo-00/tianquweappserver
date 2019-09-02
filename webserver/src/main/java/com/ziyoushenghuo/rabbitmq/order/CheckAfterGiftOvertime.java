package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.service.ShopProfitService;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MQConstant.ORDER_CHECK_SENDGIFT_STATUS_NAME)
public class CheckAfterGiftOvertime {


    @Autowired
    OrderService orderService;


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private RefundService refundService;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    private static Logger logger = LoggerFactory.getLogger(CheckAfterDelivery.class);

    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.info("deal overtime gift order refund with order id = " + orderid);
            int oid = Integer.valueOf(orderid);
            Order order = orderService.getOrderById(oid);
            if (order != null && order.getGivenstatus() == Order.ORDER_GIVEN_STATUS_NORMAL) {

                logger.warn("deal with overtime gift order refund with order id = " + orderid);
                boolean hasrefund = refundService.checkOrderRefundExist(order.getId(), Refund.REFUND_TYPE_GIFT_OVER_TIME);
                if (hasrefund) {
                    logger.warn("deal with overtime gift order refund but find already refunded with id = " + orderid);
                    return;
                }
                Refund refund = new Refund();
                //todo 全部退款
                refund.setMoney(order.getOrdermoney());
                refund.setOrderid(order.getId());

                refund.setReason(Refund.REFUND_REASONS[Refund.REFUND_TYPE_GIFT_OVER_TIME]);
                refund.setStatus(Refund.REFUND_STATUS_NORMAL);
                refund.setType(Refund.REFUND_TYPE_GIFT_OVER_TIME);
                refund.setUserid(order.getBuyerid());

                try {
                    refund.setCreatetime(new Date());
                    refundService.createOrUpdate(refund);

                } catch (Exception e) {

                }

                WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                if (refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK) {
                    refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                    refund.setRefundid(refundResult.getRefundid());
                    refund.setRefundtime(new Date());
                    refundService.createOrUpdate(refund);
                    order.setFinishreason(Order.ORDER_FINISH_REASON_GIFT_OVER);
                    order.setGivenstatus(Order.ORDER_GIVEN_STATUS_OVER);
                    order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                    order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(), Refund.REFUND_TYPE_GIFT_OVER_TIME, Refund.REFUND_STATUS_REFUND_OK));
                    orderService.UpdateOrder(order);

                } else {
                    refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                    refund.setFailstr(refundResult.getErrdes());

                    refundService.createOrUpdate(refund);
                    String queuemsg = "" + refund.getId();
                    delayRefundFailSender.send(queuemsg);

                    logger.warn("deal with overtime gift fail with with = " + refundResult.getErrdes());

                    boolean hasKey = redisTemplate.hasKey(RedisKeyConstant.FLAG_REFUND_FAIL);
                    if (!hasKey) {
                        //send msg to admin
                        AliSmsUtils.sendNotify("田趣小集退款异常:", refundResult.getErrdes());

                        redisTemplate.opsForValue().set(RedisKeyConstant.FLAG_REFUND_FAIL, "1", RedisKeyConstant.EXPIRE_REFUND_FAIL, TimeUnit.HOURS);
                    }

                    order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(), Refund.REFUND_TYPE_GIFT_OVER_TIME, Refund.REFUND_STATUS_REFUND_FAIL));
                    order.setFinishreason(Order.ORDER_FINISH_REASON_GIFT_OVER);
                    order.setGivenstatus(Order.ORDER_GIVEN_STATUS_OVER);
                    order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                    orderService.UpdateOrder(order);
                }


            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }

    }

}
