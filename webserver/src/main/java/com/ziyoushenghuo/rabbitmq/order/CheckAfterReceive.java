package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.scheduling.FlagCheckOrderExpress;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.ShopProfitService;
import com.ziyoushenghuo.settlement.SettleService;
import com.ziyoushenghuo.settlement.ShopSettle;
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
@RabbitListener(queues = MQConstant.ORDER_CHECK_AFTERRECEIVE_NAME)
public class CheckAfterReceive {

    @Autowired
    OrderService orderService;

    @Autowired
    ShopProfitService shopProfitService;

    @Autowired
    SettleService settleService;

    private static Logger logger = LoggerFactory.getLogger(CheckAfterReceive.class);

    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.info("deal with after order with order id = " + orderid);
            int oid = Integer.valueOf(orderid);

            Order order = orderService.getOrderById(oid);

            if(order!=null)
            {
                if((order.getOrderStatus() == Order.ORDER_STATUS_FINISH || order.getOrderStatus() == Order.ORDER_STATUS_TOCOMMENT)&& order.getProfitStatus() == Order.ORDER_PROFIT_STATUS_NORMAL)
                {
                    //直接开始结算处理
                    logger.info("deal with receive order by  settle with order id = " + orderid);
                    settleService.onOrderProfitSettle(order);
                }
                else if(order.getOrderStatus() == Order.ORDER_STATUS_REFUND && order.getProfitStatus() == Order.ORDER_PROFIT_STATUS_NORMAL)
                {
                    // do nothing
                }
                else
                {
                    // do nothing
                }

            }
        }catch (Exception e)
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
