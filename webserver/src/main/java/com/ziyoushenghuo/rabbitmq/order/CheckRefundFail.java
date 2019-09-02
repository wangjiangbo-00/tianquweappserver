package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.RefundProcess;
import com.ziyoushenghuo.rabbitmq.MQConstant;
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
@RabbitListener(queues = MQConstant.ORDER_REFUND_FAIL_NAME)
public class CheckRefundFail {

    @Autowired
    OrderService orderService;

    @Autowired
    RefundService refundService;

    @Autowired
    RefundProcessService refundProcessService;

    private static Logger logger = LoggerFactory.getLogger(CheckRefundFail.class);

    @RabbitHandler
    public void process(String refundid) {

        try {
            logger.warn("deal with refund order  with refundid id = " + refundid);
            int rid = Integer.valueOf(refundid);

            Refund refund = refundService.getById(rid);



            if(refund!=null)
            {
                if(refund.getStatus() == Refund.REFUND_STATUS_REFUND_FAIL)
                {
                    // do refund
                    long orderid = refund.getOrderid();

                    Order order = orderService.getOrderById(orderid);
                    if(order!=null)
                    {
                        WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                        if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
                        {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                            refund.setRefundid(refundResult.getRefundid());
                            refund.setRefundtime(new Date());
                            refundService.createOrUpdate(refund);
                        }
                        else
                        {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                            refund.setFailstr(refundResult.getErrdes());
                            refundService.createOrUpdate(refund);
                            // failed again todo add expection


                        }
                        if(refund.getType()==Refund.REFUND_TYPE_GROUPFAIL)
                        {


                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_GROUPFAIL,refund.getStatus()));


                        }
                        else if(refund.getType()==Refund.REFUND_TYPE_PRE_SHIP_FREE)
                        {


                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_PRE_SHIP_FREE,refund.getStatus()));

                        }
                        else if(refund.getType()==Refund.REFUND_TYPE_USER_REFUND)
                        {

                            RefundProcess refundProcess = refundProcessService.getByOrderId(orderid);
                            if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
                            {
                                refundProcess.setStatus(RefundProcess.REFUND_PROCESS_REFUND_DONE);
                                refundProcess.setRefundtime(new Date());
                                refundProcessService.createOrUpdate(refundProcess);
                            }
                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_USER_REFUND,refund.getStatus()));

                        }
                        else if(refund.getType()==Refund.REFUND_TYPE_GIFT_OVER_TIME)
                        {
                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_GIFT_OVER_TIME,refund.getStatus()));
                            order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                            order.setFinishreason(Order.ORDER_FINISH_REASON_GIFT_OVER);

                        }
                        else
                        {
                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),refund.getType(),refund.getStatus()));
                        }



                        orderService.UpdateOrder(order);
                    }

                }


            }
            else
            {
                logger.warn("deal with refund order  with refundid id = " + refundid + ", and no refund found");
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
