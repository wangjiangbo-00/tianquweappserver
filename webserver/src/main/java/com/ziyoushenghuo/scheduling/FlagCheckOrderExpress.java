package com.ziyoushenghuo.scheduling;



import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.OrderExpress;
import com.ziyoushenghuo.express.KdMessage;
import com.ziyoushenghuo.express.KdniaoSubscribeAPI;
import com.ziyoushenghuo.rabbitmq.MQConstant;

import com.ziyoushenghuo.rabbitmq.delay.DelayAfterDeliverySender;
import com.ziyoushenghuo.rabbitmq.delay.DelayUnGroupSender;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.service.CustomerService;
import com.ziyoushenghuo.service.OrderExpressService;
import com.ziyoushenghuo.service.OrderService;
import com.ziyoushenghuo.web.controller.WxCustomerController;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FlagCheckOrderExpress {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DelayAfterDeliverySender delayAfterDeliverySender;

    @Autowired
    private OrderExpressService orderExpressService;


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    WeixinTemplate weixinTemplate;

    @Autowired
    CustomerService customerService;

    private static Logger logger = LoggerFactory.getLogger(FlagCheckOrderExpress.class);

    @Scheduled(initialDelay = SchedulingConstant.COMMON_DELAY,fixedRate = SchedulingConstant.FLAG_CHECK_ORDER_EXPRESS)
    public void task()
    {
        try {
            List<String> orderstrs= redisTemplate.opsForList().range(RedisKeyConstant.FLAG_ORDER_EXPRESS,0,-1);
            redisTemplate.delete(RedisKeyConstant.FLAG_ORDER_EXPRESS);

            if(!orderstrs.isEmpty())
            {
                for(String orderstr :orderstrs)
                {
                    int orderid = Integer.valueOf(orderstr);

                    Order order = orderService.getOrderById(orderid);

                    if(order!=null)
                    {
                        if(order.getOrderStatus() == Order.ORDER_STATUS_TOSHIP)
                        {

                            String queuemsg = String .valueOf(order.getId()) ;

                            delayAfterDeliverySender.send(queuemsg);

                            order.setOrderStatus(Order.ORDER_STATUS_TORECEIVE);

                            orderService.UpdateOrder(order);

                            OrderExpress orderExpress = orderExpressService.getByOrderId(orderid);

                            if(orderExpress!=null)
                            {
                                Customer customer = null;
                                if(order.getOrdertype() == Order.ORDER_TYPE_NORMAL)
                                {
                                    customer = customerService.GetOpenIdById(order.getBuyerid());
                                }
                                else
                                {
                                    customer = customerService.GetOpenIdById(order.getGiverid());
                                }

                                if(customer!=null)
                                {
                                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                                    weixinTemplate.sendTemplateOrderDelery(access_token,order,customer,orderExpress,true);
                                }
                            }
                        }
                    }

                }
            }
        }catch (Exception e)
        {
            logger.warn(e.getMessage());
        }









    }

}
