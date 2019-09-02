package com.ziyoushenghuo.rabbitmq.order;

import com.ziyoushenghuo.alisms.AliSmsUtils;
import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.FreeOrder;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.Refund;
import com.ziyoushenghuo.rabbitmq.MQConstant;


import com.ziyoushenghuo.rabbitmq.delay.DelayRefundFailSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;
import com.ziyoushenghuo.rabbitmq.delay.DelayUnPaySender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.repository.FreeOrderRepository;
import com.ziyoushenghuo.service.CustomerService;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.service.RefundService;
import com.ziyoushenghuo.weixinpay.WxPayUtils;
import javassist.bytecode.stackmap.BasicBlock;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MQConstant.ORDER_DO_REFUND_FREE_ORDER_NAME)
public class RefundFreeOrder {

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;


    @Autowired
    FreeOrderRepository freeOrderRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private RefundService refundService;

    @Autowired
    private DelayRefundFailSender delayRefundFailSender;

    private static Logger logger = LoggerFactory.getLogger(RefundGiftShipFee.class);


    @RabbitHandler
    public void process(String orderid) {

        try {
            logger.warn("refund   free order   with order id = " + orderid);

            int oid = Integer.valueOf(orderid);

            Order order = orderService.getOrderById(oid);

            if(order!=null )
            {

                boolean hasrefund = refundService.checkOrderRefundExist(order.getId(), Refund.REFUND_TYPE_FREE_ORDER);

                if(hasrefund)
                {
                    logger.warn("refund   free order  but find refund exists with orderid = " + orderid );
                    return;
                }


                FreeOrder freeOrder = new FreeOrder();
                freeOrder.setGoodsid(order.getOrderGoods().getGoodId());
                freeOrder.setGoodsname(order.getOrderGoods().getGoodtitle());
                freeOrder.setGoodspic(order.getOrderGoods().getGoodposter());
                freeOrder.setMoney(order.getOrdermoney() - order.getPreshippfee());
                freeOrder.setUserid(order.getBuyerid());
                freeOrder.setOrderid(order.getId().intValue());
                freeOrder.setNum(order.getOrderGoods().getBuysum());

                Customer customer = customerService.GetCoverById(order.getBuyerid());

                if(customer!=null)
                {
                    freeOrder.setNikename(customer.getNickname());
                    freeOrder.setHeadpic(customer.getHeadpic());
                }

                freeOrder.setTime(new Date());

                freeOrderRepository.save(freeOrder);

                Refund refund = new Refund();
                refund.setMoney(order.getOrdermoney() - order.getPreshippfee());
                refund.setOrderid(order.getId());

                refund.setReason(Refund.REFUND_REASONS[Refund.REFUND_TYPE_FREE_ORDER]);
                refund.setType(Refund.REFUND_TYPE_FREE_ORDER);
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

                WxPayUtils.RefundResult refundResult = WxPayUtils.refund(order,refund);

                if(refundResult.getResult() == WxPayUtils.ORDER_RESULT_OK)
                {
                    refund.setStatus(Refund.REFUND_STATUS_REFUND_OK);
                    refund.setRefundid(refundResult.getRefundid());
                    refund.setRefundtime(new Date());
                    refundService.createOrUpdate(refund);

                    order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_FREE_ORDER,Refund.REFUND_STATUS_REFUND_OK));


                }
                else
                {

                    refund.setStatus(Refund.REFUND_STATUS_REFUND_FAIL);
                    refund.setFailstr(refundResult.getErrdes());
                    //refund.setReason(refundResult.getErrdes());
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


                }

                order.setIsfree(1);

                order.setRefundFlag(Refund.GetNewRefundFlag(order.getRefundFlag(),Refund.REFUND_TYPE_FREE_ORDER,Refund.REFUND_STATUS_REFUND_FAIL));

                orderService.UpdateOrder(order);

            }
            else
            {
                logger.warn(" refund   free order  but order not exists with id = " + orderid  );
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
