package com.ziyoushenghuo.rabbitmq.configuration;

import com.ziyoushenghuo.rabbitmq.MQConstant;
import com.ziyoushenghuo.rabbitmq.QueueConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


import java.util.HashMap;
import java.util.Map;


/* 配置发货后延迟消息
  @author 王江波
  @version V1.0
*/
@Configuration
public class AfterDeliveryConfiguration extends QueueConfiguration{

    //信道配置

    @Bean
    public Queue deadLetterAfterDeliveryQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.DEFAULT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstant.ORDER_CHECK_AFTERDELIVERY_NAME);
        Queue queue = new Queue(MQConstant.DEAD_LETTER_QUEUE_AFTERDELIVERY_NAME,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Binding  deadLetterAfterDeliveryBinding() {
        return BindingBuilder.bind(deadLetterAfterDeliveryQueue()).to(defaultExchange()).with(MQConstant.DEAD_LETTER_QUEUE_AFTERDELIVERY_NAME);
    }



    @Bean
    public Queue routeAfterDeliveryQueue() {
        Queue queue = new Queue(MQConstant.ORDER_CHECK_AFTERDELIVERY_NAME,true,false,false);
        return queue;
    }

    @Bean
    public Binding  AfterDeliveryQueueBinding() {
        return BindingBuilder.bind(routeAfterDeliveryQueue()).to(defaultExchange()).with(MQConstant.ORDER_CHECK_AFTERDELIVERY_NAME);
    }

}
