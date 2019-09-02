package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.service.OrderService;
import javassist.bytecode.stackmap.BasicBlock;
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
@RabbitListener(queues = MQConstant.ORDER_CHECK_AUTOCLOSE_NAME)
public class CheckAutoCloseOrder {

    @Autowired
    OrderService orderService;



    private static Logger logger = LoggerFactory.getLogger(CheckUnpayOrder.class);
    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("deal with auto close order  with order id = " + orderid);

            int oid = Integer.valueOf(orderid);

            Order order = orderService.getOrderById(oid);

            if(order!=null)
            {
                if(order.getOrderStatus() == Order.ORDER_STATUS_UNPAY && order.getPayStatus() == Order.ORDER_PAY_STATUS_NORMAL)
                {
                    logger.warn("deal with auto close order  with order id = " + orderid + ",and set status = " + Order.ORDER_STATUS_UNPAY);

                    orderService.DeleteAllInterrelated(order);

                }
                else
                {

                    logger.warn("deal with auto close order   with order id = " + orderid + ",and no need do something " );



                }

            }
            else
            {
                logger.warn("deal with unpay order  with order id = " + orderid + ",and not found corresponding order " );
            }
        } catch (Exception e)
        {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }


    }

}
