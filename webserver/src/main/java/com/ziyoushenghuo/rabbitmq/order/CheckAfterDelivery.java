package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayAfterReceiveSender;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.ShopProfitService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.Date;

@Component
@RabbitListener(queues = MQConstant.ORDER_CHECK_AFTERDELIVERY_NAME)
public class CheckAfterDelivery {

    @Autowired
    OrderService orderService;

    @Autowired
    private DelayAfterReceiveSender afterReceiveSender;

    private static Logger logger = LoggerFactory.getLogger(CheckAfterDelivery.class);

    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("deal with unreceive order  with order id = " + orderid);
            int oid = Integer.valueOf(orderid);

            Order order = orderService.getOrderById(oid);

            if (order != null) {
                if ((order.getOrderStatus() == Order.ORDER_STATUS_TORECEIVE)) {
                    logger.warn("deal with unreceive order  with order id = " + orderid + ",and  current status = " + order.getOrderStatus());
                    order.setOrderStatus(Order.ORDER_STATUS_TOCOMMENT);
                    orderService.UpdateOrder(order);
                    afterReceiveSender.send("" + orderid);
                } else {
                    // do nothing user may has accept the order
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
