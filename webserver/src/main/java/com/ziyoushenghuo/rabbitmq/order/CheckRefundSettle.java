package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.RefundProcess;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundProcessService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.settlement.CustomerSettle;
import com.ziyoushenghuo.settlement.SettleService;
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
@RabbitListener(queues = MQConstant.ORDER_REFUND_OK_SETTILE_NAME)
public class CheckRefundSettle {

    @Autowired
    OrderService orderService;

    @Autowired
    RefundService refundService;

    @Autowired
    RefundProcessService refundProcessService;

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    @Autowired
    private SettleService settleService;


    @Autowired
    private CustomerSettle customerSettle;

    private static Logger logger = LoggerFactory.getLogger(CheckRefundFail.class);

    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("deal with refund order  with oidid = " + orderid);
            long oid = Integer.valueOf(orderid);

            RefundProcess refundProcess = refundProcessService.getByOrderId(oid);

            if(refundProcess!=null)
            {
                if(refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_OVERTIME && refundProcess.getApplymode() == RefundProcess.REFUND_PROCESS_TYPE_ONLYMONEY
                        || refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_STATUS_REFUNDING || refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_ACCEPT)
                {
                    // do refund

                    if(refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_OVERTIME || refundProcess.getStatus() == RefundProcess.REFUND_PROCESS_SELLER_ACCEPT)
                    {
                        refundProcess.setStatus(RefundProcess.REFUND_PROCESS_STATUS_REFUNDING);
                    }

                    Refund refund = new Refund();
                    refund.setMoney(refundProcess.getRefundmoney());
                    refund.setOrderid(oid);

                    refund.setReason(Refund.REFUND_REASONS[Refund.REFUND_TYPE_USER_REFUND]);
                    refund.setStatus(Refund.REFUND_STATUS_NORMAL);
                    refund.setType(Refund.REFUND_TYPE_USER_REFUND);

                    refund.setUserid(refundProcess.getBuyid());

                    try
                    {
                        refund.setCreatetime(new Date());
                        refundService.createOrUpdate(refund);

                        refundProcess.setRefundid(refund.getId());
                        refundProcessService.createOrUpdate(refundProcess);

                    }
                    catch (Exception e)
                    {

                    }

                    Order order = orderService.getOrderById(oid);
                    order.setRefundProcessStatus(RefundProcess.REFUND_PROCESS_STATUS_REFUNDING);
                    if(order!=null)
                    {
                        WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                        if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
                        {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                            refund.setRefundid(refundResult.getRefundid());refund.setRefundtime(new Date());
                            refundService.createOrUpdate(refund);

                            refundProcess.setStatus(RefundProcess.REFUND_PROCESS_REFUND_DONE);
                            refundProcessService.createOrUpdate(refundProcess);

                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_USER_REFUND,Refund.REFUND_STATUS_REFUND_OK));
                            order.setRefundProcessStatus(RefundProcess.REFUND_PROCESS_REFUND_DONE);

                            settleService.onOrderProfitSettle(order);


                            customerSettle.onOrderCancalSettle(order,Refund.REFUND_TYPE_USER_REFUND);

                        }
                        else
                        {
                            refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                            refund.setFailstr(refundResult.getErrdes());
                            refundService.createOrUpdate(refund);

                            String queuemsg = "" + refund.getId();
                            delayRefundFailSender.send(queuemsg);

                            boolean hasKey = redisTemplate.hasKey(RedisKeyConstant.FLAG_REFUND_FAIL);
                            if(!hasKey)
                            {
                                //send msg to admin
                                AliSmsUtils.sendNotify("田趣小集退款异常:",refundResult.getErrdes());

                                redisTemplate.opsForValue().set(RedisKeyConstant.FLAG_REFUND_FAIL,"1",RedisKeyConstant.EXPIRE_REFUND_FAIL, TimeUnit.HOURS);
                            }
                            order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_USER_REFUND,Refund.REFUND_STATUS_REFUND_FAIL));


                        }

                        orderService.UpdateOrder(order);
                    }

                }


            }
            else
            {
                logger.warn("deal with refund order  with refundid id = " + oid + ", and no refund found");
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
