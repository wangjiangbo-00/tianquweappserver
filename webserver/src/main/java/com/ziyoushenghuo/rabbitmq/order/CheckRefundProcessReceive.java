package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.RefundProcess;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayRefundSettleSender;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundProcessService;
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
@RabbitListener(queues = MQConstant.ORDER_CHECK_REFUND_AUTO_RECEIVE_NAME)
public class CheckRefundProcessReceive {

    @Autowired
    OrderService orderService;

    @Autowired
    RefundProcessService refundProcessService;

    @Autowired
    DelayRefundSettleSender delayRefundSettleSender;

    private static Logger logger = LoggerFactory.getLogger(CheckUnpayOrder.class);
    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("deal with auto close order  with order id = " + orderid);

            int oid = Integer.valueOf(orderid);

            RefundProcess refundProcess = refundProcessService.getByOrderId(oid);

            if(refundProcess!=null)
            {
                if(refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_RECEIVE &&refundProcess.getSendbackstatus() == RefundProcess.REFUND_SENDBACK_TORECEIVE)
                {
                    refundProcess.setStatus(RefundProcess.REFUND_PROCESS_STATUS_REFUNDING);
                    refundProcess.setSendbackstatus(RefundProcess.REFUND_PROCESS_SELLER_OVERTIME);

                    delayRefundSettleSender.send(orderid);

                    refundProcessService.createOrUpdate(refundProcess);

                }
                else
                {

                    logger.warn("deal with auto close order   with order id = " + orderid + ",and no need do something " );
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
