package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.entry.TeamFounder;
import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.service.TeamFounderService;
import com.ziyoushenghuo.web.controller.WxCustomerController;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MQConstant.ORDER_CHECK_UNGROUP_NAME)
public class CheckUngroupOrder {
    private static Logger logger = LoggerFactory.getLogger(CheckUngroupOrder.class);
    @Autowired
    OrderService orderService;

    @Autowired
    TeamFounderService teamFounderService;

    @Autowired
    RefundService refundService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;


    @RabbitHandler
    public void process(String groupid) {

        logger.warn("deal with ungroup order  with group id = " + groupid);

        try{
            int gid = Integer.valueOf(groupid);

            TeamFounder teamFounder = teamFounderService.GetById(gid);

            if(teamFounder!=null)
            {
                if(teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_NORMAL )
                {
                    logger.warn("deal with ungroup order  with group id = " + groupid + ",and begin deal the group with status " + teamFounder.getGroupresult());
                    Date now = new Date();
                    if(now.after(teamFounder.getExpiretime()))
                    {
                        //正常应该进入这个条件
                        teamFounder.setGroupresult(TeamFounder.GROUP_STATUS_FAIL);


                        List<Order> orders = orderService.getByGroupId(gid);
                        for (Order order:orders)
                        {

                            if(order.getPayStatus() != Order.ORDER_PAY_STATUS_NORMAL)
                            {
                                // 有支付行为，发起退款操作，记录退款记录
                                Refund refund = new Refund();
                                refund.setMoney(order.getOrdermoney());
                                refund.setOrderid(order.getId());
                                refund.setReason(Refund.REFUND_REASONS[Refund.REFUND_TYPE_GROUPFAIL]);
                                refund.setType(Refund.REFUND_TYPE_GROUPFAIL);
                                refund.setStatus(Refund.REFUND_STATUS_NORMAL);
                                refund.setUserid(order.getBuyerid());

                                try
                                {
                                    refund.setCreatetime(new Date());
                                    refundService.createOrUpdate(refund);

                                }
                                catch (Exception e)
                                {

                                }

                                //todo all money refund
                                WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                                if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
                                {
                                    refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                                    refund.setRefundid(refundResult.getRefundid());
                                    refund.setRefundtime(new Date());
                                    refundService.createOrUpdate(refund);

                                    order.setOrderStatus(Order.ORDER_STATUS_FINISH);
                                    order.setFinishreason(Order.ORDER_FINISH_REASON_GROUP_FAIL);
                                    order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_GROUPFAIL,Refund.REFUND_STATUS_REFUND_OK));
                                    logger.warn("deal with ungroup order  with group id = " + groupid + ",refund ok with order id = " + order.getId() );
                                }
                                else
                                {
                                    logger.warn("deal with ungroup order  with group id = " + groupid + ",refund fail with order id = " + order.getId() + ",and fail reason = "  + refundResult.getErrdes());
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

                                    order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_GROUPFAIL,Refund.REFUND_STATUS_REFUND_FAIL));
                                    order.setFinishreason(Order.ORDER_FINISH_REASON_GROUP_FAIL);
                                    order.setOrderStatus(Order.ORDER_STATUS_FINISH);


                                }

                            }


                            orderService.UpdateOrder(order);
                        }

                        teamFounderService.CreateOrUpdate(teamFounder);
                    }

                }
                else if(teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_OK)
                {
                    // do nothings
                    logger.warn("deal with ungroup order  with group id = " + groupid + ",and need not do something for current status = " + teamFounder.getGroupresult());
                }
                else if(teamFounder.getGroupresult() == TeamFounder.GROUP_STATUS_FAIL)
                {
                    // do nothings
                    logger.warn("deal with ungroup order  with group id = " + groupid + ",and need not do something for current status = " + teamFounder.getGroupresult());
                }

            }
            else
            {
                logger.warn("deal with ungroup order  with group id = " + groupid + ",and not found corresponding group order");
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
