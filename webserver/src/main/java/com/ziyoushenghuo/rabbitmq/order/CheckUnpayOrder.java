package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayAutoCloseSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayCheckGiftOvertime;

import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayUnPaySender;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.WeixinPayResultService;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import javassist.bytecode.stackmap.BasicBlock;
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
@RabbitListener(queues = MQConstant.ORDER_CHECK_UNPAY_NAME)
public class CheckUnpayOrder {

    @Autowired
    OrderService orderService;

    @Autowired
    private DelayCheckGiftOvertime delayCheckGiftStatus;
    @Autowired
    private DelayAutoCloseSender delayAutoCloseSender;

    @Autowired
    private DelayUnPaySender delayUnPaySender;

    @Autowired
    private WeixinPayResultService weixinPayResultService;

    private static Logger logger = LoggerFactory.getLogger(CheckUnpayOrder.class);
    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("deal with unpay order  with order id = " + orderid);

            int oid = Integer.valueOf(orderid);

            Order order = orderService.getOrderById(oid);

            if(order!=null)
            {
                if(order.getOrderStatus() == Order.ORDER_STATUS_TOPAY && (order.getPayStatus() == Order.ORDER_PAY_STATUS_NORMAL||order.getPayStatus() == Order.ORDER_PAY_STATUS_OK))
                {

                    WxPayUtils.Result result = new WxPayUtils.Result();

                    result = WxPayUtils.queryorder(order.getId());

                    if(result.getResult() == WxPayUtils.ORDER_RESULT_OK)
                    {

                        weixinPayResultService.OnOrderPaySuccess(order,result.getTransaction_id());
                    }
                    else if(result.getResult() == WxPayUtils.ORDER_RESULT_FAIL)
                    {

                        WxPayUtils.closeorder(order.getId(),0);
                        logger.warn("deal with unpay order  with order id = " + orderid + ",and set status = " + Order.ORDER_STATUS_UNPAY);
                        order.setOrderStatus(Order.ORDER_STATUS_UNPAY);
                        orderService.UpdateOrder(order);
                        delayAutoCloseSender.send(orderid);
                    }
                    else if(result.getResult() == WxPayUtils.ORDER_RESULT_EXCEPTION)
                    {
                        logger.warn("deal with unpay order  with order id = " + orderid + ",and set status = " + Order.ORDER_STATUS_UNPAY);
                        // try again
                        delayUnPaySender.send(orderid);
                    }

                }
                else
                {
                    logger.warn("deal with unpay order  with order id = " + orderid + ",and no need do something " );
                    // try check weixin pay result
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
