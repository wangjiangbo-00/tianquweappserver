package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.RefundProcess;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundSettleSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundProcessService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
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
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MQConstant.ORDER_REFUND_REPLY_WAIT_NAME)
public class CheckRefundProcessApplyWait {

    @Autowired
    OrderService orderService;

    @Autowired
    DelayRefundSettleSender delayRefundSettleSender;

    @Autowired
    RefundProcessService refundProcessService;

    private static Logger logger = LoggerFactory.getLogger(CheckRefundFail.class);

    @RabbitHandler
    public void process(String orderid) {

        try {

            int oid = Integer.valueOf(orderid);

            RefundProcess refundProcess = refundProcessService.getByOrderId(oid);



            if(refundProcess!=null)
            {
                if(refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_NOREPLY && refundProcess.getHasreply() == 0)
                {
                    // do refund
                    if(refundProcess.getApplymode() == RefundProcess.REFUND_PROCESS_TYPE_ONLYMONEY)
                    {
                        delayRefundSettleSender.send(orderid);
                        refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_OVERTIME);
                    }
                    else
                    {
                        refundProcess.setStatus(RefundProcess.REFUND_PROCESS_SELLER_RECEIVE);
                        refundProcess.setSendbackstatus(RefundProcess.REFUND_SENDBACK_NOMAL);
                    }
                    refundProcessService.createOrUpdate(refundProcess);

                }


            }
            else
            {

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
